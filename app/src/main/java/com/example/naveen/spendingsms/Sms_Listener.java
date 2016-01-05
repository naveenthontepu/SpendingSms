package com.example.naveen.spendingsms;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.Arrays;

public class Sms_Listener extends Service {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    final String TAG ="spendingsms";
    ReadSms readsms;
    IntentFilter intentFilter;
    public Sms_Listener() {
    }

    public void getnumbers(String sms){
        String[] dividedstring = sms.split(" ");
        Log.i(TAG,"strings = "+ Arrays.asList(sms.split(" ")));
        int length = dividedstring.length;
        float number=0;
        for (int i=0;i<length;i++){
            if (dividedstring[i].toUpperCase().contains("INR")){
                if (dividedstring[i].length()==3){
                    number = getnumber(dividedstring[i+1]);
                    i+=1;
                }else {
                    number = getnumber(dividedstring[i]);
                }
                break;
            }
            Log.i(TAG,"number = "+ number);
        }
        if (!sms.toUpperCase().contains("OTP")) {
            if (sms.contains("withdrawal") || sms.contains("debited") || sms.contains("purchase")) {
                float total = prefs.getFloat("amount",0);
                editor.putFloat("amount",total+number);
                editor.commit();
            }
        }
    }

    public float getnumber(String number){
        float amount=0;
        String num = number.toUpperCase().replaceAll("[a-zA-z]+", "");
        String num1 = num.replaceAll(",", "");
        Log.i(TAG, "strings = " + num + " num1 = " + num1);
        num1 = num1.startsWith(".") ? num1.substring(1) : num1;
        num1 = num1.endsWith(".")?num1.substring(0,(num1.length()-1)):num1;
        Log.i(TAG,"nu1 = "+num1);
        amount = Float.parseFloat(num1);
        return amount;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"onCreate");
        prefs = getSharedPreferences("com.example.naveen.spendingsms", MODE_PRIVATE);
        editor = prefs.edit();
        editor.commit();
        readsms = new ReadSms() {
            @Override
            protected void onSmsReceived(String s) {
                getnumbers(s);
            }
        };
        intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"onStartCommand");
        registerReceiver(readsms,intentFilter);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(readsms);
    }
    public abstract class ReadSms extends BroadcastReceiver {

        SmsManager sms;
        public ReadSms() {
            sms = SmsManager.getDefault();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            // an Intent broadcast.
            Bundle bundle = intent.getExtras();
            Log.i(TAG, "sms bundle = " + bundle);

            try {

                if (bundle != null) {

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {

                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i],"3gpp");
                        if (currentMessage == null){
                            currentMessage = SmsMessage.createFromPdu((byte[])pdusObj[i],"3gpp2");
                        }
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                        Log.i(TAG,"phone nummber ="+phoneNumber);
                        String senderNum = phoneNumber;
                        String message = currentMessage.getDisplayMessageBody();

                        if(message != null) {
                            int length = message.length();
                            //message = message.substring(length - 4, length);
                            Log.e("Message", "Hello" + message);

                            if (phoneNumber.toUpperCase().contains(prefs.getString("bankname",null))) {
                                onSmsReceived(message);
                                abortBroadcast();
                            }
                            //Toast.makeText(context, "akbar broadacast message "+message, Toast.LENGTH_SHORT).show();
                        }
                    } // end for loop
                } // bundle is null

            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" + e);

            }

        }
        protected abstract void onSmsReceived(String s);

    }

}
