package com.config.oauth.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.config.oauth.models.ResultadoBean;



@FeignClient(name = "gestion-usuarios")
public interface UsuarioFeignClient {
	
	@GetMapping("/getUsuarioById/{id}")
	public ResultadoBean getUsuarioById(@PathVariable("id") Integer id);
	
	@GetMapping("/getUsuarioByDni/{dni}")
	public ResultadoBean getUsuarioByDni(@PathVariable("dni") String dni);
	
	@GetMapping("/getUsuarioByEmail/{email}")
	public ResultadoBean getUsuarioByEmail(@PathVariable("email") String email);
}
