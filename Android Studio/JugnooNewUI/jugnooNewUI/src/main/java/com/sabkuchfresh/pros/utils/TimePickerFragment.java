package com.sabkuchfresh.pros.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

import product.clicklabs.jugnoo.R;

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
		TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
		try {
			timePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE,getString(R.string.ok),timePickerDialog);
			timePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE,getString(R.string.dismiss),timePickerDialog);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timePickerDialog;
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// Do something with the time chosen by the user
		if(onTimeSetListener != null){
			onTimeSetListener.onTimeSet(view, hourOfDay, minute);
		}
	}

	public void show(FragmentManager manager, String tag, TimePickerDialog.OnTimeSetListener onTimeSetListener) {
		this.onTimeSetListener = onTimeSetListener;
		/*try {
			final Calendar c = Calendar.getInstance();
			if(getArguments().containsKey(ADDITIONAL_TIME_MINUTES)){
                c.add(Calendar.MINUTE,getArguments().getInt(ADDITIONAL_TIME_MINUTES));
            }
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			((TimePickerDialog)getDialog()).updateTime(hour, minute);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		super.show(manager, tag);
	}
}