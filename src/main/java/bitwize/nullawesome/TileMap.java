package bitwize.nullawesome;

import android.graphics.*;
import java.util.ArrayList;

public class TileMap {
    
    public static final int TILE_SIZE = 32;

    public static final int FLAG_SOLID = 1;

    private Bitmap tileImage;
    private int[] tileFlags;
    private short[] map;
    private int width, height;
    
    private TileMap(Bitmap i, int w, int h) {
	tileImage = i;
	tileFlags = new int[1024];
	width = w; height = h;
	map = new short[w * h];
    }

    public short getTile(int x, int y) {
	if(x < 0 || x >= width) return 0;
	if(y < 0 || y >= height) return 0;
	return map[y * width + x];
    }

    public int getWidth() {
	return width;
    }

    public int getHeight() {
	return height;
    }
    public short getTileWorldCoords(float x, float y) {
	return getTile((int)x / TILE_SIZE, (int)y / TILE_SIZE);
    }
    public int getTileFlags(short tile) {
	if(tile < 0 || tile >= tileFlags.length) return 0;
	return tileFlags[tile];
    }

    public Bitmap getTileImage() {
	return tileImage;
    }

    public static TileMap getTestMap() {
	TileMap tm;
	Bitmap b = Bitmap.createBitmap(TILE_SIZE, TILE_SIZE * 2, Bitmap.Config.ARGB_8888);
	b.eraseColor(Color.TRANSPARENT);
	Canvas c = new Canvas(b);
	Paint p = new Paint();
	p.setColor(0xffffff00);
	p.setStyle(Paint.Style.FILL);
	c.drawRect(0, TILE_SIZE, TILE_SIZE, TILE_SIZE * 2, p);
	tm = new TileMap(b, 40, 25);
	for(int i=0; i<40; i++) {
	    tm.map[320+i] = 1;
	    tm.map[360+i] = 1;
	}
	tm.map[292] = 1;
	tm.map[252] = 1;
	tm.tileFlags[1] = FLAG_SOLID;
	return tm;
    }
}
