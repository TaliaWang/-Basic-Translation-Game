import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.InputStreamReader;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.io.PrintWriter;
import java.util.Random;

/**
 * This is a translation game.
 *
 * @author Wang, Talia
 * @version 2017-06-01
 */
public class TranslationGameGUI
{
    // class constants
    private static final Color BACKGROUND_COLOUR = Color.WHITE;
    private static final BufferedReader console = 
        new BufferedReader(new InputStreamReader(System.in));
    private static final int FRAME_HEIGHT = 600;
    private static final int FRAME_WIDTH = 1000;
    private final int MINIMUM_NUMBER_OF_SENTENCES = 1;
    private final int MAXIMUM_NUMBER_OF_SENTENCES = 10;
    private final String SENTINEL_VALUE = "done";
    private final String WELCOME_IMAGE = "welcome.png";

    private final String ENGLISH_FILE = "english.text";
    private final String FRENCH_FILE = "french.text";
    private final String IMAGE_FILE = "image.text";
    private final String IMAGE_SOURCE_FILE = "imageSource.text";
    private final String SCORE_FILE = "playerScore.text";

    private final BufferedReader ENGLISH_INPUT_FILE = 
        new BufferedReader(new FileReader(ENGLISH_FILE));
    private final BufferedReader FRENCH_INPUT_FILE = 
        new BufferedReader(new FileReader(FRENCH_FILE));
    private final BufferedReader IMAGE_INPUT_FILE = 
        new BufferedReader(new FileReader(IMAGE_FILE));
    private final BufferedReader IMAGE_SOURCE_INPUT_FILE = 
        new BufferedReader(new FileReader(IMAGE_SOURCE_FILE));

    // instance fields
    private ImageComponent currentImage;
    private SentencePair currentSentence;
    private int currentSentenceIndex;
    private JFrame frame;
    private boolean gameFinished;
    private ImageComponent[] gameImage;
    private int goal;
    private JButton imageSourcesButton;
    private String input;
    private boolean isEnglish;
    private JLabel label;
    private int numberTranslated;
    private JButton quitButton;
    private Random randomGenerator;
    private JButton resetScoresButton;
    private BufferedReader scoreInputFile;
    private PrintWriter scoreOutputFile;
    private SentencePair[] sentencePair;
    private HashMap<Integer, SentencePair> sentence;
    private JButton startButton;
    private ImageComponent welcomeImage;

    /* constructors */

    /**
     * Constructs a translation game with a graphical user interface.
     */
    public TranslationGameGUI() throws IOException
    {
        currentSentence = new SentencePair("", "");
        gameFinished = false;
        gameImage = new ImageComponent[MAXIMUM_NUMBER_OF_SENTENCES];
        goal = 0;
        imageSourcesButton = new JButton("");
        input = "";
        isEnglish = false;
        numberTranslated = 0;
        quitButton = new JButton("");
        randomGenerator = new Random();
        resetScoresButton = new JButton("");
        sentence = new HashMap<Integer, SentencePair>();
        sentencePair = new SentencePair[MAXIMUM_NUMBER_OF_SENTENCES];
        startButton = new JButton("");
        welcomeImage = null;

        // Set up the necessary components.
        fillHashMap();
        loadGameImages();
        makeFrame();
    } // end of constructor TranslationGameGUI() throws IOException

    /* methods */

    /**
     * Runs this translation game from start to finish.
     */
    public void runGame() throws IOException
    {
        gatherGoal();

        /*
         * Runs the game until the sentinel value is entered 
         * or the user has translated his/her goal.
         */
        do
        {
            generateSentence();
            changeImage(currentSentenceIndex);
            getInput();
            processInput();
        } 
        while (numberTranslated < goal && !gameFinished);

        resetWhetherTranslated();
        displayScores();
        finishGame();
    }// end of runGame() throws IOException

