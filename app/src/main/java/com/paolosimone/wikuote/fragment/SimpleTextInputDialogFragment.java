package com.paolosimone.wikuote.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.paolosimone.wikuote.R;

/**
 * Generic input dialog fragment that ask the user to insert a simple text.
 */
public class SimpleTextInputDialogFragment extends DialogFragment {

    private static final String TITLE = "title";
    private static final String POSITIVE_MSG = "positive_msg";

    private OnInputSubmitListener listener;

    private EditText editText;

    /**
     * Build a new dialog fragment having the given title, and the given button text.
     * @param title the request to the user, appearing as title of the dialog
     * @param positive the positive button text
     * @return the new dialog fragment instance
     */
    public static SimpleTextInputDialogFragment newInstance(String title, String positive){
        SimpleTextInputDialogFragment frag = new SimpleTextInputDialogFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(POSITIVE_MSG, positive);
        frag.setArguments(args);
        return frag;
    }

    /**
     * Set a listener that respond to the event when the user submit the text.
     * @param listener the listener
     */
    public void setOnInputSubmitListener(OnInputSubmitListener listener){
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(TITLE);
        String positiveMsg= getArguments().getString(POSITIVE_MSG);

        editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setView(editText);
        builder.setPositiveButton(positiveMsg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Implemented later to avoid automatic dismiss
            }
        });
        builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    @Override
    public void onStart(){
        super.onStart();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog==null) return;

        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = editText.getText().toString();
                if (inputText.equals("")){
                    return;
                }
                if (listener!=null){
                    listener.onInputSubmit(inputText);
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * Listener that handles the event when the user submits the text.
     */
    public interface OnInputSubmitListener {
        /**
         * Handles the event when the user submits the text.
         * @param input the text inserted by the user
         */
        void onInputSubmit(String input);
    }
}
