package com.glimmer.intenttest;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import java.util.Calendar;
import java.util.List;
import org.apache.http.protocol.HTTP;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final int PICK_CONTACT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_call = (Button) findViewById(R.id.btn_call);
        Button btn_map = (Button) findViewById(R.id.btn_map);
        Button btn_web = (Button) findViewById(R.id.btn_web);
        Button btn_send_email = (Button) findViewById(R.id.btn_send_email);
        Button btn_create_calendar = (Button) findViewById(R.id.btn_create_calendar);
        Button btn_pick_contact = (Button) findViewById(R.id.btn_pick_contact);
        ImageButton ibtn_picture = (ImageButton) findViewById(R.id.ibtn_picture);

        btn_call.setOnClickListener(this);
        btn_map.setOnClickListener(this);
        btn_web.setOnClickListener(this);
        btn_send_email.setOnClickListener(this);
        /**
         * 添加日期备注
         */
        btn_create_calendar.setOnClickListener(this);
        btn_pick_contact.setOnClickListener(this);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (action.equals(Intent.ACTION_SEND)) {
            if (type.startsWith("image/")) {
                //handle sent text
                Uri data = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (data != null) {
                    if (intent.getType().contains("image/")) {
                        Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_SHORT).show();
                        ibtn_picture.setImageURI(data);
                    }
                }
            } else if (type.startsWith("text/")) {
                //handle sent image
                String text = intent.getParcelableExtra(Intent.EXTRA_TEXT);
                if (text != null) {
                    Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();

                }
            }
        } else if (action.equals(Intent.ACTION_MAIN)) {
            Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_call:
                call();
                break;
            case R.id.btn_map:
                map("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California");
                break;
            case R.id.btn_web:
                web("http://www.android.com");
                break;
            case R.id.btn_create_calendar:
                calendar();
                break;
            case R.id.btn_send_email:
                sendEmail();
                break;
            case R.id.btn_pick_contact:
                pickContact();
                break;
            default:
                break;

        }
    }

    /**
     * 从content provider里获取数据 如何从被选的联系人中读出电话号码
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {

                Uri contactUri = data.getData();
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(column);
                    Toast.makeText(MainActivity.this, number, Toast.LENGTH_SHORT).show();
                    cursor.close();
                }
            }
        }
    }

    private void calendar() {
        Intent calendarIntent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI);
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2012, 0, 19, 7, 30);      //2012.1.19 7:30-10:30
        Calendar endTime = Calendar.getInstance();
        endTime.set(2012, 0, 19, 10, 30);
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());
        calendarIntent.putExtra(CalendarContract.Events.TITLE, "Glimmer class");
        calendarIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "Secret dojo");
        startActivity(calendarIntent);
    }

    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType(HTTP.PLAIN_TEXT_TYPE);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jon@example.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Email subject");
        //发送的消息
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message text");
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://path/to/email/attachment"));
        String title = (String) getResources().getText(R.string.app_name);
        //指定显示的标题
        Intent chooser = Intent.createChooser(emailIntent, title);
        startActivity(chooser);
    }

    private void web(String uriString) {
        Uri webpage = Uri.parse(uriString);
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
        startActivity(webIntent);
    }

    private void map(String uriString) {
        Uri location = Uri.parse(uriString);
        Intent locationIntent = new Intent(Intent.ACTION_VIEW, location);
        startActivity(locationIntent);
    }

    private void call() {
        Uri number = Uri.parse("tel:15019494700");
        Intent intent = new Intent(Intent.ACTION_DIAL, number);
        //判断是否有app去接受这intent防止app crash
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isIntentSafe = activities.size() > 0;
        if (isIntentSafe) {
            startActivity(intent);
        }
    }

    private void pickContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

}
