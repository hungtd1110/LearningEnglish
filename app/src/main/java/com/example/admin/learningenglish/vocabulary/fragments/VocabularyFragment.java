package com.example.admin.learningenglish.vocabulary.fragments;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.admin.learningenglish.R;
import com.example.admin.learningenglish.databases.Database;
import com.example.admin.learningenglish.main.activities.MainActivity;
import com.example.admin.learningenglish.models.TuVung;
import com.example.admin.learningenglish.vocabulary.activities.PracticeVocabularyActivity;
import com.example.admin.learningenglish.vocabulary.adapters.VocabularyAdapter;

import java.util.ArrayList;

/**
 * Created by admin on 3/22/2018.
 */

public class VocabularyFragment extends Fragment {
    ArrayList<TuVung> listVocabulary = new ArrayList<>();
    VocabularyAdapter adapter;

    ListView lvVocabulary;
    ImageView imgDelete, imgAdd, imgPractice;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vocabulary,container,false);

        getDatabase();
        addControls(view);
        addEvents();
        init();
        
        return view;
    }

    private void getDatabase() {
        Database database = new Database(getActivity());
        String sql = "select * from tuvung";
        Cursor cursor = database.getData(sql);
        listVocabulary.clear();
        while (cursor.moveToNext()) {
            TuVung tuVung = new TuVung();
            tuVung.setId(cursor.getString(0));
            tuVung.setTu(cursor.getString(1));
            tuVung.setTuLoai(cursor.getString(2));
            tuVung.setNghia(cursor.getString(3));

            listVocabulary.add(tuVung);
        }
    }

    private void init() {
        adapter = new VocabularyAdapter(getActivity(), listVocabulary, 1);
        lvVocabulary.setAdapter(adapter);
    }

    private void addControls(View view) {
        lvVocabulary = (ListView) view.findViewById(R.id.lvVocabulary);

        imgAdd = (ImageView) view.findViewById(R.id.imgAdd);
        imgDelete = (ImageView) view.findViewById(R.id.imgDelete);
        imgPractice = (ImageView) view.findViewById(R.id.imgPractice);
    }

    private void addEvents() {
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAdd();
            }
        });
        
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogDelete();
            }
        });

        imgPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogPractice();
            }
        });
    }

    private void showDialogPractice() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_practice_vocabulary, null);

        final Spinner spKindPractice = (Spinner) view.findViewById(R.id.spKindPractice);
        final EditText edtNumberWord = (EditText) view.findViewById(R.id.edtNumberWord);

        ArrayList<String> listSpinner = new ArrayList<>();
        listSpinner.add("Việt - Anh");
        listSpinner.add("Anh - Việt");
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, listSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKindPractice.setAdapter(adapter);

        builder.setTitle("Practice")
                .setView(view)
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //xử lý thực hành
                        boolean check = checkNumberWord(edtNumberWord.getText().toString());
                        if (check) {
                            Intent intent = new Intent(getActivity(), PracticeVocabularyActivity.class);
                            intent.putExtra("numberWord", edtNumberWord.getText().toString());
                            intent.putExtra("kindPractice", spKindPractice.getSelectedItem().toString());
                            startActivity(intent);
                        }
                    }
                });
        builder.create();
        builder.show();

    }

    private boolean checkNumberWord(String s) {
        try {
            int number = Integer.parseInt(s);
            if (number > listVocabulary.size()) {
                Toast.makeText(getActivity(), "Số từ tối đa là " + listVocabulary.size(), Toast.LENGTH_SHORT).show();
                return false;
            }
            else {
                return true;
            }
        }
        catch (Exception e) {
            Toast.makeText(getActivity(), "Hãy nhập chính xác số từ", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void showDialogDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete All")
                .setMessage("Bạn có chắc muốn xóa hết các từ không ?")
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //xử lý xóa
                        deleteAllWord();
                    }
                });
        builder.create();
        builder.show();
    }

    private void deleteAllWord() {
        Database database = new Database(getActivity());
        String sql = "delete from tuvung";
        database.queryData(sql);

        listVocabulary.clear();
        adapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "Đã xóa hết", Toast.LENGTH_SHORT).show();
    }

    private void showDialogAdd() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LinearLayout.inflate(getActivity(), R.layout.dialog_add_vocabulary, null);
        final EditText edtWord = (EditText) view.findViewById(R.id.edtWord);
        final EditText edtKindWord = (EditText) view.findViewById(R.id.edtKindWord);
        final EditText edtTranslate = (EditText) view.findViewById(R.id.edtTranslate);

        builder.setTitle("Add Word")
                .setView(view)
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //xử lý thêm
                        TuVung tuVung = new TuVung();
                        tuVung.setTu(edtWord.getText().toString());
                        tuVung.setTuLoai(edtKindWord.getText().toString());
                        tuVung.setNghia(edtTranslate.getText().toString());

                        boolean check = checkWork(tuVung);
                        if (check) {
                            addWork(tuVung);
                            getDatabase();
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), "Đã thêm từ", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getActivity(), "Từ đã tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        builder.create();
        builder.show();
    }

    private void addWork(TuVung tuVung) {
        Database database = new Database(getActivity());
        String sql = "insert into tuvung (tu, tuloai, nghia) values ('" + tuVung.getTu() + "', '" + tuVung.getTuLoai() +
                "', '" + tuVung.getNghia() + "')";
        database.queryData(sql);
    }

    private boolean checkWork(TuVung tuVung) {
        Database database = new Database(getActivity());
        String sql = "select count(tu) from tuvung where tu = '" + tuVung.getTu() + "' and tuloai = '" + tuVung.getTuLoai() + "'";
        Cursor cursor = database.getData(sql);
        while (cursor.moveToNext()) {
            int count = cursor.getInt(0);
            if (count == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.txtTitle.setText("Vocabulary");
        MainActivity.imgTimKiem.setVisibility(View.GONE);
        MainActivity.rlActionbarBot.setVisibility(View.GONE);
    }
}
