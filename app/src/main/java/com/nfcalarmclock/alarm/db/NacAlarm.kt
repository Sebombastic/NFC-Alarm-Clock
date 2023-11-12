package com.nfcalarmclock.alarm.db

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nfcalarmclock.media.NacMedia
import com.nfcalarmclock.shared.NacSharedPreferences
import com.nfcalarmclock.util.NacCalendar
import com.nfcalarmclock.util.NacCalendar.Day
import com.nfcalarmclock.util.NacUtility
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.Calendar
import java.util.EnumSet
import java.util.Locale

/**
 * Alarm.
 */
@Entity(tableName = "alarm")
class NacAlarm() : Comparable<NacAlarm>, Parcelable
{

	/**
	 * Unique alarm ID.
	 * <p>
	 * When setting the Id manually, it must be 0 to be autogenerated.
	 */
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	var id: Long = 0

	/**
	 * Flag indicating whether the alarm is currently active or not.
	 */
	@ColumnInfo(name = "is_active")
	var isActive = false

	/**
	 * Amount of time, in milliseconds, the alarm has been active for.
	 *
	 * This will typically only change when the alarm is snoozed.
	 */
	@ColumnInfo(name = "time_active")
	var timeActive: Long = 0

	/**
	 * Number of times the alarm has been snoozed.
	 */
	@ColumnInfo(name = "snooze_count")
	var snoozeCount = 0

	/**
	 * Flag indicating whether the alarm is enabled or not.
	 */
	@ColumnInfo(name = "is_enabled")
	var isEnabled = false

	/**
	 * Hour at which to run the alarm.
	 */
	@ColumnInfo(name = "hour")
	var hour = 0

	/**
	 * Minute at which to run the alarm.
	 */
	@ColumnInfo(name = "minute")
	var minute = 0

	/**
	 * Hour at which to run the alarm, when it is snoozed.
	 */
	@ColumnInfo(name = "snooze_hour")
	var snoozeHour = 0

	/**
	 * Minute at which to run the alarm, when it is snoozed
	 */
	@ColumnInfo(name = "snooze_minute")
	var snoozeMinute = 0

	/**
	 * Days on which to run the alarm.
	 */
	@ColumnInfo(name = "days")
	var days: EnumSet<Day> = EnumSet.noneOf(Day::class.java)

	/**
	 * Flag indicating whether the alarm should be repeated or not.
	 */
	@ColumnInfo(name = "should_repeat")
	var repeat = false

	/**
	 * Flag indicating whether the alarm should vibrate the phone or not.
	 */
	@ColumnInfo(name = "should_vibrate")
	var vibrate = false

	/**
	 * Flag indicating whether the alarm should use NFC or not.
	 */
	@ColumnInfo(name = "should_use_nfc")
	var useNfc = false

	/**
	 * ID of the NFC tag that needs to be used to dismiss the alarm.
	 */
	@ColumnInfo(name = "nfc_tag_id")
	var nfcTagId: String = ""

	/**
	 * Type of media.
	 *
	 * TODO: Do I need this? Might need it for Spotify.
	 */
	@ColumnInfo(name = "media_type")
	var mediaType = 0

	/**
	 * Path to the media that will play when the alarm is run.
	 */
	@ColumnInfo(name = "media_path")
	var mediaPath: String = ""

	/**
	 * Title of the media that will play when the alarm is run.
	 */
	@ColumnInfo(name = "media_title")
	var mediaTitle: String = ""

	/**
	 * Volume level to set when the alarm is run.
	 */
	@ColumnInfo(name = "volume")
	var volume = 0

	/**
	 * Audio source to use for the media that will play when the alarm is run.
	 */
	@ColumnInfo(name = "audio_source")
	var audioSource: String = ""

	/**
	 * Name of the alarm.
	 */
	@ColumnInfo(name = "name")
	var name: String = ""

	/**
	 * Flag indicating whether text-to-speech should be used or not.
	 */
	@ColumnInfo(name = "should_use_tts")
	var useTts = false

	/**
	 * Frequency at which to play text-to-speech, in units of min.
	 */
	@ColumnInfo(name = "tts_frequency")
	var ttsFrequency = 0

	/**
	 * Flag indicating whether to gradually increase the volume or not, when an
	 * alarm is active.
	 */
	@ColumnInfo(name = "should_gradually_increase_volume")
	var shouldGraduallyIncreaseVolume = false

