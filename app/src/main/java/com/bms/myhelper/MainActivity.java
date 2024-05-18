package com.bms.myhelper;


import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.SEND_SMS;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    protected static String gandu_PASSWORD="BENCHOD";
    protected static int ALLOW=0;
    protected static HashMap<String,String> contactList = new HashMap<>();
    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] ma_ka_bhosda_PERMISSIONS = new String[]{RECEIVE_SMS,POST_NOTIFICATIONS,SEND_SMS,READ_CONTACTS,ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION};
        if (!hasPermissions(this, ma_ka_bhosda_PERMISSIONS)) ActivityCompat.requestPermissions(this, ma_ka_bhosda_PERMISSIONS,1);
        else {
            if (ActivityCompat.checkSelfPermission(this, ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)ActivityCompat.requestPermissions(this,new String[]{ACCESS_BACKGROUND_LOCATION},1);
            else getContactList();
        }
    }
    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) for (String permission : permissions) if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) return false;
        return true;
    }
    protected static void sendSms(String n, String m){
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> parts =smsManager.divideMessage(m);
        smsManager.sendMultipartTextMessage(n.trim(), null,parts, null, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)ActivityCompat.requestPermissions(this,new String[]{ACCESS_BACKGROUND_LOCATION},1);
                    else getContactList();
                }
                break;
            }
        }
    }

    private void getContactList() {
        Intent intent = new Intent(this,service.class);
        if (!service.fuck(this)) startForegroundService(intent);
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor != null) {
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String name, number;
                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex);
                    number = cursor.getString(numberIndex);
                    number = number.replace(" ", "");
                    contactList.put(name.toLowerCase().trim(),number.trim());
                }
            } finally {
                cursor.close();
            }
        }
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        SharedPreferences settings = getSharedPreferences("ps", 0);
        String silent = settings.getString("pk", null);
        if(silent!=null){
            gandu_PASSWORD=silent;
            ALLOW=1;
            findViewById(R.id.use).setVisibility(View.VISIBLE);
            findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setP();
                }
            });
        }else setP();
    }
    private void setP(){
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        findViewById(R.id.use).setVisibility(View.GONE);
        findViewById(R.id.textView).setVisibility(View.VISIBLE);
        findViewById(R.id.editTextTextPersonName).setVisibility(View.VISIBLE);
        findViewById(R.id.button).setVisibility(View.VISIBLE);
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) if (Character.isWhitespace(source.charAt(i))) return "";
                return null;
            }
        };
        ((EditText) findViewById(R.id.editTextTextPersonName)).setFilters(new InputFilter[]{filter});
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this).setTitle("Alert").setMessage("Set Password ?").setCancelable(false).setNegativeButton("Cancel  ", null).setPositiveButton("  Set Password", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gandu_PASSWORD = ((EditText) findViewById(R.id.editTextTextPersonName)).getText().toString().trim();
                        new AlertDialog.Builder(MainActivity.this).setTitle("Alert").setMessage("Password Has Been Set !!!").setCancelable(false).setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                findViewById(R.id.textView).setVisibility(View.GONE);
                                findViewById(R.id.editTextTextPersonName).setVisibility(View.GONE);
                                findViewById(R.id.button).setVisibility(View.GONE);
                                SharedPreferences settings = getSharedPreferences("ps", 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("pk", gandu_PASSWORD);
                                editor.apply();
                                findViewById(R.id.use).setVisibility(View.VISIBLE);
                                ALLOW=1;
                                findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        setP();
                                    }
                                });
                            }
                        }).show();
                    }
                }).show();
            }
        });
    }
}