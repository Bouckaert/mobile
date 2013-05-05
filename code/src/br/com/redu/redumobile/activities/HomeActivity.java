package br.com.redu.redumobile.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.data.LoadStatusesFromWebTask;
import br.com.redu.redumobile.db.DbHelper;
import br.com.redu.redumobile.db.DbHelperHolder;
import br.com.redu.redumobile.fragments.TitlableFragment;
import br.com.redu.redumobile.fragments.home.EnvironmentFragment;
import br.com.redu.redumobile.fragments.home.LastSeenFragment;
import br.com.redu.redumobile.fragments.home.NewLecturesFragment;
import br.com.redu.redumobile.fragments.home.UserWallFragment;
import br.com.redu.redumobile.util.PinCodeHelper;

import com.buzzbox.mob.android.scheduler.SchedulerManager;
import com.buzzbox.mob.android.scheduler.analytics.AnalyticsManager;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class HomeActivity extends BaseActivity implements DbHelperHolder {

	public static final String ITEM_EXTRA_PARAM = "ITEM_CHECKED";

	private static final int DELAY_TO_CHECK_NOTIFICATIONS_IN_MINUTES = 30;
	
	static final int NUM_ITEMS = 4;
	
	static final int ITEM_LAST_SEEN_STATUS = 0;
	static final int ITEM_NEW_LECTURES = 1;
	static final int ITEM_WALL = 2;
	static final int ITEM_ENVIRONMENTS = 3;
	
	private View popupMenuButton;
	private PopupWindow popupWindow;
	
	private DbHelper mDbHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_home);

		setActionBarTitle("Redu");

		popupMenuButton = addActionToActionBar(R.drawable.ic_menu, new OnClickListener() {
			@Override
			public void onClick(View v) {
				onPopupMenuClicked();
			}
		});
		
		// START BuzzNotify
		int openAppStatus = AnalyticsManager.onOpenApp(this);
		if (openAppStatus == AnalyticsManager.OPEN_APP_FIRST_TIME) {
			SchedulerManager.getInstance().saveTask(this, "*/" + DELAY_TO_CHECK_NOTIFICATIONS_IN_MINUTES + " * * * *", LoadStatusesFromWebTask.class);
			SchedulerManager.getInstance().restart(this, LoadStatusesFromWebTask.class);
		} else if (openAppStatus == AnalyticsManager.OPEN_APP_UPGRADE) {
			SchedulerManager.getInstance().restartAll(getApplicationContext());
		}
		SchedulerManager.getInstance().runNow(this, LoadStatusesFromWebTask.class, 0);
		// END BuzzNotify

		final ViewPager vp = (ViewPager) findViewById(R.id.vp);
		vp.setAdapter(new MainAdapter(getSupportFragmentManager()));
		
		PageIndicator indicator = (TitlePageIndicator) findViewById(R.id.titles);
		indicator.setViewPager(vp);
		
		int itemChecked = getIntent().getIntExtra(ITEM_EXTRA_PARAM, ITEM_WALL);
		indicator.setCurrentItem(itemChecked);
		
		mDbHelper = DbHelper.getInstance(this);
		
		initializePopupMenu();
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		onPopupMenuClicked();
		return false;
	}

	private void onPopupMenuClicked() {
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
		} else {
			popupWindow.showAsDropDown(popupMenuButton);
		}
	}
	
	private void initializePopupMenu() {
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

		ListView popupMenuContent = (ListView) inflater.inflate(
				R.layout.popup_menu, null);

		popupMenuContent.setAdapter(new ArrayAdapter<String>(getApplicationContext(), 
				R.layout.popup_menu_item, 
				new String[] {"Configurações", "Termos de uso", "Privacidade", "Sair"}));
		
		popupMenuContent.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent i = null;
				
				switch (position) {
				case 0:
					i = new Intent(getApplicationContext(), SettingsActivity.class);
					break;
				case 1:
					i = new Intent(getApplicationContext(), TermsOfUseActivity.class);
					break;
				case 2:
					i = new Intent(getApplicationContext(), PrivacyActivity.class);
					break;
				case 3:
					i = new Intent(getApplicationContext(), LoginWebViewActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					PinCodeHelper.clearPinCode(getApplicationContext());
					break;
				}
				
				startActivity(i);
			}
		});

		popupWindow = new PopupWindow(getApplicationContext());
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setWidth((int) dipToPx(200, getResources()));
		popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		popupWindow.setContentView(popupMenuContent);
		popupWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					popupWindow.dismiss();
					return true;
				}
				return false;
			}
		});
	}
	
	public float dipToPx(int dip, Resources res) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, res.getDisplayMetrics());
	}
	
	class MainAdapter extends FragmentStatePagerAdapter {
		private final TitlableFragment[] fragments;

		public MainAdapter(FragmentManager fm) {
			super(fm);

			fragments = new TitlableFragment[NUM_ITEMS];

			fragments[ITEM_NEW_LECTURES] = new NewLecturesFragment();
			fragments[ITEM_LAST_SEEN_STATUS] = new LastSeenFragment();
			fragments[ITEM_WALL] = new UserWallFragment();
			fragments[ITEM_ENVIRONMENTS] = new EnvironmentFragment();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return fragments[position].getTitle();
		}
		
		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		@Override
		public Fragment getItem(int position) {
			return fragments[position];
		}
	}

	@Override
	protected void onDestroy() {
		mDbHelper.close();
		super.onDestroy();
	}

	@Override
	public DbHelper getDbHelper() {
		return mDbHelper;
	}

}
