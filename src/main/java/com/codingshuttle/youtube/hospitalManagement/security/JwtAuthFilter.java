package com.codingshuttle.youtube.hospitalManagement.security;

import com.codingshuttle.youtube.hospitalManagement.entity.User;
import com.codingshuttle.youtube.hospitalManagement.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.ArrayList;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final AuthUtil authUtil;

    // what happens that GlobalExceptional handler class handles exceptions at controller level. but if errors occurs at filter layer(before api even reach controller) then
    // we make use of this HandlerExceptionResolver which will take/throw the exception at Filter layer to GlobalExceptionHandler class
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            log.info("incoming request: {}", request.getRequestURI());

            final String requestTokenHeader = request.getHeader("Authorization");
            if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer")) {
                //This method passes the request to the next filter in the chain.
                filterChain.doFilter(request, response);
                return;
            }

            String token = requestTokenHeader.split("Bearer ")[1];
            String username = authUtil.getUsernameFromToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findByUsername(username).orElseThrow();
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                        = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }

    }
}
/*
* Key Takeaways

Order matters: Filters execute in a specific sequence
JwtAuthFilter runs early: Sets authentication before authorization checks
AuthorizationFilter runs last: Enforces your permitAll()/authenticated() rules
Stateless = No sessions: Each request is independent
CSRF disabled: Not needed for JWT-based auth
Public endpoints skip auth: /public/** and /auth/** are accessible without tokens
Everything else needs JWT: All other endpoints require valid JWT in Authorization header
* */

/*
🔥 AuthenticationToken has ONLY ONE purpose:
👉 To tell Spring Security who the current user is during the processing of ONE request.

That’s it.
It does NOT survive between requests.
It does NOT go to the client.
It does NOT store login session.

It only lives in the SecurityContextHolder for that specific request.
* 2. If I close my website after logging in… what happens to AuthenticationToken?
✔ YES — The AuthenticationToken in SecurityContextHolder becomes EMPTY.
✔ It is not stored permanently.
✔ It lives only for the duration of one HTTP request, then Spring erases it.

Because:
- Spring Security stores Authentication in ThreadLocal
- ThreadLocal = memory tied to a single request thread
- After response → SecurityContextHolder is cleared

So closing browser = SecurityContextHolder empty.

**** BOOM ****
YES — 100% correct.
If you make 10 authenticated requests, then:

✅ SecurityContextHolder is filled 10 times
✅ AuthenticationToken is created 10 times
✅ SecurityContextHolder is cleared 10 times

Because each HTTP request is independent.

🔥 Detailed but very short explanation
Request 1

JWT filter reads your token

Creates AuthenticationToken

Puts it → SecurityContextHolder

Controller runs

Response returns

SecurityContextHolder is cleared

Request 2

New HTTP request → new thread

JWT filter again creates new AuthenticationToken

Stored in SecurityContextHolder

After response → cleared

✔ Same for all 10 requests → 10 independent cycles.
✔ Why does Spring do this?

Because:

HTTP is stateless

No request can “remember the previous one”

SecurityContextHolder is ThreadLocal, tied to ONE request thread

After controller finishes, Spring MUST empty it to avoid leakage to another user

🧠 Simple analogy

Think of each request as a visitor at a checkpoint:

Visitor shows JWT pass

Guard verifies it

Guard creates internal ID badge (AuthenticationToken)

Guard uses it while the visitor is inside

Visitor leaves → badge is destroyed

When visitor returns → repeat

Happens 10 times for 10 visits
* */