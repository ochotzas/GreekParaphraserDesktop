package Design;

import Enums.State;

import javax.swing.*;
import java.awt.*;

public class StateHandler
{
    public static void setState(JLabel label, State state)
    {
        switch (state)
        {
            case SUCCESS -> label.setForeground(Color.green);
            case FAILURE -> label.setForeground(Color.red);
            case WARNING -> label.setForeground(Color.orange);
            case INFO -> label.setForeground(Color.blue);
            case NEUTRAL -> label.setForeground(Color.black);
        }
    }
}
