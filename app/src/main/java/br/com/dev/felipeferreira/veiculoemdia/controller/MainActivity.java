package br.com.dev.felipeferreira.veiculoemdia.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import br.com.dev.felipeferreira.veiculoemdia.R;

public class MainActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editPlaca;
    private Button btnCadastro;
    private Button btnVerRegistro;
    private ImageView imgCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editName = findViewById(R.id.editText_main_name);
        editPlaca = findViewById(R.id.editText_main_placa);
        btnCadastro = findViewById(R.id.btn_main_cadastro);
        btnVerRegistro = findViewById(R.id.btn_main_verRegistros);
        imgCancelar = findViewById(R.id.img_main_cancelar);

        int updateUnicoId = 0;

        if (getIntent().getExtras() != null) {
            updateUnicoId = getIntent().getExtras().getInt("updateUnicoId", 0);
        }

        if (updateUnicoId > 0) {
            SqlHelper sqlHelper = SqlHelper.getInstance(this);
            Cursor cursor = sqlHelper.displayLastCustomer(updateUnicoId, "veiculo");
            if (cursor != null) {
                editName.setText(cursor.getString(cursor.getColumnIndex("name")));
                editPlaca.setText(cursor.getString(cursor.getColumnIndex("placa")));
            }
        }

        btnVerRegistro.setOnClickListener(v -> {
            openRegistros();
        });

        btnCadastro.setOnClickListener(v -> {
            if (!validate()) {
                alertShort(R.string.campoInvalido);
                return;
            }

            if (validatePlaca()) {
                alertShort(R.string.placaInvalida);
                return;
            }

            String sPlaca = editPlaca.getText().toString();
            String sName = editName.getText().toString();
            String type = "veiculo";

            new Thread(() -> {
                SqlHelper sqlHelper = SqlHelper.getInstance(MainActivity.this);

                int updateId = 0;

                if (getIntent().getExtras() != null) {
                    updateId = getIntent().getExtras().getInt("updateId", 0);
                }

                long veiculoId;

                if (updateId > 0) {
                    veiculoId = sqlHelper.updateItem(updateId, type, sName, sPlaca);
                } else {
                    veiculoId = sqlHelper.addItem(type, sName, sPlaca);
                }

                int finalUpdateId = updateId;

                runOnUiThread(() -> {
                    if (veiculoId > 0) {
                        if (finalUpdateId > 0) {
                            alertLong(R.string.veiculoAtualizado);
                            openRegistros();
                            finish();
                        } else {
                            alertLong(R.string.veiculoCadastrado);
                            openRegistros();
                        }
                    }
                    // Testando a branch Back-End
                    if (imgCancelar.getVisibility() == View.INVISIBLE)
                        imgCancelar.setVisibility(View.INVISIBLE);
                    imgCancelar.setOnClickListener(view -> {startActivity(new Intent(MainActivity.this, MenuNavActivity.class));});
                });
            }).start();
        });
    }

    public void alertShort(int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void alertLong(int message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void openRegistros() {
        Intent intent = new Intent(this, MenuNavActivity.class);
        intent.putExtra("type", "veiculo");
        startActivity(intent);
    }

    public boolean validate() {
        return !editName.getText().toString().isEmpty() &&
                !editPlaca.getText().toString().isEmpty();
    }

    public boolean validatePlaca() {
        //String sPlaca = editPlaca.getText().toString().trim();
        String sPlaca = editPlaca.getText().toString().replaceAll("[^A-Za-z0-9]", "");

        return sPlaca.length() != 7;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}