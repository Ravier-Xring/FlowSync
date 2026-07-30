package hgc.flowsyncapi.service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hgc.flowsyncapi.common.AuthContext;
import hgc.flowsyncapi.dto.PasswordUpdateRequest;
import hgc.flowsyncapi.entity.User;
import hgc.flowsyncapi.mapper.UserMapper;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class UserService{
private final UserMapper mapper;
private final PasswordService passwords;
private final OperationLogService logs;
public UserService(UserMapper m,PasswordService p,OperationLogService l){mapper=m;passwords=p;logs=l;}
public List<User>list(){List<User>x=mapper.selectList(new LambdaQueryWrapper<User>().orderByAsc(User::getId));x.forEach(u->u.setPassword(null));return x;}
public void updatePassword(PasswordUpdateRequest r,Long current){
Long loginId=AuthContext.userId();
Long id=r.getUserId()!=null?r.getUserId():loginId;
if(id==null)throw new IllegalArgumentException("缺少用户ID");
if(!Objects.equals(loginId,id)&&!AuthContext.isLeader())throw new IllegalStateException("只能修改自己的密码");
User u=mapper.selectById(id);
if(u==null)throw new IllegalArgumentException("用户不存在");
if(!passwords.matches(r.getOldPassword(),u.getPassword()))throw new IllegalArgumentException("原密码错误");
if(r.getNewPassword()==null||r.getNewPassword().length()<6)throw new IllegalArgumentException("新密码至少6位");
u.setPassword(passwords.encode(r.getNewPassword()));
mapper.updateById(u);
logs.record("用户","修改密码",id,"修改登录密码");
}
}
