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
			if(e.getActionCommand() == "Load LHS")
			{
				tableLHS.setArray(userQuery("Chris", "Green"));
				//view.setLHSTitle("");
			} else if (e.getActionCommand() == "Load RHS")
			{
				tableRHS.setArray(userQuery("Payden", "Taylor"));
				//view.setRHSTitle("");
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
}
