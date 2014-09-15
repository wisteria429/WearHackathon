package wisteria429.wearhackathon;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItemAsset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Arrays;


public class MyActivity extends Activity implements DataApi.DataListener,GoogleApiClient.ConnectionCallbacks{
    private final static String TAG = "MyActivity";
    private GoogleApiClient mGoogleApiClient;
    private Handler handler;

    private ArrayList<Integer> data;

    private SoundPool mSoundPool;
    private int mSoundId[] = new int[6];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        handler = new Handler();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();



    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();

        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        mSoundId[0] = mSoundPool.load(getApplicationContext(), R.raw.a, 0);
        mSoundId[1] = mSoundPool.load(getApplicationContext(), R.raw.b, 0);
        mSoundId[2] = mSoundPool.load(getApplicationContext(), R.raw.c, 0);
        mSoundId[3] = mSoundPool.load(getApplicationContext(), R.raw.d, 0);
        mSoundId[4] = mSoundPool.load(getApplicationContext(), R.raw.e, 0);
        mSoundId[5] = mSoundPool.load(getApplicationContext(), R.raw.e, 0);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSoundPool.release();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged");
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.d(TAG, "DataItem deleted: " + event.getDataItem().getUri());
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {

                DataMap dataMap = DataMap.fromByteArray(event.getDataItem().getData());
                data = dataMap.getIntegerArrayList("key");


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                TextView text = (TextView)findViewById(R.id.text);
                                text.setText(Arrays.toString(data.toArray()));
                                for (int i: data) {
                                    mSoundPool.play(i, 1.0F, 1.0F, 0, 0, 1.0F);
                                    try {
                                        Thread.sleep(500);


                                    } catch (Exception e) {

                                    }
                                }
                            }
                        });
                    }
                }).start();
            }
        }
    }

}
