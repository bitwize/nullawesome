package bitwize.nullawesome;

import android.graphics.PointF;
import java.util.HashMap;

public class ElevatorState {
    public static enum Type {
        STATIONARY,
        OSCILLATING,
        STEPTRIGGERED;
        private static HashMap<String, Type> namedTypes = new HashMap<String, Type>();
        static {
            for(Type t : Type.values()) {
                namedTypes.put(t.toString().toLowerCase(), t);
            }
        }
        public static Type byName(String aName) {
            Type t = namedTypes.get(aName);
            if(t == null) throw new RuntimeException("invalid elevator-state type");
            return t;
        }
    }
    public Type type = Type.STATIONARY;
    public PointF fulcrum = new PointF();
    public PointF startPoint = new PointF();
    public float springConstant;
}
