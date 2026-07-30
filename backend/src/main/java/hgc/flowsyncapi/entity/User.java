package hgc.flowsyncapi.entity;
import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
@TableName("sys_user")
public class User{
@TableId(type=IdType.AUTO)
private Long id;
private String username;
private String password;
private String realName;
private String role;
private String phone;
private String email;
private LocalDateTime createTime;
public Long getId(){return id;}
public void setId(Long v){this.id=v;}
public String getUsername(){return username;}
public void setUsername(String v){this.username=v;}
public String getPassword(){return password;}
public void setPassword(String v){this.password=v;}
public String getRealName(){return realName;}
public void setRealName(String v){this.realName=v;}
public String getRole(){return role;}
public void setRole(String v){this.role=v;}
public String getPhone(){return phone;}
public void setPhone(String v){this.phone=v;}
public String getEmail(){return email;}
public void setEmail(String v){this.email=v;}
public LocalDateTime getCreateTime(){return createTime;}
public void setCreateTime(LocalDateTime v){this.createTime=v;}
}