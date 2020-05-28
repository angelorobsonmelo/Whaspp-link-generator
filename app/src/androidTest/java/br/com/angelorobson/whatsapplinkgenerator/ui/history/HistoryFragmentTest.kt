package br.com.angelorobson.whatsapplinkgenerator.ui.history

import android.content.res.Resources
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import br.com.angelorobson.whatsapplinkgenerator.AndroidTestApplication
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.di.TestComponent
import br.com.angelorobson.whatsapplinkgenerator.model.builders.HistoryEntityBuild
import br.com.angelorobson.whatsapplinkgenerator.ui.component
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.ActivityService
import br.com.angelorobson.whatsapplinkgenerator.utils.TestIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class HistoryFragmentTest {

    var idlingResource: TestIdlingResource? = null
    private var resources: Resources? = null
    private var scenario: FragmentScenario<HistoryFragment>? = null

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
                listOf(
                    HistoryEntityBuild.Builder()
                        .oneHistory()
                        .createdAt("2020-05-27T11:24:43.644")
                        .build(),
                    HistoryEntityBuild.Builder()
                        .oneHistory()
                        .createdAt("2020-05-28T10:14:43.644")
                        .id(2)
                        .build()
                )
            )
        }
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource!!.countingIdlingResource)
    }

    @Test
    fun initialViews() {
        onView(withId(R.id.recyclerView))
            .check(matches(isDisplayed()))

    }
}