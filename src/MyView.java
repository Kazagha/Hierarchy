import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.table.TableModel;

public class MyView extends JPanel {

	JPanel panelLHS;
	JPanel panelRHS;
	
	JTable tableLHS = new JTable(new MyTableModel());
	JTable tableRHS = new JTable(new MyTableModel());
	
	JTextField userTextFieldLHS = new JTextField();
	JTextField userTextFieldRHS = new JTextField();
	
	JLabel titleLabelLHS = new JLabel(" - ");
	JLabel titleLabelRHS = new JLabel(" - ");
	
	JButton swapButton = new JButton();
	JButton loadButtonLHS = new JButton("Load LHS");
	JButton loadButtonRHS = new JButton("Load RHS");
	
	public MyView()
	{		
		setColumnWidth(tableLHS);
		setColumnWidth(tableRHS);
		JPanel panelLHS = new ComponentWithTitle(titleLabelLHS, tableLHS);
		JPanel panelRHS = new ComponentWithTitle(titleLabelRHS, tableRHS);
		
		Icon swapIcon = new ImageIcon("images/arrow-repeat.png");
		if(swapIcon != null)
		{
			swapButton.setIcon(swapIcon);	
		}
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelLHS, panelRHS);
		splitPane.setResizeWeight(0);
		
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		
		layout.setHorizontalGroup(
				layout.createParallelGroup()
					.addGroup(layout.createSequentialGroup()
							.addContainerGap()
							.addComponent(userTextFieldLHS)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(swapButton)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(userTextFieldRHS)
							.addContainerGap())
					.addGroup(Alignment.CENTER, layout.createSequentialGroup()
							.addContainerGap()
							.addComponent(loadButtonLHS)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(loadButtonRHS)
							.addContainerGap())
					.addGroup(layout.createSequentialGroup()
							.addContainerGap()
							//.addComponent(tableLHS)
							.addComponent(splitPane)
							.addContainerGap())
				);
		
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(userTextFieldLHS)
						.addComponent(userTextFieldRHS)
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
		ComponentWithTitle(JLabel title, JComponent component)
		{
			GroupLayout layout = new GroupLayout(this);
			setLayout(layout);
			
			JScrollPane scrollPane = new JScrollPane(component);
			scrollPane.setMinimumSize(new Dimension(300,0));			
			
			layout.setHorizontalGroup(
					layout.createParallelGroup()
							.addComponent(title, GroupLayout.Alignment.CENTER)
							.addComponent(scrollPane)
					);
			
			layout.setVerticalGroup(
					layout.createSequentialGroup()
						.addComponent(title)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(scrollPane)
					);		
		}	
	}
	
	/*
	 * This is a short term solution and needs to be implements in a 
	 * TableColumnModel properly. Including centering text. 
	 */
	public void setColumnWidth(JTable t)
	{
		t.getColumnModel().getColumn(0).setMaxWidth(80);
		t.getColumnModel().getColumn(0).setMinWidth(80);
	}
	
	public String getLHSTextField()
	{
		return userTextFieldLHS.getText();
	}
	
	public String getRHSTextField()
	{
		return userTextFieldRHS.getText();
	}
	
	public MyTableModel getLHSTableModel()
	{
		return (MyTableModel) tableLHS.getModel();
	}
	
	public MyTableModel getRHSTableModel()
	{
		return (MyTableModel) tableRHS.getModel();
	}
	
	public void setControllerActions(ActionListener controllerActionListener)
	{
		loadButtonLHS.setActionCommand("Load LHS");
		loadButtonRHS.setActionCommand("Load RHS");
		
		loadButtonLHS.addActionListener(controllerActionListener);
		loadButtonRHS.addActionListener(controllerActionListener);
	}
	
	public void setLHSTitle(String s)
	{
		titleLabelLHS.setText(s);		
	}
	
	public void setRHSTitle(String s)
	{
		titleLabelRHS.setText(s);
	}
	
	public void createAndShowGUI()
	{
		//Create frame setup Window
		JFrame frame = new JFrame("My View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(650, 300));
		
		//Add the content  
		frame.getContentPane().add(this);
		
		//Display the window
		frame.pack();
		frame.setVisible(true);		
	}
}
