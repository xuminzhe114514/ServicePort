package com.example.demo.service.impl;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl extends BaseServiceImpl<User, Long, UserRepository>
        implements UserService {

    public UserServiceImpl(UserRepository repository) {super(repository);}

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User updatePassword(User user) {
        String pwd = user.getPassword();
        if (pwd != null && !isEncrypted(pwd)) {
            user.setPassword(passwordEncoder.encode(pwd));
        }
        return super.save(user);
    }

    private boolean isEncrypted(String password) {
        return password != null &&
                password.matches("^\\$2[ayb]\\$\\d{2}\\$[A-Za-z0-9./]{53}$");
    }

    @Override
    public User save(User user) {
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return super.save(user);
    }

    @Override
    public User matchPassword(User user, String password) {
        if (passwordEncoder.matches(password, user.getPassword())) return user;
        return null;
    }

    @Override
    public User findByUsername(String username) {
        return repository.findByUsername(username).orElse(null);
    }

    @Override
    public User login(String username, String password) {
        User user = findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword()) && user.getStatus() == 1) {
            return user;
        }
        return null;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<User> findByRole(String role) {
        return repository.findByRole(role);
    }

    @Override
    public User changeStatus(Long id, Integer status) {
        User user = findById(id);
        if (user != null) {
            user.setStatus(status);
            return super.save(user);
        }
        return null;
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public Page<User> findByKeyword(String keyword, Pageable pageable) {
        List<User> userResult = repository.searchUsers(keyword);
        return PageableExecutionUtils.getPage(userResult, pageable, userResult::size);
    }

    @Override
    public int countByRole(String role) {return repository.findByRole(role).size();}

    @Override
    public int countByUserStatus(Integer userStatus) {return repository.findByStatus(userStatus).size();}

    @Override
    public int countAll() {return repository.findAll().size();}
}