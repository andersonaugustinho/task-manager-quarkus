package com.example.taskmanager; // Certifique-se de que o pacote está correto e corresponde ao seu

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskResource {

    @GET
    public List<Task> getAllTasks() {
        return Task.listAll(); // Panache method
    }

    @GET
    @Path("/{id}")
    public Task getTaskById(@PathParam("id") Long id) {
        return Task.findById(id); // Panache method
    }

    @POST
    @Transactional // Ensures database operation is transactional
    public Response createTask(Task task) {
        if (task.description == null || task.description.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Description cannot be empty. Please provide a valid task. xxxxx").build();
        }
        // Garante que o ID não seja definido externamente ao criar uma nova tarefa
        if (task.id != null) {
            task.id = null;
        }
        Task.persist(task); // Saves the task to the database
        return Response.created(URI.create("/tasks/" + task.id)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional // Ensures database operation is transactional
    public Response updateTask(@PathParam("id") Long id, Task updatedTask) {
        Task task = Task.findById(id);
        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        task.description = updatedTask.description;
        task.completed = updatedTask.completed;
        // Panache automaticamente detecta mudanças e persiste dentro de uma transação
        // Task.persist(task); // Not strictly necessary here, but doesn't hurt
        return Response.ok(task).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional // Ensures database operation is transactional
    public Response deleteTask(@PathParam("id") Long id) {
        if (Task.deleteById(id)) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}