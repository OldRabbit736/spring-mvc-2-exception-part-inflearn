# ExceptionResolver
아래 내용은 spring framework 환경을 가정한다.

## 역할
기본적으로 Handler(@Controller) 밖으로 던져지는 예외를 캐치하고 알맞은 형태로 변환한다.  
예외를 적절한 HttpServletResponse 객체로 변경한다.    
클라이언트에게 전달될 데이터 구조(API, WebPage)에 따라 객체 정보를 구성해서 리턴한다.  
API의 경우 예외를 HTTP status code, 메시지 등으로 변경된 정보를 포함하게 된다.

## 실행순서
기본적으로 예외등이 핸들러 밖으로 던져지면, DispatcherServlet은 등록된
ExceptionResolver 들을 이용하여 예외를 resolve 하려 시도한다.  
resolve 결과로 HttpServletResponse가 DispatcherServlet으로 전달되면,

## 종류
### HandlerExceptionResolver


### 스프링 기본 제공 ExceptionResolver
- ExceptionHandlerExceptionResolver
- ResponseStatusExceptionResolver
- DefaultHandlerExceptionResolver

## 뭘 써야 하죠?
HandlerExceptionHandler를 직접 사용하기는 복잡하다.  
API 오류 응답의 경우 response에 직접 데이터를 세팅해야 해서 불편하고 번거롭다.  
필요도 없는 경우에도 ModelAndView는 무조건 반환해야 하는 점도 API 응답할 때 번거러운 점이 된다. 

ResponseStatusExceptionResolver는 예외를 HTTP 상태코드로 자동 변환해주고,
DefaultHandlerExceptionResolver는 

스프링은 이 문제를 해결하기 위해 ExceptionHandlerExceptionResolver라는
매우 혁신적인 예외 처리 기능을 제공한다.  
이것을 사용하자!
