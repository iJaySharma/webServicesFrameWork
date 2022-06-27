package com.thinking.machines.webRock.pojo;
public class ServiceResponse implements java.io.Serializable
{
private Object response;
private Object exception;
private boolean isSuccessful;
public void setResponse(Object response)
{
this.response=response;
}
public Object getResponse()
{
return this.response;
}
public void setException(Object exception)
{
this.exception=exception;
}
public Object getException()
{
return this.exception;
}
public void setIsSuccessful(boolean isSuccessful)
{
this.isSuccessful=isSuccessful;
}
public boolean getIsSuccessful()
{
return this.isSuccessful;
}
}