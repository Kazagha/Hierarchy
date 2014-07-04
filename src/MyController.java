import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
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
		this.treeRHS = view.getJTree();	
		ToolTipManager.sharedInstance().registerComponent(treeRHS);
		this.createHierarchyNodes((DefaultMutableTreeNode) treeRHS.getModel().getRoot());
		this.view.setControllerActions(new MyActionListener());
		
		//saveConf();
	}
	
	public Conf loadConf(String fileName)
	{
		Conf tempConf = new Conf(new File(fileName));
		
		tempConf.set(new String[] {"Server", "Instance", "Database", "Username", "Password"});
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
	
	public void setActions(){}
	
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
				tempText = view.getLHSTextField().split(" ");
				if(tempText.length == 2)
				{
					tableLHS.setArray(userQuery(tempText[0], tempText[1]));
					view.setLHSViewTitle(view.getLHSTextField());
				}
				//TODO: Create Table Listener
				setActiveRoles();
				break;
			case "Load RHS":
				tempText = view.getRHSTextField().split(" ");
				if(tempText.length == 2)
				{								
				tableRHS.setArray(userQuery(tempText[0], tempText[1]));
				view.setRHSViewTitle(view.getRHSTextField());
				}
				break;
			case "Swap Sides":
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
				
				dataLHS.removeAll(tempArray);
				dataRHS.removeAll(tempArray);

				tableRHS.refreshArray();
				tableLHS.refreshArray();	
				
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
			//System.out.println(hd.getNodeName() + " " + hd.getNodeList() + " - " + hd.nodeSeq);
			//rootNode.add(new DefaultMutableTreeNode(hd.getNodeName()));
			DefaultMutableTreeNode parentNode = rootNode;
			
			//Iterate though the tiers of the Hierarchy 
			for(int nodeNumber : hd.getNodeList())
			{
				//Find the number of children on the 'parentNode' 
				int childCount = parentNode.getChildCount();
				//Iterate through the children until a match is found
				for(int childIndex = 0; childIndex < childCount; childIndex++)
				{
					DefaultMutableTreeNode tempChild = (DefaultMutableTreeNode) parentNode.getChildAt(childIndex);
					//Find the child's node number
					//HierarchyData tempChild = (HierarchyData) parentNode.getChildAt(childIndex);
					HierarchyData tempHierarchyNode = (HierarchyData) tempChild.getUserObject();
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
			array = sql.queryUser(firstName, lastName);
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
	
	private void hierarchySort(ArrayList<HierarchyData> array)
	{
		Collections.sort(array, HierarchyData.Comparators.TIER_PARENT_SEQ);
	}
	
	private void setActiveRoles()
	{
		MyTreeRenderer tr = (MyTreeRenderer) treeRHS.getCellRenderer();
		tr.setActiveRoles(tableLHS.getArray());
		treeRHS.repaint();
	}
}
