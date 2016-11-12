package product.clicklabs.jugnoo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.FetchChatResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


/**
 * Created by ankit on 10/11/16.
 */

public class ChatActivity extends Activity implements View.OnClickListener{

    private RelativeLayout relative;
    private TextView textViewTitle;
    private ImageView imageViewBack;
	private EditText input;
	private int position = 0;
	private ImageView send;

	RecyclerView recyclerViewChat;
	ChatAdapter chatAdapter;
	ArrayList<FetchChatResponse.ChatHistory> chatResponse = new ArrayList<>();
	private FetchChatResponse fetchChatResponse;
	private SimpleDateFormat sdf;
	private final String LOGIN_TYPE = "0";
	private Handler handler = new Handler();
	private Handler myHandler=new Handler();


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

			recyclerViewChat = (RecyclerView) findViewById(R.id.recyclerViewChat);
			recyclerViewChat.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
			recyclerViewChat.setHasFixedSize(false);
			recyclerViewChat.setItemAnimator(new DefaultItemAnimator());
			chatResponse = new ArrayList<>();
			chatAdapter = new ChatAdapter(ChatActivity.this, chatResponse);
			recyclerViewChat.setAdapter(chatAdapter);

			// done action listener to send the chat
			input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        sendChat();
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
				sendChat();
				break;

        }
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
	private void sendChat() {
		//hideKeyboard(input);
		String inputText = input.getText().toString().trim();
		Calendar time = Calendar.getInstance();
		if (!(inputText.isEmpty())) {
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
								//updateListData(getResources().getString(R.string.add_cash), false);
								if(handler != null && loadDiscussion != null) {
									handler.postDelayed(loadDiscussion, 15000);
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