	/**
	 * Flag indicating whether to restrict changing the volume or not, when an
	 * alarm is active.
	 */
	@ColumnInfo(name = "should_restrict_volume")
	var shouldRestrictVolume = false

	/**
	 * Flag indicating whether or not to use dismiss early.
	 */
	@ColumnInfo(name = "should_dismiss_early")
	var useDismissEarly = false

	/**
	 * Amount of time, in minutes, to allow a user to dismiss early by.
	 */
	@ColumnInfo(name = "dismiss_early_time")
	var dismissEarlyTime = 30

	/**
	 * Time of alarm that would have been next but was dismissed early.
	 */
	@ColumnInfo(name = "time_of_dismiss_early_alarm")
	var timeOfDismissEarlyAlarm: Long = 0

	/**
	 * Helper to build an alarm.
	 */
	class Builder()
	{

		/**
		 * Alarm object that will be built.
		 */
		private val alarm: NacAlarm = NacAlarm()

		/**
		 * Constructor.
		 */
		init
		{
			val calendar = Calendar.getInstance()

			this.setIsEnabled(true)
				.setHour(calendar[Calendar.HOUR_OF_DAY])
				.setMinute(calendar[Calendar.MINUTE])
				.setDays(Day.NONE)
				.setRepeat(false)
				.setVibrate(false)
				.setUseNfc(false)
				.setNfcTagId("")
				.setMediaType(NacMedia.TYPE_NONE)
				.setMediaPath("")
				.setMediaTitle("")
				.setVolume(0)
				.setAudioSource("Media")
				.setName("")
				.setUseTts(false)
				.setShouldGraduallyIncreaseVolume(false)
				.setShouldRestrictVolume(false)
				.setUseDismissEarly(false)
				.setDismissEarlyTime(0)
				.setTimeOfDismissEarlyAlarm(0)
		}

		/**
		 * Constructor.
		 */
		constructor(shared: NacSharedPreferences?) : this()
		{
			if (shared != null)
			{
				this.setDays(NacCalendar.Day.valueToDays(shared.days))
					.setRepeat(shared.repeat)
					.setVibrate(shared.vibrate)
					.setUseNfc(shared.useNfc)
					.setMediaPath(shared.mediaPath)
					.setVolume(shared.volume)
					.setAudioSource(shared.audioSource)
					.setName(shared.name)
					.setUseTts(shared.speakToMe)
					.setTtsFrequency(shared.speakFrequency)
					.setShouldGraduallyIncreaseVolume(shared.shouldGraduallyIncreaseVolume)
					.setShouldRestrictVolume(shared.shouldRestrictVolume)
					.setUseDismissEarly(shared.useDismissEarly)
					.setDismissEarlyTime(shared.dismissEarlyTime)
			}
		}

		/**
		 * Build the alarm.
		 */
		fun build(): NacAlarm
		{
			return alarm
		}

		/**
		 * Set the audio source.
		 *
		 * @param  source  The audio source.
		 *
		 * @return The Builder.
		 */
		fun setAudioSource(source: String): Builder
		{
			alarm.audioSource = source
			return this
		}

		/**
		 * Set the days to the run the alarm.
		 *
		 * @param  days  The set of days to run the alarm on.
		 *
		 * @return The Builder.
		 */
		fun setDays(days: EnumSet<Day>): Builder
		{
			alarm.days = days
			return this
		}

		/**
		 * @see .setDays
		 */
		fun setDays(value: Int): Builder
		{
			return this.setDays(NacCalendar.Day.valueToDays(value))
		}

		/**
		 * Set the time before an alarm goes off to show the dismiss early button.
		 *
		 * @param  dismissEarly  Amount of time, in minutes.
		 *
		 * @return The Builder.
		 */
		fun setDismissEarlyTime(dismissEarly: Int): Builder
		{
			alarm.dismissEarlyTime = dismissEarly
			return this
		}

		/**
		 * Set the hour.
		 *
		 * @param  hour  The hour at which to run the alarm.
		 *
		 * @return The Builder.
		 */
		fun setHour(hour: Int): Builder
		{
			alarm.hour = hour
			return this
		}

		/**
		 * Set the alarm ID.
		 *
		 * @param  id  The alarm id.
		 *
		 * @return The Builder.
		 */
		fun setId(id: Long): Builder
		{
			alarm.id = id
			return this
		}

