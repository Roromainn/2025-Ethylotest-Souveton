package rs.iut.ethylotest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    private EditText editTextPoids;
    private Switch switchSexe;
    private Switch switchDebutant;
    private Personne personne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextPoids = findViewById(R.id.editTextNumberDecimal);
        switchSexe = findViewById(R.id.sexe);
        switchDebutant = findViewById(R.id.debutant);
        personne = new Personne();
    }

    @Override
    protected void onStop() {
        controlsToPersonne();
        savePersonne();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadPersonne();
        personneToControls();
    }

    private void personneToControls() {
        editTextPoids.setText(personne.getPoids());
        switchSexe.setChecked(personne.isSexe());
        switchDebutant.setChecked(personne.isDebutant());
    }

    private void controlsToPersonne() {
        personne.setPoids(editTextPoids.getText().toString());
        personne.setSexe(switchSexe.isChecked());
        personne.setDebutant(switchDebutant.isChecked());
    }

    private void savePersonne() {
        SharedPreferences prefs = getSharedPreferences("ethylotest_prefs", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        Gson gson = new Gson();
        String str = gson.toJson(personne);
        ed.putString("personne", str);
        ed.apply();
    }

    private void loadPersonne() {
        SharedPreferences prefs = getSharedPreferences("ethylotest_prefs", MODE_PRIVATE);
        String str = prefs.getString("personne", null);
        if (str != null) {
            Gson gson = new Gson();
            personne = gson.fromJson(str, Personne.class);
        }
    }
}
