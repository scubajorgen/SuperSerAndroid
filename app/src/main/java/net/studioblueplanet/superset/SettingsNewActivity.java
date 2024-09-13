package net.studioblueplanet.superset;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsNewActivity extends PreferenceActivity
{
	private static final String ACTION_PREFS_LAYOUT="net.studioblueplanet.superset.PREFS_LAYOUT";
	private static final String ACTION_PREFS_USER="net.studioblueplanet.superset.PREFS_USER";
	/**
	 * Determines whether to always show the simplified settings UI, where
	 * settings are presented in a single list. When false, settings are shown
	 * as a master/detail two-pane view on tablets. When true, a single pane is
	 * shown on tablets.
	 */
	private static final boolean ALWAYS_SIMPLE_PREFS = false;

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		String action;
		super.onPostCreate(savedInstanceState);

		// Next code is executed for the old versions or if 
		// ALWAYS_SIMPLE_PREFS is true. It shows simple preferences
	    action = getIntent().getAction();
	    if (action != null)
	    {
	    	if (action.equals(ACTION_PREFS_USER)) 
		    {
		        addPreferencesFromResource(R.xml.pref_user);
		    }
	    	else if (action.equals(ACTION_PREFS_LAYOUT)) 
	    	{
		        addPreferencesFromResource(R.xml.pref_layout);
	    	}
	    		
	    }
	    else
	    {
		    if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) || (ALWAYS_SIMPLE_PREFS)) 
		    {
		        // Load the legacy preferences headers
		        addPreferencesFromResource(R.xml.pref_headers_legacy);
		    }
	    }
	}

	@Override
    protected boolean isValidFragment(String string)
	{
		return true;
	}

	/** {@inheritDoc} */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target)
	{
		// This code shows the new preferences
		if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) && !ALWAYS_SIMPLE_PREFS)
		{
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}


	/**
	 * This fragment shows notification preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class LayoutPreferenceFragment extends
	        PreferenceFragment
	{
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_layout);
		}
	}

	/**
	 * This fragment shows data and sync preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class UserPreferenceFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_user);

		}
	}
}
