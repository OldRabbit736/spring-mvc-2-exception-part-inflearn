package hello.exception.api;

import hello.exception.exception.UserException;
import hello.exception.exhandler.ErrorResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class ApiExceptionV2Controller {

    /*

    ExceptionHandlerExceptionResolver
    - ExceptionHandlerExceptionResolver 는 사실 예외가 controller(handler) 밖으로 던져졌을 때
      DispatcherServlet 이 돌리는 ExceptionResolver 들 중 가장 우선순위가 높다. 즉 제일 처음 호출되는 로직이다.
    - ExceptionHandlerExceptionResolver 는 예외가 발생한 클래스 내부에 던져진 예외와 일치하는 예외를 다루는
      @ExceptionHandler 가 있는지 살펴보고 있다면 해당 로직을 호출한다.
    - 아래 "illegalExHandler" 의 경우처럼 리턴하게 되면 "정상 흐름"을 response 가 "정상 흐름"을 타게된다.
      response.sendError 가 호출되지도 않고, Exception 이 catch 되었기 때문에,
      response 가 WAS 에 전달되면 WAS 는 재호출을 하지 않고(/error) response 를 클라이언트로 전달한다.
    - 주의점이, HTTP status code 가 200 등 정상 상태가 된다는 것이다.
      HTTP status code 를 오버라이딩 하고 싶다면 @ResponseStatus 를 사용하면 된다.
    - @ExceptionHandler 메서드가 처리하는 예외는 정의된 컨트롤러에서 발생한 예외뿐이다. 다른 컨트롤러에서 발생한 예외는 처리하지 않는다.


    ExceptionHandlerExceptionResolver 우선순위
    - @ExceptionHandler 메서드가 대상으로 하는 예외는, 해당 예외 클래스는 물론 그 자식 예외 클래스까지이다.
    - 따라서 한 예외를 처리할 수 있는 능력을 가진 @ExceptionHandler 메서드는 다수가 될 수 있다.
      왜냐하면 해당 예외를 지정한 @ExceptionHandler 메서드는 물론,
      해당 예외를 자식으로 하는 부모 예외를 지정한 @ExceptionHandler 메서드가 모두 해당 예외를 처리할 수 있는 능력을 갖고 있기 때문이다.
      이 중 어떤 것이 호출되는 것일까?
    - 항상 자세한 ExceptionHandler 가 우선순위를 가진다.
    - 예를 들어 A 라는 예외 클래스, B 라는 A의 부모 예외 클래스 각각의 @ExceptionHandler 메서드가 있다고 하자.
      만약 A 예외가 발생하면 ExceptionHandlerExceptionResolver 는 어떤 @ExceptionHandler 메서드 를 호출하느냐면,
      A 를 대상으로하는 @ExceptionHandler 메서드를 호출한다. 더 구체적이기 때문이다.
      만약 B 예외가 발생한다면, 당연히 B 를 대상으로하는 @ExceptionHandler 메서드가 호출된다.
    - 이것을 다른말로 하면, Exception 클래스 자체를 처리하는 @ExceptionHandler 메서드 하나가
      모든 예외를 처리할 수 있다는 것이다.
    - 실제로 해당 메서드는 else 예외 처리의 느낌으로 사용되곤 한다.


    @ExceptionHandler 파라미터와 응답
    - 마치 스프링의 컨트롤러처럼 다양한 파라미터와 응답을 지정할 수 있다.
    - 사실 View 도 리턴 가능하다. 이 경우 @RestController 말고 @Controller 애노테이션을 사용해야 한다.
      그런데 이 기능은 @ExceptionHandler 와 함께 잘 사용되진 않는다.
    - https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-ann-exceptionhandler-args


    자세한 실행 흐름
    - 컨트롤러를 호출한 결과 IllegalArgumentException 예외가 컨트롤러 밖으로 던져진다.
    - 예외가 발생했으므로 ExceptionResolver 가 작동한다. 가장 우선순위가 높은 ExceptionHandlerExceptionResolver 가 실행된다.
    - ExceptionHandlerExceptionResolver 는 해당 컨트롤러에 IllegalArgumentException 을 처리할 수 있는
      @ExceptionHandler 가 있는지 확인한다.
    - illegalExHandler() 를 실행한다. 컨트롤러가 @RestController 애노테이션되어 있으므로
      illegalExHandler() 에도 @ResponseBody 가 적용된다. 따라서 HTTP 컨버터가 사용되고, 응답이 JSON 으로 반환된다.
    - @ResponseStatus(HttpStatus.BAD_REQUEST) 를 지정했으므로 HTTP 샃태 코드 400으로 응답한다.
      만약 @ResponseStatus 사용하지 않고 리턴타입을 ResponseEntity 으로 지정했다면,
      HTTP 응답 코드를 프로그래밍해서 동적으로 변경할 수 있다.
      @ResponseStatus 는 애노테이션이므로 동적으로 상태코드를 변경할 수 없다.


     */

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler   // 예외 클래스를 생략하면 파라미터 타입을 따른다.
    //@ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalExHandler(IllegalArgumentException e) {
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("BAD", e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExHandler(UserException e) {
        log.error("[exceptionHandler] ex", e);
        ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    // 모든 예외를 처리할 수 있는 메서드
    // 아래 RuntimeException("잘못된 사용자") 예외가 발생하면, illegalExhandler,userExHandler 모두 매칭이 안되므로
    // 이 핸들러가 resolve 하게 된다.
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandler(Exception e) {
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("EX", "내부 오류");
    }

    // 복수의 예외를 지정할 수 있다.
    // 이 때 파라미터의 타입은 예외들의 공통 부모 타입이어야 한다.
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler({UserException.class, IllegalArgumentException.class})
//    public ErrorResult biHandler(Exception e) {
//        log.error("[exceptionHandler] ex", e);
//        return new ErrorResult("EX", "내부 오류");
//    }


    @GetMapping("/api2/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {

        if (id.equals("ex")) {
            throw new RuntimeException("잘못된 사용자");
        }

        if (id.equals("bad")) {
            throw new IllegalArgumentException("잘못된 입력 값");
        }

        if (id.equals("user-ex")) {
            throw new UserException("사용자 오류");
        }

        return new MemberDto(id, "hello " + id);
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }

}
