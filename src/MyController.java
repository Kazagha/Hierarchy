import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import net.arcanesanctuary.Configuration.Conf;

public class MyController {

	private MyView view;
	private Conf conf;
	private SQLQuery sql;
	private JTable tableLHS;
	private JTable tableRHS;
	private MyTableModel modelRHS;
	private MyTableModel modelLHS;
	private JTree treeRHS;
	private JCheckBoxMenuItem manualEntryCheckBox;
	private JFileChooser fc;
	private enum DataType {HIERARCHY, ROLE};
	
	JTable jTableLHS;
	
	static enum Actions {
		LOADLHS ("loadLHS"),
		LOADRHS ("LoadRHS"),
		SWAPSIDES ("SwapSides");
		
		String commandString;
		
		Actions(String commandString)
		{
			this.commandString = commandString;
		}
		
		public String getCommandString()
		{
			return this.commandString;
		}
		
		public Actions getENUM(String s)
		{
			return LOADLHS;
		}
	}
	
	public MyController(MyView view)
	{
		// Set core MVC elements:
		// Create the View and set Actions
		this.view = view;
		this.view.setControllerActions(new MyActionListener());
		// Create the Model
		this.tableLHS = this.view.getTableLHS();
		this.modelLHS = ((MyTableModel) this.tableLHS.getModel());
		this.tableRHS = this.view.getTableRHS();
		this.modelRHS = ((MyTableModel) this.tableRHS.getModel()); 

		// Load connection settings, pass to SQL Query
		this.conf = loadConf("Shiv.conf");
		this.sql = new SQLQuery(conf);
		
		// Create the tree
		this.treeRHS = view.getJTree();
		
		// Tool tip manager for the JTree
		ToolTipManager.sharedInstance().registerComponent(treeRHS);
		
		// Create Listeners
		jTableLHS = view.getTableLHS();
		jTableLHS.getSelectionModel().addListSelectionListener(new RowListener());
		manualEntryCheckBox = view.getManualEntryCheckBox();
		manualEntryCheckBox.addItemListener(new MyItemListener());
				
		// Setup the File Chooser
		fc = new JFileChooser();
		fc.setFileSelectionMode(fc.FILES_ONLY);
		fc.setFileFilter(new CSVFilter());
	}
	
	public Conf loadConf(String fileName)
	{
		Conf tempConf = new Conf(new File(fileName));
		
		tempConf.add(new String[] {"Server", "Instance", "Database", "Domain", "Username", "Password"});
		//tempConf.prompt();		
		//tempConf.set("url",  "jdbc:jtds:sqlserver://" + tempConf.get("Server")+ ";instance="+ tempConf.get("Instance") + ";DatabaseName=" + tempConf.get("Database") + ";Domain=" + tempConf.get("Domain"));
		
		tempConf.setHiddenPrompt(new String[] {"Password"});
		
		return tempConf;
	}
	
	public void saveConf()
	{
		conf.nullValues(new String[] {"Password"});
		conf.del(new String[] {"url"});
		conf.save();		
	}
	
