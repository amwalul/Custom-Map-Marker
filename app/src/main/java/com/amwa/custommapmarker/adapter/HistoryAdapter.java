package com.amwa.custommapmarker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amwa.custommapmarker.R;
import com.amwa.custommapmarker.data.model.History;
import com.amwa.custommapmarker.util.Util;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<History> histories = new ArrayList<>();
    private Interaction interaction;

    public HistoryAdapter(Context context) {
        this.context = context;
    }

    public HistoryAdapter(Context context, Interaction interaction) {
        this.context = context;
        this.interaction = interaction;
    }

    public void setHistories(List<History> histories) {
        this.histories = histories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(itemRow);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, final int position) {
        final History history = histories.get(position);

        holder.tvStartLoc.setText(history.getStartLocaction());
        holder.tvEndLoc.setText(history.getEndLocation());
        holder.tvTime.setText(history.getTime());
        holder.tvScore.setText(String.valueOf(history.getScore()));
        holder.tvDistance.setText(
                context.getResources().getString(
                        R.string.distance, Util.meterToKilometer(history.getDistance(), true)
                )
        );
        holder.tvDuration.setText(
                context.getResources().getString(
                        R.string.duration, Util.secondToMinute(history.getDuration(), true)));

        holder.itemView.setOnClickListener(v -> interaction.onHistoryItemSelected(position, history));

        if (position == histories.size() - 1) {
            holder.separator.setVisibility(View.GONE);
        } else {
            holder.separator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvStartLoc, tvEndLoc, tvTime, tvDistance, tvDuration, tvScore;
        View separator;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStartLoc = itemView.findViewById(R.id.tvStartLoc);
            tvEndLoc = itemView.findViewById(R.id.tvEndLoc);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvScore = itemView.findViewById(R.id.tvScore);
            separator = itemView.findViewById(R.id.separator);
        }
    }

    public interface Interaction {
        void onHistoryItemSelected(int position, History history);
    }
}
