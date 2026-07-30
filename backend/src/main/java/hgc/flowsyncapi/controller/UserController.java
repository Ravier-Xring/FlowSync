package hgc.flowsyncapi.controller;
import hgc.flowsyncapi.common.*;
import hgc.flowsyncapi.dto.PasswordUpdateRequest;
import hgc.flowsyncapi.entity.User;
import hgc.flowsyncapi.service.UserService;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/users")
public class UserController{
private final UserService s;
public UserController(UserService x){s=x;}
@GetMapping
public ApiResponse<PageResult<User>>list(@RequestParam(required=false)Integer page,@RequestParam(required=false)Integer size){
return ApiResponse.success(PageResult.of(s.list(),page,size));
}
@PostMapping("/update-password")
public ApiResponse<Void>update(@RequestBody PasswordUpdateRequest r,@RequestParam(required=false)Long currentUserId){s.updatePassword(r,currentUserId);return ApiResponse.success("密码修改成功",null);}
}
