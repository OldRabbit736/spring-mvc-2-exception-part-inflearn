package hello.exception;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

// Web 서버 설정을 위해 spring boot 에서 제공하는 인터페이스
@Component
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
