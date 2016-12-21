package product.clicklabs.jugnoo.support.models;

/**
 * Created by shankar on 1/21/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;

public class ShowPanelResponse {

	@SerializedName("support_data")
	@Expose
	private SupportData supportData;


	@SerializedName("fresh_meals_data")
	@Expose
	private HistoryResponse.Datum datum;
	@SerializedName("menus_data")
	@Expose
	private HistoryResponse.Datum menusDatum;


	public SupportData getSupportData() {
		return supportData;
	}

	public void setSupportData(SupportData supportData) {
		this.supportData = supportData;
	}

	public HistoryResponse.Datum getDatum() {
		return datum;
	}

	public void setDatum(HistoryResponse.Datum datum) {
		this.datum = datum;
	}

	public HistoryResponse.Datum getMenusDatum() {
		return menusDatum;
	}

	public void setMenusDatum(HistoryResponse.Datum menusDatum) {
		this.menusDatum = menusDatum;
	}


	public class SupportData {
		@SerializedName("1")
		@Expose
		private List<Item> menu1 = new ArrayList<Item>();
		@SerializedName("2")
		@Expose
		private List<Item> menu2 = new ArrayList<Item>();
		@SerializedName("3")
		@Expose
		private List<Item> menu3 = new ArrayList<Item>();
		@SerializedName("4")
		@Expose
		private List<Item> menu4 = new ArrayList<Item>();
		@SerializedName("5")
		@Expose
		private List<Item> menu5 = new ArrayList<Item>();
		@SerializedName("6")
		@Expose
		private List<Item> menu6 = new ArrayList<Item>();
		@SerializedName("7")
		@Expose
		private List<Item> menu7 = new ArrayList<Item>();
		@SerializedName("8")
		@Expose
		private List<Item> menu8 = new ArrayList<Item>();
		@SerializedName("9")
		@Expose
		private List<Item> menu9 = new ArrayList<Item>();
		@SerializedName("10")
		@Expose
		private List<Item> menu10 = new ArrayList<Item>();
		@SerializedName("11")
		@Expose
		private List<Item> menu11 = new ArrayList<Item>();
		@SerializedName("12")
		@Expose
		private List<Item> menu12 = new ArrayList<Item>();
		@SerializedName("13")
		@Expose
		private List<Item> menu13 = new ArrayList<Item>();
		@SerializedName("14")
		@Expose
		private List<Item> menu14 = new ArrayList<Item>();
		@SerializedName("15")
		@Expose
		private List<Item> menu15 = new ArrayList<Item>();
		@SerializedName("16")
		@Expose
		private List<Item> menu16 = new ArrayList<Item>();
		@SerializedName("17")
		@Expose
		private List<Item> menu17 = new ArrayList<Item>();
		@SerializedName("18")
		@Expose
		private List<Item> menu18 = new ArrayList<Item>();
		@SerializedName("19")
		@Expose
		private List<Item> menu19 = new ArrayList<Item>();
		@SerializedName("20")
		@Expose
		private List<Item> menu20 = new ArrayList<Item>();
		@SerializedName("21")
		@Expose
		private List<Item> menu21 = new ArrayList<Item>();
		@SerializedName("22")
		@Expose
		private List<Item> menu22 = new ArrayList<Item>();
		@SerializedName("23")
		@Expose
		private List<Item> menu23 = new ArrayList<Item>();
		@SerializedName("24")
		@Expose
		private List<Item> menu24 = new ArrayList<Item>();
		@SerializedName("25")
		@Expose
		private List<Item> menu25 = new ArrayList<Item>();
		@SerializedName("26")
		@Expose
		private List<Item> menu26 = new ArrayList<Item>();
		@SerializedName("27")
		@Expose
		private List<Item> menu27 = new ArrayList<Item>();
		@SerializedName("28")
		@Expose
		private List<Item> menu28 = new ArrayList<Item>();
		@SerializedName("29")
		@Expose
		private List<Item> menu29 = new ArrayList<Item>();
		@SerializedName("30")
		@Expose
		private List<Item> menu30 = new ArrayList<Item>();

		public List<Item> getMenu1() {
			return menu1;
		}

		public void setMenu1(List<Item> menu1) {
			this.menu1 = menu1;
		}

		public List<Item> getMenu2() {
			return menu2;
		}

		public void setMenu2(List<Item> menu2) {
			this.menu2 = menu2;
		}

		public List<Item> getMenu3() {
			return menu3;
		}

		public void setMenu3(List<Item> menu3) {
			this.menu3 = menu3;
		}

		public List<Item> getMenu4() {
			return menu4;
		}

		public void setMenu4(List<Item> menu4) {
			this.menu4 = menu4;
		}

		public List<Item> getMenu5() {
			return menu5;
		}

		public void setMenu5(List<Item> menu5) {
			this.menu5 = menu5;
		}

		public List<Item> getMenu6() {
			return menu6;
		}

		public void setMenu6(List<Item> menu6) {
			this.menu6 = menu6;
		}

		public List<Item> getMenu7() {
			return menu7;
		}

		public void setMenu7(List<Item> menu7) {
			this.menu7 = menu7;
		}

		public List<Item> getMenu8() {
			return menu8;
		}

		public void setMenu8(List<Item> menu8) {
			this.menu8 = menu8;
		}

		public List<Item> getMenu9() {
			return menu9;
		}

		public void setMenu9(List<Item> menu9) {
			this.menu9 = menu9;
		}

		public List<Item> getMenu10() {
			return menu10;
		}

		public void setMenu10(List<Item> menu10) {
			this.menu10 = menu10;
		}

		public List<Item> getMenu11() {
			return menu11;
		}

		public void setMenu11(List<Item> menu11) {
			this.menu11 = menu11;
		}

		public List<Item> getMenu12() {
			return menu12;
		}

		public void setMenu12(List<Item> menu12) {
			this.menu12 = menu12;
		}

		public List<Item> getMenu13() {
			return menu13;
		}

		public void setMenu13(List<Item> menu13) {
			this.menu13 = menu13;
		}

		public List<Item> getMenu14() {
			return menu14;
		}

		public void setMenu14(List<Item> menu14) {
			this.menu14 = menu14;
		}

		public List<Item> getMenu15() {
			return menu15;
		}

		public void setMenu15(List<Item> menu15) {
			this.menu15 = menu15;
		}

		public List<Item> getMenu16() {
			return menu16;
		}

		public void setMenu16(List<Item> menu16) {
			this.menu16 = menu16;
		}

		public List<Item> getMenu17() {
			return menu17;
		}

		public void setMenu17(List<Item> menu17) {
			this.menu17 = menu17;
		}

		public List<Item> getMenu18() {
			return menu18;
		}

		public void setMenu18(List<Item> menu18) {
			this.menu18 = menu18;
		}

		public List<Item> getMenu19() {
			return menu19;
		}

		public void setMenu19(List<Item> menu19) {
			this.menu19 = menu19;
		}

		public List<Item> getMenu20() {
			return menu20;
		}

		public void setMenu20(List<Item> menu20) {
			this.menu20 = menu20;
		}

		public List<Item> getMenu21() {
			return menu21;
		}

		public void setMenu21(List<Item> menu21) {
			this.menu21 = menu21;
		}

		public List<Item> getMenu22() {
			return menu22;
		}

		public void setMenu22(List<Item> menu22) {
			this.menu22 = menu22;
		}

		public List<Item> getMenu23() {
			return menu23;
		}

		public void setMenu23(List<Item> menu23) {
			this.menu23 = menu23;
		}

		public List<Item> getMenu24() {
			return menu24;
		}

		public void setMenu24(List<Item> menu24) {
			this.menu24 = menu24;
		}

		public List<Item> getMenu25() {
			return menu25;
		}

		public void setMenu25(List<Item> menu25) {
			this.menu25 = menu25;
		}

		public List<Item> getMenu26() {
			return menu26;
		}

		public void setMenu26(List<Item> menu26) {
			this.menu26 = menu26;
		}

		public List<Item> getMenu27() {
			return menu27;
		}

		public void setMenu27(List<Item> menu27) {
			this.menu27 = menu27;
		}

		public List<Item> getMenu28() {
			return menu28;
		}

		public void setMenu28(List<Item> menu28) {
			this.menu28 = menu28;
		}

		public List<Item> getMenu29() {
			return menu29;
		}

		public void setMenu29(List<Item> menu29) {
			this.menu29 = menu29;
		}

		public List<Item> getMenu30() {
			return menu30;
		}

		public void setMenu30(List<Item> menu30) {
			this.menu30 = menu30;
		}
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

