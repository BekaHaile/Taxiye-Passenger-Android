package product.clicklabs.jugnoo;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import product.clicklabs.jugnoo.adapters.ChatAdapter;
import product.clicklabs.jugnoo.adapters.ChatSuggestionAdapter;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.models.RideTypeValue;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.FetchChatResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static product.clicklabs.jugnoo.Constants.ACTIVATION;
import static product.clicklabs.jugnoo.Constants.RETENTION;
import static product.clicklabs.jugnoo.Constants.REVENUE;
import static product.clicklabs.jugnoo.Constants.SLASH;
import static product.clicklabs.jugnoo.home.HomeActivity.passengerScreenMode;
import static product.clicklabs.jugnoo.utils.FirebaseEvents.ACCEPT_RIDE;
import static product.clicklabs.jugnoo.utils.FirebaseEvents.AUTO;
import static product.clicklabs.jugnoo.utils.FirebaseEvents.CALL_DRIVER;
import static product.clicklabs.jugnoo.utils.FirebaseEvents.DRIVER_ARRIVED;
import static product.clicklabs.jugnoo.utils.FirebaseEvents.POOL;
import static product.clicklabs.jugnoo.utils.FirebaseEvents.RIDE_START;
import static product.clicklabs.jugnoo.utils.FirebaseEvents.TRANSACTION;
import static product.clicklabs.jugnoo.utils.FlurryEventNames.CALL_TO_DRIVER_MADE_WHEN_ARRIVED;
import static product.clicklabs.jugnoo.utils.FlurryEventNames.CALL_TO_DRIVER_MADE_WHEN_NOT_ARRIVED;


/**
 * Created by ankit on 10/11/16.
 */

public class ChatActivity extends BaseFragmentActivity implements View.OnClickListener{

    private RelativeLayout relative;
    private TextView textViewTitle;
    private ImageView imageViewBack;
	private EditText input;
	private int position = 0;
	private ImageView send, ivCallDriver;

	RecyclerView recyclerViewChat, recyclerViewChatOptions;
	ChatAdapter chatAdapter;
	ArrayList<FetchChatResponse.ChatHistory> chatResponse = new ArrayList<>();
	private FetchChatResponse fetchChatResponse;
	private SimpleDateFormat sdf;
	private final String LOGIN_TYPE = "0";
	private Handler handler = new Handler();
	private Handler myHandler=new Handler();
	ChatSuggestionAdapter chatSuggestionAdapter;
	ArrayList<FetchChatResponse.Suggestion> chatSuggestions = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

