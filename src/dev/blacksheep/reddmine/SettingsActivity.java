package dev.blacksheep.reddmine;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

import com.securepreferences.SecurePreferences;

public class SettingsActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
		Preference pref_tutorial = (Preference) findPreference("pref_tutorial");
		pref_tutorial.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				SecurePreferences sp = new SecurePreferences(SettingsActivity.this);
				sp.edit().putString("tutorial", "0").commit();
				startActivity(new Intent(SettingsActivity.this, TutorialActivity.class));
				finish();
				return true;
			}
		});
	}
}
