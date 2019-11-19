package product.clicklabs.jugnoo.home.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 5/2/16.
 */
public class PushDialog {

	private final String TAG = PushDialog.class.getSimpleName();
	private static final long INIT_DELAY = 3000;
	private static final long DELAY = 1000;
	private static final int PERCENTAGE_CONSTANT = 100;
	private Activity activity;
	private Callback callback;
	private ProgressWheel progressWheel;
	private Dialog dialog = null;
    private String title = "";
    private boolean isVideoViewSet = false, isVideoPaused = false;
	private int mCurrentposition = 0;
	private Runnable updateCurrentPosition;
	private ScheduledExecutorService mScheduledExecutorService;
	private VideoView simpleVideoView;
	private RelativeLayout rlSimpleVideoView;
	private int duration, watchedVideo = 0;

	public PushDialog(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}

	public PushDialog show() {
		try {
			String pushDialogContent = Prefs.with(activity).getString(Constants.SP_PUSH_DIALOG_CONTENT,
					Constants.EMPTY_JSON_OBJECT);
			JSONObject jObj = new JSONObject(pushDialogContent);
			if(!pushDialogContent.equalsIgnoreCase(Constants.EMPTY_JSON_OBJECT)
					&& jObj.optInt(Constants.KEY_SHOW_DIALOG, 0) == 1){
				title = jObj.optString(Constants.KEY_TITLE, activity.getResources().getString(R.string.app_name));
				String message = jObj.optString(Constants.KEY_MESSAGE, "");
				final int deepindex = jObj.optInt(Constants.KEY_DEEPINDEX, -1);
				final int restaurantId = jObj.optInt(Constants.KEY_RESTAURANT_ID, -1);
				String picture = jObj.optString(Constants.KEY_PICTURE, "");
				if("".equalsIgnoreCase(picture)){
					picture = jObj.optString(Constants.KEY_IMAGE, "");
				}
				String buttonText = jObj.optString(Constants.KEY_BUTTON_TEXT, activity.getString(R.string.ok));
				final String url =  jObj.optString(Constants.KEY_URL, "");
				final String video = "https://www.radiantmediaplayer.com/media/bbb-360p.mp4"; //jObj.optString(Constants.KEY_VIDEO, "");

				dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.dialog_push);


				RelativeLayout relative = dialog.findViewById(R.id.relative);
				new ASSL(activity, relative, 1134, 720, false);

				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);

				LinearLayout linearLayoutInner = dialog.findViewById(R.id.linearLayoutInner);
				ImageView imageView = dialog.findViewById(R.id.imageView);
				rlSimpleVideoView = dialog.findViewById(R.id.rlSimpleVideoView);
				simpleVideoView = dialog.findViewById(R.id.simpleVideoView);
				progressWheel = dialog.findViewById(R.id.progressWheel);
				progressWheel.setVisibility(View.GONE);
				TextView textViewTitle = dialog.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
				TextView textViewMessage = dialog.findViewById(R.id.textViewMessage); textViewMessage.setTypeface(Fonts.mavenRegular(activity));
				final Button button = dialog.findViewById(R.id.button);button.setTypeface(Fonts.mavenRegular(activity));
				ImageView imageViewClose = dialog.findViewById(R.id.imageViewClose);

				textViewTitle.setText(title);
				textViewMessage.setText(message);
				button.setText(buttonText);


				try {
					if(!"".equalsIgnoreCase(picture) && Utils.isImageFile(picture)) {
						rlSimpleVideoView.setVisibility(View.GONE);
						Picasso.with(activity).load(picture)
								.placeholder(R.drawable.ic_notification_placeholder)
								.error(R.drawable.ic_notification_placeholder)
								.into(imageView);
						isVideoViewSet = false;
					} else if ((!"".equalsIgnoreCase(video) && Utils.isVideoFile(video)) || (!"".equalsIgnoreCase(picture) && Utils.isVideoFile(picture))) {
						rlSimpleVideoView.setVisibility(View.VISIBLE);
						imageView.setVisibility(View.GONE);
						simpleVideoView.setSecure(true);
						simpleVideoView.setVideoPath(!video.isEmpty() ? video : picture);
						progressWheel.setVisibility(View.VISIBLE);
						isVideoViewSet = true;

						MediaController mediaController = new MediaController(new ContextThemeWrapper(activity, R.style.MediaController), false);
						mediaController.setAnchorView(simpleVideoView);
						//Setting MediaController and URI, then starting the videoView
						simpleVideoView.setMediaController(mediaController);
						simpleVideoView.requestFocus();
						simpleVideoView.start();


						updateCurrentPosition = () -> {
							mCurrentposition = (simpleVideoView.getCurrentPosition() * PERCENTAGE_CONSTANT) / simpleVideoView.getDuration();
						};

						mScheduledExecutorService = Executors.newScheduledThreadPool(1);
						mScheduledExecutorService.scheduleWithFixedDelay(() -> simpleVideoView.post(updateCurrentPosition), INIT_DELAY, DELAY, TimeUnit.MILLISECONDS);

						simpleVideoView.setOnPreparedListener(mp -> {
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									duration = (int)TimeUnit.MILLISECONDS.toMillis(simpleVideoView.getDuration());
									long multi = (watchedVideo * duration) / PERCENTAGE_CONSTANT;
									simpleVideoView.seekTo((int) multi);
								}
							}, 400);

							LinearLayout viewGroupLevel1 = (LinearLayout)  mediaController.getChildAt(0);
							//Set your color with desired transparency here:
							viewGroupLevel1.setBackgroundColor(activity.getResources().getColor(R.color.transparent));

							mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
								@Override
								public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
									((ViewGroup) mediaController.getParent()).removeView(mediaController);
									((FrameLayout) dialog.findViewById(R.id.videoViewWrapper)).addView(mediaController);
									mediaController.setVisibility(View.VISIBLE);
								}
							});
							mp.setOnSeekCompleteListener(mp1 -> {
								Prefs.with(activity).save("videoTime", 0);
								progressWheel.setVisibility(View.GONE);
								if(isVideoPaused) {
									mp1.pause();
									isVideoPaused = false;
								} else {
									mp1.start();
								}
							});
						});

						simpleVideoView.setOnCompletionListener(mediaPlayer -> simpleVideoView.seekTo(0));

					}else{
						isVideoViewSet = false;
						imageView.setVisibility(View.GONE);
						rlSimpleVideoView.setVisibility(View.GONE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Prefs.with(activity).save(Constants.SP_PUSH_DIALOG_CONTENT,
								Constants.EMPTY_JSON_OBJECT);
						callback.onButtonClicked(deepindex, url, restaurantId);
						dialog.dismiss();
					}
				});

				View.OnClickListener onClickListener = new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Prefs.with(activity).save(Constants.SP_PUSH_DIALOG_CONTENT,
								Constants.EMPTY_JSON_OBJECT);
						dialog.dismiss();
					}
				};

				imageViewClose.setOnClickListener(onClickListener);
				relative.setOnClickListener(onClickListener);

				linearLayoutInner.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				});

				dialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	private void showLoading() {
		if(progressWheel != null && isVideoViewSet) {
			progressWheel.setVisibility(View.VISIBLE);
		}
	}

	public void onResume() {
		if(progressWheel != null && isVideoViewSet) {
			watchedVideo = Prefs.with(activity).getInt("videoTime", 0);
			showLoading();
			simpleVideoView.start();
		}
	}

	public void onPause() {
		if(simpleVideoView != null && isVideoViewSet) {
			Prefs.with(activity).save("videoTime", (simpleVideoView.getCurrentPosition() * PERCENTAGE_CONSTANT) / duration);
		}
	}

	public void onActivityResult() {
		if(simpleVideoView != null && isVideoViewSet) {
			simpleVideoView.pause();
			isVideoPaused = true;
		}
	}

	public void dismiss(boolean clearDialogContent){
		if(clearDialogContent){
			Prefs.with(activity).save(Constants.SP_PUSH_DIALOG_CONTENT,
					Constants.EMPTY_JSON_OBJECT);
			Prefs.with(activity).save("videoTime", 0);
		}
		if(dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}


	public interface Callback{
		void onButtonClicked(int deepIndex, String url, int restaurantId);
	}

}