### 커밋 컨벤션

- `✨ feature` 새 기능을 구현합니다.
- `🛠️ fix` 버그/이슈를 해결합니다.
- `♻️️ refactor` 리팩터링합니다.
- `✅️ test` 테스트 코드 작성/수정/테스트합니다.
- `📝️ doc` 문서를 작성합니다.

<br>

### 디렉토리 구조 
```angular2html
└── security_practice/
    ├── SecurityPracticeApplication.java
    ├── repository/
    ├── config/
    │   └── SecurityConfig.java
    ├── security/
    │   ├── JwtUtil.java
    │   ├── handler/
    │   │   └── LoginSuccessHandler.java
    │   └── filter/
    │       ├── LoginAuthenticationFilter.java
    │       ├── JwtAuthenticationFilter.java
    │       ├── ExceptionHandlingFilter.java
    │       └── package-info.java
    ├── mapper/
    ├── controller/
    ├── service/
    ├── domain/
    ├── exception/
    │   ├── ErrorCode.java
    │   ├── ErrorResponseEntity.java
    │   ├── CustomException.java
    │   └── CustomExceptionHandler.java
    └── dto/
        ├── response/
        └── request/
```