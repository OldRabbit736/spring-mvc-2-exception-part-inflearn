package hello.exception.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.exception.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/*

MyHandlerExceptionResolver 는 호출 구조가 너무 복잡하다!
- resolver 가 exception 을 해결했다 하더라도, 결국은 response 가 servlet container 까지 갔다가,
  다시 호출을 또 실행해서 에러 처리하는 컨트롤러까지 도달해야 한다.
- 더 간단한 방법은 없을까?


HandlerExceptionResolver 에서 그냥 응답을 만들어 버리면 된다.
- 기존에는 resolver 에서 response.sendError 메서드를 호출하여 결과적으로 WAS 가 한번 더 내부 호출을 해야만 했다.
- 하지만 resolver 에서 response.sendError 를 호출하지 않고 response 로 내보낼 각종 값을 세팅한다면?
  그러면 WAS 에게 정상 응답을 보낸 것이 되고, WAS 는 해당 응답을 클라이언트로 보내게 된다!
- 결과적으로 WAS 입장에서는 MVC 에서 예외 처리가 모두 끝났기 때문에 정상 처리를 받게 된 것이다.
  이렇게 예외를 이곳에서 모두 처리할 수 있다는 것이 핵심이다!


그런데 문제는 HandlerExceptionResolver 를 구현하는 것도 상당히 복잡하다는 것이다.
- 스프링이 제공하는 ExceptionResolver 가 이 문제를 해결해 준다!

 */

@Slf4j
public class UserHandlerExceptionResolver implements HandlerExceptionResolver {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        try {
            if (ex instanceof UserException) {
                log.info("UserException resolver to 400");
                String acceptHeader = request.getHeader("accept");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                if ("application/json".equals(acceptHeader)) {
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("ex", ex.getClass());
                    errorResult.put("message", ex.getMessage());
                    String result = objectMapper.writeValueAsString(errorResult);

                    response.setContentType("application/json");
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().write(result);
                    return new ModelAndView();
                } else {
                    // TEXT/HTML
                    return new ModelAndView("error/500");   // resources/templates/error/500.html 호출
                }

            }

        } catch (IOException e) {
            log.error("resolver ex", e);
        }

        return null;
    }
}
