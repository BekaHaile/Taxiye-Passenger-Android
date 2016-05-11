package product.clicklabs.jugnoo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import product.clicklabs.jugnoo.datastructure.AutoCompleteSearchResult;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LocalGson;
import product.clicklabs.jugnoo.utils.Prefs;


/**
 * Created by Ankit on 10/9/15.
 */
public class AddFavouritePlaces extends BaseActivity{

    private TextView textViewHome, textViewWork, textViewGym, textViewFriend, textViewTitle;
    private LinearLayout root, linearLayoutPlaces;
    private ImageView imageViewBack;
    public static final int ADD_HOME = 2, ADD_WORK = 3, ADD_GYM = 4, ADD_FRIEND = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_favourite);

        root = (LinearLayout)findViewById(R.id.root);
        new ASSL(this, root, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);textViewTitle.setTypeface(Fonts.mavenMedium(this), Typeface.BOLD);
        imageViewBack = (ImageView)findViewById(R.id.imageViewBack);
        linearLayoutPlaces = (LinearLayout)findViewById(R.id.linearLayoutPlaces);
        textViewHome = (TextView)findViewById(R.id.textViewHome); textViewHome.setTypeface(Fonts.mavenMedium(this));
        textViewWork = (TextView)findViewById(R.id.textViewWork); textViewWork.setTypeface(Fonts.mavenMedium(this));
        textViewGym = (TextView)findViewById(R.id.textViewGym); textViewGym.setTypeface(Fonts.mavenMedium(this));
        textViewFriend = (TextView)findViewById(R.id.textViewFriend); textViewFriend.setTypeface(Fonts.mavenMedium(this));

        setSavePlaces();

        imageViewBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });


    }

	@Override
	public void onBackPressed() {
		performBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(root);
		System.gc();
	}

    public void onClick(View v){
        Intent intent=new Intent(AddFavouritePlaces.this,AddPlaceActivity.class);
        switch (v.getId()){
            case R.id.textViewHome:
                intent.putExtra("requestCode","HOME");
                intent.putExtra("address", Prefs.with(AddFavouritePlaces.this).getString(SPLabels.ADD_HOME, ""));
                startActivityForResult(intent, ADD_HOME);// Activity is started with requestCode 2
                break;

            case R.id.textViewWork:
                intent.putExtra("requestCode","WORK");
                intent.putExtra("address", Prefs.with(AddFavouritePlaces.this).getString(SPLabels.ADD_WORK, ""));
                startActivityForResult(intent, ADD_WORK);// Activity is started with requestCode 2
                break;

            case R.id.textViewGym:
                intent.putExtra("requestCode","GYM");
                intent.putExtra("address", Prefs.with(AddFavouritePlaces.this).getString(SPLabels.ADD_GYM, ""));
                startActivityForResult(intent, ADD_GYM);// Activity is started with requestCode 2
                break;

            case R.id.textViewFriend:
                intent.putExtra("requestCode","FRIEND");
                intent.putExtra("address", Prefs.with(AddFavouritePlaces.this).getString(SPLabels.ADD_FRIEND, ""));
                startActivityForResult(intent, ADD_FRIEND);// Activity is started with requestCode 2
                break;
        }
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    private void setSavePlaces(){
        if(!Prefs.with(AddFavouritePlaces.this).getString(SPLabels.ADD_HOME, "").equalsIgnoreCase("")){
            String abc =Prefs.with(AddFavouritePlaces.this).getString(SPLabels.ADD_HOME, "");
            AutoCompleteSearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(abc);
            String s = "Home \n" + searchResult.name+", "+searchResult.address;
            SpannableString ss1 = new SpannableString(s);
            ss1.setSpan(new RelativeSizeSpan(1f), 0, 4, 0); // set size
            ss1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 4, 0);// set color
            textViewHome.setText(ss1);
        }

        if(!Prefs.with(AddFavouritePlaces.this).getString(SPLabels.ADD_WORK, "").equalsIgnoreCase("")){
            String abc =Prefs.with(AddFavouritePlaces.this).getString(SPLabels.ADD_WORK, "");
            AutoCompleteSearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(abc);
            String s = "Work \n" + searchResult.name+", "+searchResult.address;
            SpannableString ss1 = new SpannableString(s);
            ss1.setSpan(new RelativeSizeSpan(1f), 0, 4, 0); // set size
            ss1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 4, 0);// set color
            textViewWork.setText(ss1);
        }

        if(!Prefs.with(AddFavouritePlaces.this).getString(SPLabels.ADD_GYM, "").equalsIgnoreCase("")){
            String abc =Prefs.with(AddFavouritePlaces.this).getString(SPLabels.ADD_GYM, "");
            AutoCompleteSearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(abc);
            String s = "Gym \n" + searchResult.name+", "+searchResult.address;
            SpannableString ss1 = new SpannableString(s);
            ss1.setSpan(new RelativeSizeSpan(1f), 0, 4, 0); // set size
            ss1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 4, 0);// set color
            textViewGym.setText(ss1);
        }

        if(!Prefs.with(AddFavouritePlaces.this).getString(SPLabels.ADD_FRIEND, "").equalsIgnoreCase("")){
            String abc =Prefs.with(AddFavouritePlaces.this).getString(SPLabels.ADD_FRIEND, "");
            AutoCompleteSearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(abc);
            String s = "Friend \n" + searchResult.name+", "+searchResult.address;
            SpannableString ss1 = new SpannableString(s);
            ss1.setSpan(new RelativeSizeSpan(1f), 0, 4, 0); // set size
            ss1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 4, 0);// set color
            textViewFriend.setText(ss1);
        }
    }

    public void performBackPressed(){
        Intent intent=new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    // Call Back method  to get the Message form other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            // check if the request code is same as what is passed  here it is 2
            String strResult = data.getStringExtra("PLACE");
            AutoCompleteSearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(strResult);
            if (requestCode == ADD_HOME) {
                    if(searchResult != null){
                        String s = "Home \n" + searchResult.name + " " + searchResult.address;
                        SpannableString ss1 = new SpannableString(s);
                        ss1.setSpan(new RelativeSizeSpan(1f), 0, 4, 0); // set size
                        ss1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 4, 0);// set color
                        textViewHome.setText(ss1);
                        Prefs.with(AddFavouritePlaces.this).save(SPLabels.ADD_HOME, strResult);
                    }else {
                        textViewHome.setText("Add Home");
                    }

            } else if (requestCode == ADD_WORK) {
                if(searchResult != null) {
                    String s = "Work \n" + searchResult.name + " " + searchResult.address;
                    SpannableString ss1 = new SpannableString(s);
                    ss1.setSpan(new RelativeSizeSpan(1f), 0, 4, 0); // set size
                    ss1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 4, 0);// set color
                    textViewWork.setText(ss1);
                    Prefs.with(AddFavouritePlaces.this).save(SPLabels.ADD_WORK, strResult);
                }else{
                    textViewWork.setText("Add Work");
                }
            } else if (requestCode == ADD_GYM) {
                if(searchResult != null) {
                    String s = "Gym \n" + searchResult.name + " " + searchResult.address;
                    SpannableString ss1 = new SpannableString(s);
                    ss1.setSpan(new RelativeSizeSpan(1f), 0, 4, 0); // set size
                    ss1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 4, 0);// set color
                    textViewGym.setText(ss1);
                    Prefs.with(AddFavouritePlaces.this).save(SPLabels.ADD_GYM, strResult);
                }else{
                    textViewGym.setText("Add Gym");
                }
            } else if (requestCode == ADD_FRIEND) {
                if(searchResult != null) {
                    String s = "Friend \n" + searchResult.name + " " + searchResult.address;
                    SpannableString ss1 = new SpannableString(s);
                    ss1.setSpan(new RelativeSizeSpan(1f), 0, 4, 0); // set size
                    ss1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 4, 0);// set color
                    textViewFriend.setText(ss1);
                    Prefs.with(AddFavouritePlaces.this).save(SPLabels.ADD_FRIEND, strResult);
                }else {
                    textViewFriend.setText("Add Friend");
                }
            }
        }
    }
}
