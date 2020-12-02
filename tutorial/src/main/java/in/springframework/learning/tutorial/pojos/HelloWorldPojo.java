package in.springframework.learning.tutorial.pojos;

public class HelloWorldPojo {

    public HelloWorldPojo() {
        name = "Everybody";
        message = String.format(message, name);
    }

    public HelloWorldPojo(String name) {
        this.name = name;
        message = String.format(message, name);
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String name;
    private String message = "Hello World, %s";
}
