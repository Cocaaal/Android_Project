package com.example.projet.api;

import com.example.projet.models.Commune;
import com.example.projet.models.Departement;
import com.example.projet.models.Region;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface Api_Interface {

    //==================== COMMUNES ====================//
    @GET("departements/{departement}/communes?fields=code,nom,population,codeRegion,mairie")
    Observable<List<Commune>> getCommunesDansDepartement(@Path("departement") String departement);

    @GET("communes?fields=code,nom,population,codeRegion,mairie,codeDepartement")
    Observable<List<Commune>> getCommunesDansRegion(@Query("codeRegion") String region);

    @GET("communes?fields=code,nom,population,codeRegion,mairie,codeDepartement")
    Observable<List<Commune>> getAllCommunes();


    //==================== DEPARTEMENT ====================//
    @GET("regions/{region}/departements")
    Observable<List<Departement>> getDepartementsDansRegion(@Path("region") String departement);

    @GET("departements")
    Observable<List<Departement>> getAllDepartements();


    //==================== REGIONS ====================//
    @GET("regions?limit=13")
    Observable<List<Region>> getRegions();




}
