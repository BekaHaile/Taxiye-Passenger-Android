/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package product.clicklabs.jugnoo.sticky;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;


public class ScreenSlidePageFragment extends android.support.v4.app.Fragment {
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber, numOfPages;

    private RelativeLayout first, mainLay, third;
    private RelativeLayout second;


    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static ScreenSlidePageFragment create(int pageNumber, int numOfPages) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment(numOfPages);
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenSlidePageFragment(int numOfPages) {
        this.numOfPages = numOfPages;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_points_tutorial1, container, false);
        mainLay = (RelativeLayout)rootView.findViewById(R.id.mainLayout);
		mainLay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
		ASSL.DoMagic(mainLay);

        first = (RelativeLayout) rootView.findViewById(R.id.tutorial_one);
        second = (RelativeLayout) rootView.findViewById(R.id.tutorial_two);
        third = (RelativeLayout) rootView.findViewById(R.id.tutorial_three);

        third.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });

        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numOfPages == 1){
                    getActivity().finish();
                }
            }
        });

        first.setVisibility(View.GONE);
        second.setVisibility(View.GONE);
        third.setVisibility(View.GONE);

        if (mPageNumber == 0) {
            first.setVisibility(View.VISIBLE);
            second.setVisibility(View.GONE);
            third.setVisibility(View.GONE);
        } else if (mPageNumber == 1) {
            second.setVisibility(View.VISIBLE);
            first.setVisibility(View.GONE);
            third.setVisibility(View.GONE);
        } else if(mPageNumber == 2){
            third.setVisibility(View.VISIBLE);
            first.setVisibility(View.GONE);
            second.setVisibility(View.GONE);
        }

        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}
