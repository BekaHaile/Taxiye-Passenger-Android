package com.sabkuchfresh.retrofit.model.menus;

import java.util.Comparator;

public class ItemComparePriceLowToHigh implements Comparator<Item> {
    public int compare(Item item1, Item item2) {
        return Double.compare(item1.getPrice(), item2.getPrice());
    }
}