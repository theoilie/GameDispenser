package net.galaxygaming.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import net.galaxygaming.dispenser.event.EventManager;

public class FileResourceLoader extends ClassLoader {

	private final transient File directory;
	
	public FileResourceLoader(final ClassLoader classLoader, final File directory) {
		super(classLoader);
		this.directory = directory;
	}
	
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