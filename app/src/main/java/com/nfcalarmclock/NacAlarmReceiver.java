package com.nfcalarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Receive the signal from the AlarmManager that it's time for the alarm to go
 * off, which in turn start the NacAlarmActivity.
 */
public class NacAlarmReceiver
	extends BroadcastReceiver
{
 
	/**
	 */
	@Override
	public void onReceive(final Context context, Intent intent)
	{
		Bundle bundle = NacAlarmParcel.getExtra(intent);
		NacAlarm alarm = NacAlarmParcel.getAlarm(bundle);
		Intent newIntent = new Intent(context, NacAlarmActivity.class);
		//NacDatabase db = new NacDatabase(context);

		//if (!db.exists(alarm))
		//{
		//	new NacAlarmScheduler(context).cancel(alarm);
		//	return;
		//}

		newIntent.putExtra("bundle", bundle);
		newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(newIntent);
	}

}
