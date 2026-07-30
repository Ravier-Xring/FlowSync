package hgc.flowsyncapi.common;
import java.util.*;
public class PageResult<T>{
private long total;
private long page;
private long size;
private List<T>records;
public PageResult(){}
public PageResult(long total,long page,long size,List<T>records){this.total=total;this.page=page;this.size=size;this.records=records;}
public static<T>PageResult<T>of(List<T>all,Integer page,Integer size){
int pageNo=page==null||page<1?1:page;
int pageSize=size==null||size<1?10:Math.min(size,100);
int from=Math.min((pageNo-1)*pageSize,all.size());
int to=Math.min(from+pageSize,all.size());
return new PageResult<>(all.size(),pageNo,pageSize,all.subList(from,to));
}
public long getTotal(){return total;}
public void setTotal(long v){total=v;}
public long getPage(){return page;}
public void setPage(long v){page=v;}
public long getSize(){return size;}
public void setSize(long v){size=v;}
public List<T>getRecords(){return records;}
public void setRecords(List<T>v){records=v;}
}
