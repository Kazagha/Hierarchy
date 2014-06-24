public class RoleData
{
	//Instanise
	int i;
	String s;

	public RoleData(int i, String s)
	{
		this.i = i;
		this.s = s;	
	}

	public int getRole()
	{
		return i;
	}

	public String getDescription()
	{
		return s;
	}

	/**
	 * Equals.
	 * Compares 'this' to the input object.
	 * @param obj Object to compare against
	 * @return boolean RoleData objects with the same role number return true.
	 */
	public boolean equals(Object obj)
	{
		//Test if 'this' is the same as the object
		if (this == obj)
			return true;

		//Test of the object is null, or of a different class to 'this'
		if((obj == null) || (obj.getClass() != this.getClass()))
			return false;

		//Therefore the object must be a RoleData class, so it's safe to cast
		RoleData other = (RoleData)obj;

		//Test if the role of 'this' is equal to the role of object 
		return (this.getRole() == other.getRole());
	}
}
