/** EnterDataMode
 *  Author: Stuart Larsen
 * 
 * 		In this activity the user enters his or her data into his or her form. The form name is passed in from the calling activity, and is then loaded into SXL, 
 *  using the Utilities.loadForm(layoutName, context) function. The SXL is then parsed into the android view format and displayed in the current interface.
 * 
 * 	Once all the data is entered, and the user clicks save, the data is saved into the database. The tableview activity is then loaded and the layout name
 * 	is passed. 
 */
package my.sandbox;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EnterDataMode extends Activity implements OnClickListener {
	
	LinearLayout addedItemsView;
	TextView formTitle;
	Button submit;
	SimpleXMLLayout sxl = null;
	String mLayoutName = null;
	Map<String, View> idToViewMap;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enterdata);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mLayoutName = extras.getString(Utilities.Constants.PASS_LAYOUT_NAME);
		}
		
		Utilities.setupHeaderMenu(this, mLayoutName);
	
		formTitle = (TextView) findViewById(R.id.tvFormTitle);
		formTitle.setText(mLayoutName);
		
		// Load Layout from file
		sxl = Utilities.loadForm(mLayoutName, EnterDataMode.this);
		
		// Add view tree into current layout
		addedItemsView = (LinearLayout) findViewById(R.id.llAddedInputItems);
		idToViewMap = Utilities.parseSXLIntoView(addedItemsView, sxl, EnterDataMode.this);
		
		// Set up the submit button
		submit = (Button) findViewById(R.id.bSubmit);
		submit.setOnClickListener(this);
	}

	public void onClick(View v) {
		if (v == submit) {
			// Get current time / date stamp
			String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
			
			// Get map of string id, to string value
			Map<String, String> idToValueMap = Utilities.getIDandValuePairs(idToViewMap);
			
			DatabaseManager dbManager = Utilities.getDatabaseManager();
			
			for (String key : idToValueMap.keySet())
				dbManager.addEntry(mLayoutName, currentDateTimeString, key, idToValueMap.get(key));
			
			// Load table view, pass name of form
			try {
				Class<?> ourClass = Class.forName("my.sandbox.TableViewMode");
				Intent ourIntent = new Intent(EnterDataMode.this, ourClass);
				ourIntent.putExtra(Utilities.Constants.PASS_LAYOUT_NAME, mLayoutName);
				startActivity(ourIntent);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}