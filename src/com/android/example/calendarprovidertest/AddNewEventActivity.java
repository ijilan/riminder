package com.android.example.calendarprovidertest;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

public class AddNewEventActivity extends Activity implements OnClickListener,
		OnFocusChangeListener {

	public String eventName;
	public String eventDescription;
	public int[] eventBeginDate = new int[3];
	public int[] eventBeginTime = new int[2];
	public int[] eventEndDate = new int[3];
	public int[] eventEndTime = new int[2];
	public int reminderMinutes;

	private EditText eventNameText;
	private EditText eventDescriptionText;
	private EditText eventBeginDateText;
	private EditText eventBeginTimeText;
	private EditText eventEndDateText;
	private EditText eventEndTimeText;
	private EditText reminderminutesText;

	private Button okButton;
	private Button goBackButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_event);

		okButton = (Button) findViewById(R.id.ok);
		okButton.setOnClickListener(this);
		goBackButton = (Button) findViewById(R.id.goback);
		goBackButton.setOnClickListener(this);

		eventNameText = (EditText)findViewById(R.id.event_name);
		eventDescriptionText = (EditText)findViewById(R.id.event_description);
		
		eventBeginDateText = (EditText) findViewById(R.id.select_begin_date);
		eventBeginDateText.setFocusable(true);
		eventBeginDateText.setOnFocusChangeListener(this);

		eventBeginTimeText = (EditText) findViewById(R.id.select_begin_time);
		eventBeginTimeText.setFocusable(true);
		eventBeginTimeText.setOnFocusChangeListener(this);

		eventEndDateText = (EditText) findViewById(R.id.select_end_date);
		eventEndDateText.setFocusable(true);
		eventEndDateText.setOnFocusChangeListener(this);

		eventEndTimeText = (EditText) findViewById(R.id.select_end_time);
		eventEndTimeText.setFocusable(true);
		eventEndTimeText.setOnFocusChangeListener(this);

		reminderminutesText = (EditText) findViewById(R.id.reminder_minutes);
	}

	@Override
	public void onClick(View v) {
		if (v == okButton) {
			eventName = eventNameText.getText().toString();
			eventDescription = eventDescriptionText.getText().toString();
			reminderMinutes = Integer.parseInt(reminderminutesText.getText()
					.toString());
			// 可在此处添加简单的判断用户输入新event的各项参数的合法性的判断,我假设用户输入的一定是合法的
			addEvent(eventName, eventDescription, eventBeginDate, eventBeginTime, eventEndDate, eventEndTime, reminderMinutes); // 添加新event
		}
		else if (v == goBackButton) {
			goBack();
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (v == eventBeginDateText && hasFocus == true) {

			Calendar c = Calendar.getInstance();
			new DatePickerDialog(AddNewEventActivity.this,
					new DatePickerDialog.OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							eventBeginDateText.setText("开始日期：" + year + "-"
									+ (monthOfYear+1) + "-" + dayOfMonth);
							eventBeginDate[0] = year;
							eventBeginDate[1] = monthOfYear;
							eventBeginDate[2] = dayOfMonth;
						}
					}, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
					c.get(Calendar.DAY_OF_MONTH)).show();

		} else if (v == eventBeginTimeText && hasFocus == true) {

			Calendar c = Calendar.getInstance();
			new TimePickerDialog(AddNewEventActivity.this,
					new TimePickerDialog.OnTimeSetListener() {
						/*@Override*/
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							eventBeginTimeText.setText("开始时间：" + hourOfDay
									+ "时" + minute + "分");
							eventBeginTime[0] = hourOfDay;
							eventBeginTime[1] = minute;
						}
					}, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
					true).show();

		} else if (v == eventEndDateText && hasFocus == true) {

			Calendar c = Calendar.getInstance();
			new DatePickerDialog(AddNewEventActivity.this,
					new DatePickerDialog.OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							eventEndDateText.setText("结束日期：" + year + "-"
									+ (monthOfYear+1) + "-" + dayOfMonth);
							eventEndDate[0] = year;
							eventEndDate[1] = monthOfYear;
							eventEndDate[2] = dayOfMonth;
						}
					}, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
					c.get(Calendar.DAY_OF_MONTH)).show();

		} else if (v == eventEndTimeText && hasFocus == true) {

			Calendar c = Calendar.getInstance();
			new TimePickerDialog(AddNewEventActivity.this,
					new TimePickerDialog.OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							eventEndTimeText.setText("结束时间：" + hourOfDay + "时"
									+ minute + "分");
 							eventEndTime[0] = hourOfDay;
 							eventEndTime[1] = minute;
						}
					}, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
					true).show();

		}
	}

	private void addEvent(String eventName, String eventDescription,
			int eventBeginDate[], int eventBeginTime[], int eventEndDate[],
			int eventEndTime[], int reminderMinutus) {
		long calId = 1;
		long startMillis = 0;
		long endMillis = 0;
		Calendar beginTime = Calendar.getInstance();
		beginTime.set(eventBeginDate[0], eventBeginDate[1], eventBeginDate[2], eventBeginTime[0], eventBeginTime[1]); // 注意：月份系统会自动加1
		startMillis = beginTime.getTimeInMillis();
		Calendar endTime = Calendar.getInstance();
		endTime.set(eventEndDate[0], eventEndDate[1], eventEndDate[2], eventEndTime[0], eventEndTime[1]);
		endMillis = endTime.getTimeInMillis();

		ContentResolver cr = getContentResolver(); // 添加新event，步骤是固定的
		ContentValues values = new ContentValues();
		values.put(Events.DTSTART, startMillis);
		values.put(Events.DTEND, endMillis);
		values.put(Events.TITLE, eventName);
		values.put(Events.DESCRIPTION, eventDescription);
		values.put(Events.CALENDAR_ID, calId);
		values.put(Events.EVENT_TIMEZONE, "GMT+8");
		Uri uri = cr.insert(Events.CONTENT_URI, values);
		Long myEventsId = Long.parseLong(uri.getLastPathSegment()); // 获取刚才添加的event的Id

		ContentResolver cr1 = getContentResolver(); // 为刚才新添加的event添加reminder
		ContentValues values1 = new ContentValues();
		values1.put(Reminders.MINUTES, reminderMinutus);
		values1.put(Reminders.EVENT_ID, myEventsId);
		values1.put(Reminders.METHOD, Reminders.METHOD_ALERT);
		cr1.insert(Reminders.CONTENT_URI, values1); // 调用这个方法返回值是一个Uri

		setAlarmDeal(startMillis); // 设置reminder开始的时候，启动另一个activity

		showMessageDialog("插入成功！" + "\n" + uri.getLastPathSegment() + "\n"
				+ uri.getAuthority());
	}

	private void setAlarmDeal(long time) { // 设置全局定时器
		Intent intent = new Intent(this, AlarmActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
		AlarmManager aManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
		aManager.set(AlarmManager.RTC_WAKEUP, time, pi); // 当系统调用System.currentTimeMillis()方法返回值与time相同时启动pi对应的组件
	}
	
	public void showMessageDialog(String info) { // 弹出消息对话框，消息的内容是info，且点击此对话框的确定按钮后会返回
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(info);
		builder.setTitle("information");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				goBack(); 
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void goBack() { // 返回
		Intent itent = new Intent();
		itent.setClass(AddNewEventActivity.this,
				AndroidCalendarProviderTestActivity.class);
		startActivity(itent);
		AddNewEventActivity.this.finish();
	}
}