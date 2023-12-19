package yar.sam.api;

import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;
import java.util.NoSuchElementException;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable throwable) {
        if (throwable instanceof NoSuchElementException) {
            // Specific handling for NoSuchElementException
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Resource not found: "+ throwable.getMessage())
                           .build();
        } else if (throwable.getMessage().contains("Pricing record already exists")) {
            // Replace with the appropriate check for your application
            return Response.status(Response.Status.CONFLICT).entity("Pricing record already exists").build();
        } else if (throwable.getMessage().contains("Seat cannot be reserved")) {
            // Replace with the appropriate check for your application
            return Response.status(Response.Status.CONFLICT).entity("Seat cannot be reserved").build();
        } else if (throwable.getMessage().contains("Seat cannot be booked")) {
            // Replace with the appropriate check for your application
            return Response.status(Response.Status.CONFLICT).entity("Seat cannot be booked").build();
        } else {
            // General error handling
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Server Error: "+ throwable.getMessage())
                           .build();
        }
    }
}
