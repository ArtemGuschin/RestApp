package net.artem.restapp.service;

import lombok.RequiredArgsConstructor;
import net.artem.restapp.model.User;
import net.artem.restapp.repository.UserRepository;
import net.artem.restapp.repository.impl.UserRepositoryImpl;

import java.util.List;

@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository ;

    public UserService() {
        this.userRepository = new UserRepositoryImpl();

    }

    public User getById(Integer id) {
        return userRepository.getById(id);
    }

    public List<User> getAll() {
        return userRepository.getAll();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User update(User user) {
        return userRepository.update(user);
    }

    public void deleteById(Integer id) {
        userRepository.delete(id);
    }
}
