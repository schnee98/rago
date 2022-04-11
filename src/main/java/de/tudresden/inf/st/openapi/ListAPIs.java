package de.tudresden.inf.st.openapi;

import java.io.File;

public class ListAPIs {

    public static void main (String[] args) {
        File resource = new File("./src/main/resources");
        recursive(resource);
    }

    protected static void recursive(File file){
        if ( file.isDirectory() ){
            if ( file.listFiles().length == 0 ){
                file.delete();
                return;
            }
            for ( File f : file.listFiles() )
                recursive(f);
        } else if ( file.getName().equals("swagger.yaml") )
            file.delete();
    }
}
