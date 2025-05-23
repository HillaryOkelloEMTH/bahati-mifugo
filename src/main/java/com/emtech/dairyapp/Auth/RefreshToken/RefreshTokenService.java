package com.emtech.dairyapp.Auth.RefreshToken;

import com.emtech.dairyapp.Auth.Data.User.UserData;
import com.emtech.dairyapp.Auth.User.User;
import com.emtech.dairyapp.Auth.User.UserService;
import com.emtech.dairyapp.Auth.Utilities.JWTUtil;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepo refreshTokenRepo;
    private final JWTUtil jwtService;
    private final UserService userService;


    public boolean saveRefreshToken(String token, User user) {
        try {
            RefreshToken t = new RefreshToken();

            t.setToken(token);
            t.setUser(user);
            t.setExpiryDate(LocalDateTime.now().plusHours(72));

            refreshTokenRepo.save(t);
            return true;
        } catch (Exception e) {
            log.error(e.toString());
        }
        return false;
    }

    public EntityResponse<?> refreshToken(String token) {
        EntityResponse<String> res = new EntityResponse<>();

        try {
            Optional<RefreshToken> optional = refreshTokenRepo.findByToken(token);

            if (optional.isEmpty()) {
                res.setMessage("token absent");
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
                return res;
            }

            if (LocalDateTime.now().isAfter(optional.get().getExpiryDate())) {
                res.setMessage("Unauthorized. Refresh token expired");
                res.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                return res;
            }

            UserData userData = userService.getUserDetails(optional.get().getId());
            String freshToken = jwtService.generateToken(userData);

            res.setMessage("Access token refreshed");
            res.setStatusCode(HttpStatus.OK.value());
            res.setEntity(freshToken);
        } catch (Exception e) {
            log.error(e.toString());

            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            res.setMessage("failed to refresh token");
        }
        return res;
    }
}
