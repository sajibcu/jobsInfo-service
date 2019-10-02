package com.redcode.jobsinfo.service;

import com.redcode.jobsinfo.database.entity.model.Country;
import com.redcode.jobsinfo.database.entity.model.Role;
import com.redcode.jobsinfo.database.entity.model.User;
import com.redcode.jobsinfo.service.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    //get all user info
    List<User> getUser();

    Page<User> getUserByPage(Pageable pageable);

    User getUserbyId(Long id);

    User createNewUser(UserDTO u);

    void deleteUser(Long id);

    User editUser(UserDTO userDTO);

    void changeUserPassword(String email, String password);

    void changePassword(Long id, String password);

    User getUserEmail(String email);

    List<User> getUserByUserType(long usertType);

    List<Role> getAllRole();

    User updateUserActiveStatus(Long userId, boolean activated);

    List<User> getUserByInstitution(long institutionId);

    List<Country> getAllActiveCountry(boolean block);

}
