# RAGO - RAG OpenAPI Framework

## Introduction

`RAGO` is the first REST API fuzzing tool modeled in [JastAdd](https://jastadd.cs.lth.se/).


First of all, This tool parses [an OpenAPI specification (OAS)](https://swagger.io/specification/) in Java classes and transfers it into JastAdd Objects. These Objects are able to be re-transformed and saved in JSON which describes its API Specification, if needed.

![](img/parser.png)


After the parsing phase, `RAGO` is prepared to generate tests automatically in two different ways based on Fuzzing, `Random Testing` and `Parameter Inference`.

## OpenAPI Specification in JastAdd

[OpenAPI specification (OAS)](https://swagger.io/specification/) defines a standard, language-agnostic interface to RESTful APIs which allows both humans and computers and is represented in JSON or YAML. Consequently, it is able to use OAS in Java, following in JastAdd.


To understand better how OpenAPI documentations could be transformed in JastAdd, compare the following part of the JastAdd-grammar and [OpenAPI Object description](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#oasObject)
```
OpenAPIObject ::=   <OpenAPI> <JsonSchemaDialect> I:InfoObject Serv:ServerObject*
                    P:PathsObject* W:Webhook* C:ComponentsObject Sr:SecurityRequirementObject*
                    T:TagObject* [E:ExternalDocObject] Ex:Extension*;
```
![](img/openapi.png)

## Fuzzing Methods

### Random Testing

### Parameter Inference