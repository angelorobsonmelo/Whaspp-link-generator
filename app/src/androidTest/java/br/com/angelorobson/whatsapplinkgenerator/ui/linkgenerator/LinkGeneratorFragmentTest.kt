package br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator

import android.content.res.Resources
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import br.com.angelorobson.whatsapplinkgenerator.AndroidTestApplication
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.di.TestComponent
import br.com.angelorobson.whatsapplinkgenerator.model.entities.CountryEntity
import br.com.angelorobson.whatsapplinkgenerator.model.entities.HistoryEntity
import br.com.angelorobson.whatsapplinkgenerator.ui.component
import br.com.angelorobson.whatsapplinkgenerator.utils.FileUtils
import br.com.angelorobson.whatsapplinkgenerator.utils.TestIdlingResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test

class LinkGeneratorFragmentTest {

    private val historyEntity = HistoryEntity(
        createdAt = "14/07/2402",
        message = "message",
        phoneNumber = "phoneNumber",
        countryEntity = CountryEntity(
            countryShortName = "countryShortName",
            areaCode = "areaCode",
            flag = "flag",
            countryFullName = "countryFullName"
        )
    )

    private val mockWebServer = MockWebServer()

    @Test
    fun views() {
        val mockResponse = MockResponse()
            .setBody(FileUtils.getJson("json/countries/countries.json"))
        mockResponse.setResponseCode(200)
        mockWebServer.enqueue(mockResponse)
        mockWebServer.start(8500)

        val scenario = launchFragmentInContainer<LinkGeneratorFragment>(
            themeResId = R.style.Theme_MaterialComponents_Light_NoActionBar
        )

        var idlingResource: TestIdlingResource? = null
        var resources: Resources? = null
        /*scenario.onFragment { fragment ->
            resources = fragment.resources
            (fragment.activity!!.component as TestComponent).idlingResource() as TestIdlingResource
            IdlingRegistry.getInstance().register(idlingResource!!.countingIdlingResource)
        }*/

        onView(withId(R.id.etPhoneNumber)).check(matches(isDisplayed()))


//        IdlingRegistry.getInstance().unregister(idlingResource!!.countingIdlingResource)
        mockWebServer.close()
    }
}