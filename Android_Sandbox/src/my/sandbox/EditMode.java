/** EditMode
 *  Author: Stuart Larsen
 * 
 * 		EditMode is used to create and edit new forms. The important thing to realize here is the relation between SimpleXMLLayout, and the android views.
 *  When a user adds a new item, lets say an EditText, an item is added to the SXL (SimpleXMLLayout). Then, the function Utilities.parseSXLIntoView(view, sxl, context)
 *  is called. This function takes the new SXL, and parses it into android View form. Meaning the new item is now in some android.LinearLayout or whatever.
 *  This newly created view is then added to the current interface. This continues until the Save button is clicked.
 *  
 *  When the save button is clicked, the SXL is saved as an XML file on the android device, and a new database table is created to hold data for the new form. Then
 *  the activity finishes and returns from whatever activity it originally was on.
 */

package my.sandbox;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class EditMode extends Activity implements OnClickListener {
	
	// TODO Put these in Util.Const
	static final int DIALOG_EDIT_TEXT = 0;
	static final int DIALOG_GAMEOVER_ID = 1;

	Spinner inputType = null;
	LinearLayout addedItemsView = null;
	SimpleXMLLayout sxl = null;
	EditText layoutName = null;
	int sxlRoot;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		sxl = new SimpleXMLLayout();
		sxlRoot = sxl.setNewRoot("LinearLayout");
		sxl.addAttribute(sxlRoot, "layout_width", "fill_parent");
		sxl.addAttribute(sxlRoot, "layout_height", "wrap_content");
		sxl.addAttribute(sxlRoot, "orientation", "vertical");
		sxl.addAttribute(sxlRoot, "id", "@+id/llAddedInputItems");

		inputType = (Spinner) findViewById(R.id.sInputTypes);
		addedItemsView = (LinearLayout) findViewById(R.id.llAddedInputItems);
		layoutName = (EditText) findViewById(R.id.etFormName);

		Button addInputItem = (Button) findViewById(R.id.bAddInputType);
		Button saveForm = (Button) findViewById(R.id.bSaveForm);
		saveForm.setOnClickListener(this);
		addInputItem.setOnClickListener(this);
	}
	
	public void updateSXLIntoView() {
		Utilities.parseSXLIntoView(addedItemsView, sxl, EditMode.this);
	}

	public void onClick(View v) {

		switch(v.getId()) {
		case R.id.bAddInputType:
			String selectedInputType = inputType.getSelectedItem().toString();
			
			if ("EditText".contentEquals(selectedInputType)) {
				showDialog(DIALOG_EDIT_TEXT);
			} else if ("Spinner".contains(selectedInputType)) {
				Utilities.addItemToSXL("Spinner", sxl, null);
    			updateSXLIntoView();
			} else if ("Time".contains(selectedInputType)) {
				Utilities.addItemToSXL("Time", sxl, null);
    			updateSXLIntoView();
			} else if ("CheckBox".contains(selectedInputType)) {
				Utilities.addItemToSXL("CheckBox", sxl, null);
    			updateSXLIntoView();
			}
			break;
		case R.id.bSaveForm:
			String layName = layoutName.getText().toString();
			if (layName.length() != 0) {
				Utilities.saveForm(layoutName.getText().toString(), sxl, EditMode.this);
				Utilities.getDatabaseManager().createTable(layoutName.getText().toString());
			} else {
				// TODO Alert box
				Log.e("Save Form:", "Uhhh, there's no name for the layout...");
			}
			finish();
		}
	}
	
	protected Dialog onCreateDialog(int id) {
        switch(id) {
        case DIALOG_EDIT_TEXT:
        	final Dialog dialog = new Dialog(this);

        	dialog.setContentView(R.layout.edittextdialog);
        	dialog.setTitle("EditText Creation");
        	dialog.setCancelable(true);
        	
        	Button bCancel = (Button) dialog.findViewById(R.id.bCancel);
        	bCancel.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                	dismissDialog(DIALOG_EDIT_TEXT);
                }
            });
        	
        	Button bOkay = (Button) dialog.findViewById(R.id.bOkay);
        	bOkay.setOnClickListener(new OnClickListener() {
        		public void onClick(View v) {
        			Map<String, String> mDialogResult = new HashMap<String, String>();
        			// Save results to the bundle, and then cancel
        			mDialogResult.clear();
        			
        			EditText etName = (EditText) dialog.findViewById(R.id.etName);
        			String name = etName.getText().toString();
        			mDialogResult.put("name", name);
        			
        			EditText etDefault = (EditText) dialog.findViewById(R.id.etDefaut);
        			String defaultValue = etDefault.getText().toString();
        			mDialogResult.put("hint", defaultValue);
        			
        			Utilities.addItemToSXL("EditText", sxl, mDialogResult);
        			updateSXLIntoView();
        			
        			dismissDialog(DIALOG_EDIT_TEXT);
        		}
        	});
        	
        	return dialog;

        case DIALOG_GAMEOVER_ID:
            break;
        default:
            break;
        }
        return new Dialog(this);
    }
}