package product.clicklabs.jugnoo.emergency.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.country.picker.Country;
import com.country.picker.CountryPicker;
import com.country.picker.OnCountryPickerListener;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.tokenautocomplete.FilteredArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiEmergencyContactsList;
import product.clicklabs.jugnoo.apis.ApiEmergencySendRideStatus;
import product.clicklabs.jugnoo.datastructure.EmergencyContact;
import product.clicklabs.jugnoo.emergency.ContactsFetchAsync;
import product.clicklabs.jugnoo.emergency.EmergencyActivity;
import product.clicklabs.jugnoo.emergency.FragTransUtils;
import product.clicklabs.jugnoo.emergency.adapters.ContactsListAdapter;
import product.clicklabs.jugnoo.emergency.models.ContactBean;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.permission.PermissionCommon;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * For
 * <p>
 * Created by shankar on 2/22/16.
 */

@SuppressLint("ValidFragment")
public class EmergencyContactOperationsFragment extends Fragment implements GAAction, GACategory {

    private RelativeLayout relative;

    private TextView textViewTitle, textViewSend;
    private ImageView imageViewBack;

    private LinearLayout linearLayoutMain;
    private TextView textViewScroll;

    private LinearLayout linearLayoutEmergencyContacts;
    private RecyclerView recyclerViewEmergencyContacts;
    private ImageView imageViewEmergencyContactsSep;
    private RelativeLayout relativeLayoutOr;
    private TextView textViewOr;
    private Button buttonAddContact;

    private AutoCompleteTextView editTextPhoneContacts;
    private RecyclerView recyclerViewPhoneContacts;

    private ContactsListAdapter emergencyContactsListAdapter, phoneContactsListAdapter;
    private ArrayList<ContactBean> emergencyContactBeans, phoneContactBeans;
    private ArrayAdapter<ContactBean> phoneContactsArrayAdapter;

    private String engagementId;
    private ContactsListAdapter.ListMode listMode;

    private View rootView;
    private FragmentActivity activity;
    private static final String LIST_MODE = "listMode";
    private Dialog dialog;
    private boolean isFromEdiText;
    private PermissionCommon permissionCommon ;
    private static final int REQUEST_CODE_ADD_CONTACT = 50;
    private static final int REQUEST_CODE_VIEW_CONTACTS = 51;
    private static final int REQUEST_CODE_VIEW_CONTACTS_ON_CREATE = 52;
    private PermissionCommon.PermissionListener permissionListener = new PermissionCommon.PermissionListener() {
                @Override
                public void permissionGranted(int requestCode) {

                    isGrantCalled = true;
                    rootView.findViewById(R.id.llPermission).setVisibility(View.GONE);
                    rootView.findViewById(R.id.layoutContacts).setVisibility(View.VISIBLE);
                    new ContactsFetchAsync(activity, phoneContactBeans, new ContactsFetchAsync.Callback() {
                        @Override
                        public void onPreExecute() {
                        }

                        @Override
                        public void onPostExecute(ArrayList<ContactBean> contactBeans) {
                            phoneContactsListAdapter.notifyDataSetChanged();
                        }

						@Override
						public void onCancel() {
							performBackPressed();
						}
					}).execute();

                    if (requestCode == REQUEST_CODE_ADD_CONTACT) {
                        new FragTransUtils().openAddEmergencyContactsFragment(activity,
                                ((EmergencyActivity) activity).getContainer());
                    }

                }

                @Override
                public boolean permissionDenied(int requestCode, boolean neverAsk) {

                    if (requestCode == REQUEST_CODE_VIEW_CONTACTS) {

                        if (neverAsk) {
                            PermissionCommon.openSettingsScreen(activity);
                        }

                        return false;

                    } else if (requestCode == REQUEST_CODE_VIEW_CONTACTS_ON_CREATE) {
                        return false;
                    }

                    return true;
                }

                @Override
                public void onRationalRequestIntercepted(int requestCode) {

                }
            };;
    private boolean firstTime;
    private boolean isGrantCalled;

