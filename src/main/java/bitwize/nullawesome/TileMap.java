package bitwize.nullawesome;

import android.graphics.*;
import java.util.ArrayList;

public class TileMap {
    
    public static final int TILE_SIZE = 32;

    public static final int FLAG_SOLID = 1;

    private Bitmap tileImage;
    private Bitmap backgroundImage;
    private Rect backgroundImageSection;
    private int[] tileFlags;
    private short[] map;
    private int width, height;
    
    private TileMap(Bitmap i, Bitmap b,int w, int h) {
	tileImage = i;
	backgroundImage = b;
	if(b != null) {
	    backgroundImageSection = new Rect(0, 0, b.getWidth(), b.getHeight());
	} else {
	    backgroundImageSection = new Rect(0, 0, 0, 0);
	}
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
    public Bitmap getBackgroundImage() {
	return backgroundImage;
    }
    public Rect getBackgroundImageSection() {
	return backgroundImageSection;
    }

    private void fillRect(Rect r, StageInfo.RegionType type) {
	Rect r2 = new Rect(r);
	boolean result = r2.intersect(0, 0, width, height);
	if(!result) return;
	switch(type) {
	case EMPTY:
	    for(int j=r2.top; j<r2.bottom; j++) {
		for(int i=r2.left; i<r2.right; i++) {
		    map[j * width + i] = 0;
		}
	    }
	    break;
	case GROUND:
	    for(int j=r2.top; j<r2.bottom; j++) {
		for(int i=r2.left; i<r2.right; i++) {
		    map[j * width + i] = 1;
		}
	    }
	    break;
	case LAVA:
	    for(int j=r2.top; j<r2.bottom; j++) {
		for(int i=r2.left; i<r2.right; i++) {
		    map[j * width + i] = 2;
		}
	    }
	    break;
	}
    }

    public static TileMap createFromInfo(StageInfo info) {
	ContentRepository repo = ContentRepository.get();
	TileMap tm;
	if(repo != null) {
	    tm = new TileMap(repo.getBitmap(info.tileImageName),
			     repo.getBitmap(info.backgroundImageName),
			     info.width, info.height);
	}
	else {
	    tm = new TileMap(null, null,
			     info.width, info.height);
	}
	for(int i=0; i<info.regions.length; i++) {
	    tm.fillRect(info.regions[i],
			info.regionTypes[i]);
	}
	tm.tileFlags[1] = FLAG_SOLID;
	return tm;
    }
}
