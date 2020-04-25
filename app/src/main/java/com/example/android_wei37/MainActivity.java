package com.example.android_wei37;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private String[] Permission={Manifest.permission.BLUETOOTH,Manifest.permission.ACCESS_FINE_LOCATION};
    private ListView listView;
    private SimpleAdapter mysimpleAdapter;
    private LinkedList<HashMap<String,String>> data=new LinkedList<>();
    private String[] from={"name","mac"};
    private int[] to={R.id.item_name, R.id.item_mac};
    private LinkedList<BluetoothDevice>devices=new LinkedList<>();
    private Handler handler;
    private BluetoothAdapter bluetoothAdapter;//自己的
    private BluetoothDevice bluetoothDevice;//要連接的
    private myreceiver myreceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    Permission,
                    123);
        }else{
            //已有
            init();
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myreceiver!=null){
            unregisterReceiver(myreceiver);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    private void init(){
        listView=findViewById(R.id.listDevice);
        mysimpleAdapter=new SimpleAdapter(this,data,R.layout.item,from,to);

        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        //enable confirm
        if (!bluetoothAdapter.isEnabled()){
            Intent enableBtIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //會回傳requestcode
            startActivityForResult(enableBtIntent,321);
        }else{
            regReveiver();
        }
    }
    //判斷requestcode

    private void regReveiver(){
        myreceiver=new myreceiver();
        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(myreceiver, filter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==321 && resultCode==RESULT_OK){
            regReveiver();
        }
    }

    //Find
    //Query已有的



    //傳統藍芽的實做僅能支持一對一,BLE低功耗支持多對一
    //...
    public void test1(View view) {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.v("wei",deviceName+":"+deviceHardwareAddress);
            }
        }
    }

    //scan device
    public void test2(View view) {
        if (!bluetoothAdapter.isDiscovering()){
            Log.v("wei","沒有在搜尋");
            data.clear();
            devices.clear();
            bluetoothAdapter.startDiscovery();
        }
    }

    private class myreceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String deviceName = device.getName();
            String deviceHardwareAddress = device.getAddress(); // MAC address

            if (devices.contains(device)){
                HashMap<String,String> rawdata=new HashMap<>();
                rawdata.put(from[0],deviceName);
                rawdata.put(from[1],deviceHardwareAddress);
                data.add(rawdata);
                devices.add(device);
                mysimpleAdapter.notifyDataSetChanged();
                Log.v("wei","scaned device name:"+deviceName);
            }
        }
    }
    //stop scan
    public void test3(View view) {
        if (bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
    }

    public void test4(View view) {
    }
}
