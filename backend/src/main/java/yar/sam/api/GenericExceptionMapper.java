package yar.sam.api;

import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import yar.sam.models.ErrorInfo;
import jakarta.ws.rs.core.Response;
import java.util.NoSuchElementException;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable throwable) {
        if (throwable instanceof NoSuchElementException) {
            ErrorInfo errorInfo = new ErrorInfo(
                "Resource not found", 
                Response.Status.NOT_FOUND.toString(), 
                throwable.getMessage()
            );
            // Specific handling for NoSuchElementException
            return Response.status(Response.Status.NOT_FOUND)
                           .entity(errorInfo)
                           .build();
        } else {

            ErrorInfo errorInfo = new ErrorInfo(
                "General Error", 
                Response.Status.INTERNAL_SERVER_ERROR.toString(), 
                throwable.getMessage()
            );
            // General error handling
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(errorInfo)
                           .build();
        }
    }
}
