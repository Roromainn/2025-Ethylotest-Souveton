package rs.iut.ethylotest;

import java.io.Serializable;

/** Représente une personne avec ses paramètres pour le calcul d'alcoolémie. */
public class Personne implements Serializable {
    private String poids;
    private boolean sexe;
    private boolean debutant;

    /** Crée une personne avec des valeurs par défaut. */
    public Personne() {
        this.poids = "";
        this.sexe = false;
        this.debutant = false;
    }

    /** Retourne le poids en kg sous forme de chaîne. */
    public String getPoids() {
        return poids;
    }

    /** Définit le poids en kg sous forme de chaîne. */
    public void setPoids(String poids) {
        this.poids = poids;
    }

    /** Retourne true si la personne est une femme, false si homme. */
    public boolean isSexe() {
        return sexe;
    }

    /** Définit le sexe : true = femme, false = homme. */
    public void setSexe(boolean sexe) {
        this.sexe = sexe;
    }

    /** Retourne true si la personne est conducteur débutant. */
    public boolean isDebutant() {
        return debutant;
    }

    /** Définit si la personne est conducteur débutant. */
    public void setDebutant(boolean debutant) {
        this.debutant = debutant;
    }
}
