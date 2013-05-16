package my.sandbox;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/** SimpleXMLLayout
 * 		Used to create simple XML layouts. Can read/write/modify internal XML tree, and save/load the tree to files.
 * @author c0nrad
 * @date 5/23/12
 * 
 * Revision History:
 * 	5/23/12		SCL		Created file, set up basic structure
 * 	5/23/12 	SCL		Finished save/load functions
 * 
 * Description:
 * 		For terminology's sake:
 * 
 * 		<Tag
 * 			attribute="value" >
 * 			
 * 			<elements/>
 * 		</Tag>
 * 
 * 		Each Tag is an individual node in the tree. Each node contains the name of the tag, a map of it's attribute-value pairs, and a list of it's children, 
 * 		along with a reference to it's parent. Each node also contains an ID number. This number is unique and is used to identify the node. The id is returned
 * 		to the user on creation of a node, to access it later to add attributes or children.
 * 
 * 		The operations for the SimpleXMLLayout are pretty simple. It can be used to create new layouts (see example in SimpleXMLLayout.main), to save layouts,
 * 		to load layouts, or to modify them.
 * 
 * 		For usage in the LifeTracker application, the tags correspond directly to android view types. Some of the attributes are attributes belonging to android SDK,
 * 		while some are user defined. The ability to create custume tags for the layouts was one of the main reasons for making the SimpleXMLLayout class.
 */
public class SimpleXMLLayout {
	Node myRoot;
	ArrayList<Node> myNodes;
	int myIdCount;

	public SimpleXMLLayout() {
		myRoot = null;
		myIdCount = 1;
		myNodes = new ArrayList<Node>();
	}
	
	public void clear() {
		myRoot = null;
	}
	
	public ArrayList<Node> getNodes() {
		return myNodes;
	}

	public int setNewRoot(String name) {
		int nodeId = myIdCount++;
		Node n = new Node(null, name, nodeId);
		myRoot = n;
		myNodes.add(n);
		return nodeId;
	}

	public int addChild(int parentId, String name) {
		int nodeId = myIdCount++;
		Node parentNode = findNodeById(parentId);
		Node child = new Node(parentNode, name, nodeId);
		parentNode.myChildren.add(child);
		myNodes.add(child);

		return nodeId;
	}
	
	public int getRoot() {
		return myRoot.myId;
	}
	
	public ArrayList<Integer> getChildren(int nodeId) {
		ArrayList<Integer> out = new ArrayList<Integer>();
		Node n = findNodeById(nodeId);
		for (Node child : n.myChildren)
			out.add(child.myId);
		
		return out;
	}

	public void addAttribute(int nodeId, String attr, String value) {
		Node node = findNodeById(nodeId);
		node.myData.put(attr, value);
	}

	public Node findNodeById(int id) {
		for (Node n : myNodes) {
			if (n.myId == id)
				return n;
		}
		return null;
	}
	
	public String getNodeName(int nodeId) {
		Node n = findNodeById(nodeId);
		return n.myName;
	}
	
	public Map<String, String> getNodeAttributes(int nodeId) {
		Node n = findNodeById(nodeId);
		return n.myData;
	}

	public void printXML() {
		printXML(myRoot, 0);
	}

