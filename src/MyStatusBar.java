import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class MyStatusBar extends JPanel {

	protected JPanel leftPanel;
	protected JPanel rightPanel;
	Border blackLine = BorderFactory.createLineBorder(Color.BLACK);
	
	public MyStatusBar() {
		createPartControl();
	}
	
	protected void createPartControl()
	{		
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(getWidth(), 23));
		
		leftPanel = new JPanel(new FlowLayout(
				FlowLayout.LEADING, 8, 4));
		leftPanel.setOpaque(false);
		add(leftPanel, BorderLayout.WEST);
		
		rightPanel = new JPanel(new FlowLayout(
				FlowLayout.TRAILING, 8, 4));
		rightPanel.setOpaque(false);
		add(rightPanel, BorderLayout.EAST);		
	}
	
	/**
	 * Add the specified JComponent <code>component</code> to the left hand side of <code>this</code>.
	 * @param component - Specified component
	 */
	public void setLeftComponent(JComponent component)
	{
		leftPanel.add(component);
	}
	
	/**
	 * Remove the specified JComponent <code>component</code>
	 * @param component - The specified JComponent
	 */
	public void removeLeftComponent(JComponent component)
	{
		leftPanel.remove(component);
	}
	
	/**
	 * Add the specified JComponent <code>component</code> to the right hand side of <code>this</code>,
	 * with a background color of <code>color</code>
	 * @param component - Specified component
	 * @param color - Specified color
	 */
	public void addRightComponent(JComponent component, Color color)
	{
		JPanel panel = new JPanel(new FlowLayout(
				FlowLayout.LEADING, 20, 0));
		
		panel.setBackground(color);
		panel.setBorder(blackLine);
		
		panel.add(component);		
		rightPanel.add(panel);
	}	
	
	/**
	 * Remove the specified JComponent <code>component</code>
	 * @param component - The specified JComponent
	 */
	public void removeRightComponent(JComponent component)
	{
		rightPanel.remove(component);
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		int y = 0;
		g.setColor(new Color(156, 154, 140));
		g.drawLine(0, y, getWidth(), y);
		
		y = getHeight() - 3;
	}
}