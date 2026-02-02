package com.infoworks.lab.webapp.config;

import com.infoworks.orm.Property;
import com.infoworks.utils.jwt.TokenProvider;
import com.infoworks.utils.jwt.models.JWTPayload;

import java.util.Optional;
import java.util.stream.Stream;

public final class JwtSecurityUtils {

    private JwtSecurityUtils() {}

    public static boolean matchAnyRole(Object tokenObj, Property roleKey, String...anyRoles) {
        Optional<Object> optToken = Optional.ofNullable(tokenObj);
        if (optToken.isPresent()) {
            String token = optToken.get().toString();
            JWTPayload payload = TokenProvider.parsePayload(token, JWTPayload.class);
            String userHasRoles = payload.getData().get(roleKey.getKey());
            if (userHasRoles == null || userHasRoles.isEmpty()) return false;
            final String userHasRolesUP = userHasRoles.toUpperCase();
            return Stream.of(anyRoles)
                    .anyMatch(role -> userHasRolesUP.contains(role.toUpperCase()));
        }
        return false;
    }

    public static boolean matchAnyRole(Object tokenObj, String...anyRoles) {
        return matchAnyRole(tokenObj, new Property("roles"), anyRoles);
    }

    public static boolean isAdmin(String token) {
        return matchAnyRole(token, UserRole.ADMIN.roles());
    }

    public static boolean isTenant(String token) {
        return matchAnyRole(token, UserRole.TENANT.roles());
    }

    public static boolean isUser(String token) {
        return matchAnyRole(token, UserRole.USER.roles());
    }
}
