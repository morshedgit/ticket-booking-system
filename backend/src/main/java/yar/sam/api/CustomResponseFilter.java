package yar.sam.api;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import yar.sam.models.ApiResponse;

@Provider
public class CustomResponseFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        Object entity = responseContext.getEntity();
        if (entity instanceof ApiResponse) {
            ApiResponse<?> apiResponse = (ApiResponse<?>) entity;

            // Set the status code and headers from ApiResponse
            responseContext.setStatus(apiResponse.getStatusCode());
            apiResponse.getHeaders().forEach((key, value) -> responseContext.getHeaders().add(key, value));

            // Replace the entity with only the data part
            responseContext.setEntity(apiResponse);
        }
    }
}


    // @Operation(summary = "Get Accounts", description = "Returns a list of accounts")
    // @APIResponse(responseCode = "200", description = "Successful retrieval of accounts",
    //              content = @Content(
    //                  mediaType = "application/json",
    //                  schema = @Schema(implementation = Account.class, type = SchemaType.ARRAY)
    //              ))
    // @APIResponse(responseCode = "404", description = "Not Found",
    //              content = @Content(mediaType = "application/json", 
    //                                 schema = @Schema(implementation = ErrorInfo.class)))
    // @APIResponse(responseCode = "500", description = "Internal Server Error",
    //              content = @Content(mediaType = "application/json", 
    //                                 schema = @Schema(implementation = ErrorInfo.class))) 