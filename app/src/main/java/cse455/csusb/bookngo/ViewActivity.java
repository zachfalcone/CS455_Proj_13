package cse455.csusb.bookngo;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class ViewActivity extends AppCompatActivity {

    private boolean bFavorite = false;
    private MenuItem mFavorite;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        mFavorite = menu.findItem(R.id.favorite);
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
        }
        return super.onOptionsItemSelected(item);
    }
}
