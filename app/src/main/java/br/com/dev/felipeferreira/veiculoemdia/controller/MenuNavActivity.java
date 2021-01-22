package br.com.dev.felipeferreira.veiculoemdia.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.dev.felipeferreira.veiculoemdia.R;
import br.com.dev.felipeferreira.veiculoemdia.model.OnMenuItemClickListnerAdapter;
import br.com.dev.felipeferreira.veiculoemdia.model.Registros;

public class MenuNavActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    RecyclerView recyclerView;
    TextView textViewNenhumRegistro;
    SqlHelper sqlHelper;
    String sNenhumRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_nav);

        recyclerView = findViewById(R.id.recyclerView_registros);
        textViewNenhumRegistro = findViewById(R.id.txt_nenhumVeiculoCadastrado_registros);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_registros_de_veiculos, R.id.nav_combustivel)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);






        sNenhumRegistro = textViewNenhumRegistro.getText().toString();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String type = extras.getString("type");

            new Thread(() -> {

                List<Registros> registros = SqlHelper.getInstance(this).getRegistros(type);

                //Se "Veiculos cadastrados" = mudar para = "Nenhum veiculo cadastrado"
                if(!sNenhumRegistro.equals(getString(R.string.nenhumRegistro)))
                    textViewNenhumRegistro.setText(R.string.listaDeVeiculos);

                runOnUiThread(() -> {
                    RegistrosAdapter adapter = new RegistrosAdapter(registros);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));

                    adapter.setListener((id, type2, position, btnViewOptions, name, placa) -> {

                        //creating a popup menu
                        PopupMenu popup = new PopupMenu(MenuNavActivity.this, btnViewOptions);
                        //inflating menu from xml resource
                        popup.inflate(R.menu.menu);

                        //adding click listener
                        popup.setOnMenuItemClickListener(item -> {

                            switch (item.getItemId()) {
                                case R.id.menu_editar_registros:

                                    switch (type) {
                                        case "veiculo" :
                                            Intent intent = new Intent(this, MainActivity.class);
                                            intent.putExtra("updateId", id);
                                            intent.putExtra("updateUnicoId", id);
                                            startActivity(intent);
                                            finish();
                                            break;

                                        default:
                                            return false;
                                    }
                                    return true;

                                case R.id.menu_excluir_registros:
                                    new Thread(() -> {
                                        sqlHelper = SqlHelper.getInstance(MenuNavActivity.this);
                                        long removeId = sqlHelper.removeItem(id, type2);

                                        runOnUiThread(() -> {
                                            if (removeId > 0) {
                                                alertLong(R.string.excluidoComSucesso);
                                                registros.remove(position);
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                                    }).start();
                                    return true;

                                default:
                                    return false;
                            }
                        });
                        popup.show();
                    });
                });
            }).start();
        }
    }

    private class RegistrosAdapter extends RecyclerView.Adapter<RegistrosViewHolder> {

        private final List<Registros> registro;
        private OnMenuItemClickListnerAdapter listener;

        public RegistrosAdapter(List<Registros> registros) {
            this.registro = registros;
        }

        @NonNull
        @Override
        public RegistrosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_registos, parent, false);
            return new RegistrosViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RegistrosViewHolder viewHolder, int position) {
            Registros registros = registro.get(position);
            viewHolder.bind(registros, listener);
        }

        @Override
        public int getItemCount() {
            return registro.size();
        }

        public void setListener(OnMenuItemClickListnerAdapter listener) {
            this.listener = listener;
        }
    }

    private class RegistrosViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewPlaca;
        TextView textViewNenhumRegistro;
        TextView btnViewOptions;
        ImageView imgIcCarro;
        String sNenhumRegistro;

        int positionAdapter;

        public RegistrosViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.txt_itemRegistros_nomeCarro);
            textViewPlaca = itemView.findViewById(R.id.txt_itemRegistros_placa);
            textViewNenhumRegistro = findViewById(R.id.txt_nenhumVeiculoCadastrado_registros);
            sNenhumRegistro = textViewNenhumRegistro.getText().toString();
            btnViewOptions = (TextView) itemView.findViewById(R.id.textViewOptions);
            imgIcCarro = itemView.findViewById(R.id.img_itemRegistros_carro);

        }

        public void bind(Registros registro, final OnMenuItemClickListnerAdapter listener) {
            //Se "Nenhum veiculo cadastrado" = mudar para = "Veiculos cadastrados"
            if(sNenhumRegistro.equals(getString(R.string.nenhumRegistro)))
                textViewNenhumRegistro.setText(R.string.listaDeVeiculos);
            textViewName.setText(registro.name);
            textViewPlaca.setText(registro.placa);
            positionAdapter = getAdapterPosition();

            itemView.setOnClickListener(v -> listener.onMenuItemClickAdapter(registro.id, registro.type, positionAdapter, btnViewOptions, registro.name, registro.placa));
        }
    }

    public void alertShort(int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void alertLong(int message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nav, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}