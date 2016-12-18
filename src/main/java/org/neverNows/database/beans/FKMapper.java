package org.neverNows.database.beans;

/**
 * mapea una tabla con su foreing key
 * @author leonelsoriano3@gmail.com
 *
 */
public class FKMapper {
	
	private String nameTable;
	private String namecolumn;

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


}
