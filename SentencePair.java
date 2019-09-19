/**
 * This is a sentence pair.
 *
 * @author Wang, Talia
 * @version 2017-06-01
 */
public class SentencePair
{
    // instance fields
    private boolean correctlyTranslated;
    private String english;
    private String french;

    /* constructors */

    /**
     * Constructs a sentence pair given 
     * its english and french sentences.
     * 
     * @param english english sentence
     * @param french french sentence
     */
    public SentencePair(String english, String french)
    {
        correctlyTranslated = false;
        this.english = english;
        this.french = french;
    } // end of constructor SentencePair(String, String)

    /* accessors */

    /**
     * Returns whether this sentence pair 
     * has been correctly translated.
     * 
     * @return whether this sentence pair 
     * has been correctly translated.
     */
    public boolean isCorrectlyTranslated()
    {
        return correctlyTranslated;
    } // end of isCorrectlyTranslated()

    /**
     * Returns the english sentence of this sentence pair.
     * 
     * @return the english sentence
     */
    public String getEnglish()
    {
        return english;
    } // end of getEnglish()

    /**
     * Returns the french sentence of this sentence pair.
     * 
     * @return the french sentence
     */
    public String getFrench()
    {
        return french;
    } // end of getFrench()

    /* mutators */

    /**
     * Sets whether this sentence pair 
     * has been correctly translated.
     * 
     * @param value true or false
     */
    public void setTranslated(boolean value)
    {
        correctlyTranslated = value;
    } // end of setTranslated (boolean value)

    /**
     * Sets the english sentence of this pair.
     * 
     * @param english english sentence
     */
    public void setEnglish(String english)
    {
        this.english = english;
    } // end of setEnglish (String english)

    /**
     * Sets the french sentence of this pair.
     * 
     * @param french english sentence
     */
    public void setFrench(String french)
    {
        this.french = french;
    } // end of setFrench (String french)
} // end of SentencePair
