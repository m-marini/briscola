package org.mmarini.briscola.app;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * 
 * @author us00852
 * 
 */
public class SettingsActivity extends PreferenceActivity {

	/**
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
