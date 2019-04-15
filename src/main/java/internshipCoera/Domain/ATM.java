package internshipCoera.Domain;

import internshipCoera.Exceptions.ATMException;
import internshipCoera.Exceptions.CardException;
import internshipCoera.Exceptions.ExpiredCardException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


/**
 * Class for ATMs
 * @author Siri404
 */
public class ATM {
    private String name;
    private float availableAmount;
    private final float initialAmount;
    private LocalTime openingTime;
    private LocalTime closingTime;

    public ATM(String name, float amount, LocalTime openingTime, LocalTime closingTime){
        this.name = name;
        this.availableAmount = amount;
        this.initialAmount = amount;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    @Override
    public String toString(){
        return name;
    }

    public void setAvailableAmount(float amount){
        availableAmount = amount;
    }

    public String getName(){
        return name;
    }

    public LocalTime getOpeningTime(){
        return openingTime;
    }

    /**
     * Withdraw the specified amount using given card
     * @param card - the credit card used
     * @param amount - the amount to withdraw
     * @param dateTime - the date and time of the transaction
     * @return - the withdrawn amount
     * @throws ATMException if time not in operational hours
     * @throws CardException if the given card lacks the necessary amount
     * @throws ExpiredCardException if the card is expired at transaction date
     */
    public float withdraw(CreditCard card, float amount, LocalDateTime dateTime) throws ATMException, CardException,
            ExpiredCardException {
        if(!this.isOpen(LocalTime.from(dateTime))) {
            throw new ATMException("ATM closed!");
        }

        if(card.getExpirationDate().isBefore(LocalDate.from(dateTime))){
            throw new ExpiredCardException("Withdrawal rejected - Card has expired!");
        }

        if(amount > card.getWithdrawLimit()){
            amount = card.getWithdrawLimit();
        }

        float necessaryAmount = amount + (card.getFee()*amount)/100;
        if(necessaryAmount > this.availableAmount){
            throw new ATMException("Withdrawal rejected - ATM does not have the inquired amount!");
        }
        if(necessaryAmount > card.getAvailableAmount()){
            throw new CardException("Withdrawal rejected - your account does not have the inquired amount!");
        }

        availableAmount = availableAmount - amount;
        card.setAvailableAmount(card.getAvailableAmount() - necessaryAmount);
        return amount;
    }

    /**
     * Withdraw the remaining amount of money from the card.
     * Used after a rejected withdrawal caused by the lack of the inquired amount to withdraw all remaining money before
     * attempting to withdraw the rest using another card.
     * Function does not throw any exception because it is only called after necessary checks have been made in previous
     * withdrawal attempt.
     * @param card - credit card used
     * @param date - transaction date
     * @return remaining amount on the card.
     */
    public float withdrawRemaining(CreditCard card, LocalDateTime date){
        float amount = card.getAvailableAmount() - (card.getFee()*card.getAvailableAmount())/100;
        card.setAvailableAmount(0);
        availableAmount = availableAmount - amount;
        return amount;
    }

    /**
     * Check if ATM is operational at given time
     * @param time - transaction time
     * @return true if time in [openingTime, closingTime]
     */
    public boolean isOpen(LocalTime time){
        if(time.equals(closingTime) || time.equals(openingTime)){
            return true;
        }
        if(openingTime.isBefore(closingTime)){
            return (time.isAfter(openingTime) && time.isBefore(closingTime));
        }
        else {
            return (time.isAfter(openingTime) || time.isBefore(closingTime));
        }
    }

    /**
     * Reset available amount to initial amount.
     * Used to undo changes done when exploring other possible routes.
     */
    public void resetATM(){
        this.availableAmount = initialAmount;
    }


}
