package Design;

import Actions.ActionRequest;
import Enums.Mode;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.nio.file.Files;
import java.util.Base64;
import java.util.EmptyStackException;
import java.util.Random;
import java.util.Stack;

public class HomeWindow extends JDialog
{
    final Mode mode = Mode.USER; // change this to {"Mode.DEVELOPER"} to skip loading screen
    final int charLimit = 1000;
    JPanel mainPanel;
    JLabel helpMessage;
    JLabel writtenCharsMsg;
    JMenuBar menuBar = new JMenuBar();
    JMenuItem paraphrase = new JMenuItem("Παράφραση");
    JMenuItem correct = new JMenuItem("Διόρθωση");
    JMenuItem save = new JMenuItem("Αποθήκευση");
    JMenuItem load = new JMenuItem("Άνοιγμα");
    JMenuItem clear = new JMenuItem("Καθαρισμός");
    Stack<String> historyStack = new Stack<>();
    private JPanel contentPane;
    private JTextPane userTextPanel;
    private JTextPane paraphrasedTextPanel;
    private JButton paraphraseBtn;
    private boolean isParaphrasing;

    public HomeWindow()
    {
        setIconImage(new ImageIcon("icons/comment.png").getImage());
        setTitle("Παραφραστής Ελληνικών κειμένων");

        setResizable(false);
        loadingScreen();
        menu();

        setContentPane(contentPane);
        isParaphrasing = false;
        setModal(true);

        // when ctl+z is pressed, undo the last action
        userTextPanel.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_Z && e.isControlDown()) undo();
            }
        });

        // load backup
        loadBackup();

        charLimitHandler();
        helpMessage.setText("Πατήστε το κουμπί για παράφραση");
        writtenCharsMsg.setText("Χαρακτήρες " + userTextPanel.getText().length() + " / " + charLimit);
        writtenCharsMsg.setIcon(new ImageIcon("icons/hashtag.png"));

        paraphraseBtn.setEnabled(!userTextPanel.getText().isEmpty() && paraphrasedTextPanel.getText().isEmpty());
        paraphrase.setEnabled(!userTextPanel.getText().isEmpty() && paraphrasedTextPanel.getText().isEmpty());
        correct.setEnabled(!userTextPanel.getText().isEmpty() && paraphrasedTextPanel.getText().isEmpty());
        save.setEnabled(!userTextPanel.getText().isEmpty() && paraphrasedTextPanel.getText().isEmpty());
        clear.setEnabled(!userTextPanel.getText().isEmpty() || !paraphrasedTextPanel.getText().isEmpty());

        paraphraseBtn.addActionListener(e -> onClickAction(Enums.Action.PARAPHRASE));

        // When any key is pressed, check if the user has reached the character limit.
        userTextPanel.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e)
            {
                charLimitHandler();
                writtenCharsMsg.setText("Χαρακτήρες " + userTextPanel.getText().length() + " / " + charLimit);
                paraphraseBtn.setEnabled(!userTextPanel.getText().isEmpty());
                paraphrase.setEnabled(!userTextPanel.getText().isEmpty());
                correct.setEnabled(!userTextPanel.getText().isEmpty());
                save.setEnabled(!userTextPanel.getText().isEmpty());
                clear.setEnabled(userTextPanel.getText().isEmpty() || paraphrasedTextPanel.getText().isEmpty());
                helpMessage.setText("Πατήστε το κουμπί για παράφραση");
                historyStack.push(userTextPanel.getText());
            }
        });

        // When termination of the program save the backup file
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent)
            {
                backUp();
            }
        });

    }

    /**
     * This method is called when the user clicks on the "Παράφραση" or "Διόρθωση" button.
     *
     * @param action the action that is performed.
     */
    private void onClickAction(Enums.Action action)
    {
        helpMessage.setText(!isParaphrasing ? "Επεξεργαζόμαστε το κείμενο σας, μια στιγμή" : helpMessage.getText());
        paraphraseBtn.setEnabled(false);
        userTextPanel.setEnabled(false);
        paraphrase.setEnabled(false);
        correct.setEnabled(false);
        save.setEnabled(false);
        clear.setEnabled(false);
        load.setEnabled(false);
        paraphrasedTextPanel.setText("");
        new Thread(() ->
        {
            try
            {
                isParaphrasing = true;
                String text = userTextPanel.getText();
                if (Enums.Action.PARAPHRASE.equals(action))
                {
                    String paraphrasedText = ActionRequest.paraphrase(text);
                    paraphrasedTextPanel.setText(paraphrasedText);
                    helpMessage.setText(ActionRequest.getStatusCode() == 200 ? "Το κείμενο σας παραφράστηκε με επιτυχία" : "Κάτι πήγε στραβά");
                } else if (Enums.Action.CORRECT.equals(action))
                {
                    String correctedText = ActionRequest.correct(text);
                    paraphrasedTextPanel.setText(correctedText);
                    helpMessage.setText(ActionRequest.getStatusCode() == 200 ? "Το κείμενο σας διορθώθηκε με επιτυχία" : "Κάτι πήγε στραβά");
                }
            } catch (IOException | InterruptedException ex)
            {
                helpMessage.setText("Υπήρξε κάποιο πρόβλημα κατά την διόρθωση του κειμένου");
                throw new RuntimeException(ex);
            } finally
            {
                save.setEnabled(true);
                clear.setEnabled(true);
                load.setEnabled(true);
                userTextPanel.setEnabled(true);
                isParaphrasing = false;
            }
        }).start();
    }

    /**
     * Handles the character limit of the text area.
     */
    private void charLimitHandler()
    {
        if (userTextPanel.getText().length() >= charLimit)
        {
            userTextPanel.setText(userTextPanel.getText().substring(0, charLimit));
            helpMessage.setText("Το μέγιστο από χαρακτήρες που μπορείτε να εισάγετε είναι " + charLimit);
        } else if (userTextPanel.getText().length() < charLimit)
        {
            helpMessage.setText("Πατήστε το κουμπί για παράφραση");
        }
    }

    /**
     * This method is called from within the constructor to initialize menu items.
     */
    private void menu()
    {
        JMenu file = new JMenu("Αρχείο");
        JMenu actions = new JMenu("Ενέργειες");

        paraphrase.addActionListener(e -> onClickAction(Enums.Action.PARAPHRASE));
        correct.addActionListener(e -> onClickAction(Enums.Action.CORRECT));

        save.addActionListener(e -> onClickSave());
        load.addActionListener(e -> onClickLoad());
        clear.addActionListener(e -> onClickClear());

        paraphrase.setIcon(new ImageIcon("icons/magic-wand.png"));
        correct.setIcon(new ImageIcon("icons/pencil.png"));
        save.setIcon(new ImageIcon("icons/disk.png"));
        load.setIcon(new ImageIcon("icons/search-alt.png"));
        clear.setIcon(new ImageIcon("icons/trash.png"));

        file.add(load);
        file.add(save);
        actions.add(correct);
        actions.add(paraphrase);
        actions.add(clear);

        paraphrase.setEnabled(false);
        correct.setEnabled(false);
        save.setEnabled(false);

        menuBar.add(file);
        menuBar.add(actions);
        setJMenuBar(menuBar);
    }

    /**
     * This method is called from within the constructor to initialize the content pane.
     */
    private void onClickClear()
    {
        userTextPanel.setText("");
        paraphrasedTextPanel.setText("");
        helpMessage.setText("Πατήστε το κουμπί για παράφραση");
        paraphraseBtn.setEnabled(false);
        paraphrase.setEnabled(false);
        correct.setEnabled(false);
        save.setEnabled(false);
        charLimitHandler();

        writtenCharsMsg.setText("Χαρακτήρες " + userTextPanel.getText().length() + " / " + charLimit);
    }

    /**
     * Save the actual and paraphrased text to a file using encoding Base64.
     */
    private void onClickSave()
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Paraphrased Text File (*.ptf)", "ptf"));
        fileChooser.setSelectedFile(new File("paraphrasedText.ptf"));
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            File selectedFile = fileChooser.getSelectedFile();
            String text = encode("## Αρχικό κείμενο:\n" + userTextPanel.getText() + "\n## Παραφρασμένο κείμενο:\n" + paraphrasedTextPanel.getText());
            try
            {
                FileWriter writer = new FileWriter(selectedFile);
                writer.write(text);
                writer.close();

                helpMessage.setText("Το κείμενο αποθηκεύτηκε με επιτυχία");
            } catch (IOException ex)
            {
                helpMessage.setText("Υπήρξε κάποιο πρόβλημα κατά την αποθήκευση του κειμένου");
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Load a file and decode it using encoding Base64.
     */
    private void onClickLoad()
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Paraphrased Text File (*.ptf)", "ptf"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            File selectedFile = fileChooser.getSelectedFile();
            try
            {
                String text = decode(Files.readString(selectedFile.toPath()));
                userTextPanel.setText(text.substring(text.indexOf("## Αρχικό κείμενο:\n") + 19, text.indexOf("## Παραφρασμένο κείμενο:\n")));
                paraphrasedTextPanel.setText(text.substring(text.indexOf("## Παραφρασμένο κείμενο:\n") + 25));
                helpMessage.setText("Το αρχείο ανοίχτηκε με επιτυχία");
            } catch (IOException | StringIndexOutOfBoundsException ex)
            {
                if (ex instanceof StringIndexOutOfBoundsException)
                    helpMessage.setText("Το αρχείο δεν είναι έγκυρο, έχει γίνει κάποια τροποποίηση");
                else helpMessage.setText("Υπήρξε κάποιο πρόβλημα κατά το άνοιγμα του αρχείου");
            }
        }
    }

    /**
     * Encode the text using encoding Base64.
     *
     * @param text The text to encode.
     * @return The encoded text.
     */
    private String encode(String text)
    {
        byte[] textBytes = text.getBytes();
        byte[] encodedBytes = Base64.getEncoder().encode(textBytes);
        return new String(encodedBytes);
    }

    /**
     * Decode the text using encoding Base64.
     *
     * @param text The text to decode.
     * @return The decoded text.
     */
    private String decode(String text)
    {
        byte[] textBytes = text.getBytes();
        byte[] decodedBytes = Base64.getDecoder().decode(textBytes);
        return new String(decodedBytes);
    }

    /**
     * Back-up last user use.
     */
    private void backUp()
    {
        File dir = new File(".hidden");
        if (!dir.exists()) dir.mkdir();

        File backupFile = new File(".hidden/backup.ser");

        if (backupFile.exists()) backupFile.delete();

        try
        {
            backupFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(backupFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(userTextPanel.getText());
            oos.writeObject(paraphrasedTextPanel.getText());
            oos.close();
        } catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Restore last user use.
     */
    private void loadBackup()
    {
        try
        {
            File backupFile = new File(".hidden/backup.ser");

            if (!backupFile.exists()) return;

            FileInputStream fileIn = new FileInputStream(".hidden/backup.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            userTextPanel.setText((String) in.readObject());
            paraphrasedTextPanel.setText((String) in.readObject());
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException ex)
        {
            helpMessage.setText("Αδυναμία ανάκτησης αντιγράφου ασφαλείας");
        }
    }

    /**
     * Show loading screen.
     */
    private void loadingScreen()
    {
        JFrame loadingFrame = new JFrame("Παρακαλώ περιμένετε...");
        loadingFrame.setContentPane(new JLabel(new ImageIcon("icons/loading.gif")));
        loadingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loadingFrame.setSize(420, 300);
        loadingFrame.setLocationRelativeTo(null);
        loadingFrame.setResizable(false);
        loadingFrame.setUndecorated(true);
        loadingFrame.setShape(new RoundRectangle2D.Double(0, 0, loadingFrame.getWidth(), loadingFrame.getHeight(), 20, 20));
        loadingFrame.setVisible(true);
        loadingFrame.setLayout(new FlowLayout());
        JLabel loadingLabel = new JLabel("Μια στιγμή, ετοιμάζουμε όλα τα απαραίτητα...");
        loadingLabel.setFont(new Font("Arial", Font.ROMAN_BASELINE, 15));
        loadingFrame.add(loadingLabel);
        loadingFrame.setVisible(true);
        try
        {
            if (mode == Mode.DEVELOPER)
            {
                loadingFrame.setVisible(false);
                return;
            }

            Thread.sleep(new Random().nextInt(8000) + 1000);
            loadingLabel.setText("Φόρτωση δεδομένων...");
            Thread.sleep(new Random().nextInt(8000) + 1000);
            loadingLabel.setText("Έλεγχος αναλύσεων...");
            Thread.sleep(new Random().nextInt(8000) + 1000);
            loadingLabel.setText("Φόρτωση τελευταίων ενεργειών...");
            Thread.sleep(new Random().nextInt(8000) + 1000);
            loadingLabel.setText("Σχεδόν έτοιμο...");
            Thread.sleep(new Random().nextInt(8000) + 1000);
        } catch (InterruptedException ex)
        {
            throw new RuntimeException(ex);
        }
        loadingFrame.dispose();
    }

    /**
     * Undo last action method.
     */
    private void undo()
    {
        try
        {
            if (historyStack.isEmpty()) return;
            String lastChange = historyStack.pop();
            if (lastChange.equals("user"))

                userTextPanel.setText(historyStack.pop());
            else
                userTextPanel.setText(historyStack.pop());
        } catch (EmptyStackException ignored) {}
    }
}
