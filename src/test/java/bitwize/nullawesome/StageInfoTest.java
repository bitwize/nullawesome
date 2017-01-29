package bitwize.nullawesome;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.*;
import org.json.*;

@RunWith(MockitoJUnitRunner.class)
public class StageInfoTest {
    private static String testJson1 = "{ \"width\": 40, \"height\": 10, \"regions\": [{ \"left\": 2, \"top\": 8, \"right\": 40, \"bottom\": 10, \"type\": 1}, { \"left\": 12, \"top\": 6, \"right\": 14, \"bottom\": 8, \"type\": 1}], \"things\": [], \"tileImageName\": \"testTiles\"}";

    @Test
    public void regionsFromJSON() throws JSONException {
	JSONObject o = (JSONObject) new JSONTokener(testJson1).nextValue();
	StageInfo info = StageInfo.loadStage(o);
	assertEquals(40, info.width);
	assertEquals(10, info.height);
	assertEquals(info.regions.length, 2);
	assertEquals(info.regionTypes.length, 2);
	assertEquals(2, info.regions[0].left);
	assertEquals(8, info.regions[0].top);
	assertEquals(40, info.regions[0].right);
	assertEquals(10, info.regions[0].bottom);
	assertEquals(StageInfo.RegionType.GROUND, info.regionTypes[0]);
	assertEquals(12, info.regions[1].left);
	assertEquals(6, info.regions[1].top);
	assertEquals(14, info.regions[1].right);
	assertEquals(8, info.regions[1].bottom);
	assertEquals(StageInfo.RegionType.GROUND, info.regionTypes[1]);
    }
}
