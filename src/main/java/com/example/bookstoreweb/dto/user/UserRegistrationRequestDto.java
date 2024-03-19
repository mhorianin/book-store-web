package com.example.bookstoreweb.dto.user;

import com.example.bookstoreweb.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@FieldMatch(field = "password", fieldMatch = "repeatPassword")
public class UserRegistrationRequestDto {
    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    @Size(min = 6, max = 20)
    private String password;
    @Size(min = 6, max = 20)
    private String repeatPassword;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String shippingAddress;

}
