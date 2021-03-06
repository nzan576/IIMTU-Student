package com.coetusstudio.iimtustudent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.coetusstudio.iimtustudent.Model.AddFaculty;
import com.coetusstudio.iimtustudent.Model.Queries;
import com.coetusstudio.iimtustudent.Model.StudentDetails;
import com.coetusstudio.iimtustudent.databinding.ActivityQueriesBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueriesActivity extends AppCompatActivity {

    ActivityQueriesBinding binding;
    DatabaseReference reference;
    DatabaseReference dbnameref, dbrollref, dbfacultyref;
    String studentName, studentRollNumber, facultyName;
    HashMap<String,String> hashMapFaculty=new HashMap<>();
    FirebaseAuth auth;
    FirebaseUser currentUser ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQueriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Queries");
        dbrollref = FirebaseDatabase.getInstance().getReference().child("IIMTU").child("Student");
        dbnameref = FirebaseDatabase.getInstance().getReference().child("IIMTU").child("Student");
        dbfacultyref = FirebaseDatabase.getInstance().getReference().child("IIMTU").child("Faculty");

        binding.btnSendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.queriesTitle.getEditText().getText().toString().isEmpty()) {
                    binding.queriesTitle.setError("Empty");
                    binding.queriesTitle.requestFocus();
                } else {
                    sendlink();
                }
            }
        });

        //Spinner for studentName
        final List<String> listStudentName=new ArrayList<String>();
        listStudentName.add("Select Your Name");

        ArrayAdapter<String> studentNameArrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listStudentName);
        studentNameArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.queriesName.setAdapter(studentNameArrayAdapter);

        binding.queriesName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                studentName=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dbnameref.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    listStudentName.add(dataSnapshot.child("studentName").getValue().toString());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Spinner for StudentRollNumber
        final List<String> listStudentRollNumber=new ArrayList<String>();
        listStudentRollNumber.add("Select Roll Number");

        ArrayAdapter<String> rollNumberArrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listStudentRollNumber);
        rollNumberArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.queriesRollNumber.setAdapter(rollNumberArrayAdapter);

        binding.queriesRollNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                studentRollNumber=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dbrollref.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    listStudentRollNumber.add(dataSnapshot.child("studentRollNumber").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Spinner for facultyName
        final List<String> listFacultyName=new ArrayList<String>();
        listFacultyName.add("Select Faculty Name");

        ArrayAdapter<String> facultyNameArrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listFacultyName);
        facultyNameArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.queriesFaculty.setAdapter(facultyNameArrayAdapter);

        binding.queriesFaculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                facultyName=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dbfacultyref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dsp :dataSnapshot.getChildren()){

                    AddFaculty br = dsp.getValue(AddFaculty.class);

                    hashMapFaculty.put(br.getFacultyName(),br.getFacultyId());

                    listFacultyName.add(br.getFacultyName());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
    private void sendlink() {


        String queriesTitle = binding.queriesTitle.getEditText().getText().toString();
        Queries queries = new Queries(studentName, studentRollNumber, queriesTitle);

        reference.push().setValue(queries).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(QueriesActivity.this, "Queries sent to the Faculty", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(QueriesActivity.this, "Please, try again later!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}