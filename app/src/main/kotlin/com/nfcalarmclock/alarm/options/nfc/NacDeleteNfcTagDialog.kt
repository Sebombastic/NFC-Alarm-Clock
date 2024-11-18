package com.nfcalarmclock.alarm.options.nfc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import com.nfcalarmclock.R
import com.nfcalarmclock.view.dialog.NacBottomSheetDialogFragment

/**
 * Ask the user if they want to delete an NFC tag.
 */
class NacDeleteNfcTagDialog
	: NacBottomSheetDialogFragment()
{

	/**
	 * Listener for deleting an NFC tag.
	 */
	fun interface OnDeleteNfcTagListener
	{
		fun onDelete()
	}

	/**
	 * Listener for when the NFC tag is saved.
	 */
	var onDeleteNfcTagListener: OnDeleteNfcTagListener? = null

	/**
	 * Called when the dialog view is created.
	 */
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?)
		: View?
	{
		return inflater.inflate(R.layout.dlg_delete_nfc_tag, container, false)
	}

	/**
	 * Called when the dialog view is created.
	 */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		// Super
		super.onViewCreated(view, savedInstanceState)

		// Get the views
		val deleteButton: MaterialButton = view.findViewById(R.id.delete_nfc_tag)
		val cancelButton: MaterialButton = view.findViewById(R.id.cancel_nfc_tag)

		// Setup the delete button
		setupPrimaryButton(deleteButton, listener = {

			// Call the listener
			onDeleteNfcTagListener?.onDelete()

			// Dismiss the dialog
			dismiss()

		})

		// Setup the cancel button
		setupSecondaryButton(cancelButton)
	}

	companion object
	{

		/**
		 * Tag for the class.
		 */
		const val TAG = "NacDeleteNfcTagDialog"

	}

}