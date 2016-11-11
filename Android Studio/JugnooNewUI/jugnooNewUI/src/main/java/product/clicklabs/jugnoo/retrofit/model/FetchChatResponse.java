package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aneeshbansal on 11/11/16.
 */


public class FetchChatResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("chat_history")
	@Expose
	private List<ChatHistory> chatHistory = new ArrayList<ChatHistory>();
	@SerializedName("suggestions")
	@Expose
	private List<Object> suggestions = new ArrayList<Object>();
	@SerializedName("status")
	@Expose
	private Integer status;

	/**
	 *
	 * @return
	 * The flag
	 */
	public Integer getFlag() {
		return flag;
	}

	/**
	 *
	 * @param flag
	 * The flag
	 */
	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	/**
	 *
	 * @return
	 * The message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 *
	 * @param message
	 * The message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 *
	 * @return
	 * The chatHistory
	 */
	public List<ChatHistory> getChatHistory() {
		return chatHistory;
	}

	/**
	 *
	 * @param chatHistory
	 * The chat_history
	 */
	public void setChatHistory(List<ChatHistory> chatHistory) {
		this.chatHistory = chatHistory;
	}

	/**
	 *
	 * @return
	 * The suggestions
	 */
	public List<Object> getSuggestions() {
		return suggestions;
	}

	/**
	 *
	 * @param suggestions
	 * The suggestions
	 */
	public void setSuggestions(List<Object> suggestions) {
		this.suggestions = suggestions;
	}

	/**
	 *
	 * @return
	 * The status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 *
	 * @param status
	 * The status
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	public class ChatHistory {

		@SerializedName("chat_history_id")
		@Expose
		private Integer chatHistoryId;
		@SerializedName("message")
		@Expose
		private String message;
		@SerializedName("created_at")
		@Expose
		private String createdAt;
		@SerializedName("is_sender")
		@Expose
		private Integer isSender;

		/**
		 *
		 * @return
		 * The chatHistoryId
		 */
		public Integer getChatHistoryId() {
			return chatHistoryId;
		}

		/**
		 *
		 * @param chatHistoryId
		 * The chat_history_id
		 */
		public void setChatHistoryId(Integer chatHistoryId) {
			this.chatHistoryId = chatHistoryId;
		}

		/**
		 *
		 * @return
		 * The message
		 */
		public String getMessage() {
			return message;
		}

		/**
		 *
		 * @param message
		 * The message
		 */
		public void setMessage(String message) {
			this.message = message;
		}

		/**
		 *
		 * @return
		 * The createdAt
		 */
		public String getCreatedAt() {
			return createdAt;
		}

		/**
		 *
		 * @param createdAt
		 * The created_at
		 */
		public void setCreatedAt(String createdAt) {
			this.createdAt = createdAt;
		}

		/**
		 *
		 * @return
		 * The isSender
		 */
		public Integer getIsSender() {
			return isSender;
		}

		/**
		 *
		 * @param isSender
		 * The is_sender
		 */
		public void setIsSender(Integer isSender) {
			this.isSender = isSender;
		}

	}

}
