package com.bms.myhelper;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import java.util.Locale;

public class smsreciever extends BroadcastReceiver {
    private String n;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                String msg = smsMessage.getMessageBody();
                n = smsMessage.getOriginatingAddress();
                if (MainActivity.ALLOW==1 && msg.length() >= 8 && msg.substring(0, 8).trim().equals("myhelper")) {
                    if (msg.split("\n")[0].split(" ").length >= 2) {
                        if (msg.split("\n")[0].split(" ")[1].trim().equals(MainActivity.gandu_PASSWORD)) {
                            if (msg.split("\n").length >= 2) {
                                msg = msg.split("\n")[1];
                                if (msg.length() >= 7 && msg.substring(0, 7).trim().equals("contact")) {
                                    if (msg.split(" ").length >= 2) {
                                        String cn = "";
                                        for (int i = 1; i < msg.split(" ").length; i++)cn += " "+msg.split(" ")[i];
                                        msg = getContact(cn);
                                    }
                                    else
                                        msg = "Wrong Format !!!\n\nFormat Is Below:\nmyhelper 'your_password'\ncontact 'contact_name'";
                                } else if (msg.length() >= 8 && msg.substring(0, 8).trim().equals("location"))
                                    msg=getLoc(context);
                                else if (msg.length() >= 5 && msg.substring(0, 5).trim().equals("sound")) {
                                    if (msg.split(" ").length >= 2)
                                        msg = setSound(msg.split(" ")[1].trim(),context);
                                    else
                                        msg = "Wrong Format !!!\n\nFormat Is Below:\nmyhelper 'your_password'\nsound 'max/min'";
                                } else
                                    msg = "Specific what you want !!!\n\nFormat for getting contact Is Below:\nmyhelper 'your_password'\ncontact 'contact_name'\n\nFormat for getting location Is Below:\nmyhelper your_password\nlocation\n\nFormat for changing volume Is Below:\nmyhelper your_password\nsound 'max/min'";
                            } else
                                msg = "Specific what you want !!!\n\nFormat for getting contact Is Below:\nmyhelper 'your_password'\ncontact 'contact_name'\n\nFormat for getting location Is Below:\nmyhelper your_password\nlocation\n\nFormat for changing volume Is Below:\nmyhelper your_password\nsound 'max/min'";
                        } else msg = "Incorrect Password";
                    } else
                        msg = "Wrong Format Of LOGIN !!!\n\nLogin Format Is Below:\nmyhelper 'your_password'";
                    MainActivity.sendSms(n, msg);
                }
            }
        }
    }

    private String setSound(String s,Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (s.length()==3&&s.substring(0, 3).trim().equals("min")) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            return "Phone Volume Set To Minimum Successfully";
        }
        else if (s.length()==3&&s.substring(0, 3).trim().equals("max")) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
            audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, 0);
            return "Phone Volume Set To Maximum Successfully";
        }
        return "Wrong Format !!!\n\nFormat Is Below:\nmyhelper 'your_password'\nsound 'max/min'";
    }

    private String getLoc(Context context) {
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Location myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            return ("Phone Is Here : https://www.google.com/maps/place/"+myLocation.getLatitude()+"%2C"+myLocation.getLongitude());
        } catch (Exception e) {
            return "Couldn't Get Location";
        }
    }

    private String getContact(String s) {
        if (MainActivity.contactList.containsKey(s.toLowerCase().trim()))return (s.trim() + " : " + MainActivity.contactList.get(s.toLowerCase().trim()));
        return "No contact " + s.trim() + " found";
    }
}
