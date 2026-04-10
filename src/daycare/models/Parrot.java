package daycare.models;

/**
 * Parrot: a Bird with a vocabulary, the size of which determines its day rate.
 *
 * <p>chatty parrots (vocab >= {@link #CHATTY_THRESHOLD} words) cost more bc
 * they need more interaction and stimulation throughout the day. quiet ones
 * just hang out and eat seeds, so theyre cheaper.
 *
 * <p>Returns: instance of Parrot.
 *
 * <p>Example:
 * <pre>{@code
 * Owner bob = new Owner(2, "Bob", "1 Oak Rd", "555-0200", "b@x.com");
 * Parrot polly = new Parrot("Polly", 7, bob, 300, 0.6, true, 35);
 * polly.checkIn(0);
 * polly.checkIn(3);
 * double fee = polly.calculateWeeklyFee(); // 14.00 * 2 = 28.00 (chatty)
 * }</pre>
 */
public class Parrot extends Bird {

  /** vocab size at which a parrot graduates from "quiet" to "chatty". */
  public static final int CHATTY_THRESHOLD = 20;

  /** what a quiet parrot costs per attending day. */
  public static final float BASE_DAILY_RATE = 8.00f;

  /** what a chatty parrot (vocab >= CHATTY_THRESHOLD) costs per attending day. */
  public static final float CHATTY_DAILY_RATE = 14.00f;

  protected int vocabularySize;

  public Parrot(String name, int age, Owner owner, int id,
      double wingSpan, boolean canFly, int vocabularySize) {
    super(name, age, owner, id, wingSpan, canFly);
    this.vocabularySize = vocabularySize;
  }

  @Override
  public double calculateWeeklyFee() {
    float rate = vocabularySize >= CHATTY_THRESHOLD ? CHATTY_DAILY_RATE : BASE_DAILY_RATE;
    return rate * numOfDaysAttending();
  }

  public int getVocabularySize() {
    return vocabularySize;
  }

  public void setVocabularySize(int vocabularySize) {
    this.vocabularySize = vocabularySize;
  }

  @Override
  public String toString() {
    return super.toString()
        + String.format(" vocab=%d fee=%.2f", vocabularySize, calculateWeeklyFee());
  }
}
