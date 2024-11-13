package com.example.projet.viewmodels;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.projet.models.Collectivite;
import com.example.projet.models.Commune;
import com.example.projet.models.Departement;
import com.example.projet.room.Database;
import com.example.projet.room.Score;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.observers.DisposableObserver;

public class QuizzViewModel extends AndroidViewModel {

    private static final int DEFAULT_NB_VILLES = 10;

    private final MutableLiveData<List<Commune>> liste;
    private final MutableLiveData<Boolean> quizzEnd;
    private final MutableLiveData<Integer> score = new MutableLiveData<>();
    private final MutableLiveData<Integer> nbEssai = new MutableLiveData<>();

    private List<String> nomsVilles;
    private Collectivite collectivite;
    private int nbVilles;

    private final DataRepository repo;

    public QuizzViewModel(@NonNull Application application) {
        super(application);
        repo = DataRepository.getInstance(application.getApplicationContext());
        this.liste = new MutableLiveData<>();
        this.quizzEnd = new MutableLiveData<>();
        List<Commune> listetest = new ArrayList<>();
        listetest.add(new Commune());
        this.liste.setValue(listetest);
        this.nbVilles = -1;
        this.nbEssai.setValue(-1);
        this.collectivite = null;
    }

    public void setCollectivite(Collectivite collectivite) {
        boolean update = !collectivite.equals(this.collectivite);
        if (update) {
            this.collectivite = collectivite;
            this.setListe();
        }
    }
    public void setNbVilles(int nbVilles) {
        boolean update = nbVilles!=this.nbVilles;
        if (update) {
            this.nbVilles = nbVilles;
            this.setListe();
        }
    }

    public LiveData<Integer> getScore(){
        if (score.getValue() == null) {
            score.setValue(0);
        }
        return this.score;
    }

    public int getNbVilles(){ return this.nbVilles; }

    public MutableLiveData<Integer> getNbEssai(){ return this.nbEssai; }

    public LiveData<List<Commune>> getListe() {
        if (this.nbVilles==-1) {
            this.setNbVilles(DEFAULT_NB_VILLES);
        }
        return this.liste;
    }

    public LiveData<Boolean> isEnd() {
        if (this.quizzEnd.getValue() == null) {
            this.quizzEnd.setValue(false);
        }
        return this.quizzEnd;
    }

    private static final int VILLE_NON_PRESENTE = -1;
    private static final int VILLE_DEJA_DECOUVERTE = -2;
    public int devinerVille(String ville) {
        ville = normaliser(ville);
        int res = VILLE_NON_PRESENTE;

        //Recherche des occurences en commençant par la fin pour garder l'indice de la ville avec le plus d'habitants s'il y a des synonymes
        ArrayList<Integer> occurences = new ArrayList<>();
        for (int i=nomsVilles.size()-1; i>=0; i--) {
            if (nomsVilles.get(i).equals(ville)) {
                if (liste.getValue().get(i).isDecouverte()) {
                    res = VILLE_DEJA_DECOUVERTE;
                }else {
                    occurences.add(i);
                    res = i;
                }
            }
        }

        //Pour chaque occurence trouvée on incrémente le score et on la découvre
        for (int index : occurences) {
            if ((occurences.size() > 1) && !(collectivite instanceof Departement) ) {
                this.liste.getValue().get(index).setSynonyme(true);
            }
            this.liste.getValue().get(index).setDecouverte(true);
            this.score.setValue(this.score.getValue()+1);
        }

        switch (res) {
            case VILLE_NON_PRESENTE:
                this.nbEssai.setValue(this.nbEssai.getValue()-1);
                this.score.setValue(this.score.getValue());
                break;
            case VILLE_DEJA_DECOUVERTE:
                Toast.makeText(getApplication().getApplicationContext(), "Ville déja trouvée", Toast.LENGTH_SHORT).show();
                break;
            default :
                this.liste.setValue(this.liste.getValue());
                break;
        }

        if ((this.nbEssai.getValue() == 0) || (this.score.getValue() == nbVilles)) {
            endQuizz();
        }

        return res;
    }

    private void endQuizz() {
        this.quizzEnd.setValue(true);
        Score score = new Score(collectivite.getType()+collectivite.getCode(), this.score.getValue(), nbVilles);
        Database.databaseWriteExecutor.execute(() -> repo.setScore(score));
    }

    public void abandon(){
        this.nbEssai.setValue(0);
        endQuizz();
    }

    private void updateNomsVilles(List<Commune> communes) {
        this.nomsVilles = new ArrayList<>();
        for (Commune comm : communes) {
            String nom = this.normaliser(comm.getNom());
            this.nomsVilles.add(nom);
        }
    }

    private String normaliser(String string) {
        string = string.trim().toLowerCase().replaceAll("[- ]", "");
        string = Normalizer.normalize(string, Normalizer.Form.NFKD);
        string = string.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").replaceAll("œ","oe");
        return string;
    }

    public void setListe() {
        if (this.nbVilles == -1 || this.collectivite == null) {
            return;
        }

        switch (this.collectivite.getType()) {
            case "fronce":
                this.repo.getCommunesLesPlusPeupleesFronce(this.nbVilles).subscribeWith(new DisposableObserver<List<Commune>>() {
                    @Override
                    public void onNext(List<Commune> communes) {
                        nbVilles = communes.size();
                        liste.setValue(communes);
                        nbEssai.setValue(Math.max(1,nbVilles/3));
                        score.setValue(0);
                        updateNomsVilles(communes);
                    }
                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onComplete() {
                    }
                });
                break;

            case "region":
                this.repo.getCommunesLesPlusPeupleesRegion((this.collectivite).getCode(), this.nbVilles).subscribeWith(new DisposableObserver<List<Commune>>() {
                    @Override
                    public void onNext(List<Commune> communes) {
                        nbVilles = communes.size();
                        liste.setValue(communes);
                        nbEssai.setValue(Math.max(1,nbVilles/3));
                        score.setValue(0);
                        updateNomsVilles(communes);
                    }
                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onComplete() {

                    }
                });
                break;

            case "departement":
                this.repo.getCommunesLesPlusPeupleesDepartement((this.collectivite).getCode(), this.nbVilles).subscribeWith(new DisposableObserver<List<Commune>>() {
                    @Override
                    public void onNext(List<Commune> communes) {
                        nbVilles = communes.size();
                        liste.setValue(communes);
                        nbEssai.setValue(Math.max(1,nbVilles/3));
                        score.setValue(0);
                        updateNomsVilles(communes);
                    }
                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onComplete() {
                    }
                });
                break;
            default:
                break;
        }
    }
}
