package product.clicklabs.jugnoo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import product.clicklabs.jugnoo.adapters.ChatAdapter;
import product.clicklabs.jugnoo.adapters.ChatSuggestionAdapter;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.model.FetchChatResponse;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by ankit on 10/11/16.
 */

public class ChatActivity extends BaseFragmentActivity implements View.OnClickListener, GAAction, GACategory{

	private EditText input;

	RecyclerView recyclerViewChat, recyclerViewChatOptions;
	LinearLayoutManager linearLayoutManager;
	ChatAdapter chatAdapter;
	ArrayList<FetchChatResponse.ChatHistory> chatResponse = new ArrayList<>();
	private final String LOGIN_TYPE = "0";
	private Handler handler = new Handler();
	ChatSuggestionAdapter chatSuggestionAdapter;
	ArrayList<FetchChatResponse.Suggestion> chatSuggestions = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

		try {

			TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
			textViewTitle.setTypeface(Fonts.avenirNext(this));
			textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));
			input = (EditText) findViewById(R.id.input);
			ImageView send = (ImageView) findViewById(R.id.action_send);
			ImageView imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
			imageViewBack.setOnClickListener(this);
			ImageView ivCallDriver = (ImageView) findViewById(R.id.ivCallDriver);
			ivCallDriver.setOnClickListener(this);

			recyclerViewChat = (RecyclerView) findViewById(R.id.recyclerViewChat);
			linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
			recyclerViewChat.setLayoutManager(linearLayoutManager);
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
					if(MyApplication.getInstance().isOnline()) {
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
						sendChatClick();

						return true;
                    }
                    return false;
                }
            });

			input.setOnClickListener(this);
			send.setOnClickListener(this);

			fetchChat();

			if(Data.autoData.getAssignedDriverInfo() != null) {
                textViewTitle.setText(Data.autoData.getAssignedDriverInfo().name);
            }


		} catch (Exception e) {
			e.printStackTrace();
		}

		registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTION_FINISH_ACTIVITY));

    }

	Runnable loadDiscussion=new Runnable() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageViewBack:
                performBackPressed();
            	break;
			case R.id.action_send:
				sendChatClick();
				break;
			case R.id.ivCallDriver:
				Utils.callDriverDuringRide(ChatActivity.this);
				GAUtils.event(RIDES, CHAT, CALL+BUTTON+CLICKED);
				break;
        }
    }

	private void sendChatClick() {
		if(MyApplication.getInstance().isOnline()) {
			if (input.getText().toString().trim().length() > 0) {
				sendChat(input.getText().toString().trim());
			}
		} else{
			DialogPopup.alertPopup(ChatActivity.this, "", getString(R.string.connection_lost_desc));
		}
	}


	@Override
	public void onBackPressed() {
		performBackPressed();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
		Data.context = null;
		if (handler != null && loadDiscussion != null) {
			handler.removeCallbacks(loadDiscussion);
		}
	}

	// sends the message to server and display it
	private void sendChat(String inputText) {
		if (!(inputText.isEmpty())) {
			FetchChatResponse.ChatHistory chatHistory = new FetchChatResponse.ChatHistory();
			chatHistory.setChatHistoryId(0);
			chatHistory.setCreatedAt(DateOperations.getCurrentTimeInUTC());
			chatHistory.setIsSender(1);
			chatHistory.setMessage(inputText);
			chatResponse.add(chatHistory);
			int position = chatAdapter.getItemCount() - 1;
			chatAdapter.notifyItemInserted(position);

			linearLayoutManager.scrollToPositionWithOffset(chatAdapter.getItemCount() - 1, 10);
			postChat(inputText);
			input.setText("");
		}
	}

    public void performBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }


	private void loadDiscussions(){
		if (!MyApplication.getInstance().isOnline()) {
			DialogPopup.dialogNoInternet(ChatActivity.this,
					getString(R.string.connection_lost_title), getString(R.string.connection_lost_desc),
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

		fetchChat();
	}

	private ApiCommon<FetchChatResponse> apiCommon;
	private ApiCommon<FetchChatResponse> getApiCommon(){
		if(apiCommon == null){
			apiCommon = new ApiCommon<>(this);
		}
		return apiCommon;
	}

    private void fetchChat() {
        try {
			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
			params.put("login_type", LOGIN_TYPE);
			params.put("engagement_id", Data.autoData.getcEngagementId());

			new HomeUtil().putDefaultParams(params);

			getApiCommon().showLoader(false).execute(params, ApiName.FETCH_CHAT, new APICommonCallback<FetchChatResponse>() {
				@Override
				public void onSuccess(FetchChatResponse fetchChatResponse, String message, int flag) {
					Prefs.with(ChatActivity.this).save(Constants.KEY_CHAT_COUNT, 0);
					chatResponse.clear();
					chatResponse.addAll(fetchChatResponse.getChatHistory());
					Collections.reverse(chatResponse);
					chatAdapter.notifyDataSetChanged();
					linearLayoutManager.scrollToPositionWithOffset(chatAdapter.getItemCount() - 1, 10);
					chatSuggestions.clear();
					chatSuggestions.addAll(fetchChatResponse.getSuggestions());
					chatSuggestionAdapter.notifyDataSetChanged();
					if(chatSuggestions.size() > 0){
						recyclerViewChatOptions.setVisibility(View.VISIBLE);
					} else{
						recyclerViewChatOptions.setVisibility(View.GONE);
					}
					if(handler != null && loadDiscussion != null) {
						handler.removeCallbacks(loadDiscussion);
						handler.postDelayed(loadDiscussion, 5000);
					}
				}

				@Override
				public boolean onError(FetchChatResponse fetchChatResponse, String message, int flag) {
					return false;
				}
			});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void postChat(final String message) {
        try {
			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
			params.put("login_type", LOGIN_TYPE);
			params.put("engagement_id", Data.autoData.getcEngagementId());
			params.put("message", message);

			new HomeUtil().putDefaultParams(params);
			getApiCommon().showLoader(false).execute(params, ApiName.POST_CHAT, new APICommonCallback<FetchChatResponse>() {
				@Override
				public void onSuccess(FetchChatResponse fetchChatResponse, String message, int flag) {
					if(handler != null && loadDiscussion != null) {
						handler.removeCallbacks(loadDiscussion);
					}
					fetchChat();
				}

				@Override
				public boolean onError(FetchChatResponse fetchChatResponse, String message, int flag) {
					return false;
				}
			});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				if(intent.hasExtra(Constants.KEY_FINISH_ACTIVITY)){
					performBackPressed();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

}
