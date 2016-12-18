package org.neverNows.until;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ComandTerminal {
	
	
	/**
	 * de ser verdadero imprime siempre las salidas y entradas en consola
	 */
	private boolean berbose = false;
	
	/**
	 * ejecuta una opcion des de linea de comandos
	 * @param command el comando bash
	 * @return la salida de consola del comando
	 */
	public String executeCommand(String command) {
		
		if(berbose)
			System.out.println(command);
		
		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			
			BufferedReader reader =
                            new BufferedReader(new InputStreamReader(p.getInputStream()));

                        String line = "";
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}

			
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(berbose)
			System.out.println(output.toString());
		
		return output.toString();

	}

	public boolean isBerbose() {
		return berbose;
	}

	public void setBerbose(boolean berbose) {
		this.berbose = berbose;
	}


	
	
	

}
