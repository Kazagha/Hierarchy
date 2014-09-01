import javax.swing.tree.DefaultMutableTreeNode;

public class HierarchyTreeNode extends DefaultMutableTreeNode {

	public static enum Mode { INACTIVE, PARTIAL_ACTIVE, PARTIAL_SELECTED, ACTIVE, SELECTED };
	private static final int INACTIVE = 0;
	private static final int PARTIAL_ACTIVE = 1;
	private static final int PARTIAL_SELECTED = 2;
	private static final int ACTIVE = 3;
	private static final int SELECTED = 4;	
	
	Mode mode;
	HierarchyData value;
	
	public HierarchyTreeNode(HierarchyData hd)
	{
		mode = Mode.INACTIVE;
		this.userObject = hd;
	}
	
	public void setMode(Mode newMode)
	{
		//if(newMode == Mode.INACTIVE) {}
		
		this.mode = newMode;
	}
	
	public void setInactive()
	{
		this.mode = Mode.INACTIVE;
	}
	
	public Mode getMode()
	{
		return this.mode;
	}
}