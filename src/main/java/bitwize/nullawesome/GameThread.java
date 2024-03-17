package bitwize.nullawesome;

import java.util.ArrayList;
import android.os.SystemClock;
import android.util.Log;

public class GameThread extends Thread {

    private DrawAgent dagent;
    private ArrayList<UpdateAgent> uagents;
    private ArrayList<UpdateAgent> puagents;
    private ArrayList<RenderAgent> ragents;
    private long clock, oldclock, gameclock, pauseclock, diff;
    private boolean paused;
    private boolean running;
    public GameThread(DrawAgent da, ArrayList<UpdateAgent> ua,
                      ArrayList<UpdateAgent> pua) {
        super();
        clock = SystemClock.uptimeMillis();
        oldclock = clock;
        diff = 0;
        gameclock = 0;
        pauseclock = 0;
        paused = false;
        running = false;
        dagent = da;
        uagents = ua;
        puagents = pua;
    }
    
    public void run() {
        while(running) {
            synchronized(this) {
                oldclock = clock;
                clock = SystemClock.uptimeMillis();
                diff += clock - oldclock;
                if(paused)  {
                    while(diff >= 16) {
                        pauseclock += 16;
                        int sz = puagents.size();
                        for(int i=0;i<sz;i++) {
                            puagents.get(i).update(pauseclock);
                        }
                        diff -= 16;
                    }
                } else {
                    while(diff >= 16) {
                        gameclock += 16;
                        int sz = uagents.size();
                        for(int i=0;i<sz;i++) {
                            uagents.get(i).update(gameclock);
                        }
                        diff -= 16;
                    }
                }
                dagent.draw();
                System.gc();
            }
            try {
                Thread.sleep(15);
            }
            catch(InterruptedException e) { }
        }
    }
    
    public void pauseGame() {
        synchronized(this) { paused = true; }
    }
    public void resumeGame() {
        synchronized(this) { paused = false; }
    }

    public boolean isPaused() {
        synchronized(this) { return paused; }
    }
    public void startRunning() {
        synchronized(this) { running = true; }
        if(!isAlive()) start();
    }
    public void stopRunning() {
        synchronized(this) { running = false; }
    }
}
