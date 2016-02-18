package bitwize.nullawesome;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class bitwize.nullawesome.NullAwesomeTest \
 * bitwize.nullawesome.tests/android.test.InstrumentationTestRunner
 */
public class NullAwesomeTest extends ActivityInstrumentationTestCase2<NullAwesome> {

    public NullAwesomeTest() {
        super("bitwize.nullawesome", NullAwesome.class);
    }

}
