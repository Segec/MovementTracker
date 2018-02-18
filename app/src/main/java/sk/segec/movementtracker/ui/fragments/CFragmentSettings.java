package sk.segec.movementtracker.ui.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.ScrollView;

import com.larswerkman.holocolorpicker.ColorPicker;

import butterknife.BindView;
import sk.segec.movementtracker.R;
import sk.segec.movementtracker.ui.enums.ELoggingType;
import sk.segec.movementtracker.ui.services.CServiceGetLocation;
import sk.segec.movementtracker.utils.CConstant;
import sk.segec.movementtracker.utils.CUtils;

/**
 * Created by Michal on 17. 2. 2018.
 */
public class CFragmentSettings extends AFragmentDefault
{
    public static final String TAG = CFragmentSettings.class.getSimpleName();

    @BindView (R.id.fragment_settings_number_picker)
    NumberPicker mNumberPicker;

    @BindView (R.id.fragment_settings_scrollview)
    ScrollView mScrollView;

    @BindView (R.id.fragment_settings_view_line)
    View mLineView;

    @BindView (R.id.fragment_settings_edit_width)
    EditText mEditWidth;

    @BindView (R.id.fragment_settings_color_picker)
    ColorPicker mColorPicker;

    @BindView (R.id.fragment_settings_button_minutes)
    RadioButton mButtonMinutes;

    @BindView (R.id.fragment_settings_button_seconds)
    RadioButton mButtonSeconds;
    private ELoggingType mLoggingType;

    @Override
    int getFragmentLayout ()
    {
        return R.layout.fragment_settings;
    }

    private SharedPreferences mSharedPrefs;

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mCallBackActivity.setToolbarTitle(getString(R.string.toolbar_title_settings));

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mColorPicker.setColor(mSharedPrefs.getInt(CConstant.KEY_LINE_COLOR, R.color.colorBlack));
        mEditWidth.setText(String.valueOf(mSharedPrefs.getInt(CConstant.KEY_LINE_WIDTH, 1)));
        //set default width of line

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Integer.valueOf(mEditWidth.getText().toString()));
        mLineView.setLayoutParams(params);

        mLineView.setBackgroundColor(mColorPicker.getColor());

        mLoggingType = ELoggingType.valueOf(mSharedPrefs.getString(CConstant.KEY_LOG_INTERVAL_TYPE, ELoggingType.MINUTES.name()));
        if (mLoggingType.equals(ELoggingType.MINUTES))
        {
            mButtonMinutes.setChecked(true);
            mNumberPicker.setValue(mSharedPrefs.getInt(CConstant.KEY_LOG_INTERVAL, 1));
        }
        else
        {
            mButtonSeconds.setChecked(true);
            mNumberPicker.setValue(mSharedPrefs.getInt(CConstant.KEY_LOG_INTERVAL, 59));
        }

        mNumberPicker.setMinValue(1);
        mNumberPicker.setMaxValue(59);
        mNumberPicker.setWrapSelectorWheel(true);
        setHasOptionsMenu(true);

        mEditWidth.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged (CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged (CharSequence charSequence, int i, int i1, int i2)
            {
                try
                {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Integer.valueOf(charSequence.toString()));
                    mLineView.setLayoutParams(params);
                }
                catch (Exception e)
                {
                    Log.d(TAG, "Cannot parse value");
                }
            }

            @Override
            public void afterTextChanged (Editable editable)
            {

            }
        });

        mColorPicker.setOldCenterColor(mColorPicker.getColor());

        mColorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener()
        {
            @Override
            public void onColorChanged (int color)
            {
                mLineView.setBackgroundColor(color);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_settings_save:
            {
                mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = mSharedPrefs.edit();
                editor.putInt(CConstant.KEY_LOG_INTERVAL, mNumberPicker.getValue());

                //I realized i do not need this
                if (mButtonMinutes.isChecked())
                {
                    mLoggingType = ELoggingType.MINUTES;
                    editor.putString(CConstant.KEY_LOG_INTERVAL_TYPE, ELoggingType.MINUTES.name());
                }
                else
                {
                    mLoggingType = ELoggingType.SECONDS;
                    editor.putString(CConstant.KEY_LOG_INTERVAL_TYPE, ELoggingType.SECONDS.name());
                }
                editor.putInt(CConstant.KEY_LINE_WIDTH, mEditWidth.getText().toString().isEmpty() ? 1 : Integer.valueOf(mEditWidth.getText().toString()));
                editor.putInt(CConstant.KEY_LINE_COLOR, mColorPicker.getColor());
                editor.commit();

                //Service is running, so restart it
                if (CUtils.isServiceRunning(CServiceGetLocation.class, getContext()))
                {
                    //First stop service
                    getActivity().stopService(new Intent(getActivity(), CServiceGetLocation.class));
                    //Now restart it
                    CServiceGetLocation.startService(getActivity(), mLoggingType, mNumberPicker.getValue());
                }

                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.dialog_settings_save_title))
                        .setMessage(getString(R.string.dialog_settings_save_message))
                        .setPositiveButton(getString(R.string.dialog_button_ok), null)
                        .show();

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
