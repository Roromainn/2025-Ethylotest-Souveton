package rs.iut.ethylotest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Alcool extends AppCompatActivity {

    private Personne personne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcool);

        // Récupération de l'objet Personne passé par l'intent
        personne = (Personne) getIntent().getSerializableExtra("person");

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {

            Intent resultIntent = new Intent();
            resultIntent.putExtra("person", personne);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
