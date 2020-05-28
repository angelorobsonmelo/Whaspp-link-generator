package br.com.angelorobson.whatsapplinkgenerator.ui.history

import android.content.res.Resources
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import br.com.angelorobson.whatsapplinkgenerator.AndroidTestApplication
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.di.TestComponent
import br.com.angelorobson.whatsapplinkgenerator.model.builders.CountryEntityBuild
import br.com.angelorobson.whatsapplinkgenerator.model.builders.HistoryEntityBuild
import br.com.angelorobson.whatsapplinkgenerator.model.entities.HistoryEntity
import br.com.angelorobson.whatsapplinkgenerator.ui.component
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.ActivityService
import br.com.angelorobson.whatsapplinkgenerator.utils.TestIdlingResource
import br.com.angelorobson.whatsapplinkgenerator.utils.isToast
import br.com.angelorobson.whatsapplinkgenerator.utils.withRecyclerView
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class HistoryFragmentTest {

    var idlingResource: TestIdlingResource? = null
    private var resources: Resources? = null
    private var scenario: FragmentScenario<HistoryFragment>? = null
    private val dateTime = "2020-05-27T11:24:43.644"

    private val dateFormattedExpected = "2020-05-27 11:24"
    private val countryShortName = "BR"
    private val areaCode = "+55"
    private val phoneNumber = "82994441587"
    private val message = "Message sent"

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer<HistoryFragment>(
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

            (fragment.activity!!.applicationContext as AndroidTestApplication).historyEntitySubject.onNext(
                getHistoriesEntities()
            )
        }
    }

    private fun getHistoriesEntities(): List<HistoryEntity> {
        return listOf(
            HistoryEntityBuild.Builder()
                .oneHistoryEntity()
                .createdAt(dateTime)
                .phoneNumber(phoneNumber)
                .countryEntity(
                    CountryEntityBuild.Builder()
                        .countryShortName(countryShortName)
                        .areaCode(areaCode)
                        .oneCountryEntity()
                        .build()
                )
                .build(),
            HistoryEntityBuild.Builder()
                .oneHistoryEntity()
                .id(2)
                .message("Hi, how are you?")
                .build()
        )
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource!!.countingIdlingResource)
    }

    @Test
    fun checkItemView() {
        onView(withId(R.id.recyclerView))
            .check(matches(isDisplayed()))

        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(0, R.id.tvDate))
            .check(matches(withText(dateFormattedExpected)))

        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(0, R.id.ivFlag))
            .check(matches(isDisplayed()))

        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(0, R.id.tvCountryName))
            .check(matches(withText(countryShortName)))

        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(0, R.id.tvCountryCode))
            .check(
                matches(
                    withText(
                        resources?.getString(
                            R.string.area_code_number,
                            areaCode,
                            phoneNumber
                        )
                    )
                )
            )

        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(0, R.id.tvMessage))
            .check(matches(withText(message)))
    }

    // If you are running tests on an emulator, comment on this test block and use the test block below.
    /* @Test
     fun clickIconSend_openWhatsAppIfInstalled() {
         Intents.init()

        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(0, R.id.ivSend))
            .perform(click())

         intended(hasAction("android.intent.action.VIEW"))
         intended(hasPackage("com.whatsapp"))

         Intents.release()
     }*/


    // If you are running the tests on a real device with whatsapp installed use this, If not, comment this line and use the test above
    @Test
    fun clickIconSend_showWhatsAppNotInstalledToast() {
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(0, R.id.ivSend))
            .perform(click())

        onView(withText(R.string.whatApp_not_installed)).inRoot(isToast())
            .check(matches(isDisplayed()));
    }
}