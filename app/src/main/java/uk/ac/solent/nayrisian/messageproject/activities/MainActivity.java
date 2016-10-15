package uk.ac.solent.nayrisian.messageproject.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import uk.ac.solent.nayrisian.messageproject.R;
import uk.ac.solent.nayrisian.messageproject.encryption.MD5;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        String hash1 = MD5.hash("Hello");
        Toast.makeText(this, hash1, Toast.LENGTH_LONG).show();
        String hash2 = MD5.hash("Hello");
        Toast.makeText(this, hash2, Toast.LENGTH_LONG).show();
    }
}