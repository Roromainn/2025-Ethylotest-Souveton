package rs.iut.ethylotest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Écran d'affichage de l'alcoolémie en temps réel, avec indicateur de conduite et heure de retour au seuil légal. */
public class AlcoolemieActivity extends AppCompatActivity {

    private TextView tvTaux;
    private TextView tvHeureConduite;
    private ImageView imgConduite;

    private AlcoolRepository repository;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                rafraichir();
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.postDelayed(this, Constantes.INTERVALLE_MAJ_MS);
        }
    };

    /** Initialise l'interface et le bouton retour. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcoolemie);

        repository = new AlcoolRepository(this);

        tvTaux = findViewById(R.id.tvTaux);
        tvHeureConduite = findViewById(R.id.tvHeureConduite);
        imgConduite = findViewById(R.id.imgConduite);

        findViewById(R.id.btnRetourAlcoolemie).setOnClickListener(v -> finish());
    }

    /** Lance le timer de rafraîchissement périodique de l'alcoolémie. */
    @Override
    protected void onStart() {
        super.onStart();
        handler.post(runnable);
    }

    /** Stoppe le timer pour éviter des mises à jour en arrière-plan. */
    @Override
    protected void onStop() {
        handler.removeCallbacks(runnable);
        super.onStop();
    }

    private void rafraichir() {
        double taux = AlcoolCalculateur.calculerTauxActuel(repository.loadTaux(), repository.loadTimestamp());
        Personne personne = repository.loadPersonne();
        double seuil = personne != null
                ? AlcoolCalculateur.getSeuil(personne)
                : Constantes.SEUIL_NORMAL;

        tvTaux.setText(String.format(Locale.FRANCE, getString(R.string.taux_actuel), taux));

        if (taux <= seuil) {
            imgConduite.setImageResource(R.drawable.ic_peut_conduire);
            tvHeureConduite.setText(R.string.peut_conduire);
        } else {
            imgConduite.setImageResource(R.drawable.ic_ne_peut_pas_conduire);
            double heuresRestantes = AlcoolCalculateur.heuresAvantConduite(taux, personne);
            long msConduite = System.currentTimeMillis() + (long) (heuresRestantes * Constantes.MS_PAR_HEURE);
            String heureStr = new SimpleDateFormat("HH:mm", Locale.FRANCE).format(new Date(msConduite));
            tvHeureConduite.setText(String.format(getString(R.string.heure_conduite), heureStr));
        }
    }
}
