/** FormMenu
 *  Author: Stuart Larsen
 *  
 *  	FormMenu is the main launcher for the application. It sets up the environment and loads all available forms. If a form is selected from the list
 *  that form is loaded in EnterDataMode.
 *  
 *  	If the new form button is pressed, the EditMode activity is loaded.
 */
package my.sandbox;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainMenuMode extends ListActivity implements OnClickListener {

	Button bNewForm = null;
	String[] formNames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//full screen - call before setContentview..
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.mainmenu);
		
		deleteDatabase(DatabaseManager.DATABASE_NAME);
		
		// Set up database
		Utilities.setupDatabaseManager(this);

		// Delete files at startup
		Utilities.deleteFiles(MainMenuMode.this);

		// Show list of available forms
		updateArrayAdapater();

		// Set up New Form button
		bNewForm = (Button) findViewById(R.id.bNewForm);
		bNewForm.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateArrayAdapater();
	}

	private void updateArrayAdapater() {
		formNames = Utilities.getFileNames(MainMenuMode.this);

		setListAdapter(new ArrayAdapter<String>(MainMenuMode.this,
				android.R.layout.simple_list_item_1, formNames));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		String layoutName = formNames[position];
		try {
			Class<?> ourClass = Class.forName("my.sandbox.EnterDataMode");
			Intent ourIntent = new Intent(MainMenuMode.this, ourClass);
			ourIntent.putExtra(Utilities.Constants.PASS_LAYOUT_NAME, layoutName);
			startActivity(ourIntent);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void onClick(View v) {
		if (v == bNewForm) {			
			Class<EditMode> nextClass = EditMode.class;
			Intent ourIntent = new Intent(MainMenuMode.this, nextClass);
			startActivity(ourIntent);
		}
	}
}
