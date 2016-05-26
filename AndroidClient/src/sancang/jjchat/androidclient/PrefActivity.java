package sancang.jjchat.androidclient;
import sancang.jjchat.androidclient.R;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;


public class PrefActivity extends PreferenceActivity {

	PreferenceManager prefMgr;
	EditTextPreference prefName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		prefMgr = getPreferenceManager();
		
		addPreferencesFromResource(R.xml.layout_pref);
		
		prefName = (EditTextPreference) prefMgr.findPreference("name");
		prefName.setSummary(prefName.getText());
		
		prefName.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				
				prefName.setSummary((CharSequence) newValue);
				return true;
			}
		});
	}
}
