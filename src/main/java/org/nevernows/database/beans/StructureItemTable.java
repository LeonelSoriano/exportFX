package org.nevernows.database.beans;

import java.util.ArrayList;
import java.util.List;

public class StructureItemTable {
	
	private int order;
	
	private int id;
	
	private String name;
	
	private String type;
	
	private boolean notNull;
	
	private String defaulValue;
	
	private boolean isFk;
	
	private int maxValue;
	
	private List<Object> dataValues;
	
	public StructureItemTable(){
		this.dataValues = new ArrayList<>();
	}

	public StructureItemTable(int id, String name, String type, boolean notNull, String defaulValue, boolean isFk) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.notNull = notNull;
		this.defaulValue = defaulValue;
		this.isFk = isFk;
		this.dataValues = new ArrayList<>();
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


	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	
	public List<Object> getDataValues() {
		return dataValues;
	}
	
	/**
	 * consigue un valor de los datos por el index dado
	 * @param index index que se buscar devolver si es mayor al tamaño del index
	 * en cuestion devuelve un null o si es menor a 0
	 * @return
	 */
	public Object getDataByIndex(int index){
		if(index < 0){
			return null;
		}else if(this.dataValues.size() < index){
			return null;
		}else{
			return this.dataValues.get(index);
		}
	}
	
	/**
	 * agrega un valor a la variable que tiene los datos de la base de datos
	 * @param obj el valor a ingresar
	 */
	public void addDataValues(Object obj){
		this.dataValues.add(obj);
	}
	
	

	public void setDataValues(List<Object> dataValues) {
		this.dataValues = dataValues;
	}

	
}
