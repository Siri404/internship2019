package internshipCoera.Domain;

import java.time.LocalDate;


/**
 * Class for credit cards
 * @author Siri404
 */
public class CreditCard {
    private String type;
    private float fee;
    private float withdrawLimit;
    private LocalDate expirationDate;
    private float availableAmount;

    public CreditCard(String type, float fee, float withdrawLimit, LocalDate expirationDate, float amount){
        this.type = type;
        this.fee = fee;
        this.withdrawLimit = withdrawLimit;
        this.expirationDate = expirationDate;
        this.availableAmount = amount;
    }

    public CreditCard(CreditCard other){
        this.type = other.getType();
        this.fee = other.getFee();
        this.withdrawLimit = other.getWithdrawLimit();
        this.expirationDate = other.getExpirationDate();
        this.availableAmount = other.getAvailableAmount();
    }

    public String getType() {
        return type;
    }

    public float getFee() {
        return fee;
    }

    public float getAvailableAmount(){
        return availableAmount;
    }

    public void setAvailableAmount(float availableAmount) {
        this.availableAmount = availableAmount;
    }

    public LocalDate getExpirationDate(){
        return expirationDate;
    }

    public void setExpirationDate(LocalDate date){
        expirationDate = date;
    }

    public float getWithdrawLimit(){
        return withdrawLimit;
    }

}
