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

    private void fillRect(Rect r, int type) {
	Rect r2 = new Rect(r);
	boolean result = r2.intersect(0, 0, width, height);
	if(!result) return;
	switch(type) {
	case StageInfo.TILE_EMPTY:
	    for(int j=r2.top; j<r2.bottom; j++) {
		for(int i=r2.left; i<r2.right; i++) {
		    map[j * width + i] = 0;
		}
	    }
	    break;
	case StageInfo.TILE_GROUND:
	    for(int j=r2.top; j<r2.bottom; j++) {
		for(int i=r2.left; i<r2.right; i++) {
		    map[j * width + i] = 1;
		}
	    }
	    break;
	}
    }

    public static TileMap getTestMap() {
	TileMap tm;
	Bitmap b = ContentRepository.get().getBitmap("block1");
	Canvas c = new Canvas(b);
	Paint p = new Paint();
	p.setAntiAlias(false);
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
    public static TileMap createFromInfo(StageInfo info) {
	ContentRepository repo = ContentRepository.get();
	TileMap tm;
	if(repo != null) {
	    tm = new TileMap(repo.getBitmap(info.tileImageName),
			     info.width, info.height);
	}
	else {
	    tm = new TileMap(null,
			     info.width, info.height);
	}
	for(int i=0; i<info.regions.length; i++) {
	    tm.fillRect(info.regions[i], info.regionTypes[i]);
	}
	tm.tileFlags[1] = FLAG_SOLID;
	return tm;
    }
}
