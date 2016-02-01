package product.clicklabs.jugnoo.sticky;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;

/**
 * Created by socomo on 12/15/15.
 */
public class JugnooJeanieTutorialActivity extends FragmentActivity  {

    public static final String TAG = "TutorialActivity";
    private int numOfPages = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        numOfPages = getIntent().getIntExtra(Constants.KEY_TUTORIAL_NO_OF_PAGES, 1);
        new ASSL(JugnooJeanieTutorialActivity.this, (LinearLayout)findViewById(R.id.rv), 1134, 720, false);


        if (savedInstanceState == null) {
            loadTutorialScreen();
        }

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {


                finish();

        } else {
            super.onBackPressed();
        }
    }

    public void loadTutorialScreen() {

//        loadFragment(R.id.frame_content, (Fragment) Fragment
//                        .instantiate(this, TutorialFragment.class
//                                .getName()), "TutorialFragment", false,
//                null
//        );

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_content, new TutorialFragment(numOfPages), "TutorialFragment").addToBackStack("TutorialFragment")
                .commitAllowingStateLoss();

    }
}