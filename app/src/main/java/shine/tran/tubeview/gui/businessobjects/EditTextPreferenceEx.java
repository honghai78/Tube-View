package shine.tran.tubeview.gui.businessobjects;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * Created by Administrator on 04/11/2016.
 */
public class EditTextPreferenceEx extends EditTextPreference {
    public EditTextPreferenceEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextPreferenceEx(Context context) {
        super(context);
    }

    public EditTextPreferenceEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        int test = Integer.parseInt(getText());
        if(test>1000)
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle("Radius is settings");
            alertDialog.setMessage("Radius invalid values. Max values is 1000 km.");
            alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    setText("1000");
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }
        else if(test<1)
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle("Radius is settings");
            alertDialog.setMessage("Radius invalid values. Min values is 1 km.");
            alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    setText("1");
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }
    }
}
