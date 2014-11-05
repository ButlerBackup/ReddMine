package dev.blacksheep.reddmine;

import dev.blacksheep.reddmine.fragments.AboutFragment;
import dev.blacksheep.reddmine.fragments.HelpFragment;
import dev.blacksheep.reddmine.fragments.MainFragment;
import dev.blacksheep.reddmine.fragments.WithdrawFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private CharSequence mTitle;
	String[] titles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		titles = getResources().getStringArray(R.array.titles);
		setContentView(R.layout.activity_main);
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		switch (position) {
		case 0:
			fragmentManager.beginTransaction().replace(R.id.container, new MainFragment()).commit();
			break;
		case 1:
			fragmentManager.beginTransaction().replace(R.id.container, new WithdrawFragment()).commit();
			break;
		case 2:
			startActivity(new Intent(MainActivity.this, SettingsActivity.class));
			break;
		case 3:
			fragmentManager.beginTransaction().replace(R.id.container, new HelpFragment()).commit();
			break;
		case 4:
			fragmentManager.beginTransaction().replace(R.id.container, new AboutFragment()).commit();
			break;
		default:
			break;
		}
	}

	public void onSectionAttached(int number) {
		mTitle = titles[number];
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

}
