package br.com.angelorobson.whatsapplinkgenerator.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import br.com.angelorobson.whatsapplinkgenerator.R
import com.vansuita.materialabout.builder.AboutBuilder
import kotlinx.android.synthetic.main.fragment_about.*


class AboutFragment : Fragment(R.layout.fragment_about) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAboutScreen()
    }

    private fun setUpAboutScreen() {
        val frameLayout = about
        frameLayout.refreshDrawableState()

        val builder = AboutBuilder.with(requireContext())
            .setAppIcon(R.mipmap.ic_launcher)
            .setAppName(R.string.app_name)
            .setPhoto(R.drawable.angelo)
            .setCover(R.mipmap.profile_cover)
            .setLinksAnimated(true)
            .setDividerDashGap(13)
            .setName("Ângelo Robson")
            .setSubTitle(getString(R.string.profession))
            .setLinksColumnsCount(4)
            .setBrief(getString(R.string.brief))
            .addGitHubLink("angelorobsonmelo")
            .addLinkedInLink("ângelo-melo-8b4a47148/")
            .addEmailLink("angelorobsonmelo@gmail.com")
            .addWhatsappLink("Ângelo Robson", "+5582991228122")
            .setVersionNameAsAppSubTitle()
            .addShareAction(R.string.app_name)
            .setActionsColumnsCount(2)
            .setWrapScrollView(true)
            .setShowAsCard(true)

        val view = builder.build()

        frameLayout.addView(view)

        val item = builder.lastLinkId

        view.findItem(item).setOnClickListener {
            val url =
                "https://api.whatsapp.com/send?phone=5582991228122&text="
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

}
