# RAGO - RAG OpenAPI Framework

## Introduction

`RAGO` is the first REST API fuzzing tool modeled in [JastAdd](https://jastadd.cs.lth.se/).


First of all, This tool parses [an OpenAPI specification (OAS)](https://swagger.io/specification/) in Java classes and transfers it into `AST-Node`. These Nodes are able to be re-transformed and saved in JSON which describes its API Specification, if needed.

![](img/parser.png)


After the parsing phase, `RAGO` is prepared to generate tests automatically in two different ways based on Fuzzing, [Random Testing](#ragoRandTest) and [Parameter Inference](#ragoParamInf).

## OpenAPI Specification in JastAdd

[OpenAPI specification (OAS)](https://swagger.io/specification/) defines a standard, language-agnostic interface to RESTful APIs which allows both humans and computers and is represented in JSON or YAML. It consists of 30 objects and is structured in a tree-shape.


To understand better how OpenAPI documentations could be transformed in JastAdd, compare the following Object in the JastAdd-grammar and [OpenAPI Object description](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#oasObject)
```
OpenAPIObject ::=   <OpenAPI> <JsonSchemaDialect> I:InfoObject Serv:ServerObject*
                    P:PathsObject* W:Webhook* C:ComponentsObject Sr:SecurityRequirementObject*
                    T:TagObject* [E:ExternalDocObject] Ex:Extension*;
```
![](img/openapi.png)


There are some implementation details developers must consider:

- JastAdd doesn't support `Map`. So, it must be constructed in a tuple (AST-Node). e.g.
  - `ServerVariablesTuple ::= <Name> S:ServerVariableObject;` 
  - `variables` in [Server Object](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#serverObject)

- In OAS, several objects can be replaced by [Reference Object](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#referenceObject). In `RAGO`, we implemented this structure in an abstract node to every concerned object. e.g. 
    - [Parameter Object](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#parameterObject)
    - following abstract node in JastAdd

  ``` 
  abstract ParameterOb;
  ParameterReference : ParameterOb ::= <Ref> ...;
  ParameterObject : ParameterOb ::= <Name> <In> ...;
  ```

- Most objects can be extended with `Extension` containing unfixed name and values. In JastAdd, this is also implemented in a tuple (AST-Node) `Extension ::= <Key> <Value:Object>;`

## Fuzzing Methods

### <a name="ragoRandTest"></a>Random Testing

### <a name="ragoParamInf"></a>Parameter Inference