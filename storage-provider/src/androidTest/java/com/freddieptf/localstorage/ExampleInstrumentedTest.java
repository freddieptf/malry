package com.freddieptf.localstorage;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private final String TAG = getClass().getSimpleName();

    @Test
    public void testPersm() {
        assertTrue(
                ContextCompat.checkSelfPermission(InstrumentationRegistry.getContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED);

        assertTrue(
                ContextCompat.checkSelfPermission(InstrumentationRegistry.getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED);
    }

    @Test
    public void openFile() {
        File file = new File("storage/83E6-11EA/LibraryItem/Deadman Wonderland/1_ Who Killed Cock Robin");
//        File file = new File("/storage/emulated/0/Pictures/Screenshots");
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        assertTrue(file.isDirectory());

        assertTrue(file.canRead());
        assertTrue(file.listFiles().length > 0);
        Log.d(TAG, "openFile: " + file.listFiles().length);
        assertTrue(file.canWrite());

    }

}
