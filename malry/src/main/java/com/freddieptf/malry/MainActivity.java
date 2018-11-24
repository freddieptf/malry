package com.freddieptf.malry;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.freddieptf.malry.library.LibViewModelFactory;
import com.freddieptf.malry.library.LibraryFragment;
import com.freddieptf.mangatest.R;

import java.util.Objects;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity implements LibraryFragment.LibraryFragmentContainer {

    final public static String ACTION_UPDATE = "update";
    public static boolean DEBUG = true;
    private static final int STORAGE_REQ_CODE = 100;
    final String LOG_TAG = this.getClass().getSimpleName();
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar_actionBar);
        setSupportActionBar(toolbar);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            showLibraryFragment();
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQ_CODE);
        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_REQ_CODE && permissions.length > 0 &&
                Objects.equals(permissions[0], Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showLibraryFragment();
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




