import javax.swing.tree.DefaultMutableTreeNode;

public class HierarchyTreeNode extends DefaultMutableTreeNode {

	public static enum ActiveMode { INACTIVE, PARTIAL_ACTIVE, ACTIVE };
	public static enum SelectedMode { NOT_SELECTED, PARTIAL_SELECTED, SELECTED };
	private static final int INACTIVE = 0;
	private static final int PARTIAL_ACTIVE = 1;
	private static final int ACTIVE = 2;
	private static final int NOT_SELECTED = 0;
	private static final int PARTIAL_SELECTED = 1;
	private static final int SELECTED = 2;	
	
	ActiveMode activeMode;
	SelectedMode selectedMode;
	HierarchyData value;
	
	public HierarchyTreeNode(HierarchyData hd)
	{
		activeMode = ActiveMode.INACTIVE;
		selectedMode = SelectedMode.NOT_SELECTED;
		this.userObject = hd;
	}
	
	public void setMode(Object newMode)
	{
		if(newMode instanceof ActiveMode)
		{			
			this.activeMode = (HierarchyTreeNode.ActiveMode) newMode;
		} else if(newMode instanceof SelectedMode) 
		{
			this.selectedMode = (HierarchyTreeNode.SelectedMode) newMode;
		}
	}
	
	public ActiveMode getActiveMode()
	{
		return this.activeMode;
	}
	
	public SelectedMode getSelectedMode()
	{
		return this.selectedMode;
	}
}