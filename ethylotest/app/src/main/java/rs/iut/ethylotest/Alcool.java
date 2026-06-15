package rs.iut.ethylotest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

/** Écran de saisie d'une boisson : sélection prédéfinie ou personnalisée, et déclenchement de la consommation. */
public class Alcool extends AppCompatActivity {

    // Boissons prédéfinies : {volume en ml, degré en fraction}
    private static final double[][] PREDEFINIS = {
            {120, 0.13},  // Vin
            {250, 0.06},  // Bière
            {20,  0.40},  // Whisky
    };
    private static final int IDX_CUSTOM = 3;

    private Spinner spinnerBoisson;
    private EditText editVolume;
    private EditText editDegre;
    private Button btnConsommer;
    private Button btnRetour;
    private TextView tvTauxActuel;

    private Boisson boisson;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            afficherTauxActuel();
            handler.postDelayed(this, 1000);
        }
    };

    /** Initialise l'interface, le spinner de boissons et les listeners. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcool);

        spinnerBoisson = findViewById(R.id.spinnerBoisson);
        editVolume     = findViewById(R.id.editVolume);
        editDegre      = findViewById(R.id.editDegre);
        btnConsommer   = findViewById(R.id.btnConsommer);
        btnRetour      = findViewById(R.id.btnRetour);
        tvTauxActuel   = findViewById(R.id.tvTauxActuel);

        boisson = new Boisson();

        setupSpinner();

        btnConsommer.setOnClickListener(this::onConsommer);
        btnRetour.setOnClickListener(v -> finish());
        findViewById(R.id.btnVoirAlcoolemie).setOnClickListener(v ->
                startActivity(new Intent(this, AlcoolemieActivity.class)));
    }

    /** Restaure la dernière boisson saisie et démarre le rafraîchissement du taux. */
    @Override
    protected void onStart() {
        super.onStart();
        loadBoisson();
        handler.post(runnable);
    }

    /** Stoppe le timer et sauvegarde la boisson courante dans les SharedPreferences. */
    @Override
    protected void onStop() {
        handler.removeCallbacks(runnable);
        controlsToBoisson();
        saveBoisson();
        super.onStop();
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.boissons_predefinies));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBoisson.setAdapter(adapter);

        spinnerBoisson.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < IDX_CUSTOM) {
                    editVolume.setText(String.valueOf((int) PREDEFINIS[position][0]));
                    editDegre.setText(String.valueOf((int) (PREDEFINIS[position][1] * 100)));
                    editVolume.setEnabled(false);
                    editDegre.setEnabled(false);
                } else {
                    editVolume.setEnabled(true);
                    editDegre.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void controlsToBoisson() {
        try {
            boisson.setVolume(Double.parseDouble(editVolume.getText().toString()));
            boisson.setDegre(Double.parseDouble(editDegre.getText().toString()) / 100.0);
        } catch (NumberFormatException e) {
            boisson.setVolume(0);
            boisson.setDegre(0);
        }
    }

    private void saveBoisson() {
        SharedPreferences prefs = getSharedPreferences("ethylotest_prefs", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        Gson gson = new Gson();
        ed.putString("boisson", gson.toJson(boisson));
        ed.apply();
    }

    private void loadBoisson() {
        SharedPreferences prefs = getSharedPreferences("ethylotest_prefs", MODE_PRIVATE);
        String str = prefs.getString("boisson", null);
        if (str != null) {
            Gson gson = new Gson();
            boisson = gson.fromJson(str, Boisson.class);
            // champ personalisé
            spinnerBoisson.setSelection(IDX_CUSTOM);
            editVolume.setText(String.valueOf((int) boisson.getVolume()));
            editDegre.setText(String.valueOf((int) (boisson.getDegre() * 100)));
        }
    }

    private void onConsommer(View v) {
        controlsToBoisson();

        if (boisson.getVolume() <= 0 || boisson.getDegre() <= 0) {
            Toast.makeText(this, getString(R.string.erreur_saisie), Toast.LENGTH_SHORT).show();
            return;
        }

        // Lire la personne
        SharedPreferences prefs = getSharedPreferences("ethylotest_prefs", MODE_PRIVATE);
        String strPersonne = prefs.getString("personne", null);
        if (strPersonne == null) {
            Toast.makeText(this, getString(R.string.erreur_personne), Toast.LENGTH_SHORT).show();
            return;
        }
        Gson gson = new Gson();
        Personne personne = gson.fromJson(strPersonne, Personne.class);

        double poids = 0;
        try { poids = Double.parseDouble(personne.getPoids()); } catch (NumberFormatException e) { /* 0 */ }
        if (poids <= 0) {
            Toast.makeText(this, getString(R.string.erreur_poids), Toast.LENGTH_SHORT).show();
            return;
        }

        // K : coefficient de diffusion (0.7 homme, 0.8 femme)
        double absorb;
        if (personne.isSexe()) {
            absorb = 0.8;
        } else {
            absorb = 0.7;
        }

        double contribution = boisson.getVolume() * boisson.getDegre() * 0.8 / (absorb * poids);
        double tauxActuel = Double.parseDouble(prefs.getString("taux_actuel", "0.0"));
        long dernierTimestamp = prefs.getLong("timestamp_maj", System.currentTimeMillis());
        long maintenant = System.currentTimeMillis();
        double heuresEcoulees = (maintenant - dernierTimestamp) / 3600000.0;
        tauxActuel = Math.max(0, tauxActuel - heuresEcoulees * 0.15);
        tauxActuel += contribution; 

        // Sauvegarder
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("taux_actuel", String.valueOf(tauxActuel));
        ed.putLong("timestamp_maj", maintenant);
        ed.apply();

        saveBoisson();
        afficherTauxActuel();
        Toast.makeText(this,
                String.format(getString(R.string.boisson_ajoutee), contribution),
                Toast.LENGTH_SHORT).show();
    }

    private void afficherTauxActuel() {
        SharedPreferences prefs = getSharedPreferences("ethylotest_prefs", MODE_PRIVATE);
        double taux = Double.parseDouble(prefs.getString("taux_actuel", "0.0"));
        long dernierTimestamp = prefs.getLong("timestamp_maj", System.currentTimeMillis());
        double heuresEcoulees = (System.currentTimeMillis() - dernierTimestamp) / 3600000.0;
        taux = Math.max(0, taux - heuresEcoulees * 0.15);
        tvTauxActuel.setText(String.format(getString(R.string.taux_actuel), taux));
    }
}
