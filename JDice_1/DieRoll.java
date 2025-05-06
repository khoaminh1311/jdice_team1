import java.util.Random;
import java.util.logging.Logger;

/**
 * DieRoll class for simulating rolling dice.
 * 
 * Refactored:
 * - Renamed variables to be more descriptive.
 * - Simplified toString() method for better readability.
 * - Added input validation in constructor.
 * - Added logging for each die roll.
 */
public class DieRoll {
    private final int numberOfDice;
    private final int numberOfSides;
    private final int bonus;
    private static final Random random = new Random();
    private static final Logger logger = Logger.getLogger(DieRoll.class.getName());

    /**
     * Constructs a DieRoll object.
     * 
     * @param numberOfDice  Number of dice to roll, must be > 0
     * @param numberOfSides Number of sides per die, must be > 1
     * @param bonus         Bonus to be added to total roll
     * @throws IllegalArgumentException if dice or sides are invalid
     */
    public DieRoll(int numberOfDice, int numberOfSides, int bonus) {
        if (numberOfDice <= 0) {
            throw new IllegalArgumentException("Number of dice must be greater than 0.");
        }
        if (numberOfSides <= 1) {
            throw new IllegalArgumentException("Number of sides must be greater than 1.");
        }
        this.numberOfDice = numberOfDice;
        this.numberOfSides = numberOfSides;
        this.bonus = bonus;
    }

    /**
     * Performs the dice roll and returns the result.
     * 
     * @return RollResult object with detailed outcome.
     */
    public RollResult makeRoll() {
        RollResult result = new RollResult(bonus);
        for (int i = 0; i < numberOfDice; i++) {
            int roll = random.nextInt(numberOfSides) + 1;
            logger.info("Rolled a " + roll);
            result.addResult(roll);
        }
        return result;
    }

    /**
     * Returns a string representation in the format NdM±Bonus.
     * 
     * @return formatted roll string
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(numberOfDice).append("d").append(numberOfSides);
        if (bonus > 0) builder.append("+").append(bonus);
        else if (bonus < 0) builder.append(bonus);
        return builder.toString();
    }
}
