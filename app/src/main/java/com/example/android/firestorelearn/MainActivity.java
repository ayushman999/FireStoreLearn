package com.example.android.firestorelearn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG="MainActivity";
    private final String TITLE="title";
    private final String DESCRIPTION="description";
    EditText titleEdit;
    EditText descEdit;
    EditText priorityEdit;
    Button saveBtn;
    Button loadBtn;
    TextView text;
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    CollectionReference collectionReference=firebaseFirestore.collection("Books");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        descEdit=(EditText) findViewById(R.id.edit_text_description);
        titleEdit=(EditText) findViewById(R.id.edit_text_title);
        saveBtn=(Button) findViewById(R.id.button);
        loadBtn=(Button) findViewById(R.id.button2);
        text=(TextView) findViewById(R.id.textView);
        priorityEdit=(EditText) findViewById(R.id.edit_priority);
        saveBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                saveBook();
            }
        });
        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadBook();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        collectionReference.whereLessThan("priority",100)
                .orderBy("priority",Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {
                    return;
                }
                String data="";
                for(QueryDocumentSnapshot snapshot: value)
                {
                    Note note=snapshot.toObject(Note.class);
                    String title=note.getTitle();
                    String description=note.getDescription();
                    String id=snapshot.getId();
                    String priority=note.getPriority()+"";
                    data+="id:"+id+"\n"+"Title:"+title+"\n"+"Description:"+description+"\npriority:"+priority+"\n\n";
                }
                text.setText(data);
            }
        });

    }
    public void saveBook()
    {
        String title=titleEdit.getText().toString();
        String description=descEdit.getText().toString();
        int priority=Integer.parseInt(priorityEdit.getText().toString());
        Note data=new Note(title,description,priority);
        collectionReference.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(MainActivity.this,"Saved bc!",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Laude lag gye!",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void loadBook()
    {
        Task task1=collectionReference.whereLessThan("priority",10)
                .orderBy("priority")
                .get();
        Task task2=collectionReference.whereGreaterThan("priority",100)
                .orderBy("priority")
                .get();
        Task<List<QuerySnapshot>> allTasks=Tasks.whenAllSuccess(task1, task2);
        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                String data="";

                for(QuerySnapshot snapshot:querySnapshots)
                {
                    for(QueryDocumentSnapshot queryDocumentSnapshot:snapshot)
                    {
                        Note note=queryDocumentSnapshot.toObject(Note.class);
                        String title=note.getTitle();
                        String description=note.getDescription();
                        String id=queryDocumentSnapshot.getId();
                        String priority=note.getPriority()+"";
                        data+="id:"+id+"\n"+"Title:"+title+"\n"+"Description:"+description+"\npriority:"+priority+"\n\n";
                    }
                }
                text.setText(data);
            }
        });

    }

}