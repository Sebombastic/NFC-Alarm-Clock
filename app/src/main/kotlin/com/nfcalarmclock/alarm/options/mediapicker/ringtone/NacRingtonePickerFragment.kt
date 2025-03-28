package com.nfcalarmclock.alarm.options.mediapicker.ringtone

import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.media3.common.util.UnstableApi
import com.nfcalarmclock.R
import com.nfcalarmclock.alarm.db.NacAlarm
import com.nfcalarmclock.util.media.NacMedia
import com.nfcalarmclock.alarm.options.mediapicker.NacMediaPickerFragment
import com.nfcalarmclock.shared.NacSharedPreferences
import com.nfcalarmclock.util.addAlarm
import com.nfcalarmclock.util.addMediaInfo

/**
 * Display a dialog that shows a list of alarm ringtones.
 */
@UnstableApi
class NacRingtonePickerFragment
	: NacMediaPickerFragment()
{

	/**
	 * Radio group.
	 */
	private var radioGroup: RadioGroup? = null

	/**
	 * Create a radio button.
	 */
	private fun createRadioButton(title: String, path: String): RadioButton
	{
		// Inflate the view
		val inflater = LayoutInflater.from(context)
		val view = inflater.inflate(R.layout.radio_button_ringtone, radioGroup, true)

		// Get the radio button
		val button = view.findViewById<RadioButton>(R.id.radio_button_ringtone)

		// Generate an ID for the radio button
		val id = View.generateViewId()

		// Setup the radio button
		button.id = id
		button.text = title
		button.tag = path

		// Set the on click listener
		button.setOnClickListener {

			// Get the URI
			val uri = Uri.parse(path)

			// Play the media at the URI
			play(uri)

		}

		return button
	}

	/**
	 * Called when the Clear button is clicked.
	 */
	override fun onClearClicked()
	{
		// Super
		super.onClearClicked()

		// Clear the radio button that is checked
		radioGroup!!.clearCheck()
	}

	/**
	 * Called when the view will be created.
	 */
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?): View?
	{
		return inflater.inflate(R.layout.frg_ringtone, container, false)
	}

	/**
	 * Called after the view is created.
	 */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		// Set the radio group
		radioGroup = view.findViewById(R.id.radio_group)

		// Setup the action buttons
		setupActionButtons(view)

		// Setup the radio buttons
		setupRadioButtons()
	}

	/**
	 * Set the radio button's color state list.
	 */
	private fun setRadioButtonColor(shared: NacSharedPreferences,
		radioButton: RadioButton)
	{
		// Get the colors for the boolean states
		val colors = intArrayOf(shared.themeColor, Color.GRAY)

		// Get the IDs of the two states
		val states = arrayOf(intArrayOf(android.R.attr.state_checked),
			intArrayOf(-android.R.attr.state_checked))

		// Set the state list of the radio button
		radioButton.buttonTintList = ColorStateList(states, colors)
	}

	/**
	 * Setup the radio buttons.
	 */
	private fun setupRadioButtons()
	{
		// Get all the ringtones
		val context = requireContext()
		val ringtones = NacMedia.getRingtones(context)
		val shared = NacSharedPreferences(context)

		// Iterate over each ringtone
		for ((title, path) in ringtones)
		{
			// Skip if path is empty
			if (path.isEmpty())
			{
				continue
			}

			// Create a button for each ringtone
			val button = createRadioButton(title, path)

			// Set the radio button color
			setRadioButtonColor(shared, button)

			// Check if the paths match
			if (path == mediaPath)
			{
				// Set the radio button as selected
				button.isChecked = true
			}
		}
	}

	companion object
	{

		/**
		 * Create a new instance of this fragment.
		 */
		fun newInstance(alarm: NacAlarm?): Fragment
		{
			// Create the fragment
			val fragment: Fragment = NacRingtonePickerFragment()

			// Add the bundle to the fragment
			fragment.arguments = Bundle().addAlarm(alarm)

			return fragment
		}

		/**
		 * Create a new instance of this fragment.
		 */
		fun newInstance(
			mediaPath: String,
			mediaArtist: String,
			mediaTitle: String,
			mediaType: Int,
			shuffleMedia: Boolean,
			recursivelyPlayMedia: Boolean
		): Fragment
		{
			// Create the fragment
			val fragment: Fragment = NacRingtonePickerFragment()

			// Add the bundle to the fragment
			fragment.arguments = Bundle().addMediaInfo(mediaPath, mediaArtist, mediaTitle,
				mediaType, shuffleMedia, recursivelyPlayMedia)

			return fragment
		}

	}

}