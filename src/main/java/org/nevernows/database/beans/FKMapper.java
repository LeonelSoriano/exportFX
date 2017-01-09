package org.nevernows.database.beans;

/**
 * mapea una tabla con su foreing key
 * @author leonelsoriano3@gmail.com
 *
 */
public class FKMapper {
	
	private String nameTable;
	private String namecolumn;
	private String nameTableRef;
	private String namecolumnRef;
	
	public String getNameTable() {
		return nameTable;
	}
	public void setNameTable(String nameTable) {
		this.nameTable = nameTable;
	}
	public String getNamecolumn() {
		return namecolumn;
	}
	public void setNamecolumn(String namecolumn) {
		this.namecolumn = namecolumn;
	}
	public String getNameTableRef() {
		return nameTableRef;
	}
	public void setNameTableRef(String nameTableRef) {
		this.nameTableRef = nameTableRef;
	}
	public String getNamecolumnRef() {
		return namecolumnRef;
	}
	public void setNamecolumnRef(String namecolumnRef) {
		this.namecolumnRef = namecolumnRef;
	}

}
