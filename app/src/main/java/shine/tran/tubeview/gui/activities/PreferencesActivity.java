package shine.tran.tubeview.gui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import shine.tran.tubeview.R;
import shine.tran.tubeview.gui.fragments.PreferencesFragment;

/**
 * The preferences activity allows the user to change the settings of this app.  This activity
 * loads {@link PreferencesFragment}.
 */
public class PreferencesActivity extends AppCompatActivity {
public static View VIEW = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preferences);
        VIEW = findViewById(R.id.activity_pre);
        // display the PreferencesFragment as the main content
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferencesFragment())
                .commit();

        // display the back button in the action bar (left-hand side)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.preferences);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // when the user clicks the back/home button...
            case android.R.id.home:
                // close this activity
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
