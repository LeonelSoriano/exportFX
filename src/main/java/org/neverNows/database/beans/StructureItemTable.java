package org.neverNows.database.beans;

public class StructureItemTable {
	
	private int order;
	
	private int id;
	
	private String name;
	
	private String type;
	
	private boolean notNull;
	
	private String defaulValue;
	
	private boolean isFk;
	
	public StructureItemTable(){}

	public StructureItemTable(int id, String name, String type, boolean notNull, String defaulValue, boolean isFk) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.notNull = notNull;
		this.defaulValue = defaulValue;
		this.isFk = isFk;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNotNull() {
		return notNull;
	}

	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}

	public String getDefaulValue() {
		return defaulValue;
	}

	public void setDefaulValue(String defaulValue) {
		this.defaulValue = defaulValue;
	}

	public boolean isFk() {
		return isFk;
	}

	public void setFk(boolean isFk) {
		this.isFk = isFk;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public String toString() {
		return "StructureItemTable [id=" + id + ", name=" + name + ", type=" + type + ", notNull=" + notNull
				+ ", defaulValue=" + defaulValue + ", isFk=" + isFk + ", order= "+ order +"]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((defaulValue == null) ? 0 : defaulValue.hashCode());
		result = prime * result + id;
		result = prime * result + (isFk ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (notNull ? 1231 : 1237);
		result = prime * result + order;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StructureItemTable other = (StructureItemTable) obj;
		if (defaulValue == null) {
			if (other.defaulValue != null)
				return false;
		} else if (!defaulValue.equals(other.defaulValue))
			return false;
		if (id != other.id)
			return false;
		if (isFk != other.isFk)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (notNull != other.notNull)
			return false;
		if (order != other.order)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}


	
	

}
