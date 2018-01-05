package product.clicklabs.jugnoo.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by socomo20 on 6/1/15.
 */
public class NonScrollListView extends ListView {

    public NonScrollListView(Context context) {
        super(context);
    }
    public NonScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public NonScrollListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    private int width ;
    private int height ;
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        this.width = widthMeasureSpec;
        this.height = heightMeasureSpec;
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
            Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }


    /*public void callOnMeasure(){
        super.measure(width,height);

    }*/
}
