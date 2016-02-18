package bitwize.nullawesome;

import java.util.ArrayList;
import java.util.BitSet;
import android.util.Log;

public class PreallocPool<T> {
    ArrayList<T> objects;
    int current;
    PreallocFactory<T> factory;
    public PreallocPool(int size, PreallocFactory<T> fac) {
	objects = new ArrayList<T>(size);
	factory = fac;
	for(int i=0; i<size; i++) {
	    objects.add(factory.make());
	}
    }

    public T get() {
	T o = objects.get(current);
	current++;
	if(current >= objects.size()) {
	    Log.i("PreallocPool", "creating new; pool size is now" + current);
	    objects.add(factory.make());
	}
	return o;
    }
    public void putAll() {
	current = 0;
    }
}
