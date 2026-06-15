package rs.iut.ethylotest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Écran d'affichage de l'alcoolémie en temps réel, avec indicateur de conduite et heure de retour au seuil légal. */
public class AlcoolemieActivity extends AppCompatActivity {

    private TextView tvTaux;
    private TextView tvHeureConduite;
    private ImageView imgConduite;

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

        tvTaux         = findViewById(R.id.tvTaux);
        tvHeureConduite = findViewById(R.id.tvHeureConduite);
        imgConduite    = findViewById(R.id.imgConduite);

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
        SharedPreferences prefs = getSharedPreferences(Constantes.PREFS_NAME, MODE_PRIVATE);

        double taux = Double.parseDouble(prefs.getString(Constantes.PREF_TAUX, "0.0"));
        long timestamp = prefs.getLong(Constantes.PREF_TIMESTAMP, System.currentTimeMillis());
        double heuresEcoulees = (System.currentTimeMillis() - timestamp) / 3600000.0;
        taux = Math.max(0, taux - heuresEcoulees * Constantes.TAUX_ELIMINATION);

        boolean debutant = false;
        String strPersonne = prefs.getString(Constantes.PREF_PERSONNE, null);
        if (strPersonne != null) {
            Personne personne = new Gson().fromJson(strPersonne, Personne.class);
            debutant = personne.isDebutant();
        }
        double seuil = debutant ? Constantes.SEUIL_DEBUTANT : Constantes.SEUIL_NORMAL;

        tvTaux.setText(String.format(Locale.FRANCE, getString(R.string.taux_actuel), taux));

        if (taux <= seuil) {
            imgConduite.setImageResource(R.drawable.ic_peut_conduire);
            tvHeureConduite.setText(R.string.peut_conduire);
        } else {
            imgConduite.setImageResource(R.drawable.ic_ne_peut_pas_conduire);
            double heuresRestantes = (taux - seuil) / Constantes.TAUX_ELIMINATION;
            long msConduite = System.currentTimeMillis() + (long) (heuresRestantes * 3600000);
            String heureStr = new SimpleDateFormat("HH:mm", Locale.FRANCE).format(new Date(msConduite));
            tvHeureConduite.setText(String.format(getString(R.string.heure_conduite), heureStr));
        }
    }
}
