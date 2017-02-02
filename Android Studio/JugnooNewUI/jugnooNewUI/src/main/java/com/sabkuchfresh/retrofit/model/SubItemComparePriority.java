package com.sabkuchfresh.retrofit.model;

import java.util.Comparator;

public class SubItemComparePriority implements Comparator<SubItem> {
    @Override
    public int compare(SubItem p1, SubItem p2) {
        return p1.getPriorityId()- p2.getPriorityId();
    }
}