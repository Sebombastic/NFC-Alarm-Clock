package com.nfcalarmclock;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.util.Calendar;
import java.util.List;

/**
 */
public class NacUpcomingAlarmNotification
	extends NacNotification
{

	/**
	 * Channel.
	 */
	protected static final String UPCOMING_CHANNEL = "NacNotiChannelUpcoming";

	/**
	 * Group.
	 */
	protected static final String UPCOMING_GROUP = "NacNotiGroupUpcoming";

	/**
	 * Title of this type of notification.
	 */
	protected static final String UPCOMING_TITLE = "Upcoming Alarms";

	/**
	 * Notification ID.
	 */
	protected static final int UPCOMING_ID = 111;

	/**
	 * List of lines to show in the notification.
	 */
	protected List<CharSequence> mLines;

	/**
	 */
	public NacUpcomingAlarmNotification()
	{
		super();
	}
	
	/**
	 */
	public NacUpcomingAlarmNotification(Context context)
	{
		super(context);

		this.mLines = null;
	}

	/**
	 */
	@Override
	public void createChannel()
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
		{
			return;
		}

		Context context = this.getContext();
		Resources res = context.getResources();
		CharSequence name = res.getString(R.string.noti_channel_upcoming_name);
		String description = res.getString(R.string.noti_channel_upcoming_description);
		NotificationChannel channel = new NotificationChannel(UPCOMING_CHANNEL,
			name, NotificationManager.IMPORTANCE_LOW);
		NotificationManager manager = context.getSystemService(
			NotificationManager.class);

		channel.setDescription(description);
		channel.setShowBadge(false);
		channel.enableLights(false);
		channel.enableVibration(false);
		channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
		manager.createNotificationChannel(channel);
	}

	/**
	 * @return The notification group.
	 */
	public String getGroup()
	{
		return UPCOMING_GROUP;
	}

	/**
	 * @return The list of lines in the notification.
	 */
	protected List<CharSequence> getLines()
	{
		return this.mLines;
	}

	/**
	 * @return The notification text.
	 */
	public String getText(NacAlarm alarm)
	{
		Context context = this.getContext();
		String time = alarm.getFullTime(context);
		String name = alarm.getName();

		return (name.isEmpty()) ? time : time+"  —  "+name;
	}

	/**
	 * @return The notification title.
	 */
	public String getTitle()
	{
		List<CharSequence> lines = this.getLines();
		String title = UPCOMING_TITLE;

		return ((lines == null) || (lines.size() == 1)) ?
			title.substring(0, title.length()-1) : title;
	}

	/**
	 * @see hide
	 */
	public void hide()
	{
		Context context = this.getContext();
		NotificationManagerCompat manager = this.getNotificationManager(context);

		manager.cancel(UPCOMING_ID);
	}

	/**
	 * Update the notification.
	 */
	@TargetApi(Build.VERSION_CODES.M)
	public void update(List<NacAlarm> alarms)
	{
		Context context = this.getContext();
		NotificationManager manager = (NotificationManager)
			context.getSystemService(Context.NOTIFICATION_SERVICE);

		if ((alarms == null) || (alarms.size() == 0))
		{
			manager.cancel(UPCOMING_ID);
			return;
		}

		this.mLines = this.getNotificationText(alarms);
		List<CharSequence> lines = this.mLines;
		NotificationCompat.InboxStyle inbox = new NotificationCompat
			.InboxStyle();
		NotificationCompat.Builder builder = this.build(UPCOMING_CHANNEL)
			.setAutoCancel(false)
			.setShowWhen(false);
		int size = lines.size();

		for (CharSequence l : lines)
		{
			inbox.addLine(l);
		}


		if (size == 0)
		{
			manager.cancel(UPCOMING_ID);
			return;
		}
		else if (size > 0)
		{
			builder.setContentText(String.valueOf(lines.size())+" alarms");
		}

		builder.setStyle(inbox);
		manager.notify(UPCOMING_ID, builder.build());
	}

}
