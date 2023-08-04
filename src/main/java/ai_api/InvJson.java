package ai_api;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InvJson {
    @JsonProperty("amount")
    private int amount;

    @JsonProperty("item")
    private String item;

    // Constructor
    public InvJson() {
    }

    public InvJson(int amount, String item) {
        this.amount = amount;
        this.item = item;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setItem(String item) {
        this.item = item;
    }
}

