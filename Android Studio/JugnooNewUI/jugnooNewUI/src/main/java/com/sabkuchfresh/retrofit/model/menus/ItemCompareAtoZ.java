package com.sabkuchfresh.retrofit.model.menus;

import java.util.Comparator;

public class ItemCompareAtoZ implements Comparator<Item> {
    public int compare(Item item1, Item item2) {
        return item1.getItemName().compareToIgnoreCase(item2.getItemName());
    }
}