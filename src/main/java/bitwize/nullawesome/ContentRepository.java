package bitwize.nullawesome;

import android.graphics.*;
import android.media.*;
import android.content.Context;
import android.content.res.Resources;
import java.util.HashMap;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.json.*;

public class ContentRepository {
    private HashMap<String, Bitmap> bitmaps;
    private HashMap<String, Integer> sounds;
    private HashMap<String, JSONObject> animations;
    private SoundPool spool;
    private Context ctx;
    private static ContentRepository theInstance;
    private ContentRepository(Context c) {
	ctx = c;
	bitmaps = new HashMap<String, Bitmap>();
	sounds = new HashMap<String, Integer>();
	animations = new HashMap<String, JSONObject>();
	spool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
    }

    public void empty() {
	bitmaps = new HashMap<String, Bitmap>();
	sounds = new HashMap<String, Integer>();
	spool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
    }

    public Bitmap getBitmap(String name) {
	return bitmaps.get(name);
    }

    public JSONObject getAnimation(String name) {
	return animations.get(name);
    }

    public int getSoundID(String name) {
	return sounds.get(name);
    }


    public void loadBitmap(String name, int resID) {
	BitmapFactory.Options options = new BitmapFactory.Options();
	options.inDensity = Bitmap.DENSITY_NONE;
	options.inScaled = false;
	Bitmap b =  BitmapFactory.decodeResource(ctx.getResources(), resID, options);
	bitmaps.put(name, b);
    }

    public void loadSound(String name, int resID) {
	sounds.put(name, spool.load(ctx, resID, 1));
    }

    public void loadAnimation(String name, int resID)
    {
	try {
	    byte[] buf = new byte[1024];
	    InputStream strm = ctx.getResources().openRawResource(resID);
	    ByteArrayOutputStream ostrm = new ByteArrayOutputStream(1024);
	    while(strm.read(buf) > 0) {
		ostrm.write(buf, 0, buf.length);
	    }
	    String s = ostrm.toString();
	    JSONObject json = (JSONObject) new JSONTokener(s).nextValue();
	    animations.put(name, json);	
	}
	catch(Exception e) {
	    throw new RuntimeException(e);
	}
    }
    public void flipBitmap(String src, String dest) {
	Bitmap b = bitmaps.get(src);
	Matrix m = new Matrix();
	m.setScale(-1.f, 1.f);
	m.postTranslate(b.getWidth(), 0);
	bitmaps.put(dest, Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, false));
    }

    public SoundPool getSoundPool() {
	return spool;
    }

    public static void createInstance(Context c) {
	theInstance = new ContentRepository(c);
    }

    public static ContentRepository get() {
	return theInstance;
    }

}
