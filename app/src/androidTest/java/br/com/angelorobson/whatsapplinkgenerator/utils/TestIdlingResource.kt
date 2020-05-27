package br.com.angelorobson.whatsapplinkgenerator.utils

import androidx.test.espresso.idling.CountingIdlingResource
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.IdlingResource


class TestIdlingResource : IdlingResource {

    var countingIdlingResource = CountingIdlingResource("IdlingResource")
    override fun increment() {
        countingIdlingResource.increment()
    }

    override fun decrement() {
        countingIdlingResource.decrement()
    }
}
