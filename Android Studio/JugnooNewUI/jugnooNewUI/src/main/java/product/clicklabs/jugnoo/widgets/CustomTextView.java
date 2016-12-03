package product.clicklabs.jugnoo.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.widget.TextView;

/**
 * Created by aneeshbansal on 10/05/16.
 */
public class CustomTextView extends TextView {

	public CustomTextView(Context context) {
		super(context);
		demo();

	}

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		demo();
	}

	public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		demo();
	}

	private void demo(){
		ViewTreeObserver vto = CustomTextView.this.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (1 < CustomTextView.this.getLineCount() && CustomTextView.this.getTextSize() > 10) {
					CustomTextView.this.setTextSize(TypedValue.COMPLEX_UNIT_PX, CustomTextView.this.getTextSize() - 2);
				}
			}
		});
	}

}
