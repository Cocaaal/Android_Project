package com.example.projet.models;

import com.example.projet.room.Score;

public class Fronce implements Collectivite{

    private String nom;

    public static final String FRONCE = "";

    public Fronce() {
        this.nom = "Fronce";
    }

    public String getNom() {
        return this.nom;
    }

    @Override
    public String getLogoLink() {
        return "https://upload.wikimedia.org/wikipedia/fr/thumb/2/22/Republique-francaise-logo.svg/512px-Republique-francaise-logo.svg.png";
    }

    @Override
    public String getCode() {
        return FRONCE;
    }

    public String getType() {
        return "fronce";
    }

}
