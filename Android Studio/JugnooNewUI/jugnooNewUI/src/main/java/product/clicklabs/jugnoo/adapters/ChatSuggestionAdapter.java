package product.clicklabs.jugnoo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.retrofit.model.FetchChatResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


/**
 * Created by aneesh on 10/4/15.
 */
public class ChatSuggestionAdapter extends RecyclerView.Adapter<ChatSuggestionAdapter.infoTileViewHolder> {

	private ArrayList<FetchChatResponse.Suggestion> chatHistories;
	private Context context;
	private Callback callback;
	private FetchChatResponse fetchChatResponse;

	public ChatSuggestionAdapter(Context context, ArrayList<FetchChatResponse.Suggestion> chatHistories, Callback callback) {
		this.chatHistories = chatHistories;
		this.context = context;
		this.callback = callback;
	}

	@Override
	public int getItemCount() {
		return chatHistories.size();
	}

	@Override
	public void onBindViewHolder(infoTileViewHolder infoTileViewHolder, int i) {
		final FetchChatResponse.Suggestion itr = chatHistories.get(i);

		infoTileViewHolder.name.setTypeface(Fonts.mavenRegular(context));
		infoTileViewHolder.name.setText(itr.getSuggestion());

		infoTileViewHolder.relative.setTag(i);
		infoTileViewHolder.relative.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//				infoTileResponses.get((int) v.getTag()).completed = 1;

				try {
					int pos = (int) v.getTag();
					callback.onSuggestionClick(pos, chatHistories.get(pos));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	public infoTileViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_chat_suggestion, viewGroup, false);

		RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, 116);
		itemView.setLayoutParams(layoutParams);
		ASSL.DoMagic(itemView);

		return new infoTileViewHolder(itemView);
	}


	public class infoTileViewHolder extends RecyclerView.ViewHolder {
		protected LinearLayout relative;
		protected TextView name;
		protected int id;
		public infoTileViewHolder(View v) {
			super(v);
			relative = (LinearLayout)v.findViewById(R.id.relative);
			name = (TextView) v.findViewById(R.id.name);
			name.setTypeface(Fonts.mavenRegular(context));
		}
	}

	public interface Callback{
		void onSuggestionClick(int position, FetchChatResponse.Suggestion suggestion);
	}

}