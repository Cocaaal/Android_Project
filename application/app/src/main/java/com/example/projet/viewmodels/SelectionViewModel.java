package com.example.projet.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.projet.mainactivity.ViewType;
import com.example.projet.models.Collectivite;
import com.example.projet.models.Departement;
import com.example.projet.models.Fronce;
import com.example.projet.models.Region;
import com.example.projet.room.Score;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

public class SelectionViewModel extends AndroidViewModel {

    private final MutableLiveData<ViewType> viewType;

    private final MutableLiveData<List<? extends Collectivite>> fronce;
    private final MutableLiveData<List<? extends Collectivite>> regions;
    private final MutableLiveData<List<? extends Collectivite>> departements;

    private final DataRepository repo;

    public SelectionViewModel(@NonNull Application application) {
        super(application);
        repo = DataRepository.getInstance(application.getApplicationContext());
        this.fronce = new MutableLiveData<>();
        this.regions = new MutableLiveData<>();
        this.departements = new MutableLiveData<>();
        this.viewType = new MutableLiveData<>();
        this.loadData();
    }

    public LiveData<List<? extends Collectivite>> getData(char type) {
        switch(type) {
            case 'F' :
                return this.fronce;
            case 'R' :
                return this.regions;
            case 'D' :
                return this.departements;
        }
        return null;
    }

    public LiveData<ViewType> getViewType() {
        if (viewType.getValue() == null) {
            this.viewType.setValue(ViewType.LIST);
        }
        return this.viewType;
    }

    public void switchView() {
        if (this.viewType.getValue() == ViewType.LIST) {
            this.viewType.setValue(ViewType.GRID);
        }else {
            this.viewType.setValue(ViewType.LIST);
        }
    }

    @SuppressLint("CheckResult")
    public void loadData() {
        List<Collectivite> tmp = new ArrayList<>();
        tmp.add(new Fronce());
        this.fronce.setValue(tmp);

        this.repo.getRegions().subscribeWith(new DisposableObserver<List<Region>>() {
            @Override
            public void onNext(List<Region> res) {
                regions.setValue(res);
            }
            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                e.printStackTrace();
            }
            @Override
            public void onComplete() {
            }
        });

        this.repo.getAllDepartements().subscribeWith(new DisposableObserver<List<Departement>>() {
            @Override
            public void onNext(List<Departement> res) {
                departements.setValue(res);
            }
            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                e.printStackTrace();
            }
            @Override
            public void onComplete() {

            }
        });
    }

    public LiveData<Boolean> communesReady() {
        return repo.communesReady();
    }

    public Observable<List<Score>> getScoresFromType(char type) {
        String query = "%";
        switch (type) {
            case 'F' :
                query = "fronce"+query;
                break;
            case 'R' :
                query = "region"+query;
                break;
            case 'D' :
                query = "departement"+query;
                break;
        }
        return this.repo.getAllScoresFromType(query+"%");
    }


}
