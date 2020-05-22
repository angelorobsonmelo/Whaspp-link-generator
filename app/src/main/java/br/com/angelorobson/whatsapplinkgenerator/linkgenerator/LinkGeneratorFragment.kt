package br.com.angelorobson.whatsapplinkgenerator.linkgenerator

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.getViewModel
import br.com.angelorobson.whatsapplinkgenerator.linkgenerator.widgets.CountryAdapter
import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.link_generator_fragment.*
import java.net.URLEncoder
import java.util.*


class LinkGeneratorFragment : Fragment(R.layout.link_generator_fragment) {


    lateinit var disposable: Disposable

    override fun onStart() {
        super.onStart()

        disposable = Observable.mergeArray(
            btnSendMessage.clicks().map {
                if (isFormValid()) {
                    buttonSendClicked()
                } else {
                    FormInvalid
                }
            },
            btnShareLink.clicks().map {
                if (isFormValid()) {
                    buttonShareViaClicked()
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
                if (model.linkGeneratorResult is LinkGeneratorResult.CountriesLoaded) {
                    handleSpinner(model.linkGeneratorResult.countries)
                }
                if (model.linkGeneratorResult is LinkGeneratorResult.ContactInformationToSend) {
                    val contactInformation = model.linkGeneratorResult
                    sendMessageToWhatsApp(contactInformation)
                }
                if (model.linkGeneratorResult is LinkGeneratorResult.ContactInformationToShare) {
                    val contactInformation = model.linkGeneratorResult
                    shareLink(contactInformation)
                }
            }
    }

    private fun buttonSendClicked(): ButtonSendClicked {
        return ButtonSendClicked(
            etRegionCode.text.toString(),
            etPhoneNumber.text.toString(),
            etTextMessage.text.toString()
        )
    }

    private fun buttonShareViaClicked(): ButtonShareClicked {
        return ButtonShareClicked(
            etRegionCode.text.toString(),
            etPhoneNumber.text.toString(),
            etTextMessage.text.toString()
        )
    }

    private fun buttonCopyClicked(): ButtonCopyClicked {
        return ButtonCopyClicked(
            etRegionCode.text.toString(),
            etPhoneNumber.text.toString(),
            etTextMessage.text.toString()
        )
    }

    private fun sendMessageToWhatsApp(contactInformationToSend: LinkGeneratorResult.ContactInformationToSend) {
        try {
            val packageManager = requireActivity().packageManager
            val i = Intent(Intent.ACTION_VIEW)
            val url =
                "https://api.whatsapp.com/send?phone=${contactInformationToSend.countryCode}" +
                        "${contactInformationToSend.phoneNumber}&text=" + URLEncoder.encode(
                    contactInformationToSend.message,
                    "UTF-8"
                )
            i.setPackage("com.whatsapp")
            i.data = Uri.parse(url)
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i)
            } else {
                showToast()
            }
        } catch (e: Exception) {
            showToast()
        }
    }

    private fun showToast() {
        Toast.makeText(
            requireContext(),
            getString(R.string.whatApp_not_installed),
            Toast.LENGTH_SHORT
        ).show()
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

        if (etTextMessage.text?.isEmpty()!!) {
            etTextMessage.error = getString(R.string.empty_field)
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
                val countryCode = getString(R.string.area_code_formatted, country.areaCode)
                etRegionCode.setText(countryCode)
            }

        }


    }

    private fun shareLink(contactInformationToShare: LinkGeneratorResult.ContactInformationToShare) {
        val url =
            "https://api.whatsapp.com/send?phone=${contactInformationToShare.countryCode}" +
                    "${contactInformationToShare.phoneNumber}&text=" + URLEncoder.encode(
                contactInformationToShare.message,
                "UTF-8"
            )

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.whatApp_not_installed))
        intent.putExtra(Intent.EXTRA_TEXT, url)
        startActivity(Intent.createChooser(intent, getString(R.string.share_via)))
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}