		/**
		 * Set whether the alarm is enabled or not.
		 *
		 * @param  enabled  True if the alarm is enabled and False otherwise.
		 *
		 * @return The Builder.
		 */
		fun setIsEnabled(enabled: Boolean): Builder
		{
			alarm.isEnabled = enabled
			return this
		}

		/**
		 * Set the path, name, and type of the sound to play.
		 *
		 * @param  context  The application context.
		 * @param  path  The path to the sound to play.
		 *
		 * @return The Builder.
		 */
		fun setMedia(context: Context, path: String): Builder
		{
			// Get media information
			val type = NacMedia.getType(context, path)
			val title = NacMedia.getTitle(context, path)

			// Set media information
			setMediaType(type)
			setMediaPath(path)
			setMediaTitle(title)

			return this
		}

		/**
		 * Set the media title.
		 *
		 * @param  title  The title of the media file to play.
		 *
		 * @return The Builder.
		 */
		fun setMediaTitle(title: String): Builder
		{
			alarm.mediaTitle = title
			return this
		}

		/**
		 * Set the sound to play when the alarm goes off.
		 *
		 * @param  path  The path to the media file to play when the alarm goes
		 * off.
		 *
		 * @return The Builder.
		 */
		fun setMediaPath(path: String): Builder
		{
			alarm.mediaPath = path
			return this
		}

		/**
		 * Set the type of sound to play.
		 *
		 * @param  type  The type of media file to play.
		 *
		 * @return The Builder.
		 */
		fun setMediaType(type: Int): Builder
		{
			alarm.mediaType = type
			return this
		}

		/**
		 * Set the minute.
		 *
		 * @param  minute  The minute at which to run the alarm.
		 *
		 * @return The Builder.
		 */
		fun setMinute(minute: Int): Builder
		{
			alarm.minute = minute
			return this
		}

		/**
		 * Set the name of the alarm.
		 *
		 * @param  name  The alarm name.
		 *
		 * @return The Builder.
		 */
		fun setName(name: String): Builder
		{
			alarm.name = name
			return this
		}

		/**
		 * Set the NFC tag ID of the tag that will be used to dismiss the alarm.
		 *
		 * @param  tagId  The ID of the NFC tag.
		 *
		 * @return The Builder.
		 */
		fun setNfcTagId(tagId: String): Builder
		{
			alarm.nfcTagId = tagId
			return this
		}

		/**
		 * Set whether the alarm should repeat every week or not.
		 *
		 * @param  repeat  True if repeating the alarm after it runs, and False
		 * otherwise.
		 *
		 * @return The Builder.
		 */
		fun setRepeat(repeat: Boolean): Builder
		{
			alarm.repeat = repeat
			return this
		}

		/**
		 * Set whether the volume should be gradually increased when an alarm is active.
		 *
		 * @param  shouldIncrease  True if the volume should be gradually
		 * increased, and False otherwise.
		 *
		 * @return The Builder.
		 */
		fun setShouldGraduallyIncreaseVolume(shouldIncrease: Boolean): Builder
		{
			alarm.shouldGraduallyIncreaseVolume = shouldIncrease
			return this
		}

		/**
		 * Set whether the volume should be restricted when an alarm is active.
		 *
		 * @param  restrict  True if the volume should be restricted, and False
		 * otherwise.
		 *
		 * @return The Builder.
		 */
		fun setShouldRestrictVolume(restrict: Boolean): Builder
		{
			alarm.shouldRestrictVolume = restrict
			return this
		}

		/**
		 * Set the time of the alarm that was dismissed early.
		 *
		 * @param  time  Time of the alarm that was dismissed early (milliseonds).
		 *
		 * @return The Builder.
		 */
		fun setTimeOfDismissEarlyAlarm(time: Long): Builder
		{
			alarm.timeOfDismissEarlyAlarm = time
			return this
		}

		/**
		 * Set the frequency at which to use TTS, in units of min.
		 *
		 * @param  freq  The TTS frequency.
		 *
		 * @return The Builder.
		 */
		fun setTtsFrequency(freq: Int): Builder
		{
			alarm.ttsFrequency = freq
			return this
		}

		/**
		 * Set whether to use dismiss early.
		 *
		 * @param  useDismissEarly  Whether or not to use dismiss early.
		 *
		 * @return The Builder.
		 */
		fun setUseDismissEarly(useDismissEarly: Boolean): Builder
		{
			alarm.useDismissEarly = useDismissEarly
			return this
		}

