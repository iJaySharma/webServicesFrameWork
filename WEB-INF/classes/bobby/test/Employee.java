package bobby.test;
import com.thinking.machines.webRock.annotations.*;
import com.thinking.machines.webRock.pojo.*;
@Path("/employee")
public class Employee
{
@AutoWired(name="username")
private String name;
public void setString(String name)
{
this.name=name;
}
@Path("/get")
public void get()
{
System.out.println("Get method got invoked and returned a String");
System.out.println(this.name);
}
}










