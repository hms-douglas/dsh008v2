package dev.dect.wear.watchface.dsh008.v2.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Locale;

import dev.dect.wear.watchface.dsh008.v2.R;
import dev.dect.wear.watchface.dsh008.v2.data.Constants;
import dev.dect.wear.watchface.dsh008.v2.downloader.ApkDownloader;
import dev.dect.wear.watchface.dsh008.v2.model.Adb;
import dev.dect.wear.watchface.dsh008.v2.model.Version;
import dev.dect.wear.watchface.dsh008.v2.utils.Utils;

@SuppressLint({"SetTextI18n", "ApplySharedPref"})
public class InstallProgressActivity extends AppCompatActivity {
    public static final String EXTRA_CONNECTING_IP_PORT = "extra_c_ip_port",
                               EXTRA_PARING_IP_PORT = "extra_p_ip_port",
                               EXTRA_PARING_CODE = "extra_p_code",
                               EXTRA_VERSION = "extra_version";

    private String CONNECTING_IP_PORT,
                   PARING_IP_PORT,
                   PARING_CODE;

    private Version VERSION;

    private ImageButton WRAP_EL;

    private EditText PROGRESS_EL;

    private StringBuilder PROGRESS;

    private SharedPreferences SP;

    private ApkDownloader APK_DOWNLOADER;

