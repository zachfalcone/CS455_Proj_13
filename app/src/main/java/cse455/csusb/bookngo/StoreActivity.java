package cse455.csusb.bookngo;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class StoreActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GoogleActivity";
    private static final String CLIENT_ID = "912196512652-97kj3a1g3l4cup5v01l9lruvjhn095be.apps.googleusercontent.com";

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;

    private FloatingActionButton btnNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        View loginView = findViewById(R.id.store_view);

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // If device supports light status bar (>=Marshmallow)
            if (loginView != null) {
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.lightStatusColor));
                loginView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                // If device supports app shortcuts
                ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

                ShortcutInfo shortcut = new ShortcutInfo.Builder(this, "id1")
                        .setShortLabel("Add Book")
                        .setLongLabel("Add Book")
                        .setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_add_shortcut))
                        .setIntents(
                                new Intent[] {
                                        new Intent(Intent.ACTION_MAIN, null, this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK),
                                        new Intent(Intent.ACTION_MAIN, null, this, AddActivity.class)
                                })
                        .build();

                shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut));
            }

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(CLIENT_ID)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            btnNew = findViewById(R.id.new_button);
            btnNew.setOnClickListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_store, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                Intent profileIntent = new Intent(this, ProfileActivity.class);
                startActivity(profileIntent);
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent exitIntent = new Intent(Intent.ACTION_MAIN);
        exitIntent.addCategory(Intent.CATEGORY_HOME);
        exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(exitIntent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.new_button:
                Intent addIntent = new Intent(this, AddActivity.class);
                startActivity(addIntent);
                break;
        }
    }
}
