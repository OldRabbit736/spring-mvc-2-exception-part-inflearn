package hello.exception;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/*

스프링 부트가 제공하는 BasicErrorController 는 HTML 페이지를 제공하는 경우에는 매우 편리하다.(4xx, 5xx 페이지)
그런데 API 오류 처리는 다른 차원의 이야기이다.
API 마다, 각각의 컨트롤러나 예외마다 서로 다른 응답 결과를 출력해야 할 수도 있다. (매우 세밀하고 복잡하다.)
BasicErrorController 는 HTML 화면을 처리할 때 사용하고, API 오류 처리는 @ExceptionHandler 를 사용하자!


WAS 이 Exception 이나 response.sendError 등에 반응하게(재요청) 끔 만들어 주는 방법 2가지가 있다.
- 스프링 기본 기능 사용
    - WAS 는 아무런 설정이 없어도 /error 경로로 다시 요청을 보내도록 설정된다. (스프링부트가 설정)
    - 그리고 이 요청은 이미 만들어져 있는 BasicErrorController 가 처리하도록 되어 있다.
    - 사실상 아무것도 안 건드려도, WAS 는 /error 로 재 요청하고, BasicErrorController 는 요청에 걸맞은 응답을 보낸다.
    - API 든 웹 페이지든 어쨌든 응답을 보내야 한다.
    - client 에서 Accept 헤더 설정에 따라 BasicErrorController 가 호출하는 메서드가 달라지게 된다.
    - Accept=text/html 일 때 BasicErrorController 는 "errorHtml" 메서드를 이용하여
      웹 페이지(resources/static/error, resources/templates/error 등)를 리턴한다.
    - 그 외 경우에는 BasicErrorController 는 "error" 메서드를 이용하여 아래와 http body 에 JSON 데이터를 반환한다.
        {
            "timestamp": "2022-07-29T12:30:34.162+00:00",
            "status": 500,
            "error": "Internal Server Error",
            "exception": "java.lang.RuntimeException",
            "path": "/api/members/ex"
        }

- 커스텀 : WebServerFactoryCustomizer 사용
    - 아래와 같이 설정 Bean 을 만든다. 여기서 설정한 경로로 WAS 가 재요청을 보내게 된다.
    - 해당 경로에 반응할 컨트롤러를 만들어 두어야 한다.

참고: BasicErrorController 를 제대로 활용하기 위해서는 Customizer 를 꺼야 한다. (@Component 주석 처리)


예외 처리 페이지를 만들기 위해서 다음과 같은 과정을 거쳤다.
- WebServerCustomizer 생성 (ErrorPage 등록)
- 예외 처리용 ErrorPageController 생성
- 예외 종류에 따라서 ErrorPage 추가


스프링 부트는 이런 과정을 기본으로 제공한다.
- ErrorPage 가 자동으로 등록되어 있다. /error 라는 경로로 기본 오류 페이지를 설정한다.
    - 따로 다른 경로를 설정하지 않는 이상 (WebServerCustomizer 같은..) /error 가 기본 오류 페이지로 사용된다.
    - exception 이 WAS 까지 도달하거나 response.sendError() 가 호출되면, WAS 는 /error 를 호출하게 된다.
- BasicErrorController 라는 스프링 컨트롤러를 자동으로 등록한다.
    - ErrorPage 에서 등록한 /error 를 매핑해서 처리하는 컨트롤러이다.

* 참고
- ErrorMvcAutoConfiguration 이라는 클래스가 오류 페이지를 자동으로 등록한다.


개발자는 오류 페이지만 등록하면 된다!
- BasicErrorController 에 기본적인 로직이 모두 담겨져 있다.
- 개발자는 오류 페이지 화면만 BasicController 가 제공하는 룰과 우선순위에 따라서 등록하면 된다.
    - 뷰 템플릿
        - resources/templates/error/
    - 정적 리소스
        - resources/static/error/
    - 기본 리소스
        - resources/templates/error.html
 */
//@Component
public class WebServerCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Override
    public void customize(ConfigurableWebServerFactory factory) {

        /*
        ErrorPage 는 HttpStatus 또는 Exception 타입으로 만들 수 있다.

        지정한 HttpStatus 또는 Exception 이나 그 자식 타입 Exception 을 WAS(servlet container) 가 response 에서 감지했을 때
        해당 path 로 servlet 호출 --> spring front controller(DispatcherServlet) 가 적정한 controller 호출

        즉 servlet container 는 response 들을 모니터링 하고 있다가 여기서 등록한 exception 이나 error 가 포함된 response 를 감지하면
        다시 부메랑처럼 servlet 을 호출한다.


        예외 발생과 오류 페이지 요청 흐름
        - WAS (여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컴트롤러(예외발생)
        - WAS `/error-page/500` 다시 요청 -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤(/error-page/500)
        - 웹 브라우저(클라이언트)는 서버 내부에서 이런 일이 일어나는지 젼혀 모른다. (redirect 가 아니기 때문)
         */

        ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/error-page/404");
        ErrorPage errorPage500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error-page/500");

        ErrorPage errorPageEx = new ErrorPage(RuntimeException.class, "/error-page/500");

        factory.addErrorPages(errorPage404, errorPage500, errorPageEx);
    }

}
