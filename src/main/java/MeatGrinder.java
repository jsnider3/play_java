import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a calculator that repeatedly reduces an initial value by a percentage and adds a
 * constant, until it reaches a threshold.
 */
public class MeatGrinder {
  private int initialValue;
  private int reductionPercentage;
  private int additiveConstant;

  private Random random;

  /**
   * Creates a new MeatGrinder instance.
   *
   * @param initialValue The starting value before reductions
   * @param reductionPercentage The percentage by which to reduce the value in each step
   * @param additiveConstant The constant amount to add in each step
   */
  public MeatGrinder(int initialValue, int reductionPercentage, int additiveConstant) {
    this.initialValue = initialValue;
    this.reductionPercentage = reductionPercentage;
    this.additiveConstant = additiveConstant;
    this.random = new Random();
  }

  /**
   * Calculates the reduced value after repeated reductions and additions until it falls below the
   * threshold.
   *
   * @param thresholdValue The value below which reduction stops
   * @param isRandom Whether to use a fixed reduction percentage or random rolls
   * @return The final reduced value
   */
  public int calculateReducedValue(int thresholdValue, boolean isRandom) {
    int currentValue = initialValue;
    int prevValue = currentValue;
    int step = 1;

    while (currentValue >= thresholdValue) {
      prevValue = currentValue;

      int finalReduction = isRandom ? roll2dN() : reductionPercentage;

      currentValue = currentValue * (100 - finalReduction) / 100 + additiveConstant;

      if (currentValue >= prevValue) {
        break;
      }
    }

    return currentValue;
  }

  /**
   * Calculates the value after repeated reductions and additions until it falls below the
   * threshold.
   *
   * @param thresholdValue The value below which reduction stops
   * @return The final reduced value
   */
  public int calculateReducedValue(int thresholdValue) {
    return calculateReducedValue(thresholdValue, false);
  }

  /**
   * Calculates the value after repeated reductions (based on dice rolls) and additions until it
   * falls below the threshold.
   *
   * @param thresholdValue The value below which reduction stops
   * @return The final reduced value
   */
  public int calculateRandomlyReducedValue(int thresholdValue) {
    return calculateReducedValue(thresholdValue, true);
  }

  /**
   * Rolls 2dN dice (two dice with N sides each) and returns the sum.
   *
   * @return The percentage determined by the dice roll
   */
  private int roll2dN() {
    int die1 = random.nextInt(reductionPercentage);
    int die2 = random.nextInt(reductionPercentage);
    return die1 + die2;
  }

  /** Main method demonstrating the usage of the MeatGrinder class. */
  public static void main(String[] args) {
    MeatGrinder calculator = new MeatGrinder(45000, 11, 200);
    // int reducedValue = calculator.calculateRandomlyReducedValue(10);
    // System.out.println("Final value: " + reducedValue);
    List<Integer> results = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      int result = calculator.calculateRandomlyReducedValue(10);
      System.out.println("result " + i + " " + result);
      results.add(result);
    }

    double mean = calculateMean(results);
    double stdDev = calculateStandardDeviation(results, mean);

    System.out.println("Mean: " + mean);
    System.out.println("Standard deviation: " + stdDev);
  }

  private static double calculateMean(List<Integer> values) {
    double sum = 0;
    for (int value : values) {
      sum += value;
    }
    return sum / values.size();
  }

  private static double calculateStandardDeviation(List<Integer> values, double mean) {
    double variance = 0;
    for (int value : values) {
      variance += Math.pow(value - mean, 2);
    }
    variance /= values.size();
    return Math.sqrt(variance);
  }
}
