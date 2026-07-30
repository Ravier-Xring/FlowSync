package hgc.flowsyncapi.controller;
import hgc.flowsyncapi.common.*;
import hgc.flowsyncapi.entity.OperationLog;
import hgc.flowsyncapi.service.OperationLogService;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/operation-logs")
public class OperationLogController{
private final OperationLogService s;
public OperationLogController(OperationLogService x){s=x;}
@GetMapping
public ApiResponse<PageResult<OperationLog>>list(@RequestParam(required=false)Integer page,@RequestParam(required=false)Integer size){
AuthContext.requireLeader();
return ApiResponse.success(PageResult.of(s.list(),page,size));
}
}
