package com.redcode.jobsinfo.web.rest;

import com.redcode.jobsinfo.common.utils.SecurityUtils;
import com.redcode.jobsinfo.database.entity.model.Role;
import com.redcode.jobsinfo.service.UserService;
import com.redcode.jobsinfo.service.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserResource {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private UserService userService;

    @Autowired
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/auth/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDTO> getAllUser() {
        System.out.println("sads" + SecurityUtils.getCurrentUserName());
        log.debug("current user : " + SecurityUtils.getCurrentUserName());
        log.debug("get All User");
        return userService.getUser()
                .stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/auth/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO getUserbyId(@PathVariable("id") Long id) {
        log.debug("User Data {} " + userService.getUserbyId(id));
        return new UserDTO(userService.getUserbyId(id));
    }

    @PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO addNewUser(@RequestBody @Validated UserDTO userDTO) {
        return new UserDTO(userService.createNewUser(userDTO));
    }

    @DeleteMapping(value = "/auth/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
    }

    @PutMapping(value = "/auth/users/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO editUser(@RequestBody @Validated UserDTO userDTO) {
        return new UserDTO(userService.editUser(userDTO));
    }

    @PutMapping(value = "/auth/users/update/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO updateUserStatus(@RequestParam(required = false) long id,
                                    @RequestParam(required = false) boolean status) {
        return new UserDTO(userService.updateUserActiveStatus(id, status));
    }

    @GetMapping(value = "/auth/users/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO getCurrentUserDetails() {
        System.out.println("current : = " + SecurityUtils.getCurrentUserName());
        log.debug("Current User {} " + SecurityUtils.getCurrentUserName());
        return new UserDTO(userService.getUserEmail(SecurityUtils.getCurrentUserName()));
    }

    @GetMapping(value = "/users/email", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO getUserByEmail(@RequestParam String email) {

        return new UserDTO(userService.getUserEmail(email));
    }

    @GetMapping(value = "/auth/users/role", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Role> getUserRole() {
        return userService.getAllRole();
    }


}
