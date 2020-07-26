package com.sourav.multiplesmssender;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.wafflecopter.multicontactpicker.ContactResult;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class MySMSservice extends IntentService {
    private static final String ACTION_SMS = "com.sourav.multiplesmssender.action.FOO";
    private static final String ACTION_WHATSAPP = "com.sourav.multiplesmssender.action.BAZ";

    private static final String MESSAGE = "com.sourav.multiplesmssender.extra.PARAM1";
    private static final String COUNT = "com.sourav.multiplesmssender.extra.PARAM2";
    private static final String MOBILE_NUMBER = "com.sourav.multiplesmssender.extra.PARAM3";
    private static final String IS_EACH_WORD = "com.sourav.multiplesmssender.extra.PARAM4";

    public MySMSservice() {
        super("MySMSservice");
    }


    public static void startActionSMS(Context context, String message, String count,
                                      List<ContactResult> mobile_numbers) {

        List<String> numbers = new ArrayList<String>();
        for (int i = 0; i < mobile_numbers.size(); i++) {
            numbers.add(mobile_numbers.get(i).getPhoneNumbers().get(0).getNumber());
        }

        String[] numberArray = numbers.toArray(new String[0]);

        Intent intent = new Intent(context, MySMSservice.class);
        intent.setAction(ACTION_SMS);
        intent.putExtra(MESSAGE, message);
        intent.putExtra(COUNT, count);
        intent.putExtra(MOBILE_NUMBER, numberArray);
        context.startService(intent);
    }


    public static void startActionWHATSAPP(Context context, String message, String count,
                                           List<ContactResult> mobile_numbers, Boolean isEachWord) {

        List<String> numbers = new ArrayList<String>();
        for (int i = 0; i < mobile_numbers.size(); i++) {
            numbers.add(mobile_numbers.get(i).getPhoneNumbers().get(0).getNumber());
        }

        String[] numberArray = numbers.toArray(new String[0]);

        Intent intent = new Intent(context, MySMSservice.class);
        intent.setAction(ACTION_WHATSAPP);
        intent.putExtra(MESSAGE, message);
        intent.putExtra(COUNT, count);
        intent.putExtra(MOBILE_NUMBER, numberArray);
        intent.putExtra(IS_EACH_WORD, isEachWord);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SMS.equals(action)) {
                final String message = intent.getStringExtra(MESSAGE);
                final String count = intent.getStringExtra(COUNT);
                final String[] mobile_number = intent.getStringArrayExtra(MOBILE_NUMBER);
                handleActionSMS(message, count, mobile_number);
            } else if (ACTION_WHATSAPP.equals(action)) {
                final String message = intent.getStringExtra(MESSAGE);
                final String count = intent.getStringExtra(COUNT);
                final String[] mobile_number = intent.getStringArrayExtra(MOBILE_NUMBER);
                final boolean isEachWord = intent.getBooleanExtra(IS_EACH_WORD, false);
                handleActionWHATSAPP(message, count, mobile_number, isEachWord);
            }
        }
    }


    private void handleActionSMS(String message, String count, String[] mobile_number) {
        try {
            if (mobile_number.length != 0) {
                for (int j = 0; j < mobile_number.length; j++) {
                    for (int i = 0; i < Integer.parseInt(count.toString()); i++) {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(mobile_number[j]
                                , null, message,
                                null, null);
                        sendBroadcastMessage("Result " + (i + 1) + ": Message sent to " +
                                 mobile_number[j] + " successfully");
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }


    private void handleActionWHATSAPP(String message, String count, String[] mobile_number, boolean isEachWord) {

        if (isEachWord) {
            try {
                PackageManager packageManager = getApplicationContext().getPackageManager();
                if (mobile_number.length != 0) {
                    for (int j = 0; j < mobile_number.length; j++) {
                        for (int i = 0; i < Integer.parseInt(count.toString()); i++) {
                            String[] words = message.split(" ");
                            String number = mobile_number[j];
                            for (int k = 0; k < words.length; k++) {
                                String url = "https://api.whatsapp.com/send?phone=" + number + "&text=" +
//                                String url = "https://api.whatsapp.com?phone=" + number + "&text=" +
                                        URLEncoder.encode(words[k], "UTF-8");
                                Intent whatsappIntent = new Intent(Intent.ACTION_VIEW);
                                whatsappIntent.setPackage("com.whatsapp");
                                whatsappIntent.setData(Uri.parse(url));
                                whatsappIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                if (whatsappIntent.resolveActivity(packageManager) != null) {
                                    getApplicationContext().startActivity(whatsappIntent);
                                    Thread.sleep(10000);
                                    sendBroadcastMessage("Result: Message sent to " + number + " successfully");
                                } else {
                                    sendBroadcastMessage("Result: Whatsapp Not Installed");
                                }

                            }
                        }
                    }
                }
            } catch (Exception e) {
                sendBroadcastMessage("Result: " + e.toString());
            }
        } else {
            try {
                PackageManager packageManager = getApplicationContext().getPackageManager();
                if (mobile_number.length != 0) {
                    for (int j = 0; j < mobile_number.length; j++) {
                        for (int i = 0; i < Integer.parseInt(count.toString()); i++) {
                            String number = mobile_number[j];
                            String url = "https://api.whatsapp.com/send?phone=" + number + "&text=" +
//                            String url = "https://api.whatsapp.com?phone=" + number + "&text=" +
                                    URLEncoder.encode(message, "UTF-8");
                            Intent whatsappIntent = new Intent(Intent.ACTION_VIEW);
                            whatsappIntent.setPackage("com.whatsapp");
                            whatsappIntent.setData(Uri.parse(url));
                            whatsappIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (whatsappIntent.resolveActivity(packageManager) != null) {
                                getApplicationContext().startActivity(whatsappIntent);
                                Thread.sleep(10000);
                                sendBroadcastMessage("Result: Message sent to " + number + " successfully");
                            } else {
                                sendBroadcastMessage("Result: Whatsapp Not Installed");
                            }

                        }
                    }
                }
            } catch (Exception e) {
                sendBroadcastMessage("Result: " + e.toString());
            }
        }
    }

    private void sendBroadcastMessage(String message) {
        Intent localIntent = new Intent("my.own.broadcast");
        localIntent.putExtra("result", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
}
