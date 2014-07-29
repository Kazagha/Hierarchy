import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import com.sun.xml.internal.ws.wsdl.ActionBasedOperationSignature;

import net.arcanesanctuary.Configuration.Conf;

public class MyController {

	private MyView view;
	private Conf conf;
	private SQLQuery sql;
	private MyTableModel tableLHS;
	private MyTableModel tableRHS;
	private JTree treeRHS;
	private JCheckBoxMenuItem manualEntryCheckBox;
	
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
		this.conf = loadConf("hierarchy.conf");
		this.sql = new SQLQuery(conf);
		this.view = view;
		this.tableLHS = view.getLHSTableModel();
		this.tableRHS = view.getRHSTableModel();		
		this.view.setControllerActions(new MyActionListener());
		
		//Create the tree, add nodes, expand the root node and then hide it.
		this.treeRHS = view.getJTree();
		this.createHierarchyNodes((DefaultMutableTreeNode) treeRHS.getModel().getRoot());		
		this.treeRHS.expandRow(0);
		this.treeRHS.setRootVisible(false);
		this.treeRHS.setShowsRootHandles(true);
		
		//Tool tip manager for the JTree
		ToolTipManager.sharedInstance().registerComponent(treeRHS);
		
		//Create Listeners
		jTableLHS = view.getTableLHS();
		jTableLHS.getSelectionModel().addListSelectionListener(new RowListener());
		manualEntryCheckBox = view.getManualEntryCheckBox();
		manualEntryCheckBox.addItemListener(new MyItemListener());
		
		//Create User Name array for the auto-complete function
		view.getTextFieldListener().setUserNameArray(userNameQuery());
		
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
					tableLHS.setArray(userQuery(tempText[0], tempText[1]));
					view.setLHSViewTitle(view.getLHSTextToString());
				}
				//TODO: Create Table Listener
				setActiveRoles();
				break;
			case "Load RHS":
				tempText = view.getRHSTextToString().split(" ");
				if(tempText.length == 2)
				{								
				tableRHS.setArray(userQuery(tempText[0], tempText[1]));
				view.setRHSViewTitle(view.getRHSTextToString());
				}
				break;
			case "Swap Sides":
				//TODO: Swap Sides with an empty array causes an error
				//Get table and title information 
				String titleStringLHS = view.getLHSViewTitle();
				String titleStringRHS = view.getRHSViewTitle();
				dataLHS = tableLHS.getArray();
				dataRHS = tableRHS.getArray();
				
				//Set table and title information
				view.setLHSViewTitle(titleStringRHS);
				view.setRHSViewTitle(titleStringLHS);
				tableLHS.setArray(dataRHS);				
				tableRHS.setArray(dataLHS);
				
				//TODO: Create a table listener
				setActiveRoles();
				break;
			case "Compare":
				dataLHS = tableLHS.getArray();
				dataRHS = tableRHS.getArray();
				ArrayList<RoleData> tempArray = new ArrayList<RoleData>();
				
				for(RoleData data: dataLHS)
				{
					if(dataRHS.contains(data))
					{
						tempArray.add(data);
					}
				}
				
				tableLHS.removeArray(tempArray);
				tableRHS.removeArray(tempArray);
				
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
				removeSelected(jTableLHS.getSelectedRows());
				break;
			case "Clear Array":
				tableLHS.clearArray();
				tableRHS.clearArray();
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
		
		//Iterate though the Hierarchy List Array
		for(HierarchyData hd : hierarchyList)
		{
			//Set the first parent Node. Start searching from the root node.
			DefaultMutableTreeNode parentNode = rootNode;
			
			//Iterate though the tiers of the Hierarchy 
			for(int nodeNumber : hd.getNodeList())
			{
				//Find the number of children on the 'parentNode' 
				int childCount = parentNode.getChildCount();
				//Iterate through the children until a match is found
				for(int childIndex = 0; childIndex < childCount; childIndex++)
				{
					//Fetch the node at this child index.
					DefaultMutableTreeNode tempChild = (DefaultMutableTreeNode) parentNode.getChildAt(childIndex);
					//Fetch the Hierarchy Data from this node
					HierarchyData tempHierarchyNode = (HierarchyData) tempChild.getUserObject();
					//Fetch the node number from the Hierarchy Node
					int tempChildNodeNumber = tempHierarchyNode.getNodeNumber();					
					
					//Check if the node number (from hierarchy node list)
					//for this tier matches this child node
					if(nodeNumber == tempChildNodeNumber)
					{
						//Set the new parent node to this child node
						parentNode = (DefaultMutableTreeNode) parentNode.getChildAt(childIndex);
						break;
					}
				}
			}
			//Add the Hierarchy Data as a child to the specified parent node
			parentNode.add(new DefaultMutableTreeNode(hd));
		}
	}
	
	private ArrayList<RoleData> userQuery(String firstName, String lastName)
	{
		ArrayList<RoleData> array = new ArrayList<RoleData>(); 
		
		try {
			array = sql.queryUserRoles(firstName, lastName);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} 
		
		return array;
	}
	
	private ArrayList<HierarchyData> hierarchyQuery()
	{
		ArrayList<HierarchyData> array = null;
		
		try {
			array = sql.queryHierarchy();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
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
		tr.setActiveRoles(tableLHS.getArray());
		treeRHS.repaint();
	}
	
	private void setSelectedRoles(ArrayList<RoleData> rdArray)
	{
		MyTreeRenderer tr = (MyTreeRenderer) treeRHS.getCellRenderer();
		tr.setSelectedRoleData(rdArray);
		treeRHS.repaint();
	}
	
	public void removeSelected(int[] rows)
	{
		for(int i : rows)
		{
			System.out.println(i);
			//((MyTableModel) jTableLHS.getModel()).removeRow(i);
		}
	}
	
    private class RowListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {  	
        	//If the selection is still adjusting return
        	if (event.getValueIsAdjusting()) {
                return;
            }
        	
            //Create a tempory Role Data array
            ArrayList<RoleData> tempArray = new ArrayList<RoleData>();
           
            //Iterate through all selected rows
            for(int i : jTableLHS.getSelectedRows())
            {
            	//Convert the user selection in the view, to the underlying model
            	int modelIndex = jTableLHS.convertRowIndexToModel(i);
            	RoleData tempRD = tableLHS.getArray().get(modelIndex);
            	//Add specified roles to the array
            	tempArray.add(tempRD);
            }
            
            setSelectedRoles(tempArray);
        }
    }
    
    private class MyItemListener implements ItemListener
    {
		@Override
		public void itemStateChanged(ItemEvent e) {
	    	Object source = e.getItemSelectable(); 
	    			
			if (source == manualEntryCheckBox)
			{
				System.out.println("Source Found");

				if (e.getStateChange() == ItemEvent.DESELECTED)
				{				
					tableLHS.setEditMode(false);
					tableRHS.setEditMode(false);
					System.out.println("Checkbox has been deselected");
				} else {
					tableLHS.setEditMode(true);
					tableRHS.setEditMode(true);
				}
			}
			

		}
    	
    }
}
