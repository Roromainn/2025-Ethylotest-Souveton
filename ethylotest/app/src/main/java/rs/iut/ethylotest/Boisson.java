package rs.iut.ethylotest;

import java.io.Serializable;

public class Boisson implements Serializable {
    private double volume; // en ml
    private double degre;  // en decimal

    public Boisson() {
        this.volume = 0;
        this.degre = 0;
    }

    public Boisson(double volume, double degre) {
        this.volume = volume;
        this.degre = degre;
    }

    public double getVolume() { return volume; }
    public void setVolume(double volume) { this.volume = volume; }

    public double getDegre() { return degre; }
    public void setDegre(double degre) { this.degre = degre; }
}
