package fr.atesab.act.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import net.minecraftforge.fmllegacy.packs.ResourcePackLoader;

/**
 * A set of tools to help
 * 
 * @author ATE47
 * @since 2.5.0
 */
public class ACTUtils {
    /**
     * check if a number is positive and return it
     * 
     * @param value the number to check
     * @param name  the name of the variable
     * @return the number
     * @throws IllegalArgumentException if value < 0
     */
    public static int positive(int value, String name) {
        if (value < 0)
            throw new IllegalArgumentException(name + " must be positive!");
        return value;
    }

    /**
     * check if a number is stricly positive and return it
     * 
     * @param value the number to check
     * @param name  the name of the variable
     * @return the number
     * @throws IllegalArgumentException if value <= 0
     */
    public static int positiveStrict(int value, String name) {
        if (value <= 0)
            throw new IllegalArgumentException(name + " must be positive!");
        return value;
    }

    /**
     * check if a number is negative and return it
     * 
     * @param value the number to check
     * @param name  the name of the variable
     * @return the number
     * @throws IllegalArgumentException if value > 0
     */
    public static int negative(int value, String name) {
        if (value > 0)
            throw new IllegalArgumentException(name + " must be negative!");
        return value;
    }

    /**
     * check if a number is stricly negative and return it
     * 
     * @param value the number to check
     * @param name  the name of the variable
     * @return the number
     * @throws IllegalArgumentException if value >= 0
     */
    public static int negativeStrict(int value, String name) {
        if (value >= 0)
            throw new IllegalArgumentException(name + " must be negative!");
        return value;
    }

    /**
     * modulus, if a > 0 mod(a,b) = a%b
     * 
     * @param a a
     * @param b b
     * @return mod(a,b)
     */
    public static int mod(int a, int b) {
        return ((a % b) + b) % b;
    }

    /**
     * apply the consumer and return t
     * 
     * @param <T>    t type
     * @param t      t
     * @param action the consumer to apply
     * @return t
     */
    public static <T> T applyAndGet(T t, Consumer<T> action) {
        action.accept(t);
        return t;
    }

    /**
     * get a stream from a file in a mod jar
     * 
     * @param modId the mod id
     * @param path  the path in the jar
     * @return the stream
     * @throws IOException
     */
    public static InputStream fetchFromModJar(String modId, String path) throws IOException {
        var pack = ResourcePackLoader.getResourcePackFor(modId)
                .orElseThrow(() -> new RuntimeException("Can't find modid " + modId));
        return pack.getRootResource(path);
    }

    private ACTUtils() {
    }
}
