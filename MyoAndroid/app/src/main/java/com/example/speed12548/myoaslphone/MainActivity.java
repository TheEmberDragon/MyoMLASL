package com.example.speed12548.myoaslphone;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.scanner.ScanActivity;

import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {

    private static final String[] LOC_PERMS = {Manifest.permission.ACCESS_COARSE_LOCATION};
    TextToSpeech mTTS = null;
    private final int ACT_CHECK_TTS_DATA = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText ettext = (EditText)findViewById(R.id.txtText);
        final Button bsay = (Button)findViewById(R.id.btnSpeak);
        bsay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saySomething(ettext.getText().toString().trim(), 1);
            }
        });

        // Check to see if we have TTS voice data
        try {
            Intent ttsIntent = new Intent();
            ttsIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(ttsIntent, ACT_CHECK_TTS_DATA);
        }catch (ActivityNotFoundException e) {
            Log.e("Richard's Phone","YOYO Homey");
        }


    }

    private void saySomething(String text, int qmode) {
        if (qmode == 1)
            mTTS.speak(text, TextToSpeech.QUEUE_ADD, null, null);
        else
            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == ACT_CHECK_TTS_DATA) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // Data exists, so we instantiate the TTS engine
                mTTS = new TextToSpeech(this, this);
            } else {
                // Data is missing, so we start the TTS
                // installation process
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            if (mTTS != null) {
                int result = mTTS.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "TTS language is not supported", Toast.LENGTH_LONG).show();
                } else {
                    saySomething("TTS is ready", 0);
                }
            }
        } else {
            Toast.makeText(this, "TTS initialization failed",
                    Toast.LENGTH_LONG).show();
        }

        requestPermissions(LOC_PERMS,1340);

        Hub hub = Hub.getInstance();
        if (!hub.init(this)) {
            Log.e("HUBTAG", "Could not initialize the Hub.");
            finish();
            return;
        }
        hub.addListener(mListener);

        // Use this instead to connect with a Myo that is very near (ie. almost touching) the device
        Log.e("BLUETAG", "Checking Permission");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(LOC_PERMS,1340);
            Log.e("BLUETAG", "Location failed");
        }
        Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
        startActivity(intent);

//        Hub.getInstance().attachToAdjacentMyo();
        Hub.getInstance().setLockingPolicy(Hub.LockingPolicy.NONE);
    }

    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }

    private DeviceListener mListener = new AbstractDeviceListener() {
        @Override
        public void onConnect(Myo myo, long timestamp) {
            Toast.makeText(getApplicationContext(), "Myo Connected!", Toast.LENGTH_SHORT).show();
            saySomething("Myo is connected",0);
        }

        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            Toast.makeText(getApplicationContext(), "Myo Disconnected!", Toast.LENGTH_SHORT).show();
            saySomething("Myo is disconnected",0);
        }

        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            Toast.makeText(getApplicationContext(), "Pose: " + pose, Toast.LENGTH_SHORT).show();
            saySomething(pose.name(),1);

            //TODO: Do something awesome.
        }
    };
}