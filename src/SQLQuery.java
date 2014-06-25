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
	
    /**
     * Constructor <br> 
     * Create new SQLQuery using the specified settings
     * @param settingsConf - The specified Configuration file
     */
	public SQLQuery(Conf settingsConf)
	{
		this.conf = settingsConf;
	}
	
	/**
	 * Create the connection to the database
	 * @throws SQLException
	 */
	private void createConnection() throws SQLException 
	{
		conn = DriverManager.getConnection(conf.get("url"), conf.get("Username"), conf.get("Password"));
        System.out.println("Connection Established...");
	}
	
	/**
	 * Query User <br>
	 * Query the database for the specified users permissions
	 * @param firstName - Specified user's first name
	 * @param lastName  - Specified user's last name
	 * @return - Array of permissions
	 * @throws SQLException
	 */
	public ArrayList<RoleData> queryUser(String firstName, String lastName) throws SQLException
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
        	throw new SQLException("Failed to execute Query: " + e.getMessage(), e);
        } finally {
            conn.close();
            rs.close();
        }
        
        return tempArray;
	}
	
	public ArrayList<HierarchyData> queryHierarchy() throws SQLException
	{
		ArrayList<HierarchyData> tempArray = new ArrayList<HierarchyData>();
		ResultSet rs = null;
		createConnection();
		
		try {
			PreparedStatement ps = conn.prepareStatement(
							"SELECT "
						+ 		"node.nod_dsc, node.nod_num, node.seq_num, "
						+ 		"rsp_nd1, rsp_nd2, rsp_nd3, rsp_nd4, rsp_nd5, "
						+ 		"rsp_nd6, rsp_nd7, rsp_nd8, rsp_nd9, rsp_n10, "
						+ 		"rsp_n11, rsp_n12, rsp_n13, rsp_n14, rsp_n15, "
						+ 		"rsp_n16, rsp_n17, rsp_n18, rsp_n19 "
						+ 	"FROM "
						+		"auhinode AS node "
						+	"WHERE "
						+		"node.hcy_num = 7 "
						+	"AND "
						+		"node.end_dte IS NULL "
						+ 	"AND "
						+		"node.rsp_nd1 = 75452"
					);
			
			rs = ps.executeQuery();
			
			while (rs.next())
			{
				//Fetch the permission array list
				ArrayList<RoleData> tempRoleArray = new ArrayList<RoleData>();
				//Put the hierarchy nodes into an array
				int nodeArray[] = {	
						rs.getInt(4),  rs.getInt(5),  rs.getInt(6),  rs.getInt(7),  rs.getInt(8),
						rs.getInt(9),  rs.getInt(10), rs.getInt(11), rs.getInt(12), rs.getInt(13), 
						rs.getInt(14), rs.getInt(15), rs.getInt(16), rs.getInt(17), rs.getInt(18),
						rs.getInt(19), rs.getInt(20), rs.getInt(21)
						};
				//Create a new HierarchyData node in the array list
				tempArray.add(new HierarchyData(rs.getString(1), rs.getInt(2), rs.getInt(3),
						nodeArray, tempRoleArray));
			}
			
		} catch (SQLException e) {
			throw new SQLException("Failed to execute Query: " + e.getMessage(), e);
		} finally {
			conn.close();
			rs.close();
		}
		
		return tempArray;
	}
}