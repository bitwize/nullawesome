package bitwize.nullawesome;

import android.graphics.*;
import org.json.*;
import java.io.IOException;

/*
 * The StageInfo class contains all the information necessary to
 * initialize a stage -- including layout of the terrain and spawn
 * points for the player, items, enemies, and gadgets such as
 * elevators and doors. The terrain is laid out as a series of
 * rectangular regions, each having certain properties (normal ground,
 * slippery, harmful, etc.), "greeblized" (made more interesting by
 * varying the actual tiles used), and rendered into a TileMap. Both
 * the terrain regions and the spawn points for the various "things"
 * in the world are specified in a JSON representation, which this
 * class is responsible for loading.
 */

public class StageInfo {

    public static final int TILE_EMPTY = 0;
    public static final int TILE_GROUND = 1;

    public int width, height;
    public Rect[] regions;
    public int[] regionTypes;
    public Point[] thingLocations;
    public int[] thingTypes;
    public String tileImageName;
    public TileMap map;
    public static StageInfo loadStage(JSONObject o) {
	StageInfo info = new StageInfo();
	try {
	    JSONArray regionsArray = o.getJSONArray("regions");
	    JSONArray thingsArray = o.getJSONArray("things");
	    info.width = o.getInt("width");
	    info.height = o.getInt("height");
	    info.tileImageName = o.getString("tileImageName");
	    info.regions = new Rect[regionsArray.length()];
	    info.regionTypes = new int[regionsArray.length()];
	    for(int i=0; i<info.regions.length;i++) {
		JSONObject rgnobj = regionsArray.getJSONObject(i);
		int l = rgnobj.getInt("left");
		int t = rgnobj.getInt("top");
		int r = rgnobj.getInt("right");
		int b = rgnobj.getInt("bottom");
		int temp;
		if(r < l) {
		    temp = r; r = l; l = temp;
		}
		if(b < t) {
		    temp = b; b = t; t = temp;
		}
		info.regions[i] = new Rect();
 		info.regions[i].left = l;
 		info.regions[i].top = t;
 		info.regions[i].right = r;
 		info.regions[i].bottom = b;
		info.regionTypes[i] = rgnobj.getInt("type");
		info.map = TileMap.createFromInfo(info);
	    }
	}
	catch(JSONException je) {
	    throw new RuntimeException(je);
	}
	return info;
    }
    public static StageInfo getTestInfo() {
	try {
	    JSONObject o = (JSONObject) ContentRepository.get().loadJSON(R.raw.test_level);
	    StageInfo info = StageInfo.loadStage(o);
	    return info;
	}
	catch(JSONException e) {
	    throw new RuntimeException(e);
	}
	catch(IOException e) {
	    throw new RuntimeException(e);
	}
    }
}
