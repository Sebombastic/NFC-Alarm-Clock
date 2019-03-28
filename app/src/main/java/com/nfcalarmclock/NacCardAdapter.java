package com.nfcalarmclock;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Alarm card adapter.
 */
public class NacCardAdapter
	extends RecyclerView.Adapter<NacCardHolder>
	implements View.OnClickListener,
		NacAlarm.OnChangeListener,
		NacCardHolder.OnDeleteListener,
		NacCardTouchHelper.Adapter
{

	/**
	 * Main activity root view.
	 */
	private CoordinatorLayout mRoot;

	/**
	 * RecyclerView containing list of alarm cards.
	 */
	private RecyclerView mRecyclerView;

	/**
	 * Alarm scheduler.
	 */
	private NacScheduler mScheduler;

	/**
	 * The database.
	 */
	private NacDatabase mDatabase;

	/**
	 * The alarm to restore, when prompted after deletion.
	 */
	private NacCardUndo mUndo;

	/**
	 * Handle card swipe events.
	 */
	private NacCardTouchHelper mTouchHelper;

	/**
	 * List of alarms.
	 */
	private List<NacAlarm> mAlarmList;

	/**
	 * Indicator that the alarm was added through the floating action button.
	 */
	private boolean mWasAdded;

	/**
	 */
	public NacCardAdapter(Context context)
	{
		AppCompatActivity activity = (AppCompatActivity) context;
		NacCardTouchHelper.Callback callback =
			new NacCardTouchHelper.Callback(this);

		this.mRoot = (CoordinatorLayout) activity.findViewById(
			R.id.activity_main);
		this.mRecyclerView = (RecyclerView) this.getRoot().findViewById(
			R.id.content_alarm_list);
		this.mTouchHelper = new NacCardTouchHelper(callback);
		this.mUndo = new NacCardUndo();
		this.mDatabase = new NacDatabase(context);
		this.mScheduler = new NacScheduler(context);
		this.mAlarmList = null;
		this.mWasAdded = false;
	}

	/**
	 * Add an alarm.
	 */
	public void add()
	{
		if (this.size() >= NacSharedPreferences.DEFAULT_MAX_ALARMS)
		{
			NacUtility.quickToast(this.getRoot(),
				"Max number of alarms created");
			return;
		}

		Context context = this.getContext();
		boolean format = DateFormat.is24HourFormat(context);
		int id = this.getUniqueId();
		NacSharedPreferences shared = new NacSharedPreferences(context);
		NacAlarm alarm = new NacAlarm.Builder()
			.setId(id)
			.setRepeat(shared.getRepeat())
			.setDays(shared.getDays())
			.setVibrate(shared.getVibrate())
			.setSound(shared.getSound())
			.setName(shared.getName())
			.set24HourFormat(format)
			.build();

		this.add(alarm);
	}

	/**
	 * @see add
	 *
	 * @param  alarm  The alarm to add.
	 */
	public void add(NacAlarm alarm)
	{
		int index = this.size();

		this.add(alarm, index);
		this.getRecyclerView().scrollToPosition(index);
	}

	/**
	 * @see add
	 *
	 * @param  alarm  The alarm to add.
	 * @param  position  The position to add the alarm.
	 */
	public void add(NacAlarm alarm, int position)
	{
		if (this.getDatabase().add(alarm) < 0)
		{
			NacUtility.snackbar(this.getRoot(),
				"Error occurred when adding alarm to database.",
				"DISMISS", null);
			return;
		}

		// Using update instead of add for testing. Things should never get
		// canceled in update, only added
		this.mWasAdded = true;

		this.getScheduler().update(alarm);
		this.getAlarms().add(position, alarm);
		notifyItemInserted(position);
		//this.refresh(position);
	}

	/**
	 * Build the alarm list.
	 */
	public void build()
	{
		this.mAlarmList = this.getDatabase().read();

		this.getTouchHelper().setRecyclerView(this.getRecyclerView());
		this.getTouchHelper().reset();
		notifyDataSetChanged();
	}

	/**
	 * Copy the alarm.
	 *
	 * @param  pos	The position of the alarm card to copy.
	 */
	public void copy(int position)
	{
		NacAlarm alarm = this.get(position);
		NacAlarm copy = alarm.copy();
		int newPosition = this.size();

		copy.setId(this.getUniqueId());
		notifyItemChanged(position);
		this.add(copy);
		this.undo(copy, newPosition, NacCardUndo.Type.COPY);
		NacUtility.snackbar(this.getRoot(), "Copied alarm.", "UNDO", this);
	}

	/**
	 * Delete the alarm at the given position.
	 *
	 * @param  pos	The position of the alarm card to delete.
	 */
	public void delete(int position)
	{
		NacAlarm alarm = this.get(position);
		int firstVisible = this.getFirstVisible(position);
		int lastVisible = this.getLastVisible(position);

		this.getScheduler().cancel(alarm);
		this.getDatabase().delete(alarm);
		this.getAlarms().remove(position);
		notifyItemRemoved(position);

		if ((firstVisible >= 0) && (lastVisible >= 0))
		{
			notifyItemRangeChanged(firstVisible, lastVisible);
			//this.refresh(position);
		}

		this.undo(alarm, position, NacCardUndo.Type.DELETE);
		NacUtility.snackbar(this.getRoot(), "Deleted alarm.", "UNDO", this);
	}

	/**
	 * @return The alarm at the given index.
	 */
	public NacAlarm get(int index)
	{
		return this.getAlarms().get(index);
	}

	/**
	 * @return The list of alarms.
	 */
	private List<NacAlarm> getAlarms()
	{
		return this.mAlarmList;
	}

	/**
	 * @return The context.
	 */
	private Context getContext()
	{
		return this.getRoot().getContext();
	}

	/**
	 * @return The alarm database.
	 */
	private NacDatabase getDatabase()
	{
		return this.mDatabase;
	}

	/**
	 * @return The first visible view holder in the recycler view.
	 */
	private int getFirstVisible(int position)
	{
		int size = this.size();

		for (int i=position-1; i >= 0; i--)
		{
			if (this.getRecyclerView().findViewHolderForAdapterPosition(i)
				== null)
			{
				return i+1;
			}
		}

		return 0;
	}

	/**
	 * @return The number of items in the recycler view.
	 */
	@Override
	public int getItemCount()
	{
		return this.getAlarms().size();
	}

	/**
	 * @return The last visible view holder in the recycler view.
	 */
	private int getLastVisible(int position)
	{
		int size = this.size();

		for (int i=position; i < size; i++)
		{
			if (this.getRecyclerView().findViewHolderForAdapterPosition(i)
				== null)
			{
				return i-1;
			}
		}

		return size-1;
	}

	/**
	 * @return The RecyclerView.
	 */
	private RecyclerView getRecyclerView()
	{
		return this.mRecyclerView;
	}

	/**
	 * @return The root view.
	 */
	private View getRoot()
	{
		return this.mRoot;
	}

	/**
	 * @return The alarm scheduler.
	 */
	private NacScheduler getScheduler()
	{
		return this.mScheduler;
	}

	/**
	 * @return The touch helper.
	 */
	private NacCardTouchHelper getTouchHelper()
	{
		return this.mTouchHelper;
	}

	/**
	 * Determine a unique integer ID number to use for newly created alarms.
	 */
	private int getUniqueId()
	{
		List<Integer> used = new ArrayList<>();

		for (NacAlarm a : this.getAlarms())
		{
			used.add(a.getId());
		}

		for (int i=1; i < Integer.MAX_VALUE; i+=7)
		{
			if (!used.contains(i))
			{
				return i;
			}
		}

		return -1;
	}

	/**
	 * Refresh cards on screen, around the given position.
	 */
	private void refresh(int position)
	{
		int size = this.size();
		int start = 0;
		int end = size;

		for (int i=0; i < position; i++)
		{
			if (this.getRecyclerView().findViewHolderForAdapterPosition(i)
				!= null)
			{
				start = i;
				break;
			}
		}

		for (int i=position; i < size; i++)
		{
			if (this.getRecyclerView().findViewHolderForAdapterPosition(i)
				== null)
			{
				end = i-1;
				break;
			}
		}

		notifyItemRangeChanged(start, end-start);
	}

	/**
	 * Setup the alarm card.
	 *
	 * @param  card  The alarm card.
	 * @param  pos	The position of the alarm card.
	 */
	@Override
	public void onBindViewHolder(final NacCardHolder card, int pos)
	{
		NacAlarm alarm = this.get(pos);

		alarm.setOnChangeListener(this);
		card.init(alarm, this.mWasAdded);
		card.setOnDeleteListener(this);

		this.mWasAdded = false;
	}

	/**
	 * Update the database when alarm data is changed.
	 *
	 * @param  a  The alarm that was changed.
	 */
	@Override
	public void onChange(NacAlarm a)
	{
		this.getDatabase().update(a);
		this.getScheduler().update(a);
	}

	/**
	 * Capture the click event on the delete button, and delete the card it
	 * belongs to.
	 *
	 * @param  v  The view that was clicked.
	 */
	@Override
	public void onClick(View v)
	{
		NacAlarm alarm = this.mUndo.alarm;
		int position = this.mUndo.position;
		NacCardUndo.Type type = this.mUndo.type;

		this.mUndo.reset();

		if (type == NacCardUndo.Type.COPY)
		{
			this.delete(position);
		}
		else if (type == NacCardUndo.Type.DELETE)
		{
			this.restore(alarm, position);
		}
		else if (type == NacCardUndo.Type.RESTORE)
		{
			this.delete(position);
		}
	}

	/**
	 * Create the view holder.
	 *
	 * @param  parent  The parent view.
	 * @param  viewType  The type of view.
	 */
	@Override
	public NacCardHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		Context context = parent.getContext();
		int layout = R.layout.view_card_alarm;
		View root = LayoutInflater.from(context).inflate(layout, parent,
			false);

		return new NacCardHolder(root);
	}

	/**
	 * Delete the alarm.
	 */
	@Override
	public void onDelete(int pos)
	{
		this.delete(pos);
	}

	/**
	 * Copy the alarm.
	 *
	 * @param  pos	The position of the alarm to copy.
	 */
	@Override
	public void onItemCopy(int position)
	{
		//this.mTouchHelper.reset();
		//notifyItemChanged(pos);
		//this.refresh(position);
		this.copy(position);
	}

	/**
	 * Delete the alarm.
	 * 
	 * @param  pos	The position of the alarm to delete.
	 */
	@Override
	public void onItemDelete(int position)
	{
		//notifyItemChanged(pos);
		//this.refresh(position);
		this.delete(position);
	}

	/**
	 * Drag and reorder the alarm.
	 *
	 * @param  from  The position that the alarm is moving from.
	 * @param  to  The position the alarm is moving to.
	 */
	@Override
	public void onItemMove(int fromIndex, int toIndex)
	{
		NacAlarm fromAlarm = this.get(fromIndex);
		NacAlarm toAlarm = this.get(toIndex);

		Collections.swap(this.getAlarms(), fromIndex, toIndex);
		this.getScheduler().cancel(fromAlarm);
		this.getScheduler().cancel(toAlarm);
		this.getDatabase().swap(fromAlarm, toAlarm);
		this.getScheduler().add(fromAlarm);
		this.getScheduler().add(toAlarm);
		notifyItemMoved(fromIndex, toIndex);
	}

	/**
	 * Measure the expanded and collapsed sizes of the alarm card.
	 */
	@Override
	public void onViewAttachedToWindow(NacCardHolder card)
	{
		card.measure();
	}

	/**
	 * Restore a previously deleted alarm.
	 * 
	 * @param  alarm  The alarm to restore.
	 * @param  position  The position to insert the alarm.
	 */
	public void restore(NacAlarm alarm, int position)
	{
		this.add(alarm, position);
		this.undo(alarm, position, NacCardUndo.Type.RESTORE);
		NacUtility.snackbar(this.getRoot(), "Restored alarm.", "UNDO", this);
	}

	/**
	 * @return The number of elements in the adapter.
	 */
	public int size()
	{
		return this.getAlarms().size();
	}

	/**
	 * Save undo parameters.
	 */
	public void undo(NacAlarm alarm, int position, NacCardUndo.Type type)
	{
		this.mUndo.set(alarm, position, type);
	}

}
