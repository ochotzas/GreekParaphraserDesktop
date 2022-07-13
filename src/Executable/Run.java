package Executable;

import Design.HomeWindow;

public class Run
{
    public static void main(String[] args)
    {
        HomeWindow dialog = new HomeWindow();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);

    }
}
