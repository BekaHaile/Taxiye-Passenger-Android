package com.sabkuchfresh.retrofit.model;

import java.util.Comparator;

/**
 * Created by Gurmail S. Kang on 5/6/16.
 */
public class SubItemComparePriority implements Comparator<SubItem> {
    public int compare(SubItem subItem1, SubItem subItem2) {
        return subItem1.getPriorityId() - subItem2.getPriorityId();
    }
}
