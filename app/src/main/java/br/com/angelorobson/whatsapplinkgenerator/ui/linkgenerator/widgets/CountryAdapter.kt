package br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator.widgets

import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.*
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.databinding.CountrySpinnerRowBinding
import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country
import br.com.angelorobson.whatsapplinkgenerator.model.domains.view.CountryItem
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou

class CountryAdapter(private val activity: Activity, countries: List<Country>) :
    ArrayAdapter<Country>(activity, 0, countries) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        var converterV = convertView
        if (converterV == null) {
            converterV = LayoutInflater.from(context).inflate(
                R.layout.country_spinner_row, parent, false
            )
        }
        val country = getItem(position)
        val binding = bind<CountrySpinnerRowBinding>(converterV!!)

        binding?.country = CountryItem(
            areaCode = country?.areaCode ?: "",
            countryShortName = country?.countryShortName ?: ""
        )

        val imageFlag = binding?.ivFlag

        val uri =
            Uri.parse(country?.flag)

        GlideToVectorYou.justLoadImage(activity, uri, imageFlag!!)

        return binding.root
    }
}