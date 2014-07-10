import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;


public class MyTextFieldListener implements DocumentListener {

	@Override
	public void changedUpdate(DocumentEvent ev) {	
	}	

	@Override
	public void removeUpdate(DocumentEvent ev) {
	}

	@Override
	public void insertUpdate(DocumentEvent ev) {
		
		//What does this do?
		if (ev.getLength() != 1) {
			return;
		}
		
		Object owner = ev.getDocument().getProperty("owner");
		JTextField textField = null;
		if(owner instanceof JTextField)
		{
			textField = (JTextField) owner;
		} else {
			return;
		}
		
		//Find the position of the cursor
		int pos = ev.getOffset(); 
		String content = null;
		try {
			//Grab text from the beginning of the document to the cursor position
			content = textField.getText(0, pos + 1);
		} catch (BadLocationException e) { 
			e.printStackTrace();
		}
		
		//Set 'w' as the starting character of the word
		int w;
		for (w = pos; w >= 0; w--)
		{
			//Break on a non-letter character
			if (! Character.isLetter(content.charAt(w)))
			{
			break;
			}
		}
		
		//Not enough characters
		if ((pos - w) < 2)
		{
			return;
		}
		
		System.out.println(w);
		
		System.out.println(content);
	}
}
