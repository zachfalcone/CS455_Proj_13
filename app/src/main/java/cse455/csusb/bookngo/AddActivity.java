package cse455.csusb.bookngo;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class AddActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GoogleActivity";
    private static final String CLIENT_ID = "912196512652-97kj3a1g3l4cup5v01l9lruvjhn095be.apps.googleusercontent.com";

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;

    private TextView textTitle, textISBN, textPrice, textSchool, textDescription;
    private Spinner spinCondition;

    private String NAME, EMAIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        View addView = findViewById(R.id.add_view);

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // If device supports light status bar (>=Marshmallow)
            if (addView != null) {
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.lightStatusColor));
                addView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // If device supports custom status bar color (>=Lollipop)
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.darkStatusColor));
        }

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            // Close if not signed in
            finish();
        } else {
            NAME = mAuth.getCurrentUser().getDisplayName();
            EMAIL = mAuth.getCurrentUser().getEmail();

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(CLIENT_ID)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            textTitle = findViewById(R.id.title);
            textISBN = findViewById(R.id.isbn);
            textPrice = findViewById(R.id.price);
            textSchool = findViewById(R.id.school);
            textDescription = findViewById(R.id.description);
            spinCondition = findViewById(R.id.condition);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.apply:
                if (textTitle.getText().length() < 1)
                    Toast.makeText(getApplicationContext(), "Enter title.", Toast.LENGTH_SHORT).show();
                else if (textISBN.getText().length() < 10)
                    Toast.makeText(getApplicationContext(), "Enter valid ISBN-10.", Toast.LENGTH_SHORT).show();
                else if (textPrice.getText().length() < 1)
                    Toast.makeText(getApplicationContext(), "Enter price.", Toast.LENGTH_SHORT).show();
                else if (textSchool.getText().length() < 1)
                    Toast.makeText(getApplicationContext(), "Enter school.", Toast.LENGTH_SHORT).show();
                else if (textDescription.getText().length() < 1)
                    Toast.makeText(getApplicationContext(), "Enter description.", Toast.LENGTH_SHORT).show();
                else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference books = database.getReference("books");
                    books.push().setValue(
                            new Textbook(
                                    textTitle.getText().toString(),
                                    textISBN.getText().toString(),
                                    spinCondition.getSelectedItem().toString(),
                                    textDescription.getText().toString(),
                                    (int)(Double.parseDouble(textPrice.getText().toString()) * 100),
                                    "CSUSB",
                                    "Professor",
                                    "Course",
                                    NAME,
                                    EMAIL
                            )
                    );
                    finish();
                    break;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
