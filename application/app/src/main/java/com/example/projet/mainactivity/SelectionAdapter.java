package com.example.projet.mainactivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projet.R;
import com.example.projet.models.Collectivite;
import com.example.projet.quizzactivity.QuizzActivity;
import com.example.projet.room.Score;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionViewHolder> {

    private List<? extends Collectivite> localList;
    private final Map<String, Score> scores;
    private ViewType localViewType;
    private final Context context;

    public SelectionAdapter(Context context) {
        this.context = context;
        this.scores = new HashMap<>();
    }

    public void setViewType(ViewType viewType) {
        this.localViewType = viewType;
        this.notifyDataSetChanged();
        this.notifyItemRangeChanged(0,getItemCount());
    }

    public void setLocalList(List<? extends Collectivite> localList) {
        this.localList = localList;
    }

    public void updateScore(List<Score> scores) {
        for (Score score : scores) {
            this.scores.put(score.getId(), score);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SelectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (this.localViewType == ViewType.GRID) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_viewholder, parent, false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_viewholder, parent, false);
        }
        return new SelectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectionViewHolder holder, int position) {
        Collectivite element = this.localList.get(position);

        holder.getTitleTextView().setText(element.getNom());
        Glide.with(context)
                .load(element.getLogoLink())
                .into(holder.getImageView());

        holder.getItemView().setOnClickListener(view -> {
            Intent quizzIntent = new Intent(view.getContext(), QuizzActivity.class);
            quizzIntent.putExtra("collectivite", element);
            Context activityContext = view.getContext();
            activityContext.startActivity(quizzIntent);
        });

        if (localViewType == ViewType.LIST) {
            String text = "Pas encore de meilleur score";
            Score score = scores.get(element.getType()+element.getCode());
            if (score != null) {
                text = "Meilleur score : "+score;
            }
            holder.getScoreTextview().setText(text);
        }

    }

    @Override
    public int getItemCount() {
        return this.localList.size();
    }


}