		/**
		 * Set whether the alarm should use NFC to dismiss or not.
		 *
		 * @param  useNfc  True if the phone should use NFC to dismiss, and False
		 * otherwise.
		 *
		 * @return The Builder.
		 */
		fun setUseNfc(useNfc: Boolean): Builder
		{
			alarm.useNfc = useNfc
			return this
		}

		/**
		 * Set whether the alarm should use TTS or not.
		 *
		 * @param  useTts  True if should use TTS, and False otherwise.
		 *
		 * @return The Builder.
		 */
		fun setUseTts(useTts: Boolean): Builder
		{
			alarm.useTts = useTts
			return this
		}

		/**
		 * Set whether the alarm should vibrate the phone or not.
		 *
		 * @param  vibrate  True if the phone should vibrate when the alarm is
		 * going off and false otherwise.
		 *
		 * @return The Builder.
		 */
		fun setVibrate(vibrate: Boolean): Builder
		{
			alarm.vibrate = vibrate
			return this
		}

		/**
		 * Set the volume level.
		 *
		 * @param  volume  The volume level.
		 *
		 * @return The Builder.
		 */
		fun setVolume(volume: Int): Builder
		{
			alarm.volume = volume
			return this
		}
	}

	/**
	 * Populate values with input parcel.
	 */
	private constructor(input: Parcel) : this()
	{
		id = input.readLong()
		isActive = input.readInt() != 0
		timeActive = input.readLong()
		snoozeCount = input.readInt()
		isEnabled = input.readInt() != 0
		hour = input.readInt()
		minute = input.readInt()
		setDays(input.readInt())
		repeat = input.readInt() != 0
		vibrate = input.readInt() != 0
		useNfc = input.readInt() != 0
		nfcTagId = input.readString() ?: ""
		mediaType = input.readInt()
		mediaPath = input.readString() ?: ""
		mediaTitle = input.readString() ?: ""
		volume = input.readInt()
		audioSource = input.readString() ?: ""
		name = input.readString() ?: ""
		useTts = input.readInt() != 0
		ttsFrequency = input.readInt()
		shouldGraduallyIncreaseVolume = input.readInt() != 0
		shouldRestrictVolume = input.readInt() != 0
		useDismissEarly = input.readInt() != 0
		dismissEarlyTime = input.readInt()
		timeOfDismissEarlyAlarm = input.readLong()
	}

	/**
	 * Add to the snooze count.
	 *
	 * @param  num  Number to add to the snooze count.
	 */
	private fun addToSnoozeCount(num: Int)
	{
		snoozeCount += num
	}

	/**
	 * Add to the time, in milliseconds, that the alarm is active.
	 *
	 * @param  time  Time, in milliseconds, to add to the active time.
	 */
	fun addToTimeActive(time: Long)
	{
		timeActive += time
	}

	/**
	 * Check if any days are selected.
	 */
	val areDaysSelected: Boolean
		get() = days.isNotEmpty()

	/**
	 * Check if the alarm can be snoozed.
	 *
	 * @return True if the alarm can be snoozed, and False otherwise.
	 */
	fun canSnooze(shared: NacSharedPreferences): Boolean
	{
		val maxSnoozeCount = shared.maxSnoozeValue

		return snoozeCount < maxSnoozeCount || maxSnoozeCount < 0
	}

	/**
	 * Compare the next day this alarm will run with another alarm.
	 *
	 * @param  alarm  An alarm.
	 *
	 * @return A negative integer, zero, or a positive integer as this object is
	 * less than, equal to, or greater than the specified object.
	 */
	private fun compareDay(alarm: NacAlarm): Int
	{
		val cal = NacCalendar.getNextAlarmDay(this)
		val otherCal = NacCalendar.getNextAlarmDay(alarm)

		return cal.compareTo(otherCal)
	}

	/**
	 * Compare the in use value in this alarm with another alarm.
	 *
	 *
	 * At least one alarm should be in use, otherwise the comparison is
	 * meaningless.
	 *
	 * @param  alarm  An alarm.
	 *
	 * @return A negative integer, zero, or a positive integer as this object is
	 * less than, equal to, or greater than the specified object.
	 */
	private fun compareInUseValue(alarm: NacAlarm): Int
	{
		val value = computeInUseValue()
		val otherValue = alarm.computeInUseValue()

		return if (otherValue < 0)
		{
			-1
		} else if (value < 0)
		{
			1
		} else if (value == otherValue)
		{
			0
		} else
		{
			if (value < otherValue) -1 else 1
		}
	}

