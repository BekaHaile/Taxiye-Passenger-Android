package com.sabkuchfresh.retrofit.model.menus;

import java.util.Comparator;

public class ItemComparePriceHighToLow implements Comparator<Item> {
    public int compare(Item item1, Item item2) {
        return Double.compare(item2.getPrice(), item1.getPrice());
    }
}