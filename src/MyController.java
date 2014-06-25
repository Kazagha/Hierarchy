import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import com.sun.xml.internal.ws.wsdl.ActionBasedOperationSignature;

import net.arcanesanctuary.Configuration.Conf;

public class MyController {

	private MyView view;
	private Conf conf;
	private MyTableModel tableLHS;
	private MyTableModel tableRHS;
	
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
		conf = loadConf("hierarchy.conf");
		this.view = view;
		tableLHS = view.getLHSTableModel();
		tableRHS = view.getRHSTableModel();
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
					view.setLHSTitle(view.getLHSTextField());
				}
				break;
			case "Load RHS":
				tempText = view.getRHSTextField().split(" ");
				if(tempText.length == 2)
				{								
				tableRHS.setArray(userQuery(tempText[0], tempText[1]));
				view.setRHSTitle(view.getRHSTextField());
				}
				break;
			case "Swap Sides":
				//Get table and title information 
				String titleStringLHS = view.getLHSTitleLabel();
				String titleStringRHS = view.getRHSTitleLable();
				dataLHS = tableLHS.getArray();
				dataRHS = tableRHS.getArray();
				
				//Set table and title information
				view.setLHSTitle(titleStringRHS);
				view.setRHSTitle(titleStringLHS);
				tableLHS.setArray(dataRHS);				
				tableRHS.setArray(dataLHS);
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
				
				break;
			case "View Hierarchy":
				view.setHierarchyPanel(true);
				ArrayList<HierarchyData> hDataTest = hierarchyQuery();
				System.out.println("Hierarchy size: " + hDataTest.size());
				break;
			case "View List":
				view.setHierarchyPanel(false);
				break;
			case "Exit":
				System.exit(0);
				break;				
			default:
				break;					
			}
		}		
	}
	
	public ArrayList<RoleData> userQuery(String firstName, String lastName)
	{
		ArrayList<RoleData> array = new ArrayList<RoleData>(); 
		SQLQuery sql = new SQLQuery(conf);
		
		try {
			array = sql.queryUser(firstName, lastName);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} 
		
		return array;
	}
	
	public ArrayList<HierarchyData> hierarchyQuery()
	{
		ArrayList<HierarchyData> array = null;
		SQLQuery sql = new SQLQuery(conf);
		
		try {
			array = sql.queryHierarchy();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return array;
	}
}
