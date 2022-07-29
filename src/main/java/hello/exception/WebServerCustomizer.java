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
         */

        ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/error-page/404");
        ErrorPage errorPage500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error-page/500");

        ErrorPage errorPageEx = new ErrorPage(RuntimeException.class, "/error-page/500");

        factory.addErrorPages(errorPage404, errorPage500, errorPageEx);
    }

}
