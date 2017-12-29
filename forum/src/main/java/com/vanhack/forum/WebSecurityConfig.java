package com.vanhack.forum;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	DataSource dataSource;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource)
			.usersByUsernameQuery(getUsersByUsernameQuery())
			.passwordEncoder(passwordEncoder)
			.authoritiesByUsernameQuery(getAuthoritiesByUserNameQuery());
	}

	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
        		.antMatchers("/static/**", "/css/**", "/js/**").permitAll()
                .antMatchers("/", "/dashboard","/findTopicByFilter", "/detailTopic","/signup","/createUser").permitAll().anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
                .permitAll()
                .logoutSuccessUrl("/findTopicByFilter");
        
    }

	private String getUsersByUsernameQuery() {
		return "select login username,password, enabled from user where login=?";
	}


	private String getAuthoritiesByUserNameQuery() {
		StringBuilder authotiriesByUserNameQuery = new StringBuilder();
		authotiriesByUserNameQuery.append("select user.login username, role.name_role as role "); 
		authotiriesByUserNameQuery.append("from user, user_role, role ");
		authotiriesByUserNameQuery.append("where  ");
		authotiriesByUserNameQuery.append(" user.id_user = user_role.id_user and ");
		authotiriesByUserNameQuery.append(" user_role.id_role = role.id_role and ");
		authotiriesByUserNameQuery.append(" user.login=? ");
		return authotiriesByUserNameQuery.toString();
	}

}
