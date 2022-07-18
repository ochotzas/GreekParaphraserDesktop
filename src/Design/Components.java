package Design;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

/**
 * The Components class contains the design of the components used.
 */
public class Components
{
    /**
     * The Components class contains the design of the components JTextPanel.
     */
    public static void JTextPaneDesign(JTextPane textPane)
    {
        textPane.setBorder(BorderFactory.createEmptyBorder());
        textPane.setOpaque(false);
        textPane.setBackground(new Color(0, 0, 0, 0));
        textPane.setFont(new Font("Arial", Font.PLAIN, 14));
        textPane.setCaretColor(Color.BLACK);
        textPane.setCaretPosition(textPane.getText().length());
        DefaultCaret caret = (DefaultCaret) textPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        textPane.setSelectionColor(Color.lightGray);
    }

    /**
     * The Components class contains the design of the components JButton.
     */
    public static void JButtonDesign(JButton button)
    {
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
    }

    /**
     * The Components class contains the design of the components JPanel.
     */
    public static void JPanelDesign(JPanel panel)
    {
        panel.setOpaque(false);
    }

    /**
     * The Components class contains the design of the components JLabel.
     */
    public static void JLabelDesign(JLabel label)
    {
        label.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    /**
     * The Components class contains the design of the components JMenuBar.
     */
    public static void JMenuBarDesign(JMenuBar menuBar)
    {
        menuBar.setOpaque(false);
        menuBar.setBorder(BorderFactory.createEmptyBorder());
    }

    /**
     * The Components class contains the design of the components JMenuBar.
     */
    public static void JMenuDesign(JMenuBar menu)
    {
        menu.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }
}
