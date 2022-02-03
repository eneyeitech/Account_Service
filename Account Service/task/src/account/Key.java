package account;

import javax.validation.constraints.NotEmpty;

public class Key {
    @NotEmpty
    private String user;
    private String operation;

    public Key() {}

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
