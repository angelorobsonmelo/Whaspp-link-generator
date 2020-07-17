package br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator

import android.content.Context
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.work.*
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.databinding.LinkGeneratorFragmentBinding
import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country
import br.com.angelorobson.whatsapplinkgenerator.ui.getViewModel
import br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator.widgets.CountryAdapter
import br.com.angelorobson.whatsapplinkgenerator.ui.share.showToast
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.BindingFragment
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.extensions.addHours
import br.com.angelorobson.whatsapplinkgenerator.ui.worker.ScheduleMessageWorker
import br.com.ilhasoft.support.validation.Validator
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.jakewharton.rxbinding3.view.clicks
import com.paulinasadowska.rxworkmanagerobservers.extensions.getWorkInfoByIdObservable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.link_generator_fragment.*
import java.lang.System.currentTimeMillis
import java.util.*
import java.util.concurrent.TimeUnit


class LinkGeneratorFragment : BindingFragment<LinkGeneratorFragmentBinding>() {

    override fun getLayoutResId(): Int = R.layout.link_generator_fragment

    private val compositeDisposable = CompositeDisposable()
    private var countrySelected = Country()
    private val schedulerPublisherSubject = PublishSubject.create<Long>()
    private var countries: ArrayList<Country> = arrayListOf()
    private lateinit var adapter: CountryAdapter
    private lateinit var mValidator: Validator

    override fun onStart() {
        super.onStart()
        etPhoneNumber.requestFocus()
        setupValidator()

        val networkListenerObservable = ReactiveNetwork
            .observeNetworkConnectivity(requireContext())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        setHasOptionsMenu(true)
        adapter = CountryAdapter(requireActivity(), countries)

        val disposable = Observable.mergeArray(
            btnSendMessage.clicks().map {
                ButtonSendClickedEvent(
                    countryCode = etRegionCode.text.toString(),
                    phoneNumber = etPhoneNumber.text.toString(),
                    message = etTextMessage.text.toString(),
                    country = countrySelected,
                    isFormValid = mValidator.validate()
                )
            },
            btnCopyLink.clicks().map {
                ButtonCopyClickedEvent(
                    countryCode = etRegionCode.text.toString(),
                    phoneNumber = etPhoneNumber.text.toString(),
                    message = etTextMessage.text.toString(),
                    isFormValid = mValidator.validate()
                )
            },
            schedulerPublisherSubject.map { delay ->
                ScheduleMessageEvent(
                    countryCode = etRegionCode.text.toString(),
                    phoneNumber = etPhoneNumber.text.toString(),
                    message = etTextMessage.text.toString(),
                    country = countrySelected,
                    isFormValid = mValidator.validate(),
                    delay = delay
                )
            },
            networkListenerObservable.map {
                progress_horizontal.isVisible = true
                InitialEvent
            }
        ).compose(getViewModel(LinkGeneratorViewModel::class))
            .subscribe(
                { model ->
                    if (model.linkGeneratorResult is LinkGeneratorResult.Loading) {
                        binding.progressHorizontal.isVisible = model.linkGeneratorResult.isLoading
                    }

                    if (model.linkGeneratorResult is LinkGeneratorResult.CountriesLoaded) {
                        binding.progressHorizontal.isVisible = model.linkGeneratorResult.isLoading
                        countries.addAll(model.linkGeneratorResult.countries)
                        handleSpinner()
                    }
                    if (model.linkGeneratorResult is LinkGeneratorResult.Error) {
                        binding.progressHorizontal.isVisible = model.linkGeneratorResult.isLoading
                    }
                },
                {
                    showToast(
                        it.localizedMessage ?: getString(R.string.unknown_error),
                        requireContext()
                    )
                }
            )

        compositeDisposable.add(disposable)
        btnScheduleMessage.setOnClickListener {
            scheduleMessage()
        }
    }

    private fun scheduleMessage() {
        SingleDateAndTimePickerDialog.Builder(context) //.bottomSheet()
            .curved()
            .bottomSheet()
            .displayAmPm(false)
            .minDateRange(Date())
            .minutesStep(1)
            .defaultDate(Date())
            .displayListener {
                hideKeyBoard()
            }
            .title(getString(R.string.schedule_message_title))
            .listener { selectedDate ->
                val currentTime = Date().time
                val customTime = selectedDate.time
                val delay = customTime - currentTime
                schedulerPublisherSubject.onNext(delay)
            }
            .display()
    }

    private fun setupValidator() {
        mValidator = Validator(binding)
        mValidator.enableFormValidationMode()
    }

    private fun handleSpinner() {
        spinnerCountryCode.adapter = adapter
        adapter.notifyDataSetChanged()

        val positionToBeSelected: Int = try {
            getItemPositionItemToBeSelected()
        } catch (e: IndexOutOfBoundsException) {
            0
        }
        spinnerCountryCode.setSelection(positionToBeSelected)

        val areaCode =
            getString(R.string.area_code_formatted, countries[positionToBeSelected].areaCode)
        etRegionCode.setText(areaCode)

        handleItemSelected()
    }

    @Throws(IndexOutOfBoundsException::class)
    private fun getItemPositionItemToBeSelected(): Int {
        val countryShortName = Locale.getDefault().country

        return if (countrySelected.countryShortName.isNotEmpty()) {
            adapter.getPosition(
                countries.filter {
                    it.countryShortName == countrySelected.countryShortName
                }[0]
            )
        } else {
            adapter.getPosition(
                countries.filter {
                    it.countryShortName == countryShortName
                }[0]
            )
        }
    }

    private fun handleItemSelected() {
        spinnerCountryCode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val country = parent?.getItemAtPosition(position) as Country
                val countryCode = getString(R.string.area_code_formatted, country.areaCode)
                etRegionCode.setText(countryCode)
                countrySelected = country
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        hideKeyBoard()
        return NavigationUI.onNavDestinationSelected(
            item,
            findNavController()
        ) || super.onOptionsItemSelected(item)
    }

    private fun hideKeyBoard() {
        val im = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(etPhoneNumber.windowToken, 0)
        im.hideSoftInputFromWindow(etTextMessage.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}