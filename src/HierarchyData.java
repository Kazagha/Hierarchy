import java.util.ArrayList;
import java.util.Comparator;

public class HierarchyData {

	public String nodeName;
	public ArrayList<RoleData> permissionList;
	public ArrayList<Integer> nodeList = new ArrayList<Integer>();
	public int nodeNum;
	public int nodeSeq;
	
	HierarchyData(String nodeName, int nodeNum, int nodeSeq,
			int[] nodeArray, ArrayList<RoleData> permissionArray)
	{
		this.nodeName = nodeName;
		this.nodeNum = nodeNum;
		this.nodeSeq = nodeSeq;
		this.permissionList = permissionArray;
		
		/*
		 * Only add Values greater than 0 to the nodeList
		 * In the hierarchy a 0 value represents a null
		 */
		for(int i: nodeArray)
		{
			if(i > 0)
			{
				this.nodeList.add(i);
			} else {
				break;
			}
		}
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
	
	public int getNodeTier()
	{
		return nodeList.size();
	}
	
	public ArrayList<Integer> getNodeList()
	{
		return nodeList;
	}
	
	public ArrayList<RoleData> getPermissionList()
	{
		return permissionList;
	}
	
	public boolean contains(RoleData rd)
	{
		return permissionList.contains(rd);
	}
	
	public static class Comparators
	{
		public static Comparator<HierarchyData> TIER = new Comparator<HierarchyData>()
				{
			@Override
			public int compare(HierarchyData node1, HierarchyData node2)
			{
				return node1.getNodeTier() - node2.getNodeTier();
			}
				};
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
