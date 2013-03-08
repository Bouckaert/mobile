package br.com.redu.redumobile.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import br.com.redu.redumobile.widgets.ActionBar;

public class BaseActivity extends FragmentActivity {
	private ActionBar mActionBar;

	protected void onCreate(Bundle savedInstanceState, int layoutResID) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(layoutResID);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
	}

	public void setActionBar(ActionBar ab) {
		mActionBar = ab;
		mActionBar.setActivity(this);
		// mActionBar.setUpClass(HomeActivity.class);
	}

	protected void setUpClass(Class<? extends Activity> upClass) {
		mActionBar.setUpClass(upClass);
	}

	protected void addActionToActionBar(int drawableResId,
			OnClickListener clickAction) {
		mActionBar.addAction(drawableResId, clickAction);
	}

	protected void setActionBarTitle(String title) {
		mActionBar.setTitle(title);
	}

	public void onNotificationsClicked(View v) {
		
	}
	
	public void onSettingsClicked(View v) {
		
	}
	
	public void onEnvironmentClicked(View v) {
		Intent i = new Intent(this, EnvironmentActivity.class);
		startActivity(i);
	}
	
//	@Override
//	public void onBackPressed() {
//
//		finish(ActivityAnimation.SLIDE_RIGHT);
//	}
}
