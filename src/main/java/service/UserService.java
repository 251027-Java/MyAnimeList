package service;

import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.UserRepository;

@Service
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public User registerUser(String username, String password){
        if(username.isBlank() || password.isBlank() || password.length() < 8){
            return null;
        }
        User savedUser = userRepository.save(new User(username, password));
        return savedUser;
    }

    public boolean doesUsernameExist(String username){
        return userRepository.existsUserByUsername(username);
    }

    public User loginUser(User user){
        String username = user.getUsername();
        String password = user.getPassword();

        User loggedInUser = userRepository.findByUsernameAndPassword(username, password);

        return loggedInUser;
    }

}
