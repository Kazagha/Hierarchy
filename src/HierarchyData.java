import java.util.ArrayList;

public class HierarchyData {

	public String nodeName;
	public ArrayList<RoleData> permissionArray;
	public int nodeNum;
	HierarchyData(String nodeName, int nodeNum, ArrayList<RoleData> permissionArray)
	{
		this.nodeName = nodeName;
		this.nodeNum = nodeNum;
		this.permissionArray = permissionArray;
	}
	
	public String getNodeName()
	{
		return nodeName;
	}
	
	public String toString()
	{
		return nodeName;
	}
	
	public int getNodeNumber()
	{
		return nodeNum;
	}
	
	public boolean contains(RoleData rd)
	{
		return permissionArray.contains(rd);
	}
	
	/**
	 * Equals.
	 * Compares 'this' to the input object.
	 * @param obj Object to compare against
	 * @return boolean HierarchyData objects with the same node number return true.
	 */
	public boolean equals(Object obj)
	{
		//Test if 'this' is the same as the object
		if (this == obj)
			return true;

		//Test of the object is null, or of a different class to 'this'
		if((obj == null) || (obj.getClass() != this.getClass()))
			return false;

		//Therefore the object must be a HierarchyData class, so it's safe to cast
		HierarchyData other = (HierarchyData)obj;

		//Test if the Node Number of 'this' is equal to the Node Number of object 
		return (this.getNodeNumber() == other.getNodeNumber());
	}
}
