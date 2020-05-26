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
import br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator.widgets.CountryAdapter
import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.link_generator_fragment.*
import java.util.*
import kotlin.collections.ArrayList


class LinkGeneratorFragment : Fragment(R.layout.link_generator_fragment) {


    private lateinit var disposable: Disposable
    private var countrySelected = Country()
    private var countries: ArrayList<Country> = arrayListOf()
    private lateinit var adapter: CountryAdapter

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        adapter = CountryAdapter(requireActivity(), countries)

        disposable = Observable.mergeArray(
            btnSendMessage.clicks().map {
                buttonSendClicked()
            },
            btnCopyLink.clicks().map {
                buttonCopyClicked()
            }
        ).compose(getViewModel(LinkGeneratorViewModel::class).init(Initial))
            .subscribe { model ->
                if (model.linkGeneratorResult is LinkGeneratorResult.Loading) {
                    progress_horizontal.isVisible = model.linkGeneratorResult.isLoading
                }

                if (model.linkGeneratorResult is LinkGeneratorResult.CountriesLoaded) {
                    progress_horizontal.isVisible = model.linkGeneratorResult.isLoading
                    countries.addAll(model.linkGeneratorResult.countries)
                    handleSpinner()
                }
                if (model.linkGeneratorResult is LinkGeneratorResult.Error) {
                    progress_horizontal.isVisible = model.linkGeneratorResult.isLoading
                }
            }
    }

    private fun buttonSendClicked(): ButtonSendClickedEvent {
        return ButtonSendClickedEvent(
            countryCode = etRegionCode.text.toString(),
            phoneNumber = etPhoneNumber.text.toString(),
            message = etTextMessage.text.toString(),
            country = countrySelected
        )
    }

    private fun buttonCopyClicked(): ButtonCopyClickedEvent {
        return ButtonCopyClickedEvent(
            countryCode = etRegionCode.text.toString(),
            phoneNumber = etPhoneNumber.text.toString(),
            message = etTextMessage.text.toString()
        )
    }

    private fun handleSpinner() {
        spinnerCountryCode.adapter = adapter
        adapter.notifyDataSetChanged()

        val countryShortName = Locale.getDefault().country
        val positionToBeSelected: Int

        positionToBeSelected = getItemPositionItemToBeSelected(countryShortName)

        spinnerCountryCode.setSelection(positionToBeSelected)

        val areaCode =
            getString(R.string.area_code_formatted, countries[positionToBeSelected].areaCode)
        etRegionCode.setText(areaCode)

        handleItemSelected()
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

    private fun getItemPositionItemToBeSelected(countryShortName: String): Int {
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