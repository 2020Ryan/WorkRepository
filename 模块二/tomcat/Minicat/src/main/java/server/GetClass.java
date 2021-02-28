package server;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GetClass extends ClassLoader {
    /*@OverrideMyClassLoader extends ClassLoader
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String myPath = "E://"+name;
        //String myPath = "file://" + appBase + name.replace(".","/") + ".class";
        System.out.println(myPath);

        byte[] classBytes = null;
        Path path = null;
        try {
            path = Paths.get(new URI(name));
            classBytes = Files.readAllBytes(path);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        Class clazz = defineClass(name, classBytes, 0, classBytes.length);
        return clazz;

    }*/

    protected Class<?> findClass(String appBase, String name) {
        String myPath =appBase + name.replace(".","\\") + ".class";
        System.out.println(myPath);
        String newPath = myPath.replace("\\","%2F");
        System.out.println(newPath);
        //System.out.println(newPath);
        byte[] classBytes = null;
        Path path = null;
        try {
            File file = new File(myPath);
            path = file.toPath();
            //path = Paths.get(new URI(newPath));
            classBytes = Files.readAllBytes(path);
        } catch (IOException  e) {
            e.printStackTrace();
        }
        Class clazz = defineClass(name, classBytes, 0, classBytes.length);
        return clazz;
    }

}
