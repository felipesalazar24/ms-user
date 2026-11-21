package cl.sanos_y_salvos.ms_base.api.service;

import cl.sanos_y_salvos.ms_base.api.DTO.AuditEventDTO;
import cl.sanos_y_salvos.ms_base.api.DTO.UserDto;
import cl.sanos_y_salvos.ms_base.api.model.User;
import cl.sanos_y_salvos.ms_base.api.producer.AuditKafkaProducer;
import cl.sanos_y_salvos.ms_base.api.repository.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditKafkaProducer auditKafkaProducer;

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el id: " + id));
        return entityToDto(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = dtoToEntity(userDto);
        User savedUser = userRepository.save(user);

        auditKafkaProducer.sendAuditEvent(buildAuditEvent("USER_CREATED", savedUser));

        return entityToDto(savedUser);
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el id: " + id));

        updateEntityFromDto(existingUser, userDto);
        User updatedUser = userRepository.save(existingUser);

        auditKafkaProducer.sendAuditEvent(buildAuditEvent("USER_UPDATED", updatedUser));

        return entityToDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el id: " + id));

        userRepository.deleteById(id);

        auditKafkaProducer.sendAuditEvent(buildAuditEvent("USER_DELETED", existingUser));
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
        return entityToDto(user);
    }

    private AuditEventDTO buildAuditEvent(String eventType, User user) {
        String name = user.getName() != null ? user.getName() : "";
        String lastName = user.getLastName() != null ? user.getLastName() : "";
        String fullName = (name + " " + lastName).trim();

        return new AuditEventDTO(
                UUID.randomUUID().toString(),
                eventType,
                Instant.now(),
                user.getId(),
                fullName,
                user.getEmail(),
                user.getCity(),
                user.getCountry(),
                user.getRole()
        );
    }

    private User dtoToEntity(UserDto dto) {
        User entity = new User();
        entity.setName(dto.getName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setAddress(dto.getAddress());
        entity.setAddressNumber(dto.getAddressNumber());
        entity.setCity(dto.getCity());
        entity.setCountry(dto.getCountry());
        entity.setRole(dto.getRole());
        return entity;
    }

    private UserDto entityToDto(User entity) {
        UserDto dto = new UserDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setAddress(entity.getAddress());
        dto.setAddressNumber(entity.getAddressNumber());
        dto.setCity(entity.getCity());
        dto.setCountry(entity.getCountry());
        dto.setRole(entity.getRole());
        dto.setPassword(entity.getPassword());
        return dto;
    }

    private void updateEntityFromDto(User entity, UserDto dto) {
        entity.setName(dto.getName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setAddress(dto.getAddress());
        entity.setAddressNumber(dto.getAddressNumber());
        entity.setCity(dto.getCity());
        entity.setCountry(dto.getCountry());
        entity.setRole(dto.getRole());
    }
}