    private Adb ADB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_install_progress);

        Utils.updateStatusBarColor(this);

        initVariables();

        initListeners();

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(APK_DOWNLOADER != null && !APK_DOWNLOADER.isComplete()) {
            APK_DOWNLOADER.cancel();
        }

        if(ADB != null) {
            ADB.disconnectALL();
        }
    }

    private void initVariables() {
        VERSION = getIntent().getSerializableExtra(EXTRA_VERSION, Version.class);
        CONNECTING_IP_PORT = getIntent().getStringExtra(EXTRA_CONNECTING_IP_PORT);
        PARING_IP_PORT = getIntent().getStringExtra(EXTRA_PARING_IP_PORT);
        PARING_CODE = getIntent().getStringExtra(EXTRA_PARING_CODE);

        SP = getSharedPreferences(Constants.Sp.SP, MODE_PRIVATE);

        PROGRESS = new StringBuilder();

        PROGRESS_EL = findViewById(R.id.progress);
        WRAP_EL = findViewById(R.id.btnWrapUnwrap);

        ADB = new Adb(this);
    }

    private void initListeners() {
        Utils.addAppbarEffectListener(
            findViewById(R.id.titleBar),
            findViewById(R.id.toolbar),
            findViewById(R.id.titleExpanded),
            findViewById(R.id.titleCollapsed)
        );

        WRAP_EL.setOnClickListener((v) -> {
            final boolean b = !SP.getBoolean(Constants.Sp.INSTALLER_IS_TO_WRAP_TEXT, Constants.DefaultSettings.INSTALLER_IS_TO_WRAP_TEXT);

            SP.edit().putBoolean(Constants.Sp.INSTALLER_IS_TO_WRAP_TEXT, b).commit();

            updateWrapMode(v);
        });

        findViewById(R.id.btnBack).setOnClickListener((v) -> {
            v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);

            finish();
        });

        findViewById(R.id.btnCopyLog).setOnClickListener((v) -> {
            v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);

            final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            final ClipData clip = ClipData.newPlainText("log", PROGRESS_EL.getText().toString());

            clipboard.setPrimaryClip(clip);
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void init() {
        if(VERSION == null || !VERSION.isValid()) {
            Toast.makeText(this, getString(R.string.toast_error_invalid_version), Toast.LENGTH_SHORT).show();

            finish();

            return;
        }

        if(CONNECTING_IP_PORT == null || CONNECTING_IP_PORT.isEmpty() || !CONNECTING_IP_PORT.matches(Constants.REGEX_IPV4_AND_PORT)) {
            Toast.makeText(this, getString(R.string.toast_error_invalid_ip_connecting), Toast.LENGTH_SHORT).show();

            finish();

            return;
        }

        boolean[] isToPairFirst = new boolean[]{false};

        if(PARING_IP_PORT != null && !PARING_IP_PORT.isEmpty()) {
            if(!PARING_IP_PORT.matches(Constants.REGEX_IPV4_AND_PORT)) {
                Toast.makeText(this, getString(R.string.toast_error_invalid_ip_paring), Toast.LENGTH_SHORT).show();

                finish();

                return;
            }

            if(PARING_CODE == null || PARING_CODE.isEmpty() || PARING_CODE.length() < 5) {
                Toast.makeText(this, getString(R.string.toast_error_invalid_code_paring), Toast.LENGTH_SHORT).show();

                finish();

                return;
            }

            isToPairFirst[0] = true;
        }

        updateWrapMode(null);

        printWait();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if(isToPairFirst[0]) {
                pair();
            } else {
                connect();
            }
        }, 1000);
    }

    private void updateWrapMode(View v) {
        if(v != null) {
            v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
        }

        if(SP.getBoolean(Constants.Sp.INSTALLER_IS_TO_WRAP_TEXT, Constants.DefaultSettings.INSTALLER_IS_TO_WRAP_TEXT)) {
            WRAP_EL.setImageResource(R.drawable.icon_tool_bar_no_wrap);
            PROGRESS_EL.setHorizontallyScrolling(false);
        } else {
            WRAP_EL.setImageResource(R.drawable.icon_tool_bar_wrap);
            PROGRESS_EL.setHorizontallyScrolling(true);
        }
    }

    private void pair() {
        printProgressInformation(R.string.progress_paring, PARING_IP_PORT);

        final String result = ADB.pair(PARING_IP_PORT, PARING_CODE);

        if(result == null) {
            printProgressInformation(R.string.progress_paring_error, PARING_IP_PORT);

            endWithError();
        } else if(!result.toLowerCase(Locale.ROOT).contains("successfully")) {
            printProgressInformation(result);

            printProgressInformation(result);

            endWithError();
        } else {
            printProgressInformation(R.string.progress_paring_success, PARING_IP_PORT);

            printWait();

            new Handler(Looper.getMainLooper()).postDelayed(this::connect, 2000);
        }
    }

    private void connect() {
        printProgressInformation(R.string.progress_connecting, CONNECTING_IP_PORT);

        final String result = ADB.connect(CONNECTING_IP_PORT);

        if(result == null) {
            printProgressInformation(R.string.progress_connecting_error, CONNECTING_IP_PORT);

            endWithError();
        } else if(result.toLowerCase(Locale.ROOT).contains("refused")) {
            printProgressInformation(R.string.progress_connecting_error, CONNECTING_IP_PORT);

            printProgressInformation(result);

            endWithError();
        } else if(result.toLowerCase(Locale.ROOT).contains("failed to connect")) {
            printProgressInformation(R.string.progress_connecting_error, CONNECTING_IP_PORT);
            printProgressInformation(R.string.progress_connecting_pair, CONNECTING_IP_PORT);

            endWithError();
        } else {
            printProgressInformation(R.string.progress_connecting_success, CONNECTING_IP_PORT);

            getFile();
        }
    }

    private void install(File file) {
        printProgressInformation(R.string.progress_installing);

        printWait();

        new Thread(() -> {
            final String result = ADB.install(file);

            new Handler(Looper.getMainLooper()).post(() -> {
                if(result == null) {
                    printProgressInformation(R.string.progress_installing_error);

                    endWithError();
                } else if(result.toLowerCase(Locale.ROOT).contains("downgrade")) {
                    printProgressInformation(R.string.progress_installing_error);

                    printProgressInformation(getString(R.string.progress_installing_error_downgrade).replaceFirst("%s", VERSION.getVersionName()));

                    endWithError();
                } else if(!result.toLowerCase(Locale.ROOT).contains("success")) {
                    printProgressInformation(R.string.progress_installing_error);

                    printProgressInformation(result);

                    endWithError();
                } else {
                    printProgressInformation(R.string.progress_success);

                    endWithSuccess();
                }
            });
        }).start();
    }

    private void getFile() {
        printProgressInformation(R.string.progress_checking_cache);

        final File fileVersion = new File(getCacheDir(), VERSION.getFileName());

        if(fileVersion.exists()) {
            printProgressInformation(R.string.progress_checking_cache_found);

            install(fileVersion);
        } else {
            printProgressInformation(R.string.progress_checking_cache_not_found);

            downloadFile();
        }
    }

    private void downloadFile() {
        printProgressInformation(R.string.progress_download_initialized);

        APK_DOWNLOADER = new ApkDownloader(
            this,
            VERSION,
            new ApkDownloader.OnApkDownloaderListener() {
                @Override
                public void onProgressChange(float percentage) {
                    printProgressBar(R.string.progress_download_progress, percentage, false);
                }

                @Override
                public void onDownloadComplete(File apk) {
                    printProgressBar(R.string.progress_download_progress, 100f, true);

                    printProgressInformation(R.string.progress_download_progress_complete);

                    printProgressInformation(R.string.progress_checking_cached);

                    install(apk);
                }

                @Override
                public void onDownloadError(float progress) {
                    printProgressBar(R.string.progress_download_progress, progress, true);

                    printProgressInformation(R.string.progress_download_progress_error);

                    endWithError();
                }
            }
        );

        APK_DOWNLOADER.download();
    }

    private void disconnect() {
        final String result = ADB.disconnectALL();

        if(result != null && !result.isEmpty()) {
            printProgressInformation(R.string.progress_disconnected);
        }
    }

    private void endWithSuccess() {
        disconnect();

        printProgressInformation(R.string.progress_end);

        PROGRESS.append(getString(R.string.progress_end_message));

        PROGRESS_EL.setText(PROGRESS.toString());

        PROGRESS_EL.setTextColor(getColor(R.color.progress_text_success));
    }

    private void endWithError() {
        disconnect();

        printProgressInformation(R.string.progress_end);

        PROGRESS_EL.setTextColor(getColor(R.color.progress_text_error));
    }

    private void printProgressInformation(int resId) {
        printProgressInformation(getString(resId));
    }

    private void printProgressInformation(int resId, String s) {
        printProgressInformation(getString(resId).replaceFirst("%s", s));
    }

    private void printProgressInformation(String s) {
        if(PROGRESS.length() != 0) {
            PROGRESS.append("\n");
        }

        PROGRESS.append("> " + s);

        PROGRESS_EL.setText(PROGRESS.toString());
    }

    private void printWait() {
        if(PROGRESS.length() == 0) {
            PROGRESS_EL.setText("> " + getString(R.string.progress_wait));
        } else {
            PROGRESS_EL.setText(PROGRESS.toString() + "\n> " + getString(R.string.progress_wait));
        }
    }

    private void printProgressBar(int resId, float progress, boolean save) {
        final String s = getString(resId).replaceFirst("%d", String.format(Locale.getDefault(), "%.1f", progress));

        if(save) {
            printProgressInformation(s);
        } else {
            PROGRESS_EL.setText(PROGRESS.toString() + "\n> " + s);
        }
    }
}