package hgc.flowsyncapi.entity;
import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
@TableName("task_log")
public class TaskLog{
@TableId(type=IdType.AUTO)
private Long id;
private Long taskId;
private Integer progressPercent;
private String content;
private Long operatorId;
private LocalDateTime createTime;
public Long getId(){return id;}
public void setId(Long v){this.id=v;}
public Long getTaskId(){return taskId;}
public void setTaskId(Long v){this.taskId=v;}
public Integer getProgressPercent(){return progressPercent;}
public void setProgressPercent(Integer v){this.progressPercent=v;}
public String getContent(){return content;}
public void setContent(String v){this.content=v;}
public Long getOperatorId(){return operatorId;}
public void setOperatorId(Long v){this.operatorId=v;}
public LocalDateTime getCreateTime(){return createTime;}
public void setCreateTime(LocalDateTime v){this.createTime=v;}
}