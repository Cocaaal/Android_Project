package com.example.projet.models;

import android.util.Log;

import com.example.projet.room.Score;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.Normalizer;

public class Departement implements Collectivite{

    public final static String BASELINK = "https://www.regions-departements-france.fr/images/departements/logo/";
    public final static Collectivite[] DEPARTEMENTS_OUTREMER = {
            new Departement("Guadeloupe","971"),
            new Departement("Martinique","972"),
            new Departement("Guyane","973"),
            new Departement("La Réunion","974"),
            new Departement("Mayotte","976")
    };

    @SerializedName("nom")
    @Expose
    private String nom;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("codeRegion")
    @Expose
    private String codeRegion;

    public Departement(String nom, String code) {
        this.nom = nom;
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    @Override
    public String getLogoLink() {
        String logo = "-logo-";
        if (getCode().length() == 3) {
            logo = "-logo-departement-";
        }

        String nom = getNom();
        if ("54".equals(getCode())) {
            nom = "meurhe-et-moselle";
        }

        String link = this.getCode() + logo + nom.replace(' ','-').replace('\'','-').toLowerCase() + ".jpg";
        link = Normalizer.normalize(link, Normalizer.Form.NFKD);
        link = link.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        return BASELINK + link;
    }

    public String getType() {
        return "departement";
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

    public String getCodeRegion() {
        return codeRegion;
    }

    public void setCodeRegion(String codeRegion) {
        this.codeRegion = codeRegion;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Departement.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("nom");
        sb.append('=');
        sb.append(((this.nom == null)?"<null>":this.nom));
        sb.append(',');
        sb.append("code");
        sb.append('=');
        sb.append(((this.code == null)?"<null>":this.code));
        sb.append(',');
        sb.append("codeRegion");
        sb.append('=');
        sb.append(((this.codeRegion == null)?"<null>":this.codeRegion));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}