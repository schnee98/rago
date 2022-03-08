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
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import de.tudresden.inf.st.jastadd.dumpAst.ast.Dumper;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class OpenAPIMain_test {

  protected static boolean isNumeric(String str) {
    try {
      int d = Integer.parseInt(str);
    } catch (NumberFormatException nfe) {
      return false;
    }
    return true;
  }

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

    File resource = new File("./src/main/resources/3.0");

    for (File file : resource.listFiles())
      filenames.add(file.getName());
    System.out.println(filenames.size());

    for (String file : filenames) {
      String writerName = genDir + file;
      writerName = writerName.substring(0, writerName.length() - 5);
      FileWriter expectedWriter = new FileWriter(writerName + "-expected.json");
      FileWriter actualWriter = new FileWriter(writerName + "-actual.json");

      // parsed openAPI object with swagger-parser
      SwaggerParseResult result = new OpenAPIParser().readLocation(resource.getPath() + "/" + file, null, null);
      POJOOpenAPI = result.getOpenAPI();
      System.out.println("Loading expression DSL file '" + file + "'.");


      // validation of OpenAPI in POJO
      JsonNode expectedNode = mapper.readTree(Json.mapper().writeValueAsString(POJOOpenAPI));
      validation = new OpenAPIV3Parser().readContents(expectedNode.toString()).getMessages();
      if ( validation.size() != 0 )
        System.out.println("validation failed!");
      else
        System.out.println("validated!");

      // save expected object
      expectedWriter.write(expectedNode.toPrettyString());
      expectedWriter.close();

      // OpenAPI in POJO to OpenAPI in JastAdd
      jastAddObject = OpenAPIObject.parseOpenAPI(POJOOpenAPI);
      Dumper.read(jastAddObject).dumpAsPNG(Paths.get(writerName + ".png"));

      // OpenAPI in JastAdd to OpenAPI in POJO
      OpenAPI transformedAPI = OpenAPIObject.reverseOpenAPI(jastAddObject);

      // validation of transferred OpenAPI
      JsonNode actualNode = mapper.readTree(Json.mapper().writeValueAsString(transformedAPI));
      validation = new OpenAPIV3Parser().readContents(actualNode.toString()).getMessages();
      if ( validation.size() != 0 )
        System.out.println("validation failed!");
      else
        System.out.println("validated");

      // save generated object
      actualWriter.write(actualNode.toPrettyString());
      actualWriter.close();

      // compare if api (source object) is equivalent to api3 (generated object)
      compareJson(expectedNode, actualNode, Paths.get(file));
    }

    // clean all generated jsons
    /*
    contents = genDirectory.listFiles();
    if (contents != null) {
      for (File file : contents)
      file.delete();
    }
     */
  }

  protected void compareJson(JsonNode expectedNode, JsonNode actualNode, Path path) throws IOException {
    JsonNode diff = JsonDiff.asJson(expectedNode, actualNode);
    String pathNode;

    for (int i = diff.size() - 1; i >= 0; i--) {
      // get the path of a node involving difference.
      pathNode = "$" + diff.get(i).get("path").toString()
          .replace("/", ".")
          .replace("~1", "/")
          .replace("\"", "");
      for (String s : pathNode.split("\\.")) {
        if (isNumeric(s) && Integer.parseInt(s) < 100)
          pathNode = pathNode.replace(s, "[" + s + "]");
      }


      // check, if this node exists or has an empty value.
      System.out.println("1");
      System.out.println(pathNode);
      if (JsonPath.parse(actualNode.toString()).read(pathNode, String.class) == null || JsonPath.parse(actualNode.toString()).read(pathNode, String.class).isEmpty())
        ((ArrayNode) diff).remove(i);
      else if (!JsonPath.parse(actualNode.toString()).read(pathNode.substring(0, pathNode.lastIndexOf(".")).concat(".$ref"), String.class).isEmpty())
        ((ArrayNode) diff).remove(i);
    }

    // if the Jsons are equivalent, there is no reason to to the text comparison.
    // if there is a difference, a text comparison might look better than just the diff.
    if (diff.size() != 0) {
      Assertions.assertEquals(actualNode.toPrettyString(), expectedNode.toPrettyString(), "JSONs for " + path + " are different:\n" + diff.toPrettyString());
    }
  }
}