    public static EmergencyContactOperationsFragment newInstance(String engagementId, ContactsListAdapter.ListMode listMode) {
        EmergencyContactOperationsFragment emergencyContactOperationsFragment = new EmergencyContactOperationsFragment();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_ENGAGEMENT_ID, engagementId);
        bundle.putInt(LIST_MODE, listMode.getOrdinal());
        emergencyContactOperationsFragment.setArguments(bundle);

        return emergencyContactOperationsFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
//		FlurryAgent.init(activity, Config.getFlurryKey());
//		FlurryAgent.onStartSession(activity, Config.getFlurryKey());
//		FlurryAgent.onEvent(EmergencyContactOperationsFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
        super.onStop();
//		FlurryAgent.onEndSession(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(activity);
        if(permissionCommon!=null){
            if(PermissionCommon.isGranted(Manifest.permission.READ_CONTACTS,activity) && !isGrantCalled){
                permissionListener.permissionGranted(REQUEST_CODE_VIEW_CONTACTS);
             }else if(firstTime){
                firstTime = false;
                permissionCommon.getPermission(REQUEST_CODE_VIEW_CONTACTS_ON_CREATE,PermissionCommon.SKIP_RATIONAL_MESSAGE,Manifest.permission.READ_CONTACTS);

            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_emergency_contacts_operations, container, false);

        this.engagementId = getArguments().getString(Constants.KEY_ENGAGEMENT_ID, "");
        int listModeInt = getArguments().getInt(LIST_MODE, ContactsListAdapter.ListMode.ADD_CONTACTS.getOrdinal());
        if (listModeInt == ContactsListAdapter.ListMode.ADD_CONTACTS.getOrdinal()) {
            listMode = ContactsListAdapter.ListMode.ADD_CONTACTS;
        } else if (listModeInt == ContactsListAdapter.ListMode.EMERGENCY_CONTACTS.getOrdinal()) {
            listMode = ContactsListAdapter.ListMode.EMERGENCY_CONTACTS;
        } else if (listModeInt == ContactsListAdapter.ListMode.DELETE_CONTACTS.getOrdinal()) {
            listMode = ContactsListAdapter.ListMode.DELETE_CONTACTS;
        } else if (listModeInt == ContactsListAdapter.ListMode.CALL_CONTACTS.getOrdinal()) {
            listMode = ContactsListAdapter.ListMode.CALL_CONTACTS;
        } else if (listModeInt == ContactsListAdapter.ListMode.SEND_RIDE_STATUS.getOrdinal()) {
            listMode = ContactsListAdapter.ListMode.SEND_RIDE_STATUS;
        }

        activity = getActivity();
        permissionCommon = new PermissionCommon(this).setCallback(permissionListener);
        relative = (RelativeLayout) rootView.findViewById(R.id.relative);
        try {
            new ASSL(activity, relative, 1134, 720, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.avenirNext(activity));
        imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
        textViewSend = (TextView) rootView.findViewById(R.id.textViewSend);
        textViewSend.setTypeface(Fonts.mavenRegular(activity));

        linearLayoutMain = (LinearLayout) rootView.findViewById(R.id.linearLayoutMain);
        textViewScroll = (TextView) rootView.findViewById(R.id.textViewScroll);

        linearLayoutEmergencyContacts = (LinearLayout) rootView.findViewById(R.id.linearLayoutEmergencyContacts);
        ((TextView) rootView.findViewById(R.id.textViewEmergencyContacts)).setTypeface(Fonts.mavenLight(activity));
        imageViewEmergencyContactsSep = (ImageView) rootView.findViewById(R.id.imageViewEmergencyContactsSep);
        relativeLayoutOr = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutOr);
        textViewOr = (TextView) rootView.findViewById(R.id.textViewOr);
        textViewOr.setTypeface(Fonts.mavenLight(activity));
        buttonAddContact = (Button) rootView.findViewById(R.id.buttonAddContact);
        buttonAddContact.setTypeface(Fonts.mavenRegular(activity));
        ((Button)rootView.findViewById(R.id.buttonGrantPermission)).setTypeface(Fonts.mavenRegular(activity));
        ((TextView)rootView.findViewById(R.id.text_permission)).setTypeface(Fonts.mavenRegular(activity));
        buttonAddContact.setVisibility(View.GONE);
        relativeLayoutOr.setVisibility(View.GONE);

        recyclerViewEmergencyContacts = (RecyclerView) rootView.findViewById(R.id.recyclerViewEmergencyContacts);
        recyclerViewEmergencyContacts.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewEmergencyContacts.setItemAnimator(new DefaultItemAnimator());
        recyclerViewEmergencyContacts.setHasFixedSize(false);

        textViewTitle.getPaint().setShader(Utils.textColorGradient(activity, textViewTitle));

        emergencyContactBeans = new ArrayList<>();
        emergencyContactsListAdapter = new ContactsListAdapter(emergencyContactBeans, activity, R.layout.list_item_contact,
                new ContactsListAdapter.Callback() {
                    @Override
                    public void contactClicked(int position, ContactBean contactBean) {
                        contactCalledAccToListMode(contactBean);
                    }
                }, listMode);
        recyclerViewEmergencyContacts.setAdapter(emergencyContactsListAdapter);


        ((TextView) rootView.findViewById(R.id.textViewPhoneContacts)).setTypeface(Fonts.mavenLight(activity));
        editTextPhoneContacts = (AutoCompleteTextView) rootView.findViewById(R.id.editTextPhoneContacts);
        editTextPhoneContacts.setTypeface(Fonts.mavenLight(activity));
        recyclerViewPhoneContacts = (RecyclerView) rootView.findViewById(R.id.recyclerViewPhoneContacts);
        recyclerViewPhoneContacts.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewPhoneContacts.setItemAnimator(new DefaultItemAnimator());
        recyclerViewPhoneContacts.setHasFixedSize(false);

        phoneContactBeans = new ArrayList<>();
        phoneContactsListAdapter = new ContactsListAdapter(phoneContactBeans, activity, R.layout.list_item_contact,
                new ContactsListAdapter.Callback() {
                    @Override
                    public void contactClicked(int position, ContactBean contactBean) {
                        isFromEdiText = false;
                        dialogConfirmEmergencyContact(activity, activity.getString(R.string.confirm) + " " + activity.getString(R.string.emergency_contacts), "",
                                false, contactBean);
                        //contactCalledAccToListMode(contactBean);
                    }
                }, listMode);
        recyclerViewPhoneContacts.setAdapter(phoneContactsListAdapter);

        phoneContactsArrayAdapter = new FilteredArrayAdapter<ContactBean>(this.getContext(), R.layout.list_item_contact,
                ((List<ContactBean>) phoneContactBeans)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = l.inflate(R.layout.list_item_contact, parent, false);
                    ListView.LayoutParams layoutParams = new ListView.LayoutParams(640, 128);
                    convertView.setLayoutParams(layoutParams);

                    ASSL.DoMagic(convertView);
                }


                ContactBean p = getItem(position);
                ((TextView) convertView.findViewById(R.id.textViewContactName)).setTypeface(Fonts.mavenLight(activity));
                ((TextView) convertView.findViewById(R.id.textViewContactNumberType)).setTypeface(Fonts.mavenLight(activity));
                ((TextView) convertView.findViewById(R.id.textViewContactName)).setText(p.getName());
                ((TextView) convertView.findViewById(R.id.textViewContactNumberType)).setText(p.getPhoneNo() + " " + p.getType());
                convertView.findViewById(R.id.imageViewOption).setVisibility(View.GONE);

                convertView.findViewById(R.id.relative).setTag(position);
                convertView.findViewById(R.id.relative).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int position = (int) v.getTag();
                            ContactBean p = getItem(position);
                            editTextPhoneContacts.dismissDropDown();
                            isFromEdiText = true;
                            dialogConfirmEmergencyContact(activity, activity.getString(R.string.confirm) + " " + activity.getString(R.string.emergency_contacts), "",
                                    false, p);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


                return convertView;
            }

