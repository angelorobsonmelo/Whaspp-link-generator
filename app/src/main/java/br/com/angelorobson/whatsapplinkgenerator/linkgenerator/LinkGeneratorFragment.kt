package br.com.angelorobson.whatsapplinkgenerator.linkgenerator

import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.getViewModel
import br.com.angelorobson.whatsapplinkgenerator.linkgenerator.widgets.CountryAdapter
import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.link_generator_fragment.*
import java.util.*


class LinkGeneratorFragment : Fragment(R.layout.link_generator_fragment) {


    lateinit var disposable: Disposable

    override fun onStart() {
        super.onStart()

        disposable = Observable.mergeArray(
            btnSendMessage.clicks().map {
                if (isFormValid()) {
                    ButtonSendClicked
                } else {
                    FormInvalid
                }
            },
            btnShareLink.clicks().map {
                ButtonSendClicked
            }
        ).compose(getViewModel(LinkGeneratorViewModel::class).init(Initial))
            .subscribe { model ->
                if (model.linkGeneratorResult is LinkGeneratorResult.CountriesLoaded) {
                    handleSpinner(model.linkGeneratorResult.countries)
                }
            }
    }

    private fun isFormValid(): Boolean {
        var valid = true
        if (etRegionCode.text?.isEmpty()!!) {
            etRegionCode.error = "Preencha esta campo"
            valid = false
        }

        if (etPhoneNumber.text?.isEmpty()!!) {
            etPhoneNumber.error = "Preencha esta campo"
            valid = false
        }

        if (etTextMessage.text?.isEmpty()!!) {
            etTextMessage.error = "Preencha esta campo"
            valid = false
        }

        return valid
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
                val areaCode = getString(R.string.area_code_formatted, country.areaCode)
                etRegionCode.setText(areaCode)
            }

        }


    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}