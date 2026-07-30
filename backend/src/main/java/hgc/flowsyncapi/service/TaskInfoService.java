package hgc.flowsyncapi.service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hgc.flowsyncapi.common.AuthContext;
import hgc.flowsyncapi.dto.*;
import hgc.flowsyncapi.entity.*;
import hgc.flowsyncapi.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
@Service
public class TaskInfoService{
private final TaskInfoMapper mapper;
private final TaskLogMapper logs;
private final TaskSummaryMapper summaries;
private final ProjectInfoService projects;
private final OperationLogService opLogs;
public TaskInfoService(TaskInfoMapper m,TaskLogMapper l,TaskSummaryMapper s,ProjectInfoService p,OperationLogService o){mapper=m;logs=l;summaries=s;projects=p;opLogs=o;}
public List<TaskInfo>list(Long projectId){
LambdaQueryWrapper<TaskInfo>w=new LambdaQueryWrapper<TaskInfo>().orderByDesc(TaskInfo::getId);
if(projectId!=null)w.eq(TaskInfo::getProjectId,projectId);
if(!AuthContext.isLeader()){
Long uid=AuthContext.userId();
w.and(x->x.eq(TaskInfo::getAssigneeId,uid).or().eq(TaskInfo::getCreatorId,uid));
}
return mapper.selectList(w);
}
public boolean canSeeTask(Long taskId){
TaskInfo t=mapper.selectById(taskId);
if(t==null)return false;
return AuthContext.isLeader()||Objects.equals(t.getAssigneeId(),AuthContext.userId())||Objects.equals(t.getCreatorId(),AuthContext.userId());
}
public TaskInfo save(TaskInfo t,Long current){
Long uid=AuthContext.userId();
if(t.getId()==null){
AuthContext.requireLeader();
if(t.getCreatorId()==null)t.setCreatorId(uid);
if(t.getStatus()==null)t.setStatus("未开始");
if(t.getPriority()==null)t.setPriority("中");
mapper.insert(t);
opLogs.record("任务","创建",t.getId(),t.getTitle());
}else{
TaskInfo old=mapper.selectById(t.getId());
if(old==null)throw new IllegalArgumentException("任务不存在");
if(AuthContext.isLeader()){
mapper.updateById(t);
opLogs.record("任务","编辑",t.getId(),t.getTitle());
}else if(Objects.equals(old.getAssigneeId(),uid)){
old.setStatus(t.getStatus());
mapper.updateById(old);
opLogs.record("任务","更新状态",old.getId(),old.getStatus());
return old;
}else throw new IllegalStateException("只能更新自己负责的任务状态");
}
return t;
}
@Transactional
public void delete(Long id){
AuthContext.requireLeader();
deleteCascade(id);
opLogs.record("任务","删除",id,"级联删除任务数据");
}
private void deleteCascade(Long id){
mapper.selectList(new LambdaQueryWrapper<TaskInfo>().eq(TaskInfo::getParentId,id)).forEach(t->deleteCascade(t.getId()));
logs.delete(new LambdaQueryWrapper<TaskLog>().eq(TaskLog::getTaskId,id));
summaries.delete(new LambdaQueryWrapper<TaskSummary>().eq(TaskSummary::getTaskId,id));
mapper.deleteById(id);
}
@Transactional
public int importPlan(AiTaskPlanImportRequest r){
AuthContext.requireLeader();
int n=0;
for(AiTaskPlanItem i:r.getItems()){
if(i.getAssigneeId()==null)throw new IllegalArgumentException("每个任务都必须选择负责人");
TaskInfo t=new TaskInfo();
t.setProjectId(r.getProjectId());
t.setCreatorId(AuthContext.userId());
t.setAssigneeId(i.getAssigneeId());
t.setTitle(i.getTitle());
t.setDescription(i.getDescription());
t.setPriority(i.getPriority()==null?"中":i.getPriority());
t.setStatus("未开始");
t.setDueDate(LocalDate.now().plusDays(i.getSuggestedDays()==null?7:Math.max(1,i.getSuggestedDays())));
mapper.insert(t);
n++;
}
opLogs.record("AI","导入任务",r.getProjectId(),"导入"+n+"条任务");
return n;
}
}
