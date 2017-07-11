package com.sabkuchfresh.pros.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by shankar on 19/06/17.
 */

public class DatePickerFragment extends DialogFragment
		implements DatePickerDialog.OnDateSetListener {

	private DatePickerDialog.OnDateSetListener onDateSetListener;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
//		if(c.get(Calendar.HOUR_OF_DAY) > 20){
			c.add(Calendar.DAY_OF_MONTH, 1);
//		}

		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);


		// Create a new instance of DatePickerDialog and return it
		DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
		dialog.getDatePicker().setMinDate(c.getTimeInMillis());

		c.add(Calendar.DAY_OF_MONTH, 30);
		dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
		return dialog;
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		// Do something with the date chosen by the user
		if(onDateSetListener != null){
			onDateSetListener.onDateSet(view, year, month, day);
		}
	}

	public void show(FragmentManager manager, String tag, DatePickerDialog.OnDateSetListener onDateSetListener) {
		this.onDateSetListener = onDateSetListener;
		super.show(manager, tag);
	}
}