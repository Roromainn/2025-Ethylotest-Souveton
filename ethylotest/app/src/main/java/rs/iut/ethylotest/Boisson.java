package rs.iut.ethylotest;

import java.io.Serializable;

/** Représente une boisson alcoolisée avec son volume et son degré d'alcool. */
public class Boisson implements Serializable {
    private double volume; // en ml
    private double degre;  // en décimal (ex: 0.06 pour 6%)

    /** Crée une boisson vide (volume et degré à 0). */
    public Boisson() {
        this.volume = 0;
        this.degre = 0;
    }

    /**
     * Crée une boisson avec les paramètres donnés.
     * @param volume volume en ml
     * @param degre  degré d'alcool en fraction (ex: 0.06 pour 6%)
     */
    public Boisson(double volume, double degre) {
        this.volume = volume;
        this.degre = degre;
    }

    /** Retourne le volume de la boisson en ml. */
    public double getVolume() { return volume; }

    /** Définit le volume de la boisson en ml. */
    public void setVolume(double volume) { this.volume = volume; }

    /** Retourne le degré d'alcool en fraction (ex: 0.06 pour 6%). */
    public double getDegre() { return degre; }

    /** Définit le degré d'alcool en fraction (ex: 0.06 pour 6%). */
    public void setDegre(double degre) { this.degre = degre; }
}
