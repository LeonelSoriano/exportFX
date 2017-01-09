package org.nevernows;

/**
 * excepcion que verifica que la cantidad de parametros del json
 * son igual a los de las base de datos
 * @author leonel
 *
 */
public class SqlJsonNoEqualParamException extends Exception {

	private static final long serialVersionUID = -3441746223781584341L;

	public SqlJsonNoEqualParamException() {
		super(" No pueden ser distintos los parametros de entrada y"
				+ " los de la base de datos ");
	}


}
