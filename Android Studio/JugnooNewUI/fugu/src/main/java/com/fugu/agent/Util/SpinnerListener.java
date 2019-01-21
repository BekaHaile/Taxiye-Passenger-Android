package com.fugu.agent.Util;

/**
 * Created by gurmail on 24/07/18.
 *
 * @author gurmail
 */

import com.fugu.agent.model.broadcastResponse.Tag;

import java.util.List;

public interface SpinnerListener {
    void onItemsSelected(List<Tag> items);
}
