package io.xeros.script;

/**
 * Manifests a logic script.
 * 
 * @author Adil
 * @version 12/03/2017
 */
public interface Script {
	
	/**
	 * Instantiates a script.
	 * 
	 * @param args
	 * 			Script arguments.
	 * @return
	 * 		the instance.
	 * @throws Throwable
	 * 				if exception occurs.
	 */
	public Script instance(Object... args) throws Throwable;
	
	/**
	 * Starts a script.
	 * 
	 * @param args
	 * 			The arguments.
	 */
	Object start(Object... args);
	
	/**
	 * @return the type of script it is.
	 */
	ScriptType getType();

}
