import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class MySearchDialog extends JPanel {

	//Frame Width and Height
	final int FRAME_WIDTH = 280;
	final int FRAME_HEIGHT = 110;
	
	JFrame searchFrame = new JFrame();
	Component viewComponent;
	
	JButton nextButton = new JButton("Next");
	JButton prevButton = new JButton("Prev");
	JTextField searchTextField = new JTextField();
	
	public MySearchDialog(Component parentComponent)
	{		
		viewComponent = parentComponent;
		searchFrame.setTitle("Hierarchy Search");
		searchFrame.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		
		searchFrame.getContentPane().add(this);
		
		JLabel titleLabel = new JLabel("Find: ");
		
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		
		layout.setHorizontalGroup(
			layout.createParallelGroup(Alignment.CENTER)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(titleLabel)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(searchTextField)
						.addContainerGap()					
						)
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addComponent(prevButton)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(nextButton)
					.addContainerGap()
						)
				);
		
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup()
						.addComponent(titleLabel)
						.addComponent(searchTextField, 20, 20, 20)
						)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup()
					.addComponent(prevButton)
					.addComponent(nextButton)
					)
				.addContainerGap()
				);
		
		searchFrame.pack();
	}
	
	public void showDialog()
	{
		searchFrame.setVisible(true);
		searchFrame.setLocationRelativeTo(viewComponent);
	}	
	
	public String getTextField()
	{
		return searchTextField.getText();
	}
	
	public void setActionListener(ActionListener aListener)
	{
		nextButton.setActionCommand("Search Next");
		nextButton.addActionListener(aListener);
		
		prevButton.setActionCommand("Search Prev");
		prevButton.addActionListener(aListener);
	}
}
