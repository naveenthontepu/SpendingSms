package com.example.naveen.spendingsms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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

        printText.setText("Total Expenditure = INR "+prefs.getFloat("amount",0));

        if (!prefs.getBoolean("serviceStarted",false)) {
            Intent intent = new Intent(this, Sms_Listener.class);
            startService(intent);
            editor.putBoolean("serviceStarted",true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sms = sms_text.getText().toString();
            }
        });
        fab.setVisibility(View.GONE);
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
}
