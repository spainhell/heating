package eu.spanhel.heating;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CToast {
    public static void Info(AppCompatActivity activity, String message, int duration) {
        Context context = activity.getApplicationContext();

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();

    }
}
