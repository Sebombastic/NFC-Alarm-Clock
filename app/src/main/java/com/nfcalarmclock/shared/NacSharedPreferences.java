package com.nfcalarmclock.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import androidx.preference.PreferenceManager;
import com.nfcalarmclock.R;
import com.nfcalarmclock.file.NacFile;
import com.nfcalarmclock.media.NacMedia;

/**
 * Container for the values of each preference.
 */
@SuppressWarnings("RedundantSuppression")
public class NacSharedPreferences
{

	/**
	 * The context application.
	 */
	private final Context mContext;

	/**
	 * Shared preferences instance.
	 */
	private final SharedPreferences mInstance;

	/**
	 * Resources.
	 */
	public final Resources resources;

	/**
	 */
	public NacSharedPreferences(Context context)
	{
		this.mContext = context;
		this.mInstance = PreferenceManager.getDefaultSharedPreferences(context);
		this.resources = context.getResources();
	}

	/**
	 * Edit whether this is the app's first run or not.
	 */
	public void editAppFirstRun(Context context, boolean first)
	{
		String key = context.getString(R.string.app_first_run);

		this.saveBoolean(key, first, false);
	}

	/**
	 * Edit whether statistics should start to be collected or not.
	 *
	 * @param  shouldStart  Whether statistics should start to be collected or
	 *     not.
	 */
	public void editAppStartStatistics(boolean shouldStart)
	{
		String key = this.resources.getString(R.string.app_start_statistics);

		this.saveBoolean(key, shouldStart, false);
	}

	/**
	 * Edit the default audio source to use when a new alarm card is created.
	 * <p>
	 * This can be changed for an alarm by clicking the audio settings button.
	 */
	public void editAudioSource(String source)
	{
		String key = this.resources.getString(R.string.alarm_audio_source_key);

		this.saveString(key, source, false);
	}

	/**
	 * Edit the height of the alarm card when it is collapsed.
	 */
	public void editCardHeightCollapsed(int height)
	{
		String key = this.resources.getString(R.string.card_height_collapsed);

		this.saveInt(key, height, false);
	}

	/**
	 * Edit the height of the alarm card height it is collapsed, but the dismiss
	 * button is showing.
	 */
	public void editCardHeightCollapsedDismiss(int height)
	{
		String key = this.resources.getString(R.string.card_height_collapsed_dismiss);

		this.saveInt(key, height, false);
	}

	/**
	 * Edit the height of the alarm card when it is expanded.
	 */
	public void editCardHeightExpanded(int height)
	{
		String key = this.resources.getString(R.string.card_height_expanded);

		this.saveInt(key, height, false);
	}

	/**
	 * Edit the flag indicating if the alarm card has been measured or not.
	 */
	public void editCardIsMeasured(boolean isMeasured)
	{
		String key = this.resources.getString(R.string.card_is_measured);

		this.saveBoolean(key, isMeasured, false);
	}

	/**
	 * Edit the default dismiss early time when an alarm is created.
	 */
	public void editDismissEarlyTime(int dismissEarly)
	{
		String key = this.resources.getString(R.string.alarm_dismiss_early_time_key);

		this.saveInt(key, dismissEarly, false);
	}

	/**
	 * Edit the previous version that this app was using.
	 * <p>
	 * Normally, this should be the same as the current version, but when an
	 * install occurs, these values will differ.
	 */
	public void editPreviousAppVersion(String version)
	{
		String key = this.resources.getString(R.string.previous_app_version);

		this.saveString(key, version, false);
	}

	/**
	 * Edit the previous system volume, before an alarm goes off.
	 */
	public void editPreviousVolume(int previous)
	{
		String key = this.resources.getString(R.string.sys_previous_volume);

		this.saveInt(key, previous, false);
	}

	/**
	 * Edit the counter that will indicate whether it is time to show dialog to
	 * Rate My App.
	 */
	public void editRateMyAppCounter(int counter)
	{
		String key = this.resources.getString(R.string.app_rating_counter);

		this.saveInt(key, counter, false);
	}

