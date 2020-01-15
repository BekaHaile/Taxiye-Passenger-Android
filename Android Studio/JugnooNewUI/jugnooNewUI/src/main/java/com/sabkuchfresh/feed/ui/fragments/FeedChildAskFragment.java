package com.sabkuchfresh.feed.ui.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;

import static com.sabkuchfresh.feed.ui.fragments.FeedAddPostFragment.FEED_DETAIL;

/**
 * Created by Parminder Singh on 3/20/17.
 */

public class FeedChildAskFragment extends ImageSelectFragment {


    private EditText etContent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_child_feed_review, container, false);
        activity = ((FreshActivity) getActivity());
        etContent = (EditText) rootView.findViewById(R.id.etContent);
        if(Data.getFeedData()!=null && !TextUtils.isEmpty(Data.getFeedData().getFeedAddPostHInt()))
            etContent.setHint(Data.getFeedData().getFeedAddPostHInt());
        else
            etContent.setHint(R.string.looking_for_something);
        etContent.addTextChangedListener(editTextWacherContent);
        displayImagesRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_view_photos);
        scrollView = (ScrollView) rootView.findViewById(R.id.scroll_view);
        setSubmitActivated(false);

        if(feedDetail!=null){
            etContent.setText(feedDetail.getContent());
            etContent.setSelection(etContent.getText().length());
            setUpImagesAdapter();

        }

        return rootView;


    }


    public static FeedChildAskFragment newInstance(FeedDetail feedDetail) {
        FeedChildAskFragment feedChildAskFragment = new FeedChildAskFragment();
        if(feedDetail!=null){
            Bundle bundle =new Bundle();
            bundle.putSerializable(FEED_DETAIL,feedDetail);
            feedChildAskFragment.setArguments(bundle);
        }
        return feedChildAskFragment;


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if(getArguments()!=null && getArguments().containsKey(FEED_DETAIL)){
            feedDetail= (FeedDetail) getArguments().getSerializable(FEED_DETAIL);
        }
        super.onCreate(savedInstanceState);
    }



    @Override
    public boolean canSubmit() {
        if (TextUtils.isEmpty(etContent.getText().toString().trim()) && (imageSelected==null || imageSelected.size()==0)) {
            Toast.makeText(activity, R.string.please_enter_something, Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    @Override
    public String getText() {
        return etContent.getText().toString().trim();
    }

    @Override
    public Integer getRestaurantId() {
        return FeedAddPostFragment.NOT_APPLICABLE;
    }

    @Override
    protected Integer getScore() {
        return FeedAddPostFragment.NOT_APPLICABLE;
    }

    @Override
    public boolean canUploadImages() {
        return true;

    }
    @Override
    public boolean submitEnabledState() {
        return etContent.getText().toString().trim().length()>0;
    }

    @Override
    public boolean isAnonymousPostingEnabled() {
        return true;
    }

    @Override
    public EditText getFocusEditText() {
        return etContent;
    }
}
