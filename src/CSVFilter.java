import java.io.File;

import javax.swing.filechooser.FileFilter;

public class CSVFilter extends FileFilter {

	public CSVFilter() {}

	@Override
	public boolean accept(File f) {
		// Show all directories
		if (f.isDirectory())
		{
			return true;
		}
		
		// Get the file name
		String nameString = f.getName();
		// Find the position of the last '.' character
		int lastIndexOf = nameString.lastIndexOf(".");
		
		String extensionString = null;
		
		// Check that a file extension exists
		if(lastIndexOf > 0)
		{
			// Find the extension
			extensionString = nameString.substring(lastIndexOf);
		}
				
		// If the extension exists, is it a '.CSV' extension
		if (extensionString != null)
			if (extensionString.equalsIgnoreCase(".csv"))
			{
				return true;
			} 
		
		return false;
	}

	@Override
	public String getDescription() {
		return "CSV (Comma delimited)";
	}
}
