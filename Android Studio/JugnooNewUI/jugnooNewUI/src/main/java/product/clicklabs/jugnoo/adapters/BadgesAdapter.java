package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.FeedBackInfo;

public class BadgesAdapter extends RecyclerView.Adapter<BadgesAdapter.viewholder> {
    ArrayList<FeedBackInfo.ImageBadges> imageBadges;
    ArrayList<Integer> clickImagesIds=new ArrayList<>();
    BadgesClickListener activity;
    private int clickCount=0;
//    float scalingFactor=0;
//    float imageSize;
//    int margin;
//    public static boolean plusClicked = false;

    public BadgesAdapter(BadgesClickListener activity, ArrayList<FeedBackInfo.ImageBadges> imageBadges) {
        this.imageBadges = imageBadges;
        this.activity = activity;

//        double temp=1.0/((double)imageBadges.size()/2);
//        scalingFactor=(float)temp;
//        imageSize=scalingFactor;
////        margin=dpToPx((int)(60*imageSize));


    }
//
//    public int dpToPx(int dp) {
//        float density = activity.getResources()
//                .getDisplayMetrics()
//                .density;
//        return Math.round((float) dp * density);
//    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_badges, parent, false);

        return new viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {


//        if (this.imageBadges.get(position) == null) {
//            Picasso.with(activity).load(R.drawable.ic_plus_theme_selector)
//                    .transform(new CircleTransform())
//                    .into(holder.imageView);
//        } else {
        String image = this.imageBadges.get(position).getImageAdress();
        String name = this.imageBadges.get(position).getName();
        Picasso.with((Activity) activity).load(image)
                .transform(new CircleTransform())
                .into(holder.imageView);
        holder.textView.setText(name);
//        }
    }

    @Override
    public int getItemCount() {
        return imageBadges.size();
    }

    public static boolean badgeCLicked = false;

    class viewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.profileImg)
        ImageView imageView;
        @BindView(R.id.tvBadgeName)
        TextView textView;
        @BindView(R.id.summary_badge)
        View border;
        @BindView(R.id.badgeClicked)
        ImageView badgeTick;

        public viewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(v.getTag()==null&&clickCount<5){
                        v.setTag("Clicked");
                        clickCount++;
                        badgeCLicked=true;
                        border.setBackground(((Activity)activity).getResources().getDrawable(R.drawable.circle_border_autos));
                        textView.setTextColor(((Activity)activity).getResources().getColor(R.color.theme_color));
                        clickImagesIds.add(imageBadges.get(getAdapterPosition()).getBadgeId());
                        badgeTick.setVisibility(View.VISIBLE);
                    }
                    else if(v.getTag()!=null&&v.getTag().equals("Clicked"))
                        {
                           v.setTag(null);
                           clickCount--;
                           int index=clickImagesIds.indexOf(imageBadges.get(getAdapterPosition()).getBadgeId());
                            if(index>=0){
                                clickImagesIds.remove(index);
                            }
                            badgeCLicked=false;
                            border.setBackground(((Activity)activity).getResources().getDrawable(R.drawable.background_transparent));
                            badgeTick.setVisibility(View.GONE);
                            textView.setTextColor(((Activity)activity).getResources().getColor(R.color.black));
                        }

                    activity.onClickBadge(imageBadges.get(getAdapterPosition()).getBadgeId(), getAdapterPosition());

                }
            });
        }
    }
    public String getClickedBadgesIds(){
        String badgeIds="";
        if (clickImagesIds != null && clickImagesIds.size() > 0) {
            for (int i = 0; i < clickImagesIds.size(); i++) {

                badgeIds = badgeIds + clickImagesIds.get(i) + ",";

            }
            if (!badgeIds.equalsIgnoreCase("")) {
                badgeIds = badgeIds.substring(0, badgeIds.length() - 1);
            }
        }
        return badgeIds;
    }

    public interface BadgesClickListener {
        void onClickBadge(int badgeId, int position);
    }

}
