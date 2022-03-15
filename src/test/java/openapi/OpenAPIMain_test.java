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
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class OpenAPIMain_test {

  static List<File> resources = new ArrayList<>();

  @BeforeAll
  static void init() {
    File r = new File("./src/main/resources");
    initResources(r);
  }

  @MethodSource("getResources")
  @DisplayName("Parser Test (System Messages)")
  @ParameterizedTest(name = "Parser test for resource file ''{0}''")
  static void parserTest(File file) throws Exception {
    OpenAPIObject jastAddObject;
    OpenAPI POJOOpenAPI;
    ObjectMapper mapper = new ObjectMapper();
    List<String> validation;

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
      validation.clear();
    }
    else
      System.out.println("validated!");

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

    // compare if api (source object) is equivalent to api3 (generated object)
    compareJson(expectedNode, actualNode, Paths.get(file.getPath()));
  }

  static Stream<File> getResources() {
    return resources.stream();
  }

  static void compareJson(JsonNode expectedNode, JsonNode actualNode, Path path) throws IOException {
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
        if ( !s.contains("['") && isNumeric(s) && Integer.parseInt(s) < 200)
          result = result.concat("[" + s + "].");
        else
          result = result.concat(s + ".");
      }
      pathNode = result.substring(0, result.length()-1);

      // check, if this node is null or has an empty value.
      if (JsonPath.parse(expectedNode.toString()).read(pathNode, String.class) == null || JsonPath.parse(expectedNode.toString()).read(pathNode, String.class).isEmpty())
        ((ArrayNode) diff).remove(i);
      else if (JsonPath.parse(actualNode.toString()).read(pathNode, String.class) == null || JsonPath.parse(actualNode.toString()).read(pathNode, String.class).isEmpty())
        ((ArrayNode) diff).remove(i);

      result = "";
    }

    // if the Jsons are equivalent, there is no reason to to the text comparison.
    // if there is a difference, a text comparison might look better than just the diff.
    if (diff.size() != 0) {
      Assertions.assertEquals(actualNode.toPrettyString(), expectedNode.toPrettyString(), "JSONs for " + path + " are different:\n" + diff.toPrettyString());
    }
  }

  static boolean isNumeric(String str) {
    try {
      int d = Integer.parseInt(str);
    } catch (NumberFormatException nfe) {
      return false;
    }
    return true;
  }

  static void initResources(File file) {
    if ( file.isDirectory() ) {
      for ( File f : file.listFiles() )
        initResources(f);
    } else if ( file.isFile() && file.getPath().contains("yaml") )
      resources.add(file);
  }
}