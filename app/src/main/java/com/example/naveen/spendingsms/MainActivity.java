package com.example.naveen.spendingsms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;
import java.util.InputMismatchException;

public class MainActivity extends AppCompatActivity {

    EditText sms_text;
    TextView printText;
    final String TAG ="spendingsms";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    EditText banktext;
    Button storebutton;
    TextInputLayout bt_til_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = getSharedPreferences("com.example.naveen.spendingsms", MODE_PRIVATE);
        editor = prefs.edit();
        editor.commit();
        sms_text = (EditText)findViewById(R.id.sms_text);
        sms_text.setVisibility(View.GONE);
        printText = (TextView)findViewById(R.id.printText);
        banktext=(EditText)findViewById(R.id.banktext);
        bt_til_et=(TextInputLayout)findViewById(R.id.bt_til_et);

        storebutton=(Button)findViewById(R.id.storebutton);

        printText.setText("Total Expenditure = INR "+prefs.getFloat("amount",0));


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sms = sms_text.getText().toString();
            }
        });
        fab.setVisibility(View.GONE);
        if (prefs.getString("bankname",null)!=null){
            bt_til_et.setVisibility(View.GONE);
            banktext.setVisibility(View.GONE);
            storebutton.setVisibility(View.GONE);
            printText.setVisibility(View.VISIBLE);
        }else {
            bt_til_et.setVisibility(View.VISIBLE);
            banktext.setVisibility(View.VISIBLE);
            printText.setVisibility(View.GONE);
            storebutton.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void bankname(View view) {
        if(banktext.getText().toString()!=null&&!banktext.getText().toString().equals("")){
            bt_til_et.setError("");
            if (bt_til_et.getChildCount() == 2)
                bt_til_et.getChildAt(1).setVisibility(View.INVISIBLE);
            editor.putString("bankname",banktext.getText().toString().toUpperCase());
            editor.commit();
            if (!prefs.getBoolean("serviceStarted",false)) {
                Intent intent = new Intent(this, Sms_Listener.class);
                startService(intent);
                editor.putBoolean("serviceStarted", true);
                editor.commit();
            }
            printText.setVisibility(View.VISIBLE);
            bt_til_et.setVisibility(View.GONE);
            banktext.setVisibility(View.GONE);
            storebutton.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(printText.getWindowToken(),0);
        }else {
            if (bt_til_et.getChildCount() == 2)
                bt_til_et.getChildAt(1).setVisibility(View.VISIBLE);
            bt_til_et.setError("Please Enter Bank Name");
        }
    }
}
