package dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ClienteRequest {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @Size(max = 20, message = "O telefoine deve ter 20 caracteres")
    private String telefone;

    @Email(message = "Informe um email valido")
    @Size(max = 225, message = "O email deve ter até 255 caracteres")
    private String email;

}
