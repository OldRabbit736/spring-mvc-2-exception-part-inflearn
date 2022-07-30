package hello.exception.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/*

목표
- 예외가 발생해서 서블릿을 넘어 WAS 까지 전달되면 HTTP 상태코드가 500으로 처리된다.
  발생하는 예외에 따라 400, 404 등 다른 상태코드로 처리하고 싶다. 오류 메시지, 형식등을 API 마다 다르게 처리하고 싶다.


HandlerExceptionResolver
- 스프링 MVC 는 컨트롤러(핸들러) 밖으로 예외가 던져진 경우 예외를 해결하고, 동작을 새로 정의할 수 있는 HandlerExceptionResolver 를 제공한다.
- HandlerExceptionResolver 의 resolveException()이 ModelAndView 리턴 할 경우
  컨트롤러에서 발생한 예외가 DispatcherServlet 을 거쳐 WAS 로 향하게 하는 대신,
  다음과 같은 "정상적인 흐름"을 하게 한다.
    - 컨트롤러에서 예외 발생 --> DispatcherServlet 에 예외 도착
      --> ExceptionResolver 로 예외 전달, 예외 해결 시도 --> sendError 호출, ModelAndView 리턴
      --> DispatcherServlet 으로 정상 응답 전달 --> afterCompletion 등 다음 정상 루트 ...
      - 참고 1: ExceptionResolver 로 예외를 해결해도 postHandle() 은 호출되지 않는다.)
      - 참고 2: new ModelAndView() 로 리턴하면, 렌더링 할 View 가 없으므로, afterCompletion 이 후
               DispatcherServlet 이 View 를 리턴하는 루트 대신, WAS 로 response 를 보내는 루트를 타게 된다.
               WAS 는 response 에 담겨 있는 sendError 를 통해 세팅 된 상태코드, 메시지 등을 보고
               해당 상태코드와 매핑되어 있는 path 를 다시 호출하게 된다.
               따로 커스텀 매핑이 없다면 기본 매핑 경로인 /error 를 통해 BasicErrorController 를 호출하게 될 것이다.
- HandlerExceptionResolver 의 resolveException()이 null 리턴 할 경우
  기존에 예외가 발생했을 경우처럼 예외가 DispatcherServlet 을 거쳐 WAS 로 향하게 된다.
  그럼, 기존처럼 500 상태코드 에러를 출력한다.


HandlerExceptionResolver resolveException() 반환 값에 따른 DispatcherServlet 동작 방식
- 빈 ModelAndView
    - new ModelAndView() 처럼 빈 ModelAndView 를 반환하면 뷰를 렌더링 하지 않고, 서블릿이 정상적으로 서블릿 컨테이너에 리턴한다.
    - response.Error call 흔적이 있다면 해당 상태 코드와 매핑되어 있는 경로로 다시 서블릿 컨테이너(WAS)가 요청을 한다.
- ModelAndView 지정
    - 뷰가 렌더링 되고 클라이언트로 전송된다.
- null
    - 다음 ExceptionResolver 를 찾아서 실행한다. 만약 처리할 수 있는 ExceptionResolver 가 없으면 예외 처리가 안되고,
      기존에 발생한 예외가 서블릿 밖으로 던져진다.
    - 예외를 WAS 가 받고, 예외와 매핑된 path 로 다시 요청을 한다.


ExceptionResolver 활용
- 예외 상태 코드 변환
    - 예외를 response.sendError 호출로 변경해서 서블릿에서 상태 코드에 따른 오류를 처리하도록 위임
    - 이후 WAS 는 서블릿 오류 페이지를 찾아서 내부 호출, 예를 들면 스프링부트가 기본으로 설정한 /error 경로를 호출함
- 뷰 템플릿 처리
    - ModelAndView 에 값을 채워서 예외에 따른 새로운 오류 화면 뷰 렌더링 후 고객에게 제공
- API 응답 처리
    - response.getWriter().println("hello"); 처럼 HTTP 응답 바디에 직접 데이터를 넣어주는 것도 가능하다.
      여기에 JSON 으로 응답하면 JSON 값을 HTTP 응답 바디에 넣어줄 수 있다.


WebMvcConfigurer 를 통해 등록
- extendHandlerExceptionResolvers 메서드를 이용해 원하는 HandlerExceptionResolver 를 등록해 주어야 한다.
- configureHandlerExceptionResolvers 를 사용하면 스프링이 기본으로 등록하는 ExceptionResolver 가 제거되므로 주의.
  extendHandlerExceptionResolvers 를 사용하자.




 */

@Slf4j
public class MyHandlerExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        try {
            if (ex instanceof IllegalArgumentException) {
                log.info("IllegalArgumentException resolver to 400");

                // 예외 상태 코드 변환
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                return new ModelAndView();  // 이 리턴 값은 흐름을 정상으로 돌려 놓는다.

                // API 응답 처리
                //response.getWriter().println("This content will be put in the body");
                //return new ModelAndView();
            }

        } catch (IOException e) {
            log.info("resolver ex", e);
        }

        return null;
    }
}
