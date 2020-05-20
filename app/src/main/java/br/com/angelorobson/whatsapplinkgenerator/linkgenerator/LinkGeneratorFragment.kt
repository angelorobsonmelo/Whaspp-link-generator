package br.com.angelorobson.whatsapplinkgenerator.linkgenerator

import androidx.fragment.app.Fragment
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.getViewModel
import br.com.angelorobson.whatsapplinkgenerator.linkgenerator.widgets.CountryAdapter
import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.link_generator_fragment.*

class LinkGeneratorFragment : Fragment(R.layout.link_generator_fragment) {

    lateinit var disposable: Disposable


    override fun onStart() {
        super.onStart()

        disposable = Observable.mergeArray(
            btnSendMessage.clicks().map {
                ButtonSendClicked
            }
        ).compose(getViewModel(LinkGeneratorViewModel::class).init(Initial))
            .subscribe { model ->
                if (model.linkGeneratorResult is LinkGeneratorResult.CountriesLoaded) {
                    handleSpinner(model.linkGeneratorResult.countries)
                }
            }
    }

    private fun handleSpinner(countries: List<Country>) {
        val adapter =
            CountryAdapter(requireContext(), countries)
        spinnerCountryCode.adapter = adapter


    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}