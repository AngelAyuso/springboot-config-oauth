package com.config.oauth.services;

import com.config.oauth.models.entity.Usuario;

public interface IUsuarioService {
	
	public Usuario findByUsername(String username);
}
