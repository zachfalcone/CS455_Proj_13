package cse455.csusb.bookngo;

import android.content.Intent;
import android.content.pm.ShortcutManager;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "GoogleActivity";
    private static final String CLIENT_ID = "912196512652-97kj3a1g3l4cup5v01l9lruvjhn095be.apps.googleusercontent.com";

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;

    private TextView textName, textEmail, textCount;

    private String NAME, EMAIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        View profileView = findViewById(R.id.profile_view);

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // If device supports light status bar (>=Marshmallow)
            if (profileView != null) {
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.lightStatusColor));
                profileView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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

            textName = findViewById(R.id.text_name);
            textName.setText(NAME);

            textEmail = findViewById(R.id.text_email);
            textEmail.setText(EMAIL);

            textCount = findViewById(R.id.text_count);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference books = database.getReference("books");
            Query myBooks = books.orderByChild("userEmail").equalTo(EMAIL);
            myBooks.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        textCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_sign_out:
                mAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
                    getSystemService(ShortcutManager.class).removeAllDynamicShortcuts();
                Intent loginIntent = new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            /*case R.id.name_button:
                Intent contactIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + EMAIL));
                contactIntent.putExtra(Intent.EXTRA_SUBJECT, "[Book n' Go] " + "Sample Book" + " Inquiry");
                startActivity(Intent.createChooser(contactIntent, "Email " + NAME));
                break;*/
        }
    }
}
