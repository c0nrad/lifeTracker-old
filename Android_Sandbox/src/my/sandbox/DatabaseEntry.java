/** DatabaseEntry.java
 *  Author: Stuart Larsen
 * 
 * 		DatabaseEntry is the intermediate step between a database query and using the data.
 */

package my.sandbox;

public class DatabaseEntry {
	public String mDate;				/* The date time stamp of the entry */
	public String mTypeId;			 	/* The type of the input item, such as EditText, followed by it's unique number. Ex: Spinner0 */
	public String mData;				/* The data of the data type. For Example, the EditText might save the weight, so mData would be "180" */
	public int mId;						/* Primary unique key for the database, not used */
	
	DatabaseEntry(String date, String id, String data) {
		this.mDate = date;
		this.mTypeId = id;
		this.mData = data;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return mDate + " " + mTypeId + " " + mData;
	}
}