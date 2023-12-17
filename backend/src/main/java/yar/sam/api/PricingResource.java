package yar.sam.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import yar.sam.dao.PricingDao;
import yar.sam.models.Pricing;
import io.smallrye.mutiny.Uni;

@Path("/pricings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PricingResource {

    @Inject
    PricingDao pricingDao;

    // @GET
    // public Uni<Response> getAllPricings() {
    //     return pricingDao.getAllPricings()
    //             .onItem().transform(pricings -> Response.ok(pricings).build());
    // }

    @POST
    public Uni<Response> createPricing(Pricing pricing) {
        return pricingDao.addPricing(pricing)
                .onItem().transform(createdPricing -> Response.status(Response.Status.CREATED).entity(createdPricing).build())
                .onFailure().recoverWithItem(th -> {
                    if (th instanceof RuntimeException) {
                        if (th.getMessage().equals("No rows affected")) {
                            return Response.status(Response.Status.NO_CONTENT).entity("No rows affected").build();
                        } else if (th.getMessage().contains("Pricing record already exists")) {
                            // Replace with the appropriate check for your application
                            return Response.status(Response.Status.CONFLICT).entity("Pricing record already exists").build();
                        }
                    }
                    // Handle other exceptions
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(th.getMessage()).build();
                });
    }

    @PUT
    @Path("/{id}")
    public Uni<Response> updatePricing(@PathParam("id") int id, Pricing pricing) {
        pricing.setId(id);
        return pricingDao.updatePricing(pricing)
                .onItem().transform(updated -> Response.ok().build());
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> deletePricing(@PathParam("id") int id) {
        return pricingDao.deletePricing(id)
                .onItem().transform(deleted -> Response.noContent().build());
    }
}
