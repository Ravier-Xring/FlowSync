package hgc.flowsyncapi.service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hgc.flowsyncapi.entity.*;
import hgc.flowsyncapi.mapper.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class OverviewService{
private final UserMapper users;
private final ProjectInfoMapper projects;
private final TaskInfoMapper tasks;
private final TaskSummaryMapper summaries;
private final TaskLogMapper logs;
public OverviewService(UserMapper a,ProjectInfoMapper b,TaskInfoMapper c,TaskSummaryMapper d,TaskLogMapper e){
users=a;projects=b;tasks=c;summaries=d;logs=e;
}
public Map<String,Object>get(Long currentUserId){
List<ProjectInfo> projectList=projects.selectList(new LambdaQueryWrapper<ProjectInfo>().orderByDesc(ProjectInfo::getId));
List<TaskInfo> taskList=tasks.selectList(new LambdaQueryWrapper<TaskInfo>().orderByDesc(TaskInfo::getId));
List<TaskSummary> summaryList=summaries.selectList(new LambdaQueryWrapper<TaskSummary>().orderByDesc(TaskSummary::getId));
List<TaskLog> logList=logs.selectList(new LambdaQueryWrapper<TaskLog>().orderByDesc(TaskLog::getId));
if(!hgc.flowsyncapi.common.AuthContext.isLeader()){
Long uid=hgc.flowsyncapi.common.AuthContext.userId();
taskList=taskList.stream().filter(t->Objects.equals(t.getAssigneeId(),uid)||Objects.equals(t.getCreatorId(),uid)).collect(Collectors.toList());
Set<Long>visibleProjectIds=taskList.stream().map(TaskInfo::getProjectId).collect(Collectors.toSet());
Set<Long>projectIdsFromTasks=visibleProjectIds;
projectList=projectList.stream().filter(p->Objects.equals(p.getOwnerId(),uid)||projectIdsFromTasks.contains(p.getId())).collect(Collectors.toList());
Set<Long>finalVisibleProjectIds=projectList.stream().map(ProjectInfo::getId).collect(Collectors.toSet());
Set<Long>visibleTaskIds=taskList.stream().map(TaskInfo::getId).collect(Collectors.toSet());
summaryList=summaryList.stream().filter(s->finalVisibleProjectIds.contains(s.getProjectId())).collect(Collectors.toList());
logList=logList.stream().filter(l->visibleTaskIds.contains(l.getTaskId())).collect(Collectors.toList());
}
LocalDate today=LocalDate.now();
LocalDate soon=today.plusDays(7);
List<TaskInfo> myTasks=currentUserId==null?Collections.emptyList():taskList.stream().filter(t->Objects.equals(t.getAssigneeId(),currentUserId)).collect(Collectors.toList());
Map<String,Object>m=new LinkedHashMap<>();
m.put("userCount",users.selectCount(null));
m.put("projectCount",projectList.size());
m.put("taskCount",taskList.size());
m.put("summaryCount",summaryList.size());
m.put("logCount",logList.size());
m.put("activeProjectCount",countByStatus(projectList,"进行中"));
m.put("finishedProjectCount",countByStatus(projectList,"已完成"));
m.put("openTaskCount",taskList.stream().filter(t->!"已完成".equals(t.getStatus())).count());
m.put("finishedTaskCount",countByStatus(taskList,"已完成"));
m.put("overdueTaskCount",taskList.stream().filter(t->isOverdue(t,today)).count());
m.put("dueSoonTaskCount",taskList.stream().filter(t->isDueSoon(t,today,soon)).count());
m.put("taskDoneRate",rate(countByStatus(taskList,"已完成"),taskList.size()));
m.put("projectDoneRate",rate(countByStatus(projectList,"已完成"),projectList.size()));
m.put("myTaskCount",myTasks.size());
m.put("myOpenTaskCount",myTasks.stream().filter(t->!"已完成".equals(t.getStatus())).count());
m.put("myFinishedTaskCount",myTasks.stream().filter(t->"已完成".equals(t.getStatus())).count());
m.put("myDueSoonTaskCount",myTasks.stream().filter(t->isDueSoon(t,today,soon)).count());
m.put("projectStatusStats",statusStats(projectList,ProjectInfo::getStatus,new String[]{"未开始","进行中","已完成"}));
m.put("taskStatusStats",statusStats(taskList,TaskInfo::getStatus,new String[]{"未开始","进行中","已完成"}));
m.put("taskPriorityStats",statusStats(taskList,TaskInfo::getPriority,new String[]{"高","中","低"}));
m.put("summaryTypeStats",statusStats(summaryList,TaskSummary::getSummaryType,new String[]{"阶段总结","最终总结"}));
m.put("recentProjects",limit(projectList,5));
m.put("recentTasks",limit(taskList,6));
m.put("recentSummaries",limit(summaryList,5));
m.put("recentLogs",limit(logList,5));
m.put("myRecentTasks",limit(myTasks.stream().sorted(Comparator.comparing(TaskInfo::getId,Comparator.nullsLast(Long::compareTo)).reversed()).collect(Collectors.toList()),5));
return m;
}
private boolean isOverdue(TaskInfo t,LocalDate today){return t.getDueDate()!=null&&t.getDueDate().isBefore(today)&&!"已完成".equals(t.getStatus());}
private boolean isDueSoon(TaskInfo t,LocalDate today,LocalDate soon){return t.getDueDate()!=null&&!t.getDueDate().isBefore(today)&&!t.getDueDate().isAfter(soon)&&!"已完成".equals(t.getStatus());}
private long countByStatus(List<? extends Object> list,String status){
return list.stream().filter(x->Objects.equals(status,x instanceof ProjectInfo?((ProjectInfo)x).getStatus():((TaskInfo)x).getStatus())).count();
}
private int rate(long done,int total){return total==0?0:(int)Math.round(done*100.0/total);}
private <T>List<T>limit(List<T>list,int size){return list.stream().limit(size).collect(Collectors.toList());}
private <T>List<Map<String,Object>>statusStats(List<T>list,java.util.function.Function<T,String>getter,String[]names){
Map<String,Long>counts=list.stream().collect(Collectors.groupingBy(x->Optional.ofNullable(getter.apply(x)).orElse("未设置"),LinkedHashMap::new,Collectors.counting()));
List<Map<String,Object>>result=new ArrayList<>();
for(String name:names)result.add(stat(name,counts.getOrDefault(name,0L)));
counts.forEach((name,value)->{if(Arrays.stream(names).noneMatch(name::equals))result.add(stat(name,value));});
return result;
}
private Map<String,Object>stat(String name,long value){Map<String,Object>m=new LinkedHashMap<>();m.put("name",name);m.put("value",value);return m;}
}
