package internshipCoera.Controller;

import internshipCoera.Domain.ATM;
import internshipCoera.Domain.CreditCard;
import internshipCoera.Exceptions.ATMException;
import internshipCoera.Exceptions.CardException;
import internshipCoera.Exceptions.ExpiredCardException;
import internshipCoera.Exceptions.UnnecessaryFundsException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author Siri404
 */
public class Controller {
    private List<ATM> atms;
    private List<CreditCard> cards;
    private Map<String, Integer> distances;
    private LocalDateTime dateTime;

    public Controller(String atmsFile, String cardsFile, String distancesFile, String dateTimeFile) {
        readParams(atmsFile, cardsFile, distancesFile, dateTimeFile);
    }

    private void readATMS(String fileName) {
        Path path = Paths.get(fileName);
        ArrayList<ATM> atms = new ArrayList<>();
        try {
            Files.lines(path).forEach(line -> {
                List<String> items = Arrays.asList(line.split(","));
                List<String> timeArgs = Arrays.asList(items.get(2).split(":"));
                LocalTime openingTime = LocalTime.of(Integer.valueOf(timeArgs.get(0)), Integer.valueOf(timeArgs.get(1)));
                timeArgs = Arrays.asList(items.get(3).split(":"));
                LocalTime closingTime = LocalTime.of(Integer.valueOf(timeArgs.get(0)), Integer.valueOf(timeArgs.get(1)));
                ATM atm = new ATM(items.get(0), Float.valueOf(items.get(1)), openingTime, closingTime);
                atms.add(atm);
            });

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.atms = atms;
    }

    private void readCards(String fileName) {
        Path path = Paths.get(fileName);
        ArrayList<CreditCard> cards = new ArrayList<>();
        try {
            Files.lines(path).forEach(line -> {
                List<String> items = Arrays.asList(line.split(","));
                List<String> dateArgs = Arrays.asList(items.get(3).split("\\."));
                LocalDate date = LocalDate.of(Integer.valueOf(dateArgs.get(2)), Integer.valueOf(dateArgs.get(1)),
                        Integer.valueOf(dateArgs.get(0)));
                CreditCard creditCard = new CreditCard(items.get(0), Float.valueOf(items.get(1)),
                        Float.valueOf(items.get(2)), date, Float.valueOf(items.get(4)));
                cards.add(creditCard);
            });

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        this.cards = cards;
    }

    private void readDistances(String fileName) {
        Path path = Paths.get(fileName);
        HashMap<String, Integer> distances = new HashMap<>();

        try {
            Files.lines(path).forEach(line -> {
                List<String> items = Arrays.asList(line.split(","));
                distances.put(items.get(0) + items.get(1), Integer.valueOf(items.get(2)));
                distances.put(items.get(1) + items.get(0), Integer.valueOf(items.get(2)));
            });

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.distances = distances;
    }

    private void readDateTime(String fileName) {
        Path path = Paths.get(fileName);
        try {
            Optional<String> line = Files.lines(path).findFirst();
            line.ifPresent(s -> {
                        List<String> items = Arrays.asList(s.split(" "));
                        LocalDate date = LocalDate.of(Integer.valueOf(items.get(0)), Integer.valueOf(items.get(1)),
                                Integer.valueOf(items.get(2)));
                        LocalTime time = LocalTime.of(Integer.valueOf(items.get(3)), Integer.valueOf(items.get(4)));
                        this.dateTime = LocalDateTime.of(date, time);
                    }
            );
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void readParams(String atmsFile, String cardsFile, String distancesFile, String dateTimeFile) {
        readATMS(atmsFile);
        readCards(cardsFile);
        readDistances(distancesFile);
        readDateTime(dateTimeFile);
    }

    /**
     * Reset changes done while exploring other possible routes.
     */
    private void resetAtms() {
        atms.forEach(ATM::resetATM);
    }


    /**
     * Returns the time and date at which ATM opens - time necessary to cover the distance
     *
     * @param dateTime    - initial dateTime
     * @param openingTime - opening time for ATM
     * @return
     */
    private static LocalDateTime waitUntilOpen(LocalDateTime dateTime, LocalTime openingTime) {
        LocalDateTime dif = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(),
                openingTime.getHour(), openingTime.getMinute());

        //adjust date if necessary
        if (openingTime.isBefore(dateTime.toLocalTime())) {
            dif = dif.with(dif.plus(1, ChronoUnit.DAYS));
        }
        return dif;
    }

    /**
     * Withdraw the given amount from dest ATM and return the time necessary considering current location, distance to
     * ATM and operational hours.
     *
     * @param location   - starting location
     * @param dest       - ATM on which withdrawal is attempted
     * @param dateTime   - date and time at starting location
     * @param cards      - list of credit cards that can be used in withdrawal
     * @param waitedTime - the amount of time waited
     * @param amount     - the amount of money to withdraw
     * @return the time spent
     * @throws UnnecessaryFundsException if there are no remaining money on any of the cards
     */
    private long withdraw(String location, ATM dest, LocalDateTime dateTime, List<CreditCard> cards, long waitedTime,
                          float amount)
            throws UnnecessaryFundsException {
        try {
            long walkingTime = distances.get(location + dest.getName());
            dest.withdraw(cards.get(0), amount, dateTime.plus(walkingTime, ChronoUnit.MINUTES));
        } catch (ATMException e) {
            //Must wait until atm opens
            if (e.getMessage().equals("ATM closed!")) {
                long walkingTime = distances.get(location + dest.getName());
                LocalDateTime openingTime = waitUntilOpen(dateTime, dest.getOpeningTime()).minus(walkingTime, ChronoUnit.MINUTES);
                return this.withdraw(location, dest, openingTime, cards, dateTime.until(openingTime, ChronoUnit.MINUTES),
                        amount);
            } else {
                System.out.println(e.getMessage());
            }
        } catch (CardException e) {
            long remaining = (long) dest.withdrawRemaining(cards.get(0), dateTime);
            cards.remove(0);
            if (cards.size() == 0) {
                throw new UnnecessaryFundsException("Not enough money! Credit cards should have a total of 8000, fees" +
                        "not included!");
            }
            return this.withdraw(location, dest, dateTime, cards, waitedTime, amount - remaining);
        } catch (ExpiredCardException e){
            cards.remove(0);
            if (cards.size() == 0) {
                throw new UnnecessaryFundsException("Not enough money! Credit cards should have a total of 8000, fees" +
                        "not included!");
            }
            return this.withdraw(location, dest, dateTime, cards, waitedTime, amount);
        }
        return (long) distances.get(location + dest.getName()) + waitedTime;
    }

    /**
     * Find the time cost of the best route going through set of ATMs and finishing at given dest.
     *
     * @param atmSet   - set of ATMs that route must go through
     * @param dest     - end of the route
     * @param route    - best route so far
     * @param dateTime - time and date at this point in the route
     * @param cards    - copy of the list of credit cards
     * @return the time cost of the best route
     * @throws UnnecessaryFundsException if there are no remaining money on any of the cards
     */
    private long cost(Set<ATM> atmSet, ATM dest, List<ATM> route, LocalDateTime dateTime, List<CreditCard> cards)
            throws UnnecessaryFundsException {
        if (atmSet.size() == 1) {
            return withdraw("StartPoint", dest, dateTime, cards, 0, 2000);
        } else {
            atmSet.remove(dest);
            long cost = Long.MAX_VALUE;
            ATM nextATM = null;
            for (ATM atm : atmSet) {
                //starting new route -> reset resources
                Set<ATM> auxAtmSet = new HashSet<>(atmSet);
                List<ATM> newRoute = new ArrayList<>();
                dateTime = this.dateTime.with(this.dateTime);
                cards.clear();
                this.cards.forEach(creditCard -> cards.add(new CreditCard(creditCard)));
                resetAtms();
                long currentCost = cost(auxAtmSet, atm, newRoute, dateTime, cards);
                dateTime = this.dateTime.with(this.dateTime.plus(currentCost, ChronoUnit.MINUTES));
                currentCost += withdraw(atm.getName(), dest, dateTime, cards, 0, 2000);
                if (cost > currentCost) {
                    cost = currentCost;
                    route.clear();
                    route.addAll(newRoute);
                    nextATM = atm;
                }
            }

            route.add(nextATM);
            return cost;
        }
    }

    /**
     * Find the shortest time route that goes through all ATMs
     *
     * @return the shortest time route
     * @throws UnnecessaryFundsException if there are no remaining money on any of the cards
     */
    public List<ATM> getAtmsRoute() throws UnnecessaryFundsException {
        List<ATM> currentRoute = new ArrayList<>();
        List<ATM> bestRoute = new ArrayList<>();
        long currentCost, bestCost = Integer.MAX_VALUE;
        Set<ATM> S = new HashSet<>(atms);
        for (ATM atm : atms) {
            Set<ATM> aux = new HashSet<>(S);
            List<CreditCard> cards = new ArrayList<>();
            this.cards.forEach(creditCard -> cards.add(new CreditCard(creditCard)));
            currentCost = cost(aux, atm, currentRoute, dateTime, cards);
            currentRoute.add(atm);
            if (currentCost < bestCost) {
                bestCost = currentCost;
                bestRoute = new ArrayList<>(currentRoute);
            }
            currentRoute.clear();
        }
        return bestRoute;
    }

}
