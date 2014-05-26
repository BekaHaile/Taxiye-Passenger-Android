package product.clicklabs.jugnoo;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.Chronometer;

public class PausableChronometer extends Chronometer {

	private long eclipsedTime;

	public PausableChronometer(Context context) {
		super(context);
		init();
	}

	public PausableChronometer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PausableChronometer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void start() {
		setBase(SystemClock.elapsedRealtime() - eclipsedTime);
		super.start();
	}

	public void restart() {
		stop();
		this.eclipsedTime = 0l;
		start();
	}

	public void stop() {
		this.eclipsedTime = SystemClock.elapsedRealtime() - this.getBase();
		super.stop();
	}

	public long stopAndReturnEclipsedTime() {
		stop();
		return this.eclipsedTime;
	}

	private void init() {
		this.eclipsedTime = 0l;
		this.setOnChronometerTickListener(new OnChronometerTickListener() {
			NumberFormat formatter = new DecimalFormat("00");

			@Override
			public void onChronometerTick(Chronometer arg0) {
				float countUp = (SystemClock.elapsedRealtime() - arg0.getBase()) / 1000;
				String asText = formatter.format(countUp / 60) + ":"
						+ formatter.format(countUp % 60);
				setText(asText);
			}
		});
	}
}
