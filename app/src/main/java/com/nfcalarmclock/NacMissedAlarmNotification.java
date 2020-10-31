package com.nfcalarmclock;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 */
public class NacMissedAlarmNotification
	extends NacNotification
{

	/**
	 * Alarm.
	 */
	private NacAlarm mAlarm;

	/**
	 */
	public NacMissedAlarmNotification()
	{
		super();
		this.mAlarm = null;
	}

	/**
	 */
	public NacMissedAlarmNotification(Context context)
	{
		super(context);
		this.mAlarm = null;
	}

	/**
	 * @see NacNotification#builder()
	 */
	@Override
	protected NotificationCompat.Builder builder()
	{
		NotificationCompat.InboxStyle inbox = new NotificationCompat
			.InboxStyle();
		List<CharSequence> body = this.getBody();

		for (CharSequence line : body)
		{
			inbox.addLine(line);
		}

		return super.builder()
			.setGroupSummary(true)
			.setAutoCancel(true)
			.setShowWhen(true)
			.setStyle(inbox);
	}

	/**
	 * @see NacNotification#createChannel()
	 */
	@TargetApi(Build.VERSION_CODES.O)
	@Override
	protected NotificationChannel createChannel()
	{
		NotificationChannel channel = super.createChannel();
		if (channel == null)
		{
			return null;
		}

		channel.setShowBadge(true);
		channel.enableLights(true);
		channel.enableVibration(true);
		return channel;
	}

	/**
	 * @return Alarm.
	 */
	public NacAlarm getAlarm()
	{
		return this.mAlarm;
	}

	/**
	 * @see NacNotification#getBodyLine(NacAlarm)
	 */
	public String getBodyLine(NacAlarm alarm)
	{

		Context context = this.getContext();
		NacAlarm actualAlarm = NacDatabase.findAlarm(context, alarm);
		return super.getBodyLine(actualAlarm);
	}

	/**
	 * @see NacNotification#getChannelDescription()
	 */
	protected String getChannelDescription()
	{
		Context context = this.getContext();
		NacSharedConstants cons = new NacSharedConstants(context);
		return cons.getDescriptionMissedNotification();
	}

	/**
	 * @see NacNotification#getChannelName()
	 */
	protected String getChannelName()
	{
		Context context = this.getContext();
		NacSharedConstants cons = new NacSharedConstants(context);
		return cons.getMissedNotification();
	}

	/**
	 * @see NacNotification#getChannelId()
	 */
	protected String getChannelId()
	{
		return "NacNotiChannelMissed";
	}

	/**
	 * @see NacNotification#getContentPendingIntent()
	 */
	protected PendingIntent getContentPendingIntent()
	{
		Context context = this.getContext();
		Intent intent = new Intent(context, NacMainActivity.class);
		int flags = Intent.FLAG_ACTIVITY_NEW_TASK
			| Intent.FLAG_ACTIVITY_CLEAR_TASK;

		intent.addFlags(flags);
		return PendingIntent.getActivity(context, 0, intent, 0);
	}

	/**
	 * @see NacNotification#getContentText()
	 */
	protected String getContentText()
	{
		Context context = this.getContext();
		Locale locale = Locale.getDefault();
		NacSharedConstants cons = new NacSharedConstants(context);
		int size = this.getBody().size();
		String word = cons.getAlarm(size);

		return (size > 0) ? String.format(locale, "%1$d %2$s", size, word) : "";
	}

	/**
	 * @return A list of notification lines.
	 */
	@TargetApi(Build.VERSION_CODES.M)
	public static List<CharSequence> getExtraLines(Context context, String groupKey)
	{
		List<CharSequence> lines = new ArrayList<>();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
		{
			return lines;
		}

		NotificationManager manager = (NotificationManager)
			context.getSystemService(Context.NOTIFICATION_SERVICE);
		StatusBarNotification[] statusbar = manager
			.getActiveNotifications();

		for (StatusBarNotification sb : statusbar)
		{
			Notification notification = sb.getNotification();
			String sbGroup = notification.getGroup();

			if ((groupKey != null) && groupKey.equals(sbGroup))
			{
				CharSequence[] extraLines = (CharSequence[]) notification.extras
					.get(NotificationCompat.EXTRA_TEXT_LINES);

				lines.addAll(Arrays.asList(extraLines));
			}
		}

		return lines;
	}

	/**
	 * @see NacNotification#getGroup()
	 */
	protected String getGroup()
	{
		return "NacNotiGroupMissed";
	}

	/**
	 * @see NacNotification#getId()
	 */
	protected int getId()
	{
		return 222;
	}

	/**
	 * @see NacNotification#getImportance()
	 */
	protected int getImportance()
	{
		return NotificationManagerCompat.IMPORTANCE_DEFAULT;
	}

	/**
	 * @see NacNotification#getPriority()
	 */
	protected int getPriority()
	{
		return NotificationCompat.PRIORITY_DEFAULT;
	}

	/**
	 * @see NacNotification#getTitle()
	 */
	public String getTitle()
	{
		Context context = this.getContext();
		Locale locale = Locale.getDefault();
		NacSharedConstants cons = new NacSharedConstants(context);
		int count = this.getLineCount();

		return String.format(locale, "<b>%1$s</b>", cons.getMissedAlarm(count));
	}

	/**
	 * Set the alarm.
	 */
	public void setAlarm(NacAlarm alarm)
	{
		this.mAlarm = alarm;
	}

	/**
	 * @see NacNotification#setupBody()
	 */
	protected void setupBody()
	{
		Context context = this.getContext();
		String groupKey = this.getGroup();
		NacAlarm alarm = this.getAlarm();
		String line = this.getBodyLine(alarm);
		List<CharSequence> body = this.getExtraLines(context,
			groupKey);

		body.add(line);

		this.mBody = body;
	}

	/**
	 * @see NacNotification#show()
	 */
	public void show()
	{
		NacAlarm alarm = this.getAlarm();
		if (alarm == null)
		{
			return;
		}

		// Used to call cancel() if size was 0. Might not have to because
		// show() should cancel it anyway
		this.setupBody();
		super.show();
	}

}
