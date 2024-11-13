package com.example.projet.mainactivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projet.R;
import com.example.projet.models.Collectivite;
import com.example.projet.room.Score;
import com.example.projet.viewmodels.SelectionViewModel;

import java.util.List;

import io.reactivex.observers.DisposableObserver;

public class SelectionFragment extends Fragment{

    private static final String ARG_TYPE = "type";

    private char type;

    private RecyclerView recyclerView;
    private SelectionAdapter selectionAdapter;

    private SelectionViewModel viewModel;

    public SelectionFragment() {
        // Required empty public constructor
    }

    public static SelectionFragment newInstance(char param) {
        SelectionFragment fragment = new SelectionFragment();
        Bundle args = new Bundle();
        args.putChar(ARG_TYPE, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getChar(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_selection, container, false);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.viewModel = new ViewModelProvider(requireActivity()).get(SelectionViewModel.class);
        MutableLiveData<Boolean> listeReady = new MutableLiveData<>();
        listeReady.setValue(false);

        this.recyclerView = (RecyclerView) view.findViewById(R.id.quizz_list);

        this.selectionAdapter = new SelectionAdapter(getActivity().getApplicationContext());
        recyclerView.setAdapter(selectionAdapter);

        this.viewModel.getData(type).observe(getViewLifecycleOwner(), new Observer<List<? extends Collectivite>>() {
            @Override
            public void onChanged(List<? extends Collectivite> liste) {
                selectionAdapter.setLocalList(liste);
            }
        });
        this.viewModel.getViewType().observe(getViewLifecycleOwner(), new Observer<ViewType>() {
            @Override
            public void onChanged(ViewType viewType) {
                changeView(viewType);
            }
        });
        listeReady.observe(getViewLifecycleOwner(),
                bool -> viewModel.getScoresFromType(type).subscribeWith(
                        new DisposableObserver<List<Score>>() {
            @Override
            public void onNext(List<Score> res) {
                recyclerView.post(() -> {
                    selectionAdapter.updateScore(res);
                });
            }
            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                e.printStackTrace();
            }
            @Override
            public void onComplete() {
            }
        }));
    }

    public void changeView(ViewType viewType) {
        if (viewType == ViewType.GRID) {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 2));
        }else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        }
        selectionAdapter.setViewType(viewType);
        recyclerView.setAdapter(selectionAdapter);
    }

}