package cse455.csusb.bookngo;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

public class AddActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GoogleActivity";
    private static final String CLIENT_ID = "912196512652-97kj3a1g3l4cup5v01l9lruvjhn095be.apps.googleusercontent.com";
    private static final int IMAGE_REQUEST = 1;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;

    private EditText textTitle, textISBN, textPrice, textSchool, textDescription;
    private Spinner spinCondition;
    private ImageView imagePhoto, clearPhoto;
    private TextView textCondition;

    private String NAME, EMAIL;

    private Bitmap image;
    private int imagePadding;

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
            //textSchool = findViewById(R.id.school);
            textDescription = findViewById(R.id.description);
            spinCondition = findViewById(R.id.condition);
            textCondition = findViewById(R.id.text_condition);
            imagePhoto = findViewById(R.id.photo);
            imagePadding = imagePhoto.getPaddingStart();
            clearPhoto = findViewById(R.id.clear_photo);

            imagePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent getImageIntent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
                    Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).setType("image/*");
                    Intent chooseImageIntent = Intent.createChooser(getIntent(), "Select a photo of the book.");
                    chooseImageIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickImageIntent});
                    startActivityForResult(chooseImageIntent, IMAGE_REQUEST);
                }
            });

            clearPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (image != null) {
                        image.recycle();
                        image = null;
                    }
                    imagePhoto.setImageDrawable(getDrawable(R.drawable.ic_add_photo));
                    imagePhoto.setPadding(imagePadding,imagePadding,imagePadding,imagePadding);
                    imagePhoto.setAdjustViewBounds(false);
                    imagePhoto.setEnabled(true);
                    clearPhoto.setVisibility(View.GONE);
                }
            });

            textCondition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    spinCondition.performClick();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Toast.makeText(getApplicationContext(), "working", Toast.LENGTH_SHORT).show();
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri chosenImage = data.getData();
            try {
                InputStream imageStream = getContentResolver().openInputStream(chosenImage);
                image = BitmapFactory.decodeStream(imageStream);
                imagePhoto.setImageBitmap(image);
                imagePhoto.setPadding(0,0,0,0);
                imagePhoto.setAdjustViewBounds(true);
                imagePhoto.setEnabled(false);
                clearPhoto.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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
                else if (textISBN.getText().length() < 13)
                    Toast.makeText(getApplicationContext(), "Enter valid ISBN-13.", Toast.LENGTH_SHORT).show();
                else if (textPrice.getText().length() < 1)
                    Toast.makeText(getApplicationContext(), "Enter price.", Toast.LENGTH_SHORT).show();
                else if ((Double.parseDouble(textPrice.getText().toString())) >= 1000.0)
                    Toast.makeText(getApplicationContext(), "Please lower price to less than $1000.00.", Toast.LENGTH_SHORT).show();
                /*else if (textSchool.getText().length() < 1)
                    Toast.makeText(getApplicationContext(), "Enter school.", Toast.LENGTH_SHORT).show();*/
                else if (textDescription.getText().length() < 1)
                    Toast.makeText(getApplicationContext(), "Enter description.", Toast.LENGTH_SHORT).show();
                else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference books = database.getReference("books");
                    String bookKey = books.push().getKey();
                    final Textbook textbook = new Textbook(
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
                    );
                    if (image != null) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageReference = storage.getReference().child("textbooks/" + bookKey + ".jpg");

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        int width = image.getWidth(), height = image.getHeight();
                        if (width > height && width > 2560) {
                            double scale = width/2560f;
                            Bitmap smallerImage = Bitmap.createScaledBitmap(image, (int)(width/scale), (int)(height/scale), true);
                            smallerImage.compress(Bitmap.CompressFormat.JPEG, 85, baos);

                        } else if (height >= width && height > 2560) {
                            double scale = height/2560f;
                            Bitmap smallerImage = Bitmap.createScaledBitmap(image, (int)(width/scale), (int)(height/scale), true);
                            smallerImage.compress(Bitmap.CompressFormat.JPEG, 85, baos);
                        } else {
                            image.compress(Bitmap.CompressFormat.JPEG, 85, baos);
                        }
                        byte[] data = baos.toByteArray();

                        storageReference.putBytes(data);
                        textbook.setHasImage(true);
                    }
                    books.child(bookKey).setValue(textbook);
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