    /*
     * Fills the hash map with sentence pairs.
     */
    private void fillHashMap()
    {
        for (int i = 0; i < MAXIMUM_NUMBER_OF_SENTENCES; i++)
        {
            // Ensure files exist.
            try
            {
                /* Gets english and french sentences 
                from their respective files. */
                String englishSentence = ENGLISH_INPUT_FILE.readLine();
                String frenchSentence = FRENCH_INPUT_FILE.readLine();
                sentencePair[i] = new SentencePair(englishSentence, 
                    frenchSentence);
                sentence.put(i + 1, sentencePair[i]);
            }
            catch (Exception exception)
            {
                // Display error message.
                System.out.println("File(s) not found.");
            } // end of catch (Exception exception)
        } // end of for (int i = 0...)
    } // end of method fillHashMap()

    /*
     * Creates the button panel.
     */
    private void makeButtonPanel()
    {
        // Create an actionListener for the buttons.
        ButtonListener actionListener = new ButtonListener();

        // Create a button panel.
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND_COLOUR);
        frame.add(buttonPanel, BorderLayout.PAGE_END);

        // Change text of buttons.
        imageSourcesButton.setText("Display Image Sources");
        quitButton.setText("Quit Game");
        startButton.setText("Start Game");
        resetScoresButton.setText("Reset Scores");

        imageSourcesButton.addActionListener(actionListener);
        quitButton.addActionListener(actionListener);
        startButton.addActionListener(actionListener);
        resetScoresButton.addActionListener(actionListener);

