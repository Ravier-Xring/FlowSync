package hgc.flowsyncapi.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import hgc.flowsyncapi.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
@Service
public class JwtService{
private final ObjectMapper json;
private final byte[]secret;
public JwtService(ObjectMapper j,@Value("${jwt.secret:flowsync-local-secret}")String key){json=j;secret=key.getBytes(StandardCharsets.UTF_8);}
public String create(User u){
try{
Map<String,Object>header=Map.of("alg","HS256","typ","JWT");
Map<String,Object>payload=new LinkedHashMap<>();
payload.put("sub",String.valueOf(u.getId()));
payload.put("username",u.getUsername());
payload.put("role",u.getRole());
payload.put("exp",Instant.now().plusSeconds(60L*60*24*7).getEpochSecond());
String h=enc(json.writeValueAsBytes(header));
String p=enc(json.writeValueAsBytes(payload));
return h+"."+p+"."+sign(h+"."+p);
}catch(Exception e){throw new IllegalStateException("生成登录令牌失败");}
}
public Long parseUserId(String token){
try{
String[]parts=token.split("\\.");
if(parts.length!=3||!Objects.equals(sign(parts[0]+"."+parts[1]),parts[2]))return null;
Map<?,?>payload=json.readValue(Base64.getUrlDecoder().decode(parts[1]),Map.class);
Number exp=(Number)payload.get("exp");
if(exp==null||exp.longValue()<Instant.now().getEpochSecond())return null;
Object sub=payload.get("sub");
return sub==null?null:Long.valueOf(String.valueOf(sub));
}catch(Exception e){return null;}
}
private String sign(String value)throws Exception{
Mac mac=Mac.getInstance("HmacSHA256");
mac.init(new SecretKeySpec(secret,"HmacSHA256"));
return enc(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
}
private String enc(byte[]bytes){return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);}
}
