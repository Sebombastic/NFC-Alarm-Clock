package com.nfcalarmclock.util

import android.content.Context
import android.content.res.Resources
import android.text.format.DateFormat
import com.nfcalarmclock.R
import com.nfcalarmclock.alarm.db.NacAlarm
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.EnumSet
import java.util.Locale
import java.util.TimeZone

/**
 * A list of possible days the alarm can run on.
 */
object NacCalendar
{

	/**
	 * Convert the alarm on the given day to a Calendar.
	 *
	 * @param  alarm  The alarm.
	 * @param  day  The day to convert.
	 *
	 * @return A Calendar.
	 */
	private fun alarmDayToCalendar(alarm: NacAlarm, day: Day): Calendar
	{
		// Get the current calendar instance
		val now = Calendar.getInstance()

		// Build the alarm calendar instance
		val alarmCalendar = alarmToCalendar(alarm)
		alarmCalendar[Calendar.DAY_OF_WEEK] = Day.dayToCalendarDay(day)

		// Check if the next calendar has already passed
		if (alarmCalendar.before(now))
		{
			// Alarm will occur in one week
			alarmCalendar.add(Calendar.DAY_OF_MONTH, 7)
		}

		// Return the alarm calendar
		return alarmCalendar
	}

	/**
	 * Get today's day, with the alarm hour and minute, if supplied.
	 *
	 * @return Today's day, with the alarm hour and minute, if supplied.
	 */
	fun alarmToCalendar(alarm: NacAlarm): Calendar
	{
		// Get the current calendar instance
		val today = Calendar.getInstance()

		// Set the calendar instance with attributes from the alarm
		today[Calendar.HOUR_OF_DAY] = alarm.hour
		today[Calendar.MINUTE] = alarm.minute
		today[Calendar.SECOND] = 0
		today[Calendar.MILLISECOND] = 0

		return today
	}

	/**
	 * Convert all the days an alarm is scheduled to go off, to Calendars.
	 *
	 * @param  alarm  The alarm.
	 *
	 * @return A list of Calendars.
	 */
	private fun alarmToCalendars(alarm: NacAlarm): List<Calendar>
	{
		val calendars: MutableList<Calendar> = ArrayList()

		// Days are selected
		if (alarm.areDaysSelected)
		{
			val timeOfDismissEarlyAlarm = alarm.timeOfDismissEarlyAlarm

			// Iterate over each selected day
			for (d in alarm.days)
			{
				// Get calendar and time of calendar
				val c = alarmDayToCalendar(alarm, d)
				val t = c.timeInMillis

				// Time matches the alarm that was dismissed early. Add a week to
				// this calendar
				if (timeOfDismissEarlyAlarm > 0 && t == timeOfDismissEarlyAlarm)
				{
					c.add(Calendar.DAY_OF_MONTH, 7)
				}

				// Add calendar to list of calendars
				calendars.add(c)
			}
		}
		// No days are selected. This alarm will occur either today or tomorrow and
		// is a one-time alarm
		else
		{
			val c = alarmToNextOneTimeCalendar(alarm)
			calendars.add(c)
		}

		return calendars
	}

	/**
	 * Convert the one time alarm to the next calendar.
	 */
	fun alarmToNextOneTimeCalendar(alarm: NacAlarm): Calendar
	{
		// Get the current calendar instance
		val now = Calendar.getInstance()

		// Build the alarm calendar instance
		val alarmCalendar = alarmToCalendar(alarm)

		// Check if the alarm calendar
		if (alarmCalendar.before(now))
		{
			alarmCalendar.add(Calendar.DAY_OF_MONTH, 1)
		}

		// Return the alarm calendar
		return alarmCalendar
	}

	/**
	 * Convert the calendar to a string in the given format.
	 */
	private fun calendarToString(calendar: Calendar, format: String?): String
	{
		// Create the date format
		val locale = Locale.getDefault()
		val formatter = SimpleDateFormat(format, locale)

		// Format the calendar time
		return formatter.format(calendar.time)
	}

	/**
	 * Format hours.
	 */
	fun formatHour(hour: Int, is24HourFormat: Boolean): Int
	{
		return if (is24HourFormat)
		{
			// Use 24 hour format
			hour
		}
		else
		{
			// Convert to 12 hour format
			to12HourFormat(hour)
		}
	}