	/**
	 * Edit the default value of a newly created alarm for if the volume should
	 * gradually be increased when an alarm is active.
	 */
	public void editShouldGraduallyIncreaseVolume(boolean shouldIncrease)
	{
		String key = this.resources.getString(R.string.alarm_should_gradually_increase_volume_key);

		this.saveBoolean(key, shouldIncrease, false);
	}

	/**
	 * Edit the default should restrict volume value when an alarm is created.
	 */
	public void editShouldRestrictVolume(boolean shouldRestrict)
	{
		String key = this.resources.getString(R.string.alarm_should_restrict_volume_key);

		this.saveBoolean(key, shouldRestrict, false);
	}

	/**
	 * Edit the value indicating whether the main activity should be refreshed or
	 * not.
	 */
	public void editShouldRefreshMainActivity(boolean shouldRefresh)
	{
		String key = this.resources.getString(R.string.app_should_refresh_main_activity);

		this.saveBoolean(key, shouldRefresh, false);
	}

	/**
	 * Edit the frequency at which the text-to-speech should go off when an alarm
	 * is going off.
	 */
	public void editSpeakFrequency(int freq)
	{
		String key = this.resources.getString(R.string.speak_frequency_key);

		this.saveInt(key, freq, false);
	}

	/**
	 * Edit the flag indicating whether text-to-speech should be used when an
	 * alarm goes off.
	 */
	public void editSpeakToMe(boolean speak)
	{
		String key = this.resources.getString(R.string.speak_to_me_key);

		this.saveBoolean(key, speak, false);
	}

	/**
	 * Edit the default use dismiss early when an alarm is created.
	 */
	public void editUseDismissEarly(boolean useDismissEarly)
	{
		String key = this.resources.getString(R.string.alarm_use_dismiss_early_key);

		this.saveBoolean(key, useDismissEarly, false);
	}

	/**
	 * Edit whether the app was supported or not.
	 */
	public void editWasAppSupported(boolean wasSupported)
	{
		String key = this.resources.getString(R.string.key_app_supported);

		this.saveBoolean(key, wasSupported, false);
	}

	/**
	 * Edit whether the permission to ignore battery optimization was requested.
	 */
	public void editWasIgnoreBatteryOptimizationPermissionRequested(boolean requested)
	{
		String key = this.resources.getString(R.string.key_permission_ignore_battery_optimization_requested);

		this.saveBoolean(key, requested, false);
	}

	/**
	 * Edit whether the POST_NOTIFICATIONS permission was requested.
	 */
	public void editWasPostNotificationsPermissionRequested(boolean requested)
	{
		String key = this.resources.getString(R.string.key_permission_post_notifications_requested);

		this.saveBoolean(key, requested, false);
	}

	/**
	 * Edit whether the SCHEDULE_EXACT_ALARM permission was requested.
	 */
	public void editWasScheduleExactAlarmPermissionRequested(boolean requested)
	{
		String key = this.resources.getString(R.string.key_permission_schedule_exact_alarm_requested);

		this.saveBoolean(key, requested, false);
	}

	/**
	 * @return The AM color.
	 */
	public int getAmColor()
	{
		String key = this.resources.getString(R.string.am_color_key);
		int value = this.resources.getInteger(R.integer.default_am_color);

		return this.getInt(key, value);
	}

	/**
	 * Get the app's first run value.
	 *
	 * @return The app's first run value.
	 */
	public boolean getAppFirstRun(Context context)
	{
		String key = context.getString(R.string.app_first_run);
		boolean defaultValue = true;

		return this.getBoolean(key, defaultValue);
	}

	/**
	 * Get whether statistics should start to be collected or not.
	 *
	 * @return Whether statistics should start to be collected or not.
	 */
	public boolean getAppStartStatistics()
	{
		String key = this.resources.getString(R.string.app_start_statistics);
		boolean value = this.resources.getBoolean(R.bool.default_app_start_statistics);

		return this.getBoolean(key, value);
	}

