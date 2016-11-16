package product.clicklabs.jugnoo.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.Spinner;

/**
 * Created by ankit on 16/11/16.
 */

public class MySpinner extends Spinner {
    AdapterView.OnItemSelectedListener listener;

    public MySpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        if (listener != null)
            listener.onItemSelected(null, null, position, 0);
    }

    public void setOnItemSelectedEvenIfUnchangedListener(
            AdapterView.OnItemSelectedListener listener) {
        this.listener = listener;
    }
}
