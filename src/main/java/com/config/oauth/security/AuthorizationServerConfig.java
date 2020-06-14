package com.config.oauth.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * Clase de configuracion para crear el token JWT
 * @author Angel Ayuso
 *
 */
@RefreshScope
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter{
	
	@Autowired
	private Environment env;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private InfoAdicionalToken infoAdicionalToken;

	/**
	 * Los permisos que van a tener los endpoints del servicio de autentificacion de oatuh2 para crear y validar el Token
	 * Estos dos endpoints estan protegidos por autentificacion via Http Basic, utilizando los credenciales del cliente, de la aplicacion
	 * enviando el idAplicacion y el secret
	 */
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("permitAll()")	//Acceso publico para crear y obtener el Token
				.checkTokenAccess("isAuthenticated()"); //Metodo para validar el Token - requiere Autentificacion
	}

	/**
	 * Registra los clientes
	 * No solo se autentifica con el usuario en BBDD sino tambien con los credenciales de la aplicacion Cliente
	 * por tanto doble autetificacion, de usuario y aplicacion
	 * Se podrian crear varios clientes para diferentes aplicaciones que llamaran al Backend
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory().withClient(env.getProperty("config.security.oauth.client.id")) //Se registra Cliente en memoria
				.secret(passwordEncoder.encode(env.getProperty("config.security.oauth.client.secret")))	//Password encriptada
				.scopes("read","write")				//Alcance de la aplicacion cliente
				.authorizedGrantTypes("password","refresh_token")	//Tipo de concesion de la aplicacion, como hacer el tipo de Autentificacion
													// como obtener el Token
													// password - Cuando es con credenciales, cuando tenemos usuarios en BBDD
				.accessTokenValiditySeconds(3600)  //1 hora de token
				.refreshTokenValiditySeconds(3600);
	}

	/**
	 * Configuracion del Endpoint
	 * Crea el TokenStorage de JWT
	 * Crea el Converter que se encarga de meter la informacion del usuario en el Token
	 * Con TokenEnhancerChain añadimos la informacion adicional del Token que se ha agreado en UsuarioService
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(infoAdicionalToken, accessTokenConverter()));
		
		endpoints.authenticationManager(authenticationManager)
			.tokenStore(tokenStore())
			.accessTokenConverter(accessTokenConverter())
			.tokenEnhancer(tokenEnhancerChain);
	}

	/**
	 * Componente que se encarga de generar y guardar los datos del Token con el JwtTokenStore
	 * @return
	 */
	@Bean
	public JwtTokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}
	
	/**
	 * Codigo secreto para la firma del token
	 * @return
	 */
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter tokenCorverter = new JwtAccessTokenConverter();
		tokenCorverter.setSigningKey(env.getProperty("config.security.oauth.jwt.key"));
		return tokenCorverter;
	}
}