package com.sabkuchfresh.retrofit.model;

import java.util.Comparator;

/**
 * Created by Gurmail S. Kang on 5/5/16.
 */
public class SubItemCompareAtoZ implements Comparator<SubItem> {
    public int compare(SubItem subItem1, SubItem subItem2) {
        return subItem1.getSubItemName().compareToIgnoreCase(subItem2.getSubItemName());
    }
}