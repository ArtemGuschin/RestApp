package net.artem.restapp.dto;

public record EventDTO(Integer id,
                       UserDTO userDTO,
                       FileDTO fileDTO) {

}
