package Files;

import javax.swing.*;

public class Main {
    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(() -> 
        {
            JFrame frame = new JFrame("Juegardo");
            GameManager gamePanel = new GameManager();
            
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(gamePanel);
            frame.pack(); // Ajusta el tamaño al preferido del panel
            frame.setLocationRelativeTo(null); // Centra la ventana
            frame.setVisible(true);
        });
    }
}
