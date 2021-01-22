package br.com.dev.felipeferreira.veiculoemdia.model.ui.combustivel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CombustivelViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CombustivelViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}