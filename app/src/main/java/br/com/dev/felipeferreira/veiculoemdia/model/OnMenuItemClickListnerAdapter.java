package br.com.dev.felipeferreira.veiculoemdia.model;

import android.view.MenuItem;
import android.widget.TextView;

public interface OnMenuItemClickListnerAdapter {
    void onMenuItemClickAdapter(int id, String type, int position,  TextView btnVewOptions, String name, String placa);
}