	public void fetchSQLData()
	{
		try {
			// Remove existing nodes
			this.treeRHS.removeAll();
			
			// Add nodes, expand the root node and then hide it.
			this.createHierarchyNodes((DefaultMutableTreeNode) treeRHS.getModel().getRoot());		
			this.treeRHS.expandRow(0);
			this.treeRHS.setRootVisible(false);
			this.treeRHS.setShowsRootHandles(true);
			
			// Create User Name array for the auto-complete function
			view.getTextFieldListener().setUserNameArray(userNameQuery());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(view, "Refreshing credentials has failed", "SQL Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void saveCSV(ArrayList array)
	{
		String tempString = new String();
		
		if(array.isEmpty()) { return; }

		// Array of RoleData
		if(array.get(0) instanceof RoleData)
		{
			// Transfer RoleData objects into Strings in tempString
			for(Object obj : array)
			{
				RoleData rd = (RoleData) obj;
				tempString += String.format("%s, %s%n", rd.getRole(), rd.getDescription());
			}
		// Array of Hierarchy Data
		} else if (array.get(0) instanceof HierarchyData)
		{
			// Transfer HierarchyData objects into Strings in tempString
			for(Object obj : array)
			{
				HierarchyData hd = (HierarchyData) obj; 
				tempString += String.format("%s, %s%n", hd.getNodeNumber(), hd.getNodeName());
				// TODO: To be useful this should include node path and only save active rolls 
			}
		// Invalid Array
		} else {
			return;
		}
		
		int returnVal = fc.showSaveDialog(view);
		
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			// Find the file the user selected
			File fileSelection = fc.getSelectedFile();
			
			writeStringToFile(fileSelection, tempString);			
		} else {
			// Save Cancelled by User
		}
	}
	
	public void writeStringToFile(File file, String saveString)
	{
		try(FileOutputStream fos = new FileOutputStream(file)) {
			byte[] outputBytes = saveString.getBytes();
			fos.write(outputBytes);
		} catch (FileNotFoundException e) {
			String err = String.format("File not found: %n%s", file.getPath());					
			JOptionPane.showMessageDialog(view, err, "Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e)
		{
			String err = String.format("File not found: %n%s", e.getMessage());		
			JOptionPane.showMessageDialog(view, err, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public class MyActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			String[] tempText = null;
			ArrayList<RoleData> dataLHS = null;
			ArrayList<RoleData> dataRHS = null;
			switch(e.getActionCommand())
			{
			case "Load LHS":
				tempText = view.getLHSTextToString().split(" ");
				if(tempText.length == 2)
				{
					modelLHS.setArray(userQuery(tempText[0], tempText[1]));
					view.setLHSViewTitle(view.getLHSTextToString());
				}
				//TODO: Create Table Listener
				setActiveRoles();
				break;
			case "Load RHS":
				tempText = view.getRHSTextToString().split(" ");
				if(tempText.length == 2)
				{								
					modelRHS.setArray(userQuery(tempText[0], tempText[1]));
				view.setRHSViewTitle(view.getRHSTextToString());
				}
				break;
			case "Swap Sides":
				//Get table and title information 
				String titleStringLHS = view.getLHSViewTitle();
				String titleStringRHS = view.getRHSViewTitle();
				dataLHS = modelLHS.getArray();
				dataRHS = modelRHS.getArray();
				
				//Set table and title information
				view.setLHSViewTitle(titleStringRHS);
				view.setRHSViewTitle(titleStringLHS);
				modelLHS.setArray(dataRHS);				
				modelRHS.setArray(dataLHS);
				
				//TODO: Create a table listener
				setActiveRoles();
				break;
			case "Compare":
				dataLHS = modelLHS.getArray();
				dataRHS = modelRHS.getArray();
				ArrayList<RoleData> tempArray = new ArrayList<RoleData>();
				
				for(RoleData data: dataLHS)
				{
					if(dataRHS.contains(data))
					{
						tempArray.add(data);
					}
				}
				
				modelLHS.removeArray(tempArray);
				modelRHS.removeArray(tempArray);
				
				//TODO: Create Table Listener
				setActiveRoles();
				break;
			case "View Hierarchy":
				view.setHierarchyPanel(true);				
				break;
			case "View List":
				view.setHierarchyPanel(false);
				break;
			case "Set Active Roles":
				setActiveRoles();				
				break;
			case "Remove Selected":
				removeSelectedRows(jTableLHS);
				break;
			case "Insert Roles":
				((MyTableModel) jTableLHS.getModel()).addExtraRows(5);
				break;
			case "Clear Array":
				modelLHS.clearArray();
				modelRHS.clearArray();
				break;
			case "Export Role":
				dataLHS = modelLHS.getArray();
				saveCSV(dataLHS);				
				break;
			case "Export Hierarchy":
				DefaultMutableTreeNode t = ((DefaultMutableTreeNode) treeRHS.getModel().getRoot());
				break;
			case "Source":				
				// Remove existing 'URL' variable
				conf.del(new String[] {"url"});
				
				// Prompt the user for the variables
				conf.promptJOptionPane("Set Credentials", 
						new String[] {"Server", "Instance", "Database", "Domain", "Username", "Password"});
				
				// Set the 'URL' variable
				conf.set("url",  "jdbc:jtds:sqlserver://" + conf.get("Server")+ ";instance="+ conf.get("Instance") + ";DatabaseName=" + conf.get("Database") + ";Domain=" + conf.get("Domain"));
				
				// Using the credentials, fetch data from the specified SQL Server
				fetchSQLData();				
				break;
			case "Exit":
				// Save the current Conf
				saveConf();
				// Exit the program
				System.exit(0);
				break;				
			default:
				break;					
			}
		}		
	}
	
	private void createHierarchyNodes(DefaultMutableTreeNode rootNode) throws Exception
	{
		ArrayList<HierarchyData> hierarchyList = hierarchyQuery();
		// Sort the Hierarchy Data
		Collections.sort(hierarchyList, HierarchyData.Comparators.TIER_SEQ);
		this.view.setRHSHierarchyTitle("Hierarchy View");
		
		// Iterate though the Hierarchy List Array
		for(HierarchyData hd : hierarchyList)
		{
			// Set the first parent Node. Start searching from the root node.
			DefaultMutableTreeNode parentNode = rootNode;
			
			// Iterate though the tiers of the Hierarchy 
			for(int nodeNumber : hd.getNodeList())
			{
				// Find the number of children on the 'parentNode' 
				int childCount = parentNode.getChildCount();
				// Iterate through the children until a match is found
				for(int childIndex = 0; childIndex < childCount; childIndex++)
				{
					// Fetch the node at this child index.
					DefaultMutableTreeNode tempChild = (DefaultMutableTreeNode) parentNode.getChildAt(childIndex);
					// Fetch the Hierarchy Data from this node
					HierarchyData tempHierarchyNode = (HierarchyData) tempChild.getUserObject();
					// Fetch the node number from the Hierarchy Node
					int tempChildNodeNumber = tempHierarchyNode.getNodeNumber();					
					
					// Check if the node number (from hierarchy node list)
					// for this tier matches this child node
					if(nodeNumber == tempChildNodeNumber)
					{
						// Set the new parent node to this child node
						parentNode = (DefaultMutableTreeNode) parentNode.getChildAt(childIndex);
						break;
					}
				}
			}
			// Add the Hierarchy Data as a child to the specified parent node
			parentNode.add(new DefaultMutableTreeNode(hd));
		}
	}
	
	private ArrayList<RoleData> userQuery(String firstName, String lastName)
	{
		ArrayList<RoleData> array = new ArrayList<RoleData>(); 
		
		try {
			array = sql.queryUserRoles(firstName, lastName);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(view, "Fetching user permissions has failed", "SQL Query Error", JOptionPane.ERROR_MESSAGE);			
		} 
		
		return array;
	}
	
	private ArrayList<HierarchyData> hierarchyQuery() throws Exception
	{
		ArrayList<HierarchyData> array = null;
		
		array = sql.queryHierarchy();
		
		return array;
	}
	
	private ArrayList<String> userNameQuery() throws Exception
	{
		ArrayList<String> array = null;
		
		array = sql.queryUserNames();		
		
		Collections.sort(array);
		
		return array;
	}
	
	private void setActiveRoles()
	{
		MyTreeRenderer tr = (MyTreeRenderer) treeRHS.getCellRenderer();
		tr.setActiveRoles(modelLHS.getArray());
		treeRHS.repaint();
	}
	
	private void setSelectedRoles(ArrayList<RoleData> rdArray)
	{
		MyTreeRenderer tr = (MyTreeRenderer) treeRHS.getCellRenderer();
		tr.setSelectedRoleData(rdArray);
		treeRHS.repaint();
	}
	
	public void removeSelectedRows(JTable selectedJTable)
	{
		ArrayList<RoleData> tempArray = new ArrayList<RoleData>();
		int[] rows = selectedJTable.getSelectedRows();
		
		// Convert view index to model index, then find the role
		for(int i : rows)
		{
			int rowModel = selectedJTable.convertRowIndexToModel(i);
			RoleData tempRole = ((MyTableModel) selectedJTable.getModel()).getRoleAt(rowModel);
			tempArray.add(tempRole);
		}

		// Clear the 'selection' of the rows
		selectedJTable.clearSelection();
		// Remove the selected rows from the table
		((MyTableModel) selectedJTable.getModel()).removeArray(tempArray);
	}
	
    private class RowListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {  	
        	// If the selection is still adjusting return
        	if (event.getValueIsAdjusting()) {
                return;
            }
        	
            // Create a temporary Role Data array
            ArrayList<RoleData> tempArray = new ArrayList<RoleData>();
           
            // Iterate through all selected rows
            for(int i : jTableLHS.getSelectedRows())
            {
            	// Convert the user selection in the view, to the underlying model
            	int modelIndex = jTableLHS.convertRowIndexToModel(i);
            	RoleData tempRD = modelLHS.getArray().get(modelIndex);
            	// Add specified roles to the array
            	tempArray.add(tempRD);
            }
            
            setSelectedRoles(tempArray);
        }
    }
    
    private class MyItemListener implements ItemListener
    {
		@Override
		public void itemStateChanged(ItemEvent e) {
			// Find the source that triggered the event
	    	Object source = e.getItemSelectable(); 
	    			
	    	// Check if the source was the 'Manual Entry' checkbox
			if (source == manualEntryCheckBox)
			{
				if (e.getStateChange() == ItemEvent.DESELECTED)
				{				
					view.setManualEntry(false);
					modelLHS.setEditMode(false);
					//tableRHS.setEditMode(false);
				} else {
					view.setManualEntry(true);
					modelLHS.setEditMode(true);
					//tableRHS.setEditMode(true);
				}
			}
		}    	
    }
}
