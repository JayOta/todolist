package br.com.jayota.todolist.user;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;


/*
 * Modificadores:
 * public
 * private
 * protected
 */
@RestController
@RequestMapping("/users") // (localhost:8080/users)
public class UserController {
    
    @Autowired
    private IUserRepository userRepository;

    /* Body */
    @PostMapping("/") // (localhost:8080/users/)
    public ResponseEntity create(@RequestBody UserModel userModel) {
        var user = this.userRepository.findByUsername(userModel.getUsername());

        if(user != null) { // se o usuário já existir ->
            System.out.println("Usuário já existe!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe!"); // Retornamos o status code com uma mensagem
        } // se o usuário não existir ->

        var passwordHasrhed = BCrypt.withDefaults() 
        .hashToString(12, userModel.getPassword().toCharArray()); // Cria um hash da senha (criptografando-a)

        userModel.setPassword(passwordHasrhed); // Cria a senha do usuário com a senha criptografada

        var UserCreated = this.userRepository.save(userModel); // Salvamos os dados do usuário criado
        return ResponseEntity.status(HttpStatus.OK).body(UserCreated); // Retornamos o usuário criado, utilizando o status code com uma mensagem
    }
}

// ResponseEntity = Utilizado para passar algo que obteve sucesso ou erro