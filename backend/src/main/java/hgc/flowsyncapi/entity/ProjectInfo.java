package hgc.flowsyncapi.entity;
import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
@TableName("project_info")
public class ProjectInfo{
@TableId(type=IdType.AUTO)
private Long id;
private String name;
private String description;
private String status;
private String priority;
private Long ownerId;
private LocalDate startDate;
private LocalDate endDate;
private LocalDateTime createTime;
public Long getId(){return id;}
public void setId(Long v){this.id=v;}
public String getName(){return name;}
public void setName(String v){this.name=v;}
public String getDescription(){return description;}
public void setDescription(String v){this.description=v;}
public String getStatus(){return status;}
public void setStatus(String v){this.status=v;}
public String getPriority(){return priority;}
public void setPriority(String v){this.priority=v;}
public Long getOwnerId(){return ownerId;}
public void setOwnerId(Long v){this.ownerId=v;}
public LocalDate getStartDate(){return startDate;}
public void setStartDate(LocalDate v){this.startDate=v;}
public LocalDate getEndDate(){return endDate;}
public void setEndDate(LocalDate v){this.endDate=v;}
public LocalDateTime getCreateTime(){return createTime;}
public void setCreateTime(LocalDateTime v){this.createTime=v;}
}