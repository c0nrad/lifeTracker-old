/** TableViewMode
 * 	Author: Stuart Larsen
 * 
 * 		TableView queries the database and loads all the data for a specific layout into a table.
 */
package my.sandbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TableViewMode extends ListActivity {

	DatabaseManager dbManager = null;
	String mLayoutName = null;
	TextView tvFormTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tableview);
		
		// Load Layout name
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mLayoutName = extras.getString(Utilities.Constants.PASS_LAYOUT_NAME);
		}
		
		Utilities.setupHeaderMenu(this, mLayoutName);
		
		// Set Form Title
		tvFormTitle = (TextView) findViewById(R.id.tvFormTitle);
		tvFormTitle.setText(mLayoutName);
		
		// Get database manager
		dbManager = Utilities.getDatabaseManager();
		
		// Get all data
		List<DatabaseEntry> entries = dbManager.getAllEntries(mLayoutName);
		
		// Get IdToNameMap
		Map<String, String> idToName = Utilities.getIdToNameMap(mLayoutName, this);
		
		// Set up databaseentrygrouper
		DatabaseEntryGrouper mDatabaseEntryGrouper = new DatabaseEntryGrouper(entries, idToName);
		ArrayList<String> mDisplayData = mDatabaseEntryGrouper.getDisplayData();
		
		// Set up table header
		TextView tvTableHeader = (TextView) findViewById(R.id.tvTableHeader);
		tvTableHeader.setText(mDatabaseEntryGrouper.getTitleString());	
		
		// Display all data
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mDisplayData);
		setListAdapter(adapter);
	}
}
