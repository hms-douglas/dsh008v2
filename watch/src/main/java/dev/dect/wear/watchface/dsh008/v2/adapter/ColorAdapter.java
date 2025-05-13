package dev.dect.wear.watchface.dsh008.v2.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import dev.dect.dsh008.v2.CalendarColor;
import dev.dect.dsh008.v2.CalendarUtils;
import dev.dect.wear.watchface.dsh008.v2.R;

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
        private final ConstraintLayout EL_BUTTON;

        private final TextView EL_BUTTON_TEXT;

        public MyViewHolder(View view) {
            super(view);

            this.EL_BUTTON = view.findViewById(R.id.color);
            this.EL_BUTTON_TEXT = view.findViewById(R.id.text);
        }
    }

    @NonNull
    @Override
    public ColorAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_event_color, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull ColorAdapter.MyViewHolder holder, int position) {
        final Context ctx = holder.EL_BUTTON.getContext();

        int color;

        try {
            color = COLORS.getInt(holder.getAdapterPosition());
        } catch (Exception ignore) {
            color = ctx.getColor(R.color.main);
        }

        holder.EL_BUTTON_TEXT.setTextColor(CalendarColor.textColorFor(ctx, color));

        final Drawable colorDrawable =  CalendarUtils.getTintedDrawable(ctx, R.drawable.circle, color);

        holder.EL_BUTTON.setBackground(colorDrawable);

        holder.EL_BUTTON.setOnClickListener((v) -> Toast.makeText(ctx, ctx.getString(R.string.toast_info_color), Toast.LENGTH_SHORT).show());

        holder.EL_BUTTON.setOnLongClickListener((v) -> {
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

            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return COLORS.length();
    }
}
