package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.EmergencyContact;
import product.clicklabs.jugnoo.datastructure.RefreshEmergencyContacts;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class EmergencyContactsActivity extends BaseActivity implements RefreshEmergencyContacts, FlurryEventNames {

    LinearLayout relative;

    TextView textViewTitle;
    ImageView imageViewBack;

    ScrollView scrollView;
    LinearLayout linearLayoutInScroll;
    TextView textViewExtraScroll;

    RelativeLayout relativeLayoutEmergencyContact1Top;
    TextView textViewEmergencyContact1;
    ImageView imageViewEmergencyContact1PM, imageViewEmergencyContact1Edit;
    LinearLayout linearLayoutEmergencyContact1Fields;
    EditText editTextEC1Name, editTextEC1Phone, editTextEC1Email;
    ImageView imageViewEC1PickContact;
    RelativeLayout relativeLayoutEC1Delete;
    RelativeLayout relativeLayoutEC1Operations;
    Button buttonVerifyEC1, buttonResendSMSEC1;
    TextView textViewEC1NotVerified;

    RelativeLayout relativeLayoutEmergencyContact2Top;
    TextView textViewEmergencyContact2;
    ImageView imageViewEmergencyContact2PM, imageViewEmergencyContact2Edit;
    LinearLayout linearLayoutEmergencyContact2Fields;
    EditText editTextEC2Name, editTextEC2Phone, editTextEC2Email;
    ImageView imageViewEC2PickContact;
    RelativeLayout relativeLayoutEC2Delete;
    RelativeLayout relativeLayoutEC2Operations;
    Button buttonVerifyEC2, buttonResendSMSEC2;
    TextView textViewEC2NotVerified;


    EmergencyContact emergencyContact1, emergencyContact2;
    int editEC1 = 0, editEC2 = 0;

    private static final int PICK_CONTACT_1 = 101, PICK_CONTACT_2 = 102;


    public static RefreshEmergencyContacts refreshEmergencyContacts;

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        refreshEmergencyContacts = this;

        editEC1 = 0; editEC2 = 0;

        relative = (LinearLayout) findViewById(R.id.relative);
        new ASSL(this, relative, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.mavenRegular(this));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);


        scrollView = (ScrollView) findViewById(R.id.scrollView);
        linearLayoutInScroll = (LinearLayout) findViewById(R.id.linearLayoutInScroll);
        textViewExtraScroll = (TextView) findViewById(R.id.textViewExtraScroll);


        // Emergency contact 1
        relativeLayoutEmergencyContact1Top = (RelativeLayout) findViewById(R.id.relativeLayoutEmergencyContact1Top);
        textViewEmergencyContact1 = (TextView) findViewById(R.id.textViewEmergencyContact1); textViewEmergencyContact1.setTypeface(Fonts.latoRegular(this));
        imageViewEmergencyContact1PM = (ImageView) findViewById(R.id.imageViewEmergencyContact1PM);
        imageViewEmergencyContact1Edit = (ImageView) findViewById(R.id.imageViewEmergencyContact1Edit);
        ((TextView) findViewById(R.id.textViewEmergencyContact1)).setTypeface(Fonts.latoRegular(this));
        linearLayoutEmergencyContact1Fields = (LinearLayout) findViewById(R.id.linearLayoutEmergencyContact1Fields);
        editTextEC1Name = (EditText) findViewById(R.id.editTextEC1Name); editTextEC1Name.setTypeface(Fonts.latoRegular(this));
        editTextEC1Phone = (EditText) findViewById(R.id.editTextEC1Phone); editTextEC1Phone.setTypeface(Fonts.latoRegular(this));
        editTextEC1Email = (EditText) findViewById(R.id.editTextEC1Email); editTextEC1Email.setTypeface(Fonts.latoRegular(this));
        imageViewEC1PickContact = (ImageView) findViewById(R.id.imageViewEC1PickContact);
        relativeLayoutEC1Delete = (RelativeLayout) findViewById(R.id.relativeLayoutEC1Delete);
        ((TextView) findViewById(R.id.textViewEC1Delete)).setTypeface(Fonts.latoRegular(this));
        ((TextView) findViewById(R.id.textViewEC1Phone91)).setTypeface(Fonts.latoRegular(this));
        relativeLayoutEC1Operations = (RelativeLayout) findViewById(R.id.relativeLayoutEC1Operations);
        buttonVerifyEC1 = (Button) findViewById(R.id.buttonVerifyEC1); buttonVerifyEC1.setTypeface(Fonts.latoRegular(this));
        buttonResendSMSEC1 = (Button) findViewById(R.id.buttonResendSMSEC1); buttonResendSMSEC1.setTypeface(Fonts.latoRegular(this));
        textViewEC1NotVerified = (TextView) findViewById(R.id.textViewEC1NotVerified); textViewEC1NotVerified.setTypeface(Fonts.latoRegular(this));
        linearLayoutEmergencyContact1Fields.setVisibility(View.GONE);



        //Emergency contact 2
        relativeLayoutEmergencyContact2Top = (RelativeLayout) findViewById(R.id.relativeLayoutEmergencyContact2Top);
        textViewEmergencyContact2 = (TextView) findViewById(R.id.textViewEmergencyContact2); textViewEmergencyContact2.setTypeface(Fonts.latoRegular(this));
        imageViewEmergencyContact2PM = (ImageView) findViewById(R.id.imageViewEmergencyContact2PM);
        imageViewEmergencyContact2Edit = (ImageView) findViewById(R.id.imageViewEmergencyContact2Edit);
        ((TextView) findViewById(R.id.textViewEmergencyContact2)).setTypeface(Fonts.latoRegular(this));
        linearLayoutEmergencyContact2Fields = (LinearLayout) findViewById(R.id.linearLayoutEmergencyContact2Fields);
        editTextEC2Name = (EditText) findViewById(R.id.editTextEC2Name); editTextEC2Name.setTypeface(Fonts.latoRegular(this));
        editTextEC2Phone = (EditText) findViewById(R.id.editTextEC2Phone); editTextEC2Phone.setTypeface(Fonts.latoRegular(this));
        editTextEC2Email = (EditText) findViewById(R.id.editTextEC2Email); editTextEC2Email.setTypeface(Fonts.latoRegular(this));
        imageViewEC2PickContact = (ImageView) findViewById(R.id.imageViewEC2PickContact);
        relativeLayoutEC2Delete = (RelativeLayout) findViewById(R.id.relativeLayoutEC2Delete);
        ((TextView) findViewById(R.id.textViewEC2Delete)).setTypeface(Fonts.latoRegular(this));
        ((TextView) findViewById(R.id.textViewEC2Phone91)).setTypeface(Fonts.latoRegular(this));
        relativeLayoutEC2Operations = (RelativeLayout) findViewById(R.id.relativeLayoutEC2Operations);
        buttonVerifyEC2 = (Button) findViewById(R.id.buttonVerifyEC2); buttonVerifyEC2.setTypeface(Fonts.latoRegular(this));
        buttonResendSMSEC2 = (Button) findViewById(R.id.buttonResendSMSEC2); buttonResendSMSEC2.setTypeface(Fonts.latoRegular(this));
        textViewEC2NotVerified = (TextView) findViewById(R.id.textViewEC2NotVerified); textViewEC2NotVerified.setTypeface(Fonts.latoRegular(this));
        linearLayoutEmergencyContact2Fields.setVisibility(View.GONE);


        relativeLayoutEmergencyContact1Top.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearLayoutEmergencyContact1Fields.getVisibility() == View.GONE) {
                    if (emergencyContact1 == null) {
                        imageViewEmergencyContact1PM.setImageResource(R.drawable.emergency_minus_icon);
                    } else {
//                        if (emergencyContact1.verificationStatus == 1) {
//                        } else {
//                            imageViewEmergencyContact1PM.setImageResource(R.drawable.emergency_minus_icon);
//                        }
                    }
                    if (linearLayoutEmergencyContact2Fields.getVisibility() == View.VISIBLE) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                linearLayoutEmergencyContact1Fields.setVisibility(View.VISIBLE);
                            }
                        }, 205);
                    } else {
                        linearLayoutEmergencyContact1Fields.setVisibility(View.VISIBLE);
                    }
                    closeEC2Layout();
                } else if (linearLayoutEmergencyContact1Fields.getVisibility() == View.VISIBLE) {
                    if (emergencyContact1 != null) {
//                        if (emergencyContact1.verificationStatus != 1) {
//                            imageViewEmergencyContact1PM.setImageResource(R.drawable.emergency_plus_icon);
//                        }
                        linearLayoutEmergencyContact1Fields.setVisibility(View.GONE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (editEC1 == 1) {
                                    imageViewEmergencyContact1Edit.performClick();
                                }
                            }
                        }, 250);
                    }
                }
            }
        });

        relativeLayoutEmergencyContact2Top.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emergencyContact1 != null) {
                    if (linearLayoutEmergencyContact2Fields.getVisibility() == View.GONE) {
                        if(emergencyContact2 == null){
                            imageViewEmergencyContact2PM.setImageResource(R.drawable.emergency_minus_icon);
                        }
                        else{
//                            if(emergencyContact2.verificationStatus == 1){
//                            }
//                            else{
//                                imageViewEmergencyContact2PM.setImageResource(R.drawable.emergency_minus_icon);
//                            }
                        }
                        if (linearLayoutEmergencyContact1Fields.getVisibility() == View.VISIBLE) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    linearLayoutEmergencyContact2Fields.setVisibility(View.VISIBLE);
                                }
                            }, 205);
                        } else {
                            linearLayoutEmergencyContact2Fields.setVisibility(View.VISIBLE);
                        }
                        closeEC1Layout();
                    } else if (linearLayoutEmergencyContact2Fields.getVisibility() == View.VISIBLE) {
                        if (emergencyContact2 == null) {
                            imageViewEmergencyContact2PM.setImageResource(R.drawable.emergency_plus_icon);
                        }
                        linearLayoutEmergencyContact2Fields.setVisibility(View.GONE);
                        closeEC1Layout();
                        if (emergencyContact2 != null){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (editEC2 == 1) {
                                        imageViewEmergencyContact2Edit.performClick();
                                    }
                                }
                            }, 250);
                        }
                    }
                }
                else{
                    Toast.makeText(EmergencyContactsActivity.this, "Add primary contact first", Toast.LENGTH_SHORT).show();
                }
            }
        });


        imageViewEC1PickContact.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT_1);
            }
        });

        imageViewEC2PickContact.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT_2);
            }
        });


        imageViewEmergencyContact1Edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emergencyContact1 != null) {
                    if (0 == editEC1) {
                        editEC1 = 1;

                        editTextEC1Name.setEnabled(true);
                        editTextEC1Name.setSelection(editTextEC1Name.getText().length());
                        editTextEC1Phone.setEnabled(true);
                        editTextEC1Email.setEnabled(true);

                        textViewEC1NotVerified.setVisibility(View.GONE);
                        buttonResendSMSEC1.setVisibility(View.GONE);

                        imageViewEC1PickContact.setVisibility(View.VISIBLE);
                        buttonVerifyEC1.setVisibility(View.VISIBLE);
                        buttonVerifyEC1.setText("UPDATE");

                        if(linearLayoutEmergencyContact1Fields.getVisibility() == View.GONE){
                            relativeLayoutEmergencyContact1Top.performClick();
                        }
                    } else {
                        editEC1 = 0;

                        editTextEC1Name.setEnabled(false);
                        editTextEC1Phone.setEnabled(false);
                        editTextEC1Email.setEnabled(false);

                        if (emergencyContact1.verificationStatus == 0) {
                            textViewEC1NotVerified.setVisibility(View.VISIBLE);
                            buttonResendSMSEC1.setVisibility(View.VISIBLE);
                        }

                        imageViewEC1PickContact.setVisibility(View.GONE);
                        buttonVerifyEC1.setVisibility(View.GONE);
                        buttonVerifyEC1.setText("VERIFY");

                    }
                }
            }
        });

        imageViewEmergencyContact2Edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emergencyContact2 != null) {
                    if (0 == editEC2) {
                        editEC2 = 1;

                        editTextEC2Name.setEnabled(true);
                        editTextEC2Name.setSelection(editTextEC2Name.getText().length());
                        editTextEC2Phone.setEnabled(true);
                        editTextEC2Email.setEnabled(true);

                        textViewEC2NotVerified.setVisibility(View.GONE);
                        buttonResendSMSEC2.setVisibility(View.GONE);

                        imageViewEC2PickContact.setVisibility(View.VISIBLE);
                        buttonVerifyEC2.setVisibility(View.VISIBLE);
                        buttonVerifyEC2.setText("UPDATE");

                        if(linearLayoutEmergencyContact2Fields.getVisibility() == View.GONE){
                            relativeLayoutEmergencyContact2Top.performClick();
                        }
                    } else {
                        editEC2 = 0;

                        editTextEC2Name.setEnabled(false);
                        editTextEC2Phone.setEnabled(false);
                        editTextEC2Email.setEnabled(false);

                        if (emergencyContact2.verificationStatus == 0) {
                            textViewEC2NotVerified.setVisibility(View.VISIBLE);
                            buttonResendSMSEC2.setVisibility(View.VISIBLE);
                        }

                        imageViewEC2PickContact.setVisibility(View.GONE);
                        buttonVerifyEC2.setVisibility(View.GONE);
                        buttonVerifyEC2.setText("VERIFY");
                    }
                }
            }
        });


        buttonVerifyEC1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextEC1Name.getText().toString().trim();
                String phoneNo = editTextEC1Phone.getText().toString().trim();
                String email = editTextEC1Email.getText().toString().trim();

                if("".equalsIgnoreCase(name)){
                    editTextEC1Name.requestFocus();
                    editTextEC1Name.setError("Name can't be empty");
                }
                else if("".equalsIgnoreCase(phoneNo)){
                    editTextEC1Phone.requestFocus();
                    editTextEC1Phone.setError("Phone number can't be empty");
                }
                else{
                    String reducedPhone = Utils.retrievePhoneNumberTenChars(phoneNo);
                    if(Utils.validPhoneNumber(reducedPhone)){
                        reducedPhone = "+91"+reducedPhone;
                        if(!"".equalsIgnoreCase(email) && !Utils.isEmailValid(email)){
                            editTextEC1Email.requestFocus();
                            editTextEC1Email.setError("Invalid email id");
                        }
                        else {
                            if (0 == editEC1) {
                                addEmergencyContactAPI(EmergencyContactsActivity.this, name, reducedPhone, email);
                                FlurryEventLogger.event(EMERGENCY_CONTACT_ADDED);
                            } else {
                                if (emergencyContact1 != null) {
                                    if (name.equalsIgnoreCase(emergencyContact1.name)
                                        && reducedPhone.equalsIgnoreCase(emergencyContact1.phoneNo)
                                        && email.equalsIgnoreCase(emergencyContact1.email)) {
                                        Toast.makeText(EmergencyContactsActivity.this, "Entered fields are same as the previous", Toast.LENGTH_SHORT).show();
                                    } else {
                                        editEmergencyContactAPI(EmergencyContactsActivity.this, name, reducedPhone, email, emergencyContact1);
                                    }
                                } else {
                                    Toast.makeText(EmergencyContactsActivity.this, "Contact not added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                    else{
                        editTextEC1Phone.requestFocus();
                        editTextEC1Phone.setError("Invalid phone number");
                    }
                }
            }
        });


        buttonVerifyEC2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextEC2Name.getText().toString().trim();
                String phoneNo = editTextEC2Phone.getText().toString().trim();
                String email = editTextEC2Email.getText().toString().trim();

                if("".equalsIgnoreCase(name)){
                    editTextEC2Name.requestFocus();
                    editTextEC2Name.setError("Name can't be empty");
                }
                else if("".equalsIgnoreCase(phoneNo)){
                    editTextEC2Phone.requestFocus();
                    editTextEC2Phone.setError("Phone number can't be empty");
                }
                else{
                    String reducedPhone = Utils.retrievePhoneNumberTenChars(phoneNo);
                    if(Utils.validPhoneNumber(reducedPhone)){
                        reducedPhone = "+91"+reducedPhone;
                        if(!"".equalsIgnoreCase(email) && !Utils.isEmailValid(email)){
                            editTextEC2Email.requestFocus();
                            editTextEC2Email.setError("Invalid email id");
                        }
                        else{
                            if(0 == editEC2) {
                                addEmergencyContactAPI(EmergencyContactsActivity.this, name, reducedPhone, email);
                                FlurryEventLogger.event(EMERGENCY_CONTACT_ADDED);
                            }
                            else{
                                if(emergencyContact2 != null) {
                                    if (name.equalsIgnoreCase(emergencyContact2.name)
                                        && reducedPhone.equalsIgnoreCase(emergencyContact2.phoneNo)
                                        && email.equalsIgnoreCase(emergencyContact2.email)) {
                                        Toast.makeText(EmergencyContactsActivity.this, "Entered fields are same as the previous", Toast.LENGTH_SHORT).show();
                                    } else {
                                        editEmergencyContactAPI(EmergencyContactsActivity.this, name, reducedPhone, email, emergencyContact2);
                                    }
                                }
                                else{
                                    Toast.makeText(EmergencyContactsActivity.this, "Contact not added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                    else{
                        editTextEC2Phone.requestFocus();
                        editTextEC2Phone.setError("Invalid phone number");
                    }
                }
            }
        });


        buttonResendSMSEC1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emergencyContact1 != null){
                    resendVerificationAPI(EmergencyContactsActivity.this, emergencyContact1.phoneNo, emergencyContact1.email);
                }
            }
        });

        buttonResendSMSEC2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emergencyContact2 != null){
                    resendVerificationAPI(EmergencyContactsActivity.this, emergencyContact2.phoneNo, emergencyContact2.email);
                }
            }
        });

        relativeLayoutEC1Delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPopup.alertPopupTwoButtonsWithListeners(EmergencyContactsActivity.this, "", "Are you sure you want to delete this emergency contact?", "OK", "Cancel",
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(emergencyContact1 != null){
                                deleteEmergencyContactAPI(EmergencyContactsActivity.this, emergencyContact1);
                            }
                        }
                    }, new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, true, false);
            }
        });

        relativeLayoutEC2Delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPopup.alertPopupTwoButtonsWithListeners(EmergencyContactsActivity.this, "", "Are you sure you want to delete this emergency contact?", "OK", "Cancel",
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(emergencyContact2 != null){
                                deleteEmergencyContactAPI(EmergencyContactsActivity.this, emergencyContact2);
                            }
                        }
                    }, new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, true, false);
            }
        });



        imageViewBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });





        linearLayoutInScroll.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardLayoutListener(linearLayoutInScroll, textViewExtraScroll, new KeyboardLayoutListener.KeyBoardStateHandler() {
            @Override
            public void keyboardOpened() {

            }

            @Override
            public void keyBoardClosed() {

            }
        }));

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        setEmergencyContacts();

    }

    private void setEmergencyContacts(){
        try{
            if(Data.emergencyContactsList != null){
                if(Data.emergencyContactsList.size() >= 2){
                    emergencyContact1 = Data.emergencyContactsList.get(0);
                    emergencyContact2 = Data.emergencyContactsList.get(1);

                    
                    
                    textViewEmergencyContact1.setText(emergencyContact1.name);
                    editTextEC1Name.setText(emergencyContact1.name); editTextEC1Name.setEnabled(false);
                    editTextEC1Phone.setText(Utils.retrievePhoneNumberTenChars(emergencyContact1.phoneNo)); editTextEC1Phone.setEnabled(false);
                    editTextEC1Email.setText(emergencyContact1.email); editTextEC1Email.setEnabled(false);

                    imageViewEmergencyContact1Edit.setVisibility(View.VISIBLE);
                    imageViewEC1PickContact.setVisibility(View.GONE);
                    buttonVerifyEC1.setVisibility(View.GONE);
                    relativeLayoutEC1Delete.setVisibility(View.GONE);
                    buttonVerifyEC1.setText("VERIFY");

                    if(1 == emergencyContact1.verificationStatus){
                        imageViewEmergencyContact1PM.setImageResource(R.drawable.emergency_verified_icon);

                        textViewEC1NotVerified.setVisibility(View.GONE);
                        buttonResendSMSEC1.setVisibility(View.GONE);
                    }
                    else{
						imageViewEmergencyContact1PM.setImageResource(R.drawable.ic_contact_unverified);
//                        if(linearLayoutEmergencyContact1Fields.getVisibility() == View.GONE) {
//                            imageViewEmergencyContact1PM.setImageResource(R.drawable.ic_contact_unverified);
//                        }
//                        else{
//                            imageViewEmergencyContact1PM.setImageResource(R.drawable.emergency_minus_icon);
//                        }

                        textViewEC1NotVerified.setVisibility(View.VISIBLE);
                        buttonResendSMSEC1.setVisibility(View.VISIBLE);
                    }



                    relativeLayoutEmergencyContact2Top.setVisibility(View.VISIBLE);
                    
                    textViewEmergencyContact2.setText(emergencyContact2.name);
                    editTextEC2Name.setText(emergencyContact2.name); editTextEC2Name.setEnabled(false);
                    editTextEC2Phone.setText(Utils.retrievePhoneNumberTenChars(emergencyContact2.phoneNo)); editTextEC2Phone.setEnabled(false);
                    editTextEC2Email.setText(emergencyContact2.email); editTextEC2Email.setEnabled(false);

                    imageViewEmergencyContact2Edit.setVisibility(View.VISIBLE);
                    imageViewEC2PickContact.setVisibility(View.GONE);
                    buttonVerifyEC2.setVisibility(View.GONE);
                    relativeLayoutEC2Delete.setVisibility(View.VISIBLE);
                    buttonVerifyEC2.setText("VERIFY");

                    if(1 == emergencyContact2.verificationStatus){
                        imageViewEmergencyContact2PM.setImageResource(R.drawable.emergency_verified_icon);

                        textViewEC2NotVerified.setVisibility(View.GONE);
                        buttonResendSMSEC2.setVisibility(View.GONE);
                    }
                    else{
						imageViewEmergencyContact2PM.setImageResource(R.drawable.ic_contact_unverified);
//                        if(linearLayoutEmergencyContact2Fields.getVisibility() == View.GONE) {
//                            imageViewEmergencyContact2PM.setImageResource(R.drawable.emergency_plus_icon);
//                        }
//                        else{
//                            imageViewEmergencyContact2PM.setImageResource(R.drawable.emergency_minus_icon);
//                        }

                        textViewEC2NotVerified.setVisibility(View.VISIBLE);
                        buttonResendSMSEC2.setVisibility(View.VISIBLE);
                    }

                }
                else if(Data.emergencyContactsList.size() >= 1){
                    emergencyContact1 = Data.emergencyContactsList.get(0);
                    emergencyContact2 = null; editEC2 = 0;

                    textViewEmergencyContact1.setText(emergencyContact1.name);
                    editTextEC1Name.setText(emergencyContact1.name); editTextEC1Name.setEnabled(false);
                    editTextEC1Phone.setText(Utils.retrievePhoneNumberTenChars(emergencyContact1.phoneNo)); editTextEC1Phone.setEnabled(false);
                    editTextEC1Email.setText(emergencyContact1.email); editTextEC1Email.setEnabled(false);

                    imageViewEmergencyContact1Edit.setVisibility(View.VISIBLE);
                    imageViewEC1PickContact.setVisibility(View.GONE);
                    buttonVerifyEC1.setVisibility(View.GONE);
                    relativeLayoutEC1Delete.setVisibility(View.VISIBLE);
                    buttonVerifyEC1.setText("VERIFY");

                    if(1 == emergencyContact1.verificationStatus){
                        imageViewEmergencyContact1PM.setImageResource(R.drawable.emergency_verified_icon);

                        textViewEC1NotVerified.setVisibility(View.GONE);
                        buttonResendSMSEC1.setVisibility(View.GONE);
                    }
                    else{
						imageViewEmergencyContact1PM.setImageResource(R.drawable.ic_contact_unverified);
//                        if(linearLayoutEmergencyContact1Fields.getVisibility() == View.GONE) {
//                            imageViewEmergencyContact1PM.setImageResource(R.drawable.emergency_plus_icon);
//                        }
//                        else{
//                            imageViewEmergencyContact1PM.setImageResource(R.drawable.emergency_minus_icon);
//                        }

                        textViewEC1NotVerified.setVisibility(View.VISIBLE);
                        buttonResendSMSEC1.setVisibility(View.VISIBLE);
                    }

                    relativeLayoutEmergencyContact2Top.setVisibility(View.VISIBLE);
                    
                    textViewEmergencyContact2.setText("Secondary Emergency Contact");
                    editTextEC2Name.setText(""); editTextEC2Name.setEnabled(true);
                    editTextEC2Phone.setText(""); editTextEC2Phone.setEnabled(true);
                    editTextEC2Email.setText(""); editTextEC2Email.setEnabled(true);

                    imageViewEmergencyContact2Edit.setVisibility(View.GONE);
                    imageViewEC2PickContact.setVisibility(View.VISIBLE);

                    if(linearLayoutEmergencyContact2Fields.getVisibility() == View.GONE) {
                        imageViewEmergencyContact2PM.setImageResource(R.drawable.emergency_plus_icon);
                    }
                    else{
                        imageViewEmergencyContact2PM.setImageResource(R.drawable.emergency_minus_icon);
                    }

                    buttonVerifyEC2.setVisibility(View.VISIBLE);
                    textViewEC2NotVerified.setVisibility(View.GONE);
                    buttonResendSMSEC2.setVisibility(View.GONE);
                    relativeLayoutEC2Delete.setVisibility(View.GONE);
                    buttonVerifyEC2.setText("VERIFY");
                }
                else{
                    emergencyContact1 = null; editEC1 = 0;
                    emergencyContact2 = null; editEC2 = 0;

                    textViewEmergencyContact1.setText("Primary Emergency Contact");
                    editTextEC1Name.setText(""); editTextEC1Name.setEnabled(true);
                    editTextEC1Phone.setText(""); editTextEC1Phone.setEnabled(true);
                    editTextEC1Email.setText(""); editTextEC1Email.setEnabled(true);

                    imageViewEmergencyContact1Edit.setVisibility(View.GONE);
                    imageViewEC1PickContact.setVisibility(View.VISIBLE);

                    if(linearLayoutEmergencyContact1Fields.getVisibility() == View.GONE) {
                        imageViewEmergencyContact1PM.setImageResource(R.drawable.emergency_plus_icon);
                    }
                    else{
                        imageViewEmergencyContact1PM.setImageResource(R.drawable.emergency_minus_icon);
                    }

                    buttonVerifyEC1.setVisibility(View.VISIBLE);
                    textViewEC1NotVerified.setVisibility(View.GONE);
                    buttonResendSMSEC1.setVisibility(View.GONE);
                    relativeLayoutEC1Delete.setVisibility(View.GONE);
                    buttonVerifyEC1.setText("VERIFY");




                    relativeLayoutEmergencyContact2Top.setVisibility(View.GONE);

                    textViewEmergencyContact2.setText("Secondary Emergency Contact");
                    editTextEC2Name.setText(""); editTextEC2Name.setEnabled(true);
                    editTextEC2Phone.setText(""); editTextEC2Phone.setEnabled(true);
                    editTextEC2Email.setText(""); editTextEC2Email.setEnabled(true);

                    imageViewEmergencyContact2Edit.setVisibility(View.GONE);
                    imageViewEC2PickContact.setVisibility(View.VISIBLE);

                    if(linearLayoutEmergencyContact2Fields.getVisibility() == View.GONE) {
                        imageViewEmergencyContact2PM.setImageResource(R.drawable.emergency_plus_icon);
                    }
                    else{
                        imageViewEmergencyContact2PM.setImageResource(R.drawable.emergency_minus_icon);
                    }

                    buttonVerifyEC2.setVisibility(View.VISIBLE);
                    textViewEC2NotVerified.setVisibility(View.GONE);
                    buttonResendSMSEC2.setVisibility(View.GONE);
                    relativeLayoutEC2Delete.setVisibility(View.GONE);
                    buttonVerifyEC2.setText("VERIFY");



                    relativeLayoutEmergencyContact1Top.performClick();
                    
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }



    private void closeEC1Layout(){
        if(emergencyContact1 == null){
            imageViewEmergencyContact1PM.setImageResource(R.drawable.emergency_plus_icon);
        }
        else{
//            if(emergencyContact1.verificationStatus == 1){
//            }
//            else{
//                imageViewEmergencyContact1PM.setImageResource(R.drawable.emergency_plus_icon);
//            }
        }
        linearLayoutEmergencyContact1Fields.setVisibility(View.GONE);
        Utils.hideSoftKeyboard(this, editTextEC1Name);
    }

    private void closeEC2Layout(){
        if(emergencyContact2 == null){
            imageViewEmergencyContact2PM.setImageResource(R.drawable.emergency_plus_icon);
        }
        else{
//            if(emergencyContact2.verificationStatus == 1){
//            }
//            else{
//                imageViewEmergencyContact2PM.setImageResource(R.drawable.emergency_plus_icon);
//            }
        }
        linearLayoutEmergencyContact2Fields.setVisibility(View.GONE);
        Utils.hideSoftKeyboard(this, editTextEC2Name);
    }




    public void getAllEmergencyContactsAPI(final Activity activity) {
        if(AppStatus.getInstance(activity).isOnline(activity)) {

            DialogPopup.showLoadingDialog(activity, "Loading...");

            HashMap<String, String> params = new HashMap<>();
            params.put("access_token", Data.userData.accessToken);
            Log.i("params", "=" + params.toString());

            RestClient.getApiServices().emergencyContactsList(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.i("Server response", "response = " + responseStr);
                    DialogPopup.dismissLoadingDialog();
                    try {
                        JSONObject jObj = new JSONObject(responseStr);
                        String message = JSONParser.getServerMessage(jObj);
                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                            int flag = jObj.getInt("flag");
                            if (ApiResponseFlags.EMERGENCY_CONTACTS.getOrdinal() == flag) {
                                if (Data.emergencyContactsList == null) {
                                    Data.emergencyContactsList = new ArrayList<>();
                                }
                                Data.emergencyContactsList.clear();
                                Data.emergencyContactsList.addAll(JSONParser.parseEmergencyContacts(jObj));
                                setEmergencyContacts();
                            } else {
                                DialogPopup.alertPopup(activity, "", message);
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                    }
                    DialogPopup.dismissLoadingDialog();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("request fail", error.toString());
                    DialogPopup.dismissLoadingDialog();
                    DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                }
            });
        }
        else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
        }

    }




    public void addEmergencyContactAPI(final Activity activity, String name, String phoneNo, String email) {
        if(AppStatus.getInstance(activity).isOnline(activity)) {

            DialogPopup.showLoadingDialog(activity, "Loading...");

            HashMap<String, String> params = new HashMap<>();
            params.put("access_token", Data.userData.accessToken);
            params.put("name", name);
            params.put("phone_no", phoneNo);
            params.put("email", email);

            Log.e("params", "=" + params.toString());

            RestClient.getApiServices().emergencyContactsAdd(params, callback);
        }
        else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
        }
    }


    public void editEmergencyContactAPI(final Activity activity, String name, String phoneNo, String email, EmergencyContact previousEmergencyContact) {
        if(AppStatus.getInstance(activity).isOnline(activity)) {

            DialogPopup.showLoadingDialog(activity, "Loading...");

            HashMap<String, String> params = new HashMap<>();
            params.put("access_token", Data.userData.accessToken);
            params.put("id", ""+previousEmergencyContact.id);
            if(!previousEmergencyContact.name.equalsIgnoreCase(name)){
                params.put("name", name);
            }
            if(!previousEmergencyContact.phoneNo.equalsIgnoreCase(phoneNo)){
                params.put("phone_no", phoneNo);
            }
            if(!previousEmergencyContact.email.equalsIgnoreCase(email)){
                params.put("email", email);
            }

            Log.i("params", "=" + params.toString());

            RestClient.getApiServices().emergencyContactsEdit(params, callback);
        }
        else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
        }
    }


    public void deleteEmergencyContactAPI(final Activity activity, EmergencyContact previousEmergencyContact) {
        if(AppStatus.getInstance(activity).isOnline(activity)) {

            DialogPopup.showLoadingDialog(activity, "Loading...");

            HashMap<String, String> params = new HashMap<>();

            params.put("access_token", Data.userData.accessToken);
            params.put("id", "" + previousEmergencyContact.id);

            Log.i("params", "=" + params.toString());

            RestClient.getApiServices().emergencyContactsDelete(params, callback);
        }
        else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
        }
    }



    public void resendVerificationAPI(final Activity activity, String phoneNo, String email) {
        if(AppStatus.getInstance(activity).isOnline(activity)) {

            DialogPopup.showLoadingDialog(activity, "Loading...");

            HashMap<String, String> params = new HashMap<>();
            params.put("access_token", Data.userData.accessToken);
            params.put("phone_no", phoneNo);
            params.put("email", email);

            Log.i("params", "=" + params.toString());

            RestClient.getApiServices().emergencyContactsRequestVerification(params, callback);
        }
        else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
        }

    }


    Callback<SettleUserDebt> callback = new Callback<SettleUserDebt>() {
        @Override
        public void success(SettleUserDebt settleUserDebt, Response response) {
            String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
            Log.i("Server response", "response = " + responseStr);
            DialogPopup.dismissLoadingDialog();
            try {
                JSONObject jObj = new JSONObject(responseStr);
                String message = JSONParser.getServerMessage(jObj);
                if (!SplashNewActivity.checkIfTrivialAPIErrors(EmergencyContactsActivity.this, jObj)) {
                    int flag = jObj.getInt("flag");
                    if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
                        DialogPopup.dialogBanner(EmergencyContactsActivity.this, message);
                    } else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                        DialogPopup.dialogBanner(EmergencyContactsActivity.this, message);
                        getAllEmergencyContactsAPI(EmergencyContactsActivity.this);
                    } else {
                        DialogPopup.dialogBanner(EmergencyContactsActivity.this, message);
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                DialogPopup.alertPopup(EmergencyContactsActivity.this, "", Data.SERVER_ERROR_MSG);
            }
            DialogPopup.dismissLoadingDialog();
        }

        @Override
        public void failure(RetrofitError error) {
            Log.e("request fail", error.toString());
            DialogPopup.dismissLoadingDialog();
            DialogPopup.alertPopup(EmergencyContactsActivity.this, "", Data.SERVER_NOT_RESOPNDING_MSG);
        }
    };







    String name = "", phone = "", email = "";

    //code
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (reqCode) {
                case PICK_CONTACT_1:
                    fetchContactInfo(data);
                    editTextEC1Name.setText(name);
                    editTextEC1Email.setText(email);
                    editTextEC1Phone.setText(phone);
                    editTextEC1Name.setSelection(editTextEC1Name.getText().length());

                    editTextEC1Name.setError(null);
                    editTextEC1Email.setError(null);
                    editTextEC1Phone.setError(null);

                    break;

                case PICK_CONTACT_2:
                    fetchContactInfo(data);
                    editTextEC2Name.setText(name);
                    editTextEC2Email.setText(email);
                    editTextEC2Phone.setText(phone);
                    editTextEC2Name.setSelection(editTextEC2Name.getText().length());

                    editTextEC2Name.setError(null);
                    editTextEC2Email.setError(null);
                    editTextEC2Phone.setError(null);

                    break;
            }
        }
        else{
            Toast.makeText(this, "User cancelled the process", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchContactInfo(Intent data){
        try {
            name = "";
            email = "";
            phone = "";

            Uri contactData = data.getData();
            Cursor c = managedQuery(contactData, null, null, null, null);
            if (c.moveToFirst()) {
                String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if (hasPhone.equalsIgnoreCase("1")) {
                    Cursor phones = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                        null, null);
                    phones.moveToFirst();
                    phone = phones.getString(phones.getColumnIndex("data1"));
                    phone = phone.replaceAll(" ", "");
                    phone = Utils.retrievePhoneNumberTenChars(phone);
                }

                Cursor emailCur = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                    new String[]{id}, null);
                while (emailCur.moveToNext()) {
                    String email = emailCur.getString(
                        emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    String emailType = emailCur.getString(
                        emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                    System.out.println("Email " + email + " Email Type : " + emailType);
                    EmergencyContactsActivity.this.email = email;
                    break;
                }
                emailCur.close();

                name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            }
        } catch (Exception e) {
            e.printStackTrace();
            name = "";
            email = "";
            phone = "";
        }
    }


    public void performBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public void onBackPressed() {
        performBackPressed();
    }


    @Override
    protected void onDestroy() {
        refreshEmergencyContacts = null;
        ASSL.closeActivity(relative);
        System.gc();
        super.onDestroy();
    }

    @Override
    public void refreshEmergencyContacts() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    setEmergencyContacts();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
