package dev.dect.wear.watchface.dsh008.v2.popup;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import dev.dect.wear.watchface.dsh008.v2.R;
import dev.dect.wear.watchface.dsh008.v2.adapter.PickerItemAdapter;
import dev.dect.wear.watchface.dsh008.v2.model.Version;
import dev.dect.wear.watchface.dsh008.v2.utils.Utils;

@SuppressLint("InflateParams")
public class PickerPopup extends Dialog {
    public interface OnPickerPopupListener {
        void onPickerPicked(int indexPicked);
    }

    private ConstraintLayout POPUP_VIEW,
                             POPUP_CONTAINER;

    public PickerPopup(Context ctx, ArrayList<Version> versions, Version active, OnPickerPopupListener l) {
        super(ctx, R.style.Theme_Translucent);

        final ArrayList<String> displayNames = new ArrayList<>();

        int activeIndex = 0;

        for(int i = 0; i < versions.size(); i++) {
            final Version version = versions.get(i);

            displayNames.add("v" + version.getVersionName());

            if(version.getVersionCode() == active.getVersionCode()) {
                activeIndex = i;
            }
        }

        build(ctx, R.string.version_popup_title, displayNames.toArray(new String[0]), activeIndex, l);
    }

    public void dismissWithAnimation() {
        Utils.Popup.callOutAnimation(this, POPUP_CONTAINER, POPUP_VIEW);
    }

    private void build(Context ctx, int title, String[] displayNames, int activeIndex, OnPickerPopupListener l) {
        final View view = ((LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_picker, null);

        POPUP_CONTAINER = view.findViewById(R.id.popupContainer);
        POPUP_VIEW = view.findViewById(R.id.popup);

        POPUP_CONTAINER.setOnClickListener((v) -> this.dismissWithAnimation());

        ((TextView) view.findViewById(R.id.popupTitle)).setText(title);

        final AppCompatButton btnMain = view.findViewById(R.id.popupBtnMain);

        btnMain.setOnClickListener((v) -> this.dismissWithAnimation());

        if(displayNames.length == 1) {
            btnMain.setText(R.string.popup_btn_ok);
            btnMain.setAllCaps(true);
        }

        final PickerItemAdapter pickerItemAdapter = new PickerItemAdapter(displayNames, activeIndex);

        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));

        recyclerView.setAdapter(pickerItemAdapter);

        recyclerView.scrollToPosition(activeIndex);

        recyclerView.setOnClickListener((v) -> {
            final int i = pickerItemAdapter.getSelected();

            if(i != activeIndex) {
                l.onPickerPicked(i);

                this.dismissWithAnimation();
            }
        });

        Utils.Popup.setMaxHeight(ctx, view.findViewById(R.id.popup));

        this.setContentView(view);

        Objects.requireNonNull(getWindow()).setStatusBarColor(ctx.getColor(R.color.popup_background));
    }
}
