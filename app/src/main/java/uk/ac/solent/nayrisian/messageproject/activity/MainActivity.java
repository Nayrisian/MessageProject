package uk.ac.solent.nayrisian.messageproject.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import uk.ac.solent.nayrisian.messageproject.R;
import uk.ac.solent.nayrisian.messageproject.database.DatabaseHandler;

/**
 *
 */
public class MainActivity extends AppCompatActivity {
    DatabaseHandler db = new DatabaseHandler(this);

    // UI references
    private Button _btnDelete;

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

        _btnDelete = (Button) findViewById(R.id.btnDelete);
        _btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.delAccount(1, getBaseContext());
                Toast.makeText(getBaseContext(), "Deleted...", Toast.LENGTH_LONG).show();
            }
        });
    }
}