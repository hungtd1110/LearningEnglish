package com.example.admin.learningenglish.dictionary.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.admin.learningenglish.databases.Database;
import com.example.admin.learningenglish.dictionary.activities.TranslateImageActivity;
import com.example.admin.learningenglish.dictionary.adapters.DictionaryAdapter;
import com.example.admin.learningenglish.dictionary.adapters.SuggestAdapter;
import com.example.admin.learningenglish.main.activities.MainActivity;
import com.example.admin.learningenglish.R;
import com.example.admin.learningenglish.dictionary.activities.RecentActivity;
import com.example.admin.learningenglish.dictionary.activities.TranslationActivity;
import com.example.admin.learningenglish.dictionary.activities.WordMarkActivity;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

/**
 * Created by admin on 10/7/2017.
 */

public class DictionaryFragment extends Fragment {
    private ListView lvTuDien,lvSuggest;
    private ArrayList<String> dsSuggest = new ArrayList<>();
    private ArrayList<String> dsSuggestCurrent = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dictionary,container,false);

        addControl(view);
        addEvent();
        khoiTao();

        return view;
    }

    private void khoiTao() {
        String[] ten = {
                "Từ vừa tra",
                "Đánh dấu",
                "Tra từ qua ảnh"
        };

        int[] hinh = {
                R.drawable.word_recent,
                R.drawable.word_mark,
                R.drawable.translation_image
        };

        DictionaryAdapter dictionaryAdapter = new DictionaryAdapter(getActivity(),ten,hinh);
        lvTuDien.setAdapter(dictionaryAdapter);
    }

    private void addEvent() {
        lvTuDien.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent;
                switch (i) {
                    case 0:
                        intent = new Intent(getActivity(),RecentActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(getActivity(),WordMarkActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(getActivity(),TranslateImageActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

        MainActivity.imgTimKiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.searchView.showSearch();
            }
        });

        MainActivity.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getActivity(),TranslationActivity.class);
                intent.putExtra("word",query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if (newText.length() > 2) {
                    if (newText.length() == 3) {
                        getDataSuggest(newText);
                    }
                    getDataSuggestCurrent(newText);
                }
                return false;
            }
        });

        MainActivity.searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }

            @Override
            public void onSearchViewClosed() {
                closeListSuggest();
            }
        });

        lvSuggest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(),TranslationActivity.class);
                intent.putExtra("word",dsSuggestCurrent.get(i));
                startActivity(intent);
            }
        });
    }

    private void getDataSuggestCurrent(String newText) {
        dsSuggestCurrent.clear();
        for (String s : dsSuggest) {
            if (s.contains(newText)) {
                dsSuggestCurrent.add(s);
            }
        }
        SuggestAdapter adapter = new SuggestAdapter(getActivity(),dsSuggestCurrent);
        lvSuggest.setAdapter(adapter);
    }

    private void closeListSuggest() {
        dsSuggestCurrent.clear();
        SuggestAdapter adapter = new SuggestAdapter(getActivity(),dsSuggestCurrent);
        lvSuggest.setAdapter(adapter);
    }

    private void getDataSuggest(String newText) {
        Database database = new Database(getActivity());
        Cursor cursor = database.getData("select tu from tudien where tu like '%" + newText + "%'");
        dsSuggest.clear();
        while (cursor.moveToNext()) {
            dsSuggest.add(cursor.getString(0));
        }
    }

    private void addControl(View view) {
        lvTuDien = (ListView) view.findViewById(R.id.lvTuDien);
        lvSuggest = (ListView) view.findViewById(R.id.lvSuggest);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.txtTitle.setText("Dictionary");
        MainActivity.rlActionbarBot.setVisibility(View.GONE);
        MainActivity.imgTimKiem.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        MainActivity.searchView.closeSearch();
    }
}
