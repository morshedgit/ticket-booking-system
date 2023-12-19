package yar.sam.api;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.logging.Logger;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.BeanParam;
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
import yar.sam.models.ApiResponse;
import yar.sam.models.Contact;
import yar.sam.models.ErrorInfo;
import yar.sam.util.PaginationParams;
import yar.sam.util.UniTransformers;

@Path("/accounts")
public class AccountResource {

    @Inject
    AccountDao dao;

    @GET   
    public Uni<ApiResponse<List<Account>>> getAccounts(@QueryParam("email") Optional<String> email) {
        return dao.getAccounts(email)
            .map(accounts -> new ApiResponse<>(
                accounts, 
                new StringBuilder().append("Rows found: ").append(accounts.size()).toString(),
                Response.Status.OK.getStatusCode(), 
                new HashMap<>()
            ));
    }

    @POST
    public Uni<Response> addAccount(Account account) {
        return dao.addAccount(account)
            .onItem().transform(createdAccount -> Response.status(Response.Status.CREATED).entity(createdAccount).build())
            .onFailure().recoverWithItem(UniTransformers::toErrorResponse);
    }

    @PUT
    @Path("/{account_id}")
    public Uni<Response> updateAccount(@PathParam("account_id") int accountId,Account account) {
        account.setId(accountId);
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

    @GET
    @Path("/{account_id}/contacts")
    public Uni<Response> getContacts(@PathParam("account_id") int accountId, @BeanParam PaginationParams paginationParams) {
        return dao.getContacts(accountId,paginationParams)
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
    @Path("/{account_id}/contacts/{contact_id}/addresses")
    public Uni<Response> getAddresses(@PathParam("account_id") int accountId, @PathParam("contact_id") int contactId, @BeanParam PaginationParams paginationParams) {
        return dao.getAddresses(accountId, contactId, paginationParams)
        .onItem().transform(addressList -> Response.ok(addressList).build())
        .onFailure().recoverWithItem(UniTransformers::toErrorResponse);
    
    }

    @POST
    @Path("/{account_id}/contacts/{contact_id}/addresses")
    public Uni<Response> addAddress(@PathParam("account_id") int accountId, @PathParam("contact_id") int contactId, Address address) {
        return dao.addAddress(accountId, contactId, address)
            .onItem().transform(result -> Response.status(Response.Status.CREATED).entity(result).build())
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

}
