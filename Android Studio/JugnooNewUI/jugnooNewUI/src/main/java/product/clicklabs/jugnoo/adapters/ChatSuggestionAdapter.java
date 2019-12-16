package product.clicklabs.jugnoo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.retrofit.model.FetchChatResponse;
import product.clicklabs.jugnoo.utils.Fonts;


/**
 * Created by aneesh on 10/4/15.
 */
public class ChatSuggestionAdapter extends RecyclerView.Adapter<ChatSuggestionAdapter.SuggestionViewHolder> {

	private ArrayList<FetchChatResponse.Suggestion> chatHistories;
	private Context context;
	private Callback callback;
	private int margin;

	public ChatSuggestionAdapter(Context context, ArrayList<FetchChatResponse.Suggestion> chatHistories, Callback callback) {
		this.chatHistories = chatHistories;
		this.context = context;
		this.callback = callback;
		margin = Utils.dpToPx(context, 10);
	}

	@Override
	public int getItemCount() {
		return chatHistories.size();
	}

	@Override
	public void onBindViewHolder(SuggestionViewHolder holder, int i) {
		final FetchChatResponse.Suggestion itr = chatHistories.get(i);

		holder.name.setTypeface(Fonts.mavenRegular(context));
		holder.name.setText(itr.getSuggestion());

		holder.name.setTag(i);
		holder.name.setOnClickListener(v -> {
			try {
				int pos = (int) v.getTag();
				callback.onSuggestionClick(pos, chatHistories.get(pos));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

	}

	@Override
	public SuggestionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_chat_suggestion, viewGroup, false);

		RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
		layoutParams.setMarginStart(margin);
		layoutParams.setMarginEnd(margin);
		itemView.setLayoutParams(layoutParams);

		return new SuggestionViewHolder(itemView);
	}


	public class SuggestionViewHolder extends RecyclerView.ViewHolder {
		protected TextView name;
		protected int id;
		public SuggestionViewHolder(View v) {
			super(v);
			name = v.findViewById(R.id.name);
			name.setTypeface(Fonts.mavenRegular(context));
		}
	}

	public interface Callback{
		void onSuggestionClick(int position, FetchChatResponse.Suggestion suggestion);
	}

}