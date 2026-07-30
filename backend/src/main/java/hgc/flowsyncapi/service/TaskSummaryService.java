package hgc.flowsyncapi.service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hgc.flowsyncapi.common.AuthContext;
import hgc.flowsyncapi.entity.TaskSummary;
import hgc.flowsyncapi.mapper.TaskSummaryMapper;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class TaskSummaryService{
private final TaskSummaryMapper mapper;
private final ProjectInfoService projects;
private final TaskInfoService tasks;
private final OperationLogService opLogs;
public TaskSummaryService(TaskSummaryMapper m,ProjectInfoService p,TaskInfoService t,OperationLogService o){mapper=m;projects=p;tasks=t;opLogs=o;}
public List<TaskSummary>list(){
List<TaskSummary>all=mapper.selectList(new LambdaQueryWrapper<TaskSummary>().orderByDesc(TaskSummary::getId));
if(AuthContext.isLeader())return all;
return all.stream().filter(s->projects.canSeeProject(s.getProjectId())).toList();
}
public TaskSummary save(TaskSummary x,Long current){
if(!projects.canSeeProject(x.getProjectId()))throw new IllegalStateException("不能给不可见项目添加总结");
if(x.getTaskId()!=null&&!tasks.canSeeTask(x.getTaskId()))throw new IllegalStateException("不能关联不可见任务");
x.setCreatedBy(AuthContext.userId());
mapper.insert(x);
opLogs.record("总结","新增",x.getId(),x.getSummaryType());
return x;
}
}
