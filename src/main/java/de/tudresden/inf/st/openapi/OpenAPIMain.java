package de.tudresden.inf.st.openapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tudresden.inf.st.openapi.ast.OpenAPIObject;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OpenAPIMain {

    /**
     * main-method, calls the set of methods to test the OpenAPI-Generator with JastAdd
     **/
    public static void main(String[] args) throws Exception {
        String fileName = "./src/main/resources/3.0/petstore.yaml";
        OpenAPIObject jastAddObject;
        SwaggerParseResult result = new OpenAPIParser().readLocation(fileName, null, null);
        OpenAPI openAPI = result.getOpenAPI();
        List<String> generatedURLs = new ArrayList<>();

        jastAddObject = OpenAPIObject.parseOpenAPI(openAPI);

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

    public void sendRandomRequests() {}

    public void sendInferredRequests(List<String> randomRequests) {}

    public void connectGET(String path){
        try {
            URL url = new URL(path);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0"); // request header

            con.setRequestMethod("GET"); // optional default is GET

            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println("connected path : " + path);
            System.out.println("HTTP status code (GET) : " + responseCode);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void connectPOST(String path){
        try {
            URL url = new URL(path);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            con.setRequestMethod("POST"); // HTTP POST
            con.setRequestProperty("User-Agent", "Mozilla/5.0"); // request header
            con.setDoOutput(true); // POST

            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println("connected path : " + path);
            System.out.println("HTTP status code (POST) : " + responseCode);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}