package com.nfcalarmclock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Environment;
import android.view.View;
import java.io.IOException;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Music dialog when selecting an alarm sound.
 */
public class NacMusicDialog
	extends NacMediaDialog
	implements View.OnClickListener,NacPermissions.OnResultListener,NacDialog.OnShowListener
{

	/**
	 * Request value for Read permissions.
	 */
	private static final int NAC_MUSIC_DIALOG_READ_REQUEST = 1;

	/**
	 */
	public NacMusicDialog()
	{
		super();

		this.addOnShowListener(this);
	}

	/**
	 * Add a directory entry to the dialog.
	 */
	public void addDirectoryEntry(NacButtonGroup container,
		String text, File file)
	{
		Context context = container.getContext();
		NacImageTextButton entry = new NacImageTextButton(context);
		int size = (int) context.getResources().getDimension(R.dimen.isz_dlg_music);
		int spacing = (int) context.getResources().getDimension(R.dimen.sp_dlg_music);

		entry.setTag(file);
		entry.setOnClickListener(this);
		container.add(entry);
		entry.setImageBackground(R.mipmap.baseline_folder_white_48dp);
		entry.setText(text);
	}

	/**
	 * Add a music file entry to the dialog.
	 */
	public void addFileEntry(NacButtonGroup container, String text, File file)
	{
		if (file.length() == 0)
		{
			return;
		}

		Context context = container.getContext();
		NacImageSubTextButton entry = new NacImageSubTextButton(context);
		int size = (int) context.getResources().getDimension(R.dimen.isz_dlg_music);
		int spacing = (int) context.getResources().getDimension(R.dimen.sp_dlg_music);

		String title = NacMedia.getTitle(file);
		String artist = NacMedia.getArtist(file);

		if (title.isEmpty())
		{
			return;
		}

		entry.setTag(file);
		entry.setOnClickListener(this);
		container.add(entry);
		entry.setImageBackground(R.mipmap.baseline_play_arrow_white_48dp);
		entry.setTextTitle(title);
		entry.setTextSubtitle(artist);
	}

	/**
	 * Build using a layout for displaying music.
	 */
	@Override
	public AlertDialog.Builder build(Context context)
	{
		return this.build(context, R.layout.dlg_sound_music);
	}

	/**
	 * @return A listing of music files and directories under a given path.
	 *         Directories will be listed first, before files.
	 */
	public List<File> getDirectoryListing(String path)
	{
		File[] listing = new File(path).listFiles(NacMedia.getFilter());
		List<File> directories = new ArrayList<>();
		List<File> files = new ArrayList<>();

		for (int i=0; (listing != null) && (i < listing.length); i++)
		{
			if (listing[i].isDirectory())
			{
				directories.add(listing[i]);
			}
			else if (listing[i].isFile())
			{
				files.add(listing[i]);
			}
		}

		Collections.sort(directories);
		Collections.sort(files);
		directories.addAll(files);

		return directories;
	}

	/**
	 * Build the dialog.
	 */
	@Override
	public void onBuildDialog(Context context, AlertDialog.Builder builder)
	{
		super.onBuildDialog(context, builder);

		String title = context.getString(R.string.dlg_music_title);

		builder.setTitle(title);
	}

	/**
	 * Called when an entry is clicked.
	 */
	@Override
	public void onClick(View view)
	{
		File file = (File) view.getTag();
		String name = file.getName();
		String path;

		try
		{
			path = file.getCanonicalPath();
		}
		catch (IOException e)
		{
			NacUtility.printf("IOException occurred when trying to getCanonicalPath().");
			return;
		}

		if (file.isDirectory())
		{
			this.showDirectory(path);
		}
		else if (file.isFile())
		{
			this.play(path, name);
		}
	}

	/**
	 * Called after the user selects an option to allow/deny READ permissions.
	 */
	@Override
	public void onResult(int request, String[] permissions, int[] grant)
	{
		if (request == NAC_MUSIC_DIALOG_READ_REQUEST)
		{
			if ((grant.length > 0)
				&& (grant[0] == PackageManager.PERMISSION_GRANTED))
			{
				this.show();
				this.scale();
			}
			else
			{
				this.dismiss();
			}
		}
	}

	/**
	 * Setup views for when the dialog is shown.
	 */
	@Override
	public void onShowDialog(NacDialog dialog, View root)
	{
		Context context = root.getContext();

		if (!NacPermissions.hasRead(context))
		{
			NacPermissions.request(context,
				Manifest.permission.READ_EXTERNAL_STORAGE,
				NAC_MUSIC_DIALOG_READ_REQUEST);
			NacPermissions.setResultListener(context, this);
			this.hide();
			return;
		}

		this.showDirectory(null);
	}

	/**
	 * Scale the dialog window.
	 */
	@Override
	public void scale()
	{
		this.scale(0.8, 0.8, false, true);
	}

	/**
	 * Show directory contents in the dialog.
	 */
	public void showDirectory(String path)
	{
		View root = this.getRoot();
		NacButtonGroup container = (NacButtonGroup) root.findViewById(R.id.group);
		String mainpath = Environment.getExternalStorageDirectory().toString();
		path = (path == null) ? mainpath : path;
		List<File> files = this.getDirectoryListing(path);

		container.removeAllViews();

		if (!path.equals(mainpath))
		{
			this.addDirectoryEntry(container, "(Previous folder)",
				new File(path+"/.."));
		}

		for (File f : files)
		{
			if (f.isDirectory())
			{
				this.addDirectoryEntry(container, f.getName(), f);
			}
			else if (f.isFile())
			{
				this.addFileEntry(container, f.getName(), f);
			}
		}
	}

}
