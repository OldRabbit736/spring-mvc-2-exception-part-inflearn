package hello.exception;

import hello.exception.filter.LogFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LogFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/*");

        /*
        setDispatcherTypes() 에서 어떤 타입의 dispatcher type 에서 filter 가 호출될 지 설정한다.
        아무것도 넣지 않으면 기본 값이 DispatcherType.REQUEST 이다.
        특별히 오류 페이지 경로도 필터를 적용할 것이 아니면, 기본 값을 그대로 사용하면 된다.
        아래 줄을 코멘트 처리하고 다시 시험해 보면, ERROR page 재 요청 시에 필터가 적용 안되는 것을 확인할 수 있다.

        결론: ERROR 재 요청 시에 filter 가 또 호출되는 것을 막으려면 그냥 setDispatcherTypes 를 호출하지 않으면 된다.
         */
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);
        return filterRegistrationBean;
    }
}

// 참조: filter 는 servlet 기술이지만, interceptor 는 spring 기술이다.
