import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MeatGrinderTest {
  @Test
  public void testSimpleReduction() {
    MeatGrinder calculator = new MeatGrinder(100, 20, 5);
    int reducedValue = calculator.calculateReducedValue(90);
    assertEquals(85, reducedValue);
  }

  @Test
  public void testMultipleReductions() {
    MeatGrinder calculator = new MeatGrinder(100, 20, 5);
    int reducedValue = calculator.calculateReducedValue(60);
    assertEquals(55, reducedValue);
  }

  @Test
  public void testThresholdReached() {
    MeatGrinder calculator = new MeatGrinder(100, 50, 0);
    int reducedValue = calculator.calculateReducedValue(15);
    assertEquals(12, reducedValue);
  }

  @Test
  public void testInfiniteLoopDetection() {
    MeatGrinder calculator = new MeatGrinder(100, 20, 5);
    int reducedValue = calculator.calculateReducedValue(10);
    assertEquals(25, reducedValue);
  }
}
