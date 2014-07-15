/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.util;

import java.io.File;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author t7seven7t
 */
public abstract class I18n {
    
	public I18n() {
		throw new AssertionError("Cannot instantiate utility class.");
	}
	
    public static ResourceBundle getResourceBundle(File directory, String bundleName, Locale locale, ClassLoader classLoader) {
        try {
            return ResourceBundle.getBundle(bundleName, locale, new FileResourceLoader(classLoader, directory));
        } catch (MissingResourceException e) {
            throw new RuntimeException("Could not find resource bundle: " + bundleName + ".properties");
        }
    }
    
    public static ResourceBundle getResourceBundle(File directory, String bundleName, ClassLoader classLoader) {
        return getResourceBundle(directory, bundleName, Locale.getDefault(), classLoader);
    }   
}