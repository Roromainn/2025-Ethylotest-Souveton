package rs.iut.ethylotest;

import java.io.Serializable;

public class Personne implements Serializable {
    private String poids;
    private boolean sexe;
    private boolean debutant;

    public boolean isDebutant(){
        return debutant;
    }

    public boolean isSexe(){
        return sexe;
    }

    public String getPoids(){
        return poids;
    }


    public void setDebutant(boolean debutant) {
        this.debutant = debutant;
    }

    public void setPoids(String poids) {
        this.poids = poids;
    }

    public void setSexe(boolean sexe) {
        this.sexe = sexe;
    }

    public Personne() {
        this.poids = "";
        this.sexe = false;
        this.debutant = false;
    }
}
