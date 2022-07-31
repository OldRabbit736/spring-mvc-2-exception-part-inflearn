package hello.exception.exhandler.advice;

import hello.exception.exception.UserException;
import hello.exception.exhandler.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/*

@ControllerAdvice
- 대상으로 지정한 여러 컨트롤러에 @ExceptionHandler, @InitBinder 기능을 부여해주는 역할을 한다.
- 대상을 지정하지 않으면 모든 컨트롤러에 적용된다. (글로벌 적용)
- @RestControllerAdvice 는 @ControllerAdvice 와 같고, @ResponseBody 만 추가된 차이만 있다.
  @Controller, @RestController 의 차이와 같다.

대상 컨트롤러 지정 방법
- https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-ann-controller-advice
- 특정 애노테이션이 있는 컨트롤러를 지정할 수 있다.
- 특정 패캐지를 지정할 수 있다. 이 경우 해당 패키지와 그 하위 패키지 내의 모든 컨트롤러가 대상이 된다.
- 컨트롤러 타입을 지정할 수 있다. 해당 타입은 물론 그 자식 타입까지 모두 대상이 된다.
- 컨트롤러 지정을 생략하면 모든 콘트롤러에 적용된다. (글로벌 적용)
- 보통 패키지 정도를 지정하는 정도로 쓰인다고 한다.

정리
- @ExceptionHandler 와 @ControllerAdvice 를 조합하면 예외를 깔끔하게 해결할 수 있다.

 */

@Slf4j
@RestControllerAdvice(basePackages = "hello.exception.api")
public class ExControllerAdvice {

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

}
