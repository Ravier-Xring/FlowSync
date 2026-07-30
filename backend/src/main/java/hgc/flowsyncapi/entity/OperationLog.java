package hgc.flowsyncapi.entity;
import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
@TableName("operation_log")
public class OperationLog{
@TableId(type=IdType.AUTO)
private Long id;
private Long operatorId;
private String module;
private String action;
private Long targetId;
private String detail;
private LocalDateTime createTime;
public Long getId(){return id;}
public void setId(Long v){id=v;}
public Long getOperatorId(){return operatorId;}
public void setOperatorId(Long v){operatorId=v;}
public String getModule(){return module;}
public void setModule(String v){module=v;}
public String getAction(){return action;}
public void setAction(String v){action=v;}
public Long getTargetId(){return targetId;}
public void setTargetId(Long v){targetId=v;}
public String getDetail(){return detail;}
public void setDetail(String v){detail=v;}
public LocalDateTime getCreateTime(){return createTime;}
public void setCreateTime(LocalDateTime v){createTime=v;}
}
