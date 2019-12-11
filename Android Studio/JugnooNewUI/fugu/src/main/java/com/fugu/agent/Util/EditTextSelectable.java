package com.fugu.agent.Util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gurmail on 19/06/18.
 *
 * @author gurmail
 */

public class EditTextSelectable extends androidx.appcompat.widget.AppCompatEditText  {

    private KeyImeChange keyImeChangeListener;

    public void setKeyImeChangeListener(KeyImeChange listener){
        keyImeChangeListener = listener;
    }

    public interface onSelectionChangedListener {
        public void onSelectionChanged(int selStart, int selEnd);
    }


    private List<onSelectionChangedListener> listeners;

    public EditTextSelectable(Context context) {
        super(context);
        listeners = new ArrayList<onSelectionChangedListener>();
    }

    public EditTextSelectable(Context context, AttributeSet attrs) {
        super(context, attrs);
        listeners = new ArrayList<onSelectionChangedListener>();
    }

    public EditTextSelectable(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        listeners = new ArrayList<onSelectionChangedListener>();
    }

    public interface KeyImeChange{
        public void onKeyIme(int keyCode, KeyEvent event);
    }


    public void addOnSelectionChangedListener(onSelectionChangedListener o) {
        listeners.add(o);
    }

    protected void onSelectionChanged(int selStart, int selEnd) {
        if(listeners!=null)
            for (onSelectionChangedListener l : listeners)
                l.onSelectionChanged(selStart, selEnd);

    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if(keyImeChangeListener != null){
            keyImeChangeListener.onKeyIme(keyCode, event);
        }
        return false;
    }
}
