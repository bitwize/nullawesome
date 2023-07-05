package bitwize.nullawesome;

import android.graphics.PointF;
import java.util.BitSet;

public class SpriteOverlay {
    public SpriteShape[] shapes;
    public PointF[] offsets;
    public BitSet draw;
    public SpriteOverlay(int size) {
        shapes = new SpriteShape[size];
        offsets = new PointF[size];
        draw = new BitSet();
    }
    public SpriteOverlay put(int index, SpriteShape shp, PointF ptf) {
        shapes[index] = shp;
        offsets[index] = ptf;
        draw.set(index, true);
        return this;
    }
    public SpriteOverlay put(int index, SpriteShape shp, float x, float y) {
        return put(index, shp, new PointF(x, y));
    }
}
