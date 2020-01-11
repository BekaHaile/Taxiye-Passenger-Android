package product.clicklabs.jugnoo.rentals;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.viewpager.widget.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.home.models.RideTypeValue;
import product.clicklabs.jugnoo.rentals.qrscanner.ScannerActivity;

public class InstructionDialog {

    public static boolean shownInSession = false;

    public static void showHelpDialog(final Activity activity) {
        if(shownInSession){
            new IntentIntegrator(activity).setCaptureActivity(ScannerActivity.class).initiateScan();
            return;
        }

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.instruction_dialog_layout);

        List<InstructionDialogModel> list = new ArrayList<>();
        List<Region.Instructions> instructions = null;

        List<Region> region = Data.autoData.getRegions();

        for(int i = 0;i < region.size();i++)
        {
            if(region.get(i).getRideType() == RideTypeValue.BIKE_RENTAL.getOrdinal())
            {
                instructions = region.get(i).getInstructions();
            }
        }

        if(instructions != null)
        {
            for(int i = 0;i < instructions.size();i++)
            {
                String title = instructions.get(i).getTitle();
                String image = instructions.get(i).getImage();
                String description = instructions.get(i).getDescription();
                list.add(new InstructionDialogModel(title,image,description));
            }

        }

        final ViewPager viewPager = dialog.findViewById(R.id.viewPager);
        Button buttonNext = dialog.findViewById(R.id.button_next);
        Button buttonSkip = dialog.findViewById(R.id.button_skip);
        ImageView imageViewBack = dialog.findViewById(R.id.image_view_back);

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() + 1 == (viewPager.getAdapter()).getCount()) {

                    // Making Dialog Dismiss after the last page

                    dialog.dismiss();
                    new IntentIntegrator(activity).setCaptureActivity(ScannerActivity.class).initiateScan();

                } else {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
            }
        });


        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new IntentIntegrator(activity).setCaptureActivity(ScannerActivity.class).initiateScan();
            }
        });

//        viewPager.setVisibility(View.VISIBLE);

        InstructionDialogAdapter adapter = new InstructionDialogAdapter(activity, list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(dialog.getWindow()).setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);

        }
        //  viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setAdapter(adapter);
        final CirclePageIndicator indicator = dialog.findViewById(R.id.page_indicator);
        indicator.setViewPager(viewPager);
        indicator.setCurrentItem(viewPager.getCurrentItem());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

                indicator.setCurrentItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        dialog.show();
        shownInSession = true;
    }

}
