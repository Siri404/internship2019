package internshipCoera.Domain;

import internshipCoera.Exceptions.ATMException;
import internshipCoera.Exceptions.CardException;
import internshipCoera.Exceptions.ExpiredCardException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.*;

public class ATMTest {
    private ATM atm1;
    private ATM atm2;
    private LocalTime time;
    private CreditCard card;
    private LocalDateTime date;


    @Before
    public void setUp() {
        atm1 = new ATM("ATM1", 50000, LocalTime.of(17,0), LocalTime.of(20,0));
        atm2 = new ATM("ATM2", 50000, LocalTime.of(17,0), LocalTime.of(1,0));
        time = LocalTime.of(23, 30);
        date = LocalDateTime.of(LocalDate.of(2020,4,13), time);
        card = new CreditCard("silver", (float) 0.2, 2000,
                LocalDate.of(2019, 4, 13), 3000);
    }

    @After
    public void tearDown() {
        atm1 = null;
        atm2 = null;
        time = null;
    }

    @Test
    public void withdraw() {
        try{
            atm1.withdraw(card, 200, date);
        }catch (ExpiredCardException | CardException  e){
            fail();
        }catch (ATMException e){
            assertEquals("ATM closed!", e.getMessage());
        }

        time = LocalTime.of(19, 30);
        date = LocalDateTime.of(LocalDate.of(2020,4,13), time);

        try{
            atm1.withdraw(card, 200, date);
        }catch (CardException | ATMException e){
            fail();
        }catch (ExpiredCardException e){
            assertEquals("Withdrawal rejected - Card has expired!", e.getMessage());
        }

        card.setExpirationDate(LocalDate.of(2021, 11, 11));
        try{
            assert (2000 == atm1.withdraw(card, 2000000, date));
        }catch (ExpiredCardException | CardException | ATMException e){
            fail();
        }

        try{
            atm1.withdraw(card, 2000, date);
        }catch (ExpiredCardException | ATMException e){
            fail();
        }catch (CardException e){
            assertEquals("Withdrawal rejected - your account does not have the inquired amount!",
                    e.getMessage());
        }

        atm1.setAvailableAmount(0);

        try{
            atm1.withdraw(card, 1, date);
        }catch (ExpiredCardException | CardException e){
            fail();
        }catch (ATMException e){
            assertEquals("Withdrawal rejected - ATM does not have the inquired amount!",
                    e.getMessage());
        }

    }

    @Test
    public void withdrawRemaining() {
        card.setAvailableAmount(1000);
        assert(998 == atm2.withdrawRemaining(card, date));
    }

    @Test
    public void isOpen(){
        assert !atm1.isOpen(time);
        assert atm2.isOpen(time);

        time = LocalTime.of(16,0);
        assert !atm1.isOpen(time);
        assert !atm2.isOpen(time);

        time = LocalTime.of(0,0);
        assert atm2.isOpen(time);

        time = LocalTime.of(18,0);
        assert atm1.isOpen(time);
    }
}