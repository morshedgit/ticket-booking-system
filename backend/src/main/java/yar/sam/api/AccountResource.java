package yar.sam.api;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

import org.jboss.logging.Logger;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import yar.sam.dao.AccountDao;
import yar.sam.models.Account;
import yar.sam.models.Address;
import yar.sam.models.Contact;
import yar.sam.util.UniTransformers;

@Path("/accounts")
public class AccountResource {

    private static final Logger LOGGER = Logger.getLogger(AccountResource.class);

    @Inject
    AccountDao dao;

    @GET
    @Path("/{account_id}/contacts/{contact_id}/addresses")
    public Uni<Response> getAddresses(@PathParam("account_id") int accountId, @PathParam("contact_id") int contactId) {
        return dao.getAddresses(accountId, contactId)
        .onItem().transform(addressList -> Response.ok(addressList).build())
        .onFailure().recoverWithItem(UniTransformers::toErrorResponse);
    
    }

    @POST
    @Path("/{account_id}/contacts/{contact_id}/addresses")
    public Uni<Response> addAddress(@PathParam("account_id") int accountId, @PathParam("contact_id") int contactId, Address address) {
        return dao.addAddress(accountId, contactId, address)
            .onItem().transform(ignored -> Response.status(Response.Status.CREATED).build())
            .onFailure().recoverWithItem(UniTransformers::toErrorResponse);
    }

    @PUT
    @Path("/{account_id}/contacts/{contact_id}/addresses")
    public Uni<Response> updateAddress(@PathParam("account_id") int accountId, @PathParam("contact_id") int contactId, Address address) {
        return dao.updateAddress(address)
            .onItem().transform(ignored -> Response.status(Response.Status.NO_CONTENT).build())
            .onFailure().recoverWithItem(UniTransformers::toErrorResponse);
    }

    @DELETE
    @Path("/{account_id}/contacts/{contact_id}/addresses/{address_id}")
    public Uni<Response> deleteAddress(@PathParam("address_id") int addressId) {
        return dao.deleteAddress(addressId)
            .onItem().transform(ignored -> Response.status(Response.Status.NO_CONTENT).build())
            .onFailure().recoverWithItem(UniTransformers::toErrorResponse);
    }

    @GET
    @Path("/{account_id}/contacts")
    public Uni<Response> getContacts(@PathParam("account_id") int accountId) {
        return dao.getContacts(accountId)
            .onItem().transform(contacts -> Response.ok(contacts).build())
            .onFailure().recoverWithItem(UniTransformers::toErrorResponse);
    }

    @POST
    @Path("/{account_id}/contacts/")
    public Uni<Response> addContact(@PathParam("account_id") int accountId, Contact contact) {
        return dao.addContact(accountId, contact)
            .onItem().transform(result -> Response.status(Response.Status.CREATED).entity(result).build())
            .onFailure().recoverWithItem(UniTransformers::toErrorResponse);
    }

    @PUT
    @Path("/{account_id}/contacts/{contact_id}")
    public Uni<Response> updateContact(@PathParam("account_id") int accountId, @PathParam("contact_id") int contactId, Contact contact) {
        contact.setId(contactId);
        return dao.updateContact(contact)
            .onItem().transform(ignored -> Response.status(Response.Status.NO_CONTENT).build())
            .onFailure().recoverWithItem(UniTransformers::toErrorResponse);
    }

    @DELETE
    @Path("/{account_id}/contacts/{contact_id}")
    public Uni<Response> deleteContact(@PathParam("account_id") int accountId, @PathParam("contact_id") int contactId) {
        return dao.deleteContact(contactId)
            .onItem().transform(ignored -> Response.status(Response.Status.NO_CONTENT).build())
            .onFailure().recoverWithItem(UniTransformers::toErrorResponse);
    }

    @GET
    public Uni<Response> getAccounts(@QueryParam("email") Optional<String> email) {
        return dao.getAccounts(email)
            .onItem().transform(accounts -> Response.ok(accounts).build())
            .onFailure().recoverWithItem(UniTransformers::toErrorResponse);
    }

    @POST
    public Uni<Response> addAccount(Account account) {
        return dao.addAccount(account)
            .onItem().transform(createdAccount -> Response.status(Response.Status.CREATED).entity(createdAccount).build())
            .onFailure().recoverWithItem(UniTransformers::toErrorResponse);
    }

    @PUT
    public Uni<Response> updateAccount(Account account) {
        return dao.updateAccount(account)
            .onItem().transform(ignored -> Response.status(Response.Status.NO_CONTENT).build())
            .onFailure().recoverWithItem(UniTransformers::toErrorResponse);
    }

    @DELETE
    @Path("/{account_id}")
    public Uni<Response> deleteAccount(@PathParam("account_id") int accountId) {
        return dao.deleteAccount(accountId)
            .onItem().transform(ignored -> Response.status(Response.Status.NO_CONTENT).build())
            .onFailure().recoverWithItem(UniTransformers::toErrorResponse);
    }
}
