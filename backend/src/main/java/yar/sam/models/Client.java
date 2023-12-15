package yar.sam.models;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Client {
    private int id;
    private String name;
    private List<Account> managers;
    @JsonProperty("manager_ids")
    private List<Integer> managerIds;
    public List<Integer> getManagerIds() {
        return managerIds;
    }
    public void setManagerIds(List<Integer> managerIds) {
        this.managerIds = managerIds;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Account> getManagers() {
        return managers;
    }
    public void setManagers(List<Account> managers) {
        this.managers = managers;
    }

}

