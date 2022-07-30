package hello.exception.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/*

스프링이 기본적으로 로드하는 ExceptionResolver 들이 있다.
HandlerExceptionResolverComposite 가 다음 순서로 등록한다.
- ExceptionHandlerExceptionResolver
    - @ExceptionHandler 를 처기한다. API 예외 처리는 대부분 이 기능으로 해결한다.
- ResponseStatusExceptionResolver
    - HTTP 상태 코드가 지정된 예외를 해결해준다.
- DefaultHandlerExceptionResolver (우선 순위가 가장 낮다)
    - 스프링 내부 기본 예외를 처리한다.



ResponseStatusExceptionResolver
- 예외에 따라 HTTP 상태 코드를 지정해 준다.
- 다음 두 가지 경우를 처리한다.
    - @ResponseStatus 가 달려있는 예외 (바로 아래 BadRequestException 같은 경우)
    - ResponseStatusException 예외

@ResponseStatus 가 달려있는 예외
- 이 예외가 컨트롤러 밖으로 넘어가면 ResponseStatusExceptionResolver 가 해당 어노테이션을 확인해서
  오류 코드, 메시지를 담는댜.
- ResponseStatusExceptionResolver 코드를 확인해보면 결국 response.sendError(statusCode, resolvedReason)를
  호출하는 것을 확인할 수 있다.
- sendError 를 호출했기 때문에 WAS 에서 /error 경로로 내부 요청한다.

ResponseStatusException 예외
- @ResponseStatus 는 개발자가 직접 변경할 수 없는 예외에는 적용할 수 없다. (애노테이션을 넣어야 하므로)
- 또한 애노테이션을 사용하기 때문에 조건에 따라 동적으로 값을 변경하는 것도 어렵다.
- ResponseStatusException 이 이를 해결해 준다.
- /exception/api/ApiExceptionController#responseStatusEx2 참조
- 이 Exception 을 ResponseStatusExceptionResolver 가 처리할 때도 결국 sendError 를 호출한다.


DefaultHandlerExceptionResolver
- 스프링 내부에서 발생하는 스프링 예외를 해결한다.
- 파라미터 바인딩 시점에 타입이 맞지 않으면 TypeMismatchException 이 발생하는데,
  만약 그대로 두면 서블릿 컨테이너까지 오류가 올라가거, 결과적으로 500 오류가 발생한다.
- 하지만 파라미터 바인딩은 대부분 클라이언트가 HTTP 요청 정보를 잘못 호출해서 발생한다.
  이 경우에 HTTP 상태코드 400이 적절하다.
- DefaultHandlerExceptionResolver 는 해당 예외를 400 오류로 변경해준다.
- 이 외에도 스프링에서 발생하는 수 많은 Exception 에 대한 대응 내용이 작성되어 있다.
- 이 resolver 도 마찬가지로 response.sendError 를 호출한다.
- /exception/api/ApiExceptionController#defaultException 참조





 */

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "잘못된 요청 오류")
//@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "error.bad") //messages.properties 에서 "error.bad" 키로 메시지 가져옴
public class BadRequestException extends RuntimeException {
}