	/**
	 * Compare the time of this alarm with another alarm.
	 *
	 * @param  alarm  An alarm.
	 *
	 * @return A negative integer, zero, or a positive integer as this object is
	 * less than, equal to, or greater than the specified object.
	 */
	private fun compareTime(alarm: NacAlarm): Int
	{
		val locale = Locale.getDefault()
		val format = "%1$02d:%2$02d"
		val time = String.format(locale, format, hour,
			minute)
		val otherTime = String.format(locale, format, alarm.hour,
			alarm.minute)

		return time.compareTo(otherTime)
	}

	/**
	 * Compare this alarm with another alarm.
	 *
	 * @param  other  An alarm.
	 *
	 * @return A negative integer, zero, or a positive integer as this object is
	 * less than, equal to, or greater than the specified object.
	 */
	override fun compareTo(other: NacAlarm): Int
	{
		return if (this.equals(other))
		{
			0
		} else if (isInUse || other.isInUse)
		{
			val value = compareInUseValue(other)
			if (value == 0)
			{
				compareTime(other)
			} else
			{
				value
			}
		} else if (isEnabled xor other.isEnabled)
		{
			if (isEnabled) -1 else 1
		} else
		{
			compareDay(other)
		}
	}

	/**
	 * Get value corresponding to how in use an alarm is. This is used as a
	 * means to easily compare two alarms that are both in use.
	 *
	 * If an alarm is NOT IN USE, return -1.
	 *
	 * If an alarm is ACTIVE AND NOT SNOOZED, return 0.
	 *
	 * If an alarm is ACTIVE AND SNOOZED, return snooze count.
	 *
	 * If an alarm is NOT ACTIVE AND SNOOZED, return 1000 * snooze count.
	 *
	 * @return A value corresponding to how in use an alarm is.
	 */
	private fun computeInUseValue(): Int
	{
		// Check if alarm is not in use
		if (!isInUse)
		{
			return -1
		}

		// Scale the in use value
		val scale = if (isActive) 1 else 1000

		// Compute value by how many times the alarm has been snoozed
		return scale * snoozeCount
	}

	/**
	 * Create a copy of this alarm.
	 *
	 *
	 * The ID of the new alarm will be set to 0.
	 *
	 * @return A copy of this alarm.
	 */
	fun copy(): NacAlarm
	{
		return Builder()
			.setId(0)
			.setIsEnabled(isEnabled)
			.setHour(hour)
			.setMinute(minute)
			.setDays(days)
			.setRepeat(shouldRepeat)
			.setVibrate(shouldVibrate)
			.setUseNfc(shouldUseNfc)
			.setNfcTagId(nfcTagId)
			.setMediaType(mediaType)
			.setMediaPath(mediaPath)
			.setMediaTitle(mediaTitle)
			.setVolume(volume)
			.setAudioSource(audioSource)
			.setName(name)
			.setUseTts(shouldUseTts)
			.setTtsFrequency(ttsFrequency)
			.setShouldGraduallyIncreaseVolume(shouldGraduallyIncreaseVolume)
			.setShouldRestrictVolume(shouldRestrictVolume)
			.setUseDismissEarly(shouldUseDismissEarly)
			.setDismissEarlyTime(dismissEarlyTime)
			.setTimeOfDismissEarlyAlarm(timeOfDismissEarlyAlarm)
			.build()
	}

	/**
	 * Describe contents (required for Parcelable).
	 */
	override fun describeContents(): Int
	{
		return 0
	}

	/**
	 * Dismiss an alarm.
	 *
	 *
	 * This will not update the database, or schedule the next alarm. That
	 * still needs to be done after calling this method.
	 */
	fun dismiss()
	{
		isActive = false
		timeActive = 0
		snoozeCount = 0
		snoozeHour = -1
		snoozeMinute = -1

		// Check if the alarm should not be repeated
		if (!shouldRepeat)
		{
			// Toggle the alarm
			toggleAlarm()
		}
	}

	/**
	 * Dismiss an alarm early.
	 */
	fun dismissEarly()
	{
		// Alarm should be repeated
		if (shouldRepeat)
		{
			val cal = NacCalendar.getNextAlarmDay(this)
			val time = cal.timeInMillis

			timeOfDismissEarlyAlarm = time
		}
		// Alarm should only be run once
		else
		{
			toggleAlarm()
		}
	}