	/**
	 * @return The audio source.
	 */
	public String getAudioSource()
	{
		String key = this.resources.getString(R.string.alarm_audio_source_key);
		String[] audioSources = this.resources.getStringArray(R.array.audio_sources);
		String value = audioSources[2];

		return this.getString(key, value);
	}

	/**
	 * @return Auto dismiss duration.
	 */
	public int getAutoDismiss()
	{
		String key = this.resources.getString(R.string.auto_dismiss_key);
		int value = this.resources.getInteger(R.integer.default_auto_dismiss_index);

		return this.getInt(key, value);
	}

	/**
	 * @return The summary text to use when displaying the auto dismiss widget.
	 */
	public static String getAutoDismissSummary(Resources res, int index)
	{
		String[] summaries = res.getStringArray(R.array.auto_dismiss_summaries);

		return summaries[index];
	}

	/**
	 * @see #getAutoDismissTime(int)
	 */
	public int getAutoDismissTime()
	{
		int index = this.getAutoDismiss();

		return NacSharedPreferences.getAutoDismissTime(index);
	}

	/**
	 * @return Calculate the auto dismiss duration from an index value,
	 *     corresponding to a location in the spainner widget.
	 */
	public static int getAutoDismissTime(int index)
	{
		return (index < 5) ? index : (index-4)*5;
	}

	/**
	 * @return A boolean value from the SharedPreferences instance.
	 */
	public boolean getBoolean(String key, boolean defValue)
	{
		return this.getInstance().getBoolean(key, defValue);
	}

	/**
	 * @return The alarm card height when it is collapsed.
	 */
	public int getCardHeightCollapsed()
	{
		String key = this.resources.getString(R.string.card_height_collapsed);
		int value = this.resources.getInteger(R.integer.default_card_height_collapsed);

		return this.getInt(key, value);
	}

	/**
	 * @return The alarm card height when it is collapsed, with dismiss showing.
	 */
	public int getCardHeightCollapsedDismiss()
	{
		String key = this.resources.getString(R.string.card_height_collapsed_dismiss);
		int value = this.resources.getInteger(R.integer.default_card_height_collapsed_dismiss);

		return this.getInt(key, value);
	}

	/**
	 * @return The alarm card height when it is expanded.
	 */
	public int getCardHeightExpanded()
	{
		String key = this.resources.getString(R.string.card_height_expanded);
		int value = this.resources.getInteger(R.integer.default_card_height_expanded);

		return this.getInt(key, value);
	}

	/**
	 * @return True if the alarm card has been measured, and False otherwise.
	 */
	public boolean getCardIsMeasured()
	{
		String key = this.resources.getString(R.string.card_is_measured);
		boolean value = this.resources.getBoolean(R.bool.default_card_is_measured);

		return this.getBoolean(key, value);
	}

	/**
	 * TODO: This is used in NacCalendar. Delete that and you're golden.
	 *
	 * @return The application context.
	 */
	public Context getContext()
	{
		return this.mContext;
	}

	/**
	 * @return Which style to use for the day buttons.
	 * <p>
	 *         1: Represents using the filled-in buttons (Default)
	 *         2: Represents the outlined button style
	 */
	public int getDayButtonStyle()
	{
		String key = this.resources.getString(R.string.day_button_style_key);
		int value = this.resources.getInteger(R.integer.default_day_button_style);

		return this.getInt(key, value);
	}

	/**
	 * @return The alarm days.
	 */
	public int getDays()
	{
		String key = this.resources.getString(R.string.alarm_days_key);
		int value = this.resources.getInteger(R.integer.default_days);

		return this.getInt(key, value);
	}

	/**
	 * @return The days color.
	 */
	public int getDaysColor()
	{
		String key = this.resources.getString(R.string.days_color_key);
		int value = this.resources.getInteger(R.integer.default_days_color);

		return this.getInt(key, value);
	}

	/**
	 * @return The index that corresponds to the time before an alarm goes off to start showing the
	 *         dismiss early button by.
	 */
	public int getDismissEarlyIndex()
	{
		int time = this.getDismissEarlyTime();

		return NacSharedPreferences.getDismissEarlyTimeToIndex(time);
	}

