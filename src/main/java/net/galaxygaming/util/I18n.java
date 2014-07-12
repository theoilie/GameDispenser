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
    
    public static ResourceBundle getResourceBundle(File directory, String bundleName, ClassLoader classLoader) {
        try {
            return ResourceBundle.getBundle(bundleName, Locale.getDefault(), new FileResourceLoader(classLoader, directory));
        } catch (MissingResourceException e) {
            throw new RuntimeException("Could not find resource bundle: " + bundleName + ".properties");
        }
    }
    
}
