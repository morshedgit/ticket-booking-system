package yar.sam.api;

import java.util.List;
import java.util.Optional;

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
import yar.sam.dao.AccountDao;
import yar.sam.models.Account;
import yar.sam.models.Address;
import yar.sam.models.Contact;

@Path("/accounts")
public class AccountResource {

    private static final Logger LOGGER = Logger.getLogger(SeatResource.class);

    @Inject
    AccountDao dao;

    @GET
    @Path("/{account_id}/contacts/{contact_id}/addresses")
    public Uni<List<Address>> getAddresses(@PathParam("account_id") int accountId,@PathParam("contact_id") int contactId) {       
        return dao.getAddresses(accountId,contactId);
    }

    @POST
    @Path("/{account_id}/contacts/{contact_id}/addresses")
    public Uni<Void> addAddress(@PathParam("account_id") int accountId,@PathParam("contact_id") int contactId, Address address) {       
        return dao.addAddress(accountId, contactId, address);
    }

    @PUT
    @Path("/{account_id}/contacts/{contact_id}/addresses")
    public Uni<Void> updateAddress(@PathParam("account_id") int accountId,@PathParam("contact_id") int contactId, Address address) {       
        return dao.updateAddress(address);
    }

    @DELETE
    @Path("/{account_id}/contacts/{contact_id}/addresses/{address_id}")
    public Uni<Void> deleteAddress(@PathParam("address_id") int addressId) {       
        return dao.deleteAddress(addressId);
    }

    @POST
    @Path("/{account_id}/contacts/")
    public Uni<Void> addContact(@PathParam("account_id") int accountId, Contact contact) {       
        return dao.addContact(accountId, contact);
    }

    @PUT
    @Path("/{account_id}/contacts/")
    public Uni<Void> updateContact(@PathParam("account_id") int accountId, Contact contact) {       
        return dao.updateContact(contact);
    }

    @DELETE
    @Path("/{account_id}/contacts/{contact_id}")
    public Uni<Void> deleteContact(@PathParam("contact_id") int contact_id) {       
        return dao.deleteContact(contact_id);
    }

    @GET
    @Path("/{account_id}/contacts")
    public Uni<List<Contact>> getContacts(@PathParam("account_id") int accountId) {       
        return dao.getContacts(accountId);
    }

    @GET
    public Uni<List<Account>> getAccounts(@QueryParam("email") String email) {       
        return dao.getAccounts(Optional.ofNullable(email));
    }

    @POST
    public Uni<Void> addAccount(Account account) {       
        return dao.addAccount(account);
    }

    @PUT
    public Uni<Void> updateAccount(Account account) {       
        return dao.updateAccount(account);
    }

    @DELETE
    @Path("/{account_id}")
    public Uni<Void> deleteAccount(@PathParam("account_id") int account_id) {       
        return dao.deleteAccount(account_id);
    }

}
