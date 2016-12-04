package com.jugnoo.pay.utils;

import android.view.View;

public interface RecyclerViewClickListener
{

    public void recyclerViewListClicked(View v, int position);
    public void recyclerViewListClicked(View v, int position, int viewType);
}