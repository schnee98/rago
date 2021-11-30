# JastAdd in OpenAPI Parser

This is a documentation of differences between the document of [OpenAPI Specification in version 3.0.2](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.2.md) and [OpenAPI parser for Java projects](https://github.com/openapi4j/openapi4j).

## Differences

Object Name | Description
---|:---:
[OpenAPI Object](#openAPI)|variable "path" is defined differently ( in spec. as one object and in parser as map )
[Paths Object](#paths)|see OpenAPI Object
[Path Item Object](#pathItem)|variables in type "Operation Object" are defined the part of a map in openapi parser
[Operation Object](#operation)|Responses Object is defined as a map in openapi parser
[Parameter Object](#parameter)|allowEmptyValue does not exist in openapi parser
[Encoding Object](#encoding)|variable "allowReserved" does not exist in openapi parser
[Responses Object](#responses)|see Operation Object
[Callback Object](#callback)|openapi parser has expressions as a map
[Link Object](#link)|openapi parser has variable "headers" instead of variable "requestBody"
[Reference Object](#reference)|no Reference Object in openapi parser
[OAuth Flow Object](#oAuthFlow)|openapi parser has new variable "configuration"

## Contents

### <a name="openAPI"></a>OpenAPI Object

- variable "path" is defined differently ( in spec. as one object and in parser as map )
- Links to compare
  - [In OpenAPI Specification](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.2.md#oasObject)
  - [In openapi parser](https://github.com/openapi4j/openapi4j/blob/master/openapi-parser/src/main/java/org/openapi4j/parser/model/v3/OpenApi3.java)  


### Info Object

- no differences

### Contact Object

- no differences

### License Object

- no differences

### Server Object

- no differences

### Server Variable Object

- no differences

### Components Object

- no differences

### <a name="paths"></a>Paths Object

- see OpenAPI Object

### <a name="pathItem"></a>Path Item Object

- variables in type "Operation Object" are defined the part of a map in openapi parser
- Links to compare
  - [In OpenAPI Specification](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.2.md#pathItemObject)
  - [In openapi parser](https://github.com/openapi4j/openapi4j/blob/master/openapi-parser/src/main/java/org/openapi4j/parser/model/v3/Path.java)

### <a name="operation"></a>Operation Object

- Responses Object is defined as a map in openapi parser
- Links to compare
    - [In OpenAPI Specification](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.2.md#operationObject)
    - [In openapi parser](https://github.com/openapi4j/openapi4j/blob/master/openapi-parser/src/main/java/org/openapi4j/parser/model/v3/Operation.java)
    
### External Documentation Object

- no differences

### <a name="parameter"></a>Parameter Object

- allowEmptyValue does not exist in openapi parser
- Links to compare
    - [In OpenAPI Specification](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.2.md#parameterObject)
    - [In openapi parser](https://github.com/openapi4j/openapi4j/blob/master/openapi-parser/src/main/java/org/openapi4j/parser/model/v3/Parameter.java)

### Request Body Object

- no differences

### Media Type Object

- no differences

### <a name="encoding"></a>Encoding Object

- variable "allowReserved" does not exist in openapi parser
- Links to compare
    - [In OpenAPI Specification](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.2.md#encodingObject)
    - [In openapi parser](https://github.com/openapi4j/openapi4j/blob/master/openapi-parser/src/main/java/org/openapi4j/parser/model/v3/EncodingProperty.java)

### Responses Object

- see Operation Object

### Response Object

- no differences

### <a name="callback"></a>Callback Object

- openapi parser has expressions as a map
- Links to compare
    - [In OpenAPI Specification](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.2.md#callbackObject)
    - [In openapi parser](https://github.com/openapi4j/openapi4j/blob/master/openapi-parser/src/main/java/org/openapi4j/parser/model/v3/Callback.java)

### Example Object

- no differences

### <a name="link"></a>Link Object

- openapi parser has variable "headers" instead of variable "requestBody"
- Links to compare
    - [In OpenAPI Specification](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.2.md#linkObject)
    - [In openapi parser](https://github.com/openapi4j/openapi4j/blob/master/openapi-parser/src/main/java/org/openapi4j/parser/model/v3/Link.java)

### Header Object

- no differences

### Tag Object

- no differences

### Reference Object

- no Reference Object in openapi parser
- most objects has variable "Ref" and method getRef() or isRef()

### Schema Object

- not relevant in current JastAPI

### Discriminator Object

- not relevant in current JastAPI

### XML Object

- not relevant in current JastAPI

### Security Scheme Object

- no differences

### OAuth Flows Object

- no differences

### <a name="oAuthFlow"></a>OAuth Flow Object

- openapi parser has new variable "configuration"
- Links to compare
    - [In OpenAPI Specification](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.2.md#oauthFlowObject)
    - [In openapi parser](https://github.com/openapi4j/openapi4j/blob/master/openapi-parser/src/main/java/org/openapi4j/parser/model/v3/OAuthFlow.java)

### Security Requirement Object

- no differences