	/**
	 * Get the time before an alarm goes off to start showing the dismiss early button by.
	 *
	 * @param  index  The index that corresponds to the time.
	 */
	public static int getDismissEarlyIndexToTime(int index)
	{
		return (index < 5) ? index+1 : (index-3)*5;
	}

	/**
	 * @return The time before an alarm goes off to start showing the dismiss
	 *         early button by.
	 */
	public int getDismissEarlyTime()
	{
		String key = this.resources.getString(R.string.alarm_dismiss_early_time_key);
		int value = this.resources.getInteger(R.integer.default_dismiss_early_time);

		return this.getInt(key, value);
	}

	/**
	 * @return The index that corresponds to the time before an alarm goes off to start showing the
	 *         dismiss early button by.
	 */
	public static int getDismissEarlyTimeToIndex(int time)
	{
		return (time <= 5) ? time-1 : (time/5) + 3;
	}

	/**
	 * @return Whether easy snooze is enabled or not.
	 */
	public boolean getEasySnooze()
	{
		String key = this.resources.getString(R.string.easy_snooze_key);
		boolean value = this.resources.getBoolean(R.bool.default_easy_snooze);

		return this.getBoolean(key, value);
	}

	/**
	 * @return Whether a new alarm card should be expanded or not.
	 */
	public boolean getExpandNewAlarm()
	{
		String key = this.resources.getString(R.string.expand_new_alarm_key);
		boolean value = this.resources.getBoolean(R.bool.default_expand_new_alarm);

		return this.getBoolean(key, value);
	}

	/**
	 * @return The SharedPreferences instance.
	 */
	public SharedPreferences getInstance()
	{
		return this.mInstance;
	}

	/**
	 * @return An integer value from the SharedPreferences instance.
	 */
	public int getInt(String key, int defValue)
	{
		return this.getInstance().getInt(key, defValue);
	}

	/**
	 * @return The max number of snoozes.
	 */
	public int getMaxSnooze()
	{
		String key = this.resources.getString(R.string.max_snooze_key);
		int value = this.resources.getInteger(R.integer.default_max_snooze_index);

		return this.getInt(key, value);
	}

	/**
	 * Get the summary text to use when displaying the max snooze widget.
	 *
	 * @return The summary text to use when displaying the max snooze widget.
	 */
	public static String getMaxSnoozeSummary(Resources res, int index)
	{
		String[] summaries = res.getStringArray(R.array.max_snooze_summaries);

		return summaries[index];
	}

	/**
	 * @see #getMaxSnoozeValue(int)
	 */
	public int getMaxSnoozeValue()
	{
		int index = this.getMaxSnooze();

		return NacSharedPreferences.getMaxSnoozeValue(index);
	}

	/**
	 * @return Calculate the max snooze duration from an index corresponding
	 *     to a location in the spinner widget.
	 */
	public static int getMaxSnoozeValue(int index)
	{
		return (index == 11) ? -1 : index;
	}

	/**
	 * @return The media path.
	 */
	public String getMediaPath()
	{
		String key = this.resources.getString(R.string.alarm_sound_key);

		return this.getString(key, "");
	}

	/**
	 * @return The sound message.
	 */
	public static String getMediaMessage(Context context, String path)
	{
		//return !NacFile.isEmpty(path)
		return ((path != null) && (path.length() > 0))
			? NacMedia.getTitle(context, path)
			: context.getResources().getString(R.string.description_media);
	}

	/**
	 * @return True to display missed alarm notifications, and False otherwise.
	 */
	public boolean getMissedAlarmNotification()
	{
		String key = this.resources.getString(R.string.missed_alarm_key);
		boolean value = this.resources.getBoolean(R.bool.default_missed_alarm);

		return this.getBoolean(key, value);
	}

	/**
	 * @return The name of the alarm.
	 */
	public String getName()
	{
		String key = this.resources.getString(R.string.alarm_name_key);

		return this.getString(key, "");
	}

