package internshipCoera;

import internshipCoera.Controller.Controller;
import internshipCoera.Exceptions.UnnecessaryFundsException;


public class App {
    public static void main(String[] args){
        String atmsFile = "src/main/resources/ATMS.in";
        String cardsFile = "src/main/resources/Cards.in";
        String distancesFile = "src/main/resources/Distances.in";
        String dateTimeFile = "src/main/resources/DateTime.in";
        Controller controller = new Controller(atmsFile, cardsFile, distancesFile, dateTimeFile);
        try{
            System.out.println(controller.getAtmsRoute());

        }catch (UnnecessaryFundsException e){
            System.out.println(e.getMessage());
        }
    }
}
