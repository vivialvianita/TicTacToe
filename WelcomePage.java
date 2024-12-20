/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group #1
 * 1 - 5026221161 - Ryan Adi Putra Pratama
 * 2 - 5026231185 - Jannati Urfa Muhayat
 * 3 - 5026231226 - Vivi Alvianita
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.sound.sampled.*;
import java.io.File;

public class WelcomePage extends JPanel {
    private static final long serialVersionUID = 1L;
    private Clip backgroundMusic;
    private Image backgroundImage;


    public WelcomePage(JFrame parentFrame) {
        backgroundImage = new ImageIcon("images/jellyfishy.jpeg").getImage();
        playBackgroundMusic("audio/Spongebob_Squarepants_-_Ripped_Pants_with_lyrics_[YouConvert.net].wav");

        setLayout(null);

        JButton ticTacToeButton = new JButton("Play Tic-Tac-Toe");
        JButton connectFourButton = new JButton("Play Connect Four");

        styleButton(ticTacToeButton, "images/buttonttt.png", 200, 100);
        styleButton(connectFourButton, "images/buttoncf.png", 200, 100);

        // Tombol untuk permainan Tic-Tac-Toe
        ticTacToeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopBackgroundMusic();
                // Tampilkan dialog untuk memilih mode permainan
                showGameModeDialog(parentFrame);
            }
        });

        // Tombol untuk permainan Connect Four
        connectFourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopBackgroundMusic();
                parentFrame.getContentPane().removeAll();
                parentFrame.setContentPane(new ConnectFourGraphics());
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });

        add(ticTacToeButton);
        add(connectFourButton);

        parentFrame.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int frameWidth = parentFrame.getWidth();
                int frameHeight = parentFrame.getHeight();

                ticTacToeButton.setBounds(frameWidth / 2 - 100, frameHeight / 2 - 60, 200, 50);
                connectFourButton.setBounds(frameWidth / 2 - 100, frameHeight / 2 + 10, 200, 50);
                repaint();
            }
        });

        Dimension size = parentFrame.getSize();
        ticTacToeButton.setBounds(size.width / 2 - 150, size.height / 2 - 120, 300, 100);
        connectFourButton.setBounds(size.width / 2 - 150, size.height / 2 + 20, 300, 100);
    }

    private void showGameModeDialog(JFrame parentFrame) {
        String[] options = {"Human vs Human", "Human vs AI"};
        int choice = JOptionPane.showOptionDialog(parentFrame, "Choose Game Mode",
                "Game Mode", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);

        // Berdasarkan pilihan, buat halaman permainan dengan mode yang dipilih
        if (choice == 0) {
            // Mode Human vs Human
            GameMain gameMain = new GameMain(parentFrame);  // Menggunakan konstruktor dengan satu parameter JFrame
            gameMain.setGameMode("Human vs Human"); // Setel mode permainan
            parentFrame.getContentPane().removeAll();
            parentFrame.setContentPane(gameMain);
            parentFrame.revalidate();
        } else if (choice == 1) {
            // Mode Human vs AI
            GameMain gameMain = new GameMain(parentFrame);  // Menggunakan konstruktor dengan satu parameter JFrame
            gameMain.setGameMode("Human vs AI"); // Setel mode permainan
            parentFrame.getContentPane().removeAll();
            parentFrame.setContentPane(gameMain);
            parentFrame.revalidate();
        }
    }

    private ImageIcon getScaledIcon(String iconPath, int width, int height) {
        ImageIcon icon = new ImageIcon(iconPath);
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    private void styleButton(JButton button, String iconPath, int width, int height) {
        button.setIcon(getScaledIcon(iconPath, width, height)); // Sesuaikan ukuran ikon
        button.setText(""); // Hapus teks
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
    }

    private void playBackgroundMusic(String musicPath) {
        try {
            File musicFile = new File(musicPath);
            if (!musicFile.exists()) {
                throw new Exception("Music file not found: " + musicPath);
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicFile);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInputStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.err.println("Error playing background music: " + e.getMessage());
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
            backgroundMusic.close();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Welcome to Bikini Bottom");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new WelcomePage(frame));
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
