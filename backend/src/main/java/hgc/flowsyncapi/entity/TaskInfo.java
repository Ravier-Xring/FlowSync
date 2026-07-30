package hgc.flowsyncapi.entity;
import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
@TableName("task_info")
public class TaskInfo{
@TableId(type=IdType.AUTO)
private Long id;
private Long projectId;
private Long parentId;
private String title;
private String description;
private Long assigneeId;
private Long creatorId;
private String status;
private String priority;
private LocalDate dueDate;
private String aiSuggestion;
private LocalDateTime createTime;
public Long getId(){return id;}
public void setId(Long v){this.id=v;}
public Long getProjectId(){return projectId;}
public void setProjectId(Long v){this.projectId=v;}
public Long getParentId(){return parentId;}
public void setParentId(Long v){this.parentId=v;}
public String getTitle(){return title;}
public void setTitle(String v){this.title=v;}
public String getDescription(){return description;}
public void setDescription(String v){this.description=v;}
public Long getAssigneeId(){return assigneeId;}
public void setAssigneeId(Long v){this.assigneeId=v;}
public Long getCreatorId(){return creatorId;}
public void setCreatorId(Long v){this.creatorId=v;}
public String getStatus(){return status;}
public void setStatus(String v){this.status=v;}
public String getPriority(){return priority;}
public void setPriority(String v){this.priority=v;}
public LocalDate getDueDate(){return dueDate;}
public void setDueDate(LocalDate v){this.dueDate=v;}
public String getAiSuggestion(){return aiSuggestion;}
public void setAiSuggestion(String v){this.aiSuggestion=v;}
public LocalDateTime getCreateTime(){return createTime;}
public void setCreateTime(LocalDateTime v){this.createTime=v;}
}