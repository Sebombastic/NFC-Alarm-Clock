package com.nfcalarmclock.util

import android.content.Context
import android.content.res.Resources
import android.text.format.DateFormat
import com.nfcalarmclock.R
import com.nfcalarmclock.alarm.db.NacAlarm
import com.nfcalarmclock.shared.NacSharedPreferences
import com.nfcalarmclock.util.NacUtility.capitalize
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.EnumSet
import java.util.Locale

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
	 * TODO: Maybe rename this to something better like toCalendar() or something
	 *
	 * @return Today's day, with the alarm hour and minute, if supplied.
	 */
	private fun alarmToCalendar(alarm: NacAlarm): Calendar
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
	fun calendarToString(calendar: Calendar, format: String?): String
	{
		// Create the date format
		val locale = Locale.getDefault()
		val formatter = SimpleDateFormat(format, locale)

		// Format the calendar time
		return formatter.format(calendar.time)
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
	 * @param  format  The 24 hour format, to determine how to interpret the hour.
	 *
	 * @return The time.
	 */
	fun getClockTime(hour: Int, minute: Int, format: Boolean): String
	{
		val locale = Locale.getDefault()
		var hour = hour

		// Check if not using 24 hour format
		if (!format)
		{
			// Convert the 12 hour format
			hour = to12HourFormat(hour)
		}

		// Format the time
		return String.format(locale, "%1\$d:%2$02d", hour, minute)
	}

	/**
	 * The full time string, EEE, HH:MM AM/PM.
	 *
	 * @return The full time string, EEE, HH:MM AM/PM.
	 */
	fun getFullTime(context: Context, calendar: Calendar): String
	{
		val hour = calendar[Calendar.HOUR_OF_DAY]
		val convertedHour = to12HourFormat(hour)

		// Get the time format
		var format = if (DateFormat.is24HourFormat(context))
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
		val format = DateFormat.is24HourFormat(context)

		// Check the format
		return if (format)
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
			// Alarm is disabled, so continue to next alarm
			if (!a.isEnabled)
			{
				continue
			}

			// Get the calendar instance for the next time the alarm will run
			val calendar = getNextAlarmDay(a)

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
	 *
	 * @return The Calendar day on which the given alarm will run next.
	 */
	fun getNextAlarmDay(alarm: NacAlarm): Calendar
	{
		// Convert the alarm to a list of calendar instances
		val calendars = alarmToCalendars(alarm)

		// Get the calendar day that is the soonest
		return getNextDay(calendars)!!
	}

	/**
	 * Get the Calendary day that represents the next upcoming day.
	 *
	 * @return The Calendary day that represents the next upcoming day.
	 */
	private fun getNextDay(calendars: List<Calendar>): Calendar?
	{
		var next: Calendar? = null

		// Iterate over each calendar instance
		for (c in calendars)
		{
			// Check if either the "next" calendar has not been set yet,
			// or occurs after the current calendar item
			next = if ((next == null) || next.after(c))
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
			val NONE: EnumSet<Day> = EnumSet.noneOf(Day::class.java)

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
			val WEEK: EnumSet<Day> = EnumSet.allOf(Day::class.java)

			/**
			 * All weekday days.
			 */
			val WEEKDAY: EnumSet<Day> = EnumSet.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)

			/**
			 * All weekend days.
			 */
			val WEEKEND: EnumSet<Day> = EnumSet.of(SUNDAY, SATURDAY)

			/**
			 * Length of week.
			 */
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

				// Get the days of week
				val daysOfWeek = context.resources.getStringArray(R.array.days_of_week)
				val dow: MutableList<String> = ArrayList()

				// Abbreviate the days of week
				for (s in daysOfWeek)
				{
					// Get the first 3 characters in the day of week
					val abbrv = s.substring(0, 3)

					// Add it to the list
					dow.add(abbrv)
				}

				//List<String> dow = cons.getDaysOfWeekAbbr();
				val days = listOf(*Day.values())
				val summary = StringBuilder(32)

				// Iterate over each day in the week
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
						summary.append(dow[i])
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
		 * Build a message to display.
		 *
		 * @return Build a message to display.
		 */
		private fun build(
			shared: NacSharedPreferences,
			alarm: NacAlarm?,
			prefix: String?
		): String
		{
			val res = shared.resources

			// No alarm provided
			return if (alarm == null)
			{
				getNoAlarmsScheduled(res)
			}
			// Alarm is not enabled
			else if (!alarm.isEnabled)
			{
				getAlarmDisabled(res, alarm)
			}
			else
			{
				// Get the next alarm day
				val context = shared.context
				val calendar = getNextAlarmDay(alarm)

				// e.g. Alarm in 12 hour 5 min
				if (shared.nextAlarmFormat == 0)
				{
					getTimeIn(context, calendar, prefix)
				}
				// e.g. Alarm on ...
				else
				{
					getTimeOn(context, calendar, prefix)
				}
			}
		}

		/**
		 * Get the message when an alarm is disabled.
		 *
		 * @return The message when an alarm is disabled.
		 */
		private fun getAlarmDisabled(resources: Resources, alarm: NacAlarm) : String
		{
			val locale = Locale.getDefault()
			val length = resources.getInteger(R.integer.max_message_name_length)
			val isDisabled = resources.getString(R.string.is_disabled)
			val name = alarm.getNameNormalizedForMessage(length)

			// No alarm name
			if (name.isEmpty())
			{
				// Get the word
				val alarmPlural = resources.getQuantityString(R.plurals.alarm, 1)
				val alarmWord = capitalize(alarmPlural)

				String.format(locale, "%1\$s %2\$s.", alarmWord, isDisabled)
			}
			// Show alarm name
			else
			{
				String.format(locale, "\"%1\$s\" %2\$s.", name, isDisabled)
			}
			return ""
		}

		/**
		 * Get the message to display when the next alarm will occur.
		 *
		 * @return The message to display when the next alarm will occur.
		 */
		fun getNextAlarm(shared: NacSharedPreferences, alarm: NacAlarm?): String
		{
			// Get the prefix
			val prefix = shared.resources.getString(R.string.next_alarm)

			// Create the message
			return build(shared, alarm, prefix)
		}

		/**
		 * Get the message when no alarms are scheduled.
		 *
		 * @return The message when no alarms are scheduled.
		 */
		private fun getNoAlarmsScheduled(resources: Resources) : String
		{
			val locale = Locale.getDefault()
			val noAlarmsScheduled = resources.getString(R.string.message_no_alarms_scheduled)

			return String.format(locale, "%1\$s.", noAlarmsScheduled)
		}

		/**
		 * Get the message to display when an alarm will run IN some amount of time.
		 *
		 * @return The message to display when an alarm will run IN some amount of
		 *         time.
		 */
		private fun getTimeIn(
			context: Context,
			calendar: Calendar,
			prefix: String?
		): String
		{
			val locale = Locale.getDefault()
			val res = context.resources
			val time = (calendar.timeInMillis - System.currentTimeMillis()) / 1000
			var format = "%1\$d %2\$s %3\$d %4\$s"

			// Get the time components
			val day = time / (60 * 60 * 24) % 365
			val hr = time / (60 * 60) % 24
			val min = time / 60 % 60
			val sec = time % 60

			// Get the units of time
			val dayunit = res.getQuantityString(R.plurals.unit_day, day.toInt())
			val hrunit = res.getQuantityString(R.plurals.unit_hour, hr.toInt())
			val minunit = res.getQuantityString(R.plurals.unit_minute, min.toInt())
			val secunit = res.getQuantityString(R.plurals.unit_second, sec.toInt())
			val timeIn = context.getString(R.string.time_in)


			val timeRemaining = if (day > 0)
			{
				// Days
				String.format(locale, format, day, dayunit, hr, hrunit)
			}
			else
			{
				if (hr > 0)
				{
					// Hours
					String.format(locale, format, hr, hrunit, min, minunit)
				}
				else
				{
					// Seconds
					if (min == 0L)
					{
						format = format.substring(10, 19)
					}

					// Minutes and/or seconds, depending on above conditional
					String.format(locale, format, min, minunit, sec, secunit)
				}
			}

			// Build the message
			return String.format(locale, "%1\$s %2\$s %3\$s", prefix, timeIn, timeRemaining)
		}

		/**
		 * Get the message to display when an alarm will run ON some date and time.
		 *
		 * @return The message to display when an alarm will run ON some date and
		 *         time.
		 */
		private fun getTimeOn(
			context: Context,
			calendar: Calendar,
			prefix: String?
		): String
		{
			// Get the message contents
			val locale = Locale.getDefault()
			val timeOn = context.getString(R.string.time_on)
			val time = getFullTime(context, calendar)

			// Format the message
			return String.format(locale, "%1\$s %2\$s %3\$s", prefix, timeOn, time)
		}

		/**
		 * Get the message to display when the alarm will run.
		 *
		 * @return The message to display when the alarm will run.
		 */
		fun getWillRun(shared: NacSharedPreferences, alarm: NacAlarm): String
		{
			val locale = Locale.getDefault()
			val res = shared.resources

			// Get the resources
			val length = res.getInteger(R.integer.max_message_name_length)
			val willRun = res.getString(R.string.will_run)

			// Get the name of the alarm
			val name = alarm.getNameNormalizedForMessage(length)

			// Determine the prefix to use for the message
			val prefix = if (name.isEmpty())
			{
				willRun
			}
			else
			{
				String.format(locale, "\"%1\$s\" %2\$s", name, willRun.lowercase(locale))
			}

			// Get the message
			return build(shared, alarm, prefix)
		}

	}

}