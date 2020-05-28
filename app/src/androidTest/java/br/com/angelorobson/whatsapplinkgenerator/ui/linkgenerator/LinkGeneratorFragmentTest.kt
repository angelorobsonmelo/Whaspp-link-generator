package br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator

import android.content.res.Resources
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.di.TestComponent
import br.com.angelorobson.whatsapplinkgenerator.ui.component
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.ActivityService
import br.com.angelorobson.whatsapplinkgenerator.utils.FileUtils
import br.com.angelorobson.whatsapplinkgenerator.utils.TestIdlingResource
import br.com.angelorobson.whatsapplinkgenerator.utils.isToast
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test


class LinkGeneratorFragmentTest {


    private val mockWebServer = MockWebServer()
    var idlingResource: TestIdlingResource? = null
    private var resources: Resources? = null
    private var scenario: FragmentScenario<LinkGeneratorFragment>? = null

    @Before
    fun setUp() {
        val mockResponse = MockResponse()
            .setBody(FileUtils.getJson("json/countries/countries.json"))
        mockResponse.setResponseCode(200)
        mockWebServer.enqueue(mockResponse)
        mockWebServer.start(8500)

        scenario = launchFragmentInContainer<LinkGeneratorFragment>(
            themeResId = R.style.Theme_MaterialComponents_Light_NoActionBar
        )

        scenario?.onFragment { fragment ->
            resources = fragment.resources
            idlingResource =
                ((fragment.activity!!.component as TestComponent).idlingResource() as TestIdlingResource)
            val activityService =
                ((fragment.activity!!.component as TestComponent).activityService() as ActivityService)
            activityService.onCreate(fragment.activity!!)

            IdlingRegistry.getInstance().register(idlingResource!!.countingIdlingResource)
            idlingResource!!.increment()
        }
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource!!.countingIdlingResource)
        mockWebServer.close()
    }

    @Test
    fun initialViews() {
        onView(withId(R.id.spinnerCountryCode)).check(matches(isDisplayed()))
        onView(withId(R.id.tvCountryCode)).check(matches(withText(R.string.country_code)))

        onView(withId(R.id.etRegionCode)).check(matches(isDisplayed()))
        onView(withId(R.id.etRegionCode)).check(matches(not(isEnabled())))

        onView(withId(R.id.tvPhoneNumber)).check(matches(withText(R.string.phone_number)))
        onView(withId(R.id.tvPhoneNumber)).check(matches(isDisplayed()))

        onView(withId(R.id.etPhoneNumber)).check(matches(isDisplayed()))
        onView(withId(R.id.etPhoneNumber)).check(matches(withHint(R.string.cell_phone)))
        onView(withId(R.id.etPhoneNumber)).check(matches(withInputType(android.text.InputType.TYPE_CLASS_PHONE)))

        onView(withId(R.id.etTextMessage)).check(matches(isDisplayed()))
        onView(withId(R.id.etTextMessage)).check(matches(withHint(R.string.message)))

        onView(withId(R.id.btnSendMessage)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSendMessage)).check(matches(isEnabled()))
        onView(withId(R.id.btnSendMessage)).check(matches(withText(R.string.send)))

        onView(withId(R.id.btnCopyLink)).check(matches(isDisplayed()))
        onView(withId(R.id.btnCopyLink)).check(matches(isEnabled()))
        onView(withId(R.id.btnCopyLink)).check(matches(withText(R.string.copy_link)))
    }

    @Test
    fun clickSendButton_withFormInvalid_showCanBeEmptyMessage() {
        onView(withId(R.id.btnSendMessage)).perform(click())
        onView(withId(R.id.etPhoneNumber)).check(matches(hasErrorText(resources?.getString(R.string.error_message_empty_validation))))
    }

    @Test
    fun clickCopyLinkButton_withFormInvalid_showCanBeEmptyMessage() {
        onView(withId(R.id.btnCopyLink)).perform(click())
        onView(withId(R.id.etPhoneNumber)).check(matches(hasErrorText(resources?.getString(R.string.error_message_empty_validation))))
    }

    @Test
    fun clickCopyLinkButton_withFormValid_showCopiedMessage() {
        onView(withId(R.id.etPhoneNumber)).perform(typeText("8299155554"))
        onView(withId(R.id.btnCopyLink)).perform(click())

        onView(withText(R.string.copied)).inRoot(isToast()).check(matches(isDisplayed()));
    }

 /*   @Test
    fun clickSendButton_withFormValid_openWhatsAppIfInstalled() {
        TestUtils.waitEspresso(2000)

        onView(withId(R.id.etPhoneNumber)).perform(typeText("8299155554"))
        onView(withId(R.id.btnSendMessage)).perform(click())
//        intended(toPackage("com.whatsapp"))
    }*/

}