package de.tudresden.inf.st.openapi;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OpenAPIMain {

  /**
   * main-method, calls the set of methods to test the OpenAPI-Generator with JastAdd
   **/
  public static void main(String[] args) throws Exception {
    List<String> filenames = new ArrayList<>();
    String genDir = "./gen-api-ex/";
    File genDirectory = new File(genDir);
    File[] contents;

    File resource = new File("./src/main/resources");


    String fileName = "./src/main/resources/3.1/basicref.yaml";



    URL expUrl = OpenAPIMain.class.getClassLoader().getResource(fileName);
    File file = null;
    if (expUrl != null) {
      file = new File(expUrl.getFile());
    } else {
      file = new File(fileName);
    }
    if (file == null) {
      throw new FileNotFoundException("Could not load JSON file " + fileName);
    }

    System.out.println("Loading expression DSL file '" + fileName + "'.");

    if (args.length > 0) {
      fileName = args[0];
    }

  }
}