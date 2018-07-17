package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.feed.models.FeedCommonResponse;

import java.util.List;

public class FetchActiveLocaleResponse extends FeedCommonResponse {

	@SerializedName("locale_set")
	@Expose
	private List<Locale> localeSet;

	public List<Locale> getLocaleSet() {
		return localeSet;
	}

	public void setLocaleSet(List<Locale> localeSet) {
		this.localeSet = localeSet;
	}

	public static class Locale {
		@SerializedName("name")
		@Expose
		private String name;
		@SerializedName("locale")
		@Expose
		private String locale;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getLocale() {
			return locale;
		}

		public void setLocale(String locale) {
			this.locale = locale;
		}
	}

}
