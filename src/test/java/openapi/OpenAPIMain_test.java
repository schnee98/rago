package openapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.flipkart.zjsonpatch.JsonDiff;
import com.jayway.jsonpath.JsonPath;
import de.tudresden.inf.st.openapi.ast.*;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class OpenAPIMain_test {

  @Test
  public void test() throws Exception {
    OpenAPIObject jastAddObject;
    OpenAPI POJOOpenAPI;
    ObjectMapper mapper = new ObjectMapper();
    List<String> validation;
    List<String> filenames = new ArrayList<>();
    String genDir = "./src/test/apiGen/";
    File genDirectory = new File(genDir);
    File[] contents;

    File resource = new File("./src/main/resources");

    recursiveTest(resource);
  }

  protected static void compareJson(JsonNode expectedNode, JsonNode actualNode, Path path) throws IOException {
    JsonNode diff = JsonDiff.asJson(expectedNode, actualNode);
    String pathNode;
    String result = "";

    for (int i = diff.size() - 1; i >= 0; i--) {
      // get the path of a node involving difference.
      pathNode = "$" + diff.get(i).get("path").toString();
      for (String s : pathNode.split("/")) {
        if (s.contains("."))
          pathNode = pathNode.replace(s, "['" + s + "']");
        else if (s.contains(" "))
          pathNode = pathNode.replace(s, "['" + s + "']");
      }
      pathNode = pathNode
          .replace("/", ".")
          .replace("~1", "/")
          .replace("\"", "");
      for (String s : pathNode.split("\\.")) {
        if ( !s.contains("['") && isNumeric(s) && Integer.parseInt(s) < 100)
          result = result.concat("[" + s + "].");
        else
          result = result.concat(s + ".");
      }

      pathNode = result.substring(0, result.length()-1);
      //System.out.println(JsonPath.parse(expectedNode.toString()).read(pathNode, String.class));
      //System.out.println(JsonPath.parse(actualNode.toString()).read(pathNode, String.class));
      System.out.println(pathNode);
      // check, if this node exists or has an empty value.
      if (JsonPath.parse(expectedNode.toString()).read(pathNode, String.class) == null || JsonPath.parse(expectedNode.toString()).read(pathNode, String.class).isEmpty())
        ((ArrayNode) diff).remove(i);
      else if (JsonPath.parse(actualNode.toString()).read(pathNode, String.class) == null || JsonPath.parse(actualNode.toString()).read(pathNode, String.class).isEmpty())
        ((ArrayNode) diff).remove(i);
      else if (!JsonPath.parse(actualNode.toString()).read(pathNode.substring(0, pathNode.lastIndexOf(".")).concat(".$ref"), String.class).isEmpty())
        ((ArrayNode) diff).remove(i);

      result = "";
    }

    // if the Jsons are equivalent, there is no reason to to the text comparison.
    // if there is a difference, a text comparison might look better than just the diff.
    if (diff.size() != 0) {
      Assertions.assertEquals(actualNode.toPrettyString(), expectedNode.toPrettyString(), "JSONs for " + path + " are different:\n" + diff.toPrettyString());
    }
  }

  protected static boolean isNumeric(String str) {
    try {
      int d = Integer.parseInt(str);
    } catch (NumberFormatException nfe) {
      return false;
    }
    return true;
  }

  protected static void recursiveTest(File file) throws Exception {
    if ( file.isDirectory() ) {
      for ( File f : file.listFiles() )
        recursiveTest(f);
    } else if ( file.isFile() && file.getPath().contains("yaml") ) {
      OpenAPIObject jastAddObject;
      OpenAPI POJOOpenAPI;
      ObjectMapper mapper = new ObjectMapper();
      List<String> validation;
      /*
      String writerName = genDir + file;
      writerName = writerName.substring(0, writerName.length() - 5);
      FileWriter expectedWriter = new FileWriter(writerName + "-expected.json");
      FileWriter actualWriter = new FileWriter(writerName + "-actual.json");
       */

      // parsed openAPI object with swagger-parser
      SwaggerParseResult result = new OpenAPIParser().readLocation(file.getPath(), null, null);
      POJOOpenAPI = result.getOpenAPI();
      System.out.println("Loading expression DSL file '" + file + "'.");


      // validation of OpenAPI in POJO
      JsonNode expectedNode = mapper.readTree(Json.mapper().writeValueAsString(POJOOpenAPI));
      validation = new OpenAPIV3Parser().readContents(expectedNode.toString()).getMessages();
      if ( validation.size() != 0 ) {
        System.out.println("validation failed!");
        for ( String s : validation )
          System.out.println(s);
      }
      else
        System.out.println("validated!");

      // save expected object
      //expectedWriter.write(expectedNode.toPrettyString());
      //expectedWriter.close();

      // OpenAPI in POJO to OpenAPI in JastAdd
      jastAddObject = OpenAPIObject.parseOpenAPI(POJOOpenAPI);

      // OpenAPI in JastAdd to OpenAPI in POJO
      OpenAPI transformedAPI = OpenAPIObject.reverseOpenAPI(jastAddObject);

      // validation of transferred OpenAPI
      JsonNode actualNode = mapper.readTree(Json.mapper().writeValueAsString(transformedAPI));
      validation = new OpenAPIV3Parser().readContents(actualNode.toString()).getMessages();
      if ( validation.size() != 0 ) {
        System.out.println("validation failed!");
        for ( String s : validation )
          System.out.println(s);
      }
      else
        System.out.println("validated");

      // save generated object
      //actualWriter.write(actualNode.toPrettyString());
      //actualWriter.close();

      // compare if api (source object) is equivalent to api3 (generated object)
      compareJson(expectedNode, actualNode, Paths.get(file.getPath()));
    }
  }
}