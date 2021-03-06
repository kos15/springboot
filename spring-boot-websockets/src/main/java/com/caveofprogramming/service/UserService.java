package com.caveofprogramming.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.caveofprogramming.model.entity.SiteUser;
import com.caveofprogramming.model.entity.TokenType;
import com.caveofprogramming.model.entity.VerificationToken;
import com.caveofprogramming.model.repository.UserDao;
import com.caveofprogramming.model.repository.VerificationDao;

@Service
public class UserService implements UserDetailsService {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private VerificationDao verificationDao;
	
	public void register(SiteUser user) {
		user.setRole("ROLE_USER");
		userDao.save(user);
	}

	public void save(SiteUser user) {
		userDao.save(user);
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		SiteUser user = userDao.findByEmail(email);
		
		if(user == null) {
			return null;
		}
		
		List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRole());
		
		String password = user.getPassword();
		
		Boolean enabled = user.getEnabled();
		
		return new User(email, password, enabled, true, true, true, auth);
	}
	
	public String createEmailVerificationToken(SiteUser user) {
		VerificationToken token = new VerificationToken(UUID.randomUUID().toString(), user, TokenType.REGISTRATION);
		verificationDao.save(token);
		return token.getToken();
	}
	
	public VerificationToken getVerificationToken(String token) {
		return verificationDao.findByToken(token);
	}

	public void deleteToken(VerificationToken token) {
		verificationDao.delete(token);
	}

	public SiteUser get(String email) {
		return userDao.findByEmail(email);
	}

	public Optional<SiteUser> get(Long id) {
		return userDao.findById(id);
	}

	public String getUserName(Long chatWithUserID) {
		Optional<SiteUser> userOptional = userDao.findById(chatWithUserID);
		SiteUser user = userOptional.get();
		
		return user.getFirstname() + " " + user.getSurname();
	}
}
