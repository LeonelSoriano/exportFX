package org.nevernows.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.nevernows.database.FatherDatabase;
import org.nevernows.database.SqliteDb;
import org.nevernows.database.beans.StructureItemTable;
import org.nevernows.database.beans.StructureTable;
import org.nevernows.param.CommonString;

/**
 * clase que general el excel desde la base de datos
 * date: 01-09-2016
 * @author leonelsoriano3@gmail.com 
 * @version 1.0
 */
public class GenerateExcel {
	
	private Logger logger = Logger.getLogger(SqliteDb.class.getName());
	
	private XSSFWorkbook workbook;

	private FatherDatabase database;

	private static final String NAME_FILE = "massive_load.xlsx";

	private  static final String NAME_FILE_CONFIGURATION = "config.json";

	private String basePAth;

	private Map<String, String> replaceWord;

	List<String> tablesOrder;

	private Map<String, XSSFSheet> mapTableSheet;

	
	/**
	 * genera un excel desde la base de datos
	 * @param los parametros de la base de datos
	 * @param isTest si esta en pruebas esto no deberia de ir aca pero lo 
	 * puse :P
	 */
	public GenerateExcel(String databaseStr, boolean isTest) {

		this.database = new SqliteDb(databaseStr);
		
		this.replaceWord = new HashMap<>();

		this.workbook = new XSSFWorkbook();

		this.mapTableSheet = new HashMap<>();

		if (isTest) {
			this.basePAth = CommonString.PATH_TEST_EXCEL;
		} else {

		}
		XSSFSheet spreadsheet = workbook.createSheet("Sheet Name");
	}

