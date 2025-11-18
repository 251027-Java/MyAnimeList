package repository;

import model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Integer insertUser(User user);
    boolean existsUserByUsername(String usernmae);

    User findByUsernameAndPassword(String username, String password);

    boolean existsUserByUserId(Integer id);

}
