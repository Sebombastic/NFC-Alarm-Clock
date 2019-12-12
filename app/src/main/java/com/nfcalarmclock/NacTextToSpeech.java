package com.nfcalarmclock;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import java.util.Locale;

/**
 * Text to speech.
 */
public class NacTextToSpeech
	implements TextToSpeech.OnInitListener,
		AudioManager.OnAudioFocusChangeListener
{

	/**
	 * On speaking listener.
	 */
	public interface OnSpeakingListener
	{

		/**
		 * Called when speech engine is done speaking.
		 */
		public void onDoneSpeaking();

		/**
		 * Called when speech engine has started speaking.
		 */
		public void onStartSpeaking();
	}

	/**
	 */
	public static class NacUtteranceListener
		extends UtteranceProgressListener
	{

		/**
		 * Context.
		 */
		private Context mContext;

		/**
		 * Audio focus change listener.
		 */
		private AudioManager.OnAudioFocusChangeListener
			mOnAudioFocusChangeListener;

		/**
		 * On speaking listener.
		 */
		private OnSpeakingListener mOnSpeakingListener;

		/**
		 */
		public NacUtteranceListener(Context context,
			AudioManager.OnAudioFocusChangeListener listener)
		{
			this.mContext = context;
			this.mOnAudioFocusChangeListener = listener;
		}

		/**
		 * @return The context.
		 */
		private Context getContext()
		{
			return this.mContext;
		}

		/**
		 * @return This audio focus change listener.
		 */
		private AudioManager.OnAudioFocusChangeListener
			getOnAudioFocusChangeListener()
		{
			return this.mOnAudioFocusChangeListener;
		}

		/**
		 * @return The on speaking listener.
		 */
		private OnSpeakingListener getOnSpeakingListener()
		{
			return this.mOnSpeakingListener;
		}

		/**
		 */
		@Override
		public void onDone(String utteranceId)
		{
			NacAudio.abandonAudioFocus(this.getContext(),
				this.getOnAudioFocusChangeListener());

			if (this.getOnSpeakingListener() != null)
			{
				this.getOnSpeakingListener().onDoneSpeaking();
			}
		}

		/**
		 */
		@Override
		public void onStart(String utteranceId)
		{
			if (this.getOnSpeakingListener() != null)
			{
				this.getOnSpeakingListener().onStartSpeaking();
			}
		}

		/**
		 */
		@SuppressWarnings("deprecation")
		@Override
		public void onError(String utteranceId)
		{
			NacUtility.printf("onError! %s", utteranceId);
		}

		/**
		 * Set on speaking listener.
		 */
		public void setOnSpeakingListener(OnSpeakingListener listener)
		{
			this.mOnSpeakingListener = listener;
		}

	}

	/**
	 * The context.
	 */
	private Context mContext;

	/**
	 * The speech engine.
	 */
	private TextToSpeech mSpeech;

	/**
	 * Message buffer to speak.
	 */
	private String mBuffer;

	/**
	 * Check if speech engine is initialized.
	 */
	private boolean mInitialized;

	/**
	 * The utterance listener.
	 */
	private NacUtteranceListener mUtterance;

	/**
	 * Audio attributes.
	 */
	private NacAudio.Attributes mAudioAttributes;

	/**
	 * Utterance ID when speaking through the TTS engine.
	 */
	public static final String UTTERANCE_ID = "NacAlarmTts";

	/**
	 */
	public NacTextToSpeech(Context context, OnSpeakingListener listener)
	{
		NacSharedPreferences shared = new NacSharedPreferences(context);
		this.mContext = context;
		this.mSpeech = null;
		this.mBuffer = "";
		this.mInitialized = false;
		this.mUtterance = new NacUtteranceListener(context, this);
		this.mAudioAttributes = new NacAudio.Attributes(context,
			shared.getAudioSource());

		this.setOnSpeakingListener(listener);
	}

	/**
	 * @return The speech buffer.
	 */
	private String getBuffer()
	{
		return this.mBuffer;
	}

	/**
	 * @return The context.
	 */
	private Context getContext()
	{
		return this.mContext;
	}

	/**
	 * @return The speech engine.
	 */
	private TextToSpeech getTextToSpeech()
	{
		return this.mSpeech;
	}

	/**
	 * @return The utterance listener.
	 */
	private NacUtteranceListener getUtteranceListener()
	{
		return this.mUtterance;
	}

	/**
	 * @return True if there is a buffer present and False otherwise.
	 */
	public boolean hasBuffer()
	{
		String buffer = this.getBuffer();

		return ((buffer != null) && (!buffer.isEmpty()));
	}

	/**
	 * @return True if the speech engine is initialized and False otherwise.
	 */
	public boolean isInitialized()
	{
		return (this.mInitialized && (this.mSpeech != null));
	}

	/**
	 * @return True if the speech engine is running, and False otherwise.
	 */
	public boolean isSpeaking()
	{
		TextToSpeech speech = this.getTextToSpeech();

		return (this.isInitialized() && speech.isSpeaking());
	}

	/**
	 * Change media state when audio focus changes.
	 */
	@Override
	public void onAudioFocusChange(int focusChange)
	{
		String change = "UNKOWN";

		if (focusChange == AudioManager.AUDIOFOCUS_GAIN)
		{
			change = "GAIN";
		}
		else if (focusChange == AudioManager.AUDIOFOCUS_LOSS)
		{
			change = "LOSS";
		}
		else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)
		{
			change = "LOSS_TRANSIENT";
		}
		else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK)
		{
			change = "LOSS_TRANSIENT_CAN_DUCK";
		}
	}

	/**
	 */
	@Override
	public void onInit(int status)
	{
		this.mInitialized = (status == TextToSpeech.SUCCESS);

		if (this.isInitialized())
		{
			TextToSpeech speech = this.getTextToSpeech();

			speech.setLanguage(Locale.US);
			//this.mSpeech.setLanguage(Locale.US);

			if (this.hasBuffer())
			{
				String buffer = this.getBuffer();

				this.speak(buffer);
				this.setBuffer("");
			}
		}
	}

	/**
	 * Set the speech message buffer.
	 */
	private void setBuffer(String buffer)
	{
		this.mBuffer = buffer;
	}

	/**
	 * Set on speaking listener.
	 */
	public void setOnSpeakingListener(OnSpeakingListener listener)
	{
		this.getUtteranceListener().setOnSpeakingListener(listener);
	}

	/**
	 * Shutdown the speech engine.
	 */
	public void shutdown()
	{
		TextToSpeech speech = this.getTextToSpeech();

		if (speech != null)
		{
			speech.shutdown();
			//this.mSpeech.shutdown();

			this.mSpeech = null;
		}
	}

	/**
	 * @see speak
	 */
	public void speak(String message)
	{
		this.speak(message, this.mAudioAttributes);
	}

	/**
	 * Speak the given text.
	 */
	public void speak(String message, NacAudio.Attributes attrs)
	{
		Context context = this.getContext();
		TextToSpeech speech = this.getTextToSpeech();
		this.mAudioAttributes = attrs;

		if (speech == null)
		{
			//this.mSpeech = new TextToSpeech(context, this);
			speech = new TextToSpeech(context, this);
			this.mSpeech = speech;

			speech.setOnUtteranceProgressListener(this.mUtterance);
			//this.mSpeech.setOnUtteranceProgressListener(this.mUtterance);
		}

		if (this.isInitialized())
		{
			if(!NacAudio.requestAudioFocusTransient(context, this, attrs))
			{
				NacUtility.printf("Audio Focus TRANSIENT NOT Granted!");
				return;
			}

			Bundle bundle = NacBundle.toBundle(attrs);

			//this.mSpeech.speak(message, TextToSpeech.QUEUE_FLUSH,
			speech.speak(message, TextToSpeech.QUEUE_FLUSH, bundle,
				UTTERANCE_ID);
		}
		else
		{
			this.setBuffer(message);
		}
	}

	/**
	 * Stop the speech engine.
	 */
	public void stop()
	{
		TextToSpeech speech = this.getTextToSpeech();

		if (speech != null)
		{
			speech.stop();
			//this.mSpeech.stop();
		}
	}

}
