package hgc.flowsyncapi.common;
public class ApiResponse<T>{
 private boolean success;private String message;private T data;
 public ApiResponse(){} public ApiResponse(boolean s,String m,T d){success=s;message=m;data=d;}
 public static<T>ApiResponse<T>success(T d){return new ApiResponse<>(true,"操作成功",d);}
 public static<T>ApiResponse<T>success(String m,T d){return new ApiResponse<>(true,m,d);}
 public static<T>ApiResponse<T>fail(String m){return new ApiResponse<>(false,m,null);}
 public boolean isSuccess(){return success;}public void setSuccess(boolean v){success=v;}
 public String getMessage(){return message;}public void setMessage(String v){message=v;}
 public T getData(){return data;}public void setData(T v){data=v;}
}
