import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.arcanesanctuary.Configuration.Conf;
import net.sourceforge.jtds.jdbc.Driver;

import java.util.ArrayList;

public class SQLQuery {
	
	ArrayList<RoleData> permissions = new ArrayList<RoleData>();
	Conf conf;
    Connection conn = null;
    final String driver = "net.sourceforge.jtds.jdbc.Driver";
	
	public SQLQuery(File configurationFile)
	{
		conf = new Conf(configurationFile);
		conf.prompt();
	    conf.set("url",  "jdbc:jtds:sqlserver://" + conf.get("Server")+ ";instance="+ conf.get("Instance") + ";DatabaseName=" + conf.get("Database"));		
	}
	
	public SQLQuery(Conf settingsConf)
	{
		this.conf = settingsConf;
	}
	
	public void createConnection() throws SQLException 
	{
		conn = DriverManager.getConnection(conf.get("url"), conf.get("Username"), conf.get("Password"));
        System.out.println("Connection Established...");
	}
	
	public ArrayList<RoleData> query(String firstName, String lastName) throws SQLException
	{
		ArrayList<RoleData> tempArray = new ArrayList<RoleData>();
		ResultSet rs = null;
		createConnection();
    
        try {         
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
            	tempArray.add(new RoleData(Integer.valueOf(rs.getString(1)), rs.getString(2)));
            }
        } catch (SQLException e) {
        	throw new SQLException("Failed to execute Query", e);
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