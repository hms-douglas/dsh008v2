package dev.dect.wear.watchface.dsh008.v2.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import dev.dect.dsh008.v2.CalendarColor;
import dev.dect.dsh008.v2.CalendarUtils;
import dev.dect.wear.watchface.dsh008.v2.R;
import dev.dect.wear.watchface.dsh008.v2.utils.Utils;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.MyViewHolder> {
    public interface OnColorAdapterListener {
         void onColorRemoved(JSONArray colors, int index, int color);
    }

    private final JSONArray COLORS;

    private OnColorAdapterListener LISTENER;

    public ColorAdapter(JSONArray colors) {
        this.COLORS = colors;
    }

    public void setListener(OnColorAdapterListener listener) {
        this.LISTENER = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout EL_COLOR;

        private final TextView EL_TEXT;

        private final ImageButton EL_BUTTON_DELETE;

        public MyViewHolder(View view) {
            super(view);

            this.EL_COLOR = view.findViewById(R.id.color);
            this.EL_TEXT = view.findViewById(R.id.text);
            this.EL_BUTTON_DELETE = view.findViewById(R.id.btnDelete);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_event_color, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Context ctx = holder.EL_COLOR.getContext();

        int color;

        try {
            color = COLORS.getInt(holder.getAdapterPosition());
        } catch (Exception ignore) {
            color = ctx.getColor(R.color.main);
        }

        holder.EL_TEXT.setTextColor(CalendarColor.textColorFor(ctx, color));
        holder.EL_TEXT.setText(Utils.Converter.intColorToHex(color).substring(0, 7));

        final Drawable colorDrawable =  CalendarUtils.getTintedDrawable(ctx, R.drawable.circle, color);

        holder.EL_COLOR.setBackground(colorDrawable);

        holder.EL_BUTTON_DELETE.setOnClickListener((v) -> {
            v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);

            final int indexRemoved = holder.getAdapterPosition();

            int colorRemoved;

            try {
                colorRemoved = COLORS.getInt(indexRemoved);
            } catch (Exception ignore) {
                colorRemoved = -1;
            }

            COLORS.remove(indexRemoved);

            notifyItemRemoved(indexRemoved);

            if(LISTENER != null) {
                LISTENER.onColorRemoved(COLORS, indexRemoved, colorRemoved);
            }
        });
    }

    @Override
    public int getItemCount() {
        return COLORS.length();
    }
}
