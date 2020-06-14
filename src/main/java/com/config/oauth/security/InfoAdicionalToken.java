package com.config.oauth.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import com.config.oauth.models.entity.Usuario;
import com.config.oauth.services.IUsuarioService;

/**
 * Clase que agrega informacion adicional al Token
 * Implementa una interfaz que es un Token potenciador, que permite agregar informacion, tambien conocido
 * como los Cleims de JWT
 * @author Angel Ayuso
 *
 */
@Component
public class InfoAdicionalToken implements TokenEnhancer{

	@Autowired
	private IUsuarioService usuarioService;
	
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		Map<String, Object> info = new HashMap<>();
		Usuario usuario = usuarioService.findByUsername(authentication.getName());
		
		if(usuario != null) {
			info.put("nombre", usuario.getNombre());
			info.put("idUsuario", usuario.getIdUsuario());
		}
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
		
		return accessToken;
	}
	
}
