package eu.spanhel.heating;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

class CToast {
    static void Info(AppCompatActivity activity, String message, int duration) {
        Context context = activity.getApplicationContext();
        Toast.makeText(context, message, duration).show();
    }
}
