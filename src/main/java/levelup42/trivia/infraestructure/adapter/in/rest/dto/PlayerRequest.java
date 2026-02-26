package levelup42.trivia.infraestructure.adapter.in.rest.dto;

public class PlayerRequest {

    private String name;
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
