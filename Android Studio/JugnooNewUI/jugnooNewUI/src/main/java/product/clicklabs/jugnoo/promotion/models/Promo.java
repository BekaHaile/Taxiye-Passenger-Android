package product.clicklabs.jugnoo.promotion.models;

import android.os.Parcel;
import android.os.Parcelable;

import product.clicklabs.jugnoo.datastructure.PromoCoupon;

/**
 * Created by shankar on 01/05/17.
 */

public class Promo implements Parcelable {

	private String name, clientId;
	private PromoCoupon promoCoupon;
	private int iconRes, lineColorRes, couponCardType;
	private boolean isScratched;

	public Promo(String name, String clientId, PromoCoupon promoCoupon, int iconRes, int lineColorRes, boolean isScratched, int couponCardType) {
		this.name = name;
		this.clientId = clientId;
		this.promoCoupon = promoCoupon;
		this.iconRes = iconRes;
		this.lineColorRes = lineColorRes;
		this.isScratched = isScratched;
		this.couponCardType = couponCardType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIconRes() {
		return iconRes;
	}

	public void setIconRes(int iconRes) {
		this.iconRes = iconRes;
	}

	public int getLineColorRes() {
		return lineColorRes;
	}

	public void setLineColorRes(int lineColorRes) {
		this.lineColorRes = lineColorRes;
	}

	public PromoCoupon getPromoCoupon() {
		return promoCoupon;
	}

	public void setPromoCoupon(PromoCoupon promoCoupon) {
		this.promoCoupon = promoCoupon;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public int getCouponCardType() {
		return couponCardType;
	}

	public void setCouponCardType(int couponCardType) {
		this.couponCardType = couponCardType;
	}

	public boolean isScratched() {
		return isScratched;
	}

	public void setScratched(boolean scratched) {
		isScratched = scratched;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.name);
		dest.writeString(this.clientId);
		dest.writeSerializable(this.promoCoupon);
		dest.writeInt(this.iconRes);
		dest.writeInt(this.lineColorRes);
		dest.writeInt(this.couponCardType);
		dest.writeByte(this.isScratched ? (byte) 1 : (byte) 0);
	}

	protected Promo(Parcel in) {
		this.name = in.readString();
		this.clientId = in.readString();
		this.promoCoupon = (PromoCoupon) in.readSerializable();
		this.iconRes = in.readInt();
		this.lineColorRes = in.readInt();
		this.couponCardType = in.readInt();
		this.isScratched = in.readByte() != 0;
	}

	public static final Parcelable.Creator<Promo> CREATOR = new Parcelable.Creator<Promo>() {
		@Override
		public Promo createFromParcel(Parcel source) {
			return new Promo(source);
		}

		@Override
		public Promo[] newArray(int size) {
			return new Promo[size];
		}
	};
}
