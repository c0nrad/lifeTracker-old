/** DatabaseEntryGrouper
 *  Author: Stuart Larsen
 *  
 *  	Manages a group of database entries.
 */

package my.sandbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseEntryGrouper {
	private List<DatabaseEntry> mEntries;
	private Map<String, String> mIdToName;
	
	public DatabaseEntryGrouper(List<DatabaseEntry> entries, Map<String, String> idToName) {
		this.mEntries = entries;
		this.mIdToName = idToName;
	}
	
	/* Ex: Date		Weight		Pushups		Got Laid */
	public String getTitleString() {
		String out = "Date";
		for (String key : mIdToName.keySet()) {
			out += "\t\t" + mIdToName.get(key);
		}
		return out;
	}
	
	public ArrayList<String> getDisplayData() {
		String curData = "";
		String curDate = null;
		ArrayList<String> out = new ArrayList<String>();
		
		for (DatabaseEntry e : mEntries) {
			if (curDate == null) {
				curData += "\t\t" + e.mData;
				curDate = e.mDate;
				continue;
			} else if (curDate.contentEquals(e.mDate)) {
				curData += "\t\t" + e.mData;
				continue;
			} else {
				out.add(curDate.split(",")[0] + curData);
				curDate = e.mDate;
				curData = "\t\t" + e.mData;
				continue;
			}
		}
		out.add(curDate.split(",")[0] + curData);
		return out;
	}
	
	public List<DatabaseEntry> getEntries() {
		return mEntries;
	}
}
