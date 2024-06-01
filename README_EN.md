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
  

Example of [Parameter Object](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#parameterObject) implementation
``` 
  abstract ParameterOb;
  ParameterReference : ParameterOb ::= <Ref> ...;
  ParameterObject : ParameterOb ::= <Name> <In> ...;
```


- Most objects can be extended with `Extension` containing unfixed name and values. In JastAdd, this is also implemented in a tuple (AST-Node) `Extension ::= <Key> <Value:Object>;`

## Fuzzing in RAGO

To generate API tests automatically, `RAGO` supports two following techniques based on Fuzzing which involves providing invalid, unexpected, or random data as inputs to an API.

In this first version, this tool considers only `GET` request type and query parameters. It means that `RAGO` currently generates only URLs.

### <a name="ragoRandTest"></a>Random Testing

Random testing is based on the simple randomizer, `RandomRequestGenerator`. This generator reads an `OpenAPIObject` mapped by an OpenAPI documentation and checks all parameter types of operations existing in the `OpenAPIObject`.


Afterwards, `RandomRequestGenerator` knows which request needs which parameter type and generates random parameters for all requests and URLs appending these parameters.


### <a name="ragoParamInf"></a>Parameter Inference

Most tests based on the simple randomizing techniques are not effective because of low coverages and unusual validations. To observe more concrete test results, `InferParameter` generates tests automatically using random test results of `RandomRequestGenerator`.


Test results (responses) generated by `RandomRequestGenerator` are saved in a dictionary and utilized to infer parameters that might be usable in other requests.


If there is a same schema set in a request and a response, parameters of them are inferred by following strategies:

- Case-insensitive
- Id completion in a field name (e.g. if a property has a name "id", it gets an additional field name available in the specification)


** Notice ** : Parameter Inference is an experimental fuzzing approach inspired by the 
Specification-based Approach[^1] and RESTTESTGEN[^2]


## Bibliography

[^1]: Hamza Ed-Douibi, Javier Luis Cánovas Izquierdo, and Jordi Cabot. “Automatic generation of test cases for REST APIs: A specification-based approach”. In: 2018 IEEE 22nd international enterprise distributed object computing conference (EDOC). IEEE. 2018, pp. 181–190


[^2]: Emanuele Viglianisi, Michael Dallago, and Mariano Ceccato. “RestTestGen: automated black-box testing of RESTful APIs”. In: 2020 IEEE 13th International Conference on Software Testing, Validation and Verification (ICST). IEEE. 2020, pp. 142–152