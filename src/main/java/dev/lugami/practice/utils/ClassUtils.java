package dev.lugami.practice.utils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassUtils {

    /**
     * Gets all classes in a given package.
     * If nothing is found, it throws ClassNotFoundException.
     * If the package doesn't exist, it throws IOException.
     *
     * @param packageName The package name to look for classes.
     * @return the list of classes
     * @throws ClassNotFoundException if a class cannot be located
     * @throws IOException if an I/O error occurs
     */
    public static List<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        List<Class<?>> classes = new ArrayList<>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                dirs.add(new File(resource.getFile()));
            } else if (resource.getProtocol().equals("jar")) {
                JarURLConnection jarConn = (JarURLConnection) resource.openConnection();
                JarFile jar = jarConn.getJarFile();
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entryName.startsWith(path) && entryName.endsWith(".class") && !entry.isDirectory()) {
                        String className = entryName.substring(0, entryName.length() - 6).replace('/', '.');
                        classes.add(Class.forName(className));
                    }
                }
            }
        }

        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }

        return classes;
    }

    /**
     * Gets all classes in a given package from a specified JAR file.
     *
     * @param file the JAR file to search
     * @param pkgname the package name to look for classes
     * @return the list of classes
     */
    public static List<Class<?>> getClasses(File file, String pkgname) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        try {
            String relPath = pkgname.replace('.', '/');
            try (JarFile jarFile = new JarFile(file)) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entryName.endsWith(".class")
                        && entryName.startsWith(relPath)
                        && entryName.length() > (relPath.length() + "/".length())) {
                        String className = entryName.replace('/', '.').replace('\\', '.');
                        if (className.endsWith(".class"))
                            className = className.substring(0, className.length()-6);
                        Class<?> c = loadClass(className);
                        if (c != null)
                            classes.add(c);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unexpected IOException reading JAR File '" + file.getAbsolutePath() + "'", e);
        }

        return classes;
    }

    /**
     * Recursively finds all classes in a given directory.
     *
     * @param directory the directory to search
     * @param packageName the package name for the classes
     * @return the list of classes
     * @throws ClassNotFoundException if a class cannot be located
     */
    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    assert !file.getName().contains(".");
                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                }
            }
        }
        return classes;
    }

    /**
     * Loads a class by its fully qualified name.
     *
     * @param className the fully qualified name of the class
     * @return the loaded class
     */
    public static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unexpected ClassNotFoundException loading class '" + className + "'");
        } catch (NoClassDefFoundError e) {
            return null;
        }
    }

    /**
     * Processes a JAR file and loads all classes contained in it.
     *
     * @param jarFile the JAR file to process
     * @return the list of loaded classes
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if a class cannot be located
     */
    public static List<Class<?>> processJarFile(File jarFile) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            URL[] urls = {new URL("jar:file:" + jarFile + "!/")};
            URLClassLoader classLoader = URLClassLoader.newInstance(urls);
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class") && !entry.isDirectory()) {
                    String className = entry.getName().replace('/', '.').replace(".class", "");
                    classes.add(Class.forName(className, true, classLoader));
                }
            }
        }
        return classes;
    }
}
