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
	
    public enum RegionType {
	EMPTY,
	GROUND
    }
    
    public int width, height;
    public Rect[] regions;
    public StageInfo.RegionType[] regionTypes;
    public JSONObject[] thingParams;
    public int[] thingIds;
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
	    info.regionTypes = new StageInfo.RegionType[regionsArray.length()];
	    info.thingIds = new int[thingsArray.length()];
	    info.thingParams = new JSONObject[thingsArray.length()];
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
		info.regionTypes[i] =
			StageInfo.RegionType.values()[rgnobj.getInt("type")];
	    }
	    for(int i=0; i<info.thingParams.length;i++) {
		    JSONObject thingObj = thingsArray.getJSONObject(i);
		    info.thingParams[i] = thingObj;
		    info.thingIds[i] = EntityRepository.NO_ENTITY;
	    }
	    info.map = TileMap.createFromInfo(info);
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
    public static int getEidForThing(StageInfo info, int thingNum) {
	if(thingNum <  0) return EntityRepository.NO_ENTITY;
	if(thingNum >= info.thingIds.length) return EntityRepository.NO_ENTITY;
	return info.thingIds[thingNum];
    }
}
