package sk.segec.movementtracker.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import sk.segec.movementtracker.R;

/**
 * Created by Michal on 18. 2. 2018.
 */
public class CViewUtils
{
    public static Snackbar showSnackBarSettings (@NonNull View view, @NonNull final Activity activity, String message)
    {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_settings_title,
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick (View v)
                            {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", activity.getPackageName(), null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                activity.startActivity(intent);
                            }
                        });
        snackbar.setDuration(6000);

        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorWhite));
        textView.setSingleLine(false);

        return snackbar;
    }

    public static void showSnackBar (View view, String message)
    {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorBlack));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorWhite));
        snackbar.show();
    }

}
