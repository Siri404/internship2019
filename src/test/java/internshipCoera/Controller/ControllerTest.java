package internshipCoera.Controller;

import internshipCoera.Domain.ATM;
import internshipCoera.Exceptions.UnnecessaryFundsException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ControllerTest {
    private Controller testCtrl1;
    private Controller testCtrl2;
    private Controller testCtrl3;

    @Before
    public void setUp() {
        String atmsFile1 = "src/test/resources/ATMS_Test1.in";
        String cardsFile1 = "src/test/resources/Cards_Test1.in";
        String distancesFile1 = "src/test/resources/Distances_Test1.in";
        String dateTimeFile1 = "src/test/resources/DateTime_Test1.in";
        testCtrl1 = new Controller(atmsFile1, cardsFile1, distancesFile1, dateTimeFile1);

        String atmsFile2 = "src/test/resources/ATMS_Test2.in";
        String cardsFile2 = "src/test/resources/Cards_Test2.in";
        String distancesFile2 = "src/test/resources/Distances_Test2.in";
        String dateTimeFile2 = "src/test/resources/DateTime_Test2.in";
        testCtrl2 = new Controller(atmsFile2, cardsFile2, distancesFile2, dateTimeFile2);

        String atmsFile3 = "src/test/resources/ATMS_Test3.in";
        String cardsFile3 = "src/test/resources/Cards_Test3.in";
        String distancesFile3 = "src/test/resources/Distances_Test3.in";
        String dateTimeFile3 = "src/test/resources/DateTime_Test3.in";
        testCtrl3 = new Controller(atmsFile3, cardsFile3, distancesFile3, dateTimeFile3);
    }

    @After
    public void tearDown(){
        testCtrl1 = null;
        testCtrl2 = null;
        testCtrl3 = null;
    }

    @Test
    public void getAtmsRoute() throws UnnecessaryFundsException {
        List<ATM> result = testCtrl1.getAtmsRoute();
        assert result.get(0).getName().equals("ATM 2");
        assert result.get(1).getName().equals("ATM 3");
        assert result.get(2).getName().equals("ATM 1");

        result = testCtrl2.getAtmsRoute();
        assert result.get(0).getName().equals("ATM 2");
        assert result.get(1).getName().equals("ATM 3");
        assert result.get(2).getName().equals("ATM 1");

        result = testCtrl3.getAtmsRoute();
        assert "ATM 3ATM 1ATM 2 ATM 1ATM 3ATM 2".contains(result.get(0).getName() + result.get(1).getName() +
                result.get(2).getName());
    }
}