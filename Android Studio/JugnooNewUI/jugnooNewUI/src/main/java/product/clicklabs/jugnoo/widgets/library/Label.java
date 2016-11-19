package product.clicklabs.jugnoo.widgets.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Fonts;


public class Label extends LinearLayout {

	private Context context;
	private TextView tvLabel;
	private OnClickCrossListener listenerOnCrossClick;
	private OnLabelClickListener listenerOnLabelClick;
	private LabelValues labelValues;

	public Label(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public Label(Context context, int padding) {
		super(context);
		init(context, padding);
	}

	private void init(final Context context, int padding) {
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View labelView = inflater.inflate(R.layout.layout_label_cuisine, this, true);

		LinearLayout llLabel = (LinearLayout) labelView.findViewById(R.id.llLabel);
		llLabel.setPadding(padding, padding, padding, padding);

		llLabel.setClickable(true);
		llLabel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(labelValues.getSelected() == 0){
					labelValues.setSelected(1);
				} else {
					labelValues.setSelected(0);
				}
				setText(labelValues);
				if (listenerOnLabelClick != null) {
					listenerOnLabelClick.onClickLabel((Label) labelView);
				}
			}
		});

		tvLabel = (TextView) labelView.findViewById(R.id.tvLabel);
		tvLabel.setTypeface(Fonts.mavenMedium(context));

		ImageView ivCross = (ImageView) labelView.findViewById(R.id.ivCross);
		ivCross.setVisibility(GONE);

	}

	public String getText() {
		return labelValues.getValue();
	}

	public int getSelected() {
		return labelValues.getSelected();
	}

	public void setText(LabelValues labelValues) {
		this.labelValues = labelValues;
		tvLabel.setText(labelValues.getValue());
		if (labelValues.getSelected() == 1) {
			tvLabel.setBackgroundResource(R.drawable.background_theme_round);
			tvLabel.setTextColor(context.getResources().getColor(R.color.white));
		} else {
			tvLabel.setBackgroundResource(R.drawable.background_white_rounded_bordered);
			tvLabel.setTextColor(context.getResources().getColor(R.color.text_color));
		}
	}

	/**
	 * Set a callback listener when the cross icon is clicked.
	 *
	 * @param listener Callback instance.
	 */
	public void setOnClickCrossListener(OnClickCrossListener listener) {
		this.listenerOnCrossClick = listener;
	}

	/**
	 * Interface for a callback listener when the cross icon is clicked.
	 */
	public interface OnClickCrossListener {

		/**
		 * Call when the cross icon is clicked.
		 */
		void onClickCross(Label label);
	}

	/**
	 * Set a callback listener when the {@link Label} is clicked.
	 *
	 * @param listener Callback instance.
	 */
	public void setOnLabelClickListener(OnLabelClickListener listener) {
		this.listenerOnLabelClick = listener;
	}

	/**
	 * Interface for a callback listener when the {@link Label} is clicked.
	 * Container Activity/Fragment must implement this interface.
	 */
	public interface OnLabelClickListener {

		/**
		 * Call when the {@link Label} is clicked.
		 */
		void onClickLabel(Label label);
	}
}
