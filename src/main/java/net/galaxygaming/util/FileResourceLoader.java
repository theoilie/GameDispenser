package net.galaxygaming.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import net.galaxygaming.dispenser.event.EventManager;

/**
 * File resource loader will attempt to load a resource
 * firstly from a file in the directory specified. If it
 * fails to find the file it will check inside of the jar
 * file managed by the given class loader for the resource.
 */
public class FileResourceLoader extends ClassLoader {

	private final transient File directory;
	
	/**
	 * @param classLoader class loader for jar fall back
	 * @param directory directory to check for resource
	 */
	public FileResourceLoader(final ClassLoader classLoader, final File directory) {
		super(classLoader);
		this.directory = directory;
	}
	
	/**
	 * Retrieves a resource from the path given in the 
	 * directory this loader was initialized with. Upon
	 * failure this method will check the class loader
	 * for the resource.
	 */
	@Override
	public URL getResource(final String string) {
	    if (directory != null) {
	        final File file = new File(directory, string);
	        if (file.exists()) {
	            try {
	                return file.toURI().toURL();
	            } catch (MalformedURLException ex) {
	                // Nothing...
	            }
	        }
	    }
		return super.getResource(string);
	}
	
	/**
	 * Retrieves a resource as a stream from the path given 
	 * in the directory this loader was initialized with. 
	 * Upon failure this method will check the class loader
     * for the resource.
	 */
	@Override
	public InputStream getResourceAsStream(final String string) {
	    if (directory != null) {
	        final File file = new File(directory, string);
	        if (file.exists()) {
	            try {
	                return new FileInputStream(file);
	            } catch (FileNotFoundException ex) {
	                // Do nothing...
	            }
	        }
	    }
		return super.getResourceAsStream(string);
	}
	
    /**
     * Cloning is not supported.
     */
    @Override
    public EventManager clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}