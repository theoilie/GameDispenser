package net.galaxygaming.dispenser.game;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.bukkit.plugin.InvalidDescriptionException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class GameDescriptionFile {
    private static final Yaml yaml = new Yaml(new SafeConstructor());
    private String name;
    private String main;
    private String version;
    private List<String> authors;
    private List<String> depend;
    
    public GameDescriptionFile(final InputStream stream) throws InvalidDescriptionException {
        loadMap((Map<?, ?>) yaml.load(stream));
    }
    
    /**
     * Gives the name of the game
     * @return the name of the game
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gives the fully qualified name for the main class of the game
     * @return the fully qualified main class for the game
     */
    public String getMain() {
        return main;
    }
    
    /**
     * Gives the version of the game
     * @return the version of the game
     */
    public String getVersion() {
        return version;
    }
    
    /**
     * Gives the name of a game, including the version
     * @return the name of the game and respective version
     */
    public String getFullName() {
        return name + " v" + version;
    }
    
    /**
     * Gives a list of authors of the game
     * @return an immutable list of the game's authors
     */
    public List<String> getAuthors() {
        return authors;
    }
    
    /**
     * Gives a list of other games this game requires
     * @return an immutable list of this game's dependencies
     */
    public List<String> getDepend() {
        return depend == null ? Lists.<String>newArrayList() : depend;
    }
    
    private void loadMap(Map<?, ?> map) throws InvalidDescriptionException {
        try {
            name = map.get("name").toString();
            
            if (!name.matches("^[A-Za-z0-9 _.-]+$")) {
                throw new InvalidDescriptionException("name '" + name + "' contains invalid characters.");
            }
        } catch (NullPointerException e) {
            throw new InvalidDescriptionException(e, "name is not defined");
        } catch (ClassCastException e) {
            throw new InvalidDescriptionException(e, "name is of wrong type");
        }
                
        try {
            version = map.get("version").toString();
        } catch (NullPointerException e) {
            throw new InvalidDescriptionException(e, "version is not defined");
        } catch (ClassCastException e) {
            throw new InvalidDescriptionException(e, "version is of wrong type");
        }
        
        try {
            main = map.get("main").toString();
        } catch (NullPointerException e) {
            throw new InvalidDescriptionException(e, "main is not defined");
        } catch (ClassCastException e) {
            throw new InvalidDescriptionException(e, "main is of wrong type");
        }
        
        if (map.get("depend") != null) {
            ImmutableList.Builder<String> dependBuilder = ImmutableList.<String>builder();
            try {
                for (Object dependency : (Iterable<?>) map.get("depend")) {
                    dependBuilder.add(dependency.toString());
                }
            } catch (ClassCastException e) {
                throw new InvalidDescriptionException(e, "depend is of wrong type");
            } catch (NullPointerException e) {
                throw new InvalidDescriptionException(e, "invalid dependency format");
            }
            depend = dependBuilder.build();
        }
        
        if (map.get("authors") != null) {
            ImmutableList.Builder<String> authorsBuilder = ImmutableList.<String>builder();
            if (map.get("author") != null) {
                authorsBuilder.add(map.get("author").toString());
            }
            
            try {
                for (Object dependency : (Iterable<?>) map.get("depend")) {
                    authorsBuilder.add(dependency.toString());
                }
            } catch (ClassCastException e) {
                throw new InvalidDescriptionException(e, "depend is of wrong type");
            } catch (NullPointerException e) {
                throw new InvalidDescriptionException(e, "invalid dependency format");
            }
            depend = authorsBuilder.build();
        }
    }   
}