            @Override
            protected boolean keepObject(ContactBean person, String mask) {
//                mask = mask.toLowerCase();
//                boolean matched = person.getName().toLowerCase().startsWith(mask)
//                        || person.getPhoneNo().toLowerCase().startsWith(mask);
//                return matched;

                if(mask.length() > 2) {
                    mask = mask.toLowerCase();

                    return person.getName().toLowerCase().contains(mask)
                            || person.getPhoneNo().toLowerCase().contains(mask);
                } else{
                    return false;
                }





            }

        };
        editTextPhoneContacts.setAdapter(phoneContactsArrayAdapter);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.imageViewBack:
                        if (ContactsListAdapter.ListMode.SEND_RIDE_STATUS == listMode) {
                        } else {
                        }
                        performBackPressed();
                        break;

                    case R.id.textViewSend:
                        if (ContactsListAdapter.ListMode.SEND_RIDE_STATUS == listMode) {
                            clickOnSend();
                            GAUtils.event(RIDES, SEND_RIDE_STATUS, SEND + CLICKED);
                        }
                        break;

                    case R.id.linearLayoutMain:
                        Utils.hideSoftKeyboard(activity, editTextPhoneContacts);
                        break;

                    case R.id.buttonAddContact:
                        permissionCommon.getPermission(REQUEST_CODE_ADD_CONTACT, Manifest.permission.READ_CONTACTS);
                        break;
                    case R.id.buttonGrantPermission:
                        permissionCommon.getPermission(REQUEST_CODE_VIEW_CONTACTS,PermissionCommon.SKIP_RATIONAL_MESSAGE,Manifest.permission.READ_CONTACTS);
                        break;
                }
            }
        };


        imageViewBack.setOnClickListener(onClickListener);
        textViewSend.setOnClickListener(onClickListener);
        linearLayoutMain.setOnClickListener(onClickListener);
        buttonAddContact.setOnClickListener(onClickListener);
        rootView.findViewById(R.id.buttonGrantPermission).setOnClickListener(onClickListener);

        setEmergencyContactsToList();



