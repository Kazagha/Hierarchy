public class RoleData
{
	//Instanise
	int roleNum;
	String roleDesc;

	public RoleData(int roleNum, String roleDesc)
	{
		this.roleNum= roleNum;
		this.roleDesc= roleDesc;	
	}
	
	public RoleData(RoleData rd)
	{
		this.roleNum = rd.getRole();
		this.roleDesc = rd.getDescription();
	}

	public int getRole()
	{
		return roleNum;
	}

	public String getDescription()
	{
		return roleDesc;
	}
	
	public void setRole(int roleNum)
	{
		this.roleNum = roleNum;
	}
	
	public void setDescription(String roleDesc)
	{
		this.roleDesc = roleDesc;
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
