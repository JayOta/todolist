package br.com.jayota.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.jayota.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

                var servletPath = request.getServletPath();

                if (servletPath.startsWith("/tasks/")) { // Validação para verificar se o que estamos chamando é a rota de tasks

                    // Pegar a autenticação (usuario e senha)
                    var authorization = request.getHeader("Authorization");
                    
                    var authEncoded = authorization.substring("Basic".length()).trim(); // Excluímos o "Basic" e o " " da autorização

                    byte[] authDecode = Base64.getDecoder().decode(authEncoded); // Decodificamos a autorização

                    var authString = new String(authDecode); // Armazenamos como uma string

                    String[] credentials = authString.split(":"); // Separamos a auto.. pelos ":"(dois pontos)
                    String username = credentials[0]; // Armazenamos o username
                    String password = credentials[1]; // Armazenamos a password


                    // Validar usuario
                    var user = this.userRepository.findByUsername(username);
                    if (user == null) { // Se não existir usuário ->
                        response.sendError(401); // Usuário recebe um erro pois não possui autorização
                    } else { // Se o usuário existir ->

                        // Validar senha
                        var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword()); // Transformamos a senha do usuário em um array de Char
                        if (passwordVerify.verified) { // Se a senha verificada for igual a true
                            // Segue viagem
                            request.setAttribute("idUser", user.getId());
                            filterChain.doFilter(request, response);
                        } else {
                            response.sendError(401);
                        }

                        
                            
                    }
                }
                else { // Se a rota chamada não for de tasks
                    filterChain.doFilter(request, response);
                }
            }

    
    
}
