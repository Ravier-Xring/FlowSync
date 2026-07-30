package hgc.flowsyncapi.entity;
import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
@TableName("task_summary")
public class TaskSummary{
@TableId(type=IdType.AUTO)
private Long id;
private Long projectId;
private Long taskId;
private String summaryType;
private String content;
private Long createdBy;
private LocalDateTime createTime;
public Long getId(){return id;}
public void setId(Long v){this.id=v;}
public Long getProjectId(){return projectId;}
public void setProjectId(Long v){this.projectId=v;}
public Long getTaskId(){return taskId;}
public void setTaskId(Long v){this.taskId=v;}
public String getSummaryType(){return summaryType;}
public void setSummaryType(String v){this.summaryType=v;}
public String getContent(){return content;}
public void setContent(String v){this.content=v;}
public Long getCreatedBy(){return createdBy;}
public void setCreatedBy(Long v){this.createdBy=v;}
public LocalDateTime getCreateTime(){return createTime;}
public void setCreateTime(LocalDateTime v){this.createTime=v;}
}