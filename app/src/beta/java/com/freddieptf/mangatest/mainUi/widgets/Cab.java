package com.freddieptf.mangatest.mainUi.widgets;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.utils.MyColorUtils;

/**
 * Created by fred on 9/26/15.
 */
public class Cab extends LinearLayout implements View.OnClickListener {

    ImageView cabCloseIcon;
    TextView cabTitleText;
    TextView cabExtraText;
    OnActionClose onActionClose;
    final String TITLE_TEXT = "title";
    final String EXTRA_TEXT = "extras";
    final String IS_VISIBLE = "is_visible";

    public Cab(Context context) {
        this(context, null);
    }

    public Cab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Cab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.toolbar_cab, this, true);
        setBackgroundColor(new MyColorUtils(context).getPrimaryColor());

        cabCloseIcon = (ImageView) findViewById(R.id.cab_close_icon);
        cabCloseIcon.setOnClickListener(this);
        cabTitleText = (TextView) findViewById(R.id.cab_title);
        cabExtraText = (TextView) findViewById(R.id.cab_details);

    }

    @Override
    public void onClick(View view) {
        if(onActionClose != null) onActionClose.onClose();
    }

    public void setOnCloseListener(OnActionClose onActionClose){
        this.onActionClose = onActionClose;
    }

    public void setTitle(String s){
        cabTitleText.setText(s);
    }

    public void setExtraText(String s){
        cabExtraText.setText(s);
    }

    private void show(){
        setVisibility(VISIBLE);
    }

    private void hide(){
        setVisibility(GONE);
    }

    public void startCabMode(boolean bool){
        if(bool) show();
        else hide();
    }

    public boolean isVisible(){
        if(getVisibility() == VISIBLE) return true;
        return false;
    }

    public void saveState(Bundle bundle){
        bundle.putString(TITLE_TEXT, cabTitleText.getText().toString());
        bundle.putBoolean(IS_VISIBLE, isVisible());
        bundle.putString(EXTRA_TEXT, cabExtraText.getText().toString());
    }

    public void restoreState(Bundle bundle){
        if(bundle.containsKey(TITLE_TEXT) || bundle.containsKey(IS_VISIBLE) || bundle.containsKey(EXTRA_TEXT)) {
            cabTitleText.setText(bundle.getString(TITLE_TEXT));
            startCabMode(bundle.getBoolean(IS_VISIBLE));
            cabExtraText.setText(bundle.getString(EXTRA_TEXT));
        }
    }

    public interface OnActionClose{
        void onClose();
    }

}
