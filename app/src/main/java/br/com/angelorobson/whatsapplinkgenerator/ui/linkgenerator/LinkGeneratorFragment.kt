package br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.ui.getViewModel
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.copyToClipBoard
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.sendMessageToWhatsApp
import br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator.widgets.CountryAdapter
import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.showToast
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.link_generator_fragment.*
import java.util.*


class LinkGeneratorFragment : Fragment(R.layout.link_generator_fragment) {


    private lateinit var disposable: Disposable
    private var countrySelected = Country()

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)

        disposable = Observable.mergeArray(
            btnSendMessage.clicks().map {
                if (isFormValid()) {
                    buttonSendClicked()
                } else {
                    FormInvalid
                }
            },
            btnCopyLink.clicks().map {
                if (isFormValid()) {
                    buttonCopyClicked()
                } else {
                    FormInvalid
                }
            }
        ).compose(getViewModel(LinkGeneratorViewModel::class).init(Initial))
            .subscribe { model ->
                if (model.linkGeneratorResult is LinkGeneratorResult.Loading) {
                    progress_horizontal.isVisible = model.linkGeneratorResult.isLoading
                }

                if (model.linkGeneratorResult is LinkGeneratorResult.CountriesLoaded) {
                    progress_horizontal.isVisible = model.linkGeneratorResult.isLoading

                    handleSpinner(model.linkGeneratorResult.countries)
                }
                if (model.linkGeneratorResult is LinkGeneratorResult.ContactInformationToSend) {
                    val contactInformation = model.linkGeneratorResult
                    sendMessageToWhatsApp(
                        contactInformation,
                        requireActivity()
                    )
                }
                if (model.linkGeneratorResult is LinkGeneratorResult.ContactInformationToCopy) {
                    val contactInformation = model.linkGeneratorResult
                    copyToClipBoard(
                        contactInformation,
                        requireActivity()
                    )
                }
                if (model.linkGeneratorResult is LinkGeneratorResult.Error) {
                    progress_horizontal.isVisible = model.linkGeneratorResult.isLoading

                    showToast(
                        model.linkGeneratorResult.errorMessage,
                        requireActivity(),
                        Toast.LENGTH_LONG
                    )
                }
            }
    }

    private fun isFormValid(): Boolean {
        var valid = true
        if (etRegionCode.text?.isEmpty()!!) {
            etPhoneNumber.error = getString(R.string.empty_field)
            valid = false
        }

        if (etPhoneNumber.text?.isEmpty()!!) {
            etPhoneNumber.error = getString(R.string.empty_field)
            valid = false
        }

        if (etTextMessage.text?.isEmpty()!! || etTextMessage.text?.isBlank()!!) {
            etTextMessage.error = getString(R.string.empty_field)
            valid = false
        }

        return valid
    }

    private fun buttonSendClicked(): ButtonSendClicked {
        return ButtonSendClicked(
            etRegionCode.text.toString(),
            etPhoneNumber.text.toString(),
            etTextMessage.text.toString(),
            countrySelected
        )
    }

    private fun buttonCopyClicked(): ButtonCopyClicked {
        return ButtonCopyClicked(
            etRegionCode.text.toString(),
            etPhoneNumber.text.toString(),
            etTextMessage.text.toString()
        )
    }

    private fun handleSpinner(countries: List<Country>) {
        val adapter =
            CountryAdapter(requireActivity(), countries)
        spinnerCountryCode.adapter = adapter

        val countryShortName = Locale.getDefault().country

        val position = adapter.getPosition(
            countries.filter {
                it.countryShortName == countryShortName
            }[0]
        )

        spinnerCountryCode.setSelection(position)

        val areaCode = getString(R.string.area_code_formatted, countries[position].areaCode)
        etRegionCode.setText(areaCode)

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
        return NavigationUI.onNavDestinationSelected(
            item,
            findNavController()
        ) || super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}