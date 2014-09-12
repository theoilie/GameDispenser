package net.galaxygaming.dispenser.game.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {

    /**
     * The name of this component as it
     * will appear in the config and to 
     * users using the set component 
     * command
     * @return name
     */
    String name() default "";
    
    /**
     * Flag whether to ignore this component
     * when checking if the game is setup
     * @return whether or not to ignore this when checking if the game is setup
     */
    boolean ignoreSetup() default false;
}