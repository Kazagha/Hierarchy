import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.arcanesanctuary.Configuration.Conf;
import net.sourceforge.jtds.jdbc.Driver;

import java.util.ArrayList;
import java.util.Collection;

public class SQLQuery {
	
	ArrayList<RoleData> permissions;
	Conf conf;
    String driver = "net.sourceforge.jtds.jdbc.Driver";
	
	public SQLQuery(File configurationFile)
	{
		conf = new Conf(configurationFile);
		conf.prompt();
	    conf.set("url",  "jdbc:jtds:sqlserver://" + conf.get("Server")+ ";instance="+ conf.get("Instance") + ";DatabaseName=" + conf.get("Database"));		
	}
	
	public SQLQuery(Conf settingsConf)
	{
		this.conf = settingsConf;
		conf.set("url",  "jdbc:jtds:sqlserver://" + conf.get("Server")+ ";instance="+ conf.get("Instance") + ";DatabaseName=" + conf.get("Database"));
	}
	
	private void createConnection()
	{
		
	}
	
	public ArrayList<RoleData> query(String firstName, String lastName) throws SQLException
	{
		ArrayList<RoleData> tempArray = new ArrayList<RoleData>();

	    Connection conn = null;
	    ResultSet rs = null;
    
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(conf.get("url"), conf.get("Username"), conf.get("Password"));
            System.out.println("Connection Established...");
            
            PreparedStatement ps = conn.prepareStatement(
            		
	            	"SELECT auhinamr.rol_num, auhirole.rol_dsc "
	            +	"FROM auhinamr "
	            +	"JOIN auhirole ON auhinamr.rol_num = auhirole.rol_num "
	            +	"JOIN aunrmast ON auhinamr.nar_num = aunrmast.nar_num "
	            + 	"AND aunrmast.nam_gv1 = ? AND aunrmast.nam_fam = ? "
	            );

            ps.setString(1, firstName);
            ps.setString(2, lastName);

            rs = ps.executeQuery();
          
            while (rs.next()) 
            {
            	permissions.add(new RoleData(Integer.valueOf(rs.getString(1)), rs.getString(2)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
            rs.close();
        }
        
        return tempArray;
	}
	
	public ArrayList<RoleData> getPermissions()
	{
		return permissions;
	}
}