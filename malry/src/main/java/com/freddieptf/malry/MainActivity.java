package com.freddieptf.malry;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.freddieptf.malry.intro.IntroFragment;
import com.freddieptf.malry.library.LibraryFragment;
import com.freddieptf.mangatest.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;


public class MainActivity extends AppCompatActivity implements LibraryFragment.LibraryFragmentContainer {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar_actionBar);
        setSupportActionBar(toolbar);

        if (PrefUtils.INSTANCE.hasSetUp(this)) showLibraryFragment();
        else showIntroFragment();

    }

    private void showLibraryFragment() {
        LibraryFragment libraryFragment = (LibraryFragment) getSupportFragmentManager()
                .findFragmentByTag(LibraryFragment.class.getSimpleName());
        if (libraryFragment == null) {
            libraryFragment = new LibraryFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, libraryFragment, LibraryFragment.class.getSimpleName())
                    .commit();
        }
    }

    private void showIntroFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(IntroFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new IntroFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment, IntroFragment.class.getSimpleName())
                    .commit();
        }
    }

    @Override
    public int getFragmentContainerId() {
        return R.id.container;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manga_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        switch (id) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            case R.id.action_settings:
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}




