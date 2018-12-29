package com.example.admin.learningenglish.main.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.admin.learningenglish.R;
import com.example.admin.learningenglish.databases.Database;
import com.example.admin.learningenglish.dictionary.fragments.DictionaryFragment;
import com.example.admin.learningenglish.grammar.fragments.GrammarFragment;
import com.example.admin.learningenglish.listen.fragments.ListenFragment;
import com.example.admin.learningenglish.listen_response.fragments.ListenResponseFragment;
import com.example.admin.learningenglish.main.fragments.MainFragment;
import com.example.admin.learningenglish.reading.fragments.ReadingFragment;
import com.example.admin.learningenglish.vocabulary.fragments.VocabularyFragment;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerMain;
    private FragmentTransaction transaction;
    public static TextView txtTitle, txtBaiHoc;
    public static RelativeLayout rlActionbarBot;
    public static Button btnHocTiep;
    public static ImageView imgTimKiem;
    public static MaterialSearchView searchView;
    private ImageView imgMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        addActionbar();
        khoiTao();
        addEvents();
        getDatabase();
    }

    private void getDatabase() {
        Database database = new Database(this);
        database.createDatabase();
    }

    private void khoiTao() {
        Fragment mainFragment = new MainFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, mainFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void addEvents() {
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerMain.isDrawerOpen(Gravity.LEFT)) {
                    drawerMain.closeDrawers();
                }
                else {
                    drawerMain.openDrawer(Gravity.LEFT);
                }
            }
        });

    }

    private void addActionbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom_1);

        rlActionbarBot = (RelativeLayout) findViewById(R.id.rlActionbarBot);
        imgMenu = (ImageView) findViewById(R.id.imgMenu);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtBaiHoc = (TextView) findViewById(R.id.txtBaiHoc);
        btnHocTiep = (Button) findViewById(R.id.btnHocTiep);
        imgTimKiem = (ImageView) findViewById(R.id.imgTimKiem);

        rlActionbarBot.setVisibility(View.GONE);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
    }

    private void addControls() {
        drawerMain = (DrawerLayout) findViewById(R.id.drawer_layout);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
//        searchView.setEllipsize(true);
    }

    @Override
    public void onBackPressed() {
        if (drawerMain.isDrawerOpen(GravityCompat.START)) {
            drawerMain.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.mnuHome) {
            Fragment mainFragment = new MainFragment();
            transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, mainFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else if (id == R.id.mnuDictionary) {
            Fragment dictionaryFragment = new DictionaryFragment();
            transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, dictionaryFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else if (id == R.id.mnuVocabulary) {
            Fragment vocabularyFragment = new VocabularyFragment();
            transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, vocabularyFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else if (id == R.id.mnuListen) {
            Fragment listenFragment = new ListenFragment();
            transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, listenFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else if (id == R.id.mnuListenResponse) {
            Fragment listenResponseFragment = new ListenResponseFragment();
            transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, listenResponseFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else if (id == R.id.mnuGrammar) {
            Fragment grammarFragment = new GrammarFragment();
            transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, grammarFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else if (id == R.id.mnuReading) {
            Fragment readingFragment = new ReadingFragment();
            transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, readingFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        drawerMain.closeDrawer(GravityCompat.START);
        return true;
    }
}
