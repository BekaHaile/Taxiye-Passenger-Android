package product.clicklabs.jugnoo.datastructure;

import android.net.Uri;

import java.util.ArrayList;

public class FeedBackInfo{
    private int rating;
    private String desc;
    private ArrayList<ImageBadges> imageBadges=new ArrayList<>();
    private ArrayList<FeedbackReason> textBadges=new ArrayList<>();

    public ArrayList<FeedbackReason> getTextBadges() {
        return textBadges;
    }

    public String getDesc() {
        return desc;
    }

    public ArrayList<ImageBadges> getImageBadges() {
        return imageBadges;
    }

    public FeedBackInfo(int rating, String desc, ArrayList<ImageBadges> imageBadges, ArrayList<FeedbackReason> textBadges){
        this.desc=desc;
        this.imageBadges=imageBadges;
        this.textBadges=textBadges;
        this.rating=rating;
    }

    public static class ImageBadges{
        public ImageBadges(String name, int badgeId, String imageAddress){
            this.badgeId=badgeId;
            this.imageAdress=imageAddress;
            this.name=name;
        }

        public int getBadgeId() {
            return badgeId;
        }

        public String getImageAdress() {
            return imageAdress;
        }

        public String getName() {
            return name;
        }

        private String name;
        private int badgeId;
        private String imageAdress;
    }




}
