package com.nfcalarmclock.system.permission.postnotifications

import android.os.Build
import androidx.annotation.RequiresApi
import com.nfcalarmclock.R
import com.nfcalarmclock.system.permission.NacPermissionRequestDialog

/**
 * Dialog to request to allow posting notifications.
 */
@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
class NacPostNotificationsPermissionRequestDialog
	: NacPermissionRequestDialog()
{

	/**
	 * The name of the permission.
	 */
	override val permission: String = NacPostNotificationsPermission.permissionName

	/**
	 * The ID of the layout.
	 */
	override val layoutId: Int = R.layout.dlg_request_post_notifications_permission

	/**
	 * The ID of the title string.
	 */
	override val titleId: Int = R.string.title_request_permission_post_notifications

	/**
	 * The ID of the text string.
	 */
	override val textId: Int = R.string.message_permission_post_notifications_request

	/**
	 * The actions to execute when the permission request is accepted.
	 */
	override fun doPermissionRequestAccepted()
	{
		// Set the flag that the permission was requested
		sharedPreferences!!.wasPostNotificationsPermissionRequested = true

		// Call the accepeted listeners
		super.doPermissionRequestAccepted()
	}

	/**
	 * The actions to execute when the permission request is canceled.
	 */
	override fun doPermissionRequestCanceled()
	{
		// Set the flag that the permission was requested
		sharedPreferences!!.wasPostNotificationsPermissionRequested = true

		// Call the canceled listeners
		super.doPermissionRequestCanceled()
	}

	companion object
	{

		/**
		 * Tag for the class.
		 */
		const val TAG = "NacPostNotificationsPermissionDialog"

	}

}