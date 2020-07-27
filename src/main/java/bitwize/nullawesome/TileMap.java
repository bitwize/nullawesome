package bitwize.nullawesome;

import android.graphics.*;
import java.util.ArrayList;

public class TileMap {
    
    public static final int TILE_SIZE = 32;

    public static final int FLAG_SOLID = 1;
    public static final int FLAG_TOUCHDEATH=2;
    private Bitmap tileImage;
    private Bitmap backgroundImage;
    private Rect backgroundImageSection;
    private int[] tileFlags;
    private short[] map;
    private int width, height;
    private Greeblizer greeblizers[] = {
	(map, width, height, r, type) -> {
	    for(int j=r.top; j<r.bottom; j++) {
		for(int i=r.left; i<r.right; i++) {
		    map[j * width + i] = 0;
		}
	    }
	},
	(map, width, height, r, type) -> {
	    if(r.bottom - r.top == 1) {
		map[r.top * width + r.left] = 4;
		map[r.top * width + r.right - 1] = 5;
		for(int i=r.left + 1; i<r.right - 1; i++) {
		    map[r.top * width + i] = 3;
		}
	    } else {
		for(int i=r.left; i<r.right; i++) {
		    if(r.top > 0
		       && map[(r.top - 1) * width + i] == 0) {
			map[r.top * width + i] = 2;
		    } else {	
			map[r.top * width + i] = 1;
		    }
		    if(r.bottom < height && map[r.bottom * width + i] == 2) {
			map[r.bottom * width + i] = 1;
		    }
		}
		for(int j=r.top + 1; j<r.bottom; j++) {		
		    for(int i=r.left; i<r.right; i++) {
			map[j * width + i] = 1;
		    }
		}
	    }
	},
	(map, width, height, r, type) -> {
	    for(int i=r.left; i<r.right; i++) {
		map[r.top * width + i] = 7;
	    }
	    for(int j=r.top + 1; j<r.bottom; j++) {
		for(int i=r.left; i<r.right; i++) {
		    map[j * width + i] = 6;
		}
	    }
	}
    };
    
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

    public void setTile(int x, int y, short v) {
	if(x < 0 || x >= width) return;
	if(y < 0 || y >= height) return;
	map[y * width + x] = v;
    }

    public int getWidth() {
	return width;
    }

    public int getHeight() {
	return height;
    }
    public short getTileWorldCoords(float x, float y) {
	if(x < 0 || y < 0) return 0;
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
	greeblizers[type.ordinal()].fillRect(map, width, height, r2, type);
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
	tm.tileFlags[2] = FLAG_SOLID;
	tm.tileFlags[3] = FLAG_SOLID;
	tm.tileFlags[4] = FLAG_SOLID;
	tm.tileFlags[5] = FLAG_SOLID;
	tm.tileFlags[6] = FLAG_SOLID | FLAG_TOUCHDEATH;
	tm.tileFlags[7] = FLAG_SOLID | FLAG_TOUCHDEATH;
	return tm;
    }
}
