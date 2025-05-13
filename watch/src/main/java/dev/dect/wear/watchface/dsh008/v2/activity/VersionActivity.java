package dev.dect.wear.watchface.dsh008.v2.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.wearable.Node;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import dev.dect.wear.watchface.dsh008.v2.R;
import dev.dect.wear.watchface.dsh008.v2.data.Constants;
import dev.dect.wear.watchface.dsh008.v2.utils.Utils;

public class VersionActivity extends Activity {
    /**
     * Activity responsible for checking if the app is updated and providing links to update it
     */

    private final String TAG = VersionActivity.class.getSimpleName();

    private final Node[] PHONE_NODE = new Node[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_version);

        initVariables();

        init();
    }

    private void initVariables() {
        Utils.getPhoneNodeAsync(this, PHONE_NODE);
    }

    /** @noinspection deprecation*/
    private void init() {
        new CheckUpdate((data) -> new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if(data == null) {
                Toast.makeText(this, getString(R.string.toast_error_update), Toast.LENGTH_SHORT).show();

                Log.e(TAG, "init0: null");

                finish();

                return;
            }

            try {
                if(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode < data.getInt(Constants.Url.KeyTag.LATEST_VERSION_VERSION_CODE)) {
                    //Gets the download url (google drive) of the latest version
                    final String downloadUrl = data.getString(Constants.Url.KeyTag.LATEST_VERSION_LINK);

                    findViewById(R.id.update).setVisibility(View.VISIBLE);
                    //Launches on the phone the browser on the download url screen
                    findViewById(R.id.btnDownloadOnPhone).setOnClickListener((v) -> Utils.openLinkOnPhone(this, PHONE_NODE, downloadUrl));
                } else {
                    findViewById(R.id.updated).setVisibility(View.VISIBLE);
                }

                findViewById(R.id.loader).setVisibility(View.GONE);
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.toast_error_update), Toast.LENGTH_SHORT).show();

                Log.e(TAG, "init1: " + e.getMessage());

                finish();
            }
        }, 3000)).start(); //Delayed so the "Utils.getPhoneNodeAsync" have time to run, just in case it fetches the data fast lol
    }

    private static class CheckUpdate extends Thread {
        private final String TAG = VersionActivity.class.getSimpleName() + "." + CheckUpdate.class.getSimpleName();

        public interface CheckUpdateListener {
            void onResult(JSONObject json);
        }

        private final CheckUpdateListener LISTENER;

        public CheckUpdate(CheckUpdateListener l) {
            this.LISTENER = l;
        }

        @Override
        public void run() {
            String data = "";

            try {
                final URL url = new URL(Constants.Url.LATEST_VERSION_FILE);

                final HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                final InputStream inputStream = httpURLConnection.getInputStream();

                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while((line = bufferedReader.readLine()) != null) {
                    data += line;
                }

                inputStream.close();

                //Converts the json text file to json and passes it to the interface reference
                this.LISTENER.onResult(new JSONObject(data));
            } catch (IOException | JSONException e) {
                this.LISTENER.onResult(null);

                Log.e(TAG, "run: " + e.getMessage());
            }
        }
    }
}