	/**
	 * Check if this alarm equals another alarm.
	 *
	 * @param  alarm  An alarm.
	 *
	 * @return True if both alarms are the same, and false otherwise.
	 */
	fun equals(alarm: NacAlarm?): Boolean
	{
		return (alarm != null)
			&& (this.equalsId(alarm))
			&& (isActive == alarm.isActive)
			&& (timeActive == alarm.timeActive)
			&& (snoozeCount == alarm.snoozeCount)
			&& (isEnabled == alarm.isEnabled)
			&& (hour == alarm.hour)
			&& (minute == alarm.minute)
			&& (days == alarm.days)
			&& (shouldRepeat == alarm.shouldRepeat)
			&& (shouldVibrate == alarm.shouldVibrate)
			&& (shouldUseNfc == alarm.shouldUseNfc)
			&& (nfcTagId == alarm.nfcTagId)
			&& (mediaType == alarm.mediaType)
			&& (mediaPath == alarm.mediaPath)
			&& (mediaTitle == alarm.mediaTitle)
			&& (volume == alarm.volume)
			&& (audioSource == alarm.audioSource)
			&& (name == alarm.name)
			&& (shouldUseTts == alarm.shouldUseTts)
			&& (ttsFrequency == alarm.ttsFrequency)
			&& (shouldGraduallyIncreaseVolume == alarm.shouldGraduallyIncreaseVolume)
			&& (shouldRestrictVolume == alarm.shouldRestrictVolume)
			&& (shouldUseDismissEarly == alarm.shouldUseDismissEarly)
			&& (dismissEarlyTime == alarm.dismissEarlyTime)
			&& (timeOfDismissEarlyAlarm == alarm.timeOfDismissEarlyAlarm)
	}

	/**
	 * Check if this alarm has the same ID as another alarm.
	 *
	 * @param  alarm  An alarm.
	 *
	 * @return True if both alarms are the same, and false otherwise.
	 */
	fun equalsId(alarm: NacAlarm): Boolean
	{
		return id == alarm.id
	}

	/**
	 * @return The time string.
	 */
	fun getClockTime(context: Context): String
	{
		val hour = hour
		val minute = minute

		return NacCalendar.getClockTime(context, hour, minute)
	}

	/**
	 * The index which corresponds to the amount of time, in minutes to allow a user to
	 * dismiss early by.
	 */
	val dismissEarlyIndex: Int
		get()
		{
			val time = dismissEarlyTime
			NacUtility.printf("Time : %d", time)

			return NacSharedPreferences.getDismissEarlyTimeToIndex(time)
		}

	/**
	 * Get the full time string.
	 *
	 * @return The full time string.
	 */
	fun getFullTime(context: Context): String
	{
		val next = NacCalendar.getNextAlarmDay(this)

		return NacCalendar.getFullTime(context, next)
	}

	/**
	 * Get the meridian (AM or PM).
	 *
	 * @return The meridian (AM or PM).
	 */
	fun getMeridian(context: Context): String
	{
		val hour = hour

		return NacCalendar.getMeridian(context, hour)
	}

	/**
	 * The normalized alarm name (with newlines replaced with spaces).
	 */
	val nameNormalized: String
		get()
		{
			return if (name.isNotEmpty())
			{
				name.replace("\n", " ")
			}
			else
			{
				name
			}
		}

	/**
	 * @see .getNameNormalized
	 */
	fun getNameNormalizedForMessage(max: Int): String
	{
		val name = nameNormalized
		val locale = Locale.getDefault()

		return if (name.length > max) String.format(locale,
			"%1\$s...",
			name.substring(0, max - 3)) else name
	}

	// --Commented out by Inspection START (6/26/21, 11:52 PM):
	//	/**
	//	 * @return The current snooze count.
	//	 */
	//	public int getSnoozeCount(NacSharedPreferences shared)
	//	{
	//		long id = this.getId();
	//		return shared.getSnoozeCount(id);
	//	}
	// --Commented out by Inspection STOP (6/26/21, 11:52 PM)

	/**
	 * The frequency at which to use TTS, in units of milliseconds.
	 */
	val ttsFrequencyMillis: Long
		get() = ttsFrequency * 60L * 1000L

