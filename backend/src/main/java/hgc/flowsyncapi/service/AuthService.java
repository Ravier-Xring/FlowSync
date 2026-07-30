package hgc.flowsyncapi.service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hgc.flowsyncapi.dto.LoginRequest;
import hgc.flowsyncapi.entity.User;
import hgc.flowsyncapi.mapper.UserMapper;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class AuthService{
private final UserMapper mapper;
private final PasswordService passwords;
private final JwtService jwt;
private final OperationLogService logs;
public AuthService(UserMapper m,PasswordService p,JwtService j,OperationLogService l){mapper=m;passwords=p;jwt=j;logs=l;}
public Map<String,Object>login(LoginRequest r){
User u=mapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername,r.getUsername()).last("LIMIT 1"));
if(u==null||!passwords.matches(r.getPassword(),u.getPassword()))throw new IllegalArgumentException("用户名或密码错误");
if(passwords.needsUpgrade(u.getPassword())){u.setPassword(passwords.encode(r.getPassword()));mapper.updateById(u);}
String token=jwt.create(u);
u.setPassword(null);
Map<String,Object>x=new LinkedHashMap<>();
x.put("token",token);
x.put("user",u);
try{logs.record("认证","登录",u.getId(),"用户登录");}catch(Exception ignored){}
return x;
}
}