	/**
	 * @return The name color.
	 */
	public int getNameColor()
	{
		String key = this.resources.getString(R.string.name_color_key);
		int value = this.resources.getInteger(R.integer.default_name_color);

		return this.getInt(key, value);
	}

	/**
	 * Get the name message.
	 *
	 * @return The name message.
	 */
	public static String getNameMessage(Resources res, String name)
	{
		// Get the empty alarm name
		String emptyName = res.getString(R.string.alarm_name);

		return !name.isEmpty() ? name : emptyName;
	}

	/**
	 * @return Whether the display next alarm should show time remaining for
	 *         the next alarm or not.
	 */
	public int getNextAlarmFormat()
	{
		String key = this.resources.getString(R.string.next_alarm_format_key);
		int value = this.resources.getInteger(R.integer.default_next_alarm_format_index);

		return this.getInt(key, value);
	}

	/**
	 * @return The PM color.
	 */
	public int getPmColor()
	{
		String key = this.resources.getString(R.string.pm_color_key);
		int value = this.resources.getInteger(R.integer.default_pm_color);

		return this.getInt(key, value);
	}

	/**
	 * @return The previous version of the app.
	 * <p>
	 * Normally, this should be the same as the current version, but when an
	 * install occurs, these values will differ.
	 */
	public String getPreviousAppVersion()
	{
		String key = this.resources.getString(R.string.previous_app_version);

		return this.getString(key, "");
	}

	/**
	 * @return The previous system volume, before an alarm goes off.
	 */
	public int getPreviousVolume()
	{
		String key = this.resources.getString(R.string.sys_previous_volume);
		int value = this.resources.getInteger(R.integer.default_previous_volume);

		return this.getInt(key, value);
	}

	/**
	 * @return The app's rating counter.
	 */
	public int getRateMyAppCounter()
	{
		String key = this.resources.getString(R.string.app_rating_counter);
		int value = this.resources.getInteger(R.integer.default_rate_my_app_counter);

		return this.getInt(key, value);
	}

	/**
	 * @return Whether the alarm should be repeated or not.
	 */
	public boolean getRepeat()
	{
		String key = this.resources.getString(R.string.alarm_repeat_key);
		boolean value = this.resources.getBoolean(R.bool.default_repeat);

		return this.getBoolean(key, value);
	}

	/**
	 * @return Whether volume should be gradually increased or not.
	 */
	public boolean getShouldGraduallyIncreaseVolume()
	{
		String key = this.resources.getString(R.string.alarm_should_gradually_increase_volume_key);
		boolean value = this.resources.getBoolean(R.bool.default_should_gradually_increase_volume);

		return this.getBoolean(key, value);
	}

	/**
	 * @return Whether the main activity should be refreshed or not.
	 */
	public boolean getShouldRefreshMainActivity()
	{
		String key = this.resources.getString(R.string.app_should_refresh_main_activity);
		boolean value = this.resources.getBoolean(R.bool.default_app_should_refresh_main_activity);

		return this.getBoolean(key, value);
	}

	/**
	 * @return Whether volume should be restricted or not.
	 */
	public boolean getShouldRestrictVolume()
	{
		String key = this.resources.getString(R.string.alarm_should_restrict_volume_key);
		boolean value = this.resources.getBoolean(R.bool.default_should_restrict_volume);

		return this.getBoolean(key, value);
	}

	/**
	 * @return Whether the alarm information should be shown or not.
	 */
	public boolean getShowAlarmInfo()
	{
		String key = this.resources.getString(R.string.show_alarm_info_key);
		boolean value = this.resources.getBoolean(R.bool.default_show_alarm_info);

		return this.getBoolean(key, value);
	}

	/**
	 * @return The shuffle status.
	 */
	public boolean getShuffle()
	{
		String key = this.resources.getString(R.string.shuffle_playlist_key);
		boolean value = this.resources.getBoolean(R.bool.default_shuffle_playlist);

		return this.getBoolean(key, value);
	}

