package levelup42.trivia.infraestructure.adapter.in.rest.dto;
import jakarta.validation.constraints.NotBlank;

public class PlayerRequest {

    @NotBlank(message = "The name cannot be empty")
    private String name;
    @NotBlank(message = "The mail cannot be empty")
    private String mail;
    
    @NotBlank(message = "The password cannot be empty")
    private String password;

    public PlayerRequest() {
    }

    public PlayerRequest(String name, String mail, String password) {
        this.name = name;
        this.mail = mail;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
