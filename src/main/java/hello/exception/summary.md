# Spring framework의 오류 처리

## 종류 1 - Servlet container(WAS) Error 재호출
주로 에러 웹 페이지를 처리하는 용도로 많이 사용된다. 물론 API 응답을 리턴하도록 해 줄수도 있지만, 많이 번거롭다.  
Servlet container(WAS)까지 exception이 도달하거나, response(HttpServletResponse)에 sendError의 호출 흔적이 있는 경우에
WAS가 내부 재호출을 하게 되고 이것을 담당 컨트롤러가 해결하는 방식의 메커니즘이다.

다음의 조합으로 많이 사용된다.
- WebServerFactoryCustomizer 호출 경로 설정 + 내부 에러 호출 담당 Controller 정의 (둘 다 사용자가 직접 작업해야 한다.)
- /error 호출 + BasicErrorController 사용 (둘 다 스프링에 의해 기본적으로 로드되어 있음)

## 종류 2 - Exception Resolver
기본적으로 Handler(@Controller) 밖으로 던져지는 예외를 캐치하고 알맞은 형태로 변환하여 예외를 해결한다.  
예외등이 핸들러 밖으로 던져지면, DispatcherServlet은 등록된 ExceptionResolver 들을 이용하여 예외 resolve를 시도한다.  
resolve 한다는 의미는 결국 예외를 "정상 흐름"으로 돌린다는 것이다.  
"정상 흐름"은 예외가 WAS까지 도달하는 것을 막고, 원하는 HttpServletResponse를 설정하는 것을 말한다.

다음의 종류가 있다.
- HandlerExceptionResolver
- 스프링 기본 제공 ExceptionResolver
  - ExceptionHandlerExceptionResolver (@ExceptionHandler + @ControllerAdvice)
  - ResponseStatusExceptionResolver
  - DefaultHandlerExceptionResolver

## 뭘 써야 하죠?
만약 **에러 페이지 리턴** 하려면 "종류 1"의 두 번째 옵션을 쓰면 편리하다.  
왜냐하면 이미 BasicController에 의해 에러 페이지 위치 및 명명법이 고정되어 있어 사용자는 에러 페이지만 정해진 위치에 생성하면 따로 할 일이 없을 정도이다.

하지만 **API 리턴**을 해야 하는 상황에서는 어떨까?  
이 때는 **ExceptionHandlerExceptionResolver + ControllerAdvice** 를 사용하는 것이 제일 좋다.  

API 오류 응답은 단순히 에러 페이지 리턴하는 것 보다 훨씬 복잡하고 세밀하다.  
- 각 시스템마다 응답의 모양과 스펙이 다르다.
- 예외에 따라서 각각 다른 데이터를 출력해야 할 수도 있다.
- 같은 예외라고 할 지라도 어떤 컨트롤러가 던졌냐에 따라서 응답의 모양을 바꿔야 할 수도 있다.

"종류 1"은 이런 세밀한 제어가 힘들고, "종류 2"의 HandlerExceptionHandler를 직접 사용하기는 복잡하다.  
HttpServletResponse에 직접 데이터를 세팅해야 해서 불편하고 번거롭다.  
필요도 없는 경우에도 ModelAndView는 무조건 반환해야 하는 점도 어려운 점이다. 
또한 컨트롤러 별로 에러 로직을 다르게 처리하는 것도 어렵다.

스프링은 이 문제를 해결하기 위해 ExceptionHandlerExceptionResolver라는
매우 혁신적인 예외 처리 기능을 제공한다.  
이것을 사용하자!

ExceptionHandlerExceptionResolver는 @ExceptionHandler 메서드로 에러 resolve를 시도한다.
또한 @ControllerAdvice controller를 따로 정의해서 여기에 @ExceptionHandler를 모아둘 수 있다.
ExceptionHandlerExceptionResolver는 Resolver 중 우선순위가 제일 높으며 자동으로 스프링에 의해 로드된다.
이 방식으로 exception, controller 별 세밀한 응답을 설정할 수 있고, 에러 핸들링 로직과 컨트롤러를 분리시켜줄 수 있어
깔끔하고 이해가 쉬운 코드 구조를 유지할 수 있게 해 준다.

## 결론  
### API 예외처리는 @ExceptionHandler + @ControllerAdvice 조합을 사용하자!
