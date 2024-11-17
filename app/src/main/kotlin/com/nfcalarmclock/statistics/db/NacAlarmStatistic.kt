package com.nfcalarmclock.statistics.db

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.nfcalarmclock.alarm.db.NacAlarm
import java.util.Date

/**
 * Statistics for an alarm.
 */
abstract class NacAlarmStatistic()
{

	/**
	 * Unique ID.
	 */
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	var id: Long = 0

	/**
	 * Timestamp of when an alarm was snoozed.
	 */
	@ColumnInfo(name = "timestamp")
	var timestamp: Date

	/**
	 * The ID of the alarm.
	 */
	@ColumnInfo(name = "alarm_id", index = true)
	var alarmId: Long? = null

	/**
	 * The hour the alarm ran at.
	 */
	@ColumnInfo(name = "hour")
	var hour = 0

	/**
	 * The minute the alarm ran at.
	 */
	@ColumnInfo(name = "minute")
	var minute = 0

	/**
	 * The name of the alarm.
	 */
	@ColumnInfo(name = "name")
	var name: String = ""

	/**
	 * Constructor.
	 */
	init
	{
		val timestamp = Date()
		this.timestamp = timestamp
	}

	/**
	 * Constructor.
	 */
	constructor(alarm: NacAlarm?) : this()
	{
		if (alarm != null)
		{
			alarmId = alarm.id
			hour = alarm.hour
			minute = alarm.minute
			name = alarm.name
		}
	}

	/**
	 * Check if two stats are equal, except for the ID.
	 */
	open fun equalsExceptId(stat: NacAlarmStatistic): Boolean
	{
		return (timestamp == stat.timestamp)
			&& (hour == stat.hour)
			&& (minute == stat.minute)
			&& (name == stat.name)
	}

	/**
	 * Convert the data to a csv format so that it can be used to write to an
	 * output file.
	 */
	open fun toCsvFormat(): String
	{
		// Zero pad the hour and minute so that the time looks more uniform
		val clockHour = hour.toString().padStart(2, '0')
		val clockMinute = minute.toString().padStart(2, '0')

		return "${timestamp},${name},${clockHour}:${clockMinute}"
	}

}
