package com.example.projet.quizzactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Guideline;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.projet.R;
import com.example.projet.models.Collectivite;
import com.example.projet.models.Departement;
import com.example.projet.models.Fronce;
import com.example.projet.models.Region;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;
import com.example.projet.models.Commune;
import com.example.projet.viewmodels.QuizzViewModel;

import org.json.JSONException;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuizzActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap map;
    private MapView mMapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private List<Marker> markers;

    private HashMap<String, LatLngBounds> bboxs;
    private LatLngBounds lastResize;
    private MutableLiveData<Boolean> mapReady;

    private Collectivite collectivite;

    private RecyclerView recyclerView;
    private QuizzAdapter quizzAdapter;
    private QuizzViewModel viewModel;

    private ImageView confirmButton;
    private TextView exitButton;
    private TextView editText;
    private TextView essaiText;
    private TextView scoreText;

    private boolean isKeyboardShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz);

        markers = new ArrayList<>();

        mapReady = new MutableLiveData<>();
        mapReady.setValue(false);
        mapReady.observe(this, aBoolean -> dessinerVilles());

        Intent intent = getIntent();
        collectivite = (Collectivite) intent.getSerializableExtra("collectivite");

        this.initRecyclerView();
        this.initInterface(savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(QuizzViewModel.class);

        this.viewModel.getListe().observe(this, communes -> {
            quizzAdapter.setLocalList(communes);
            dessinerVilles();
        });
        this.viewModel.isEnd().observe(this, quizzEnd -> {
            if (quizzEnd) {
                quizzAdapter.setLocalList(viewModel.getListe().getValue());
                quizzAdapter.quizzEnd();
                quizzEnd();
            }else {
                quizzStart();
            }
        });
        this.viewModel.getScore().observe(this, score -> essaiText.setText(
                score+"/"+viewModel.getNbVilles()+", "+ viewModel.getNbEssai().getValue()+"♥"
        ));

        this.viewModel.getNbEssai().observe(this, (Observer<Integer>) nbEssai -> essaiText.setText(
                viewModel.getScore().getValue()+"/"+viewModel.getNbVilles()+", "+ nbEssai+"♥"
        ));

        this.viewModel.setCollectivite(collectivite);

        final View activityRootView = findViewById(R.id.quizz_activity_root);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) return;
                Rect r = new Rect();
                Guideline guide = findViewById(R.id.guide);
                TextView title = findViewById(R.id.title_fronce_quizz);

                activityRootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = activityRootView.getRootView().getHeight();

                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                    if (!isKeyboardShowing) {
                        isKeyboardShowing = true;
                        guide.setGuidelinePercent(0.8f);
                        title.setVisibility(View.GONE);
                    }
                }
                else {
                    if (isKeyboardShowing) {
                        isKeyboardShowing = false;
                        guide.setGuidelinePercent(0.6f);
                        title.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        this.initInteraction();

    }

    private void resizeMap(String obj) {
        if (!bboxs.isEmpty() && map != null) {
            if (obj.equals("last")) {
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(lastResize, 10));
            }else {
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bboxs.get(obj), 10));
                lastResize = bboxs.get(obj);
            }
        }
    }

    private void initRecyclerView() {
        this.recyclerView = findViewById(R.id.city_list);
        this.quizzAdapter = new QuizzAdapter();
        this.recyclerView.setAdapter(quizzAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void initInterface(Bundle savedInstanceState){
        TextView titleTextView = findViewById(R.id.quizz_title);
        titleTextView.setText(collectivite.getNom());

        bboxs = new HashMap<>();
        int nbBoutons = 1;
        if (collectivite.getCode().equals(Fronce.FRONCE)) {
            //Afficher tous les boutons
            initResizeBouton(new Fronce());
            nbBoutons++;
            for (Collectivite dept : Departement.DEPARTEMENTS_OUTREMER) {
                initResizeBouton(dept);
                nbBoutons++;
            }
        }else if (collectivite.getCode().equals(Region.OUTREMER)) {
            //Afficher que les outre-mer
            for (Collectivite dept : Departement.DEPARTEMENTS_OUTREMER) {
                initResizeBouton(dept);
                nbBoutons++;
            }
        }else {
            //Afficher que bouton_resize
            String filename = geoJsonFileName(collectivite);
            bboxs.put(filename, null);
        }
        ((LinearLayout)findViewById(R.id.map_buttons)).setWeightSum(nbBoutons);

        ImageView btn = findViewById(R.id.bouton_resize);
        btn.setOnClickListener(view -> resizeMap("last"));


        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    private void initResizeBouton(Collectivite coll) {
        String filename = geoJsonFileName(coll);
        bboxs.put(filename, null);
        ImageView btn = findViewById(getResources().getIdentifier("bouton_"+coll.getCode(), "id",getPackageName()));
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(view -> resizeMap(filename));
    }

    private void initInteraction(){
        editText = findViewById(R.id.edit_text);
        confirmButton = findViewById(R.id.confirm_button);
        essaiText = findViewById(R.id.essai_text);
        exitButton = findViewById(R.id.exit_button);
        scoreText = findViewById(R.id.text_score);

        confirmButton.setOnClickListener(view -> {
            if (editText.getText().toString().isEmpty()) return;
            int res = viewModel.devinerVille(editText.getText().toString());
            if (res >= 0) recyclerView.getLayoutManager().scrollToPosition(res);
            editText.setText("");
        });
        ((ImageView) findViewById(R.id.param)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                EditText input = new EditText(getApplicationContext());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(5) });
                new AlertDialog.Builder(QuizzActivity.this)
                    .setTitle("Nombre de villes à deviner :")
                    .setView(input)
                    .setPositiveButton("Valider", (dialog, whichButton) -> {
                        String inputTxt = input.getText().toString();
                        if (!inputTxt.equals("")) {
                            int nbVilles = Integer.parseInt(inputTxt);
                            if (nbVilles==0) return;
                            viewModel.setNbVilles(nbVilles);
                            if (viewModel.isEnd().getValue()) {
                                quizzStart();
                            }
                        }
                    })
                    .setNegativeButton("Annuler", (dialog, whichButton) -> {})
                    .show();
            }
        });
    }

    private String geoJsonFileName(Collectivite collectivite) {
        String filename = "fronce";
        if (!collectivite.getCode().equals("")){
            filename = collectivite.getType() + "_" + collectivite.getCode() + "_"
                    + collectivite.getNom().replace(' ','_').replace('\'','_').replace('-','_');
            filename = filename.toLowerCase();
            filename = Normalizer.normalize(filename, Normalizer.Form.NFKD);
            filename = filename.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        }
        return filename;
    }

    private void quizzEnd(){
        exitButton.setText(R.string.quitter);
        exitButton.setOnClickListener(view -> finish());
        editText.setVisibility(View.INVISIBLE);
        confirmButton.setVisibility(View.INVISIBLE);
        scoreText.setVisibility(View.VISIBLE);
        scoreText.setText("Partie Terminée, score: "+viewModel.getScore().getValue()+"/"+viewModel.getNbVilles());
    }

    private void quizzStart(){
        exitButton.setText(R.string.abandon);
        exitButton.setOnClickListener(view -> viewModel.abandon());
        editText.setVisibility(View.VISIBLE);
        confirmButton.setVisibility(View.VISIBLE);
        scoreText.setVisibility(View.INVISIBLE);
    }

    private void dessinerVilles() {
        List<Commune> communes = viewModel.getListe().getValue();
        if ( !mapReady.getValue() || communes.get(0).getCode()==null ) return;

        for (Marker m : markers) {
            m.remove();
        }
        markers = new ArrayList<>();

        for (int i=0; i<communes.size(); i++) {
            Commune comm = communes.get(i);
            LatLng center = new LatLng(comm.getMairie().getCoordinates().get(1),comm.getMairie().getCoordinates().get(0));
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_red);
            if (comm.isDecouverte()) {
                icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_green);
            }
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(center)
                    .icon(icon)
            );
            markers.add(marker);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(),R.raw.map_style));

        mapReady.setValue(true);
        for (String filename : bboxs.keySet()) {
            int id = getResources().getIdentifier(filename, "raw", getPackageName());
            try {
                GeoJsonLayer layer = new GeoJsonLayer(map, id, getApplicationContext());
                GeoJsonPolygonStyle style = layer.getDefaultPolygonStyle();
                style.setStrokeColor(Color.parseColor("#1f3b82"));
                style.setStrokeWidth(5f);
                layer.addLayerToMap();
                bboxs.put(filename, layer.getBoundingBox());

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

        }
        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if (collectivite.getCode().equals(Fronce.FRONCE)) {
                    lastResize = bboxs.get(geoJsonFileName(new Fronce()));
                }else {
                    lastResize = bboxs.get(bboxs.keySet().toArray()[0]);
                }
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(lastResize, 10));
                map.setOnMarkerClickListener(marker -> {
                    Commune commune = viewModel.getListe().getValue().get(markers.indexOf(marker));
                    if (commune.isDecouverte() || viewModel.isEnd().getValue()) {
                        if (marker.getTitle() == null) {
                            marker.setTitle(commune.getNom());
                            marker.showInfoWindow();
                        }
                        return false;
                    }else {
                        return true;
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}