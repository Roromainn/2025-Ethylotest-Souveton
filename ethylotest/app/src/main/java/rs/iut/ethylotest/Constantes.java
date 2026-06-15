package rs.iut.ethylotest;

/** Constantes partagées entre les écrans. */
public class Constantes {
    /** Taux d'élimination en g/l par heure. Mettre 0.15 en production. */
    public static final double TAUX_ELIMINATION = 15.0;

    /** Intervalle de rafraîchissement en ms. Mettre 60000 en production. */
    public static long INTERVALLE_MAJ_MS = 1000;

    /** Seuil légal conducteur normal en g/l. */
    public static double SEUIL_NORMAL = 0.5;

    /** Seuil conducteur débutant en g/l. */
    public static double SEUIL_DEBUTANT = 0.2;

    /** Clé pour l'objet Personne (JSON). */
    public static final String PREF_PERSONNE = "personne";

    /** Clé pour le taux d'alcool actuel. */
    public static final String PREF_TAUX = "taux_actuel";

    /** Clé pour le timestamp de la dernière mise à jour. */
    public static final String PREF_TIMESTAMP = "timestamp_maj";

    /** Clé pour l'objet Boisson. */
    public static final String PREF_BOISSON = "boisson";

    /** Nom du fichier SharedPreferences. */
    public static final String PREFS_NAME = "ethylotest_prefs";
}
