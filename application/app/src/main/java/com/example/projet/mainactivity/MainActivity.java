package com.example.projet.mainactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.projet.R;
import com.example.projet.viewmodels.SelectionViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private ImageView changeViewButton;

    private SelectionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.viewModel = new ViewModelProvider(this).get(SelectionViewModel.class);
        this.changeViewButton = findViewById(R.id.swap_button);

        //Changer la vue quand on change de type d'affichage
        this.viewModel.getViewType().observe(this, viewType -> {
            if (viewType == ViewType.GRID) {
                changeViewButton.setImageResource(R.drawable.list);
            }else {
                changeViewButton.setImageResource(R.drawable.grid);
            }
        });

        //Quand le fichier contenant les communes est chargé on débloque la vue
        this.viewModel.communesReady().observe(this, bool -> {
            if (bool) {
                findViewById(R.id.loading).setVisibility(View.GONE);
            }
        });

        this.initViewPager();

        this.initButtons();
    }

    private void initViewPager() {
        ViewPager2 viewPager = findViewById(R.id.viewpager);
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), getLifecycle());

        fragmentAdapter.addFragment(SelectionFragment.newInstance('F'));
        fragmentAdapter.addFragment(SelectionFragment.newInstance('R'));
        fragmentAdapter.addFragment(SelectionFragment.newInstance('D'));

        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setAdapter(fragmentAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        String[] tabs = {"fronce","regions","departements"};
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabs[position])
        ).attach();
    }

    private void initButtons() {
        findViewById(R.id.swap_button).setOnClickListener(view -> viewModel.switchView());
    }

}