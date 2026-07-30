package hgc.flowsyncapi.service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hgc.flowsyncapi.common.AuthContext;
import hgc.flowsyncapi.entity.TaskLog;
import hgc.flowsyncapi.mapper.TaskLogMapper;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class TaskLogService{
private final TaskLogMapper mapper;
private final TaskInfoService tasks;
private final OperationLogService opLogs;
public TaskLogService(TaskLogMapper m,TaskInfoService t,OperationLogService o){mapper=m;tasks=t;opLogs=o;}
public List<TaskLog>list(Long taskId){
LambdaQueryWrapper<TaskLog>w=new LambdaQueryWrapper<TaskLog>().orderByDesc(TaskLog::getId);
if(taskId!=null)w.eq(TaskLog::getTaskId,taskId);
List<TaskLog>all=mapper.selectList(w);
if(AuthContext.isLeader())return all;
return all.stream().filter(l->tasks.canSeeTask(l.getTaskId())).toList();
}
public TaskLog save(TaskLog x,Long current){
if(!tasks.canSeeTask(x.getTaskId()))throw new IllegalStateException("不能为不可见任务添加进度");
if(x.getProgressPercent()==null||x.getProgressPercent()<0||x.getProgressPercent()>100)throw new IllegalArgumentException("进度必须在0到100之间");
x.setOperatorId(AuthContext.userId());
mapper.insert(x);
opLogs.record("进度","新增",x.getId(),x.getContent());
return x;
}
}