	/**
	 * Check if the alarm has a sound that will be played when it goes off.
	 */
	val hasMedia: Boolean
		get() = mediaPath.isNotEmpty()

	/**
	 * Check if the alarm is snoozed.
	 */
	val isSnoozed: Boolean
		get() = snoozeCount > 0

	/**
	 * Check if the alarm is being used, by being active or snoozed.
	 */
	val isInUse: Boolean
		get() = isActive || isSnoozed

	/**
	 * Print all values in the alarm object.
	 */
	@Suppress("unused")
	fun print()
	{
		NacUtility.printf("Alarm Information")
		NacUtility.printf("Id                  : %d", id)
		NacUtility.printf("Is Active           : %b", isActive)
		NacUtility.printf("Time Active         : %d", timeActive)
		NacUtility.printf("Snooze Count        : %d", snoozeCount)
		NacUtility.printf("Is Enabled          : %b", isEnabled)
		NacUtility.printf("Hour                : %d", hour)
		NacUtility.printf("Minute              : %d", minute)
		NacUtility.printf("Days                : %s", days)
		NacUtility.printf("Repeat              : %b", shouldRepeat)
		NacUtility.printf("Vibrate             : %b", shouldVibrate)
		NacUtility.printf("Use NFC             : %b", shouldUseNfc)
		NacUtility.printf("Nfc Tag Id          : %s", nfcTagId)
		NacUtility.printf("Media Type          : %s", mediaType)
		NacUtility.printf("Media Path          : %s", mediaPath)
		NacUtility.printf("Media Name          : %s", mediaTitle)
		NacUtility.printf("Volume              : %d", volume)
		NacUtility.printf("Audio Source        : %s", audioSource)
		NacUtility.printf("Name                : %s", name)
		NacUtility.printf("Use Tts             : %b", shouldUseTts)
		NacUtility.printf("Tts Freq            : %d", ttsFrequency)
		NacUtility.printf("Grad Inc Vol        : %b", shouldGraduallyIncreaseVolume)
		NacUtility.printf("Restrict Vol        : %b", shouldRestrictVolume)
		NacUtility.printf("Use Dismiss Early   : %b", shouldUseDismissEarly)
		NacUtility.printf("Dismiss Early       : %d", dismissEarlyTime)
		NacUtility.printf("Time of Early Alarm : %d", timeOfDismissEarlyAlarm)
	}

	/**
	 * @see .setDays
	 */
	fun setDays(value: Int)
	{
		days = NacCalendar.Day.valueToDays(value)
	}

	/**
	 * Set the amount of time, in minutes, to allow a user to dismiss early by.
	 */
	fun setDismissEarlyTimeFromIndex(index: Int)
	{
		dismissEarlyTime = NacSharedPreferences.getDismissEarlyIndexToTime(index)
	}

	/**
	 * Set the sound to play when the alarm goes off.
	 *
	 * @param  context  The application context.
	 * @param  path  The path to sound to play.
	 */
	fun setMedia(context: Context, path: String)
	{
		val type = NacMedia.getType(context, path)
		val title = NacMedia.getTitle(context, path)

		mediaType = type
		mediaPath = path
		mediaTitle = title
	}

	/**
	 * Check if should repeat the alarm after it runs or not.
	 */
	val shouldRepeat: Boolean
		get() = repeat

	/**
	 * Check if should use dismiss early or not.
	 */
	val shouldUseDismissEarly: Boolean
		get() = useDismissEarly

	/**
	 * Check if should use NFC or not.
	 */
	val shouldUseNfc: Boolean
		get() = useNfc

	/**
	 * Check if should use TTS or not.
	 */
	val shouldUseTts: Boolean
		get() = useTts

	/**
	 * Check if the phone should vibrate when the alarm is run.
	 */
	val shouldVibrate: Boolean
		get() = vibrate

	/**
	 * Snooze the alarm.
	 *
	 * @param  shared  Shared preferences.
	 *
	 * @return Calendar instance of when the snoozed alarm will go off.
	 */
	fun snooze(shared: NacSharedPreferences): Calendar
	{
		// Add the snooze duration value to the current time
		val cal = Calendar.getInstance()
		cal.add(Calendar.MINUTE, shared.snoozeDurationValue)

		// Set the snooze hour and minute
		snoozeHour = cal[Calendar.HOUR_OF_DAY]
		snoozeMinute = cal[Calendar.MINUTE]

		// Increment the snooze count
		addToSnoozeCount(1)

		return cal
	}

