package com.infoworks.lab.webapp.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * Utility class for Spring Security.
 * JHipster implementation
 */
public final class SecurityUtils {

    public static final String MATCH_ANY_ADMIN_ROLE = "ADMIN";

    private SecurityUtils() {}

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user
     */
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof UserDetails) {
                        UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                        return springSecurityUser.getUsername();
                    } else if (authentication.getPrincipal() instanceof String) {
                        return (String) authentication.getPrincipal();
                    }
                    return null;
                });
    }

    /**
     * Get the JWT of the current user.
     *
     * @return the JWT of the current user
     */
    public static Optional<String> getCurrentUserJwt() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .filter(authentication -> authentication.getCredentials() instanceof String)
                .map(authentication -> (String) authentication.getCredentials());
    }

    public static String getGuestId() {
        return SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new RuntimeException("Guest Id not found!"));
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> authentication.getAuthorities().stream()
                        .noneMatch(grantedAuthority -> grantedAuthority.getAuthority()
                                .equals(AuthoritiesConstants.GUEST)))
                .orElse(false);
    }

    /**
     * If the current user has a specific authority (security role).
     * <p>
     * The name of this method comes from the isUserInRole() method in the Servlet API
     *
     * @param anyRoles the authority to check
     * @return true if the current user has the authority, false otherwise
     */
    public static boolean isCurrentUserInRole(String...anyRoles) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Set<String> authoritiesSet = new HashSet<>(Arrays.asList(anyRoles));
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(authoritiesSet::contains))
                .orElse(false);
    }

    public static boolean matchAnyRole(String...anyRoles) {
        return isCurrentUserInRole(anyRoles);
    }

    public static boolean matchAnyAdminRole() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Set<String> authoritySet =
                Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> AuthorityUtils.authorityListToSet(authentication.getAuthorities()))
                .orElse(new HashSet<>());
        String[] args = authoritySet.toArray(new String[0]);
        return matchAnyAdminRole(args);
    }

    public static boolean matchAnyAdminRole(Collection<? extends GrantedAuthority> authorities) {
        Set<String> authoritySet = Optional.ofNullable(authorities)
                .map(AuthorityUtils::authorityListToSet)
                .orElse(new HashSet<>());
        String[] args = authoritySet.toArray(new String[0]);
        return matchAnyAdminRole(args);
    }

    private static boolean matchAnyAdminRole(String...args) {
        return String.join(" ", args).toUpperCase().contains(MATCH_ANY_ADMIN_ROLE);
    }

}