//        KeyboardLayoutListener keyboardLayoutListener = new KeyboardLayoutListener(linearLayoutMain, textViewScroll,
//                new KeyboardLayoutListener.KeyBoardStateHandler() {
//                    @Override
//                    public void keyboardOpened() {
//                        linearLayoutEmergencyContacts.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void keyBoardClosed() {
//                        linearLayoutEmergencyContacts.setVisibility(View.VISIBLE);
//                    }
//                });
//        keyboardLayoutListener.setResizeTextView(false);
//        linearLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);


        if (ContactsListAdapter.ListMode.SEND_RIDE_STATUS == listMode) {
            textViewSend.setVisibility(View.VISIBLE);
            textViewTitle.setText(activity.getResources().getString(R.string.send_ride_status));
        } else if (ContactsListAdapter.ListMode.CALL_CONTACTS == listMode) {
            textViewSend.setVisibility(View.GONE);
            textViewTitle.setText(activity.getResources().getString(R.string.call_your_contacts));
        }

        if(!PermissionCommon.isGranted(Manifest.permission.READ_CONTACTS,getActivity())){
            rootView.findViewById(R.id.layoutContacts).setVisibility(View.GONE);
            rootView.findViewById(R.id.llPermission).setVisibility(View.VISIBLE);

        }
        firstTime = true;

        return rootView;
    }


    private void performBackPressed() {
        if (activity instanceof EmergencyActivity) {
            ((EmergencyActivity) activity).performBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ASSL.closeActivity(rootView);
        System.gc();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getAllEmergencyContacts();
        }
    }


    private void setEmergencyContactsToList() {
        if (Data.userData.getEmergencyContactsList() != null) {
            emergencyContactBeans.clear();
            for (EmergencyContact emergencyContact : Data.userData.getEmergencyContactsList()) {
                ContactBean contactBean = new ContactBean(emergencyContact.name, emergencyContact.phoneNo,emergencyContact.countryCode, "",
                        ContactBean.ContactBeanViewType.CONTACT, null, null);
                contactBean.setId(emergencyContact.id);
                emergencyContactBeans.add(contactBean);
            }
            emergencyContactsListAdapter.notifyDataSetChanged();
            if (emergencyContactBeans.size() > 0) {
                imageViewEmergencyContactsSep.setVisibility(View.VISIBLE);
                relativeLayoutOr.setVisibility(View.GONE);
                buttonAddContact.setVisibility(View.GONE);
            } else {
                imageViewEmergencyContactsSep.setVisibility(View.GONE);
                relativeLayoutOr.setVisibility(View.VISIBLE);
                buttonAddContact.setVisibility(View.VISIBLE);
                if (listMode == ContactsListAdapter.ListMode.SEND_RIDE_STATUS) {
                    textViewOr.setText(activity.getResources().getString(R.string.or_send_directly));
                } else if (listMode == ContactsListAdapter.ListMode.CALL_CONTACTS) {
                    textViewOr.setText(activity.getResources().getString(R.string.or_call_directly));
                }
            }
        } else {
            imageViewEmergencyContactsSep.setVisibility(View.GONE);
            relativeLayoutOr.setVisibility(View.VISIBLE);
            buttonAddContact.setVisibility(View.VISIBLE);
            if (listMode == ContactsListAdapter.ListMode.SEND_RIDE_STATUS) {
                textViewOr.setText(activity.getResources().getString(R.string.or_send_directly));
            } else if (listMode == ContactsListAdapter.ListMode.CALL_CONTACTS) {
                textViewOr.setText(activity.getResources().getString(R.string.or_call_directly));
            }
        }
    }

    private void setSelectedObject(boolean selected, ContactBean contactBean) {
        try {
            int index = phoneContactBeans.indexOf(new ContactBean(contactBean.getName(),
                    contactBean.getPhoneNo(),contactBean.getCountryCode(), contactBean.getType(), ContactBean.ContactBeanViewType.CONTACT, null, null));
            phoneContactBeans.get(index).setSelected(selected);
            phoneContactsListAdapter.notifyDataSetChanged();
            ((LinearLayoutManager) recyclerViewPhoneContacts.getLayoutManager()).scrollToPositionWithOffset(index, 20);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getAllEmergencyContacts() {
        new ApiEmergencyContactsList(activity, new ApiEmergencyContactsList.Callback() {
            @Override
            public void onSuccess() {
                setEmergencyContactsToList();
            }

            @Override
            public void onFailure() {

            }

            @Override
            public void onRetry(View view) {
                getAllEmergencyContacts();
            }

            @Override
            public void onNoRetry(View view) {

            }
        }).emergencyContactsList();
    }

    private void clickOnSend() {
        final ArrayList<ContactBean> contacts = new ArrayList<>();
        for (ContactBean contactBean : emergencyContactBeans) {
            if (contactBean.isSelected()) {
                contacts.add(contactBean);
            }
        }
        for (ContactBean contactBean : phoneContactBeans) {
            if (contactBean.isSelected()) {
                contacts.add(contactBean);
            }
        }

        if (contacts.size() == 0) {
            DialogPopup.alertPopup(activity, "",
                    activity.getResources().getString(R.string.send_ride_status_no_contacts_message));
        } else if (contacts.size() > 5) {
            DialogPopup.alertPopupTwoButtonsWithListeners(activity,
                    "",
                    String.format(activity.getResources()
                                    .getString(R.string.send_ride_status_more_contacts_message_format),
                            "" + EmergencyActivity.MAX_EMERGENCY_CONTACTS_TO_SEND_RIDE_STATUS,
                            "" + EmergencyActivity.MAX_EMERGENCY_CONTACTS_TO_SEND_RIDE_STATUS),
                    activity.getResources().getString(R.string.ok),
                    activity.getResources().getString(R.string.cancel),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendRideStatusApi(engagementId, contacts);
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, true, false);
        } else {
            sendRideStatusApi(engagementId, contacts);
        }
    }

    private void sendRideStatusApi(final String engagementId, final ArrayList<ContactBean> contacts) {
        new ApiEmergencySendRideStatus(activity, new ApiEmergencySendRideStatus.Callback() {
            @Override
            public void onSuccess(String message) {
                DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performBackPressed();
                    }
                });
            }

            @Override
            public void onFailure() {

            }

            @Override
            public void onRetry(View view) {
                sendRideStatusApi(engagementId, contacts);
            }

            @Override
            public void onNoRetry(View view) {

            }
        }).emergencySendRideStatusMessage(engagementId, contacts);
    }


    private boolean contactCalledAccToListMode(ContactBean contactBean) {
        if (ContactsListAdapter.ListMode.CALL_CONTACTS == listMode) {
            Utils.openCallIntent(activity, contactBean.getPhoneNo());
            return true;
        } else {
            return false;
        }
    }

    public void dialogConfirmEmergencyContact(final FragmentActivity activity, String title, String message,
                                              final boolean cancellable, final ContactBean contactBean) {
        try {
            dismissAlertPopup();

            dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
            dialog.setContentView(R.layout.dialog_confirm_emergency_contact);

            RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
            new ASSL(activity, frameLayout, 1134, 720, false);

            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.6f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(cancellable);
            dialog.setCanceledOnTouchOutside(cancellable);


            TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
            textHead.setTypeface(Fonts.mavenRegular(activity));
            TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
            textMessage.setTypeface(Fonts.mavenLight(activity));


            RelativeLayout rlPhone = (RelativeLayout) dialog.findViewById(R.id.rlPhone);
            LinearLayout llCountryCode = (LinearLayout) dialog.findViewById(R.id.llCountryCode);
            final TextView tvCountryCode = (TextView) dialog.findViewById(R.id.tvCountryCode);
            tvCountryCode.setTypeface(Fonts.mavenRegular(activity));

            final EditText editTextPhoneNumber = (EditText) dialog.findViewById(R.id.editTextPhoneNumber);
            final CountryPicker countryPicker =
                    new CountryPicker.Builder().with(activity)
                            .listener(new OnCountryPickerListener() {
                                @Override
                                public void onSelectCountry(Country country) {
                                    tvCountryCode.setText(country.getDialCode());
                                }
                            })
                            .build();
            tvCountryCode.setText(Utils.getCountryCode(activity));
            if (countryPicker.getAllCountries().size() > 1) {
                llCountryCode.setEnabled(true);
                tvCountryCode.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_vector_otp, 0);
            } else {
                llCountryCode.setEnabled(false);
                tvCountryCode.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            }
            llCountryCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    countryPicker.showDialog(activity.getSupportFragmentManager());
                }
            });
            editTextPhoneNumber.setText(contactBean.getPhoneNo().replaceFirst("^0+(?!$)", ""));
            editTextPhoneNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().startsWith("0")) {
                        if (s.length() > 1) {
                            editTextPhoneNumber.setText(s.toString().substring(1));
                        } else {
                            editTextPhoneNumber.setText("");
                        }
                        Toast.makeText(activity, "Phone number should not start with 0", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            textMessage.setMovementMethod(LinkMovementMethod.getInstance());
            textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

            textHead.setText(title);
            textMessage.setText(message);

            Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
            btnOk.setTypeface(Fonts.mavenRegular(activity));
            ImageView btnClose = (ImageView) dialog.findViewById(R.id.close);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contactBean.setCountryCode(tvCountryCode.getText().toString());
                    if (editTextPhoneNumber.getText().toString().substring(0, tvCountryCode.getText().toString().length()).equalsIgnoreCase(tvCountryCode.getText().toString())) {
                        contactBean.setPhoneNo(editTextPhoneNumber.getText().toString());
                    } else {
                        contactBean.setPhoneNo(tvCountryCode.getText().toString() + editTextPhoneNumber.getText().toString());
                    }
                    if (isFromEdiText) {
                        if (!contactCalledAccToListMode(contactBean)) {
                            setSelectedObject(true, contactBean);
                        }
                    } else {
                        contactCalledAccToListMode(contactBean);
                    }
                    contactBean.setSelected(true);
                    phoneContactsListAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            });

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    contactBean.setSelected(false);
                    phoneContactsListAdapter.setCountAndNotify();
//                    phoneContactsListAdapter.notifyDataSetChanged();
                    //	try { editTextContacts.removeObject(contactBean); } catch (Exception ignored) {}
                }
            });

            frameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cancellable) {
                        dialog.dismiss();
                    }
                }
            });

            dialog.findViewById(R.id.linearLayoutInner).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dismissAlertPopup() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(permissionCommon!=null)permissionCommon.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }


}
