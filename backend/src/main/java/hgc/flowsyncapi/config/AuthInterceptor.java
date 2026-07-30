package hgc.flowsyncapi.config;
import hgc.flowsyncapi.common.AuthContext;
import hgc.flowsyncapi.entity.User;
import hgc.flowsyncapi.mapper.UserMapper;
import hgc.flowsyncapi.service.JwtService;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Component
public class AuthInterceptor implements HandlerInterceptor{
private final JwtService jwt;
private final UserMapper users;
public AuthInterceptor(JwtService j,UserMapper u){jwt=j;users=u;}
@Override
public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object handler)throws Exception{
if("OPTIONS".equalsIgnoreCase(request.getMethod()))return true;
String path=request.getRequestURI();
if(path.startsWith("/api/auth/login")||path.startsWith("/swagger")||path.startsWith("/v3/api-docs"))return true;
String auth=request.getHeader("Authorization");
if(auth==null||!auth.startsWith("Bearer ")){response.setStatus(401);return false;}
Long userId=jwt.parseUserId(auth.substring(7));
if(userId==null){response.setStatus(401);return false;}
User user=users.selectById(userId);
if(user==null){response.setStatus(401);return false;}
user.setPassword(null);
AuthContext.set(user);
return true;
}
@Override
public void afterCompletion(HttpServletRequest request,HttpServletResponse response,Object handler,Exception ex){AuthContext.clear();}
}