	/**
	 * Get the time.
	 *
	 * @param  context  The application context.
	 * @param  hour  The hour.
	 * @param  minute  The minutes.
	 *
	 * @return The time.
	 */
	fun getClockTime(context: Context, hour: Int, minute: Int): String
	{
		// Check if using 24 hour format
		val format = DateFormat.is24HourFormat(context)

		// Get the time
		return getClockTime(hour, minute, format)
	}

	/**
	 * Get the time.
	 *
	 * @param  hour    The hour.
	 * @param  minute  The minutes.
	 * @param  is24HourFormat  The 24 hour format, to determine how to interpret the hour.
	 *
	 * @return The time.
	 */
	private fun getClockTime(hour: Int, minute: Int, is24HourFormat: Boolean): String
	{
		// Format the hour
		val clockHour = formatHour(hour, is24HourFormat)

		// Zero pad the minutes and make it 2 digits long
		val clockMinute = minute.toString().padStart(2, '0')

		// Format the time
		return "$clockHour:$clockMinute"
	}

	/**
	 * Get the current date.
	 */
	fun getDate(calendar: Calendar): String
	{
		// Get the date
		val locale = Locale.getDefault()
		val skeleton = DateFormat.getBestDateTimePattern(locale, "E MMM d")
		val dateFormat = SimpleDateFormat(skeleton, locale)

		// Format the date
		dateFormat.timeZone = TimeZone.getDefault()
		dateFormat.applyLocalizedPattern(skeleton)

		return dateFormat.format(calendar.time)
	}

	/**
	 * Get the Calendar day on which the first upcoming reminder for the
	 * alarm will run.
	 *
	 * @param  alarm  The alarm.
	 *
	 * @return The Calendar day on which the first upcoming reminder for the
	 *         alarm will run.
	 */
	fun getFirstAlarmUpcomingReminder(alarm: NacAlarm, cal: Calendar): Calendar
	{
		// Get the current calendar and a copy of the calendar passed in
		val nowCal = Calendar.getInstance()
		val calCopy = cal.clone() as Calendar

		// Compute the number of minutes to subtract to show the upcoming
		// reminder at the correct time
		val minutes = -1 * alarm.timeToShowReminder

		// Subtract the number of minutes from when the alarm will run
		calCopy.add(Calendar.MINUTE, minutes)

		// Check if the calendar corresponds to a time after right now. It needs to be
		// after right now so that the reminder is shown
		if (calCopy.after(nowCal))
		{
			return calCopy
		}
		// Reminder will not be shown because the time has already passed, so need to
		// change the calendar that will be returned
		else
		{
			// Round up to the nearest minute and then add a minute
			nowCal.add(Calendar.MINUTE, 1)
			nowCal.add(Calendar.SECOND, 30)
			nowCal.set(Calendar.SECOND, 0)

			return nowCal
		}
	}

	/**
	 * The full time string, EEE, HH:MM AM/PM.
	 *
	 * @return The full time string, EEE, HH:MM AM/PM.
	 */
	fun getFullTime(calendar: Calendar, is24HourFormat: Boolean): String
	{
		val hour = calendar[Calendar.HOUR_OF_DAY]
		val convertedHour = to12HourFormat(hour)

		// Get the time format
		var format = if (is24HourFormat)
		{
			"EEE HH:mm"
		}
		else
		{
			"EEE hh:mm a"
		}

		// Add some formatting thing for the hours that are less than 10
		if (convertedHour < 10)
		{
			format = format.replaceFirst("h".toRegex(), " ")
		}

		// Convert to a string
		return calendarToString(calendar, format)
	}

	/**
	 * Get the time meridian.
	 *
	 * @param  hour  The hour.
	 *
	 * @return The time meridian.
	 */
	fun getMeridian(context: Context, hour: Int): String
	{
		// Check if time is in 24 hour format
		return if (DateFormat.is24HourFormat(context))
		{
			""
		}
		else
		{
			// Check that the hour is in the morning
			if (hour < 12)
			{
				context.getString(R.string.am)
			}
			// Check that the hour is in the evening
			else
			{
				context.getString(R.string.pm)
			}
		}
	}

