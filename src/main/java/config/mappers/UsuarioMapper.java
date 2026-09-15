package config.mappers;

import dto.request.UsuarioRequest;
import dto.response.UsuarioResponse;
import entities.Usuario;
import org.mapstruct.Mapping;

public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senha", ignore = true)
    @Mapping(target = "tentativasFalhas", ignore = true)
    @Mapping(target = "bloqueadoAte", ignore = true)
    Usuario toEntity(UsuarioRequest request);

    UsuarioResponse toResponse(Usuario usuario);
}
