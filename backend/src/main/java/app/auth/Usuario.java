package app.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import app.Entity.Venda;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuario")
public class Usuario implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotBlank(message = "O nome é obrigatório")
	private String nome;

	@NotBlank(message = "Login é obrigatório")
	@Size(min = 3, max = 20, message = "Login deve ter entre 3 e 20 caracteres")
	private String login;

	@NotBlank(message = "A senha é obrigatória")
	@Size(min = 8, message = "A senha deve ter no minimo 8 caracteres")
	private String senha;

	@NotNull(message = "O tipo de usuario é obrigatorio")
	@Enumerated(EnumType.STRING)
	private Role role;

	private boolean ativo;

	// setando Role como GESTOR ou FUNCIONARIO
	public enum Role {
		GESTOR, FUNCIONARIO;
	}
	
	
    public Usuario(long id, String nome, String login, String senha, Role role, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.role = role;
        this.ativo = ativo;
    }


	@OneToMany(mappedBy = "usuario")
	@JsonIgnoreProperties("usuario")
	private List<Venda> vendas;


	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(this.role.toString()));
		return authorities;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return this.senha;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return this.login;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return this.ativo;
	}


}
