package hgc.flowsyncapi.common;
import hgc.flowsyncapi.entity.User;
public class AuthContext{
private static final ThreadLocal<User>CURRENT=new ThreadLocal<>();
public static void set(User user){CURRENT.set(user);}
public static User get(){return CURRENT.get();}
public static Long userId(){User u=get();return u==null?null:u.getId();}
public static boolean isLeader(){User u=get();return u!=null&&("负责人".equals(u.getRole())||"管理员".equals(u.getRole()));}
public static User requireUser(){User u=get();if(u==null)throw new IllegalStateException("请先登录");return u;}
public static void requireLeader(){if(!isLeader())throw new IllegalStateException("没有权限执行该操作");}
public static void clear(){CURRENT.remove();}
}