	public void save(String fileName) {
		BufferedWriter out = null;
		try {
			FileWriter fstream = new FileWriter(fileName);
			out = new BufferedWriter(fstream);
			out.write(toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public ArrayList<String> loadFileData(FileInputStream fis) {
		ArrayList<String> fileLines = new ArrayList<String>();

		try {
			DataInputStream in = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = br.readLine()) != null) {
				fileLines.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileLines;
	}

	public void load(FileInputStream fis) {
		ArrayList<String> fileData = loadFileData(fis);

		Stack<Node> parentStack = new Stack<Node>();
		parentStack.push(null);

		Node curNode = null;

		for (String curLine : fileData) {
			curLine = curLine.trim();

			if (curLine.length() == 0)
				continue;
			
			if (curLine.contains("</")) {
				curNode = parentStack.pop();
			}

			if (curLine.contains("<") && curLine.contains("/>")) {
				if (curLine.contains("Layout")) {
					parentStack.pop();
				}
				curNode = null;
				continue;
			}
			
			if (curLine.charAt(0) == '<') {
				curNode = new Node(parentStack.peek(), curLine.substring(1), myIdCount++);
				myNodes.add(curNode);
				
				if (parentStack.peek() == null)
					myRoot = curNode;
				else
					parentStack.peek().myChildren.add(curNode);
				
				continue;
			}
			
			if (curLine.contains("/>")) {
				curLine = curLine.substring(0, 1 + curLine.lastIndexOf('\"'));
				curLine = curLine.replace('\"', ' ');
				String[] attrValue = curLine.split("=");
				curNode.myData.put(attrValue[0].trim(), attrValue[1].trim());

				continue;
			}

			if (curLine.contains(">")) {				
				curLine = curLine.substring(0, 1 + curLine.lastIndexOf('\"'));
				curLine = curLine.replace('\"', ' ');
				String[] attrValue = curLine.split("=");
				curNode.myData.put(attrValue[0].trim(), attrValue[1].trim());

				// Is it necessarily a parent?
				if (curNode.myName.contains("Layout"))
					parentStack.push(curNode);
				continue;
			}
			
			if (curLine.contains("=")) {
				curLine = curLine.replace('\"', ' ');
				String[] attrValue = curLine.split("=");
				curNode.myData.put(attrValue[0].trim(), attrValue[1].trim());
				continue;
			}
			
				System.out.println("DAFUQ IS THIS?");
		}
	}

	@Override
	public String toString() {
		return toString(myRoot, 0);
	}

	public String toString(Node n, int depth) {
		String out = "";

		String tabs = "";
		for (int x = 0; x < depth; ++x)
			tabs += "\t";

		// Print Self
		out += "\n" + tabs + "<" + n.myName;
		if (n.myData.size() >= 1) {
			for (String attr : n.myData.keySet()) {
				out += "\n" + tabs + "\t" + attr + "=\"" + n.myData.get(attr) + '\"';
			}
		}

		// Add Children
		if (n.myChildren.size() >= 1) {
			out += " >";

			for (Node child : n.myChildren) {
				out += "\n";
				out += toString(child, depth + 1);
			}

			// Print Closure
			out += "\n" + tabs + "<" + n.myName + " />";
		} else 
			out += " />";

		return out;
	}

	public void printXML(Node n, int depth) {
		System.out.println(toString());
	}

	public class Node {

		Node(Node p, String n, int id) {
			myParent = p;
			myName = n;
			myId = id;
			myData = new HashMap<String, String>(8);
			myChildren = new ArrayList<Node>();
		}

		String myName;
		int myId;
		Map<String, String> myData;

		Node myParent;
		ArrayList<Node> myChildren;
	}

	public static void main(String[] args) {
		
		SimpleXMLLayout test = new SimpleXMLLayout();
		int root = test.setNewRoot("LinearLayout");
		test.addAttribute(root, "layout_width", "fill_parent");
		test.addAttribute(root, "layout_height", "fill_parent");
		test.addAttribute(root, "orientation", "vertical");

		int title = test.addChild(root, "TextView");
		test.addAttribute(title, "text", "Workout");
		test.addAttribute(title, "textSize", "28");

		int weight = test.addChild(root, "LinearLayout");
		test.addAttribute(weight, "layout_width", "fill_parent");
		test.addAttribute(weight, "layout_height", "fill_parent");
		test.addAttribute(weight, "orientation", "horizontal");

		int weightTextView = test.addChild(weight, "TextView");
		test.addAttribute(weightTextView, "text", "Weight");
		test.addAttribute(weightTextView, "textSize", "14");

		int weightScrollBox = test.addChild(weight, "ScrollBox");
		test.addAttribute(weightScrollBox, "defautValue" ,"130");
		test.addAttribute(weightScrollBox, "scollDelta", "5");
		test.addAttribute(weightScrollBox, "minValue", "1");
		test.addAttribute(weightScrollBox, "maxValue", "1000");
		
		test.printXML();
		test.save("simpleLayout.sxl");
		test.clear();
		//test.load("simpleLayout.sxl");
		test.printXML();
	}
}
