package de.tudresden.inf.st.openapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.reader.SwaggerParser;
import io.swagger.parser.OpenAPIParser;
import io.swagger.report.MessageBuilder;
import io.swagger.util.Yaml;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import io.swagger.v3.parser.util.OpenAPIDeserializer;
import io.swagger.validate.ApiDeclarationJsonValidator;
import io.swagger.validate.SwaggerSchemaValidator;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

        // init parser
        String fileName = "./src/main/resources/3.0/petstore.yaml";
        ParseOptions options = new ParseOptions();
        options.setResolve(true);
        options.setResolveFully(true);
        options.setAllowEmptyString(false);
        SwaggerParseResult result = new OpenAPIParser().readLocation(fileName, null, null);

        String resultString;
        //SwaggerSchemaValidator
        OpenAPI openAPI = result.getOpenAPI();

        /*
        ApiDeclarationJsonValidator validator = new ApiDeclarationJsonValidator();
        validator.validate(new MessageBuilder(), Json.mapper().writerWithDefaultPrettyPrinter().writeValueAsString(openAPI));
        Json.mapper().writerWithDefaultPrettyPrinter().write
         */
        OpenAPIDeserializer deserializer = new OpenAPIDeserializer();
        String res = Json.mapper().writeValueAsString(openAPI);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(res);
        //System.out.println(node.toPrettyString());


        /** OpenAPI Validator! **/
        List<String> validation = new OpenAPIV3Parser().readContents(res).getMessages();
        for(String mes : validation)
            System.out.println("message : " + mes);


        //Json.mapper().createParser(Json.mapper().writeValueAsString(openAPI)).

        // Yaml String
        //System.out.println(Yaml.mapper().writerWithDefaultPrettyPrinter().writeValueAsString(openAPI));
        //resultString = Yaml.mapper().writerWithDefaultPrettyPrinter().writeValueAsString(openAPI);

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