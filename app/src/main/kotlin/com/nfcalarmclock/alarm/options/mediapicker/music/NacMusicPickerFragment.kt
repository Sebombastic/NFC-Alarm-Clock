package com.nfcalarmclock.alarm.options.mediapicker.music

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import com.nfcalarmclock.R
import com.nfcalarmclock.alarm.db.NacAlarm
import com.nfcalarmclock.alarm.options.mediapicker.NacMediaPickerFragment
import com.nfcalarmclock.system.file.NacFile
import com.nfcalarmclock.system.file.browser.NacFileBrowser
import com.nfcalarmclock.system.file.browser.NacFileBrowser.OnBrowserClickedListener
import com.nfcalarmclock.system.permission.readmediaaudio.NacReadMediaAudioPermission
import com.nfcalarmclock.util.NacBundle
import com.nfcalarmclock.util.media.NacMedia
import com.nfcalarmclock.util.addMediaInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Display a browser for the user to browse for music files.
 */
@UnstableApi
class NacMusicPickerFragment

	// Constructor
	: NacMediaPickerFragment(),

	// Interfaces
	OnBrowserClickedListener
{

	/**
	 * Scroll view containing all the file browser contents.
	 */
	private var scrollView: ScrollView? = null

	/**
	 * Text view showing the current directory.
	 */
	private var directoryTextView: TextView? = null

	/**
	 * File browser.
	 */
	var fileBrowser: NacFileBrowser? = null
		private set

	/**
	 * Determine the starting directory and file name that should be selected.
	 */
	@OptIn(UnstableApi::class)
	private fun getInitialFileBrowserLocation(): Pair<String, String>
	{
		val context = requireContext()
		var dir = ""
		var name = ""

		// Check if the media is a file
		if (NacMedia.isFile(context, mediaPath))
		{
			// Get the URI
			val uri = Uri.parse(mediaPath)

			// Set the directory and name
			dir = NacMedia.getRelativePath(context, uri)
			name = NacMedia.getName(context, uri)
		}
		// Check if the media is a directory
		else if (NacMedia.isDirectory(mediaPath))
		{
			dir = mediaPath
		}

		return Pair(dir, name)
	}

	/**
	 * Called when the Clear button is clicked.
	 */
	@UnstableApi
	override fun onClearClicked()
	{
		// Super
		super.onClearClicked()

		// De-select whatever is selected
		fileBrowser?.deselect(requireContext())
	}

	/**
	 * Called when the view is being created.
	 */
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?): View?
	{
		return inflater.inflate(R.layout.frg_music, container, false)
	}

	/**
	 * Called when a directory is clicked in the file browser.
	 */
	@UnstableApi
	override fun onDirectoryClicked(browser: NacFileBrowser, path: String)
	{
		// Build the path to show in the directory text view
		val textPath = if (path.isNotEmpty())
		{
			"${path}/"
		}
		else
		{
			""
		}

		// Set the alarm media path
		mediaPath = if(browser.previousDirectory.isNotEmpty())
		{
			"${textPath}${browser.previousDirectory}"
		}
		else
		{
			path
		}

		// Set the text path
		directoryTextView!!.text = textPath

		// Show the contents of the directory
		browser.show(path)
	}

	/**
	 * Called when a directory is done being shown in the file browser.
	 */
	override fun onDoneShowing(browser: NacFileBrowser)
	{
		lifecycleScope.launch {

			// Delay a little bit
			delay(50)

			// Check if the previous directory was clicked
			if (browser.previousDirectory.isNotEmpty())
			{
				val context = requireContext()

				// Select the view
				browser.select(context, browser.previousDirectory)

				// Make sure the selected view has been set
				if (browser.selectedView != null)
				{
					// Get the location and offset of the view
					val loc = IntArray(2)
					val offset = 4 * directoryTextView!!.height

					browser.selectedView!!.getLocationOnScreen(loc)

					// Calculate the Y location
					val y = if (offset <= loc[1]) loc[1] - offset else 0

					// Scroll to the view's location
					scrollView?.scrollTo(0, y)
				}
			}
			else
			{
				// Scroll to the top
				scrollView?.fullScroll(View.FOCUS_UP)
			}

		}
	}

	/**
	 * Called when a file is clicked in the file browser.
	 */
	@UnstableApi
	override fun onFileClicked(browser: NacFileBrowser, metadata: NacFile.Metadata)
	{
		val uri = metadata.toExternalUri()

		// File was selected
		if (browser.isSelected)
		{
			 // Play the file
			 play(uri)
		}
		// File was deselected
		else
		{
			// Stop any media that is already playing
			mediaPlayer?.exoPlayer?.stop()
		}
	}

	/**
	 * Called when the Ok button is clicked.
	 */
	override fun onOkClicked()
	{
		// Check if the a directory was selected. If so, show a warning.
		// The path has already been set in onBrowserClicked() so nothing
		// further needs to be done
		if (NacMedia.isDirectory(mediaPath))
		{
			showWarningDirectorySelected()
			return
		}

		// Super
		super.onOkClicked()
	}

	/**
	 * Called after the view is created.
	 */
	@UnstableApi
	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		// Setup the action buttons
		setupActionButtons(view)

		// Check if the user has read media permissions
		if (!NacReadMediaAudioPermission.hasPermission(requireContext()))
		{
			return
		}

		// Set the scrollview
		scrollView = view.findViewById(R.id.scrollview)

		// Set the textview with the directory path
		directoryTextView = view.findViewById(R.id.path)

		// Setup the file browser
		setupFileBrowser(view)
	}

	/**
	 * Setup the file browser.
	 */
	private fun setupFileBrowser(root: View)
	{
		// Create and set the file browser
		val container: LinearLayout = root.findViewById(R.id.container)
		fileBrowser = NacFileBrowser(this, container)

		// Directory to show in the textview and the ame of the file to select
		// in the file browser
		val (dir, name) = getInitialFileBrowserLocation()

		// Set the text with the path to the directory
		directoryTextView!!.text = dir

		// Setup the file browser
		fileBrowser!!.onBrowserClickedListener = this
		fileBrowser!!.show(dir) {

			// Select the item once it is done being shown
			fileBrowser!!.select(requireContext(), name)

		}
	}

	/**
	 * Show a warning indicating that a music directory was selected.
	 */
	private fun showWarningDirectorySelected()
	{
		// Create the dialog
		val dialog = NacDirectorySelectedWarningDialog()

		// Setup the dialog
		dialog.defaultShouldShuffleMedia = shuffleMedia
		dialog.defaultShouldRecursivelyPlayMedia = recursivelyPlayMedia

		// Listener for when the user has confirmed that they want to select a directory
		dialog.onDirectoryConfirmedListener = NacDirectorySelectedWarningDialog.OnDirectoryConfirmedListener { shuffleMedia, recursivelyPlayMedia ->

			// Set the shuffle and recursive play media attributes
			this.shuffleMedia = shuffleMedia
			this.recursivelyPlayMedia = recursivelyPlayMedia

			// Emulate OK click
			super.onOkClicked()

		}

		// Show the dialog
		dialog.show(childFragmentManager, NacDirectorySelectedWarningDialog.TAG)
	}

	companion object
	{

		/**
		 * Read request callback success result.
		 */
		const val READ_REQUEST_CODE = 1

		/**
		 * Create a new instance of this fragment.
		 */
		fun newInstance(alarm: NacAlarm?): Fragment
		{
			// Create the fragment
			val fragment: Fragment = NacMusicPickerFragment()

			// Add the bundle to the fragment
			fragment.arguments = NacBundle.alarmToBundle(alarm)

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
			val fragment: Fragment = NacMusicPickerFragment()

			// Add the bundle to the fragment
			fragment.arguments = Bundle().addMediaInfo(mediaPath, mediaArtist, mediaTitle,
				mediaType, shuffleMedia, recursivelyPlayMedia)

			return fragment
		}

	}

}