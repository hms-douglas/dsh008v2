package dev.dect.wear.watchface.dsh008.v2.popup;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import dev.dect.wear.watchface.dsh008.v2.R;

@SuppressLint("InflateParams")
public class DialogPopup extends Dialog {
    public static final int NO_TEXT = -1;

    public DialogPopup(Context ctx, int title, int text, Runnable btnYes, @Nullable  Runnable btnNo) {
        super(ctx);

        final View layout = ((LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_dialog, null);

        layout.findViewById(R.id.btnYes).setOnClickListener(view2 -> {
            dismiss();

            btnYes.run();
        });

        layout.findViewById(R.id.btnNo).setOnClickListener(view2 -> {
            dismiss();

            if(btnNo != null) {
                btnNo.run();
            }
        });

        ((TextView) layout.findViewById(R.id.title)).setText(title);

        if(text != NO_TEXT) {
            ((TextView) layout.findViewById(R.id.text)).setText(text);
        }

        setContentView(layout);
    }
}
