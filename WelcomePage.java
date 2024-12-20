import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.sound.sampled.*;
import java.io.File;

/**
 * WelcomePage class - Displays the welcome screen with background image,
 * background music, and options to select Tic-Tac-Toe or Connect Four.
 */
public class WelcomePage extends JPanel {
    private static final long serialVersionUID = 1L;
    private Clip backgroundMusic; // Clip for managing background music
    private Image backgroundImage; // Dynamically scaled background image

    /** Constructor to set up the welcome screen UI */
    public WelcomePage(JFrame parentFrame) {
        // Load and scale the background image
        backgroundImage = new ImageIcon("images/jellyfishy.jpeg").getImage();

        // Play background music
        playBackgroundMusic("audio/Spongebob_Squarepants_-_Ripped_Pants_with_lyrics_[YouConvert.net].wav");

        // Set layout for the panel
        setLayout(null);

        // Create buttons for game selection
        JButton ticTacToeButton = new JButton("Play Tic-Tac-Toe");
        JButton connectFourButton = new JButton("Play Connect Four");

        // Style the buttons
        styleButton(ticTacToeButton);
        styleButton(connectFourButton);

        // Add action listeners to the buttons
        ticTacToeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopBackgroundMusic(); // Stop music before switching to the game
                parentFrame.getContentPane().removeAll(); // Remove welcome page
                parentFrame.setContentPane(new GameMain(parentFrame)); // Switch to Tic-Tac-Toe game
                parentFrame.revalidate(); // Refresh the frame
            }
        });

        connectFourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopBackgroundMusic(); // Stop musik sebelum beralih ke permainan
                parentFrame.getContentPane().removeAll(); // Hapus tampilan welcome page
                parentFrame.setContentPane(new ConnectFourGraphics()); // Ganti tampilan dengan game Connect Four
                parentFrame.revalidate(); // Refresh frame
                parentFrame.repaint();    // Repaint untuk memastikan board game tampil
            }
        });

        // Add buttons dynamically based on frame size
        add(ticTacToeButton);
        add(connectFourButton);

        // Set bounds and resize behavior
        parentFrame.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int frameWidth = parentFrame.getWidth();
                int frameHeight = parentFrame.getHeight();

                ticTacToeButton.setBounds(frameWidth / 2 - 100, frameHeight / 2 - 60, 200, 50);
                connectFourButton.setBounds(frameWidth / 2 - 100, frameHeight / 2 + 10, 200, 50);
                repaint(); // Repaint the panel to adjust the background
            }
        });

        // Initial positioning
        Dimension size = parentFrame.getSize();
        ticTacToeButton.setBounds(size.width / 2 - 100, size.height / 2 - 60, 200, 50);
        connectFourButton.setBounds(size.width / 2 - 100, size.height / 2 + 10, 200, 50);
    }

    /**
     * Styles a JButton with consistent design.
     *
     * @param button JButton to style.
     */
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
    }

    /**
     * Plays background music in a loop.
     *
     * @param musicPath Path to the music file.
     */
    private void playBackgroundMusic(String musicPath) {
        try {
            File musicFile = new File(musicPath);
            if (!musicFile.exists()) {
                throw new Exception("Music file not found: " + musicPath);
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicFile);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInputStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY); // Play the music in a loop
        } catch (Exception e) {
            System.err.println("Error playing background music: " + e.getMessage());
        }
    }

    /**
     * Stops the background music.
     */
    private void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
            backgroundMusic.close();
        }
    }

    /**
     * Paints the background image dynamically.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    /**
     * Main method to test WelcomePage.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Welcome to Bikini Bottom");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new WelcomePage(frame));
            frame.setSize(800, 600); // Set preferred size of the window
            frame.setLocationRelativeTo(null); // Center the window on the screen
            frame.setVisible(true); // Show the window
        });
    }
}
