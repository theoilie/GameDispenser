/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game;

/**
 * @author t7seven7t
 */
public class GameType {
    
    private final String name;
    public GameType(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        
        if (getClass() == o.getClass()) {            
            if (this.name.equalsIgnoreCase(((GameType) o).name)) {
                return true;
            }
        } else if (o.getClass() == String.class) {
            if (this.name.equalsIgnoreCase((String) o)) {
                return true;
            }
        }
        return false;
    }
}