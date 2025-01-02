package br.com.jayota.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.jayota.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    
    @Autowired
    private ITaskRepository taskRepository;

    @SuppressWarnings("rawtypes")
    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var idUser = request.getAttribute("idUser"); // Criamos um atributo para o request "idUser"
        taskModel.setIdUser((UUID) idUser); // Colocamos os idUser na task

        var currentDate = LocalDateTime.now(); // Pegamos a data de atual
        if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) { // Se a data atual for menor do que data onde "começa/termina" ->
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("A data de início / data de término deve ser maior do que a data atual");
        }

        if(taskModel.getStartAt().isAfter(taskModel.getEndAt())) { // Se a data onde começa for maior que a data de término ->
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) // Retornamos um erro
            .body("A data de início deve ser menor do que a data de término"); // Mensagem do erro
        }


        var task = this.taskRepository.save(taskModel); // Salvamos a task
        return ResponseEntity.status(HttpStatus.OK).body(task); // retornamos a task
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request) {
        var idUser = request.getAttribute("idUser"); // Criamos um atributo para o request "idUser"
        var tasks = this.taskRepository.findByIdUser((UUID) idUser); // Procuramos através do id do usuário
        return tasks; // retornamos a task
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request) {

        var task = this.taskRepository.findById(id).orElse(null);

        if(task == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Tarefa não encontrada");
        }

        var idUser = request.getAttribute("idUser");

        if(!task.getIdUser().equals(idUser)) { // verifica se o usuário que está dando update na task é diferente do usuário pertencente à task
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Usuário não tem permissão para alterar essa tarefa");
        }

        Utils.copyNonNullProperties(taskModel, task);
        var taskUpdated = this.taskRepository.save(task);
        return ResponseEntity.ok().body(taskUpdated);
        

        
    }
}
