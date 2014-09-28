package com.example.spencer.brh2014;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ChangeLanguageDialogBuilder {

	private Context c;

	public ChangeLanguageDialogBuilder(Context context) {
		c = context;
	}

	public AlertDialog getAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(c);

		builder.setTitle("Select a language to learn:");

		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// Save the currently selected language here!
						dialog.dismiss();
					}
				});
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		// List of languages user can choose from
		String[] languages = { "English", "French" };

		// Set the list to display in dialog, no item is currently selected
		// Should find index of current language and check that one (replace -1
		// with this value)
		builder.setSingleChoiceItems(languages, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Save the selected language here
					}
				});

		return builder.create();
	}

}
