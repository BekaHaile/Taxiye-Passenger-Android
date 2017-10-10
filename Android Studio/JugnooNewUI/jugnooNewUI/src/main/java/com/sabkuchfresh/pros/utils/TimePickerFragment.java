package com.sabkuchfresh.pros.utils;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by shankar on 20/06/17.
 */

public class TimePickerFragment extends DialogFragment
		implements TimePickerDialog.OnTimeSetListener {

	public static final String ADDITIONAL_TIME_MINUTES = "additonal_time_minutes";
	private TimePickerDialog.OnTimeSetListener onTimeSetListener;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();

        if(getArguments().containsKey(ADDITIONAL_TIME_MINUTES)){
            c.add(Calendar.MINUTE,getArguments().getInt(ADDITIONAL_TIME_MINUTES));
        }
        int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// Do something with the time chosen by the user
		if(onTimeSetListener != null){
			onTimeSetListener.onTimeSet(view, hourOfDay, minute);
		}
	}

	public void show(FragmentManager manager, String tag, TimePickerDialog.OnTimeSetListener onTimeSetListener) {
		this.onTimeSetListener = onTimeSetListener;
		super.show(manager, tag);
	}
}