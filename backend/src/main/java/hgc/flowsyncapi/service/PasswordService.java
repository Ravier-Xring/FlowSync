package hgc.flowsyncapi.service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class PasswordService{
private final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
public String encode(String raw){return encoder.encode(raw);}
public boolean matches(String raw,String stored){
if(raw==null||stored==null)return false;
if(stored.startsWith("$2a$")||stored.startsWith("$2b$")||stored.startsWith("$2y$"))return encoder.matches(raw,stored);
return raw.equals(stored);
}
public boolean needsUpgrade(String stored){return stored==null||!stored.startsWith("$2");}
}
