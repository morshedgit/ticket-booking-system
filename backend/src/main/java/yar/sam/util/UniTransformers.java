package yar.sam.util;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Response;
import java.util.function.Function;
import java.util.NoSuchElementException;

public class UniTransformers {
    public static Response toErrorResponse(Throwable throwable) {
        if (throwable instanceof NoSuchElementException) {
            // Specific handling for NoSuchElementException
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Resource not found: " + throwable.getMessage())
                           .build();
        } else {
            // General error handling
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error: " + throwable.getMessage())
                           .build();
        }
    }
    
    
}
