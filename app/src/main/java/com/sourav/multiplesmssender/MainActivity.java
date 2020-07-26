package com.sourav.multiplesmssender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int CONTACT_PICKER_REQUEST = 202;
    private EditText txt_message;
    private EditText txt_number;
    private EditText txt_count;
    private Button btn_manual;
    private Button btn_sms;
    private Button btn_whatsapp;
    private Button btn_choose;
    List<ContactResult> results = new ArrayList<>();
    private Button btn_whatsapp_each_word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_message = findViewById(R.id.txt_message);
        txt_number = findViewById(R.id.txt_mobile_numbers);
        txt_count = findViewById(R.id.txt_count);

        btn_manual = findViewById(R.id.btn_manual);
        btn_sms = findViewById(R.id.btn_sms);
        btn_whatsapp = findViewById(R.id.btn_whatsapp);
        btn_whatsapp_each_word = findViewById(R.id.btn_whatsapp2);
        btn_choose = findViewById(R.id.button_choose_contacts);

        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_CONTACTS
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}

            @Override
            public void onPermissionRationaleShouldBeShown(
                    List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();

        if (!isAccessibilityOn(getApplicationContext())) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

//This is for single permission
 /*       Dexter.withActivity(this)
                .withPermission(Manifest.permission.SEND_SMS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(
                            PermissionGrantedResponse permissionGrantedResponse) {
                    }

                    @Override
                    public void onPermissionDenied(
                            PermissionDeniedResponse permissionDeniedResponse) {
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(
                            PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    }
                }).check();
*/
        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MultiContactPicker.Builder(MainActivity.this) //Activity/fragment context
                        .hideScrollbar(false)
                        .showTrack(true)
                        .searchIconColor(Color.WHITE)
                        .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE)
                        .handleColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
                        .bubbleColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent))
                        .bubbleTextColor(Color.WHITE)
                        .setTitleText("Select Contacts")
                        .setLoadingType(MultiContactPicker.LOAD_ASYNC)
                        .limitToColumn(LimitColumn.NONE)
                        .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                                android.R.anim.fade_in,
                                android.R.anim.fade_out)
                        .showPickerForResult(CONTACT_PICKER_REQUEST);
            }
        });

        btn_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySMSservice.startActionSMS(getApplicationContext(), txt_message.getText().toString(),
                        txt_count.getText().toString(), results);
                /*try {
                    if (!results.isEmpty()) {
                        for (int j = 0; j < results.size(); j++) {
                            for (int i = 0; i < Integer.parseInt(txt_count.getText().toString()); i++) {
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(results.get(j).getPhoneNumbers().get(0).getNumber()
                                        , null, txt_message.getText().toString(),
                                        null, null);
                                Toast.makeText(MainActivity.this, "SMS Sent : count " + (i + 1),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this,
                            "SMS sending failed!", Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        btn_manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendSms = new Intent(Intent.ACTION_SEND);
                sendSms.setType("text/plain");
                sendSms.putExtra(Intent.EXTRA_TEXT, txt_message.getText().toString());
                startActivity(sendSms);
            }
        });

        btn_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySMSservice.startActionWHATSAPP(getApplicationContext(), txt_message.getText().toString(),
                        txt_count.getText().toString(), results, false);
            }
        });

        btn_whatsapp_each_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySMSservice.startActionWHATSAPP(getApplicationContext(), txt_message.getText().toString(),
                        txt_count.getText().toString(), results, true);
            }
        });

        IntentFilter intent = new IntentFilter("my.own.broadcast");
        LocalBroadcastManager.getInstance(this).registerReceiver(myLocalBroadcastReceiver, intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                results = MultiContactPicker.obtainResult(data);
                StringBuilder names = new StringBuilder(results.get(0).getDisplayName());
                for (int j = 0; j < results.size(); j++) {
                    if (j != 0)
                        names.append(", ").append(results.get(j).getDisplayName());
                }
                txt_number.setText(names);
                Log.d("MyTag", results.get(0).getDisplayName());
            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("User closed the picker without selecting items.");
            }
        }
    }

    private BroadcastReceiver myLocalBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra("result");
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        }
    };

    private boolean isAccessibilityOn(Context context) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName() + "/" + WhatsappAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException ignored) {
        }

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                colonSplitter.setString(settingValue);
                while (colonSplitter.hasNext()) {
                    String accessibilityService = colonSplitter.next();

                    Log.v("", "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v("", "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v("" , "***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }
}