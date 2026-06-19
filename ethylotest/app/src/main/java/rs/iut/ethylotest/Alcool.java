package rs.iut.ethylotest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
    private TextView tvTauxActuel;

    private Boisson boisson;
    private AlcoolRepository repository;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                afficherTauxActuel();
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.postDelayed(this, Constantes.INTERVALLE_MAJ_MS);
        }
    };

    /** Initialise l'interface, le spinner de boissons et les listeners. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcool);

        repository = new AlcoolRepository(this);

        spinnerBoisson = findViewById(R.id.spinnerBoisson);
        editVolume = findViewById(R.id.editVolume);
        editDegre = findViewById(R.id.editDegre);
        tvTauxActuel = findViewById(R.id.tvTauxActuel);

        boisson = new Boisson();
        setupSpinner();

        findViewById(R.id.btnConsommer).setOnClickListener(this::onConsommer);
        findViewById(R.id.btnRetour).setOnClickListener(v -> finish());
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

    /** Stoppe le timer et sauvegarde la boisson courante. */
    @Override
    protected void onStop() {
        handler.removeCallbacks(runnable);
        controlsToBoisson();
        repository.saveBoisson(boisson);
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

    private void loadBoisson() {
        Boisson charge = repository.loadBoisson();
        if (charge != null) {
            boisson = charge;
            spinnerBoisson.setSelection(IDX_CUSTOM);
            editVolume.setText(String.valueOf((int) boisson.getVolume()));
            editDegre.setText(String.valueOf((int) (boisson.getDegre() * 100)));
        }
    }

    private void onConsommer(View v) {
        controlsToBoisson();
        String erreur = null;
        Personne personne = null;
        double poids = 0;

        if (boisson.getVolume() <= 0 || boisson.getDegre() <= 0) {
            erreur = getString(R.string.erreur_saisie);
        } else {
            personne = repository.loadPersonne();
            if (personne == null) {
                erreur = getString(R.string.erreur_personne);
            } else {
                try {
                    poids = Double.parseDouble(personne.getPoids());
                } catch (NumberFormatException ignored) {}
                if (poids <= 0) {
                    erreur = getString(R.string.erreur_poids);
                }
            }
        }

        if (erreur != null) {
            Toast.makeText(this, erreur, Toast.LENGTH_SHORT).show();
        } else {
            double contribution = AlcoolCalculateur.calculerContribution(boisson, personne);
            double tauxActuel = AlcoolCalculateur.calculerTauxActuel(repository.loadTaux(), repository.loadTimestamp());
            tauxActuel += contribution;

            repository.saveTauxEtTimestamp(tauxActuel, System.currentTimeMillis());
            repository.saveBoisson(boisson);

            planifierNotification(tauxActuel, AlcoolCalculateur.getSeuil(personne));

            Toast.makeText(this,
                    String.format(getString(R.string.boisson_ajoutee), contribution),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void afficherTauxActuel() {
        double taux = AlcoolCalculateur.calculerTauxActuel(repository.loadTaux(), repository.loadTimestamp());
        tvTauxActuel.setText(String.format(getString(R.string.taux_actuel), taux));
    }

    private void planifierNotification(double taux, double seuil) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, Constantes.NOTIF_ID, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);

        if (taux <= seuil) {
            return;
        }

        double heuresRestantes = (taux - seuil) / Constantes.TAUX_ELIMINATION;
        long triggerTime = System.currentTimeMillis() + (long) (heuresRestantes * Constantes.MS_PAR_HEURE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }
}
