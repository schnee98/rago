# RAGO - RAG OpenAPI Framework

## Introduction

`RAGO` is the first REST API fuzzing tool modeled in [JastAdd](https://jastadd.cs.lth.se/).


First of all, This tool parses [an OpenAPI specification (Swagger)](https://swagger.io/specification/) in Java classes and transfers it into JastAdd Objects. These Objects are able to be re-transformed and saved in JSON which describes its API Specification, if needed.

![](img/parser.svg)


After a parsing phase, `RAGO` is prepared to generate tests automatically in two different ways based on Fuzzing, `Random Testing` and `Parameter Inference`

## OpenAPI in RAG

- Short mention of RAG
- Short mention of OpenAPI
- UML for OpenAPIObject
- Relast for OpenAPIObject

## Fuzzing Methods

### Random Testing

### Parameter Inference