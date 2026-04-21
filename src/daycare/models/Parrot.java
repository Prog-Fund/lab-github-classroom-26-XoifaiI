package daycare.models;

import daycare.utils.BirdUtility;

/**
 * Parrot: a Bird with a vocabulary tier (Basic / Intermediate / Advanced / Amazing).
 *
 * <p>vocabularySize is a category String not a raw word count (per spec). the
 * ctor takes the raw int and runs it through {@link BirdUtility#vocabularyCategory(int)}
 * so the model only ever stores the tier.
 */
public class Parrot extends Bird {

  public static final float BASE_DAILY_RATE = 10.00f;

  protected String vocabularySize = BirdUtility.DEFAULT_VOCAB_CATEGORY;

  public Parrot(String name, int age, Owner owner, int id,
      double wingSpan, boolean canFly, int vocabularySize) {
    super(name, age, owner, id, wingSpan, canFly);
    this.vocabularySize = BirdUtility.vocabularyCategory(vocabularySize);
  }

  @Override
  public double calculateWeeklyFee() {
    return BASE_DAILY_RATE * numOfDaysAttending();
  }

  public String getVocabularySize() {
    return vocabularySize;
  }

  /** takes a raw word count and stores the categorised tier. negatives fall back to the default. */
  public void setVocabularySize(int words) {
    this.vocabularySize = BirdUtility.vocabularyCategory(words);
  }

  @Override
  public String toString() {
    return super.toString()
        + String.format(" vocab=%s fee=%.2f", vocabularySize, calculateWeeklyFee());
  }
}
