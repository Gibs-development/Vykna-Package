package io.xeros.script;

import io.xeros.script.npc.AstuteNPC;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * The script repository.
 * 
 * @author Adil
 * @version 12/03/2017
 */
@SuppressWarnings("all")
public class ScriptRepository<T extends Script> extends AbstractCollection<T> {
	
	/**
	 * Script capacity.
	 */
	private static final int CAPACITY = 2048;
	
	/**
	 * Scripts directory.
	 */
	private static final String DIRECTORY = "script";
	
	/**
	 * The scripts.
	 */
	private Object[] scripts;

	/**
	 * A collection of index pairs.
	 */
	public Set<Integer> indices = new HashSet<Integer>();
	
	/**
	 * The currently pointed index.
	 */
	private int pointer;
	
	/**
	 * The abstract NPC mapping.
	 */
	protected static final Map<Object, Class<? extends AstuteNPC>> NPC_MAPPING = new HashMap<>();
	
	/**
	 * Constructs a new {@link ScriptRepository} {@code Object}.
	 */
	public ScriptRepository() {
		scripts = new Script[CAPACITY];
	}
	
	/**
	 * Populates the script repository.
	 */
	public void init() {
		try {
			for (Class c : getRawScripts() ) {
				if (!Script.class.isAssignableFrom(c)) continue;
				Script script = null;
				try {
					script = (Script) c.newInstance();
					add((T) script);
				} catch (InstantiationException | IllegalAccessException e) {
					System.out.println("Unable to instantiate script " + c.getSimpleName() + " (NEEDS CONSTRUCTOR WITH NO ARGS) !");
				}
			}
			System.out.println("Finished initialising " + size() + " script" + (size() == 1 ? "" : "s") + "!");
		} catch (ClassNotFoundException | IOException  e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets a collection of raw script classes.
	 * 
	 * @return a collection of script classes.
	 * 
	 * @throws ClassNotFoundException
	 * 						Class not found through name string.
	 * @throws IOException
	 * 				IO operation failed or interrupted.
	 */
	public Class[] getRawScripts() throws ClassNotFoundException, IOException {
		 ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	        assert classLoader != null;
	        String path = DIRECTORY.replace('.', '/');
	        Enumeration<URL> resources = classLoader.getResources(path);
	        List<File> dirs = new ArrayList<File>();
	        while (resources.hasMoreElements()) {
	            URL resource = resources.nextElement();
	            dirs.add(new File(resource.getFile().replaceAll("%20", " ")));
	        }
	        ArrayList<Class> classes = new ArrayList<Class>();
	        for (File directory : dirs) {
	            classes.addAll(findClasses(directory, DIRECTORY));
	        }
	        return classes.toArray(new Class[classes.size()]);
	}
	
	/**
	 * Collects all class files in a directory.
	 * 
	 * @param directory
	 * 				The pointing directory.
	 * @param packageName
	 * 				The package name.
	 * @return A collection of classes in the directory.
	 */
	private List<Class> findClasses(File directory, String packageName) {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				try {
					classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		return classes;
	}

	@Override
	public Iterator<T> iterator() {
		return new ScriptRepositoryIterator<>(scripts, indices);
	}

	@Override
	public int size() {
		int count = 0;
		for (Object entity : scripts) {
			if (entity == null)
				continue;
			count++;
		}
		return count;
	}
	
	@Override
	public boolean add(T e) {
		return add(e, pointer);
	}
	
	/**
	 * Appends a script to the collection.
	 * 
	 * @param script
	 * 			The script to add.
	 * @param index
	 * 			The index to append to.
	 */
	public boolean add(T script, int index) {
		if (scripts[pointer] != null) {
			int slot = next();
			if (slot == -1) {
				return false;
			}
			pointer = slot;
			add(script, pointer);
		} else {
			scripts[pointer] = script;
			indices.add(pointer);
			define(script.getClass(), script);
		}
		return true;
	}
	
	/**
	 * @return the next free slot.
	 */
	private int next() {
		for (int slot = 0; slot < CAPACITY; slot++) {
			if (scripts[slot] == null)
				return slot;

		}
		return -1;
	}

	/**
	 * Defines a script and its type.
	 * 
	 * @param clas
	 * 			The script class.
	 * @param plugin
	 * 			The script instance.
	 */
	public void define(Class<?> clas, Script plugin) {
		try {
			switch(plugin.getType()) {
				case NPC:
					NPC_MAPPING.put(((AstuteNPC) plugin).getIdentifier(), (Class<? extends AstuteNPC>) plugin.getClass());
					break;
				default:
					System.out.println("Undefined script manifest : " + plugin.getClass().getSimpleName() + ".");
					break;
			
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the scripts
	 */
	public Object[] getScripts() {
		return scripts;
	}
	
}
