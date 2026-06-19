package rs.iut.ethylotest;

/** Constantes partagées entre les écrans. */
public class Constantes {
    /** Taux d'élimination en g/l par heure. Mettre 0.15 en production. */
    public static final double TAUX_ELIMINATION = 15.0;

    /** Densité de l'alcool pur utilisée dans la formule de Widmark. */
    public static final double DENSITE_ALCOOL = 0.8;

    /** Coefficient de Widmark pour un homme. */
    public static final double K_HOMME = 0.7;

    /** Coefficient de Widmark pour une femme. */
    public static final double K_FEMME = 0.8;

    /** Nombre de millisecondes dans une heure. */
    public static final long MS_PAR_HEURE = 3_600_000L;

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

    /** ID du canal de notification Android O+. */
    public static final String NOTIF_CHANNEL_ID = "alcoolemie_channel";

    /** Tag WorkManager pour annuler/remplacer la notification programmée. */
    public static final String NOTIF_TAG = "notification_conduite";

    /** ID de la notification affichée. */
    public static final int NOTIF_ID = 1;
}
