package link.timon.tutorial.securerest.notes.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import link.timon.tutorial.securerest.notes.common.InternalServerException;
import link.timon.tutorial.securerest.notes.domain.User;
import org.springframework.stereotype.Component;

/**
 * Utility for creating and validating JSON Web-Tokens.
 *
 * @author Timon
 */
@Component
public class JwtUtil {

    // NOTE: Never ever do this in production!!!!
    private static final String SECRET = "ultra secure secret.";
    private static final String ISSUER = "timon.link";
    private static final String SUBJECT_SEPERATOR = ",";

    public String generateFor(User user) {
        return "Bearer " + Jwts.builder()
                .setSubject(String.join("%s%s%s", user.getId(), SUBJECT_SEPERATOR, user.getUsername()))
                .setIssuer(ISSUER)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public String getUserIdFromToken(String token) {
        Claims body = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        return body.getSubject().split(SUBJECT_SEPERATOR)[0];
    }

    public String getUsernameFromToken(String token) {
        Claims body = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        return body.getSubject().split(SUBJECT_SEPERATOR)[1];
    }

    public Date getExpirationFromToken(String token) {
        Claims body = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        return body.getExpiration();
    }

    public boolean isValid(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new InternalServerException("Invalid JWT", e);
        }
    }

}