	/**
	 * Get the alarm that will run next.
	 *
	 * @param  alarms  List of alarms to check.
	 *
	 * @return The alarm that will run next.
	 */
	fun getNextAlarm(alarms: List<NacAlarm>): NacAlarm?
	{
		var nextCalendar: Calendar? = null
		var nextAlarm: NacAlarm? = null

		// Iterate over each alarm
		for (a in alarms)
		{
			// Alarm is disabled
			if (!a.isEnabled)
			{
				continue
			}
			// Next alarm will be skipped, it only has one day set, and repeat is disabled
			else if (a.isNextSkippedAndFinal)
			{
				continue
			}

			// Get the calendar instance for the next time the alarm will run
			val calendar = getNextAlarmDay(a)!!

			// Check if this is either the first calendar being checked,
			// or if this calendar corresponds to an earlier time than the
			// "nextCalendar"
			if ((nextCalendar == null) || (calendar.before(nextCalendar)))
			{
				nextCalendar = calendar
				nextAlarm = a
			}
		}

		return nextAlarm
	}

	/**
	 * Get the Calendar day on which the given alarm will run next.
	 *
	 * @param  alarm  The alarm who's days to check.
	 * @param  ignoreSkip  Whether the "shouldSkipNextAlarm" flag should be
	 *                     ignored or not.
	 *
	 * @return The Calendar day on which the given alarm will run next.
	 */
	fun getNextAlarmDay(alarm: NacAlarm, ignoreSkip: Boolean = false): Calendar?
	{
		// Convert the alarm to a list of calendar instances
		val calendars = alarmToCalendars(alarm)

		// Get the calendar day that is the soonest
		val nextDay = getNextDay(calendars)!!

		// Check if the next alarm should be skipped
		return if (alarm.shouldSkipNextAlarm && !ignoreSkip)
		{
			// Check if there was only one calendar in the list, but the alarm is scheduled to be repeated
			if ((calendars.size == 1) && alarm.repeat)
			{
				// Add a week to the calendar
				nextDay.add(Calendar.DAY_OF_MONTH, 7)
				nextDay
			}
			else
			{
				// Get another calendar day ignoring the one that was previously
				// determined
				getNextDay(calendars, skipDay = nextDay)
			}
		}
		else
		{
			// Return the next day
			nextDay
		}
	}

	/**
	 * Get the Calendary day that represents the next upcoming day.
	 *
	 * @return The Calendary day that represents the next upcoming day.
	 */
	private fun getNextDay(calendars: List<Calendar>, skipDay: Calendar? = null): Calendar?
	{
		var next: Calendar? = null

		// Iterate over each calendar instance
		for (c in calendars)
		{
			// Check if either the "next" calendar has not been set yet,
			// or occurs after the current calendar item.
			//
			// If "skipDay" is set, it will make sure to ignore that day since,
			// as the name suggests, it should be skipped
			next = if (((next == null) || next.after(c)) && (c != skipDay))
			{
				c
			}
			else
			{
				next
			}
		}

		// Get the calendar
		return next
	}

	/**
	 * Get the Calendar day on which the next upcoming reminder for the
	 * alarm will run.
	 *
	 * @param  alarm  The alarm.
	 *
	 * @return The Calendar day on which the next upcoming reminder for the
	 *         alarm will run.
	 */
	fun getNextAlarmUpcomingReminder(alarm: NacAlarm): Calendar
	{
		// Get right now
		val cal = Calendar.getInstance()

		// Add the reminder frequency and clear all fields smaller than MINUTE
		cal.add(Calendar.MINUTE, alarm.reminderFrequency)
		cal.set(Calendar.SECOND, 0)
		cal.set(Calendar.MILLISECOND, 0)

		return cal
	}

	/**
	 * Get the current timestamp in a desired format.
	 */
	fun getTimestamp(format: String): String
	{
		// Get the current time
		val locale = Locale.getDefault()
		val now = Calendar.getInstance().time

		// Create a date formatter
		val dateTimeFormat = SimpleDateFormat(format, locale)

		// Format the time
		return dateTimeFormat.format(now)
	}

