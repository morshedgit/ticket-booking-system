package yar.sam.dao;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import io.vertx.core.json.JsonArray;
import jakarta.inject.Singleton;
import jakarta.ws.rs.BeanParam;
import yar.sam.models.Account;
import yar.sam.models.Address;
import yar.sam.models.Contact;
import yar.sam.models.Seat;
import yar.sam.util.PaginationParams;

@Singleton
public class AccountDao extends BaseDao {
    private static final Logger LOGGER = Logger.getLogger(AccountDao.class);

    Function<Row,Seat> seatMapper = row -> {
        Seat createdSeat = new Seat();
        createdSeat.setId(row.getInteger("id")); 
        createdSeat.setRow(row.getInteger("row"));
        createdSeat.setCol(row.getInteger("col"));
        return createdSeat;
    };
    Function<Row,Address> addressMapper = row -> {
        Address createdAddress = new Address();
        createdAddress.setId(row.getInteger("id")); 
        createdAddress.setLine1(row.getString("line_1"));
        createdAddress.setLine2(row.getString("line_2"));
        createdAddress.setCity(row.getString("city"));
        createdAddress.setProvince(row.getString("province"));
        createdAddress.setCountryCode(row.getString("country_code"));
        createdAddress.setPostalCode(row.getString("postal_code"));
        return createdAddress;
    };
    Function<Row,Contact> contactMapper = row -> {
        ObjectMapper objectMapper = new ObjectMapper();
        Contact createdContact = new Contact();
        createdContact.setId(row.getInteger("id")); 
        createdContact.setEmail(row.getString("email"));
        createdContact.setPhoneNumber(row.getString("phone_number"));
        createdContact.setAddressIds(Arrays.asList(row.getArrayOfIntegers("address_ids")));

        try{
            JsonArray addressesJsonArray = row.getJsonArray("addresses");
            List<Address> addresses = objectMapper.readValue(addressesJsonArray.encode(), new TypeReference<List<Address>>() {});
            createdContact.setAddresses(addresses);
        } catch (JsonProcessingException e) {
            // Handle the exception or rethrow as a runtime exception
            throw new RuntimeException("Error processing JSON", e);
        }

        return createdContact;
    };

    Function<Row, Account> accountMapper = row -> {
        ObjectMapper objectMapper = new ObjectMapper();
        Account createdAccount = new Account();
        createdAccount.setId(row.getInteger("id")); 
        createdAccount.setFirstName(row.getString("first_name"));
        createdAccount.setLastName(row.getString("last_name"));
        createdAccount.setFullName(row.getString("full_name"));
        createdAccount.setContactIds(Arrays.asList(row.getArrayOfIntegers("contact_ids")));

        try {
            JsonArray contactsJsonArray = row.getJsonArray("contacts");
            List<Contact> contacts = objectMapper.readValue(contactsJsonArray.encode(), new TypeReference<List<Contact>>() {});
            createdAccount.setContacts(contacts);
        } catch (JsonProcessingException e) {
            // Handle the exception or rethrow as a runtime exception
            throw new RuntimeException("Error processing JSON", e);
        }        
    
        return createdAccount;
    };
    
    public Uni<List<Address>> getAddresses(int accountId,int contactId, @BeanParam PaginationParams paginationParams) {        

        String query =  """
                            SELECT 
                                * 
                            FROM address ad
                            JOIN contact ct ON ad.id = ANY(ct.address_ids) AND ct.id = $2
                            JOIN account ac ON ac.id = $1 AND ct.id = ANY(ac.contact_ids)
                        """;
        // Use non-blocking operations with Uni
        String orderByTablePrefix = "ad";
        return this.readAll(query, List.of(accountId,contactId), addressMapper, paginationParams,orderByTablePrefix);
    }

    public Uni<Address> addAddress(int accountId, int contactId, Address address) {
        String query = """
                    WITH i AS (
                        INSERT INTO address (line_1, line_2, city, province, country_code, postal_code) VALUES ($1, $2, $3, $4, $5, $6) RETURNING *
                    ),
                    u AS (
                        UPDATE contact SET address_ids = array_append(address_ids, (SELECT id FROM i) ) WHERE id = $7
                    )
                    SELECT * FROM i                
                """;
        return this.create(query, List.of(address.getLine1(), address.getLine2(), address.getCity(), address.getProvince(), address.getCountryCode(), address.getPostalCode(),contactId), addressMapper);
    }  

