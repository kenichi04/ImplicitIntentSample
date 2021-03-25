package com.example.implicitintentsample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class IntentStartActivity extends AppCompatActivity {

    /* 緯度 */
    private double _latitude = 0;
    /* 経度 */
    private double _longitude = 0;

    private TextView _tvLatitude;
    private TextView _tvLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_start);

        _tvLatitude = findViewById(R.id.tvLatitude);
        _tvLongitude = findViewById(R.id.tvLongitude);

        // LocationManagerオブジェクト取得
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // 位置情報が更新された際のリスなオブジェクト生成
        GPSLcationListener locationListener = new GPSLcationListener();

        // パーミッションが下りていない場合
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // ACCESS_FINE_LOCATIONの許可を求めるダイアログ表示。リクエストコード1000に設定
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(IntentStartActivity.this, permissions, 1000);
            // onCreate()を抜ける
            return;
        }
        // 位置情報の追跡を開始（パーミッションチェック後）
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 許可の場合
        if (requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            GPSLcationListener locationListener = new GPSLcationListener();
            // 再度パーミッションチェック
            if (ActivityCompat.checkSelfPermission(IntentStartActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    public void onMapSearchButtonClick(View view) {
        EditText etSearchWord = findViewById(R.id.etSearchWord);
        String searchWord = etSearchWord.getText().toString();

        try {
            // URLエンコード
            searchWord = URLEncoder.encode(searchWord, "UTF-8");
            // マップアプリと連携する文字列を生成
            String uriStr = "geo:0,0?q=" + searchWord;
            // URIオブジェクト生成
            Uri uri = Uri.parse(uriStr);

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);


        } catch (UnsupportedEncodingException e) {
            Log.e("IntentStartActivity", "検索キーワード変換失敗" , e);
        }
    }

    public void onMapShowCurrentButtonClick(View view) {
        String uriStr = "geo:" + _latitude + "," + _longitude;
        Uri uri = Uri.parse(uriStr);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private class GPSLcationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            // Locationオブジェクトから緯度、経度を取得
            _latitude = location.getLatitude();
            _longitude = location.getLongitude();

            _tvLatitude.setText(Double.toString(_latitude));
            _tvLongitude.setText(Double.toString(_longitude));
        }

        @Override
        public void onStatusChanged(String provideer, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }
}