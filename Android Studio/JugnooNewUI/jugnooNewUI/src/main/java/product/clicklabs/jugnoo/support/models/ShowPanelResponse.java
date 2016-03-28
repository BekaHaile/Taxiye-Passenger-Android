package product.clicklabs.jugnoo.support.models;

/**
 * Created by shankar on 1/21/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ShowPanelResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("menu")
	@Expose
	private List<Item> menu = new ArrayList<Item>();
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
	 * The menu
	 */
	public List<Item> getMenu() {
		return menu;
	}

	/**
	 *
	 * @param menu
	 * The menu
	 */
	public void setMenu(List<Item> menu) {
		this.menu = menu;
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


	public class Item {

		@SerializedName("support_id")
		@Expose
		private Integer supportId;
		@SerializedName("text")
		@Expose
		private String text;
		@SerializedName("level")
		@Expose
		private Integer level;
		@SerializedName("parent_id")
		@Expose
		private Integer parentId;
		@SerializedName("view_type")
		@Expose
		private Integer viewType;
		@SerializedName("action_type")
		@Expose
		private Integer actionType;
		@SerializedName("updated_at")
		@Expose
		private String updatedAt;
		@SerializedName("created_at")
		@Expose
		private String createdAt;
		@SerializedName("items")
		@Expose
		private List<Item> items = new ArrayList<Item>();

		/**
		 *
		 * @return
		 * The supportId
		 */
		public Integer getSupportId() {
			return supportId;
		}

		/**
		 *
		 * @param supportId
		 * The support_id
		 */
		public void setSupportId(Integer supportId) {
			this.supportId = supportId;
		}

		/**
		 *
		 * @return
		 * The text
		 */
		public String getText() {
			return text;
		}

		/**
		 *
		 * @param text
		 * The text
		 */
		public void setText(String text) {
			this.text = text;
		}

		/**
		 *
		 * @return
		 * The level
		 */
		public Integer getLevel() {
			return level;
		}

		/**
		 *
		 * @param level
		 * The level
		 */
		public void setLevel(Integer level) {
			this.level = level;
		}

		/**
		 *
		 * @return
		 * The parentId
		 */
		public Integer getParentId() {
			return parentId;
		}

		/**
		 *
		 * @param parentId
		 * The parent_id
		 */
		public void setParentId(Integer parentId) {
			this.parentId = parentId;
		}

		/**
		 *
		 * @return
		 * The viewType
		 */
		public Integer getViewType() {
			return viewType;
		}

		/**
		 *
		 * @param viewType
		 * The view_type
		 */
		public void setViewType(Integer viewType) {
			this.viewType = viewType;
		}

		/**
		 *
		 * @return
		 * The actionType
		 */
		public Integer getActionType() {
			return actionType;
		}

		/**
		 *
		 * @param actionType
		 * The action_type
		 */
		public void setActionType(Integer actionType) {
			this.actionType = actionType;
		}

		/**
		 *
		 * @return
		 * The updatedAt
		 */
		public String getUpdatedAt() {
			return updatedAt;
		}

		/**
		 *
		 * @param updatedAt
		 * The updated_at
		 */
		public void setUpdatedAt(String updatedAt) {
			this.updatedAt = updatedAt;
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
		 * The items
		 */
		public List<Item> getItems() {
			return items;
		}

		/**
		 *
		 * @param items
		 * The items
		 */
		public void setItems(List<Item> items) {
			this.items = items;
		}

	}

}

