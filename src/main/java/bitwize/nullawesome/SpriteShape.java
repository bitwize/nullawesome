package bitwize.nullawesome;

import android.graphics.Bitmap;
import android.graphics.Rect;
import org.json.*;

public class SpriteShape {
    public Bitmap shapes;
    public Rect subsection = new Rect();
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
    public static void copyShape(SpriteShape shpA, SpriteShape shpB)
    {
        shpB.shapes = shpA.shapes;
        shpB.subsection.set(shpA.subsection);
        shpB.maxFrames = shpA.maxFrames;
        shpB.currentFrame = 0;
        shpB.currentTime = 0;
        shpB.frames = shpA.frames;
        shpB.timings = shpA.timings;
        shpB.loop = shpA.loop;
    }
    public static void changeAnimation(SpriteShape shpA, SpriteShape shpB)
        {
                if(shpA.frames == shpB.frames) return;
//              if(shpA.maxFrames != shpB.maxFrames) return;
                shpA.frames = shpB.frames;
                shpA.timings = shpB.timings;
                shpA.loop = shpB.loop;
                shpA.currentFrame = 0;
                shpA.currentTime = 0;
        }
}
