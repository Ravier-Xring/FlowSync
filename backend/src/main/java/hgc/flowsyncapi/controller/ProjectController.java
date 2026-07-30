package hgc.flowsyncapi.controller;
import hgc.flowsyncapi.common.*;
import hgc.flowsyncapi.entity.ProjectInfo;
import hgc.flowsyncapi.service.ProjectInfoService;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/projects")
public class ProjectController{
private final ProjectInfoService s;
public ProjectController(ProjectInfoService x){s=x;}
@GetMapping
public ApiResponse<PageResult<ProjectInfo>>list(@RequestParam(required=false)Integer page,@RequestParam(required=false)Integer size){
return ApiResponse.success(PageResult.of(s.list(),page,size));
}
@PostMapping
public ApiResponse<ProjectInfo>save(@RequestBody ProjectInfo x,@RequestParam(required=false)Long currentUserId){return ApiResponse.success(s.save(x,currentUserId));}
@DeleteMapping("/{id}")
public ApiResponse<Void>delete(@PathVariable Long id){s.delete(id);return ApiResponse.success("项目删除成功",null);}
}
