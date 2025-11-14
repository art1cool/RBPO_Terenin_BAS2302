package service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import repository.UserRepository;
import entity.UserEntity;
import model.User;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity getUser(String name) {
        return userRepository.findByName(name);
    }

    public UserEntity addUser(User userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        if (userRepository.findByName(userDto.getName()) != null) {
            throw new RuntimeException("Name already in use");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setName(userDto.getName());
        userEntity.setEmail(userDto.getEmail());
        // кодируем пароль перед сохранением!
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
        // по умолчанию роль — USER (можешь менять)
        if (userEntity.getRole() == null) {
            userEntity.setRole(enums.UserRole.USER);
        }
        return userRepository.save(userEntity);
    }

    public UserEntity registerUser(User userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        if (userRepository.findByName(userDto.getName()) != null) {
            throw new RuntimeException("Name already in use");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setName(userDto.getName());
        userEntity.setEmail(userDto.getEmail());
        // кодируем пароль перед сохранением!
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
        // по умолчанию роль — USER (можешь менять)
        if (userEntity.getRole() == null) {
            userEntity.setRole(enums.UserRole.USER);
        }
        return userRepository.save(userEntity);
    }

    public void removeUser(String name) {
        userRepository.delete(getUser(name));
    }

    public UserEntity updateUser(String name, User updatedFields) {
        UserEntity existing = userRepository.findByName(name);
        if (existing == null) {
            return null;
        }

        if (updatedFields.getName() != null && !updatedFields.getName().isBlank()) {
            existing.setName(updatedFields.getName());
        }
        if (updatedFields.getEmail() != null && !updatedFields.getEmail().isBlank()) {
            existing.setEmail(updatedFields.getEmail());
        }
        if (updatedFields.getPassword() != null && !updatedFields.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(updatedFields.getPassword()));
        }

        return userRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        // используем билдера Spring Security User
        org.springframework.security.core.userdetails.User.UserBuilder builder =
                org.springframework.security.core.userdetails.User.withUsername(userEntity.getEmail());

        builder.password(userEntity.getPassword());
        builder.authorities(userEntity.getRole().getGrantedAuthorities());

        // флаги наоборот: builder.accountLocked(true) — если locked = true, то доступ блокируется
        if (userEntity.isAccountLocked()) {
            builder.accountLocked(true);
        }
        if (userEntity.isAccountExpired()) {
            builder.accountExpired(true);
        }
        if (userEntity.isCredentialsExpired()) {
            builder.credentialsExpired(true);
        }
        if (userEntity.isDisabled()) {
            builder.disabled(true);
        }

        return builder.build();
    }
}
