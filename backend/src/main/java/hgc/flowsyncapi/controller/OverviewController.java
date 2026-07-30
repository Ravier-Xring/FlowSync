package hgc.flowsyncapi.controller;
import hgc.flowsyncapi.common.ApiResponse;
import hgc.flowsyncapi.service.OverviewService;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api/overview")
public class OverviewController{
private final OverviewService s;
public OverviewController(OverviewService x){s=x;}
@GetMapping
public ApiResponse<Map<String,Object>>get(@RequestParam(required=false)Long currentUserId){
return ApiResponse.success(s.get(currentUserId));
}
}
