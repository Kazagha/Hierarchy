import java.io.File;

import net.arcanesanctuary.Configuration.Conf;

public class MyController {

	private MyView view;
	private Conf conf;
	
	public MyController(MyView view)
	{
		this.view = view;
		setActions();
		conf = loadConf("hierarchy.conf");		
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
		conf.save();		
	}
	
	public void setActions(){}	
	
	public void testQuery()
	{
		SQLQuery sql = new SQLQuery(conf);
	}
}
