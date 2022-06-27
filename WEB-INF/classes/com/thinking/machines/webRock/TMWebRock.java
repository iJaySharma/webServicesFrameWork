package com.thinking.machines.webRock;
import javax.servlet.http.*;
import javax.servlet.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import com.google.gson.*;
import com.thinking.machines.webRock.pojo.*;
import com.thinking.machines.webRock.model.*;
import com.thinking.machines.webRock.annotations.*;
import com.thinking.machines.webRock.exceptions.*;
public class TMWebRock extends HttpServlet
{
private boolean forwardedFromPost=false;

// implementing all logics on doGet for now, there are no difference between doGet and doPost except checking for METHOD TYPE of request
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
System.out.println("doGet of TMWebRock got called");
System.out.println("doGet of TMWebRock got called");
System.out.println("doGet of TMWebRock got called");
System.out.println("doGet of TMWebRock got called");
System.out.println("doGet of TMWebRock got called");
System.out.println("doGet of TMWebRock got called");
System.out.println("doGet of TMWebRock got called");
System.out.println("doGet of TMWebRock got called");
System.out.println("doGet of TMWebRock got called");
System.out.println("doGet of TMWebRock got called"+request.getRequestURI());
PrintWriter pw=null;
Gson gson=null;
try{
pw=response.getWriter();
System.out.println("1");
ServletContext servletContext=getServletContext();
WebRockModel webRockModel;
String key,methodType;
int index;
Service service;
Class serviceClass;
Method serviceMethod;
System.out.println("2");
response.setContentType("text/html");
webRockModel=(WebRockModel)servletContext.getAttribute("webRockModel");
key=request.getRequestURI().substring(request.getContextPath().length(),request.getRequestURI().length());
System.out.println("3");
index=key.indexOf("/",1);
key=key.substring(index,key.length());
System.out.println(key);
HashMap<String,Service> map=webRockModel.getMap();
service=map.get(key);
serviceClass=service.getServiceClass();
serviceMethod=service.getService();
//gson=new Gson();

// checking method type of request (GET/POST)
System.out.println("4");
if(this.forwardedFromPost==false)
{
if(!service.getIsGetAllowed() && service.getIsPostAllowed())
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
return;
}
}
else
{
this.forwardedFromPost=false;
if(service.getIsGetAllowed() && !service.getIsPostAllowed())
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
return;
}
}
System.out.println("5");

Object serviceClassObject=serviceClass.newInstance();
//serviceMethod.invoke(serviceClassObject);

ApplicationScope applicationScope=null;
SessionScope sessionScope=null;
RequestScope requestScope=null;
ApplicationDirectory applicationDirectory=null;

// checking for Secured Access




