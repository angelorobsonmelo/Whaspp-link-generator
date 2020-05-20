package br.com.angelorobson.whatsapplinkgenerator.linkgenerator

import androidx.fragment.app.Fragment
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.getViewModel
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.link_generator_fragment.*

class LinkGeneratorFragment : Fragment(R.layout.link_generator_fragment) {

    lateinit var disposable: Disposable

    override fun onStart() {
        super.onStart()

        disposable = Observable.mergeArray(
            btnSendMessage.clicks().map { ButtonSendClicked }
        ).compose(getViewModel(LinkGeneratorViewModel::class).init(Initial))
            .subscribe {
                print(it)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}