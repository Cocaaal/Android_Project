package com.example.projet.quizzactivity;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet.R;
import com.example.projet.models.Commune;

import java.util.List;

public class QuizzAdapter extends  RecyclerView.Adapter<QuizzViewHolder> {

    private List<Commune> localList;
    private boolean quizzEnd;

    public QuizzAdapter(){}

    public void setLocalList(List<Commune> localList) {
        this.localList = localList;
        this.quizzEnd = false;
        notifyDataSetChanged();
    }

    public void quizzEnd() {
        this.quizzEnd = true;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuizzViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quizz_viewholder, parent, false);

        return new QuizzViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizzViewHolder holder, int position) {
        Commune element = this.localList.get(position);
        this.setData(holder, element, position);
    }

    @Override
    public int getItemCount() {
        return this.localList.size();
    }

    private void setData(QuizzViewHolder holder, Commune commune, int position) {
        String num = " "+(position+1)+" ";
        if (position == 0) {
            num = "\uD83C\uDFC6";
        }else if (position == 1) {
            num = "\uD83E\uDD48";
        }else if (position == 2) {
            num = "\uD83E\uDD49";
        }

        String synonyme = "";
        if (commune.isSynonyme()) {
            synonyme = ", " + commune.getCodeDepartement();
        }

        if (commune.isDecouverte()) {
            holder.getNumeroTextView().setText(num + " -");
            holder.getNomTextView().setText( commune.getNom() + synonyme );
            holder.getPopulationTextView().setText("(" + commune.getPopulation() + " hbts)");
            holder.getNomTextView().setTextColor(Color.BLACK);
        } else {
            holder.getNumeroTextView().setText(num + " -");
            if (quizzEnd) {
                holder.getNomTextView().setText(commune.getNom() + synonyme );
                holder.getPopulationTextView().setText("(" + commune.getPopulation() + " hbts)");
                holder.getNomTextView().setTextColor(Color.RED);
            }else {
                holder.getNomTextView().setText("");
                holder.getPopulationTextView().setText("");
            }
        }
    }
}
