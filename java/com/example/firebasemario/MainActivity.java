package com.example.firebasemario;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

public Button btnAddNote;
public Button btnConsultar;
public Button btnDelete;
public Button btnModify;

public EditText editTextText;

public CheckBox checkBox;

public RecyclerView recyclerView;

ArrayList<Nota> llistaNotes;  // llistaNotes on afegirem cada nota
    String idSeleccionado = "";
    NotaAdapter adaptador;
    DatabaseReference dbRef;

    private static final String TAG = "MainActivityNota"; // TAG pels camps que tindrá cada nota
    private static final String dbUrl = "https://fir-mario-e38b3-default-rtdb.firebaseio.com/";  // link db Firebase

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

       btnAddNote = findViewById(R.id.btnAddNote);
       btnConsultar = findViewById(R.id.btnConsultar);
       btnDelete = findViewById(R.id.btnDelete);
       btnModify = findViewById(R.id.btnModify);

       editTextText = findViewById(R.id.editTextText);

       checkBox = findViewById(R.id.checkBox);


       recyclerView = findViewById(R.id.recyclerView);

        llistaNotes = new ArrayList<>(); // new ArrayList de la llistaNotes

        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        dbRef = FirebaseDatabase.getInstance().getReference("notes");
        llistaNotes = new ArrayList<>();

        adaptador = new NotaAdapter(llistaNotes, nota -> {
            // Esto se ejecuta al tocar una nota de la lista
            idSeleccionado = nota.getId();
            editTextText.setText(nota.getTitol());
            checkBox.setChecked(nota.isImportant());
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptador);

        // btnDelete click
        btnDelete.setOnClickListener(v -> {
            if (!idSeleccionado.isEmpty()) {
                dbRef.child(idSeleccionado).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Nota eliminada", Toast.LENGTH_SHORT).show();
                            idSeleccionado = "";
                            editTextText.setText("");
                        });
            } else {
                Toast.makeText(this, "Selecciona una nota primer", Toast.LENGTH_SHORT).show();
            }
        });



        btnAddNote.setOnClickListener(v -> {
                    String titol = editTextText.getText().toString();
                    boolean esImportant = checkBox.isChecked();
                    String contingutProvisional = "Contingut de la nota.";

                    // si el titol no està buit llavors es guarda una nota a la database de Firebase
                    if (!titol.isEmpty()) {
                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("notes");
                        String id = dbRef.push().getKey(); // Genera ID único

                        Nota nuevaNota = new Nota(titol, contingutProvisional, esImportant, id);

                        dbRef.child(id).setValue(nuevaNota).addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Nota afegida!", Toast.LENGTH_SHORT).show();
                            editTextText.setText(""); // Limpiar el campo
                            checkBox.setChecked(false);
                        });
                    }
                    });

        btnConsultar.setOnClickListener(v -> {
            llegirNotas();
        });  // per consultar les notes

        // botó modificar
        btnModify.setOnClickListener(v -> {
            // s'ha seleccionat una nota
            if (idSeleccionado != null && !idSeleccionado.isEmpty()) {

                // llegir inputs del editText que
                String nuevoTitol = editTextText.getText().toString();
                boolean esImportant = checkBox.isChecked();
                String contingutActual = "Contingut mantingut o modificat"; // Aquí podrías usar otro EditText

                if (!nuevoTitol.isEmpty()) {
                    // nova nota amb ID
                    Nota notaEditada = new Nota(idSeleccionado, nuevoTitol,esImportant, contingutActual);

                    // Actualizamos en Firebase usant ID seleccionat
                    dbRef.child(idSeleccionado).setValue(notaEditada)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(MainActivity.this, "Nota actualitzada!", Toast.LENGTH_SHORT).show();

                                // 5. Opcional: Limpiar la selección después de editar
                                idSeleccionado = "";
                                editTextText.setText("");
                                checkBox.setChecked(false);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(MainActivity.this, "Error al modificar", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(this, "El títol no pot estar buit", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Si el usuario pulsa modificar sin haber tocado antes una nota de la lista
                Toast.makeText(this, "Selecciona una nota de la llista per modificar-la", Toast.LENGTH_SHORT).show();
            }
        });

        llegirNotas();

    }


    public void llegirNotas() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance(dbUrl).getReference("notes");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {  // creem el DataSnapshot
                llistaNotes.clear();

                for (DataSnapshot notaSnap : snapshot.getChildren()) {
                    // extreure valor del objecte
                    Nota n = notaSnap.getValue(Nota.class);

                    if (n != null) { // si nota no es null
                        //  Extraem id unic de Firebase i asignem el id a una nota
                        String claveDinamica = notaSnap.getKey();
                        n.setId(claveDinamica);

                        //afegim a la llista amb el seu ID
                        llistaNotes.add(n);
                    }
                }
                adaptador.notifyDataSetChanged(); // notifiquem al adaptador de que ja tenim les dades

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { // cancel.lar per si falla alguna cosa
                Log.e("Firebase", "Error: " + error.getMessage());
            }


        });


    }
}