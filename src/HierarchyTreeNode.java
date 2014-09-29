import javax.swing.tree.DefaultMutableTreeNode;

public class HierarchyTreeNode extends DefaultMutableTreeNode {

	public static enum ActiveMode { INACTIVE, PARTIAL_ACTIVE, ACTIVE };
	public static enum SelectedMode { NOT_SELECTED, PARTIAL_SELECTED, SELECTED };
	
	ActiveMode activeMode;
	SelectedMode selectedMode;
	HierarchyData value;
	
	public HierarchyTreeNode(HierarchyData hd)
	{
		activeMode = ActiveMode.INACTIVE;
		selectedMode = SelectedMode.NOT_SELECTED;
		this.userObject = hd;
	}
	
	/**
	 * Sets this node's 'active' or 'selected' mode to <code>newMode</code><br> 
	 * Depending on if <code>newMode</code> is a instance of 
	 * <code>HierarchyTreeNode.ActiveMode</code> or <code>HierarchyTreeNode.SelectedMode</code> 
	 * @param newMode a new active/selected mode
	 */
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
	
	/**
	 * Returns this node's 'active mode'. 
	 * @return The <code>HierarchyTreeNode.ActiveMode</code> stored at this node. 
	 */
	public ActiveMode getActiveMode()
	{
		return this.activeMode;
	}
	
	/**
	 * Returns this node's 'selected mode' 
	 * @return The <code>HierarchyTreeNode.SelectedMode</code> stored at this node. 
	 */
	public SelectedMode getSelectedMode()
	{
		return this.selectedMode;
	}
}