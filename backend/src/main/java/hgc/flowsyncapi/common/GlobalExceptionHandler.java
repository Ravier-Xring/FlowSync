package hgc.flowsyncapi.common;
import org.springframework.http.HttpStatus;import org.springframework.web.bind.annotation.*;
@RestControllerAdvice public class GlobalExceptionHandler{
 @ExceptionHandler({IllegalArgumentException.class,IllegalStateException.class})@ResponseStatus(HttpStatus.BAD_REQUEST)
 public ApiResponse<Void>business(RuntimeException e){return ApiResponse.fail(e.getMessage());}
 @ExceptionHandler(Exception.class)@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
 public ApiResponse<Void>other(Exception e){return ApiResponse.fail("服务器内部错误："+e.getMessage());}
}
