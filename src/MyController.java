import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import net.arcanesanctuary.Configuration.Conf;

public class MyController {

	private MyView view;
	private Conf conf;
	private MyTableModel tableLHS;
	private MyTableModel tableRHS;
	
	public MyController(MyView view)
	{
		this.view = view;
		view.setLHSTitle("User X");
		tableLHS = view.getLHSTableModel();
		tableRHS = view.getRHSTableModel();
		
		setActions();
		conf = loadConf("hierarchy.conf");
		testUserQuery(tableLHS, "Chris", "Green");
		saveConf();
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
	
	public void testUserQuery(MyTableModel tempModel, String firstName, String lastName)
	{
		ArrayList<RoleData> array = new ArrayList<RoleData>(); 
		SQLQuery sql = new SQLQuery(conf);
		
		try {
			array = sql.queryUser(firstName, lastName);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} 
		
		for(RoleData data : array)
		{
			//System.out.println(data.getRole());
			tempModel.tableAddRow(data.getRole(), data.getDescription());
		}		
	}
}
