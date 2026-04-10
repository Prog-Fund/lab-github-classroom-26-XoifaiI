package daycare.models;

import daycare.utils.BirdUtility;

/**
 * Parrot: a Bird with a vocabulary tier (Basic / Intermediate / Advanced / Amazing).
 *
 * <p>per spec, vocabularySize is a String tier (not a raw word count). the
 * ctor accepts the raw int the user typed and runs it through
 * {@link BirdUtility#vocabularyCategory(int)} so the model only ever stores
 * the categorised version. invalid input falls back to the default
 * ({@link BirdUtility#DEFAULT_VOCAB_CATEGORY}).
 *
 * <p>weekly fee is a flat {@link #BASE_DAILY_RATE} per attending day. anita
 * loves parrots so theres no surcharge for the chatty ones.
 *
 * <p>Returns: instance of Parrot.
 *
 * <p>Example:
 * <pre>{@code
 * Owner bob = new Owner(2, "Bob", "1 Oak Rd", "555-0200", "b@x.com");
 * Parrot polly = new Parrot("Polly", 7, bob, 0, 60, true, 35);
 * polly.checkIn(0);
 * polly.checkIn(3);
 * String tier = polly.getVocabularySize(); // "Advanced"
 * double fee = polly.calculateWeeklyFee(); // 10.00 * 2 = 20.00
 * }</pre>
 */
public class Parrot extends Bird {

  /** flat daily rate per attending day, no chatty surcharge. */
  public static final float BASE_DAILY_RATE = 10.00f;

  protected String vocabularySize = BirdUtility.DEFAULT_VOCAB_CATEGORY;

  public Parrot(String name, int age, Owner owner, int id,
      double wingSpan, boolean canFly, int vocabularySize) {
    super(name, age, owner, id, wingSpan, canFly);
    // ctor takes the raw word count and stores the categorised tier. negatives
    // get caught by BirdUtility and fall through to the default.
    this.vocabularySize = BirdUtility.vocabularyCategory(vocabularySize);
  }

  @Override
  public double calculateWeeklyFee() {
    return BASE_DAILY_RATE * numOfDaysAttending();
  }

  public String getVocabularySize() {
    return vocabularySize;
  }

  /**
   * Setter that takes a raw word count and stores the categorised tier.
   *
   * <p>matches the UML which lists {@code setVocabularySize(int)}. matches the
   * "no else in setters" rule by silently re-categorising negative inputs to
   * the default tier inside {@link BirdUtility#vocabularyCategory(int)}.
   */
  public void setVocabularySize(int words) {
    this.vocabularySize = BirdUtility.vocabularyCategory(words);
  }

  @Override
  public String toString() {
    return super.toString()
        + String.format(" vocab=%s fee=%.2f", vocabularySize, calculateWeeklyFee());
  }
}
