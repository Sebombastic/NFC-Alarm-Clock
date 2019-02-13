package com.nfcalarmclock;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * @brief The alarm name. Users can change the name upon clicking the view.
 */
public class NacCardName
	implements View.OnClickListener,NacDialog.OnDismissListener
{

	/**
	 * Alarm.
	 */
	 private NacAlarm mAlarm;

	/**
	 * Name view.
	 */
	 private NacImageTextButton mName = null;

	/**
	 * Text of days to repeat.
	 */
	private TextView mTextView = null;

	/**
	 */
	public NacCardName(View root)
	{
		super();

		this.mAlarm = null;
		this.mName = (NacImageTextButton) root.findViewById(R.id.nacName);
		this.mTextView = (TextView) root.findViewById(R.id.nacRepeatTextName);

		this.mName.setOnClickListener(this);
	}

	/**
	 * Initialize the name.
	 */
	public void init(NacAlarm alarm)
	{
		this.mAlarm = alarm;

		this.setName();
	}

	/**
	 * Display the dialog that allows users to set the name of thee alarm.
	 */
	@Override
	public void onClick(View v)
	{
		NacNameDialog dialog = new NacNameDialog();
		Context context = v.getContext();

		dialog.build(context, R.layout.dlg_alarm_name);
		dialog.addDismissListener(this);
		dialog.show();
	}

	/**
	 * Notify alarm listener that the alarm has been modified.
	 */
	@Override
	public boolean onDismissDialog(NacDialog dialog)
	{
		Object data = dialog.getData();
		String name = (data != null) ? (String) data : "";

		this.mAlarm.setName(name);
		this.setName();
		this.mAlarm.changed();

		return true;
	}

	/**
	 * Set the name of the alarm.
	 */
	public void setName()
	{
		String name = this.mAlarm.getName();
		String text = name+"  ";
		boolean focus = true;

		if (name.isEmpty())
		{
			name = NacAlarm.getNameMessage();
			text = "";
			focus = false;
		}

		NacUtility.printf("Name : %s", name);
		this.mName.setText(name);
		this.mName.setFocus(focus);
		this.mTextView.setText(text);
	}

}
