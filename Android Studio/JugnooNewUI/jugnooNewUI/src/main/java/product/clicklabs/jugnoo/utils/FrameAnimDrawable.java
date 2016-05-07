package product.clicklabs.jugnoo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

/**
 * Created by shankar on 5/3/16.
 */
public class FrameAnimDrawable {

	public Bitmap[] bitmaps;
	public int loadedCount;
	private int duration = 1000;
	private ImageView imageView;
	private AnimationDrawable animation;
	private ArrayList<CustomTarget> customTargets;

	public FrameAnimDrawable(Context context, ArrayList<String> images, ImageView imageView){
		bitmaps = new Bitmap[images.size()];
		customTargets = new ArrayList<>();
		loadedCount = 0;
		this.imageView = imageView;
		animation = new AnimationDrawable();
		float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
		int size = (int) (111f * minRatio);
		for(int i=0; i<images.size(); i++){
			CustomTarget customTarget = new CustomTarget(i);
			customTargets.add(customTarget);
			Picasso.with(context).load(images.get(i))
					.resize(size, size)
					.centerCrop()
					.into(customTargets.get(i));
		}

	}

	private void checkIfLoaded(){
		if(loadedCount == bitmaps.length){
			animation.setOneShot(false);
			for(int i=0; i<bitmaps.length; i++){
				if(bitmaps[i] != null) {
					BitmapDrawable frame = new BitmapDrawable(bitmaps[i]);
					animation.addFrame(frame, duration);
				}
			}
			if(bitmaps.length > 1) {
				imageView.setImageDrawable(animation);
				imageView.post(new Runnable() {
					@Override
					public void run() {
						animation.start();
					}
				});
			} else if(bitmaps.length == 1 && bitmaps[0] != null){
				imageView.setImageBitmap(bitmaps[0]);
			}
		}
	}


	class CustomTarget implements Target {

		int position;

		public CustomTarget(int position){
			this.position = position;
		}

		@Override
		public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
			bitmaps[position] = bitmap;
			loadedCount++;
			checkIfLoaded();
		}

		@Override
		public void onBitmapFailed(Drawable drawable) {
			loadedCount++;
			checkIfLoaded();
		}

		@Override
		public void onPrepareLoad(Drawable drawable) {

		}
	}




}