	/**
	 * @return The snooze duration.
	 */
	public int getSnoozeDuration()
	{
		String key = this.resources.getString(R.string.snooze_duration_key);
		int value = this.resources.getInteger(R.integer.default_snooze_duration_index);

		return this.getInt(key, value);
	}

	/**
	 * Get the summary text for the snooze duration widget.
	 *
	 * @return The summary text for the snooze duration widget.
	 */
	public static String getSnoozeDurationSummary(Resources res, int index)
	{
		String[] summaries = res.getStringArray(R.array.snooze_duration_summaries);

		return summaries[index];
	}

	/**
	 * @see #getSnoozeDurationValue(int)
	 */
	public int getSnoozeDurationValue()
	{
		int index = this.getSnoozeDuration();

		return NacSharedPreferences.getSnoozeDurationValue(index);
	}

	/**
	 * @return Calculate the snooze duration from an index value, corresponding
	 *     to a location in the spainner widget.
	 */
	public static int getSnoozeDurationValue(int index)
	{
		return (index < 4) ? index+1 : (index-3)*5;
	}

	/**
	 * @return The speak frequency value.
	 */
	public int getSpeakFrequency()
	{
		String key = this.resources.getString(R.string.speak_frequency_key);
		int value = this.resources.getInteger(R.integer.default_speak_frequency_index);

		return this.getInt(key, value);
	}

	/**
	 * @return The speak to me value.
	 */
	public boolean getSpeakToMe()
	{
		String key = this.resources.getString(R.string.speak_to_me_key);
		boolean value = this.resources.getBoolean(R.bool.default_speak_to_me);

		return this.getBoolean(key, value);
	}

	/**
	 * @return The value indicating which day to start on.
	 */
	public int getStartWeekOn()
	{
		String key = this.resources.getString(R.string.start_week_on_key);
		int value = this.resources.getInteger(R.integer.default_start_week_on_index);

		return this.getInt(key, value);
	}

	/**
	 * @return A string value from the SharedPreferences instance.
	 */
	public String getString(String key, String defValue)
	{
		return this.getInstance().getString(key, defValue);
	}

	/**
	 * @return The theme color.
	 */
	public int getThemeColor()
	{
		String key = this.resources.getString(R.string.theme_color_key);
		int value = this.resources.getInteger(R.integer.default_theme_color);

		return this.getInt(key, value);
	}

	/**
	 * @return The time color.
	 */
	public int getTimeColor()
	{
		String key = this.resources.getString(R.string.time_color_key);
		int value = this.resources.getInteger(R.integer.default_time_color);

		return this.getInt(key, value);
	}

	/**
	 * @return True to display upcoming alarm notifications, and False otherwise.
	 */
	public boolean getUpcomingAlarmNotification()
	{
		String key = this.resources.getString(R.string.upcoming_alarm_key);
		boolean value = this.resources.getBoolean(R.bool.default_upcoming_alarm);

		return this.getBoolean(key, value);
	}

	/**
	 * @return Whether dismiss early should be used or not.
	 */
	public boolean getUseDismissEarly()
	{
		String key = this.resources.getString(R.string.alarm_use_dismiss_early_key);
		boolean value = this.resources.getBoolean(R.bool.default_use_dismiss_early);

		return this.getBoolean(key, value);
	}

	/**
	 * @return Whether NFC is required or not.
	 */
	public boolean getUseNfc()
	{
		String key = this.resources.getString(R.string.alarm_use_nfc_key);
		boolean value = this.resources.getBoolean(R.bool.default_use_nfc);

		return this.getBoolean(key, value);
	}

	/**
	 * @return Whether the alarm should vibrate the phone or not.
	 */
	public boolean getVibrate()
	{
		String key = this.resources.getString(R.string.alarm_vibrate_key);
		boolean value = this.resources.getBoolean(R.bool.default_vibrate);

		return this.getBoolean(key, value);
	}

	/**
	 * @return The alarm volume level.
	 */
	public int getVolume()
	{
		String key = this.resources.getString(R.string.alarm_volume_key);
		int value = this.resources.getInteger(R.integer.default_volume);

		return this.getInt(key, value);
	}

