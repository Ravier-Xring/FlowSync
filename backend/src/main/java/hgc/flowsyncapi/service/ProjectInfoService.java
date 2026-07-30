package hgc.flowsyncapi.service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hgc.flowsyncapi.common.AuthContext;
import hgc.flowsyncapi.entity.*;
import hgc.flowsyncapi.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class ProjectInfoService{
private final ProjectInfoMapper mapper;
private final TaskInfoMapper tasks;
private final TaskLogMapper logs;
private final TaskSummaryMapper summaries;
private final OperationLogService opLogs;
public ProjectInfoService(ProjectInfoMapper m,TaskInfoMapper t,TaskLogMapper l,TaskSummaryMapper s,OperationLogService o){mapper=m;tasks=t;logs=l;summaries=s;opLogs=o;}
public List<ProjectInfo>list(){return listVisible();}
public List<ProjectInfo>listVisible(){
LambdaQueryWrapper<ProjectInfo>w=new LambdaQueryWrapper<ProjectInfo>().orderByDesc(ProjectInfo::getId);
if(!AuthContext.isLeader()){
Long uid=AuthContext.userId();
Set<Long>projectIds=visibleProjectIds(uid);
if(projectIds.isEmpty())return new ArrayList<>();
w.in(ProjectInfo::getId,projectIds);
}
return mapper.selectList(w);
}
public Set<Long>visibleProjectIds(Long userId){
Set<Long>ids=new LinkedHashSet<>();
if(userId==null)return ids;
mapper.selectList(new LambdaQueryWrapper<ProjectInfo>().eq(ProjectInfo::getOwnerId,userId)).forEach(p->ids.add(p.getId()));
tasks.selectList(new LambdaQueryWrapper<TaskInfo>().and(w->w.eq(TaskInfo::getAssigneeId,userId).or().eq(TaskInfo::getCreatorId,userId))).forEach(t->ids.add(t.getProjectId()));
return ids;
}
public boolean canSeeProject(Long projectId){return AuthContext.isLeader()||visibleProjectIds(AuthContext.userId()).contains(projectId);}
public ProjectInfo save(ProjectInfo p,Long current){
AuthContext.requireLeader();
if(p.getId()==null){
if(p.getOwnerId()==null)p.setOwnerId(AuthContext.userId());
if(p.getStatus()==null)p.setStatus("未开始");
if(p.getPriority()==null)p.setPriority("中");
mapper.insert(p);
opLogs.record("项目","创建",p.getId(),p.getName());
}else{
mapper.updateById(p);
opLogs.record("项目","编辑",p.getId(),p.getName());
}
return p;
}
@Transactional
public void delete(Long id){
AuthContext.requireLeader();
List<TaskInfo>projectTasks=tasks.selectList(new LambdaQueryWrapper<TaskInfo>().eq(TaskInfo::getProjectId,id));
for(TaskInfo t:projectTasks.stream().sorted(Comparator.comparing(TaskInfo::getId).reversed()).collect(Collectors.toList()))deleteTaskCascade(t.getId());
summaries.delete(new LambdaQueryWrapper<TaskSummary>().eq(TaskSummary::getProjectId,id));
mapper.deleteById(id);
opLogs.record("项目","删除",id,"级联删除项目数据");
}
private void deleteTaskCascade(Long id){
tasks.selectList(new LambdaQueryWrapper<TaskInfo>().eq(TaskInfo::getParentId,id)).forEach(t->deleteTaskCascade(t.getId()));
logs.delete(new LambdaQueryWrapper<TaskLog>().eq(TaskLog::getTaskId,id));
summaries.delete(new LambdaQueryWrapper<TaskSummary>().eq(TaskSummary::getTaskId,id));
tasks.deleteById(id);
}
}
