package cse455.csusb.bookngo;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewActivity extends AppCompatActivity {

    private boolean bFavorite = false;
    private MenuItem mFavorite, mEmail;

    private String bookID;

    private String EMAIL, TITLE, USER;

    private TextView textTitle, textISBN, textCondition, textPrice, textSchool, textCourse, textDescription;

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

        if (getIntent().getStringExtra("bookID") == null) {
            finish();
        } else {
            bookID = getIntent().getStringExtra("bookID");
            //Toast.makeText(getApplicationContext(), bookID, Toast.LENGTH_SHORT).show();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference book = database.getReference("books").child(bookID);

            textTitle = findViewById(R.id.view_title);
            textISBN = findViewById(R.id.view_isbn);
            textCondition = findViewById(R.id.view_condition);
            textPrice = findViewById(R.id.view_price);
            textSchool = findViewById(R.id.view_school);
            textCourse = findViewById(R.id.view_course);
            textDescription = findViewById(R.id.view_description);

            book.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
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
                    textDescription.setText(currentBook.getDescription());
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

                //Toast.makeText(getApplicationContext(), bFavorite ? "Starred" : "Unstarred", Toast.LENGTH_SHORT).show();
                // Add to favorites

                break;
            case R.id.email:
                Intent contactIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + EMAIL));
                contactIntent.putExtra(Intent.EXTRA_SUBJECT, "[Book n' Go] " + TITLE + " Inquiry");
                startActivity(Intent.createChooser(contactIntent, "Email " + USER));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
