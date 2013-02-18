/** Utilities
 *  Author: Stuart Larsen
 *  
 *  	Pretty much the backbone of the application. A bunch of functions that help are used in multiple activities. I commented
 *  	the functions that I thought may need explanation.
 */
package my.sandbox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;

public class Utilities {
	static public DatabaseManager mDatabaseManager;

	public static class Constants {
		static final String PASS_LAYOUT_NAME = "_LAYOUT";
	}

	private static final String TAG = "Utilities";

	static public void setupDatabaseManager(Context c) {
		mDatabaseManager = new DatabaseManager(c);
		mDatabaseManager.open();
	}

	static public DatabaseManager getDatabaseManager() {
		return mDatabaseManager;
	}

	static public String[] getFileNames(Context context) {
		return context.fileList();
	}

	static public void deleteFiles(Context context) {
		for (String fileName : getFileNames(context)) {
			context.deleteFile(fileName);
		}
	}

	static public SimpleXMLLayout loadForm(String layoutName, Context context) {
		SimpleXMLLayout sxl = new SimpleXMLLayout();
		FileInputStream fis = null;
		try {
			fis = context.openFileInput(layoutName);
			sxl.load(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sxl;
	}

	static public void saveForm(String name, SimpleXMLLayout sxl, Context context) {

		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(name, Context.MODE_PRIVATE);
			String data = sxl.toString();
			fos.write(data.getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// These are used to create keys for the nodes. For example each edittext with have an id=EditTextX where x is it's unique number
	static int EditTextCount = 0;
	static int SpinnerCount = 0;
	static int TimeCount = 0;
	static int CheckBoxCount = 0;

	static public void addItemToSXL(String selectedInputType, SimpleXMLLayout sxl, Map<String, String> attributes) {
		
		if (attributes == null) {
			attributes = new HashMap<String, String>();
		} 
		if (!attributes.containsKey("name")) {
			attributes.put("name", "Unknown");
		}
		
		int sxlRoot = sxl.getRoot();
		
		int horLayoutNode = sxl.addChild(sxlRoot, "LinearLayout");
		sxl.addAttribute(horLayoutNode, "layout_width", "fill_parent");
		sxl.addAttribute(horLayoutNode, "layout_height", "wrap_content");
		sxl.addAttribute(horLayoutNode, "orientation", "horizontal");
		
		int textViewNode = sxl.addChild(horLayoutNode, "TextView");
		sxl.addAttribute(textViewNode, "layout_width", "wrap_content");
		sxl.addAttribute(textViewNode, "layout_height", "wrap_content");
		sxl.addAttribute(textViewNode, "textSize", "14");
		sxl.addAttribute(textViewNode, "text", attributes.get("name"));

		if ("EditText".contentEquals(selectedInputType)) {
			int editTextNode = sxl.addChild(horLayoutNode, "EditText");
			sxl.addAttribute(editTextNode, "layout_width", "match_parent");
			sxl.addAttribute(editTextNode, "layout_height", "wrap_content");
			sxl.addAttribute(editTextNode, "id", "EditText" + EditTextCount++);
			
			for (String attr : attributes.keySet())
				sxl.addAttribute(editTextNode, attr, attributes.get(attr));
		} else if ("Spinner".contentEquals(selectedInputType)) {
			int spinnerNode = sxl.addChild(horLayoutNode, "Spinner");
			sxl.addAttribute(spinnerNode, "layout_width", "match_parent");
			sxl.addAttribute(spinnerNode, "layout_height", "wrap_content");
			sxl.addAttribute(spinnerNode, "id", "Spinner" + SpinnerCount++);

			for (String attr : attributes.keySet())
				sxl.addAttribute(spinnerNode, attr, attributes.get(attr));
		} else if ("Time".contentEquals(selectedInputType)) {
			int timeNode = sxl.addChild(horLayoutNode, "EditText");
			sxl.addAttribute(timeNode, "layout_width", "match_parent");
			sxl.addAttribute(timeNode, "layout_height", "wrap_content");
			sxl.addAttribute(timeNode, "inputType", "time");
			sxl.addAttribute(timeNode, "id", "Time" + TimeCount++);
		
			for (String attr : attributes.keySet())
				sxl.addAttribute(timeNode, attr, attributes.get(attr));
		} else if ("CheckBox".contentEquals(selectedInputType)) {
			int checkBoxNode = sxl.addChild(horLayoutNode, "CheckBox");
			sxl.addAttribute(checkBoxNode, "layout_width", "match_parent");
			sxl.addAttribute(checkBoxNode, "layout_height", "wrap_content");
			sxl.addAttribute(checkBoxNode, "id", "CheckBox" + CheckBoxCount++);
			
			for (String attr : attributes.keySet())
				sxl.addAttribute(checkBoxNode, attr, attributes.get(attr));
		} else {
			Log.e("SandboxActivity", "Unknown item type.");
			assert false;
		}
	}

	/* getIDandValuePairs:
	 * 		Given a map of id values to the android.Views, it takes those views and finds the values inside of them, and then links
	 * 		them to their id.
	 */
	static public Map<String, String> getIDandValuePairs(Map<String, View> idToViewMap) {
		Map<String, String> idToValueMap = new HashMap<String, String>();
		for (String id : idToViewMap.keySet()) {
			if (id.contains("EditText")) {
				EditText etView = (EditText) idToViewMap.get(id);
				idToValueMap.put(id, etView.getText().toString());
			} else if (id.contains("Spinner")) {
				Spinner spinnerView = (Spinner) idToViewMap.get(id);
				idToValueMap.put(id, spinnerView.getSelectedItem().toString()); // TODO Make sure spinner has an item.
			} else if (id.contains("Time")) {
				EditText etView = (EditText) idToViewMap.get(id);
				idToValueMap.put(id, etView.getText().toString());
			} else if (id.contains("CheckBox")) { 
				CheckBox cbView = (CheckBox) idToViewMap.get(id);
				if (cbView.isChecked())
					idToValueMap.put(id, "True");
				else
					idToValueMap.put(id, "False");
			}
		}

		return idToValueMap;
	}

	/* getIdToNameMap
	 * 		Given the layout name, it loads the layout, and then goes through each node finding it's id, and it's associated name. For example
	 * 		EditText1 - Weight, CheckBox0 - WasLaid.
	 */
	static public Map<String, String> getIdToNameMap(String layoutName, Context context) {
		SimpleXMLLayout sxl = Utilities.loadForm(layoutName, context);
		ArrayList<SimpleXMLLayout.Node> nodes =  sxl.getNodes();
		Map<String, String> idToName = new HashMap<String, String>();

		for (SimpleXMLLayout.Node n : nodes) {
			if (n.myData.containsKey("id") && n.myData.containsKey("name")) {
				idToName.put(n.myData.get("id"), n.myData.get("name"));
			}
		}

		return idToName;		
	}

	/* parseSXLIntoView
	 * 		Turns the SXL into LinearLayouts and such.
	 */
	static public Map<String, View> parseSXLIntoView(LinearLayout topLevelLayout, SimpleXMLLayout sxl, Context context) {
		topLevelLayout.removeAllViews();

		Map<String, View> idToViewMap = new HashMap<String, View>();
		Stack<Pair<LinearLayout, Integer>> parentLayouts = new Stack<Pair<LinearLayout, Integer>>();
		parentLayouts.push(new Pair<LinearLayout, Integer>(topLevelLayout, sxl.getRoot()));

		while (!parentLayouts.isEmpty()) {
			Pair<LinearLayout, Integer> pair = parentLayouts.pop();
			LinearLayout parentLayout = pair.first;
			int node = pair.second;

			for (int nodeId : sxl.getChildren(node)) {
				String name = sxl.getNodeName(nodeId);
				Map<String, String> attributes = sxl.getNodeAttributes(nodeId);

				if ("LinearLayout".contentEquals(name)) {
					LinearLayout llView;
					llView = new LinearLayout(context);

					// TODO: Set attributes
					for (String key : attributes.keySet()) {
						if ("layout_width".contentEquals(key))
							llView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
						if ("orientation".contentEquals(key)) {
							if ("vertical".contentEquals(attributes.get(key))) {
								llView.setOrientation(LinearLayout.VERTICAL);
							} else if ("horizontal".contentEquals(attributes.get(key))){
								llView.setOrientation(LinearLayout.HORIZONTAL);
							} else {
								Log.e(TAG, "Invalid Orientation:" + attributes.get(key));
							}
						}
					}

					parentLayout.addView(llView);
					parentLayouts.push(new Pair<LinearLayout, Integer>(llView, nodeId));
				} else if ("EditText".contentEquals(name)) {
					EditText etView = new EditText(context);

					for (String key : attributes.keySet()) {
						if ("inputType".contentEquals(key)) {
							if ("time".contentEquals(attributes.get(key))) 
								etView.setText("12:00");
							etView.setInputType(EditorInfo.TYPE_DATETIME_VARIATION_TIME);
						} else if ("hint".contentEquals(key)) {
							etView.setHint(attributes.get(key));
						}
					}

					if (attributes.containsKey("id")) {
						idToViewMap.put(attributes.get("id"), etView);
					} else {
						Log.e("Utilities", "All Edit Text should have an id tag...");
					}

					parentLayout.addView(etView);
				} else if ("TextView".contentEquals(name)) {
					TextView tvView = new TextView(context);

					for (String key: attributes.keySet()) {
						if ("text".contentEquals(key)) {
							tvView.setText(attributes.get(key));
						}
					}

					parentLayout.addView(tvView);		
				} else if ("Spinner".contentEquals(name)) {
					Spinner spinnerView = new Spinner(context);				

					// TODO Set Attributes

					if (attributes.containsKey("id")) {
						idToViewMap.put(attributes.get("id"), spinnerView);
					} else {
						Log.e("Utilities", "All spinners should have an id tag...");
					}

					parentLayout.addView(spinnerView);

				} else if ("CheckBox".contentEquals(name)) {
					CheckBox checkBoxView = new CheckBox(context);

					if (attributes.containsKey("id")) {
						idToViewMap.put(attributes.get("id"), checkBoxView);
					} else {
						Log.e("Utilities", "All check boxs should have an id tag...");
					}

					parentLayout.addView(checkBoxView);
				}
			}
		}
		return idToViewMap;
	}

	static void setupHeaderMenu(final Activity activity, final String layoutName) {

		TextView tvEnterData = null;
		TextView tvTableView = null;
		ImageButton bHome = null;


		bHome = (ImageButton) activity.findViewById(R.id.bHome);
		bHome.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				activity.finish();
				//try {
				//	Class<?> ourClass = Class.forName("my.sandbox.MainMenuMode");
				//	Intent ourIntent = new Intent(activity, ourClass);
				//	activity.startActivity(ourIntent);
				//} catch (ClassNotFoundException e) {
				//	e.printStackTrace();
				//}
			}  
		});

		tvEnterData = (TextView) activity.findViewById(R.id.tvEnterData);
		tvEnterData.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				try {
					Class<?> ourClass = Class.forName("my.sandbox.EnterDataMode");
					Intent ourIntent = new Intent(activity, ourClass);
					ourIntent.putExtra(Utilities.Constants.PASS_LAYOUT_NAME, layoutName);
					activity.startActivity(ourIntent);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}  
		});

		tvTableView = (TextView) activity.findViewById(R.id.tvTableView);
		tvTableView.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				try {
					Class<?> ourClass = Class.forName("my.sandbox.TableViewMode");
					Intent ourIntent = new Intent(activity, ourClass);
					ourIntent.putExtra(Utilities.Constants.PASS_LAYOUT_NAME, layoutName);
					activity.startActivity(ourIntent);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}  
		});
	}
}
