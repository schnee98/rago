package de.tudresden.inf.st.openapi;

import de.tudresden.inf.st.openapi.ast.OpenAPIObject;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.OpenApi3;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenAPIMain {

  /**
   * main-method, calls the set of methods to test the OpenAPI-Generator with JastAdd
   **/
  public static void main(String[] args) throws Exception {
    OpenAPIObject openApi = new OpenAPIObject();
    OpenApi3 api3;
    ValidationResults results;
    List<String> filenames = new ArrayList<>();
    String genDir = "./gen-api-ex/";
    File genDirectory = new File(genDir);
    File[] contents;

    File resource = new File("./src/main/resources");

    for (File file : resource.listFiles())
      filenames.add(file.getName());
    System.out.println(filenames.size());

        /*
        for( String file : filenames ){
            String writerName = genDir + file;

            URL expUrl = OpenAPIMain.class.getClassLoader().getResource(file);
            OpenApi3 api = new OpenApi3Parser().parse(expUrl, new ArrayList<>(), true);
            System.out.println("Loading expression DSL file '" + file + "'.");

            openApi = OpenAPIObject.parseOpenAPI(api);
            api3 = OpenAPIObject.composeOpenAPI(openApi);
            openApi.generateRequests();

        }
         */

    String fileName = "petstore-v2.yaml";
    //FileWriter writer = new FileWriter("./gen-api-ex/callback-example_generated.json");

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

    OpenApi3 api = new OpenApi3Parser().parse(expUrl, new ArrayList<>(), true);
    System.out.println("Loading expression DSL file '" + fileName + "'.");

    openApi = OpenAPIObject.parseOpenAPI(api);
    openApi.generateRequestsWithInferredParameters();

    //writer.write(api3.toNode().toPrettyString());
    //writer.close();


    Map<String, List<String>> s = new HashMap<>();

    if (args.length > 0) {
      fileName = args[0];
    }

  }
}