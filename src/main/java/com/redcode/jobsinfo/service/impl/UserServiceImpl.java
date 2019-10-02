package com.redcode.jobsinfo.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.redcode.jobsinfo.common.exceptions.BusinessRuleViolationException;
import com.redcode.jobsinfo.common.exceptions.EntityNotFoundException;
import com.redcode.jobsinfo.common.exceptions.UniqueIdTakenException;
import com.redcode.jobsinfo.common.utils.SecurityUtils;
import com.redcode.jobsinfo.database.entity.model.Country;
import com.redcode.jobsinfo.database.entity.model.Role;
import com.redcode.jobsinfo.database.entity.model.User;
import com.redcode.jobsinfo.repository.CountryRepository;
import com.redcode.jobsinfo.repository.RoleRepository;
import com.redcode.jobsinfo.repository.UserRepository;
import com.redcode.jobsinfo.service.UserService;
import com.redcode.jobsinfo.service.dto.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;

    private CountryRepository countryRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,PasswordEncoder passwordEncoder, CountryRepository countryRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;

        this.countryRepository = countryRepository;
    }

    @Override
    public List<User> getUser() {
        log.debug("current user : " + SecurityUtils.getCurrentUserName());
        User user = getUserEmail(SecurityUtils.getCurrentUserName());
        return userRepository.findAll();
    }

    @Override
    public Page<User> getUserByPage(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User getUserbyId(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No user found with id: " + id));
    }

    @Override
    public User createNewUser(UserDTO userDTO) {

        verifyEmailUniqueness(userDTO.getEmail());
        User user = new User();
        processUser(userDTO, user);
        user.setEmail(userDTO.getEmail());
        user.setActivated(true); // default actived but need to change further
        user.setLocked(false);

        updateUserPassword(user, "password", false);

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No user found with id: " + id));
        if (deletedStuatus(user) == true) {
            userRepository.delete(user);
            log.debug("Deleted user: {}", user);
        }
    }

    @Override
    public User editUser(UserDTO userDTO) {
        User user = userRepository.findOneById(userDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("No user found with id: " + userDTO.getId()));

        processUser(userDTO, user);
        user.setActivated(userDTO.isActivated());

        return userRepository.save(user);
    }

    @Override
    public void changeUserPassword(String email, String password) {

        userRepository.findOneByEmail(email).ifPresent(user -> {
            updateUserPassword(user, password, true);
            userRepository.save(user);
            log.debug("Change password for user: {}", user);
        });

    }

    @Override
    public void changePassword(Long id, String password) {
        User user = userRepository.findOneById(id)
                .orElseThrow(() -> new EntityNotFoundException("No user found with id: " + id));
        updateUserPassword(user, password, true);
    }

    @Override
    public User getUserEmail(String email) {
        log.debug("getUserEmail : " + email);
        User user = userRepository.findOneByEmail(email).orElseThrow(() -> new EntityNotFoundException("No user found with email : " + email));
        return user;
    }

    @Override
    public List<User> getUserByUserType(long usertType) {
        Role role = roleRepository.findOneById(usertType).orElseThrow(() -> new EntityNotFoundException("No Role found with id: " + usertType));
        return userRepository.findAllByUserType(role);
    }

    @Override
    public List<Role> getAllRole() {
        return roleRepository.findAll();
    }

    @Override
    public User updateUserActiveStatus(Long userId, boolean activated) {
        User user = userRepository.findOneById(userId)
                .orElseThrow(() -> new EntityNotFoundException("No user found with id: " + userId));
        user.setActivated(activated);
        log.debug("User {} " + user);
        firebaseActivation(user);
        return userRepository.save(user);
    }

    private void firebaseActivation(User user) {

        if ((user.getUserType().getId() == 11 || user.getUserType().getId() == 12) && user.isActivated()) {
            String email = user.getEmail();
            try {
                // user email verification
                UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
                String uId = userRecord.getUid();
                UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uId);
                request.setEmailVerified(true);
                FirebaseAuth.getInstance().updateUser(request);

                String link = FirebaseAuth.getInstance().generateEmailVerificationLink(email); // todo need send email to user for verification
                log.debug("Link: " + link);

            } catch (FirebaseAuthException e) {
                e.printStackTrace();
            }
            log.debug("mail sent to firebase " + email);
        }
    }

    @Override
    public List<User> getUserByInstitution(long institutionId) {
        return null;
    }

    @Override
    public List<Country> getAllActiveCountry(boolean block) {
        return countryRepository.findByBlock(block);
    }

    private void verifyEmailUniqueness(String email) {
        userRepository.findOneByEmail(email).ifPresent(user -> {
            throw new UniqueIdTakenException("This email address is already taken. Choose another. Email: " + email);
        });
    }

    private void updateUserPassword(User user, String password, boolean checkStrength) {
        if (checkStrength)
            verifyPasswordStrength(user, password);
        String encryptedPassword = passwordEncoder.encode(password);
        user.setPassword(encryptedPassword);
    }

    private void verifyPasswordStrength(User user, String password) {

        if (StringUtils.isEmpty(password)) {
            throw new BusinessRuleViolationException("Password cannot be empty!");
        }

        // 1. Web portal password must contain at least one numeric and one non-numeric characters, and must be at least 8 characters in length.
        if (password.length() < 8) {
            throw new BusinessRuleViolationException("Password length must be atleast 8.");
        }
        Pattern p = Pattern.compile("\\d");
        Matcher m = p.matcher(password);
        if (!m.find()) {
            throw new BusinessRuleViolationException("Password must contain numeric character.");
        }

        if (StringUtils.isNumeric(password)) {
            throw new BusinessRuleViolationException("Password must contain non-numeric character.");
        }

        // 2. Passwords cannot be the repeated digits or characters, e.g. 11111, %%%%%%%%
        p = Pattern.compile("(.)\\1{2,}");
        m = p.matcher(password);
        if (m.find()) {
            throw new BusinessRuleViolationException("Password contains repeated character " + m.group(1));
        }

        // 3. Passwords cannot be the sequential digits or characters (in increasing or decreasing order), e.g. 12345,
        p = Pattern.compile("(012|123|234|345|456|567|678|789|987|876|765|654|543|432|321|210)");
        m = p.matcher(password);
        if (m.find()) {
            throw new BusinessRuleViolationException("Password contains sequence of digits " + m.group(1));
        }

        // 4. Passwords cannot be the same as the previous one or reverse order of it
        if (passwordEncoder.matches(password, user.getPassword()) || passwordEncoder.matches(StringUtils.reverse(password), user.getPassword())) {
            throw new BusinessRuleViolationException("Passwords cannot be the same as the previous one or reverse order of it.");
        }

        // 5. Web portal passwords cannot contain the unbroken string of user's first or last names
        if (StringUtils.lowerCase(password).contains(StringUtils.lowerCase(user.getFirstName()))
                || StringUtils.lowerCase(password).contains(StringUtils.lowerCase(user.getLastName()))) {
            throw new BusinessRuleViolationException("Password cannot contain the user's first or last name.");
        }
    }

    private boolean deletedStuatus(User user) {

        return true;
    }

    private void processUser(UserDTO userDTO, User user) {
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setDob(userDTO.getDob());
        user.setGender(userDTO.getGender());
        user.setMobile(userDTO.getMobile());
        user.setTelephone(userDTO.getTelephone());
        user.setNid(userDTO.getNid());
        user.setPassport(userDTO.getPassport());

        if (userDTO.getUserType() != 0 && userDTO.getUserType() != null) {
            Role role = roleRepository.findOneById(userDTO.getUserType())
                    .orElseThrow(() -> new EntityNotFoundException("No Role found with id: " + userDTO.getUserType()));
            user.setUserType(role);
        }


        user.setAddress(userDTO.getAddress());
        user.setCity(userDTO.getCity());
        user.setState(userDTO.getState());
        user.setPostCode(userDTO.getPostCode());
        user.setCountry(userDTO.getCountry());


    }
}