	/**
	 * Get whether the app was supported or not.
	 *
	 * @return Whether the app was supported or not.
	 */
	public boolean getWasAppSupported()
	{
		String key = this.resources.getString(R.string.key_app_supported);
		boolean value = this.resources.getBoolean(R.bool.default_was_app_supported);

		return this.getBoolean(key, value);
	}

	/**
	 * Get whether the permission to ignore battery optimization was requested.
	 *
	 * @return Whether the permission to ignore battery optimization was
	 *         requested.
	 */
	public boolean getWasIgnoreBatteryOptimizationPermissionRequested()
	{
		String key = this.resources.getString(R.string.key_permission_ignore_battery_optimization_requested);
		boolean value = this.resources.getBoolean(R.bool.default_was_ignore_battery_optimization_permission_requested);

		return this.getBoolean(key, value);
	}

	/**
	 * Get whether the POST_NOTIFICATIONS permission was requested.
	 *
	 * @return Whether the POST_NOTIFICATIONS permission was requested.
	 */
	public boolean getWasPostNotificationsPermissionRequested()
	{
		String key = this.resources.getString(R.string.key_permission_post_notifications_requested);
		boolean value = this.resources.getBoolean(R.bool.default_was_post_notifications_permission_requested);

		return this.getBoolean(key, value);
	}

	/**
	 * Get whether the SCHEDULE_EXACT_ALARM permission was requested.
	 *
	 * @return Whether the SCHEDULE_EXACT_ALARM permission was requested.
	 */
	public boolean getWasScheduleExactAlarmPermissionRequested()
	{
		String key = this.resources.getString(R.string.key_permission_schedule_exact_alarm_requested);
		boolean value = this.resources.getBoolean(R.bool.default_was_schedule_exact_alarm_permission_requested);

		return this.getBoolean(key, value);
	}

	/**
	 * Increment the rate my app counter.
	 */
	public void incrementRateMyApp()
	{
		int counter = this.getRateMyAppCounter();
		this.editRateMyAppCounter(counter+1);
	}

	/**
	 * @return True if app has reached the counter limit, and False otherwise.
	 */
	public boolean isRateMyAppLimit()
	{
		int counter = this.getRateMyAppCounter();
		int limit = this.resources.getInteger(R.integer.default_rate_my_app_limit);

		return counter >= limit;
	}

	/**
	 * @return True if app has been rated, and False otherwise.
	 */
	public boolean isRateMyAppRated()
	{
		int counter = this.getRateMyAppCounter();
		int rated = this.resources.getInteger(R.integer.default_rate_my_app_rated);

		return counter == rated;
	}

	/**
	 * Set the rate my app counter to the rated value.
	 */
	public void ratedRateMyApp()
	{
		int rated = this.resources.getInteger(R.integer.default_rate_my_app_rated);

		this.editRateMyAppCounter(rated);
	}

	/**
	 * @see #save(SharedPreferences.Editor, boolean)
	 */
	public void save(SharedPreferences.Editor editor)
	{
		this.save(editor, false);
	}

	/**
	 * Save the changes that were made to the shared preference.
	 */
	public void save(SharedPreferences.Editor editor, boolean commit)
	{
		if (commit)
		{
			editor.commit();
		}
		else
		{
			editor.apply();
		}
	}

	/**
	 * Save a boolean to the shared preference.
	 */
	public void saveBoolean(String key, boolean value, boolean commit)
	{
		SharedPreferences.Editor editor = this.getInstance().edit()
			.putBoolean(key, value);

		this.save(editor, commit);
	}

	/**
	 * Save an int to the shared preference.
	 */
	public void saveInt(String key, int value, boolean commit)
	{
		SharedPreferences.Editor editor = this.getInstance().edit()
			.putInt(key, value);

		this.save(editor, commit);
	}

	/**
	 * Save a string to the shared preference.
	 */
	public void saveString(String key, String value, boolean commit)
	{
		SharedPreferences.Editor editor = this.getInstance().edit()
			.putString(key, value);

		this.save(editor, commit);
	}

}
