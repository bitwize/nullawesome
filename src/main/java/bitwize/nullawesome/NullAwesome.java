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
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
			     WindowManager.LayoutParams.FLAG_FULLSCREEN);
	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	setVolumeControlStream(AudioManager.STREAM_MUSIC);
	EntityRepository.createInstance();
	ContentRepository.createInstance(this);
	ContentRepository.get().loadBitmap("player_r", R.drawable.player);
	ContentRepository.get().loadBitmap("test_bg", R.drawable.test_bg);
	ContentRepository.get().loadBitmap("testTiles", R.drawable.test_tiles);
	ContentRepository.get().flipBitmap("player_r", "player_l");
	ContentRepository.get().loadBitmap("buttons", R.drawable.buttons);
	ContentRepository.get().loadBitmap("buttons_pressed", R.drawable.buttons2);
	ContentRepository.get().loadBitmap("smallbuttons", R.drawable.smallbuttons);
	ContentRepository.get().loadBitmap("hacktarget", R.drawable.hacktarget);
	ContentRepository.get().loadBitmap("terminal", R.drawable.terminal);
	ContentRepository.get().loadBitmap("terminal_greentext", R.drawable.terminal_greentext);
	ContentRepository.get().loadBitmap("elevator1", R.drawable.elevator1);
	ContentRepository.get().loadBitmap("door", R.drawable.door);
	ContentRepository.get().loadBitmap("collectibles", R.drawable.collectibles);
	ContentRepository.get().loadBitmap("sparkles", R.drawable.sparkles);
	ContentRepository.get().loadBitmap("sentry_drone_r", R.drawable.sentrydrone);
	ContentRepository.get().flipBitmap("sentry_drone_r", "sentry_drone_l");
	ContentRepository.get().loadBitmap("soldier_r", R.drawable.soldier);
	ContentRepository.get().flipBitmap("soldier_r", "soldier_l");
	ContentRepository.get().loadBitmap("laser_bolt", R.drawable.laser_bolt);
	ContentRepository.get().loadBitmap("shock_ray_r", R.drawable.shock_ray);
	ContentRepository.get().flipBitmap("shock_ray_r", "shock_ray_l");
	ContentRepository.get().loadBitmap("end_door_open", R.drawable.end_door_open);
	ContentRepository.get().loadBitmap("pac_font", R.drawable.pac_font);
	ContentRepository.get().loadAnimation("player_stand", R.raw.player_stand);
	ContentRepository.get().loadAnimation("player_walk", R.raw.player_walk);
	ContentRepository.get().loadAnimation("player_jump", R.raw.player_jump);
	ContentRepository.get().loadAnimation("player_hack", R.raw.player_hack);
	ContentRepository.get().loadAnimation("player_putaway", R.raw.player_putaway);
	ContentRepository.get().loadAnimation("player_die", R.raw.player_die);
	ContentRepository.get().loadAnimation("sentry_drone_stand", R.raw.sentry_drone_stand);
	ContentRepository.get().loadAnimation("sentry_drone_walk", R.raw.sentry_drone_walk);
	ContentRepository.get().loadAnimation("soldier_stand", R.raw.soldier_stand);
	ContentRepository.get().loadAnimation("soldier_walk", R.raw.soldier_walk);
	ContentRepository.get().loadAnimation("shock_ray_anim", R.raw.shock_ray_anim);
	ContentRepository.get().loadAnimation("sparkles_anim", R.raw.sparkles_anim);
	ContentRepository.get().loadAnimation("end_door_anim", R.raw.end_door_anim);
	ContentRepository.get().loadStageOrder();
	ContentRepository.get().loadStage("test_level", R.raw.test_level);
	ContentRepository.get().loadStage("stage1_1", R.raw.stage1_1);
	ContentRepository.get().loadSaveData();
	nv = new NAView(this);
        setContentView(nv);
    }

    @Override
    public void onDestroy() {
	super.onDestroy();
	nv.surfaceDestroyed(nv.getHolder());
    }
}
