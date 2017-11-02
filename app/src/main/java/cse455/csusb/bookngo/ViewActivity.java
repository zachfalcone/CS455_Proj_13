package cse455.csusb.bookngo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewActivity extends AppCompatActivity {

    private static final String TAG = "GoogleActivity";
    private static final String CLIENT_ID = "912196512652-97kj3a1g3l4cup5v01l9lruvjhn095be.apps.googleusercontent.com";

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference book;

    private boolean bFavorite = false;
    private MenuItem mFavorite, mEmail, mDelete;

    private String bookID;

    private String EMAIL, TITLE, USER;

    private TextView textTitle, textISBN, textCondition, textPrice, textSchool, textCourse, textDescription;
    private ImageView imageBook;

    private boolean ownsBook = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        View viewView = findViewById(R.id.view_view);

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // If device supports light status bar (>=Marshmallow)
            if (viewView != null) {
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.lightStatusColor));
                viewView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // If device supports custom status bar color (>=Lollipop)
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.darkStatusColor));
        }

        mAuth = FirebaseAuth.getInstance();

        if (getIntent().getStringExtra("bookID") == null || mAuth.getCurrentUser() == null) {
            finish();
        } else {
            bookID = getIntent().getStringExtra("bookID");
            //Toast.makeText(getApplicationContext(), bookID, Toast.LENGTH_SHORT).show();

            database = FirebaseDatabase.getInstance();
            book = database.getReference("books").child(bookID);

            textTitle = findViewById(R.id.view_title);
            textISBN = findViewById(R.id.view_isbn);
            textCondition = findViewById(R.id.view_condition);
            textPrice = findViewById(R.id.view_price);
            textSchool = findViewById(R.id.view_school);
            textCourse = findViewById(R.id.view_course);
            textDescription = findViewById(R.id.view_description);
            imageBook = findViewById(R.id.view_image);

            book.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Textbook currentBook = dataSnapshot.getValue(Textbook.class);
                        EMAIL = currentBook.getUserEmail();
                        TITLE = currentBook.getTitle();
                        USER = currentBook.getUserName();

                        textTitle.setText(currentBook.getTitle());
                        textISBN.setText(currentBook.getIsbn());
                        textCondition.setText(currentBook.getCondition());
                        textPrice.setText(currentBook.getPrice());
                        textSchool.setText(currentBook.getSchool());
                        textCourse.setText(currentBook.getCourse());
                        textDescription.setText("Sold by " + currentBook.getUserName() + ". " + currentBook.getDescription());

                        if (currentBook.isHasImage()) {
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageReference = storage.getReference().child("textbooks/" + dataSnapshot.getKey() + ".jpg");
                            storageReference.getBytes(10 * 1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    imageBook.setImageBitmap(image);
                                    findViewById(R.id.view_imageCard).setVisibility(View.VISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }

                        if (EMAIL.equals(mAuth.getCurrentUser().getEmail())) {
                            ownsBook = true;
                        }

                        if (dataSnapshot.child("favorites").hasChild(mAuth.getCurrentUser().getEmail().replace(".", ""))) {
                            bFavorite = true;
                        }
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
        getMenuInflater().inflate(R.menu.menu_view, menu);
        mFavorite = menu.findItem(R.id.favorite);
        mEmail = menu.findItem(R.id.email);
        mDelete = menu.findItem(R.id.delete);
        if (ownsBook) {
            mEmail.setVisible(false);
            mDelete.setVisible(true);
        }
        if (bFavorite) {
            mFavorite.setIcon(getDrawable(R.drawable.ic_star));
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorite:
                bFavorite = !bFavorite;

                mFavorite.setIcon(getDrawable(bFavorite ? R.drawable.anim_star : R.drawable.anim_unstar));
                Drawable favoriteIcon = mFavorite.getIcon();
                if (favoriteIcon instanceof Animatable) {
                    ((Animatable) favoriteIcon).start();
                }

                if (bFavorite)
                    book.child("favorites").child(mAuth.getCurrentUser().getEmail().replace(".", "")).setValue(mAuth.getCurrentUser().getEmail());
                else
                    book.child("favorites").child(EMAIL.replace(".", "")).removeValue();

                break;
            case R.id.email:
                Intent contactIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + EMAIL));
                contactIntent.putExtra(Intent.EXTRA_SUBJECT, "[Book n' Go] " + TITLE + " Inquiry");
                startActivity(Intent.createChooser(contactIntent, "Email " + USER));
                break;
            case R.id.delete:
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference().child("textbooks/" + bookID + ".jpg");

                storageReference.delete();
                book.removeValue();

                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
