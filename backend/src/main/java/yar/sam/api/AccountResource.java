package yar.sam.api;

import java.util.List;
import java.util.Optional;

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
import yar.sam.dao.AccountDao;
import yar.sam.models.Account;
import yar.sam.models.Address;
import yar.sam.models.Contact;
import yar.sam.util.PaginationParams;

@Path("/accounts")
public class AccountResource {

    @Inject
    AccountDao dao;

    @GET
    public Uni<List<Account>> getAccounts(@QueryParam("email") Optional<String> email) {
        return dao.getAccounts(email);
    }

    @POST
    public Uni<Account> addAccount(Account account) {
        return dao.addAccount(account);
    }

    @PUT
    @Path("/{account_id}")
    public Uni<Void> updateAccount(@PathParam("account_id") int accountId, Account account) {
        account.setId(accountId);
        return dao.updateAccount(account);
    }

    @DELETE
    @Path("/{account_id}")
    public Uni<Void> deleteAccount(@PathParam("account_id") int accountId) {
        return dao.deleteAccount(accountId);
    }

    @GET
    @Path("/{account_id}/contacts")
    public Uni<List<Contact>> getContacts(@PathParam("account_id") int accountId, @BeanParam PaginationParams paginationParams) {
        return dao.getContacts(accountId, paginationParams);
    }

    @POST
    @Path("/{account_id}/contacts/")
    public Uni<Contact> addContact(@PathParam("account_id") int accountId, Contact contact) {
        return dao.addContact(accountId, contact);
    }

    @PUT
    @Path("/{account_id}/contacts/{contact_id}")
    public Uni<Void> updateContact(@PathParam("account_id") int accountId, @PathParam("contact_id") int contactId, Contact contact) {
        contact.setId(contactId);
        return dao.updateContact(contact);
    }

    @DELETE
    @Path("/{account_id}/contacts/{contact_id}")
    public Uni<Void> deleteContact(@PathParam("account_id") int accountId, @PathParam("contact_id") int contactId) {
        return dao.deleteContact(contactId);
    }

    @GET
    @Path("/{account_id}/contacts/{contact_id}/addresses")
    public Uni<List<Address>> getAddresses(@PathParam("account_id") int accountId, @PathParam("contact_id") int contactId, @BeanParam PaginationParams paginationParams) {
        return dao.getAddresses(accountId, contactId, paginationParams);
    }

    @POST
    @Path("/{account_id}/contacts/{contact_id}/addresses")
    public Uni<Address> addAddress(@PathParam("account_id") int accountId, @PathParam("contact_id") int contactId, Address address) {
        return dao.addAddress(accountId, contactId, address);
    }

    @PUT
    @Path("/{account_id}/contacts/{contact_id}/addresses")
    public Uni<Void> updateAddress(@PathParam("account_id") int accountId, @PathParam("contact_id") int contactId, Address address) {
        return dao.updateAddress(address);
    }

    @DELETE
    @Path("/{account_id}/contacts/{contact_id}/addresses/{address_id}")
    public Uni<Void> deleteAddress(@PathParam("address_id") int addressId) {
        return dao.deleteAddress(addressId);
    }
}
