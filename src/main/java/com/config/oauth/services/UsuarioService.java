package com.config.oauth.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.config.oauth.clients.UsuarioFeignClient;
import com.config.oauth.models.ResultadoBean;
import com.config.oauth.models.entity.Usuario;
/**
 * Implementa IUsuarioService para obtener el objeto Usuario y que pueda ser utilizado por otras clases
 * Implementa UserDetailsService para la autentificacion
 * @author Angel Ayuso
 *
 */
@Service
public class UsuarioService implements IUsuarioService, UserDetailsService{
	
	@Autowired
	private UsuarioFeignClient client;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		ResultadoBean resultadoBean = client.getUsuarioByDni(username);
		
		if(resultadoBean.getUsuario() == null) {
			resultadoBean = client.getUsuarioByEmail(username);
		}
		
		if(resultadoBean.getUsuario() == null) {
			throw new UsernameNotFoundException("No existe el Usuario");
		}
		
		List<GrantedAuthority> authorities = resultadoBean.getUsuario().getRoles()
												.stream()
												.map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
												.collect(Collectors.toList());
		
		Boolean enabled = Boolean.valueOf(false);
		if("S".equals(resultadoBean.getUsuario().getEnabled())){
			enabled = Boolean.valueOf(true);
		}
		
		return new User(resultadoBean.getUsuario().getNombre(), resultadoBean.getUsuario().getPassword(),
				enabled, true, true, true, authorities);
	}

	@Override
	public Usuario findByUsername(String username) {
		return client.getUsuarioByDni(username).getUsuario();
	}

}