    public Uni<Void> updateAddress(Address address) {
        return client.withTransaction(transaction -> 
            transaction
                .preparedQuery("UPDATE address SET line_1 = $1, line_2 = $2, city = $3, province = $4, country_code = $5, postal_code = $6 WHERE id = $7")
                .execute(Tuple.tuple()
                    .addValue(address.getLine1())
                    .addValue(address.getLine2())
                    .addValue(address.getCity())
                    .addValue(address.getProvince())
                    .addValue(address.getCountryCode())
                    .addValue(address.getPostalCode())
                    .addValue(address.getId())
                )
                .onItem().transformToUni(ignored -> Uni.createFrom().voidItem())
        );
    }   

    public Uni<Void> deleteAddress(int addressId) {
        String query = """
                    WITH d AS (
                        DELETE FROM address WHERE id = $1
                    )
                    UPDATE 
                        contact ct
                    SET address_ids = array_remove(address_ids, $1)
                    WHERE $1 = ANY(ct.address_ids)
                """;
        return this.delete(query, List.of(addressId));
    }    
    
    public Uni<List<Contact>> getContacts(int accountId, @BeanParam PaginationParams paginationParams) {        

        String query =  """
                            SELECT 
                                ct.*,
                                json_agg(row_to_json(ad.*)) AS addresses
                            FROM contact ct
                            LEFT JOIN address ad ON ad.id = ANY(ct.address_ids)
                            JOIN account ac ON ac.id = $1 AND ct.id = ANY(ac.contact_ids)
                            GROUP BY ct.id
                        """;
        // Use non-blocking operations with Uni
        return this.readAll(query, List.of(accountId), contactMapper,paginationParams);
    }

    public Uni<Contact> addContact(int accountId, Contact contact) {
        LOGGER.error(contact.getEmail());

        String query = """
                WITH ct AS (
                    INSERT INTO contact (email,phone_number) VALUES ($1,$2) RETURNING *
                ),
                ac_up AS (
                    UPDATE account ac SET contact_ids = array_append(contact_ids,ct.id)
                    FROM ct WHERE ac.id = $3
                )
                SELECT *, '[]'::JSON as address_ids, '[]'::JSON as addresses FROM ct
                """;
        
        return this.create(query, List.of(contact.getEmail(),contact.getPhoneNumber(),accountId), contactMapper);
    }  

    public Uni<Void> updateContact(Contact contact) {
        String query = """
                UPDATE contact ct SET email = $1, phone_number = $2, address_ids = $3 WHERE id = $4
                """;
        return this.update(query, List.of(contact.getEmail(),contact.getPhoneNumber(),contact.getAddressIds().toArray(new Integer[0]),contact.getId()));
    }   

    
    public Uni<Void> deleteContact(int contactId) {
        String query = """
                WITH d AS (
                    DELETE FROM contact WHERE id = $1
                )
                UPDATE 
                    account ac
                SET contact_ids = array_remove(contact_ids, $1)
                WHERE $1 = ANY(ac.contact_ids)
                """;
        return this.delete(query, List.of(contactId));
    }

    public Uni<List<Account>> getAccounts(Optional<String> emailFilter) {        

        String query =  """
            -- Get accounts with contact_ids and address_ids
            SELECT 
                ac.*,
                json_agg(row_to_json(ct.*)
                ) AS contacts
            FROM "account" ac
            LEFT JOIN (
                SELECT 
                    ct.*,
                    json_agg(row_to_json(ad.*)) AS addresses
                FROM contact ct
                LEFT JOIN address ad ON ad.id = ANY(ct.address_ids)
                WHERE ($1::VARCHAR IS NOT NULL AND email = $1::VARCHAR) OR TRUE
                GROUP BY ct.id
            ) ct ON ct.id = ANY(ac.contact_ids)
            GROUP BY ac.id;       
                        """;

            List<Object> parameters = new ArrayList<>();
            parameters.add(emailFilter.orElse(null)); // This will add null to the list if emailFilter is empty

            return this.readAll(query, parameters, accountMapper);
    }


    public Uni<Account> addAccount( Account account) {

        String query = """
            WITH inserted_account AS (
                INSERT INTO account (first_name, last_name, full_name)
                VALUES ($1,$2,$3)
                RETURNING *
            )
            SELECT ac.*, '[]'::JSON AS contacts
            FROM inserted_account ac
                """;

        return this.create(query, List.of(account.getFirstName(),account.getLastName(),account.getFullName()), accountMapper);
    }  

    public Uni<Void> updateAccount(Account account) {
        String query = """
                UPDATE account ac SET first_name = $1, last_name = $2, full_name = $3, contact_ids = $4 WHERE id = $5
                """;

        return this.update(query, List.of(account.getFirstName(),account.getLastName(),account.getFullName(),account.getContactIds().toArray(new Integer[0]),account.getId()));

    }   

    public Uni<Void> deleteAccount(int accountId) {
        String query = """
                DELETE FROM account WHERE id = $1
                """;
        return this.delete(query,List.of(accountId));
    }

}