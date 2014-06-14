import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.security.auth.login.LoginException;

import net.arcanesanctuary.Configuration.Conf;

public class MyController {

	private MyView view;
	private Conf conf;
	
	public MyController(MyView view)
	{
		this.view = view;
		setActions();
		conf = loadConf("hierarchy.conf");
		testQuery();
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
	
	public void testQuery()
	{
		ArrayList<RoleData> array = new ArrayList<RoleData>(); 
		SQLQuery sql = new SQLQuery(conf);
		
		try {
			array = sql.queryUser("chris", "green");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} 
		
		//data = sql.getPermissions();
		for(RoleData data : array)
		{
			System.out.println(data.getRole());
		}		
	}
}
