package com.example.projet.api;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.projet.models.Collectivite;
import com.example.projet.models.Commune;
import com.example.projet.models.Departement;
import com.example.projet.models.Region;
import com.google.android.gms.common.api.Api;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api_Geo {

    private final String URL = "https://geo.api.gouv.fr/";
    private static Retrofit retrofit;
    private static Api_Interface api;
    private static Api_Geo instance = null ;

    private Api_Geo() {
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            api = retrofit.create(Api_Interface.class);
        }

    }

    public static Api_Geo getInstance() {
        if (instance == null) {
            instance = new Api_Geo();
        }
        return instance;
    }

    public Observable<List<Commune>> getCommunesDansDepartement(String departement) {
        Observable<List<Commune>> communes = api.getCommunesDansDepartement(departement)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
        return communes;
    }
    public Observable<List<Commune>> getCommunesDansRegion(String region) {
        Observable<List<Commune>> communes = api.getCommunesDansRegion(region)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
        return communes;
    }
    public Observable<List<Commune>> getAllCommunes() {
        Observable<List<Commune>> communes = api.getAllCommunes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
        return communes;
    }

    public Observable<List<Departement>> getDepartements(String region) {
        Observable<List<Departement>> departements = api.getDepartementsDansRegion(region)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
        return departements;
    }
    public Observable<List<Departement>> getAllDepartements() {
        Observable<List<Departement>> departements = api.getAllDepartements()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
        return departements;
    }
    public Observable<List<Commune>> getCommunesLesPlusPeupleesDepartement(String departement, int nombre) {
        Observable<List<Commune>> communes = api.getCommunesDansDepartement(departement)
                .map(list -> {
                    list.sort((commune1, commune2) -> commune2.getPopulation()-commune1.getPopulation());
                    List<Commune> newList = list.subList(0,Math.min(nombre, list.size()));
                    return newList;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
        return communes;
    }

    public Observable<List<Region>> getRegions() {
        Observable<List<Region>> regions = api.getRegions()
                .map(list -> {
                    list.add(Region.getOutreMer());
                    return list;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
        return regions;
    }
    public Observable<List<Commune>> getCommunesLesPlusPeupleesRegion(String region, int nombre) {
        Observable<List<Commune>> communes = api.getCommunesDansRegion(region)
                .map(list -> {
                    list.sort((commune1, commune2) -> commune2.getPopulation()-commune1.getPopulation());
                    return list.subList(0,Math.min(nombre, list.size()));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
        return communes;
    }

    @SuppressLint("CheckResult")
    public Observable<List<Commune>> getCommunesLesPlusPeupleesOutremer(int nombre) {
        List<Commune> communes = new ArrayList<>();
        for (Collectivite dept : Departement.DEPARTEMENTS_OUTREMER) {
            String code = dept.getCode();
            List<Commune> commTmp = api.getCommunesDansDepartement(code)
                .map(list -> {
                    list.sort((commune1, commune2) -> commune2.getPopulation()-commune1.getPopulation());
                    return list.subList(0,Math.min(nombre, list.size()));
                })
                .subscribeOn(Schedulers.io())
                //.observeOn(AndroidSchedulers.mainThread())
                .blockingSingle();
            communes.addAll(commTmp);
        }
        communes.sort((commune1, commune2) -> commune2.getPopulation()-commune1.getPopulation());
        return Observable.just(communes.subList(0,Math.min(nombre, communes.size())));
    }

}
