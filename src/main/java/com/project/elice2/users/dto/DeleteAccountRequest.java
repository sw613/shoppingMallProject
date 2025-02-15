package com.project.elice2.users.dto;

import com.project.elice2.users.validation.MustBeTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteAccountRequest {

    @MustBeTrue
    private boolean agreeToWithdrawal;

}
