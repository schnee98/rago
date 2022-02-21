package openapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.flipkart.zjsonpatch.JsonDiff;
import com.jayway.jsonpath.JsonPath;
import de.tudresden.inf.st.openapi.ast.OpenAPIObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.OpenApi3;

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
    OpenAPIObject openApi = new OpenAPIObject();
    OpenApi3 api3;
    ValidationResults results;
    List<String> filenames = new ArrayList<>();
    String genDir = "./src/test/apiGen/";
    File genDirectory = new File(genDir);
    File[] contents;

    File resource = new File("./src/main/resources");

    for (File file : resource.listFiles())
      filenames.add(file.getName());
    System.out.println(filenames.size());

    for (String file : filenames) {
      String writerName = genDir + file;
      FileWriter expectedWriter = new FileWriter((writerName.substring(0, writerName.length() - 5) + "-expected.json"));
      FileWriter actualWriter = new FileWriter((writerName.substring(0, writerName.length() - 5) + "-actual.json"));
      URL expUrl = OpenAPIMain_test.class.getClassLoader().getResource(file);

      // parsed openAPI object with openapi4j
      OpenApi3 api = new OpenApi3Parser().parse(expUrl, new ArrayList<>(), true);
      System.out.println("Loading expression DSL file '" + file + "'.");
      // save expected object
      expectedWriter.write(api.toNode().toPrettyString());
      expectedWriter.close();

      //results = OpenApi3Validator.instance().validate(api);
      //System.out.println(results.isValid());

      // openAPI object is integrated in JastAdd grammar
      openApi = openApi.parseOpenAPI(api);
      System.out.println(openApi.getPathsObject(0).getPathItemObject().getPost().getOperationObject().getResponseTuple(0).getResponseOb().responseObject().getContentTuple(0).getMediaTypeObject().getSchemaOb().getClass().getName());

      //Map<ResponseObject, String> map = openApi.generateRequests();

      // composed openAPI object, it is expected to be equivalent to parsed source object
      api3 = OpenAPIObject.composeOpenAPI(openApi);

      // check, if the composed openAPI object is valid
      //results = OpenApi3Validator.instance().validate(api3);
      //System.out.println(results.isValid());

      //System.out.println(api.toNode().equals(api3.toNode()));

      // save generated object
      actualWriter.write(api3.toNode().toPrettyString());
      actualWriter.close();

      // compare if api (source object) is equivalent to api3 (generated object)
      compareJson(api3.toNode(), api.toNode(), Paths.get(file));
    }

    // clean all generated jsons
    contents = genDirectory.listFiles();
    if (contents != null) {
      for (File file : contents)
        file.delete();
    }
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