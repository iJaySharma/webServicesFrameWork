package com.thinking.machines.webRock;
import com.thinking.machines.webRock.annotations.*;
import com.thinking.machines.webRock.pojo.*;
import com.thinking.machines.webRock.model.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.nio.*;
import java.lang.reflect.*;
import java.lang.annotation.*;
public class TMWebRockStartup extends HttpServlet
{
private String pathToClasses;
private WebRockModel webRockModel;
private List<Service> startupServices;
public void init()
{
this.webRockModel=new WebRockModel();
this.startupServices=new ArrayList<Service>();
ServletConfig servletConfig=getServletConfig();
System.out.println("");
System.out.println("");
System.out.println("");
System.out.println("");
System.out.println("");
System.out.println("");
System.out.println("");
System.out.println("");
System.out.println("");
System.out.println("");
String servicePackagePrefix=servletConfig.getInitParameter("SERVICE_PACKAGE_PRIFIX");
String path=getServletContext().getRealPath("/WEB-INF/classes/")+servicePackagePrefix;
this.pathToClasses=getServletContext().getRealPath("/WEB-INF/classes/");
System.out.println(path);
try{
File file=new File(path);
populateModel(file);

getServletContext().setAttribute("webRockModel",this.webRockModel);
System.out.println("StartupServices size: "+this.startupServices.size());
for(Service s: this.startupServices)
{
System.out.println("Priority: "+s.getPriority());
s.getService().invoke(s.getServiceClass().newInstance());
}
}catch(Exception e)
{
System.out.println("");
System.out.println("");
System.out.println("");
System.out.println("");
System.out.println("");
System.out.println("");
System.out.println(e);
System.out.println("");
System.out.println("");
System.out.println("");
System.out.println("");
System.out.println("");
System.out.println("");
System.out.println("");
}
}

public void populateModel(File file)
{

try{
File files[]=file.listFiles();
Class pathAnnotation=Class.forName("com.thinking.machines.webRock.annotations.Path");
Class GETAnnotation=Class.forName("com.thinking.machines.webRock.annotations.GET");
Class POSTAnnotation=Class.forName("com.thinking.machines.webRock.annotations.POST");
Class forwardAnnotation=Class.forName("com.thinking.machines.webRock.annotations.Forward");
Class onStartupAnnotation=Class.forName("com.thinking.machines.webRock.annotations.OnStartup");
Class injectApplicationScopeAnnotation=Class.forName("com.thinking.machines.webRock.annotations.InjectApplicationScope");
Class injectRequestScopeAnnotation=Class.forName("com.thinking.machines.webRock.annotations.InjectRequestScope");
Class injectSessionScopeAnnotation=Class.forName("com.thinking.machines.webRock.annotations.InjectSessionScope");
Class injectApplicationDirectoryAnnotation=Class.forName("com.thinking.machines.webRock.annotations.InjectApplicationDirectory");
Class autoWiredAnnotation=Class.forName("com.thinking.machines.webRock.annotations.AutoWired");

boolean injectApplicationScope,injectSessionScope,injectRequestScope,injectApplicationDirectory;
String classPathAnnotationValue;
String methodPathAnnotationValue;
Class c=null;
Method methods[]=null;
String key="";
Service service;
String pathToFile;
int priority;
List<AutoWiredProperty> autoWiredProperties;
AutoWiredProperty autoWiredProperty;
Field fields[];
LinkedHashMap<String,Class> requestParameterMap=null;
int numberOfParameters;
Annotation requestParameterAnnotations[][];
Class requestParameterTypes[];
//List<InjectRequestParameterPOJO> injectRequestParameters=new ArrayList<>();
int j;
int classTypeParameterCount=0;
int requestParameterCount=0;
int scopeOrDirectoryParameterCount=0;


for(File f:files)
{
	
if(f.isDirectory()) populateModel(f);
else
{
pathToFile=f.getPath();
System.out.println(pathToFile);
if(pathToFile.contains(".class"))
{
pathToFile=pathToFile.substring(pathToFile.indexOf("classes")+8,pathToFile.length()).replace("\\",".");
c=Class.forName(pathToFile.substring(0,pathToFile.length()-6));
injectApplicationScope=c.getAnnotation(injectApplicationScopeAnnotation)!=null;
injectSessionScope=c.getAnnotation(injectSessionScopeAnnotation)!=null;
injectRequestScope=c.getAnnotation(injectRequestScopeAnnotation)!=null;
injectApplicationDirectory=c.getAnnotation(injectApplicationDirectoryAnnotation)!=null;
autoWiredProperties=new ArrayList<>();

// making list of Auto wired properties

fields=c.getDeclaredFields();
System.out.println(pathToFile);
int len=0;

// Iterating over fields
for(Field field : fields)
{
len++;
if(field.getAnnotation(autoWiredAnnotation)!=null)
{
	System.out.println("test case 1");
autoWiredProperty=new AutoWiredProperty();
autoWiredProperty.setName(((AutoWired)field.getAnnotation(autoWiredAnnotation)).name());
autoWiredProperty.setType(field.getType());
autoWiredProperties.add(autoWiredProperty);
System.out.println("test case 1.1");
}
} 
 
// Iterating over methods
methods=c.getMethods();
if(c.getAnnotation(pathAnnotation)!=null)
{
// js file generation starts here


// js file generation ends here
classPathAnnotationValue=((Path)c.getAnnotation(pathAnnotation)).value();
for(Method method: methods)
{
if(method.getAnnotation(pathAnnotation)!=null)
{
methodPathAnnotationValue=((Path)method.getAnnotation(pathAnnotation)).value();
key=classPathAnnotationValue+methodPathAnnotationValue;
service=new Service();
service.setServiceClass(c);
service.setService(method);
service.setPath(key);
service.setIsGetAllowed(method.getAnnotation(GETAnnotation)!=null  ||  c.getAnnotation(GETAnnotation)!=null);
service.setIsPostAllowed(method.getAnnotation(POSTAnnotation)!=null  ||  c.getAnnotation(POSTAnnotation)!=null);
if(method.getAnnotation(forwardAnnotation)!=null) service.setForwardTo(((Forward)method.getAnnotation(forwardAnnotation)).value());
else service.setForwardTo(null);
service.setInjectApplicationScope(injectApplicationScope);
service.setInjectSessionScope(injectSessionScope);
service.setInjectRequestScope(injectRequestScope);
service.setInjectApplicationDirectory(injectApplicationDirectory);
service.setAutoWiredProperties(autoWiredProperties);
//service.setInjectRequestParameters(injectRequestParameters);

//checking for RequestParameters
numberOfParameters=method.getParameterCount();
classTypeParameterCount=0;
requestParameterCount=0;
scopeOrDirectoryParameterCount=0;

if(numberOfParameters>0)
{
requestParameterMap=new LinkedHashMap<String,Class>();
requestParameterAnnotations=method.getParameterAnnotations();
requestParameterTypes=method.getParameterTypes();
System.out.println("Number of parameters: "+numberOfParameters);

for(j=0;j<numberOfParameters;j++)
{
if(requestParameterTypes[j].getSimpleName().equals("ApplicationScope"))
{
requestParameterMap.put("__applicationScope",requestParameterTypes[j]);
scopeOrDirectoryParameterCount++;
}
else if(requestParameterTypes[j].getSimpleName().equals("SessionScope"))
{
requestParameterMap.put("__sessionScope",requestParameterTypes[j]);
scopeOrDirectoryParameterCount++;
}
else if(requestParameterTypes[j].getSimpleName().equals("RequestScope"))
{
requestParameterMap.put("__requestScope",requestParameterTypes[j]);
scopeOrDirectoryParameterCount++;
}
else if(requestParameterTypes[j].getSimpleName().equals("ApplicationDirectory"))
{
requestParameterMap.put("__applicationDirectory",requestParameterTypes[j]);
scopeOrDirectoryParameterCount++;
}
else
{
requestParameterMap.put(((RequestParameter)requestParameterAnnotations[j][0]).key(),requestParameterTypes[j]);
requestParameterCount++;
}
}//168

if(classTypeParameterCount>1 ||  (classTypeParameterCount==1 && requestParameterCount>0) ||  (classTypeParameterCount==1 &&  (classTypeParameterCount+scopeOrDirectoryParameterCount)!=numberOfParameters))
{
System.out.println("classTypeParameterCount: "+classTypeParameterCount);
System.out.println("scopeOrDirectoryParameterCount: "+scopeOrDirectoryParameterCount);
System.out.println("requestParameterCount"+requestParameterCount);
throw new Exception("Parameter types of method "+method.getName()+" are malformed");
}
service.setRequestParameterMap(requestParameterMap);
}//161
else
{
service.setRequestParameterMap(null);
}

/*
Set<String> keySet=requestParameterMap.keySet();
Class val;
String queryString="";
int ii=0;
String keyForQueryString="DEFAULT_VALUE";
if(numberOfParameters>0)
{
for(String key2:keySet)
{
keyForQueryString=key2;
if(key2=="__applicationScope" || key2=="__sessionScope" || key2=="__requestScope" || key2=="__applicationDirectory") continue;
val=requestParameterMap.get(key2);
if(val.getSimpleName()=="int" || val.getSimpleName()=="Integer")
{
if(ii!=0) queryString+="&";
ii++;
//tmpRandomAccessFile.writeBytes("if((Number.isInteger("+key2+"))==false)\r\n{\r\nreject(\"int should be passed to "+method.getName()+" method\");\r\nreturn;\r\n}\r\n");
queryString=queryString+key2+"=\"+encodeURI("+key2+")";
}
else if(val.getSimpleName()=="String" || val.getSimpleName()=="java.lang.String")
{
if(ii!=0) queryString+="&";
ii++;
//tmpRandomAccessFile.writeBytes("if("+key2+" instanceof String)==false)\r\n{\r\nreject(\"String should be passed to "+method.getName()+" method\");\r\nreturn;\r\n}\r\n");
queryString=queryString+key2+"=\"+encodeURI("+key2+")";
}
else if(val.isPrimitive()==false)
{
keyForQueryString=(char)(val.getSimpleName().charAt(0)+32)+val.getSimpleName().substring(1);
//tmpRandomAccessFile.writeBytes("if(("+(char)(val.getSimpleName().charAt(0)+32)+val.getSimpleName().substring(1)+" instanceof "+val.getSimpleName()+")==false)\r\n{\r\n");
//tmpRandomAccessFile.writeBytes("reject(\"instance of "+val.getSimpleName()+" class should be passed to "+method.getName()+" method\");\r\nreturn;\r\n}\r\n");
}
}
}
*/
//if(urlPattern.length()==0)for(String s: mappings) urlPattern=s;

this.webRockModel.addToMap(key,service);
}
}
}
}
for(Method method: methods)
{
if(method.getAnnotation(onStartupAnnotation)!=null)
{
priority=((OnStartup)method.getAnnotation(onStartupAnnotation)).priority();
service=new Service();
service.setServiceClass(c);
service.setService(method);
service.setRunOnStart(true);
service.setPriority(priority);
addToStartupServices(service);
}
}

}
}
}catch(Exception e)
{
System.out.println("ERROR");
System.out.println("ERROR");
System.out.println("ERROR");
System.out.println("ERROR");
System.out.println("ERROR");
System.out.println("ERROR");
System.out.println("ERROR");
System.out.println("ERROR");
System.out.println(e);
System.out.println(e.getCause());
System.out.println("ERROR");
System.out.println("ERROR");
System.out.println("ERROR");
System.out.println("ERROR");
System.out.println("ERROR");
System.out.println("ERROR");
System.out.println("ERROR");
System.out.println("ERROR");
System.out.println("ERROR");
System.out.println("ERROR");
System.out.println("ERROR");
System.out.println("ERROR");
}
}
public void addToStartupServices(Service service)
{
System.out.println("addToStartupServices called for "+service.getPath());
try{
int priority=service.getPriority();
Method serviceMethod=service.getService();
int i;
if(serviceMethod.getReturnType()==void.class)
{
if(serviceMethod.getParameterCount()==0)
{
for(i=0;i<this.startupServices.size();i++)
{
if(this.startupServices.get(i).getPriority()>=priority) break;
}
this.startupServices.add(i,service);
System.out.println("added");
}
else
{
System.out.println("Service "+service.getServiceClass().getName()+"::"+serviceMethod.getName()+" can not be added to Startup Services as it does not have zero paramters");
}
}
else
{
System.out.println("Service "+service.getServiceClass().getName()+"::"+serviceMethod.getName()+" can not be added to Startup Services as it does not have void as return type");
}
}catch(Exception e)
{
System.out.println("");
System.out.println("");
System.out.println("");
System.out.println("");
System.out.println("");
System.out.println(e);
System.out.println("");
System.out.println("");
System.out.println("");
System.out.println("");
System.out.println("");
System.out.println("");
}
}
}