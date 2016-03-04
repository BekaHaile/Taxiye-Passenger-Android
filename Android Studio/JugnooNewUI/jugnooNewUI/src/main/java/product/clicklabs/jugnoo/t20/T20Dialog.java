package product.clicklabs.jugnoo.t20;

import android.app.Activity;
import android.app.Dialog;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by shankar on 3/4/16.
 */
public class T20Dialog {



	public static void show(Activity activity, String title, String message) {
		try {
				final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.dialog_custom_one_button);

				FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, false);

				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);


				TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.mavenRegular(activity));
				TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.mavenLight(activity));

				textMessage.setMovementMethod(new ScrollingMovementMethod());
				textMessage.setMaxHeight((int)(800.0f*ASSL.Yscale()));

				textHead.setText(title);
				textMessage.setText(message);

				textHead.setVisibility(View.GONE);

				Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.mavenRegular(activity));

				btnOk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}

				});

				dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
