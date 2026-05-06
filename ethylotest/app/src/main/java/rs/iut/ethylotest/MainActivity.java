package rs.iut.ethylotest;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreState();
    }

    private void saveState() {
        personne.setPoids(editTextPoids.getText().toString());
        personne.setSexe(switchSexe.isChecked());
        personne.setDebutant(switchDebutant.isChecked());

        SharedPreferences prefs = getSharedPreferences("ethylotest_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("poids", personne.getPoids());
        editor.putBoolean("sexe", personne.isSexe());
        editor.putBoolean("debutant", personne.isDebutant());
        editor.apply();
    }

    private void restoreState() {
        SharedPreferences prefs = getSharedPreferences("ethylotest_prefs", MODE_PRIVATE);
        personne.setPoids(prefs.getString("poids", ""));
        personne.setSexe(prefs.getBoolean("sexe", false));
        personne.setDebutant(prefs.getBoolean("debutant", false));

        editTextPoids.setText(personne.getPoids());
        switchSexe.setChecked(personne.isSexe());
        switchDebutant.setChecked(personne.isDebutant());
    }

}