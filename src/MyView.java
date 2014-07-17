import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class MyView extends JPanel {

	//Frame Width and Height
	final int FRAME_WIDTH = 300;
	final int FRAME_HEIGHT = 650;
	
	JPanel panelLHS;
	JPanel panelRHS;
	//Lower half split pane
	JSplitPane splitPane;
	//Permissions Tables
	JTable tableLHS = new JTable(new MyTableModel());
	JTable tableRHS = new JTable(new MyTableModel());
	//Hierarchy Nodes
	DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Hierarchy");
	JTree  treeRHS  = new JTree(rootNode);
	//User Input Fields
	JTextField inputTextFieldLHS = new JTextField();
	JTextField inputTextFieldRHS = new JTextField();
	//Buttons
	JButton swapButton = new JButton();
	JButton loadButtonLHS = new JButton("Load LHS");
	JButton loadButtonRHS = new JButton("Load RHS");
	//Menu Items
	JMenuItem compareMenuItem = new JMenuItem("Compare");
	JMenuItem swapMenuItem = new JMenuItem("Swap Sides");
	JMenuItem manualEntryMenuItem = new JMenuItem("Manual Entry");
	JMenuItem saveLHSMenuItem = new JMenuItem("Left");
	JMenuItem saveRHSMenuItem = new JMenuItem("Right");
	JMenuItem exitMenuItem = new JMenuItem("Exit");
	JMenuItem hierarchyViewMenuItem = new JMenuItem("Hierarchy");
	JMenuItem listViewMenuItem = new JMenuItem("List");
	MyTextFieldListener textFieldlistener;
	
	ComponentWithTitle contentLHSView = new ComponentWithTitle(tableLHS);
	ComponentWithTitle contentRHSView = new ComponentWithTitle(tableRHS);
	ComponentWithTitle contentRHSHierarchy = new ComponentWithTitle(treeRHS);
	
	public MyView()
	{		
		setColumnWidth(tableLHS);
		setColumnWidth(tableRHS);
		
		treeRHS.setCellRenderer(new MyTreeRenderer());		
		
		textFieldlistener = new MyTextFieldListener();
		inputTextFieldLHS.getDocument().putProperty("owner", inputTextFieldLHS);
		inputTextFieldLHS.getDocument().addDocumentListener(textFieldlistener);
		textFieldlistener.setKeyMapping(inputTextFieldLHS);
		inputTextFieldRHS.getDocument().putProperty("owner", inputTextFieldRHS);
		inputTextFieldRHS.getDocument().addDocumentListener(textFieldlistener);
		textFieldlistener.setKeyMapping(inputTextFieldRHS);		
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, contentLHSView, contentRHSView);
		splitPane.setResizeWeight(0);
		
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);

		layout.setHorizontalGroup(
				layout.createParallelGroup()
					.addGroup(layout.createSequentialGroup()
							.addContainerGap()
							.addComponent(inputTextFieldLHS)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(swapButton)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(inputTextFieldRHS)
							.addContainerGap())
					.addGroup(Alignment.LEADING, layout.createSequentialGroup()
							.addContainerGap(100, 100)
							.addComponent(loadButtonLHS))
					.addGroup(Alignment.TRAILING, layout.createSequentialGroup()
							.addComponent(loadButtonRHS)
							.addContainerGap(100, 100))
					.addGroup(layout.createSequentialGroup()
							.addContainerGap()
							.addComponent(splitPane)
							.addContainerGap())
				);
		
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(inputTextFieldLHS)
						.addComponent(inputTextFieldRHS)
						.addComponent(swapButton))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(loadButtonLHS)
						.addComponent(loadButtonRHS)
						)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						//.addComponent(tableLHS)
						.addComponent(splitPane))
				.addContainerGap()
				);		
	}
	
	class ComponentWithTitle extends JPanel
	{
		JLabel titleLabel = new JLabel(" - ");
		ComponentWithTitle(JComponent component)
		{
			GroupLayout layout = new GroupLayout(this);
			setLayout(layout);
			
			JScrollPane scrollPane = new JScrollPane(component);
			scrollPane.setMinimumSize(new Dimension(300,0));			
			
			layout.setHorizontalGroup(
					layout.createParallelGroup()
							.addComponent(titleLabel, GroupLayout.Alignment.CENTER)
							.addComponent(scrollPane)
					);
			
			layout.setVerticalGroup(
					layout.createSequentialGroup()
						.addComponent(titleLabel)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(scrollPane)
					);	
			}
		
		public String getTitle()
		{
			return titleLabel.getText();
		}
		
		public void setTitle(String s)
		{
			titleLabel.setText(s);
		}
	}
	
	protected JMenuBar createMenu()
	{		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setOpaque(true);
		
		JMenu menu;
		
		//'File' Top Menu
		menu = new JMenu("File");
		//'Save' Sub Menu
		JMenu saveJMenu = new JMenu("Save");
		saveJMenu.add(saveLHSMenuItem);
		saveJMenu.add(saveRHSMenuItem);
		menu.add(saveJMenu);
		//'Exit' Sub Menu
		menu.add(exitMenuItem);
		menuBar.add(menu);
		
		//'Edit' Top Menu
		menu = new JMenu("Edit");
		//menu.add(new JMenuItem("Load Left"));
		//menu.add(new JMenuItem("Load Right"));		
		menu.add(compareMenuItem);
		menu.addSeparator();
		menu.add(swapMenuItem);
		menu.addSeparator();
		menu.add(manualEntryMenuItem);
		menuBar.add(menu);
		
		//'View' Top Menu
		menu = new JMenu("View");
		menu.add(hierarchyViewMenuItem);
		menu.add(listViewMenuItem);
		menuBar.add(menu);
		
		return menuBar;
	}
	
	/*
	 * This is a short term solution and needs to be implements in a 
	 * TableColumnModel properly. Including centering text. 
	 */
	public void setColumnWidth(JTable t)
	{
		t.getColumnModel().getColumn(0).setMaxWidth(80);
		t.getColumnModel().getColumn(0).setMinWidth(80);
		t.setAutoCreateRowSorter(true);
	}
	
	public String getLHSTextToString()
	{
		return inputTextFieldLHS.getText();
	}
	
	public String getRHSTextToString()
	{
		return inputTextFieldRHS.getText();
	}	
	
	public JTextField getLHSTextField()
	{
		return inputTextFieldLHS;
	}
	public JTextField getRHSTextField() 
	{
		return inputTextFieldRHS;
	}
	
	public String getLHSViewTitle()
	{
		return contentLHSView.getTitle();
	}
	
	public String getRHSViewTitle()
	{
		return contentRHSView.getTitle();
	}
	
	public String getRHSHierarchyTitle()
	{
		return contentRHSHierarchy.getTitle();
	}
	
	public MyTableModel getLHSTableModel()
	{
		return (MyTableModel) tableLHS.getModel();
	}
	
	public MyTableModel getRHSTableModel()
	{
		return (MyTableModel) tableRHS.getModel();
	}
	
	public JTable getTableLHS()
	{
		return tableLHS;
	}
	
	/*
	 * This can't be used without importing the CompentWithTitle into MyController
	public ComponentWithTitle getLHSContent()
	{
		return contentLHSView;
	}
	*/
	
	public DefaultMutableTreeNode getRootNode()
	{
		return rootNode;
	}
	
	public JTree getJTree()
	{	
		return treeRHS;
	}
	
	public MyTextFieldListener getTextFieldListener()
	{
		return textFieldlistener;
	}
	
	public void setControllerActions(ActionListener controllerActionListener)
	{		
		//Setup Menu Actions
		compareMenuItem.setActionCommand("Compare");
		compareMenuItem.addActionListener(controllerActionListener);
		swapMenuItem.setActionCommand("Swap Hierarchy");
		swapMenuItem.addActionListener(controllerActionListener);
		hierarchyViewMenuItem.setActionCommand("View Hierarchy");
		hierarchyViewMenuItem.addActionListener(controllerActionListener);
		listViewMenuItem.setActionCommand("View List");
		listViewMenuItem.addActionListener(controllerActionListener);
		exitMenuItem.setActionCommand("Exit");
		exitMenuItem.addActionListener(controllerActionListener);
		//Setup Button Actions
		loadButtonLHS.setActionCommand("Load LHS");
		loadButtonLHS.addActionListener(controllerActionListener);
		loadButtonRHS.setActionCommand("Load RHS");
		loadButtonRHS.addActionListener(controllerActionListener);
		swapButton.setActionCommand("Swap Sides");
		swapButton.addActionListener(controllerActionListener);
	}
	
	public void setLHSViewTitle(String s)
	{
		contentLHSView.setTitle(s);	
	}
	
	public void setRHSViewTitle(String s)
	{
		contentRHSView.setTitle(s);
	}
	
	public void setRHSHierarchyTitle(String s)
	{
		contentRHSHierarchy.setTitle(s);
	}
	
	public void setHierarchyPanel(boolean isHierarchy)
	{
		if(isHierarchy)
		{
			splitPane.setRightComponent(contentRHSHierarchy);
		} else {			
			splitPane.setRightComponent(contentRHSView);
		}
	}	
	
	public void createAndShowGUI()
	{
		//Create frame setup Window
		JFrame frame = new JFrame("My View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(FRAME_HEIGHT, FRAME_WIDTH));
		
		//Add the content  
		frame.getContentPane().add(this);
		
		//Add the Menu
		frame.setJMenuBar(createMenu());
		
		//Display the window
		frame.pack();
		frame.setVisible(true);		
	}
}
