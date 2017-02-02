package com.sabkuchfresh.retrofit.model;

import java.util.Comparator;

/**
 * Created by Gurmail S. Kang on 5/5/16.
 */
public class SubItemComparePriceLowToHigh implements Comparator<SubItem> {
    public int compare(SubItem subItem1, SubItem subItem2) {
        return Double.compare(subItem1.getPrice(), subItem2.getPrice());
    }
}