// checking for RequestParameters
String primitiveTypeString;
String keyString;
Object requestParameters[];
System.out.println("5.1");
LinkedHashMap<String,Class> requestParameterMap=service.getRequestParameterMap();
requestParameters=null;
System.out.println("5.2");
if(requestParameterMap!=null)
{
System.out.println("5.3");
Set<String> keySet=requestParameterMap.keySet();
requestParameters=new Object[keySet.size()];
System.out.println("5.4");
Iterator<String> itr=keySet.iterator();
System.out.println("5.5");
int integerValue;
Object obj;
System.out.println("Size of set: "+keySet.size());
for(int i=0;itr.hasNext();i++)
{
keyString=itr.next();
if(keyString.equals("__json"))
{
System.out.println("JSON Found");
// declaring here because we know that this if condition will get executed only once
BufferedReader bufferedReader=request.getReader();
StringBuffer stringBuffer=new StringBuffer();
String b;
String rawString;
while(true)
{
b=bufferedReader.readLine();
if(b==null) break;
stringBuffer.append(b);
}
rawString=stringBuffer.toString();
System.out.println(rawString);
requestParameters[i]=gson.fromJson(rawString,requestParameterMap.get(keyString));
continue;
}
// checking if the parameter is ApplicationScope/SessionScope/RequestScope/ApplicationDirectory
if(keyString.equals("__applicationScope"))
{
if(applicationScope==null)  applicationScope=new ApplicationScope(servletContext);
requestParameters[i]=applicationScope;
continue;
}
if(keyString.equals("__sessionScope"))
{
if(sessionScope==null) sessionScope=new SessionScope(request.getSession());
requestParameters[i]=sessionScope;
continue;
}
if(keyString.equals("__requestScope"))
{
if(requestScope==null) requestScope=new RequestScope(request);
requestParameters[i]=requestScope;
continue;
}
if(keyString.equals("__applicationDirectory"))
{
if(applicationDirectory==null) applicationDirectory=new ApplicationDirectory(new File(servletContext.getRealPath("")));
requestParameters[i]=applicationDirectory;
continue;
}

// checking if the parameter is of primitive type
if(requestParameterMap.get(keyString).isPrimitive())
{
primitiveTypeString=requestParameterMap.get(keyString).toString();
System.out.println(primitiveTypeString);
System.out.println("5.7.1");
if(primitiveTypeString.equalsIgnoreCase("int") || primitiveTypeString.equalsIgnoreCase("Integer"))
{
System.out.println("5.7.2");
System.out.println(keyString);
System.out.println(request.getParameter(keyString));
requestParameters[i]=Integer.parseInt(request.getParameter(keyString));
}
else if(primitiveTypeString.equalsIgnoreCase("long"))
{
System.out.println("5.7.3");
System.out.println(request.getParameter(keyString));
requestParameters[i]=Long.parseLong(request.getParameter(keyString));
}
else if(primitiveTypeString.equalsIgnoreCase("short"))
{
requestParameters[i]=Short.parseShort(request.getParameter(keyString));
}
else if(primitiveTypeString.equalsIgnoreCase("double"))
{
requestParameters[i]=Double.parseDouble(request.getParameter(keyString));
}
else if(primitiveTypeString.equalsIgnoreCase("float"))
{
requestParameters[i]=Float.parseFloat(request.getParameter(keyString));
}
else if(primitiveTypeString.equalsIgnoreCase("boolean"))
{
requestParameters[i]=Boolean.parseBoolean(request.getParameter(keyString));
}
else if(primitiveTypeString.equalsIgnoreCase("byte"))
{
requestParameters[i]=Byte.parseByte(request.getParameter(keyString));
}
else if(primitiveTypeString.equalsIgnoreCase("char"))
{
requestParameters[i]=request.getParameter(keyString).charAt(0);
}
}
else
{
System.out.println("5.9");
requestParameters[i]=request.getParameter(keyString);
}
}
}


Class classes[]=new Class[1];

// checking for IOC (INVERSE OF CONTROL)
classes[0]=Class.forName("com.thinking.machines.webRock.pojo.ApplicationScope");
if(service.getInjectApplicationScope() && serviceClass.getMethod("setApplicationScope",classes)!=null)
{
serviceClass.getMethod("setApplicationScope",classes).invoke(serviceClassObject,new ApplicationScope(servletContext));
}
classes[0]=Class.forName("com.thinking.machines.webRock.pojo.SessionScope");
if(service.getInjectSessionScope() && serviceClass.getMethod("setSessionScope",classes)!=null)
{
serviceClass.getMethod("setSessionScope",classes).invoke(serviceClassObject,new SessionScope(request.getSession()));
}
classes[0]=Class.forName("com.thinking.machines.webRock.pojo.RequestScope");
if(service.getInjectRequestScope() && serviceClass.getMethod("setRequestScope",classes)!=null)
{
serviceClass.getMethod("setRequestScope",classes).invoke(serviceClassObject,new RequestScope(request));
}
classes[0]=Class.forName("com.thinking.machines.webRock.pojo.ApplicationDirectory");
if(service.getInjectApplicationDirectory() && serviceClass.getMethod("setApplicationDirectory",classes)!=null)
{
	System.out.println("1.2");
serviceClass.getMethod("setApplicationDirectory",classes).invoke(serviceClassObject,new ApplicationDirectory(new File(servletContext.getRealPath(""))));
}
System.out.println("6");

// checking for IOC (INVERSE OF CONTROL) Next Level

