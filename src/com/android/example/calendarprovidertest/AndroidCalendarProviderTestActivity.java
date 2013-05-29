package com.android.example.calendarprovidertest;


import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AndroidCalendarProviderTestActivity extends Activity {
	private Button showCalendars;
	private Button addEvents;
	private Button queryEvents;
	private TextView displayEnvents;
	private EditText getEventIdEditText;
	private Button delEvent;

	public static final String[] EVENT_PROJECTION = new String[] {
			Calendars._ID, Calendars.ACCOUNT_NAME,
			Calendars.CALENDAR_DISPLAY_NAME };
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
	private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;

	long myEventsId = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		displayEnvents = (TextView) findViewById(R.id.displayevents);
		displayEnvents.setMovementMethod(ScrollingMovementMethod.getInstance());

		showCalendars = (Button) findViewById(R.id.querycalendars);
		showCalendars.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Cursor cur = null;
				ContentResolver cr = getContentResolver();
				Uri uri = Calendars.CONTENT_URI;
				// String selection = "((" + Calendars.ACCOUNT_NAME +  // 给出查询条件，查询特定用户的日历
				// " = ?) AND ("+ Calendars.ACCOUNT_TYPE + " = ?))";
				// String[] selectionArgs = new String[]
				// {"liushuaikobe@Gmail.com", "com.google"};
				cur = cr.query(uri, EVENT_PROJECTION, null, null, null); // 查询条件为null，查询所有用户的所有日历
				while (cur.moveToNext()) {
					long calID = 0;
					String displayName = null;
					String accountName = null;

					calID = cur.getLong(PROJECTION_ID_INDEX);
					displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
					accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
					
					showMessageDialog("日历ID：" + calID + "\n" + "日历显示名称：" + "\n"
							+ displayName + "\n" + "日历拥有者账户名称：" + "\n"
							+ accountName);
				}
			}

		});

		addEvents = (Button) findViewById(R.id.addevents);
		addEvents.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent itent=new Intent();
				itent.setClass(AndroidCalendarProviderTestActivity.this,AddNewEventActivity.class);
				startActivity(itent); // 启动添加新event的Activity
			}
		});

		queryEvents = (Button) findViewById(R.id.queryevents);
		queryEvents.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ContentResolver cr = getContentResolver();
				Cursor cur = cr.query(Events.CONTENT_URI, new String[] {
						Events._ID, Events.TITLE, Events.DESCRIPTION,
						Events.DTSTART, Events.DTEND },
				/* Events._ID + "=" + myEventsId */null, null, null); // 注释中的条件是是查询特定ID的events
				displayEnvents.setText("");
				while (cur.moveToNext()) {
					Long tempEventsId = cur.getLong(0);
					String tempEventsTitle = cur.getString(1);
					String tempEventsDecription = cur.getString(2);
					String tempEventsStartTime = cur.getString(3);
					String tempEventsEndTime = cur.getString(4);
					displayEnvents.append(tempEventsId + "\n");
					displayEnvents.append(tempEventsTitle + " "
							+ tempEventsDecription + "\n");
					displayEnvents.append(new SimpleDateFormat(
							"yyyy/MM/dd hh:mm").format(new Date(Long
							.parseLong(tempEventsStartTime)))
							+ "至");
					displayEnvents.append(new SimpleDateFormat(
							"yyyy/MM/dd hh:mm").format(new Date(Long
							.parseLong(tempEventsEndTime)))
							+ "\n");
				}
			}
		});

		getEventIdEditText = (EditText) findViewById(R.id.geteventid);
		delEvent = (Button) findViewById(R.id.delevent);
		delEvent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Long tempEventId = 0l;
				try {
					tempEventId = Long.parseLong(getEventIdEditText.getText()
							.toString());
				} catch (Exception e) {
					showMessageDialog("请先查询所有event，然后正确填写event的id~");
					return;
				}
				// 另一种删除event方式
				// Uri deleteUri =
				// ContentUris.withAppendedId(Events.CONTENT_URI, tempEventId);
				// int rows = getContentResolver().delete(deleteUri, null,
				// null);
				ContentResolver cr = getContentResolver();
				int rows = cr.delete(Events.CONTENT_URI, Events._ID + "= ?",
						new String[] { tempEventId + "" });

				showMessageDialog("删除了一个event：" + rows);
				Log.i("delete_event", "Rows deleted: " + rows);
			}
		});

	}

	public void showMessageDialog(String info) { // 弹出消息对话框，消息的内容是info 
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(info);
		builder.setTitle("information");
		builder.setPositiveButton("确定", null);
		AlertDialog alert = builder.create();
		alert.show();
	}
}