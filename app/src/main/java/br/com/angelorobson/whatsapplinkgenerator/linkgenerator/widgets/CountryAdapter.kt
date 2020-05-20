package br.com.angelorobson.whatsapplinkgenerator.linkgenerator.widgets

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class CountryAdapter(context: Context, countries: List<Country>) :
    ArrayAdapter<Country>(context, 0, countries) {

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

        val imageFlag = converterV?.findViewById<ImageView>(R.id.ivFlag)
        val textView = converterV?.findViewById<TextView>(R.id.tvCountryName)

        val country = getItem(position)

        val uri =
            Uri.parse(country?.flag)

        Glide.with(context)
            .load(uri)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .fitCenter()
            .into(imageFlag!!)


        textView?.text = country?.countryFullName

        return converterV!!
    }
}