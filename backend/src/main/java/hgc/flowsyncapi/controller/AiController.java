package hgc.flowsyncapi.controller;
import hgc.flowsyncapi.common.*;
import hgc.flowsyncapi.dto.*;
import hgc.flowsyncapi.service.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api/ai")
public class AiController{
private final QwenService q;
private final UserService u;
private final TaskInfoService t;
public AiController(QwenService a,UserService b,TaskInfoService c){q=a;u=b;t=c;}
@PostMapping("/task-suggestion")
public ApiResponse<Map<String,String>>suggest(@RequestBody AiTaskSuggestionRequest r){
AuthContext.requireLeader();
return ApiResponse.success(Map.of("suggestion",q.suggestion(r)));
}
@PostMapping("/task-plan")
public ApiResponse<AiTaskPlanResponse>plan(@RequestBody AiTaskPlanRequest r){
AuthContext.requireLeader();
return ApiResponse.success(q.plan(r,u.list()));
}
@PostMapping("/task-plan/import")
public ApiResponse<Map<String,Integer>>importPlan(@RequestBody AiTaskPlanImportRequest r){
AuthContext.requireLeader();
return ApiResponse.success(Map.of("importedCount",t.importPlan(r)));
}
}
