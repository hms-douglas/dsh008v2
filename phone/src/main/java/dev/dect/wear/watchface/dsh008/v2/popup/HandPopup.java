package dev.dect.wear.watchface.dsh008.v2.popup;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Objects;

import dev.dect.wear.watchface.dsh008.v2.R;
import dev.dect.wear.watchface.dsh008.v2.utils.Utils;

@SuppressLint("InflateParams")
public class HandPopup extends Dialog {
    public interface OnHandPopupListener {
        default void onHandPicked(int id) {}
    }

    private final ConstraintLayout POPUP_VIEW,
                                   POPUP_CONTAINER;

    private final OnHandPopupListener LISTENER;

    public HandPopup(Context ctx, OnHandPopupListener listener) {
        super(ctx, R.style.Theme_Translucent);

        this.LISTENER = listener;

        final View view = ((LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_static_hand, null);

        view.findViewById(R.id.style0).setOnClickListener((v) -> handPicked(0));
        view.findViewById(R.id.style1).setOnClickListener((v) -> handPicked(1));
        view.findViewById(R.id.style2).setOnClickListener((v) -> handPicked(2));
        view.findViewById(R.id.style3).setOnClickListener((v) -> handPicked(3));
        view.findViewById(R.id.style4).setOnClickListener((v) -> handPicked(4));

        view.findViewById(R.id.popupBtnNo).setOnClickListener((v) -> dismissWithAnimation());

        this.POPUP_CONTAINER = view.findViewById(R.id.popupContainer);
        this.POPUP_VIEW = view.findViewById(R.id.popup);

        POPUP_CONTAINER.setOnClickListener((v) -> this.dismissWithAnimation());

        Utils.Popup.setMaxHeight(ctx, view.findViewById(R.id.popup));

        this.setContentView(view);

        Objects.requireNonNull(getWindow()).setStatusBarColor(ctx.getColor(R.color.popup_background_transparent));
    }

    private void handPicked(int id) {
        LISTENER.onHandPicked(id);

        dismissWithAnimation();
    }

    public void dismissWithAnimation() {
        Utils.Popup.callOutAnimation(this, POPUP_CONTAINER, POPUP_VIEW);
    }
}
