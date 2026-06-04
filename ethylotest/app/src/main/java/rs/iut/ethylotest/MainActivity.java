package rs.iut.ethylotest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

    private Button btnConso;
    private ActivityResultLauncher<Intent> activityResultLauncher;

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

        btnConso = findViewById(R.id.passer_conso);
        btnConso.setOnClickListener(this::onConsoClick);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        returnFromEditPerson(result.getData());
                    }
                }
        );
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
        if (personne != null) {
            editTextPoids.setText(personne.getPoids());
            switchSexe.setChecked(personne.isSexe());
            switchDebutant.setChecked(personne.isDebutant());
        }
    }

    private void controlsToPersonne() {
        if (personne == null) personne = new Personne();
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

    private void onConsoClick(View v) {
        // On met à jour l'objet personne avec les saisies actuelles avant de l'envoyer
        controlsToPersonne();
        
        Intent intent = new Intent(this, Alcool.class);
        intent.putExtra("person", personne);
        activityResultLauncher.launch(intent);
    }

    /**
     * Appelée au retour de Alcool avec RESULT_OK.
     */
    private void returnFromEditPerson(Intent data) {
        if (data != null && data.getExtras() != null) {
            personne = (Personne) data.getExtras().getSerializable("person");
            personneToControls();
        }
    }
}
