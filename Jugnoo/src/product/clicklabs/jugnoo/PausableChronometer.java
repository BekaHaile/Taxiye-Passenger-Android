package product.clicklabs.jugnoo;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.Chronometer;

public class PausableChronometer extends Chronometer {

	  public long eclipsedTime;
	  public boolean isRunning;

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
	    this.isRunning = true;
	  }

	  public void restart() {
	    stop();
	    this.eclipsedTime = 0l;
	    start();
	    
	  }

	  public void stop() {
		  if(this.isRunning){
			  this.eclipsedTime = SystemClock.elapsedRealtime() - this.getBase();
			  super.stop();
			  this.isRunning = false;
		  }
	  }

	  private void init() {
	    this.eclipsedTime = 0l;
	    this.isRunning = false;
	  }
	}
