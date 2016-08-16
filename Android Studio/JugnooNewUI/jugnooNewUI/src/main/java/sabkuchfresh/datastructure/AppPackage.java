package sabkuchfresh.datastructure;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by aneeshbansal on 16/12/15.
 */
public class AppPackage {

	public AppPackage(int appId, String packageName) {
		this.appId =appId;
		this.packageName = packageName;
		this.installed = 0;
	}

	@SerializedName("app_id")
	@Expose
	private int appId;
	@SerializedName("package_name")
	@Expose
	private String packageName;
	@SerializedName("status")
	@Expose
	private Integer installed;

	/**
	 *
	 * @return
	 * The appId
	 */
	public int getAppID() {
		return appId;
	}

	/**
	 *
	 * @param appId
	 * The package_name
	 */
	public void setAppID(int appId) {
		this.appId = appId;
	}
	/**
	 *
	 * @return
	 * The packageName
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 *
	 * @param packageName
	 * The package_name
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 *
	 * @return
	 * The installed
	 */
	public Integer getInstalled() {
		return installed;
	}

	/**
	 *
	 * @param installed
	 * The installed
	 */
	public void setInstalled(Integer installed) {
		this.installed = installed;
	}
}
