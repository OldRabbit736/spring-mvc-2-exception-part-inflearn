package hello.exception.servlet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/*
서블릿은 다음 2가지 방식으로 예외 처리를 지원한다.
- Exception (예외)
- response.sendError(HTTP 상태 코드, 오류 메시지)

Exception 전파 경로
- 예외 전파 경로 - 만약 아무도 잡지 못한다면 예외는 어디까지 전파될까?
- WAS <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)

Exception 이 WAS(Tomcat)까지 전달 되면 어떻게 될까? (스프링 부트 기본 예외 페이지 꺼둔 경우)
- Tomcat 은 미리 준비해둔 500 - Internal Server Error 페이지를 사용자에게 넘겨준다.
- Tomcat 은 Exception 을 서버 내부에서 처리할 수 없는 오류라고 생각해서 HTTP 상태 코드 500을 반환하는 것이다.

response.sendError(HTTP 샹태 코드, 오류 메시지)
- HttpServletResponse 가 제공하는 sendError 라는 메서드를 사용할 수 있다.
- 호출하는 당장 예외를 발생시키는 것은 아니며, 서블릿 컨테이너에게 오류가 발생했다는 점을 전달한다.
  (어쨋든 응답 데이터는 서블릿 컨테이너를 통해 외부로 전송된다.)
- HTTP 상태 코드, 오류 메시지 정보를 같이 넘겨줄 수 있다.
- response.sendError() 를 호출하면 response 내부에 오류 내용을 전달한다.
  그리고 서블릿 컨테이너는 고객에게 응답 전에 response 에 sendError 가 호출 되었는지 확인한다.
  호출 되었다면 설정한 오류 코드에 맞춰 기본 오류 페이지를 보여준다.
- sendError 흐름
  WAS (sendError 호출 기록 확인) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(response.sendError())


예외 발생과 오류 페이지 요청 흐름
- WAS (여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컴트롤러(예외발생)
- WAS `/error-page/500` 다시 요청 -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤(/error-page/500)
- 웹 브라우저(클라이언트)는 서버 내부에서 이런 일이 일어나는지 젼혀 모른다. (redirect 가 아니기 때문)

오류 정보 추가
- WAS 는 다시 요청하는 것만이 아니라, 오류 정보를 request 의 attribute 에 추가해서 넘겨준다. (ErrorPageController.java 파일에서 내용 확인 가능)

서블릿 예외 처리 - 필터
- 오류가 발생하면 다시 호출하면서 필터, 서블릿, 인터셉터 모두 다시 호출된다.
- 로그인 인증 체크 같은 경우, 이미 한번 필터나 인터셉터에서 완료되었다.
  따라서 오류 페이지 호출 과정에서 다시 한번 더 호출될 필요는 없다.
- 클라이언트에서 발생한 요청인지, 아니면 오류 페이지를 출력하기 위한 내부 요청인지를 구별하는 방법이 필요하다.
- 이 문제를 해결하기 위해 DispatcherType 이라는 추가 정보가 제공된다.
- HttpServletRequest.getDispatcherType() 호출 시 DispatcherType 값이 리턴된다.

DispatcherType
- javax.servlet.DispatcherType (Enum)
- 종류
    - FORWARD: 서블릿에서 다른 서블릿이나 JSP 를 호출할 때 - RequestDispatcher.forward(request, response)
    - INCLUDE: 서블릿에서 다른 서블릿이나 JSP 의 결과를 포함할 때 - RequestDispatcher.include(request, response)
    - REQUEST: 클라이언트 요청
    - ASYNC: 서블릿 비동기 호출
    - ERROR: 오류 요청


*/

@Slf4j
@Controller
public class ServletExceptionController {


    // WAS 까지 Exception 전달 --> WAS 가 500 기본 오류 페이지를 클라이언트에 반환
    @GetMapping("/error-ex")
    public void errorEx() {
        throw new RuntimeException("예외 발생!");
    }

    // WAS 가 sendError 호출 확인 --> WAS 가 해당 오류 코드에 맞춘 기본 오류 페이지를 클라이언트에 반환
    @GetMapping("/error-404")
    public void error404(HttpServletResponse response) throws IOException {
        response.sendError(404, "404 오류!"); // 오류 메시지는 기본적으로 숨겨서 클라이언트에 반환하지만 설정을 통해 드러나도록 할 수 있다.
    }

    // WAS 가 sendError 호출 확인 --> WAS 가 해당 오류 코드에 맞춘 기본 오류 페이지를 클라이언트에 반환
    @GetMapping("/error-500")
    public void error500(HttpServletResponse response) throws IOException {
        response.sendError(500);
    }

}
