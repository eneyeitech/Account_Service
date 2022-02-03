package account;

import javax.validation.constraints.Size;

public class Password {
    @Size(min = 12, message = "The password length must be at least 12 chars!")
    private String new_password;

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }
}
