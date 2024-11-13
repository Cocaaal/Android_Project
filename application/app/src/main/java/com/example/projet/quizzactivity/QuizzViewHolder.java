package com.example.projet.quizzactivity;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet.R;

public class QuizzViewHolder extends RecyclerView.ViewHolder {

    private final TextView nom;
    private final TextView numero;
    private final TextView population;

    public QuizzViewHolder(@NonNull View view) {
        super(view);

        this.nom = view.findViewById(R.id.quizz_viewholder_nom_ville);
        this.numero = view.findViewById(R.id.quizz_viewholder_num_ville);
        this.population = view.findViewById(R.id.quizz_viewholder_population);
    }

    public TextView getNomTextView() {
        return nom;
    }
    public TextView getNumeroTextView() {
        return numero;
    }
    public TextView getPopulationTextView() {
        return population;
    }
}