	/**
	 * genera el excel desde la base de datos
	 */
	public void generateFromDB() {

		
		//genero la configuracion
		this.database.generateConfig();
		
		Map<String, String> fkConfiguration = this.database.getFKConfiguration();
		
		
		
		tablesOrder = this.database.getOrderTableByDump("test/db/export.sql");

		for (String tableName : tablesOrder) {

			XSSFSheet spreadsheet = null;
			String tableAlias = tableName;

			if (this.replaceWord.get(tableName) != null) {
				tableAlias = this.replaceWord.get(tableName);
			}
			spreadsheet = workbook.createSheet(tableAlias);

			this.mapTableSheet.put(tableAlias, spreadsheet);
			XSSFRow row;
			boolean isParam = this.database.tableIsParam(tableName);

			StructureTable structureTable = this.database.getStructureTableWithData(tableName);

			Map<String, Object[]> data = new HashMap<String, Object[]>();

			List<String> titleRow = new ArrayList<>();
			for (StructureItemTable itemTable : structureTable.getItemTables()) {
				titleRow.add(itemTable.getName());
			}

			data.put("0", titleRow.toArray());

			if (!structureTable.getItemTables().isEmpty()
					&& !structureTable.getItemTables().get(0).getDataValues().isEmpty()) {

				int dataLenght = structureTable.getItemTables().get(0).getDataValues().size();

				int columnLenght = structureTable.getItemTables().size();

				for (int i = 0; i < dataLenght; i++) {

					List<String> dataRow = new ArrayList<>();

					for (int j = 0; j < columnLenght; j++) {
						
						Object objectData = structureTable.getItemTables().get(j).getDataValues().get(i);
						
						//TODO: me falta ahora los combos aca debo saber cual sera many to many
						// asi que me falta un metodo para agregarla la configuracion del many
						// to any al configurador json de la db ademas de eso obtener el dato del fk
						// de aca abajo es decir traer el valor de verdad del fk
						

						//TODO: tengo que desaparecer la columna de many to many que estara en la tabla
						// de quiebre esto mas q todo es estetico
						
						//aca pregunto si es fk y esta en la configuracion 
						//eso para cambiarle el dato por el del fk
						if( structureTable.getItemTables().get(j).isFk() &&
								fkConfiguration.containsKey((tableName + titleRow.get(j)).toUpperCase())){
							
							Object valueConfFk = this.database.getValueFK(
									tableName, titleRow.get(i), objectData);
							if(valueConfFk != null){
								dataRow.add(valueConfFk.toString());
							}else{
								dataRow.add(objectData.toString());
							}
						}else{
							dataRow.add(objectData.toString());
						}
						
						
						//dataRow.add("leonel");

					}

					Integer columnNumber = (i + 1);
					data.put(columnNumber.toString(), dataRow.toArray());
				}

				// System.out.println("____________________________");

			}

			/*
			 * for (int i = 0; i < ; i++) { //array_type array_element =
			 * array[i];
			 * 
			 * }
			 */

			/*
			 * for(int i = 0; i < titleRow.size(); i++ ){
			 * 
			 * List<Object> listDataTmp = new ArrayList<>();
			 * 
			 * StructureItemTable item = structureTable.getItemTables().get(i);
			 * item.getId(); //item.get
			 * 
			 * 
			 * }
			 */

			// for(int i = 0; i < structureTable.getItemTables().size(); i++ ){

			// StructureItemTable item =
			// structureTable.getItemTables().get(i);

			// TODO que de ne la parte de los titulos ideas
			// que se active una variable para hacer mayuscula empezando y luego
			// de espacio en los titulos y ademas un archivo de configuracion
			// que me permita cambiar palabras en el excel sera un json que
			// quede
			// guardado alado del excel

			// data.put( new Integer(i-1).toString(),
			// item.getDataValues().toArray());
			// }
			// data.put("1", new Object[] {"Emp No.", "Name", "Salary"});
			// data.put("2", new Object[] {1d, "John", 1500000d});
			// data.put("3", new Object[] {2d, "Sam", 800000d});
			// data.put("4", new Object[] {3d, "Dean", 700000d});
			Set<String> keyset = data.keySet();
			int rownum = 0;
			for (String key : keyset) {
				row = spreadsheet.createRow(rownum);
				rownum++;
				Object[] objArr = data.get(key);
				int cellnum = 0;

				for (Object obj : objArr) {
					Cell cell = row.createCell(cellnum++);
					if (obj instanceof Date) {

						cell.setCellValue((Date) obj);

					} else if (obj instanceof Boolean) {

						cell.setCellValue((Boolean) obj);

					} else if (obj instanceof String) {

						if (this.replaceWord.get((String) obj) != null) {
							cell.setCellValue(this.replaceWord.get((String) obj));
						} else {
							cell.setCellValue((String) obj);
						}

					} else if (obj instanceof Double) {
						cell.setCellValue((Double) obj);
					}

				}
			}

		}

		workbook.removeSheetAt(0);

		this.createFile();
	}

	/**
	 * genera un archivo con un json que es la confuguracion extra de excel
	 */
	private void generateConfig() {

		JSONObject jsonConfig = new JSONObject();

		JSONArray jsonRemplaceWord = new JSONArray();

		for (Map.Entry<String, String> entry : this.replaceWord.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			JSONObject tmpJson = new JSONObject();
			tmpJson.put("word", key);
			tmpJson.put("replace", value);

			jsonRemplaceWord.put(tmpJson);
		}

		jsonConfig.put("replace", jsonRemplaceWord);


		try {
			File newTextFile = new File(this.basePAth + NAME_FILE_CONFIGURATION);
			FileWriter fw;
			fw = new FileWriter(newTextFile);
			fw.write(jsonConfig.toString());
			fw.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
			
		
	}

	/**
	 * este metodo agrega al map variables de reemplazo
	 * 
	 * @param key
	 *            palabra original
	 * @param value
	 *            palabra que se cambiara
	 */
	public void addWordToReplace(String key, String value) {
		this.replaceWord.put(key, value);
	}

	private void createFile() {
		try {
			FileOutputStream out = new FileOutputStream(new File(this.basePAth + NAME_FILE));
			workbook.write(out);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public void addFkConf(String table, String columnFK){
		this.database.getValueConfFK().put(table, columnFK);
	}
	
	public void addManyConf(String table, String column){
		this.database.getValueConfManyToMany().put(table, column);
	}

}
