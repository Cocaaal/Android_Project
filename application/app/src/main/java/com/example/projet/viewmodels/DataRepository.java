package com.example.projet.viewmodels;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.projet.api.Api_Geo;
import com.example.projet.models.Commune;
import com.example.projet.models.Departement;
import com.example.projet.models.Region;
import com.example.projet.room.Database;
import com.example.projet.room.Score;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

public class DataRepository {

    private static DataRepository instance;
    private static Api_Geo api;

    private final String COMMUNES_PREFIX = "communes.json";
    private File communesFile;

    private final int NB_COMMUNES_LIMITE = 1000;

    private Context context;

    private MutableLiveData<Boolean> ready;

    private Database db;

    private DataRepository(Context context) {
        db = Room.databaseBuilder(context, Database.class, "fronce-quizz").build();
        api = Api_Geo.getInstance();
        this.context = context;
        this.ready = new MutableLiveData<Boolean>();
        this.ready.setValue(false);
        loadAllCommunes(context);
    }

    public static DataRepository getInstance(Context context) {
        if (instance == null) {
            instance = new DataRepository(context);
        }
        return instance;
    }


    public LiveData<Boolean> communesReady() {
        return this.ready;
    }

    @SuppressLint("CheckResult")
    private void loadAllCommunes(Context context) {
        if (!Arrays.asList(context.getExternalCacheDir().list()).contains(COMMUNES_PREFIX)) {
            getAllCommunes().subscribeWith(new DisposableObserver<List<Commune>>() {
                @Override
                public void onNext(List<Commune> communes) {
                    communes.removeIf(com -> !com.isFranceMetropolitaineOuDROM());
                    communes.sort((commune1, commune2) -> commune2.getPopulation()-commune1.getPopulation());
                    communes = communes.subList(0,NB_COMMUNES_LIMITE);
                    try {
                        communesFile = new File(context.getExternalCacheDir() + "/" + COMMUNES_PREFIX);
                        Writer fileWiter = new FileWriter(communesFile.getAbsolutePath());
                        new Gson().toJson(communes, fileWiter);
                        fileWiter.flush();
                        fileWiter.close();
                        ready.setValue(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(Throwable e) {

                }
                @Override
                public void onComplete() {

                }
            });
        }else {
            communesFile = new File(context.getExternalCacheDir() + "/" + COMMUNES_PREFIX);
            ready.setValue(true);
        }
    }

    private  String readCommunesFromFile() {
        String text = "";
        try {
            InputStream is = new FileInputStream(communesFile);

            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            text = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }


    //====================REMOTE====================//
    public Observable<List<Commune>> getCommunesLesPlusPeupleesDepartement(String departement, int nombre) {
        return api.getCommunesLesPlusPeupleesDepartement(departement, nombre);
    }
    public Observable<List<Commune>> getCommunesLesPlusPeupleesRegion(String region, int nombre) {
        if (region.equals(Region.OUTREMER)) {
            return api.getCommunesLesPlusPeupleesOutremer(nombre);
        }
        return api.getCommunesLesPlusPeupleesRegion(region,nombre);
    }

    public Observable<List<Region>> getRegions() {
        return api.getRegions();
    }
    public Observable<List<Departement>> getDepartements(String region) {
        return api.getDepartements(region);
    }
    public Observable<List<Departement>> getAllDepartements() {
        return api.getAllDepartements();
    }
    public Observable<List<Commune>> getAllCommunes() {
        return api.getAllCommunes();
    }


    //====================LOCAL====================//

    public Observable<List<Commune>> getCommunesLesPlusPeupleesFronce(int nombre) {
        List<Commune> communes = new ArrayList<>();
        String json = readCommunesFromFile();
        Type listType = new TypeToken<ArrayList<Commune>>() {}.getType();
        try {
            communes = new Gson().fromJson(json, listType);
            communes = communes.subList(0,Math.min(nombre,NB_COMMUNES_LIMITE));
            List<Commune> finalCommunes = communes;
            return Observable.create(emitter -> {
                emitter.onNext(finalCommunes);
                emitter.onComplete();
            });
        }
        catch (Exception e) {
            Log.e("ALLCOMM", e.toString());
            return null;
        }
    }

    public void setScore(Score newScore) {
        Score actualScore = db.scoreDao().getScoreFromId(newScore.getId());
        if (newScore.estMeilleur(actualScore)) {
            db.scoreDao().insertScore(newScore);
        }
    }

    public Observable<List<Score>> getAllScoresFromType(String type) {
        return db.scoreDao().getAllFromType(type);
    }

}
