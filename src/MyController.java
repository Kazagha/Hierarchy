import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
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
		// View + Actions
		this.view = view;
		this.view.setControllerActions(new MyActionListener());
		// Model
		this.tableLHS = this.view.getTableLHS();
		this.modelLHS = ((MyTableModel) this.tableLHS.getModel());
		this.tableRHS = this.view.getTableRHS();
		this.modelRHS = ((MyTableModel) this.tableRHS.getModel()); 

		// Load connection settings, pass to SQL Query
		this.conf = loadConf("hierarchy.conf");
		this.sql = new SQLQuery(conf);
		
		// Create the tree, add nodes, expand the root node and then hide it.
		this.treeRHS = view.getJTree();
		this.createHierarchyNodes((DefaultMutableTreeNode) treeRHS.getModel().getRoot());		
		this.treeRHS.expandRow(0);
		this.treeRHS.setRootVisible(false);
		this.treeRHS.setShowsRootHandles(true);
		
		// Tool tip manager for the JTree
		ToolTipManager.sharedInstance().registerComponent(treeRHS);
		
		// Create Listeners
		jTableLHS = view.getTableLHS();
		jTableLHS.getSelectionModel().addListSelectionListener(new RowListener());
		manualEntryCheckBox = view.getManualEntryCheckBox();
		manualEntryCheckBox.addItemListener(new MyItemListener());
		
		// Create User Name array for the auto-complete function
		view.getTextFieldListener().setUserNameArray(userNameQuery());
				
		// Setup the File Chooser
		fc = new JFileChooser();
		fc.setFileSelectionMode(fc.FILES_ONLY);
		fc.setFileFilter(new CSVFilter());
		
		//Save the Configuration (Conf) to the 'hierarchy.conf' file
		//saveConf();
	}
	
	public Conf loadConf(String fileName)
	{
		Conf tempConf = new Conf(new File(fileName));
		
		tempConf.add(new String[] {"Server", "Instance", "Database", "Username", "Password"});
		tempConf.prompt();
		tempConf.set("url",  "jdbc:jtds:sqlserver://" + tempConf.get("Server")+ ";instance="+ tempConf.get("Instance") + ";DatabaseName=" + tempConf.get("Database"));
		
		return tempConf;
	}
	
	public void saveConf()
	{
		conf.nullValues(new String[] {"Password"});
		conf.del("url");
		//conf.save();		
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
			
			saveStringToFile(fileSelection, tempString);			
		} else {
			// Save Cancelled by User
		}
	}
	
	public void saveStringToFile(File fileToSave, String saveString)
	{
		try(FileOutputStream fos = new FileOutputStream(fileToSave)) {
			byte[] outputBytes = saveString.getBytes();
			fos.write(outputBytes);
		} catch (IOException e) {
			System.out.format("I/O Exception: %n %s", e.getMessage());
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
				conf.del("url");
				
				// Prompt the user for the variables
				conf.promptJOptionPane();	
				
				// Set the 'URL' variable
				conf.set("url",  "jdbc:jtds:sqlserver://" + conf.get("Server")+ ";instance="+ conf.get("Instance") + ";DatabaseName=" + conf.get("Database"));
				break;
			case "Exit":
				System.exit(0);
				break;				
			default:
				break;					
			}
		}		
	}
	
	private void createHierarchyNodes(DefaultMutableTreeNode rootNode)
	{
		ArrayList<HierarchyData> hierarchyList = hierarchyQuery();
		hierarchySort(hierarchyList); //Sort the Hierarchy Data
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
		} catch (SQLException e) {
			System.out.format("User Permission Query Failed: %n%n%s", e.getMessage());
		} 
		
		return array;
	}
	
	private ArrayList<HierarchyData> hierarchyQuery()
	{
		ArrayList<HierarchyData> array = null;
		
		try {
			array = sql.queryHierarchy();
		} catch (SQLException e) {
			System.out.format("Hierarchy Query Failed: %n%n%s", e.getMessage());
		}
		
		return array;
	}
	
	private ArrayList<String> userNameQuery()
	{
		ArrayList<String> array = null;
		
		try {
			array = sql.queryUserNames();			
		} catch (SQLException e) {
			System.out.format("User Name Query Failed: %n%n %s", e.getMessage());
		}
		
		Collections.sort(array);
		
		return array;
	}
	
	private void hierarchySort(ArrayList<HierarchyData> array)
	{
		Collections.sort(array, HierarchyData.Comparators.TIER_SEQ);
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
    
    private class FileChooserActionListener implements ActionListener
    {
		@Override
		public void actionPerformed(ActionEvent e) {
			
			
		}    	
    }
}
