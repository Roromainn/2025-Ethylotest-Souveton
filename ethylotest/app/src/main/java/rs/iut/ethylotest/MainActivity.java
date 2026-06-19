package rs.iut.ethylotest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/** Écran principal : saisie des informations de la personne (poids, sexe, débutant). */
public class MainActivity extends AppCompatActivity {

    private EditText editTextPoids;
    private Switch switchSexe;
    private Switch switchDebutant;
    private Personne personne;
    private AlcoolRepository repository;

    private Button btnConso;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    /** Initialise l'interface et les listeners. */
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

        repository = new AlcoolRepository(this);

        editTextPoids = findViewById(R.id.editTextNumberDecimal);
        switchSexe = findViewById(R.id.sexe);
        switchDebutant = findViewById(R.id.debutant);
        personne = new Personne();

        btnConso = findViewById(R.id.passer_conso);
        btnConso.setOnClickListener(this::onConsoClick);

        demanderPermissionNotification();

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        returnFromEditPerson(result.getData());
                    }
                }
        );
    }

    /** Sauvegarde les données de la personne. */
    @Override
    protected void onStop() {
        controlsToPersonne();
        repository.savePersonne(personne);
        super.onStop();
    }

    /** Restaure les données de la personne. */
    @Override
    protected void onStart() {
        super.onStart();
        Personne charge = repository.loadPersonne();
        if (charge != null) personne = charge;
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

    private void demanderPermissionNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 0);
            }
        }
    }

    private void onConsoClick(View v) {
        controlsToPersonne();
        repository.savePersonne(personne);
        startActivity(new Intent(this, Alcool.class));
    }

    private void returnFromEditPerson(Intent data) {
        if (data != null && data.getExtras() != null) {
            personne = (Personne) data.getExtras().getSerializable("person");
            personneToControls();
        }
    }
}
