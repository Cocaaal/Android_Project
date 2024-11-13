package com.example.projet.models;

import android.util.Log;

import com.example.projet.room.Score;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.Normalizer;

public class Region implements Collectivite{

    public final static String OUTREMER = "OUTREMER";
    public final static String BASELINK = "https://www.regions-departements-france.fr/images/regions/logo/";

    @SerializedName("nom")
    @Expose
    private String nom;
    @SerializedName("code")
    @Expose
    private String code;

    public String getNom() {
        return nom;
    }

    @Override
    public String getLogoLink() {
        if (getCode().equals(OUTREMER)) {
            return "https://media.istockphoto.com/id/586181316/fr/vectoriel/d%C3%A9partements-doutre-mer-france-carte-grise.jpg?s=612x612&w=0&k=20&c=cC2C5jWSghkvJTxQxejsuuGLlGV8IaDbIXsYGp2VEvY=";
        }
        String link = "logo-region-" + this.getNom().replace(' ','-').replace('\'','-').toLowerCase() + "-300.jpg";
        link = Normalizer.normalize(link, Normalizer.Form.NFKD);
        link = link.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return BASELINK + link;
    }

    public static Region getOutreMer() {
        Region res = new Region();
        res.setCode(Region.OUTREMER);
        res.setNom("Outre Mer");
        return res;
    }

    public String getType() {
        return "region";
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Region.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("nom");
        sb.append('=');
        sb.append(((this.nom == null)?"<null>":this.nom));
        sb.append(',');
        sb.append("code");
        sb.append('=');
        sb.append(((this.code == null)?"<null>":this.code));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}