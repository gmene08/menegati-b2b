package br.com.menegati.brb_revendedoras.security;

import br.com.menegati.brb_revendedoras.repository.UserRepository;
import br.com.menegati.brb_revendedoras.services.AuthService;
import br.com.menegati.brb_revendedoras.services.JwtService;
import com.auth0.jwt.JWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = getToken(request);

        if(token != null){
            var cpf = jwtService.validateToken(token);

            if(!cpf.isEmpty()) {
                var user = userRepository.findByCpf(cpf).orElse(null);

                if(user != null && user.isEnabled()) {

                    if(jwtService.shouldRenew(token)){
                        renewToken(token, response);
                    }

                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private void renewToken(String token, HttpServletResponse response) {
        String renewedToken = jwtService.renewToken(token);
        ResponseCookie newCookie = AuthService.buildCookie(renewedToken);

        response.addHeader(HttpHeaders.SET_COOKIE, newCookie.toString());
    }

    private String getToken(HttpServletRequest request) {
        var cookies = request.getCookies();
        if(cookies != null) {
            for (var cookie : cookies) {
                if(cookie.getName().equals("token")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
