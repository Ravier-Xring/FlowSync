package hgc.flowsyncapi.controller;
import hgc.flowsyncapi.common.*;
import hgc.flowsyncapi.entity.TaskInfo;
import hgc.flowsyncapi.service.TaskInfoService;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/tasks")
public class TaskController{
private final TaskInfoService s;
public TaskController(TaskInfoService x){s=x;}
@GetMapping
public ApiResponse<PageResult<TaskInfo>>list(@RequestParam(required=false)Long projectId,@RequestParam(required=false)Integer page,@RequestParam(required=false)Integer size){
return ApiResponse.success(PageResult.of(s.list(projectId),page,size));
}
@PostMapping
public ApiResponse<TaskInfo>save(@RequestBody TaskInfo x,@RequestParam(required=false)Long currentUserId){return ApiResponse.success(s.save(x,currentUserId));}
@DeleteMapping("/{id}")
public ApiResponse<Void>delete(@PathVariable Long id){s.delete(id);return ApiResponse.success("任务删除成功",null);}
}
