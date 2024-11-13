package com.example.projet.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Commune{

    @SerializedName("nom")
    @Expose
    private String nom;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("codeDepartement")
    @Expose
    private String codeDepartement;
    @SerializedName("siren")
    @Expose
    private String siren;
    @SerializedName("codeEpci")
    @Expose
    private String codeEpci;
    @SerializedName("codeRegion")
    @Expose
    private String codeRegion;
    @SerializedName("codesPostaux")
    @Expose
    private List<String> codesPostaux = null;
    @SerializedName("population")
    @Expose
    private Integer population;
    @SerializedName("mairie")
    @Expose
    private Mairie mairie;

    private boolean synonyme;
    private boolean decouverte = false;

    public boolean isFranceMetropolitaineOuDROM() {
        return this.getCodeRegion().length()<3;
    }

    public boolean isDecouverte() { return decouverte; }

    public void setDecouverte(boolean decouverte) { this.decouverte = decouverte; }

    public boolean isSynonyme() { return synonyme; }

    public void setSynonyme(boolean synonyme) { this.synonyme = synonyme; }

    public Mairie getMairie() { return mairie; }

    public void setMairie(Mairie mairie) { this.mairie = mairie; }

    public String getNom() {
        return nom;
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

    public String getCodeDepartement() {
        return codeDepartement;
    }

    public void setCodeDepartement(String codeDepartement) {
        this.codeDepartement = codeDepartement;
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

    public String getCodeEpci() {
        return codeEpci;
    }

    public void setCodeEpci(String codeEpci) {
        this.codeEpci = codeEpci;
    }

    public String getCodeRegion() {
        return codeRegion;
    }

    public void setCodeRegion(String codeRegion) {
        this.codeRegion = codeRegion;
    }

    public List<String> getCodesPostaux() {
        return codesPostaux;
    }

    public void setCodesPostaux(List<String> codesPostaux) {
        this.codesPostaux = codesPostaux;
    }

    public Integer getPopulation() {
        if (population == null) return 0;
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Commune.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("nom");
        sb.append('=');
        sb.append(((this.nom == null)?"<null>":this.nom));
        sb.append(',');
        sb.append("code");
        sb.append('=');
        sb.append(((this.code == null)?"<null>":this.code));
        sb.append(',');
        sb.append("codeDepartement");
        sb.append('=');
        sb.append(((this.codeDepartement == null)?"<null>":this.codeDepartement));
        sb.append(',');
        sb.append("siren");
        sb.append('=');
        sb.append(((this.siren == null)?"<null>":this.siren));
        sb.append(',');
        sb.append("codeEpci");
        sb.append('=');
        sb.append(((this.codeEpci == null)?"<null>":this.codeEpci));
        sb.append(',');
        sb.append("codeRegion");
        sb.append('=');
        sb.append(((this.codeRegion == null)?"<null>":this.codeRegion));
        sb.append(',');
        sb.append("codesPostaux");
        sb.append('=');
        sb.append(((this.codesPostaux == null)?"<null>":this.codesPostaux));
        sb.append(',');
        sb.append("population");
        sb.append('=');
        sb.append(((this.population == null)?"<null>":this.population));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}