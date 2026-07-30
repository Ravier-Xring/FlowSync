package hgc.flowsyncapi.service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hgc.flowsyncapi.common.AuthContext;
import hgc.flowsyncapi.entity.OperationLog;
import hgc.flowsyncapi.mapper.OperationLogMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class OperationLogService{
private final OperationLogMapper mapper;
private final JdbcTemplate jdbc;
public OperationLogService(OperationLogMapper m,JdbcTemplate j){mapper=m;jdbc=j;}
@PostConstruct
public void init(){
jdbc.execute("CREATE TABLE IF NOT EXISTS operation_log(id BIGINT PRIMARY KEY AUTO_INCREMENT,operator_id BIGINT,module VARCHAR(50) NOT NULL,action VARCHAR(50) NOT NULL,target_id BIGINT,detail VARCHAR(1000),create_time DATETIME DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
}
public void record(String module,String action,Long targetId,String detail){
try{
OperationLog log=new OperationLog();
log.setOperatorId(AuthContext.userId());
log.setModule(module);
log.setAction(action);
log.setTargetId(targetId);
log.setDetail(detail);
mapper.insert(log);
}catch(Exception ignored){}
}
public List<OperationLog>list(){return mapper.selectList(new LambdaQueryWrapper<OperationLog>().orderByDesc(OperationLog::getId));}
}
