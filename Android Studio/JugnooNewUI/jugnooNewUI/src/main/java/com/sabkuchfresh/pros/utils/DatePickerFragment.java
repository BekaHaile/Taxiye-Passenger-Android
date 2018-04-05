package com.sabkuchfresh.pros.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.widget.DatePicker;

import com.sabkuchfresh.fragments.AnywhereHomeFragment;

import java.util.Calendar;

import product.clicklabs.jugnoo.R;

/**
 * Created by shankar on 19/06/17.
 */

public class DatePickerFragment extends DialogFragment
		implements DatePickerDialog.OnDateSetListener {

	private DatePickerDialog.OnDateSetListener onDateSetListener;


	public static final String ADD_DAYS = "add_days";
	private DatePickerDialog datePickerDialog;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
//		if(c.get(Calendar.HOUR_OF_DAY) > 20){

		if(!getArguments().containsKey(ADD_DAYS) || getArguments().getBoolean(ADD_DAYS))
				c.add(Calendar.DAY_OF_MONTH, 2);
		else
			c.add(Calendar.MINUTE, AnywhereHomeFragment.MIN_BUFFER_TIME_MINS+AnywhereHomeFragment.BUFFER_TIME_TO_SELECT_MINS);


//		}

		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);


		// Create a new instance of DatePickerDialog and return it
		DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day+1);
		dialog.setButton(DatePickerDialog.BUTTON_POSITIVE,getString(R.string.ok),dialog);
		dialog.setButton(DatePickerDialog.BUTTON_NEGATIVE,getString(R.string.dismiss),dialog);

		dialog.getDatePicker().setMinDate(c.getTimeInMillis());
		dialog.getDatePicker().updateDate(year, month, day);

		c.add(Calendar.DAY_OF_MONTH, 30);
		dialog.getDatePicker().setMaxDate(c.getTimeInMillis());

		datePickerDialog = dialog;
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
		/*try {
			final Calendar c = Calendar.getInstance();
			if(!getArguments().containsKey(ADD_DAYS) || getArguments().getBoolean(ADD_DAYS))
                c.add(Calendar.DAY_OF_MONTH, 2);
            else
                c.add(Calendar.MINUTE, AnywhereHomeFragment.MIN_BUFFER_TIME_MINS+AnywhereHomeFragment.BUFFER_TIME_TO_SELECT_MINS);
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			datePickerDialog.updateDate(year, month, day);
			datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
			c.add(Calendar.DAY_OF_MONTH, 90);
			datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());


		} catch (Exception e) {
			e.printStackTrace();
		}*/
		super.show(manager, tag);
	}
}