
public class RoleNodeData extends RoleData {

	int nodeNum;
	public RoleNodeData(int nodeNum, int roleNum, String roleDesc) {
		super(roleNum, roleDesc);
		this.nodeNum = nodeNum;
	}
	
	public int getNodeNumber()
	{
		return nodeNum;
	}

}
