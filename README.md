# RAGO - RAG OpenAPI 프레임워크

## 소개

`RAGO`는 [JastAdd](https://jastadd.cs.lth.se/)를 모델로 한 최초의 REST API 퍼징 도구입니다.

먼저, 이 도구는 [OpenAPI Specification (OAS)](https://swagger.io/specification/)를 Java 클래스에서 파싱하여 `AST-Node`로 변환합니다.

변환한 `AST-Node`들은 API 명세서를 설명하는 JSON으로 다시 변환하여 저장할 수 있습니다.

![](img/parser.png)

파싱 단계가 끝나면, `RAGO`는 퍼징을 기반으로 두 가지 다른 방식, [랜덤 테스트](#ragoRandTest)와 [파라미터 추론](#ragoParamInf)을 통해 자동으로 테스트를 생성할 준비가 됩니다.

## JastAdd에서의 OpenAPI 명세

[OpenAPI Specification (OAS)](https://swagger.io/specification/)은 RESTful API에 대한 표준적이고 언어에 구애받지 않는 인터페이스를 정의하며, 이는 JSON 또는 YAML로 표현됩니다. 이 명세서는 30개의 객체로 구성되며 트리 구조로 되어 있습니다.

OpenAPI 문서가 JastAdd에서 어떻게 변환될 수 있는지 더 잘 이해하려면, 아래 JastAdd 문법의 객체와 [OpenAPI 객체 설명](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#oasObject)을 비교해 보십시오.
```
OpenAPIObject ::= <OpenAPI> <JsonSchemaDialect> I:InfoObject Serv:ServerObject*
                  P:PathsObject* W:Webhook* C:ComponentsObject Sr:SecurityRequirementObject*
                  T:TagObject* [E:ExternalDocObject] Ex:Extension*;
```
![](img/openapi.png)

개발자가 고려해야 할 몇 가지 구현 세부 사항이 있습니다:

- JastAdd는 `Map`을 지원하지 않습니다. 따라서 이는 튜플(AST-Node)로 구성되어야 합니다. 예를 들어,
    - `ServerVariablesTuple ::= <Name> S:ServerVariableObject;`
    - [Server Object](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#serverObject)에서 `variables`

- OAS에서는 여러 객체가 [참조 객체](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#referenceObject)로 대체될 수 있습니다. `RAGO`에서는 이러한 구조를 모든 관련 객체에 대한 추상 노드로 구현했습니다. 예를 들어,
    - [Parameter Object](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#parameterObject)
    - JastAdd의 다음 추상 노드

[Parameter Object](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#parameterObject) 구현 예제
``` 
  abstract ParameterOb;
  ParameterReference : ParameterOb ::= <Ref> ...;
  ParameterObject : ParameterOb ::= <Name> <In> ...;
```

- 대부분의 객체는 이름과 값이 고정되지 않은 `Extension`으로 확장될 수 있습니다. JastAdd에서는 이것도 튜플(AST-Node)로 구현되었습니다. `Extension ::= <Key> <Value:Object>;`

## RAGO의 퍼징

API 테스트를 자동으로 생성하기 위해, `RAGO`는 퍼징을 기반으로 하는 두 가지 기술을 지원합니다. 이 기술들은 API에 잘못된, 예상치 못한 또는 랜덤 데이터를 입력으로 제공합니다.

첫 번째 버전에서는 이 도구가 `GET` 요청 유형과 쿼리 파라미터만을 고려합니다. 이는 현재 `RAGO`가 URL만 생성한다는 것을 의미합니다.

### <a name="ragoRandTest"></a>랜덤 테스트

랜덤 테스트는 간단한 랜덤 생성기인 `RandomRequestGenerator`를 기반으로 합니다. 이 생성기는 OpenAPI 문서에 매핑된 `OpenAPIObject`를 읽고, `OpenAPIObject`에 존재하는 작업의 모든 파라미터 유형을 확인합니다.

그 후, `RandomRequestGenerator`는 어떤 요청에 어떤 파라미터 유형이 필요한지 알고 모든 요청과 URL에 대해 랜덤 파라미터를 생성합니다.

### <a name="ragoParamInf"></a>파라미터 추론

간단한 랜덤화 기술을 기반으로 한 대부분의 테스트는 커버리지가 낮고 비정상적인 검증 때문에 효과적이지 않습니다. 더 구체적인 테스트 결과를 관찰하기 위해, `InferParameter`는 `RandomRequestGenerator`의 랜덤 테스트 결과를 사용하여 테스트를 자동으로 생성합니다.

`RandomRequestGenerator`에 의해 생성된 테스트 결과(응답)는 사전에 저장되며 다른 요청에서 사용할 수 있는 파라미터를 추론하는 데 사용됩니다.

요청과 응답에 동일한 스키마 세트가 있는 경우, 그들의 파라미터는 다음 전략에 따라 추론됩니다:

- 대소문자 구분 없음
- 필드 이름에서의 ID 완성(예: 속성 이름이 "id"인 경우, 명세서에서 사용 가능한 추가 필드 이름을 얻음)

**주의**: 파라미터 추론은 명세 기반 접근 방식[^1]과 RESTTESTGEN[^2]에서 영감을 받은 실험적인 퍼징 접근 방식입니다.

## 참고 문헌

[^1]: Hamza Ed-Douibi, Javier Luis Cánovas Izquierdo, and Jordi Cabot. “Automatic generation of test cases for REST APIs: A specification-based approach”. In: 2018 IEEE 22nd international enterprise distributed object computing conference (EDOC). IEEE. 2018, pp. 181–190

[^2]: Emanuele Viglianisi, Michael Dallago, and Mariano Ceccato. “RestTestGen: automated black-box testing of RESTful APIs”. In: 2020 IEEE 13th International Conference on Software Testing, Validation and Verification (ICST). IEEE. 2020, pp. 142–152