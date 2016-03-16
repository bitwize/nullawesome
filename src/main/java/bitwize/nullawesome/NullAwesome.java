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
	ContentRepository.get().loadBitmap("block1", R.drawable.block1);
	ContentRepository.get().flipBitmap("player_r", "player_l");
	ContentRepository.get().loadBitmap("buttons", R.drawable.buttons);
	ContentRepository.get().loadBitmap("buttons_pressed", R.drawable.buttons2);
	nv = new NAView(this);
        setContentView(nv);
    }

    @Override
    public void onDestroy() {
	super.onDestroy();
	nv.surfaceDestroyed(nv.getHolder());
    }
}
