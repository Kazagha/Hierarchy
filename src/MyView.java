import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.plaf.TableHeaderUI;

public class MyView extends JPanel {

	JTable tableLHS = new JTable(10, 2);
	JTable tableRHS = new JTable(10, 2);
	
	JTextField userTextFieldLHS = new JTextField();
	JTextField userTextFieldRHS = new JTextField();
	
	JLabel titleLabelLHS = new JLabel("User 1");
	JLabel titleLabelRHS = new JLabel("User 2");
	
	JButton swapButton = new JButton();
	
	public MyView()
	{
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
							//.addComponent(titleLabelLHS)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							//.addComponent(titleLabelRHS)
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
						//.addComponent(titleLabelLHS)
						//.addComponent(titleLabelRHS)
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
	
	public String getTextFieldLHS()
	{
		return userTextFieldLHS.getText();
	}
	
	public String getTextFieldRHS()
	{
		return userTextFieldRHS.getText();
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
