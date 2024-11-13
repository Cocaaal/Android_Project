package com.example.projet.models;

import java.io.Serializable;

public interface Collectivite extends Serializable {
    public String getNom();
    public String getLogoLink();
    public String getCode();
    public String getType();
}
