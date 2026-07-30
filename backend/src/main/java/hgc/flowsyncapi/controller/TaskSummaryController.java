package hgc.flowsyncapi.controller;
import hgc.flowsyncapi.common.*;
import hgc.flowsyncapi.entity.TaskSummary;
import hgc.flowsyncapi.service.TaskSummaryService;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/summaries")
public class TaskSummaryController{
private final TaskSummaryService s;
public TaskSummaryController(TaskSummaryService x){s=x;}
@GetMapping
public ApiResponse<PageResult<TaskSummary>>list(@RequestParam(required=false)Integer page,@RequestParam(required=false)Integer size){
return ApiResponse.success(PageResult.of(s.list(),page,size));
}
@PostMapping
public ApiResponse<TaskSummary>save(@RequestBody TaskSummary x,@RequestParam(required=false)Long currentUserId){return ApiResponse.success(s.save(x,currentUserId));}
}