	/**
	 * Convert an hour to 12 hour format.
	 */
	fun to12HourFormat(hour: Int): Int
	{
		// Check if hour is past noon
		return if (hour > 12)
		{
			hour % 12
		}
		else
		{
			// Check if hour is zero
			if (hour == 0)
			{
				12
			}
			// Return the hour
			else
			{
				hour
			}
		}
	}

	/**
	 * Day of week.
	 */
	enum class Day(

		/**
		 * The value associated with an enum.
		 */
		val value: Int

	)
	{

		/**
		 * Enums.
		 */
		SUNDAY(1), MONDAY(2), TUESDAY(4), WEDNESDAY(8), THURSDAY(16), FRIDAY(32), SATURDAY(64);

		companion object
		{

			/**
			 * A set of no days.
			 */
			val NONE: EnumSet<Day>
				get() = EnumSet.noneOf(Day::class.java)

			/**
			 * The Day today.
			 */
			val TODAY: Day
				get()
				{
					// Get the day of week from the calendar instance
					val today = Calendar.getInstance()
					val dow = today[Calendar.DAY_OF_WEEK]

					// Convert the day of week to a day
					return Day.calendarDayToDay(dow)
				}

			/**
			 * Every day of week.
			 */
			val WEEK: EnumSet<Day>
				get() = EnumSet.allOf(Day::class.java)

			/**
			 * All weekday days.
			 */
			val WEEKDAY: EnumSet<Day>
				get() = EnumSet.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)

			/**
			 * All weekend days.
			 */
			@Suppress("MemberVisibilityCanBePrivate")
			val WEEKEND: EnumSet<Day>
				get() = EnumSet.of(SUNDAY, SATURDAY)

			/**
			 * Length of week.
			 */
			@Suppress("MemberVisibilityCanBePrivate")
			val WEEK_LENGTH = WEEK.size

			/**
			 * Convert an alarm to a string of days.
			 *
			 * If no days are specified and the alarm is enable.
			 */
			fun alarmToDayString(
				context: Context,
				alarm: NacAlarm,
				start: Int
			): String
			{
				// Get the days
				val string = daysToString(context, alarm.days, start)

				// Check if unable to convert the days to a string or if there are no days selected
				if (string.isEmpty() || !alarm.areDaysSelected)
				{
					// Get the current day
					val now = Calendar.getInstance()[Calendar.DAY_OF_MONTH]

					// Get the next time the alarm will ring
					val next = alarmToNextOneTimeCalendar(alarm)[Calendar.DAY_OF_MONTH]

					// Check if the two days are the same, that means that the name
					// should be today. Otherwise, it should show tomorrow
					return if (now == next)
					{
						context.getString(R.string.dow_today)
					}
					else
					{
						context.getString(R.string.dow_tomorrow)
					}
				}

				//
				return string
			}

			/**
			 * Convert from a Calendar day to a Day.
			 *
			 * @param  day  A Calendar day.
			 *
			 * @return A Day.
			 */
			fun calendarDayToDay(day: Int): Day
			{
				return when (day)
				{
					Calendar.SUNDAY    -> SUNDAY
					Calendar.MONDAY    -> MONDAY
					Calendar.TUESDAY   -> TUESDAY
					Calendar.WEDNESDAY -> WEDNESDAY
					Calendar.THURSDAY  -> THURSDAY
					Calendar.FRIDAY    -> FRIDAY
					Calendar.SATURDAY  -> SATURDAY
					else               -> SUNDAY
				}
			}

			/**
			 * Convert from a Day to a Calendar day.
			 *
			 * @param  day  A Day.
			 *
			 * @return A Calendar day.
			 */
			fun dayToCalendarDay(day: Day): Int
			{
				return when (day)
				{
					SUNDAY    -> Calendar.SUNDAY
					MONDAY    -> Calendar.MONDAY
					TUESDAY   -> Calendar.TUESDAY
					WEDNESDAY -> Calendar.WEDNESDAY
					THURSDAY  -> Calendar.THURSDAY
					FRIDAY    -> Calendar.FRIDAY
					SATURDAY  -> Calendar.SATURDAY
				}
			}

			/**
			 * Convert a set of days to a comma separate string of days.
			 *
			 * @return A string of the days.
			 *
			 * @param  context  Context.
			 * @param  daysToConvert  The set of days to convert.
			 * @param  start  The day to start the week on.
			 */
			@Suppress("MemberVisibilityCanBePrivate")
			fun daysToString(
				context: Context,
				daysToConvert: EnumSet<Day>,
				start: Int
			): String
			{

				// Every day
				if (isEveryday(daysToConvert))
				{
					return context.getString(R.string.dow_everyday)
				}
				else if (isWeekday(daysToConvert))
				{
					return context.getString(R.string.dow_weekdays)
				}
				else if (isWeekend(daysToConvert))
				{
					return context.getString(R.string.dow_weekend)
				}

				// Get the abbreviated days of week
				val daysOfWeek = context.resources.getStringArray(R.array.days_of_week_abbr)

				// Iterate over each day in the week
				val days = listOf(*Day.values())
				val summary = StringBuilder(32)
				var count = 0
				var i = start

				while (count < WEEK_LENGTH)
				{

					// Check if the day is in the enum set
					if (daysToConvert.contains(days[i]))
					{
						// Add a dot in between each day
						if (summary.isNotEmpty())
						{
							summary.append(" \u2027 ")
						}

						// Append the day
						summary.append(daysOfWeek[i])
					}

					count++
					i = (i + 1) % WEEK_LENGTH
				}

				// Convert the string builder to a string
				return summary.toString()
			}

			/**
			 * Convert a set of days to a value.
			 *
			 * @param  days  A set of days.
			 *
			 * @return The computed value of all enum days added together.
			 */
			fun daysToValue(days: EnumSet<Day>): Int
			{
				// Initialize the value
				var value = 0

				// Iterate over each day in the set
				for (d in days)
				{
					value += d.value
				}

				return value
			}

			/**
			 * Check if every day is in the set.
			 *
			 * @return True if every day is in the set, and False otherwise.
			 */
			private fun isEveryday(days: EnumSet<Day>): Boolean
			{
				return days == WEEK
			}

			/**
			 * Check if all weekdays are in the set.
			 *
			 * @return True if all weekdays are in the set, and False otherwise.
			 */
			private fun isWeekday(days: EnumSet<Day>): Boolean
			{
				return days == WEEKDAY
			}

			/**
			 * Check if all weekend days are in the set.
			 *
			 * @return True if all weekend days are in the set, and False otherwise.
			 */
			private fun isWeekend(days: EnumSet<Day>): Boolean
			{
				return days == WEEKEND
			}

			/**
			 * @see .toString
			 */
			fun valueToDayString(context: Context, value: Int, start: Int): String
			{
				// Convert value of days to enum set
				val days = valueToDays(value)

				// Convert to a string
				return daysToString(context, days, start)
			}

			/**
			 * Convert a value to a set of days.
			 *
			 * @param  value  The value of a set of days. Each day has a unique, 2^n,
			 * value so only one bit is set. Doing a bitwise-and of a
			 * day should compute to 1 if you have the correct day.
			 *
			 * @return A set of enum days.
			 */
			fun valueToDays(value: Int): EnumSet<Day>
			{
				// Initialize the set
				val days = Day.NONE

				// Iterate over each day in the week
				for (d in WEEK)
				{
					// Check the value
					if (d.value and value != 0)
					{
						days.add(d)
					}
				}

				// Return the set
				return days
			}

		}

	}

	/**
	 * Format calendar messages.
	 */
	object Message
	{

		/**
		 * Maximum message length.
		 */
		private const val MAXIMUM_LENGTH = 32

		/**
		 * Get the message to display when the next alarm will occur.
		 *
		 * @return The message to display when the next alarm will occur.
		 */
		fun getNext(
			context: Context,
			alarm: NacAlarm?,
			nextAlarmFormat: Int
		): String
		{
			// No alarm provided
			return if (alarm == null)
			{
				context.resources.getString(R.string.message_no_alarms_scheduled)
			}
			else
			{
				// Get the next alarm day
				val calendar = getNextAlarmDay(alarm)!!

				// e.g. Alarm in 12 hour 5 min
				if (nextAlarmFormat == 0)
				{
					// Get the remaining time until the alarm runs
					val timeRemaining = getTimeRemaining(context.resources, calendar)

					// Return
					context.resources.getString(R.string.message_next_alarm_in, timeRemaining)
				}
				// e.g. Alarm on ...
				else
				{
					// Get the time at which the alarm will run
					val is24HourFormat = DateFormat.is24HourFormat(context)
					val time = getFullTime(calendar, is24HourFormat)

					// Return
					context.resources.getString(R.string.message_next_alarm_on, time)
				}
			}
		}

		/**
		 * Get the message to display when an alarm will run IN some amount of time.
		 *
		 * @return The message to display when an alarm will run IN some amount of
		 *         time.
		 */
		private fun getTimeRemaining(
			resources: Resources,
			calendar: Calendar
		): String
		{
			// Get the time remaining
			val time = (calendar.timeInMillis - System.currentTimeMillis()) / 1000

			// Get the time components
			val day = time / (60 * 60 * 24) % 365
			val hr = time / (60 * 60) % 24
			val min = time / 60 % 60
			val sec = time % 60

			// Get the units of time
			val dayunit = resources.getQuantityString(R.plurals.unit_day, day.toInt())
			val hrunit = resources.getQuantityString(R.plurals.unit_hour, hr.toInt())
			val minunit = resources.getQuantityString(R.plurals.unit_minute, min.toInt())
			val secunit = resources.getQuantityString(R.plurals.unit_second, sec.toInt())

			// Format the time remaining message
			return if (day > 0)
			{
				// Days
				"$day $dayunit $hr $hrunit"
			}
			else
			{
				if (hr > 0)
				{
					// Hours
					"$hr $hrunit $min $minunit"
				}
				else
				{
					// Check if minutes is 0
					if (min == 0L)
					{
						// Only show seconds since there are no minutes
						"$sec $secunit"
					}
					else
					{
						// Minutes and seconds
						"$min $minunit $sec $secunit"
					}
				}
			}
		}

		/**
		 * Get the message to display when the alarm will run.
		 *
		 * @return The message to display when the alarm will run.
		 */
		fun getWillRun(
			context: Context,
			alarm: NacAlarm,
			nextAlarmFormat: Int
		): String
		{
			// Get the alarm name
			val name = alarm.getNameNormalizedForMessage(MAXIMUM_LENGTH)

			// Alarm is not enabled
			return if (!alarm.isEnabled)
			{
				// Alarm has a name
				if (name.isNotEmpty())
				{
					context.resources.getString(R.string.message_disabled_named_alarm, name)
				}
				// Alarm does not have a name
				else
				{
					context.resources.getString(R.string.message_disabled_general_alarm)
				}
			}
			else
			{
				// Get the next alarm day
				val calendar = getNextAlarmDay(alarm)

				// No alarm scheduled, possibly because the next alarm is skipped
				if (calendar == null)
				{
					// Alarm has a name
					if (name.isNotEmpty())
					{
						context.resources.getString(R.string.message_skip_named_alarm, name)
					}
					// Alarm does not have a name
					else
					{
						context.resources.getString(R.string.message_skip_general_alarm)
					}
				}
				// e.g. Alarm in 12 hour 5 min
				else if (nextAlarmFormat == 0)
				{
					// Get the remaining time until the alarm runs
					val timeRemaining = getTimeRemaining(context.resources, calendar)

					// Alarm has a name
					if (name.isNotEmpty())
					{
						context.resources.getString(R.string.message_will_run_in_named_alarm, name, timeRemaining)
					}
					// Alarm does not have a name
					else
					{
						context.resources.getString(R.string.message_will_run_in_general_alarm, "", timeRemaining)
					}
				}
				// e.g. Alarm on ...
				else
				{
					// Get the time at which the alarm will run
					val is24HourFormat = DateFormat.is24HourFormat(context)
					val time = getFullTime(calendar, is24HourFormat)

					// Alarm has a name
					if (name.isNotEmpty())
					{
						context.resources.getString(R.string.message_will_run_on_named_alarm, name, time)
					}
					// Alarm does not have a name
					else
					{
						context.resources.getString(R.string.message_will_run_on_general_alarm, "", time)
					}
				}
			}
		}

	}

}
