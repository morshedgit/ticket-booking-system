package yar.sam.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import yar.sam.dao.PricingDao;
import yar.sam.models.Pricing;
import io.smallrye.mutiny.Uni;

@Path("/pricings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PricingResource {

    @Inject
    PricingDao pricingDao;

    @POST
    public Uni<Pricing> createPricing(Pricing pricing) {
        return pricingDao.addPricing(pricing);
    }

    @PUT
    @Path("/{id}")
    public Uni<Void> updatePricing(@PathParam("id") int id, Pricing pricing) {
        pricing.setId(id);
        return pricingDao.updatePricing(pricing);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> deletePricing(@PathParam("id") int id) {
        return pricingDao.deletePricing(id);
    }
}
