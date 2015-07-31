package product.clicklabs.jugnoo.wallet;

/**
 * Created by socomo30 on 7/21/15.
 */
public class StoreCard {

    public int isExpired;
    public String name_on_card, card_name, expiry_year, expiry_month, card_type, card_token, card_mode, card_no, card_brand, card_bin, isDomestic;

    public void StoreCard(int isExpired, String name_on_card, String card_name, String expiry_year, String expiry_month, String card_type,
                          String card_token, String card_mode, String card_no, String card_brand, String card_bin, String isDomestic) {

        this.isExpired = isExpired;

        this.name_on_card = name_on_card;
        this.card_name = card_name;
        this.expiry_year = expiry_year;
        this.expiry_month = expiry_month;
        this.card_type = card_type;
        this.card_token = card_token;
        this.card_mode = card_mode;
        this.card_no = card_no;
        this.card_brand = card_brand;
        this.card_bin = card_bin;
        this.isDomestic = isDomestic;

    }

}

