package com.example.KodikaraGroupBusinessManagementApplication.Repo;

import com.example.KodikaraGroupBusinessManagementApplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD

=======
>>>>>>> da8f20982d36e6c63bd3494a5827a14a3ac7e545

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
<<<<<<< HEAD
}
=======
}
>>>>>>> da8f20982d36e6c63bd3494a5827a14a3ac7e545
