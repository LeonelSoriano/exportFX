package org.neverNows.until;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileUntil {
	

	
	/**
	 * devuelve en un string el contenido de un archivo tcxt
	 * @param string nombre y path relativo a resource
	 * @return el string que contiene el txt
	 * @throws IOException 
	 */
	public String txtToString(String string)  {
		
		StringBuilder pathNameFile = new StringBuilder(
				 string);
		
		StringBuilder builder = new StringBuilder();
		BufferedReader br = null;
		FileReader fr = null;
		
		try {

			fr = new FileReader(pathNameFile.toString());
			br = new BufferedReader(fr);
	
			String sCurrentLine;
	
			br = new BufferedReader(new FileReader(pathNameFile.toString()));
	
			while ((sCurrentLine = br.readLine()) != null) {
				builder.append(sCurrentLine);
			}
		
		} catch (IOException e) {
			e.printStackTrace();
			return "";

		} finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}
		
		return builder.toString();
	}
	
	/**
	 * limpia la TRANSACTION de dump ya que tiene probleas con DBI
	 * @param dumpTxt string del dump
	 * @return el dump sin lineas nuevas ni la trnasacion
	 */
	public String cleanDump(String dumpTxt){
		return dumpTxt.replace(System.getProperty("line.separator"), "")
				.replace("BEGIN TRANSACTION;", "")
				.replace("COMMIT;", "")
				.replace("\"", "'");
	}
	


}
