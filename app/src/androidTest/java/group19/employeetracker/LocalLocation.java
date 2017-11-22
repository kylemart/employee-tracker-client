package group19.employeetracker;

import android.support.test.rule.ActivityTestRule;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class LocalLocation {
    @Rule
    public ActivityTestRule<MapsActivity> mActivityRule = new ActivityTestRule<>(MapsActivity.class);

    @Test
    public void location() throws Exception {
        assertNotEquals(mActivityRule.getActivity().getUserCoords(), new LatLng(0,0));
    }
}