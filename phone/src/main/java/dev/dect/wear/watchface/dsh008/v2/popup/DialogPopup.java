package dev.dect.wear.watchface.dsh008.v2.popup;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Objects;

import dev.dect.wear.watchface.dsh008.v2.R;
import dev.dect.wear.watchface.dsh008.v2.utils.Utils;

@SuppressLint("InflateParams")
public class DialogPopup extends Dialog {
    private final ConstraintLayout POPUP_VIEW,
                                   POPUP_CONTAINER;

    public static final int NO_TEXT = -1;

    public DialogPopup(Context ctx, int title, int text, int btnYesText, Runnable btnYes, int btnNoText, @Nullable Runnable btnNo, boolean dismissible, boolean dismissibleCallsNo, boolean isDangerousAction) {
        this(ctx, title, ctx.getString(text), btnYesText, btnYes, btnNoText, btnNo, dismissible, dismissibleCallsNo, isDangerousAction);
    }

    public DialogPopup(Context ctx, int title, String text, int btnYesText, Runnable btnYes, int btnNoText, @Nullable Runnable btnNo, boolean dismissible, boolean dismissibleCallsNo, boolean isDangerousAction) {
        super(ctx, R.style.Theme_Translucent);

        final View view = ((LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_dialog, null);

        if(title == NO_TEXT) {
            view.findViewById(R.id.popupTitle).setVisibility(View.GONE);
        } else {
            ((TextView) view.findViewById(R.id.popupTitle)).setText(title);
        }

        ((TextView) view.findViewById(R.id.popupText)).setText(text);

        final AppCompatButton buttonYes = view.findViewById(R.id.popupBtnYes);

        buttonYes.setText(btnYesText);

        if(isDangerousAction) {
            buttonYes.setTextColor(ctx.getColor(R.color.popup_btn_text_dangerous));
        }

        buttonYes.setOnClickListener((v) -> {
            this.dismissWithAnimation();

            if(btnYes != null) {
                btnYes.run();
            }
        });

        if(btnNoText == NO_TEXT) {
            view.findViewById(R.id.popupBtnNo).setVisibility(View.GONE);

            buttonYes.setAllCaps(true);
        } else {
            final AppCompatButton buttonNo = view.findViewById(R.id.popupBtnNo);

            buttonNo.setText(btnNoText);

            buttonNo.setOnClickListener((v) -> {
                this.dismissWithAnimation();

                if(btnNo != null) {
                    btnNo.run();
                }
            });
        }

        this.POPUP_CONTAINER = view.findViewById(R.id.popupContainer);
        this.POPUP_VIEW = view.findViewById(R.id.popup);

        if(dismissible) {
            POPUP_CONTAINER.setOnClickListener((v) -> {
                if(dismissibleCallsNo) {
                    if(btnNo != null) {
                        btnNo.run();
                    }
                } else {
                    this.dismissWithAnimation();
                }
            });
        }

        Utils.Popup.setMaxHeight(ctx, view.findViewById(R.id.popup));

        this.setContentView(view);

        Objects.requireNonNull(getWindow()).setStatusBarColor(ctx.getColor(R.color.popup_background_transparent));
    }

    public void dismissWithAnimation() {
        Utils.Popup.callOutAnimation(this, POPUP_CONTAINER, POPUP_VIEW);
    }
}