		try {
			relative = (RelativeLayout) findViewById(R.id.relative);
			new ASSL(this, relative, 1134, 720, false);

			sdf = new SimpleDateFormat("hh:mm a");
			textViewTitle = (TextView) findViewById(R.id.textViewTitle);
			textViewTitle.setTypeface(Fonts.avenirNext(this));
			textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));
			input = (EditText) findViewById(R.id.input);
			send = (ImageView) findViewById(R.id.action_send);
			imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
			imageViewBack.setOnClickListener(this);
			ivCallDriver = (ImageView) findViewById(R.id.ivCallDriver); ivCallDriver.setOnClickListener(this);

			recyclerViewChat = (RecyclerView) findViewById(R.id.recyclerViewChat);
			recyclerViewChat.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
			recyclerViewChat.setHasFixedSize(false);
			recyclerViewChat.setItemAnimator(new DefaultItemAnimator());
			chatResponse = new ArrayList<>();
			chatAdapter = new ChatAdapter(ChatActivity.this, chatResponse);
			recyclerViewChat.setAdapter(chatAdapter);

			recyclerViewChatOptions = (RecyclerView) findViewById(R.id.recyclerViewChatOptions);
			recyclerViewChatOptions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
			recyclerViewChatOptions.setItemAnimator(new DefaultItemAnimator());
			recyclerViewChatOptions.setHasFixedSize(false);
			chatSuggestions = new ArrayList<>();
			chatSuggestionAdapter = new ChatSuggestionAdapter(this, chatSuggestions,  new ChatSuggestionAdapter.Callback() {
				@Override
				public void onSuggestionClick(int position, FetchChatResponse.Suggestion suggestion) {
					if(AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
						sendChat(suggestion.getSuggestion());
						chatSuggestions.remove(position);
					}
				}
			});
			recyclerViewChatOptions.setAdapter(chatSuggestionAdapter);
			if(chatSuggestions.size() > 0){
				recyclerViewChatOptions.setVisibility(View.VISIBLE);
			} else{
				recyclerViewChatOptions.setVisibility(View.GONE);
			}
			Prefs.with(ChatActivity.this).save(Constants.KEY_CHAT_COUNT, 0);
			GCMIntentService.clearNotifications(this);
			// done action listener to send the chat
			input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
						if(input.getText().toString().trim().length() > 0 && (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext()))) {
							sendChat(input.getText().toString().trim());
						}
                        return true;
                    }
                    return false;
                }
            });

			input.setOnClickListener(this);
			send.setOnClickListener(this);

			fetchChat(ChatActivity.this);

			if(Data.autoData.getAssignedDriverInfo() != null) {
                textViewTitle.setText(Data.autoData.getAssignedDriverInfo().name);
            }


		} catch (Exception e) {
			e.printStackTrace();
		}

		//myHandler.postAtTime(loadDiscussion, 5000);
    }

	Runnable loadDiscussion=new Runnable() {
		@Override
		public void run() {
			loadDiscussions();
			//myHandler.postAtTime(loadDiscussion, 5000);
		}
	};

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			loadDiscussions();
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		Data.context = ChatActivity.this;
	}

	public void updateListData(String message, boolean errorOccurred) {
		if (errorOccurred) {
			chatAdapter.notifyDataSetChanged();
		} else {
			chatAdapter.notifyDataSetChanged();
		}
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageViewBack:
                performBackPressed();
            	break;
			case R.id.action_send:
				if(input.getText().toString().trim().length() > 0 && (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext()))) {
					sendChat(input.getText().toString().trim());
				}
				break;
			case R.id.ivCallDriver:
				Utils.callDriverDuringRide(ChatActivity.this);
				break;
        }
    }


	@Override
	public void onBackPressed() {
		performBackPressed();
		//super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Data.context = null;
		try {
			if(handler != null){
                handler.removeCallbacks(loadDiscussion);
            }
			if(myHandler != null){
                myHandler.removeCallbacks(loadDiscussion);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// sends the message to server and display it
	private void sendChat(String inputText) {
		//hideKeyboard(input);
		//String inputText = input.getText().toString().trim();
		Calendar time = Calendar.getInstance();
		if (!(inputText.isEmpty()) && fetchChatResponse != null) {
			// add message to list
			FetchChatResponse.ChatHistory chatHistory = fetchChatResponse.new ChatHistory();
			chatHistory.setChatHistoryId(0);
			chatHistory.setCreatedAt(sdf.format(time.getTime()));
			chatHistory.setIsSender(1);
			chatHistory.setMessage(inputText);
			chatResponse.add(chatHistory);
			position = chatAdapter.getItemCount() - 1;
			chatAdapter.notifyItemInserted(position);

			// scroll to the last message
			recyclerViewChat.scrollToPosition(position);
			postChat(ChatActivity.this, inputText);
			input.setText("");
		}
	}

    public void performBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }


	private void loadDiscussions(){
		if (!AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			DialogPopup.dialogNoInternet(ChatActivity.this,
					Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
					new Utils.AlertCallBackWithButtonsInterface() {
						@Override
						public void positiveClick(View v) {
							loadDiscussions();
						}

						@Override
						public void neutralClick(View v) {
						}

						@Override
						public void negativeClick(View v) {
							performBackPressed();
						}
					});
			return;
		}

		fetchChat(ChatActivity.this);
	}

    private void fetchChat(final Activity activity) {
        try {
            if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                //DialogPopup.showLoadingDialog(ChatActivity.this, getResources().getString(R.string.loading));
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put("login_type", LOGIN_TYPE);
                params.put("engagement_id", Data.autoData.getcEngagementId());

                RestClient.getChatApiService().fetchChat(params, new Callback<FetchChatResponse>() {
					@Override
					public void success(FetchChatResponse fetchChat, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							//Log.e("Shared rides jsonString", "=" + jsonString);
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							int flag = jObj.getInt("flag");
							String message = jObj.getString("message");
							fetchChatResponse = fetchChat;
							if (!jObj.isNull("error")) {
								String errorMessage = jObj.getString("error");
							} else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == fetchChat.getFlag()) {
								chatResponse.clear();
								chatResponse.addAll(fetchChat.getChatHistory());
								Collections.reverse(chatResponse);
								chatAdapter.notifyDataSetChanged();
								recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
								chatSuggestions.clear();
								chatSuggestions.addAll(fetchChat.getSuggestions());
								chatSuggestionAdapter.notifyDataSetChanged();
								if(chatSuggestions.size() > 0){
									recyclerViewChatOptions.setVisibility(View.VISIBLE);
								} else{
									recyclerViewChatOptions.setVisibility(View.GONE);
								}
								if(handler != null && loadDiscussion != null) {
									handler.postDelayed(loadDiscussion, 5000);
								}
							}
						} catch (Exception exception) {
							exception.printStackTrace();
						}
						//DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						//DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
					}
				});

            } else {
                DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //DialogPopup.dismissLoadingDialog();
        }
    }

    private void postChat(final Activity activity, final String message) {
        try {
            if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                //DialogPopup.showLoadingDialog(ChatActivity.this, getResources().getString(R.string.loading));
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put("login_type", LOGIN_TYPE);
                params.put("engagement_id", Data.autoData.getcEngagementId());
                params.put("message", message);

                RestClient.getChatApiService().postChat(params, new Callback<FetchChatResponse>() {
					@Override
					public void success(FetchChatResponse fetchChatResponse, Response response) {
						try {
							DialogPopup.dismissLoadingDialog();
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							//Log.e("Shared rides jsonString", "=" + jsonString);
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							int flag = jObj.getInt("flag");
							String message = jObj.getString("message");
							if (!jObj.isNull("error")) {
								String errorMessage = jObj.getString("error");
							} else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								if(handler != null && loadDiscussion != null) {
									handler.removeCallbacks(loadDiscussion);
								}
									fetchChat(ChatActivity.this);
							}
						} catch (Exception exception) {
							exception.printStackTrace();
						}

					}

					@Override
					public void failure(RetrofitError error) {
						//DialogPopup.dismissLoadingDialog();
					}
				});

            } else {
                DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //DialogPopup.dismissLoadingDialog();
        }
    }
}
