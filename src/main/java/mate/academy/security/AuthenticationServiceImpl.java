package mate.academy.security;

import java.util.Optional;
import mate.academy.exception.AuthenticationException;
import mate.academy.exception.RegistrationException;
import mate.academy.lib.Inject;
import mate.academy.lib.Service;
import mate.academy.model.User;
import mate.academy.service.UserService;
import mate.academy.util.HashUtil;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Inject
    private UserService userService;

    @Override
    public User login(String email, String password) throws AuthenticationException {
        Optional<User> userOptional = userService.findByEmail(email);
        User user = userOptional.orElseThrow(() -> new AuthenticationException(
                "Can't authenticate user. Email or password are incorrect."));
        String hashPassword = HashUtil.hashPassword(password, user.getSalt());
        if (hashPassword.equals(user.getPassword())) {
            return user;
        }
        throw new AuthenticationException(
                "Can't authenticate user. Email or password are incorrect.");
    }

    @Override
    public User register(String email, String password) throws RegistrationException {
        if (userService.findByEmail(email).isPresent()) {
            throw new RegistrationException("Email: " + email + " is busy.");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        return userService.add(user);
    }
}
