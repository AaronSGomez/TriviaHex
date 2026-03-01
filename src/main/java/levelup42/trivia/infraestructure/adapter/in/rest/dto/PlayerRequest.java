package levelup42.trivia.infraestructure.adapter.in.rest.dto;
import jakarta.validation.constraints.NotBlank;

public class PlayerRequest {

    @NotBlank(message = "The name cannot be empty")
    private String name;
    @NotBlank(message = "The mail cannot be empty")
    private String mail;

    public PlayerRequest() {
    }

    public PlayerRequest(String name, String mail) {
        this.name = name;
        this.mail = mail;
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
}
