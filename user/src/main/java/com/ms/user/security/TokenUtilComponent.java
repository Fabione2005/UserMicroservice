package com.ms.user.security;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import com.ms.user.logger.CommonLogger;
import com.ms.user.security.dto.JwtUtilDto;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class TokenUtilComponent extends CommonLogger{

	//Una hora de vigencia para el token
	private static final long MILLISECONDS_JWT = 3600000;
	
	@Value("${auth.jwt.id}")
	private String id;
	
	@Value("${auth.jwt.secretKey}")
	private String secretKey;
	
	@Autowired
	private HttpServletRequest request;
	
	public String getJWTToken(String email) {

		logger.trace("Generando token para el usuario " + email);

		//String secretKey = "mySecretKey";
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");

		String token = Jwts.builder().setId(id).setSubject(email)
				.claim("authorities",
						grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + MILLISECONDS_JWT))
				.signWith(SignatureAlgorithm.HS512, secretKey.getBytes()).compact();

		logger.trace("Token creado");
		return token;
	}
	
	public JwtUtilDto getJwtUtilDto(){
		
		String token = this.getHeaderValue("Authorization").replace("Bearer ","");
		
		String sobject = Jwts.parser()
				  .setSigningKey(secretKey.getBytes())
				  .parseClaimsJws(token)
				  .getBody()
				  .getSubject();
		
		return new JwtUtilDto(token,sobject);
		
	}
	
	private String getHeaderValue(String headerName) {
		return request.getHeader(headerName);
	}
	
}
