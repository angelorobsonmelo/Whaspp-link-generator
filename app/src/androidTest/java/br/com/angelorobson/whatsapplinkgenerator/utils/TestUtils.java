package br.com.angelorobson.whatsapplinkgenerator.utils;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.util.Checks;

import com.google.android.material.textfield.TextInputLayout;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static java.lang.Thread.sleep;

public class TestUtils {


    public static void waitEspresso(long milles) {
        try {
            sleep(milles);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void checkIfIdIsDisplayed(int id){
        onView(withId(id)).check(matches(isDisplayed()));
    }

    public static void checkIfIdIsNotVisible(int id){
        onView(withId(id)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    public static void findAndClick(int id){
        onView(withId(id)).perform(click());
    }

    public static String getResourceString(int id) {
        Context targetContext = ApplicationProvider.getApplicationContext();
        return targetContext.getResources().getString(id);
    }

    public static String getResourceStringWithParameters(int id, int parameters) {
        Context targetContext = ApplicationProvider.getApplicationContext();
        return targetContext.getResources().getString(id, parameters);
    }

    public static Matcher<View> hasTextInputLayoutErrorText(final String expectedErrorText) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextInputLayout)) {
                    return false;
                }

                CharSequence error = ((TextInputLayout) view).getError();

                if (error == null) {
                    return false;
                }

                String hint = error.toString();

                return expectedErrorText.equals(hint);
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }

    public static Matcher<View> withHint(final String expectedHint) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof EditText)) {
                    return false;
                }

                String hint = ((EditText) view).getHint().toString();

                return expectedHint.equals(hint);
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }

    @NonNull
    public static Matcher<View> withError(final String expectedErrorText) {
        Checks.checkNotNull(expectedErrorText);
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public void describeTo(final Description description) {
                description.appendText("error text: ");
//                stringMatcher.describeTo(description);
            }
            @Override
            public boolean matchesSafely(final TextView textView) {
                return expectedErrorText.equals(textView.getError().toString());
            }
        };
    }


}


