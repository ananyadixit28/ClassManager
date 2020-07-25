package com.android.wefour.classmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

/*Dialog which comes in when a student clicks the menu to join a class..
this dialog takes the key of the course as input and joins the user if
 that course is present*/
public class JoinClassDialogStudent extends AppCompatDialogFragment {
    private EditText editTextExampleDialog;
    private ExampleDialogListener listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.layout_join_class_dialog,null);
        builder.setView(view)
                //negative button
                .setTitle("Enter Key").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
            //positive button
        }).setPositiveButton("Ok",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String key=editTextExampleDialog.getText().toString();
                listener.applyTexts(key);
            }
        });
        editTextExampleDialog=view.findViewById(R.id.editText);
        return builder.create();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener=(ExampleDialogListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+"must implement Example Dialog Listener");
        }
    }
// interface
    public interface ExampleDialogListener{
        void applyTexts(String key);
    }
}
