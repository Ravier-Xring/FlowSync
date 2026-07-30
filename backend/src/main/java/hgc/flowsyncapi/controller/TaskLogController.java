package hgc.flowsyncapi.controller;
import hgc.flowsyncapi.common.*;
import hgc.flowsyncapi.entity.TaskLog;
import hgc.flowsyncapi.service.TaskLogService;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/task-logs")
public class TaskLogController{
private final TaskLogService s;
public TaskLogController(TaskLogService x){s=x;}
@GetMapping
public ApiResponse<PageResult<TaskLog>>list(@RequestParam(required=false)Long taskId,@RequestParam(required=false)Integer page,@RequestParam(required=false)Integer size){
return ApiResponse.success(PageResult.of(s.list(taskId),page,size));
}
@PostMapping
public ApiResponse<TaskLog>save(@RequestBody TaskLog x,@RequestParam(required=false)Long currentUserId){return ApiResponse.success(s.save(x,currentUserId));}
}
