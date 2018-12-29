package com.example.admin.learningenglish.vocabulary.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.learningenglish.R;
import com.example.admin.learningenglish.databases.Database;
import com.example.admin.learningenglish.models.TuVung;

import java.util.ArrayList;

/**
 * Created by admin on 4/25/2018.
 */

public class VocabularyAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<TuVung> listVocabulary;
    private int checkAtivity;

    public VocabularyAdapter(Context context, ArrayList<TuVung> listVocabulary, int checkAtivity) {
        this.context = context;
        this.listVocabulary = listVocabulary;
        this.checkAtivity = checkAtivity;
    }

    @Override
    public int getCount() {
        return listVocabulary.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.listview_vocabulary_row, null);

        ImageView imgMenuVocabulary = (ImageView) view.findViewById(R.id.imgMenuVocabulary);
        TextView txtWord = (TextView) view.findViewById(R.id.txtWord);
        TextView txtKindWord = (TextView) view.findViewById(R.id.txtKindWord);
        TextView txtTranslate = (TextView) view.findViewById(R.id.txtTranslate);

        txtWord.setText(listVocabulary.get(i).getTu());
        txtKindWord.setText(listVocabulary.get(i).getTuLoai());
        txtTranslate.setText(listVocabulary.get(i).getNghia());

        if (checkAtivity == 0) {
            imgMenuVocabulary.setVisibility(View.GONE);
        }

        imgMenuVocabulary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu(view, i);
            }
        });

        return view;
    }

    private void popupMenu(View view, final int i) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.menu_vocabulary, popupMenu.getMenu());
        popupMenu.show();

        //add event
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menuEdit:
                        showDialogEdit(i);
                        break;
                    case R.id.menuDelete:
                        deleteWord(i);
                        break;
                }
                return false;
            }
        });
    }

    private void showDialogEdit(final int index) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LinearLayout.inflate(context, R.layout.dialog_add_vocabulary, null);
        final EditText edtWord = (EditText) view.findViewById(R.id.edtWord);
        final EditText edtKindWord = (EditText) view.findViewById(R.id.edtKindWord);
        final EditText edtTranslate = (EditText) view.findViewById(R.id.edtTranslate);

        edtWord.setText(listVocabulary.get(index).getTu());
        edtKindWord.setText(listVocabulary.get(index).getTuLoai());
        edtTranslate.setText(listVocabulary.get(index).getNghia());

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
                        //xử lý sửa
                        TuVung tuVung = new TuVung();
                        tuVung.setId(listVocabulary.get(index).getId());
                        tuVung.setTu(edtWord.getText().toString());
                        tuVung.setTuLoai(edtKindWord.getText().toString());
                        tuVung.setNghia(edtTranslate.getText().toString());

                        boolean check = checkWork(tuVung);
                        if (check) {
                            editWork(index, tuVung);
                            Toast.makeText(context, "Đã sửa từ", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(context, "Từ đã tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        builder.create();
        builder.show();
    }

    private void editWork(int i, TuVung tuVung) {
        Database database = new Database(context);
        String sql = "update tuvung set tu = '" + tuVung.getTu() + "', " + "tuloai = '" + tuVung.getTuLoai() +
                "', " + "nghia = '" + tuVung.getNghia() + "' where id = " + tuVung.getId();
        database.queryData(sql);

        listVocabulary.set(i, tuVung);
        notifyDataSetChanged();
    }

    private boolean checkWork(TuVung tuVung) {
        Database database = new Database(context);
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

    private void deleteWord(int i) {
        Database database = new Database(context);
        String sql = "delete from tuvung where id = " + listVocabulary.get(i).getId();
        database.queryData(sql);

        listVocabulary.remove(i);
        notifyDataSetChanged();

        Toast.makeText(context, "Đã xóa từ", Toast.LENGTH_SHORT).show();
    }
}
