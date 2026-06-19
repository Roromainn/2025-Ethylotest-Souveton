package rs.iut.ethylotest;

/** Calcule l'alcoolémie selon la formule A = V·p·0.8 / (K·m). */
public class AlcoolCalculateur {

    /** Calcule la contribution d'une boisson au taux d'alcool en g/l. */
    public static double calculerContribution(Boisson boisson, Personne personne) {
        double poids = 0;
        try {
            poids = Double.parseDouble(personne.getPoids());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        double K;
        if (personne.isSexe()) {
            K = Constantes.K_FEMME;
        } else {
            K = Constantes.K_HOMME;
        }
        return boisson.getVolume() * boisson.getDegre() * Constantes.DENSITE_ALCOOL / (K * poids);
    }

    /** Calcule le taux actuel en appliquant l'élimination depuis le timestamp stocké. */
    public static double calculerTauxActuel(double tauxStocke, long timestamp) {
        double heuresEcoulees = (System.currentTimeMillis() - timestamp) / (double) Constantes.MS_PAR_HEURE;
        return Math.max(0, tauxStocke - heuresEcoulees * Constantes.TAUX_ELIMINATION);
    }

    /** Retourne le seuil légal applicable à la personne en g/l. */
    public static double getSeuil(Personne personne) {
        return personne.isDebutant() ? Constantes.SEUIL_DEBUTANT : Constantes.SEUIL_NORMAL;
    }

    /** Retourne les heures restantes avant de pouvoir conduire (0 si déjà possible). */
    public static double heuresAvantConduite(double taux, Personne personne) {
        double seuil = getSeuil(personne);
        double heures = 0;
        if (taux > seuil) {
            heures = (taux - seuil) / Constantes.TAUX_ELIMINATION;
        }
        return heures;
    }
}
