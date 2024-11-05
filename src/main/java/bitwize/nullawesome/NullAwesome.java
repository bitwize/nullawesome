package bitwize.nullawesome;

import android.app.Activity;
import android.os.Bundle;
import android.content.pm.ActivityInfo;
import android.view.*;
import android.media.*;
public class NullAwesome extends Activity
{
    private NAView nv;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        ContentRepository cr;
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        EntityRepository.createInstance();
        ContentRepository.createInstance(this);
        cr = ContentRepository.get();
        cr.loadBitmap("player_r", R.drawable.player);
        cr.loadBitmap("test_bg", R.drawable.test_bg);
        cr.loadBitmap("testTiles", R.drawable.test_tiles);
        cr.flipBitmap("player_r", "player_l");
        cr.loadBitmap("buttons", R.drawable.buttons);
        cr.loadBitmap("buttons_pressed", R.drawable.buttons2);
        cr.loadBitmap("smallbuttons", R.drawable.smallbuttons);
        cr.loadBitmap("hacktarget", R.drawable.hacktarget);
        cr.loadBitmap("terminal", R.drawable.terminal);
        cr.loadBitmap("terminal_greentext", R.drawable.terminal_greentext);
        cr.loadBitmap("elevator1", R.drawable.elevator1);
        cr.loadBitmap("door", R.drawable.door);
        cr.loadBitmap("collectibles", R.drawable.collectibles);
        cr.loadBitmap("sparkles", R.drawable.sparkles);
        cr.loadBitmap("sentry_drone_r", R.drawable.sentrydrone);
        cr.flipBitmap("sentry_drone_r", "sentry_drone_l");
        cr.loadBitmap("soldier_r", R.drawable.soldier);
        cr.flipBitmap("soldier_r", "soldier_l");
        cr.loadBitmap("laser_bolt", R.drawable.laser_bolt);
        cr.loadBitmap("shock_ray_r", R.drawable.shock_ray);
        cr.flipBitmap("shock_ray_r", "shock_ray_l");
        cr.loadBitmap("end_door_open", R.drawable.end_door_open);
        cr.loadBitmap("pac_font", R.drawable.pac_font);
        cr.loadAnimation("player_stand", R.raw.player_stand);
        cr.loadAnimation("player_walk", R.raw.player_walk);
        cr.loadAnimation("player_jump", R.raw.player_jump);
        cr.loadAnimation("player_hack", R.raw.player_hack);
        cr.loadAnimation("player_putaway", R.raw.player_putaway);
        cr.loadAnimation("player_die", R.raw.player_die);
        cr.loadAnimation("sentry_drone_stand", R.raw.sentry_drone_stand);
        cr.loadAnimation("sentry_drone_walk", R.raw.sentry_drone_walk);
        cr.loadAnimation("soldier_stand", R.raw.soldier_stand);
        cr.loadAnimation("soldier_walk", R.raw.soldier_walk);
        cr.loadAnimation("shock_ray_anim", R.raw.shock_ray_anim);
        cr.loadAnimation("sparkles_anim", R.raw.sparkles_anim);
        cr.loadAnimation("end_door_anim", R.raw.end_door_anim);
        cr.loadStageOrder();
        cr.loadStage("test_level", R.raw.test_level);
        cr.loadStage("stage1_1", R.raw.stage1_1);
        cr.loadString("ready", R.string.ready);
        cr.loadString("scan", R.string.scan);
        cr.loadString("select", R.string.select);
        cr.loadString("access", R.string.access);
        cr.loadString("denied", R.string.denied);
        cr.loadString("granted", R.string.granted);
        cr.loadString("key_required", R.string.key_required);
        cr.loadString("copy", R.string.copy);
        cr.loadString("copied", R.string.copied);
        cr.loadSaveData();
        nv = new NAView(this);
        setContentView(nv);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        nv.surfaceDestroyed(nv.getHolder());
    }
}
