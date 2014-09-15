package wisteria429.wearhackathon;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


public class MyActivity extends Activity implements SensorEventListener{
    private final static String TAG = "MyActivity";

    private SensorManager mSensorManager;
    private Sensor sensor;
    private float[] currentOrientationValues = { 0.0f, 0.0f, 0.0f };
    private float[] currentAccelerationValues = { 0.0f, 0.0f, 0.0f };

    private boolean islisten =false;
    private ArrayList<Integer> sendValue = new ArrayList<Integer>();
    private Timer timer;
    private MyTimerTask timerT;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
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
    protected void onResume() {
        super.onResume();

        //加速度センサをリスナに登録
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGoogleApiClient.connect();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (islisten) {
            float[] values = event.values;

            //重力加速度を取り除く
            currentOrientationValues[0] = event.values[0] * 0.1f + currentOrientationValues[0] * (1.0f - 0.1f);
            currentOrientationValues[1] = event.values[1] * 0.1f + currentOrientationValues[1] * (1.0f - 0.1f);
            currentOrientationValues[2] = event.values[2] * 0.1f + currentOrientationValues[2] * (1.0f - 0.1f);

            currentAccelerationValues[0] = event.values[0] - currentOrientationValues[0];
            currentAccelerationValues[1] = event.values[1] - currentOrientationValues[1];
            currentAccelerationValues[2] = event.values[2] - currentOrientationValues[2];

            int type = AcceleParse.getAcceleType(currentAccelerationValues);
            Log.d(TAG, "TYPE : " + type
                    + "X : " + currentAccelerationValues[AcceleParse.ACCELE_X]
                    + "Y : " + currentAccelerationValues[AcceleParse.ACCELE_Y]
                    + "Z : " + currentAccelerationValues[AcceleParse.ACCELE_Z]);
            if (type != 0) {
                sendValue.add(type);
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG,"onAccuracyChanged: " + sensor + ", accuracy: " + accuracy);
    }

    //変身ボタンをタップした時の動作
    public void onStartListen(View v) {
        sendValue.clear();
        //センサのチェックを開始
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        //3秒間のタイマーを開始
        timerT = new MyTimerTask();
        timer = new Timer(true);
        timer.schedule(timerT, 3000);

        islisten = true;
    }


    public class MyTimerTask extends TimerTask{

        @Override
        public void run() {
            //3秒経過後の処理
            islisten = false;
            Log.d(TAG, Arrays.toString(sendValue.toArray()));


            // DataMapインスタンスを生成する
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create("/datapath");
            DataMap dataMap = dataMapRequest.getDataMap();

            // データをセットする
            dataMap.putIntegerArrayList("key", sendValue);

            // データを更新する
            PutDataRequest request = dataMapRequest.asPutDataRequest();
            PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);
            pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    Log.d("TAG", "onResult: " + dataItemResult.getStatus());
                }
            });


        }
    }

}