        buttonPanel.add(startButton);
        buttonPanel.add(quitButton);
        buttonPanel.add(resetScoresButton);
        buttonPanel.add(imageSourcesButton);
    } // end of method makeButtonPanel()

    /*
     * Creates the application frame and its content. 
     */
    private void makeFrame()
    {
        // Create the frame.
        frame = new JFrame("");
        frame.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        frame.getContentPane().setBackground(BACKGROUND_COLOUR);

        // Display the first image.
        loadWelcomeImage();
        frame.add(welcomeImage,BorderLayout.CENTER);

        // Welcome the user.
        label = 
        new JLabel("Welcome to the English/French Translation game.");
        frame.add(label, BorderLayout.PAGE_START);

        // Make the button panel.
        makeButtonPanel();

        frame.pack();
        frame.setVisible(true);
    } // end of method makeFrame()

    /*
     * Initializes the images which provide 
     * a graphic depiction of the sentence pairs. 
     */
    private void loadGameImages()
    {
        String[] imageName = new String[MAXIMUM_NUMBER_OF_SENTENCES];

        // Initialize images to be used in the game.
        for (int i = 0; i < MAXIMUM_NUMBER_OF_SENTENCES; i++)
        {
            // Ensure file with all images exists. 
            try
            {
                imageName[i] = IMAGE_INPUT_FILE.readLine();
                gameImage[i] = new ImageComponent(imageName[i]);
            }
            catch (Exception exception)
            {
                // Display error message. 
                System.out.println("File not found.");
            } // end of catch (Exception exception)
        } // end of for (int i = 0...)
    } // end of method loadGameImages()

    /*
     * Creates the image the user sees at the beginning of the game.
     */
    private void loadWelcomeImage()
    {
        welcomeImage = new ImageComponent(WELCOME_IMAGE);
        currentImage = welcomeImage;
    } // end of method loadImageData()

    /*
     * Gathers the goal from the user. 
     */
    private void gatherGoal() throws IOException
    {
        // Prompt user for input.
        input = JOptionPane.showInputDialog("How many sentences "
            + "do you want to translate? " 
            + MINIMUM_NUMBER_OF_SENTENCES + "-" 
            + MAXIMUM_NUMBER_OF_SENTENCES + ": ");

        numberTranslated = 0;
        boolean inputValid = false;

        do
        {

            /* Ensures goal entered is in 
            correct range of sentences to translate. */
            try
            {
                if (Integer.parseInt(input) < MINIMUM_NUMBER_OF_SENTENCES 
                || Integer.parseInt(input) > MAXIMUM_NUMBER_OF_SENTENCES)
                {
                    throw new NumberFormatException ("Not valid");
                }
                else
                {
                    goal = Integer.parseInt(input);
                } // end of if (Integer.parseInt(input)...)
                inputValid = true;
            }
            catch (NumberFormatException exception)
            {
                // Display error messages if necessary.
                JOptionPane.showMessageDialog(null, input + 
                    " is not a valid integer from " 
                    + MINIMUM_NUMBER_OF_SENTENCES + "-" 
                    + MAXIMUM_NUMBER_OF_SENTENCES
                    + ".", "Error", JOptionPane.ERROR_MESSAGE);

                input = 
                JOptionPane.showInputDialog("Enter an integer from " 
                    + MINIMUM_NUMBER_OF_SENTENCES + "-" 
                    + MAXIMUM_NUMBER_OF_SENTENCES + ": ");
            } // end of catch (NumberFormatexception exception)
        }
        while (!inputValid);

        // Tell user to begin translating.
        label.setText("Please translate the following sentences."
            + "\n Enter " + SENTINEL_VALUE 
            + " or translate the number of sentences you specified.");
    } // end of method gatherGoal() throws IOException

    /*
     * Changes the image displayed.
     */
    private void changeImage(int currentSentenceIndex)
    {
        // Update the displayed image.
        BorderLayout layout = 
            (BorderLayout)(frame.getContentPane()).getLayout();
        Component component = 
            layout.getLayoutComponent(BorderLayout.CENTER);
        int imageIndex = currentSentenceIndex - 1;
        frame.remove(currentImage);
        frame.add(gameImage[imageIndex]);
        currentImage = gameImage[imageIndex];

        frame.pack();
        frame.setVisible(true);
        frame.repaint();
    } // end of method changeImage(int currentSentenceIndex)

    /*
     * Gets the current sentence.
     */
    private void generateSentence()
    {
        do
        {
            currentSentenceIndex = 
            randomGenerator.nextInt(MAXIMUM_NUMBER_OF_SENTENCES) + 1;
            currentSentence = sentence.get(currentSentenceIndex);
        } 
        while (currentSentence.isCorrectlyTranslated());
    } // end of method generateSentence()

    /*
     * Determines whether the sentence to translate will be 
     * displayed in English or French.
     */
    private void getInput()
    {
        if (randomGenerator.nextBoolean())
        {
            // Get french input.
            input = 
            JOptionPane.showInputDialog(currentSentence.getEnglish());
            isEnglish = true;
        } 
        else
        {
            // Get English input.
            input = 
            JOptionPane.showInputDialog(currentSentence.getFrench());
            isEnglish = false;
        } // end of if (randomGenerator.nextBoolean())
    } // end of method getInput()

    /* 
     * Ensures user translates sentence in opposite language 
     * from the language displayed.
     */
    private void processInput()
    {
        if (isEnglish)
        {
            // Ensure input is in French.
            if (input.trim().equals(currentSentence.getFrench()))
            {
                numberTranslated = numberTranslated + 1;
                currentSentence.setTranslated(true);
                label.setText("Correct");
            }
            else if (!input.equals(SENTINEL_VALUE))
            {
                label.setText("Incorrect");
            } 
            else
            {
                gameFinished = true;
            } // end of if (input.trim()...)
        }
        else
        {
            // Ensure input is in English.
            if (input.trim().equals(currentSentence.getEnglish()))
            {
                numberTranslated = numberTranslated + 1;
                currentSentence.setTranslated(true);
                label.setText("Correct");
            }
            else if (!input.equals(SENTINEL_VALUE))
            {
                label.setText("Incorrect");
            } 
            else
            {
                gameFinished = true;
            } // end of input.trim()...)
        } // end of if (isEnglish)
    } // end of method processInput()

    /*
     * Wraps up this translation game. 
     */
    private void finishGame() throws IOException
    {
        /* Gives the user the option to reset 
        the most recent score and high score. */
        JOptionPane.showMessageDialog(null, 
            "Reset the scores if you would like. " 
            + "Then, restart or quit game.", "Good work!", 
            JOptionPane.INFORMATION_MESSAGE);

        // Close connections to the following text files. 
        try
        {
            ENGLISH_INPUT_FILE.close();
            FRENCH_INPUT_FILE.close();
            IMAGE_INPUT_FILE.close();
        }
        catch (Exception exception)
        {
            System.out.println("File(s) cannot be closed.");
        } // end of catch (Exception exception)
    } // end of method finishGame() throws IOException

    /*
     * Resets the value of whether 
     * each sentence pair is correctly translated.
     */
    private void resetWhetherTranslated()
    {
        for (int i = 0; i < MAXIMUM_NUMBER_OF_SENTENCES; i++)
        {
            sentencePair[i].setTranslated(false);
        } // end of for (i = 0...)
    } // end of resetWhetherTranslated()

    /*
     * Processes and displays the scores of this translation game.
     */
    private void displayScores() throws IOException
    {

        JOptionPane.showMessageDialog(null, "You translated " 
            + numberTranslated + " sentence(s).", 
            "Good work!", JOptionPane.INFORMATION_MESSAGE);

        int mostRecentScore = 0;

        // Establish a input connection to the score file.
        try
        {
            scoreInputFile = 
            new BufferedReader(new FileReader(SCORE_FILE));
        }
        catch (Exception exception)
        {
            System.out.println("File not found.");
        } // end of catch (Exception exception)

        // Get the user's previous score from the score text file. 
        String lineOfText = scoreInputFile.readLine();

        while (lineOfText != null)
        {
            mostRecentScore = Integer.parseInt(lineOfText);
            lineOfText = scoreInputFile.readLine();
        } // end of while (!scoreInputFile.readLine() != null)

        JOptionPane.showMessageDialog(null, "Most previous score: " 
            + mostRecentScore, "Good work!", 
            JOptionPane.INFORMATION_MESSAGE);

        int highestScore = 0;

        // Determine the user's highest score so far. 
        if (numberTranslated > highestScore)
        {
            highestScore = numberTranslated;
        } // end of if (numberTranslated > highestScore)

        if (mostRecentScore > highestScore)
        {
            highestScore = mostRecentScore;
        } // end of if (mostRecentScore > highestScore)

        // Close input to score file to allow output to the same file. 
        scoreInputFile.close();

        JOptionPane.showMessageDialog(null, "High score in this game: " 
            + highestScore, "Good work!", 
            JOptionPane.INFORMATION_MESSAGE);

        // Establish an output connection to the score file.  
        try
        {
            scoreOutputFile = 
            new PrintWriter(new FileWriter(SCORE_FILE));
        }
        catch (Exception exception)
        {
            System.out.println("File not found.");
        } // end of catch (Exception exception)

        // Update the user's score.
        scoreOutputFile.println(numberTranslated);

        // Close output connections to the score file.
        scoreOutputFile.close();
    } // end of method displayScores() throws IOException

    /**
     * Launches this translation game.
     * 
     * @param argument not used
     */
    public static void main(String[] argument) throws IOException
    {
        TranslationGameGUI game = new TranslationGameGUI();
    } // end of method main(String[] argument) throws IOException

    /* private classes */

    /*
     * A listener which can be registered by an event source and which
     * can receive event objects.
     */
    private class ButtonListener implements ActionListener 
    {
        /*
         * Responds to button events.
         */
        public void actionPerformed(ActionEvent event)
        {
            Object source = event.getSource();

            if (source == startButton)
            {
                try
                {
                    runGame();
                }
                catch (Exception exception)
                {
                    System.out.println("Game unable to start.");
                } // end of catch (Exception exception)
            }
            else if (source == resetScoresButton)
            {
                // Establish an output connection to the score file.  
                try
                {
                    scoreOutputFile = 
                    new PrintWriter(new FileWriter(SCORE_FILE));
                }
                catch (Exception exception)
                {
                    System.out.println("File not found.");
                } // end of catch (Exception exception)

                // Reset the most previous score.
                scoreOutputFile.println("0");
                label.setText("The scores will be reset " 
                    + "the next time the game runs.");

                // Close output connections to the score file.
                scoreOutputFile.close();
            }
            else if (source == imageSourcesButton)
            {
                String lineOfText = "";
                String imageSources = "";
                try
                {
                    // Gather image sources from the text file.
                    lineOfText = IMAGE_SOURCE_INPUT_FILE.readLine();
                }
                catch (Exception exception)
                {
                    System.out.println("Error reading from file.");
                } // end of (Exception exception)

                // Continue gathering image sources from the text file.
                while (lineOfText != null)
                {
                    imageSources = "<html>" + imageSources + "<br/>" 
                    + lineOfText + "<html>";
                    try
                    {
                        lineOfText = IMAGE_SOURCE_INPUT_FILE.readLine();
                    }
                    catch (Exception exception)
                    {
                        System.out.println("Error reading from file.");
                    } // end of catch (Exception exception)
                } // end of while (lineOfText != null)

                // Display image sources.
                label.setText(imageSources);

            }
            else if (source == quitButton)
            {
                // Establish an output connection to the score file.  
                try
                {
                    scoreOutputFile = 
                    new PrintWriter(new FileWriter(SCORE_FILE));
                }
                catch (Exception exception)
                {
                    System.out.println("File not found.");
                } // end of catch (Exception exception)

                // Reset the score.
                scoreOutputFile.println("0");

                // Close output connections to the score file.
                scoreOutputFile.close();

                System.exit(0);
            } // end of if (source == startButton)
        } // end of method actionPerformed(ActionEvent event)
    } // end of class ButtonListener

    /*
     * A component with a drawn image.
     */
    private class ImageComponent extends Component
    {
        // class fields
        private static final int NO_PROBLEMS_ENCOUNTERED = 0;
        private static final int PROBLEMS_ENCOUNTERED = -1;

        // instance fields
        private BufferedImage bufferedImage;
        private int status;

        /* constructors */

        /*
         * Creates a component with a drawn image. If the image was
         * drawn, the component's status is NO_PROBLEMS_ENCOUNTERED;
         * otherwise, PROBLEMS_ENCOUNTERED.
         */
        public ImageComponent(String fileName)
        {
            bufferedImage = null;
            status = NO_PROBLEMS_ENCOUNTERED;
            try
            {
                bufferedImage = ImageIO.read(new File(fileName));
            }
            catch (IOException exception)
            {
                status = PROBLEMS_ENCOUNTERED;
            } // end of catch (IOException exception)
        } // end of constructor ImageComponent(String fileName)

        /* accessors */

        /*
         * Returns the status of this component: NO_PROBLEMS_ENCOUNTERED
         * or PROBLEMS_ENCOUNTERED.
         */
        public int getStatus()
        {
            return status;
        } // end of method getStatus()

        /*
         * Returns a string representation of this component.
         * 
         * @return a string representing this component
         */
        public String toString()
        {
            return
            getClass().getName()
            + "["
            + "buffered image: " + bufferedImage
            + ", status: " + status
            + "]";
        } // end of method toString()

        /* mutators */

        /*
         * Called when the contents of the component should be painted, 
         * such as when the component is first being shown or is 
         * damaged and in need of repair.
         */
        public void paint(Graphics graphicsContext)
        {
            graphicsContext.drawImage(bufferedImage, 0, 0, null);
        } // end of method paint(Graphics graphicsContext)
    } // end of ImageComeponent extends Component
} // end of TranslationGameGUI