//for testing purpose set xyz in application scope
servletContext.setAttribute("xyz",service.getServiceClass().newInstance());
autoWiredProperties=service.getAutoWiredProperties();
for(AutoWiredProperty autoWiredProperty:autoWiredProperties)
{
String name=autoWiredProperty.getName();
String methodName="set"+autoWiredProperty.getVariableName().substring(0,1).toUpperCase()+autoWiredProperty.getVariableName().substring(1,autoWiredProperty.getVariableName().length());
System.out.println("Method name is "+methodName);
Class Type=autoWiredProperty.getType();
Method autoWiredSetterMethod=service.getServiceClass().getMethod(methodName,autoWiredProperty.getType());
if(request.getAttribute(name)!=null && dataType.isInstance(request.getAttribute(name))==true)
{
//found in request scope and data type of data in request scope and autoWiredFields is same
autoWiredSetterMethod.invoke(obj,request.getAttribute(name));
}
else if(request.getSession().getAttribute(name)!=null && dataType.isInstance(request.getSession().getAttribute(name)))
{
//found in session 
autoWiredSetterMethod.invoke(obj,request.getSession().getAttribute(name));
}
else if(servletContext.getAttribute(autoWiredProperty.getName())!=null && Type.isInstance(servletContext.getAttribute(autoWiredProperty.getName())))
{
System.out.println("ok");
autoWiredSetterMethod.invoke(obj,servletContext.getAttribute(name));
System.out.println("ok");
}
}

System.out.println("control is here");

// checking for request forwarding
String forwardTo=service.getForwardTo();
System.out.println(forwardTo);
if(forwardTo!=null)
{
System.out.println("6.2");
if(map.containsKey(forwardTo))
{
System.out.println("6.3");
service=map.get(forwardTo);
String contextURI=request.getRequestURI();
int index1=contextURI.indexOf("/",1);
int index2=contextURI.indexOf("/",index1+1);
String str=contextURI.substring(index1,index2);

RequestDispatcher requestDispatcher=request.getRequestDispatcher(str+forwardTo);
requestDispatcher.forward(request,response);

}
else
{
RequestDispatcher requestDispatcher=request.getRequestDispatcher(forwardTo);
requestDispatcher.forward(request,response);
}
}
else
{
System.out.println("6.5");
}
}catch(Exception e)
{
System.out.println(e+" "+request.getRequestURI());
System.out.println(e.getCause());
}
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
System.out.println("doPost of TMWebRock got called");
System.out.println("doPost of TMWebRock got called");
System.out.println("doPost of TMWebRock got called");
System.out.println("doPost of TMWebRock got called");
System.out.println("doPost of TMWebRock got called");
System.out.println("doPost of TMWebRock got called");
System.out.println("doPost of TMWebRock got called");
System.out.println("doPost of TMWebRock got called");
System.out.println("doPost of TMWebRock got called");
this.forwardedFromPost=true;
doGet(request,response);
/*
try{
PrintWriter pw=response.getWriter();
WebRockModel webRockModel;
String key,methodType;
int index;
Service service;
Class serviceClass;
Method serviceMethod;
response.setContentType("text/html");
webRockModel=(WebRockModel)getServletContext().getAttribute("webRockModel");
key=request.getRequestURI().substring(request.getContextPath().length(),request.getRequestURI().length());
index=key.indexOf("/",1);
key=key.substring(index,key.length());
HashMap<String,Service> map=webRockModel.getMap();
service=map.get(key);
serviceClass=service.getServiceClass();
serviceMethod=service.getService();
Class get=Class.forName("com.thinking.machines.webRock.annotations.GET");
if(!service.getIsPostAllowed() && service.getIsGetAllowed())
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
return;
}
ServiceResponse serviceResponse=new ServiceResponse();
serviceResponse.setIsSuccessful(true);
serviceResponse.setResponse(service.getService().invoke(service.getServiceClass().newInstance()));
pw.println(gson.toJson(serviceResponse));
pw.flush();
}catch(Exception e)
{
System.out.println(e);
ServiceResponse serviceResponse=new ServiceResponse();
serviceResponse.setIsSuccessful(false);
serviceResponse.setException(e.getMessage());
pw.println(gson.toJson(serviceResponse));
pw.flush();
}
*/
}
}
