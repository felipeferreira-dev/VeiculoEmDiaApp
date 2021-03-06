package br.com.dev.felipeferreira.veiculoemdia.model.ui.combustivel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import br.com.dev.felipeferreira.veiculoemdia.R;



public class CombustivelFragment extends Fragment {

    private CombustivelViewModel combustivelViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        combustivelViewModel =
                new ViewModelProvider(this).get(CombustivelViewModel.class);
        View root = inflater.inflate(R.layout.fragment_combustivel, container, false);
        final TextView textView = root.findViewById(R.id.text_combustivel);
        combustivelViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }
}