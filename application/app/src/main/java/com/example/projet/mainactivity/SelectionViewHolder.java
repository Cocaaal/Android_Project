package com.example.projet.mainactivity;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projet.R;

public class SelectionViewHolder extends RecyclerView.ViewHolder {

    private final TextView titleTextView;
    private final ImageView imageView;
    private final View itemView;
    private final TextView score;

    public SelectionViewHolder(@NonNull View view) {
        super(view);

        this.titleTextView = view.findViewById(R.id.quizz_viewholder_nom_ville);
        this.imageView = view.findViewById(R.id.logo_image);
        this.itemView = view;
        this.score = view.findViewById(R.id.meilleur_score);
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }
    public ImageView getImageView() {
        return imageView;
    }
    public View getItemView() { return itemView;}
    public TextView getScoreTextview() { return score;}

}
