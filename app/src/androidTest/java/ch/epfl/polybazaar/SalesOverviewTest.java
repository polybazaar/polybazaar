package ch.epfl.polybazaar;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;

import ch.epfl.polybazaar.UI.SalesOverview;


public class SalesOverviewTest {

    @Rule
    public final ActivityTestRule<SalesOverview> activityRule =
            new ActivityTestRule<>(
                    SalesOverview.class,
                    true,
                    false);



}
