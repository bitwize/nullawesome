package bitwize.nullawesome;

import android.graphics.Bitmap;
import android.graphics.Rect;
import org.json.*;

public class SpriteShape {
    public Bitmap shapes;
    public Rect subsection;
    public int maxFrames;
    public int currentFrame;
    public int currentTime;
    public int[] frames;
    public int[] timings;
    public boolean loop;
    public static SpriteShape loadAnimation(JSONObject o) {
	SpriteShape s = new SpriteShape();
	try {
	    JSONArray framesArray;
	    JSONArray timingsArray;
	    s.shapes = ContentRepository.get().getBitmap(o.getString("shapeName"));
	    s.maxFrames = o.getInt("maxFrames");
	    framesArray = o.getJSONArray("frames");
	    timingsArray = o.getJSONArray("timings");
	    s.loop = o.getBoolean("loop");
	    if(framesArray.length() != timingsArray.length()) {
		s.maxFrames = 0;
		s.frames = null;
		s.timings = null;		    
		return s;
	    }
	    s.frames = new int[framesArray.length()];
	    s.timings = new int[timingsArray.length()];
	    for(int i=0;i<s.frames.length;i++) {
		s.frames[i] = framesArray.getInt(i);
		s.timings[i] = timingsArray.getInt(i);
	    }
	}
	catch(JSONException je) {
	    throw new RuntimeException(je);
	}
	return s;
    }
}
