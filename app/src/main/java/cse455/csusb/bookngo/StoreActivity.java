package cse455.csusb.bookngo;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;

public class StoreActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GoogleActivity";
    private static final String CLIENT_ID = "912196512652-97kj3a1g3l4cup5v01l9lruvjhn095be.apps.googleusercontent.com";

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;

    private FloatingActionButton btnNew;
    private MenuItem mProfile, mSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        View storeView = findViewById(R.id.store_view);

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // If device supports light status bar (>=Marshmallow)
            if (storeView != null) {
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.lightStatusColor));
                storeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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

            // Sample data
            ArrayList<Textbook> textbooks = new ArrayList<>();
            textbooks.add(new Textbook("Math", "0000000000001", "Excellent", "Description...", 2000, "CSUSB", "Professor", "MATH 101"));
            textbooks.add(new Textbook("English", "0000000000002", "Good", "Description...", 1500, "CSUSB", "Professor", "ENG 101"));
            textbooks.add(new Textbook("Biology", "0000000000003", "Acceptable", "Description...", 1000, "CSUSB", "Professor", "BIOL 101"));
            textbooks.add(new Textbook("C++", "0000000000004", "Good", "Description...", 1500, "CSUSB", "Professor", "CSE 101"));
            textbooks.add(new Textbook("Physics", "0000000000005", "Good", "Description...", 2000, "CSUSB", "Professor", "PHYS 101"));

            RecyclerView recyclerTextbooks = findViewById(R.id.recycler_textbooks);
            recyclerTextbooks.setLayoutManager(new LinearLayoutManager(this));

            TextbookAdapter textbookAdapter = new TextbookAdapter(textbooks);
            textbookAdapter.setOnItemClickListener(new TextbookAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent viewIntent = new Intent(getApplicationContext(), ViewActivity.class);
                    startActivity(viewIntent);
                }
            });

            recyclerTextbooks.setAdapter(textbookAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_store, menu);
        mProfile = menu.findItem(R.id.profile);
        mSearch = menu.findItem(R.id.search);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                Intent profileIntent = new Intent(this, ProfileActivity.class);
                startActivity(profileIntent);
                break;
            case R.id.search:
                mProfile.setIcon(getDrawable(R.drawable.anim_close));
                Drawable profileIcon = mProfile.getIcon();
                if (profileIcon instanceof Animatable) {
                    ((Animatable) profileIcon).start();
                }

                mProfile.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        stopSearch();
                        return true;
                    }
                });

                mSearch.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Search();
                        return true;
                    }
                });

                getSupportActionBar().setDisplayShowCustomEnabled(true);
                getSupportActionBar().setCustomView(R.layout.activity_search);

                EditText search = findViewById(R.id.search_box);
                InputMethodManager keyboard = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                search.requestFocus();
                keyboard.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);

                search.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {
                        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                            Search();
                            return true;
                        } else if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_BACK) {
                            stopSearch();
                            return true;
                        }
                        return false;
                    }
                });

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void Search() {
        EditText search = findViewById(R.id.search_box);
        String book = search.getText().toString();
        if (!book.isEmpty())
            findBook(book);
        else
            Toast.makeText(this, "No book entered.", Toast.LENGTH_SHORT).show();
        stopSearch();
    }

    public void stopSearch() {
        EditText search = findViewById(R.id.search_box);
        search.clearFocus();
        getSupportActionBar().setDisplayShowCustomEnabled(false);
        mProfile.setIcon(getDrawable(R.drawable.anim_profile));
        Drawable profileIcon = mProfile.getIcon();
        if (profileIcon instanceof Animatable) {
            ((Animatable) profileIcon).start();
        }
        mProfile.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return false;
            }
        });
        mSearch.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return false;
            }
        });
    }

    public void findBook(String text) {
        Toast.makeText(getApplicationContext(), "Search for " + text, Toast.LENGTH_SHORT).show();
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
