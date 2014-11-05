package dev.blacksheep.reddmine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.securepreferences.SecurePreferences;

import dev.blacksheep.reddmine.fragments.TutorialOneFragment;
import dev.blacksheep.reddmine.fragments.TutorialTwoFragment;

public class TutorialActivity extends FragmentActivity {
	FragmentPagerAdapter adapterViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);
		SecurePreferences sp = new SecurePreferences(TutorialActivity.this);
		if (sp.getString("tutorial", "").equals("1")) {
			startActivity(new Intent(TutorialActivity.this, MainActivity.class));
			finish();
		}
		ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
		adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
		vpPager.setAdapter(adapterViewPager);
	}

	public static class MyPagerAdapter extends FragmentPagerAdapter {
		private static int NUM_ITEMS = 2;

		public MyPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return new TutorialOneFragment();
			case 1:
				return new TutorialTwoFragment();
			default:
				return null;
			}
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "Page " + position;
		}

	}

}