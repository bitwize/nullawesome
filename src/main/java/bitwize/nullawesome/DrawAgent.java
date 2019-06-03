package bitwize.nullawesome;

import android.graphics.*;
import android.view.*;
import java.util.*;
import android.util.Log;
import android.util.DisplayMetrics;

public class DrawAgent {

    public static final int HRES = 480;
    public static final int VRES = 320;

    private ArrayList<RenderAgent> rlist;
    private SurfaceHolder holder;
    private boolean running;
    private Rect src, dst;
    private Matrix xform;
    private static final Paint paint = new Paint();
    private Bitmap backBuffer;
    private Canvas backCanvas;
    public DrawAgent(ArrayList<RenderAgent> r) {
	rlist = r;
	src = new Rect();
	dst = new Rect();
	xform = new Matrix();
	backBuffer = Bitmap.createBitmap(HRES, VRES, Bitmap.Config.ARGB_8888);
	backBuffer.setDensity(Bitmap.DENSITY_NONE);
	backCanvas = new Canvas(backBuffer);
	paint.setAntiAlias(false);
	paint.setAlpha(255);
    }
    
    public void draw() {
	backBuffer.eraseColor(0xff000000);
	Canvas c2;
	for(int i=0; i<rlist.size();i++) {
	    rlist.get(i).drawOn(backCanvas);
	}
	if(holder != null) {
	    c2 = holder.lockCanvas();
	    if(c2 != null) {
		src.left = 0; src.top = 0;
		dst.left = 0; dst.top = 0;
		src.right = HRES; src.bottom = VRES;
		dst.right = c2.getWidth(); dst.bottom = c2.getHeight();
		c2.drawColor(0, PorterDuff.Mode.CLEAR);
		c2.drawBitmap(backBuffer, src, dst, paint);
		holder.unlockCanvasAndPost(c2);
	    }
	}
    }

    public void setHolder(SurfaceHolder h) {
	holder = h;
    }

    public void drawMap(Canvas c, TileMap map, Point offset) {
	int w = c.getWidth();
	int h = c.getHeight();
	int tw = (w / TileMap.TILE_SIZE) + 1;
	int th = (h / TileMap.TILE_SIZE) + 1;
	int tl = offset.x / TileMap.TILE_SIZE;
	int tt = offset.y / TileMap.TILE_SIZE;
	int tr = tl + tw;
	int tb = tt + th;
	if(tl < 0) tl = 0;
	if(tt < 0) tt = 0;
	if(tr > map.getWidth()) tr = map.getWidth();
	if(tb > map.getHeight()) tb = map.getHeight();
	for(int j=tt; j<tb; j++) {
	    for(int i=tl; i<tr; i++) {
		int oy = j * TileMap.TILE_SIZE;
		int ox = i * TileMap.TILE_SIZE;
		short tile = map.getTile(i, j);
		if(tile <= 0) continue;
		src.left = 0;
		src.top = TileMap.TILE_SIZE * tile;
		src.right = TileMap.TILE_SIZE;
		src.bottom = src.top + TileMap.TILE_SIZE;
		dst.left = ox - offset.x;
		dst.top = oy - offset.y;
		dst.right = dst.left + TileMap.TILE_SIZE;
		dst.bottom = dst.top + TileMap.TILE_SIZE;
		c.drawBitmap(map.getTileImage(),src, dst, paint);
	    }
	}
    }

    public void drawSprite(Canvas c, Bitmap b, Rect subsection, PointF location) {
	dst.left = (int)location.x;
	dst.top = (int)location.y;
	dst.right = dst.left + (subsection.right - subsection.left);
	dst.bottom = dst.top + (subsection.bottom - subsection.top);
	c.drawBitmap(b, subsection, dst, paint);
    }
    public void drawTileBG(Canvas c, Bitmap b, Rect subsection, PointF location) {
	int w = subsection.width();
	int h = subsection.height();
	int startx = -(((int)location.x) % w);
	int starty = -(((int)location.y) % h);
	if(startx > 0) startx -= w;
	if(starty > 0) starty -= h;
	for(; starty < VRES; starty += h) {
	    for(; startx < HRES; startx += w) {
		dst.left = startx;
		dst.top = starty;
		dst.right = dst.left + w;
		dst.bottom = dst.top + h;
		c.drawBitmap(b, subsection, dst, paint);
	    }
	}
    }
    public void drawButton(Canvas c, Bitmap b, Rect subsection, PointF location) {
	paint.setAlpha(192);
	drawSprite(c, b, subsection, location);
	paint.setAlpha(255);
    }

}
