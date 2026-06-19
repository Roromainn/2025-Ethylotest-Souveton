package rs.iut.ethylotest;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/** Centralise l'accès aux SharedPreferences pour toutes les données de l'application. */
public class AlcoolRepository {

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    /** Crée un repository lié au contexte fourni. */
    public AlcoolRepository(Context context) {
        this.prefs = context.getSharedPreferences(Constantes.PREFS_NAME, Context.MODE_PRIVATE);
    }

    /** Charge la personne depuis les préférences, ou null si absente. */
    public Personne loadPersonne() {
        String str = prefs.getString(Constantes.PREF_PERSONNE, null);
        Personne personne = null;
        if (str != null) {
            personne = gson.fromJson(str, Personne.class);
        }
        return personne;
    }

    /** Sauvegarde la personne dans les préférences. */
    public void savePersonne(Personne personne) {
        prefs.edit().putString(Constantes.PREF_PERSONNE, gson.toJson(personne)).apply();
    }

    /** Charge la dernière boisson saisie, ou null si absente. */
    public Boisson loadBoisson() {
        String str = prefs.getString(Constantes.PREF_BOISSON, null);
        Boisson boisson = null;
        if (str != null) {
            boisson = gson.fromJson(str, Boisson.class);
        }
        return boisson;
    }

    /** Sauvegarde la boisson dans les préférences. */
    public void saveBoisson(Boisson boisson) {
        prefs.edit().putString(Constantes.PREF_BOISSON, gson.toJson(boisson)).apply();
    }

    /** Retourne le taux d'alcool stocké en g/l (0.0 si absent). */
    public double loadTaux() {
        return Double.parseDouble(prefs.getString(Constantes.PREF_TAUX, "0.0"));
    }

    /** Retourne le timestamp de la dernière mise à jour du taux. */
    public long loadTimestamp() {
        return prefs.getLong(Constantes.PREF_TIMESTAMP, System.currentTimeMillis());
    }

    /** Sauvegarde le taux et le timestamp associé. */
    public void saveTauxEtTimestamp(double taux, long timestamp) {
        prefs.edit()
                .putString(Constantes.PREF_TAUX, String.valueOf(taux))
                .putLong(Constantes.PREF_TIMESTAMP, timestamp)
                .apply();
    }
}