	/**
	 * Toggle the the current day/enabled attribute of the alarm.
	 *
	 * An alarm can only be toggled if repeat is not enabled.
	 */
	private fun toggleAlarm()
	{
		// Check if the alarm should be repeated
		if (shouldRepeat)
		{
			return
		}

		// Check if there are any days selected
		if (areDaysSelected)
		{
			toggleToday()
		}
		// No days selected
		else
		{
			isEnabled = false
		}
	}

	/**
	 * Toggle a day.
	 */
	fun toggleDay(day: Day)
	{
		// Check if day is contained in the list of alarm days
		if (days.contains(day))
		{
			days.remove(day)
		}
		// Day is not present in alarm days
		else
		{
			days.add(day)
		}
	}

	/**
	 * Toggle repeat.
	 */
	fun toggleRepeat()
	{
		repeat = !shouldRepeat
	}

	/**
	 * Toggle today.
	 */
	private fun toggleToday()
	{
		// Get today's day
		val day = NacCalendar.Day.TODAY

		// Toggle today
		toggleDay(day)
	}

	/**
	 * Toggle use NFC.
	 */
	fun toggleUseNfc()
	{
		useNfc = !shouldUseNfc
	}

	/**
	 * Toggle vibrate.
	 */
	fun toggleVibrate()
	{
		vibrate = !shouldVibrate
	}

	/** Whether or not the alarm will alarm soon.
	 *
	 *
	 * "Soon" is determined by the dismiss early time. If it will alarm within
	 * that time, then it is soon.
	 */
	fun willAlarmSoon(): Boolean
	{
		// Alarm is disabled or unable to use dismiss early
		if (!isEnabled || !shouldUseDismissEarly || dismissEarlyTime == 0)
		{
			return false
		}

		// Determine the difference in time between the next alarm and today
		val today = Calendar.getInstance()
		val cal = NacCalendar.getNextAlarmDay(this)
		val diff = (cal.timeInMillis - today.timeInMillis) / 1000 / 60

		// Compare the two amounts of time
		return (diff < dismissEarlyTime)
	}

	/**
	 * Write data into parcel (required for Parcelable).
	 *
	 *
	 * Update this when adding/removing an element.
	 */
	override fun writeToParcel(output: Parcel, flags: Int)
	{
		output.writeLong(id)
		output.writeInt(if (isActive) 1 else 0)
		output.writeLong(timeActive)
		output.writeInt(snoozeCount)
		output.writeInt(if (isEnabled) 1 else 0)
		output.writeInt(hour)
		output.writeInt(minute)
		output.writeInt(NacCalendar.Day.daysToValue(days))
		output.writeInt(if (shouldRepeat) 1 else 0)
		output.writeInt(if (shouldVibrate) 1 else 0)
		output.writeInt(if (shouldUseNfc) 1 else 0)
		output.writeString(nfcTagId)
		output.writeInt(mediaType)
		output.writeString(mediaPath)
		output.writeString(mediaTitle)
		output.writeInt(volume)
		output.writeString(audioSource)
		output.writeString(name)
		output.writeInt(if (shouldUseTts) 1 else 0)
		output.writeInt(ttsFrequency)
		output.writeInt(if (shouldGraduallyIncreaseVolume) 1 else 0)
		output.writeInt(if (shouldRestrictVolume) 1 else 0)
		output.writeInt(if (shouldUseDismissEarly) 1 else 0)
		output.writeInt(dismissEarlyTime)
	}

	companion object
	{

		/**
		 * Generate parcel (required for Parcelable).
		 */
		@JvmField
		val CREATOR: Parcelable.Creator<NacAlarm> = object : Parcelable.Creator<NacAlarm>
		{
			override fun createFromParcel(input: Parcel): NacAlarm
			{
				return NacAlarm(input)
			}

			override fun newArray(size: Int): Array<NacAlarm?>
			{
				return arrayOfNulls(size)
			}
		}

	}

}

/**
 * Hilt module to provide an instance of an alarm.
 */
@InstallIn(SingletonComponent::class)
@Module
class NacAlarmModule
{

	/**
	 * Provide an instance of an alarm.
	 */
	@Provides
	fun provideAlarm() : NacAlarm
	{
		return NacAlarm.Builder().build()
	}

}
