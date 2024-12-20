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

public class GameMain extends JPanel {
    
    private static final long serialVersionUID = 1L;
    private AIPlayer aiPlayer;
    private Timer aiTimer, humanTimer;  // Menambahkan deklarasi humanTimer
    private int aiTimerSeconds, humanTimerSeconds;
    private String gameMode;

    public static final String TITLE = "Tic Tac Toe";
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_CROSS = new Color(239, 105, 80);
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225);
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    private Board board;
    private State currentState;
    private Seed currentPlayer;
    private JLabel statusBar;
    private JButton backButton;
    private JFrame parentFrame;
    private boolean isAIForX = false; // Default: Pemain X dikendalikan oleh pengguna
    private boolean isAIForO = true;  // Default: Pemain O dikendalikan oleh AI

    private Image backgroundImage; // Deklarasi backgroundImage

    // Setter untuk gameMode
    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
        System.out.println("Game Mode set to: " + gameMode);
    }

    public GameMain(JFrame parentFrame) {
        this.parentFrame = parentFrame;

        // Load background image
        backgroundImage = new ImageIcon("images/jellyfishy.jpg").getImage(); // Ganti dengan path gambar background

        // Back Button
        backButton = new JButton("Back to Welcome Page");
        backButton.addActionListener(e -> {
            parentFrame.getContentPane().removeAll();
            parentFrame.setContentPane(new WelcomePage(parentFrame));
            parentFrame.revalidate();
        });

        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
        });

        // Status Bar
        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        JPanel buttonPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        topPanel.add(backButton); // Tombol kembali ke halaman utama
        buttonPanel.add(topPanel, BorderLayout.WEST);

        // Tambahkan panel pengaturan AI di sebelah kanan
        buttonPanel.add(createSettingsPanel(), BorderLayout.EAST);

        // Tambahkan buttonPanel ke bagian atas panel utama
        super.setLayout(new BorderLayout());
        super.add(buttonPanel, BorderLayout.PAGE_START);
        super.add(statusBar, BorderLayout.PAGE_END);
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        initGame();
        newGame();
    }

    private JPanel createSettingsPanel() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JCheckBox aiForXCheckbox = new JCheckBox("AI for X");
        aiForXCheckbox.setSelected(isAIForX);
        aiForXCheckbox.addItemListener(e -> isAIForX = aiForXCheckbox.isSelected());

        JCheckBox aiForOCheckbox = new JCheckBox("AI for O");
        aiForOCheckbox.setSelected(isAIForO);
        aiForOCheckbox.addItemListener(e -> isAIForO = aiForOCheckbox.isSelected());

        settingsPanel.add(aiForXCheckbox);
        settingsPanel.add(aiForOCheckbox);

        return settingsPanel;
    }

    private void initGame() {
        board = new Board();
        aiPlayer = new AIPlayer(Seed.NOUGHT);
    }

    private void newGame() {
        aiPlayer = new AIPlayer(Seed.NOUGHT);
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = Seed.NO_SEED;
            }
        }
        currentPlayer = Seed.CROSS;
        currentState = State.PLAYING;
        repaint();

        // Memulai timer untuk giliran pertama
        if (isAIForX) {
            performAIMove(); // Langkah otomatis jika AI untuk X
        }
    }
    private void startTimerForHumanTurn() {
        // Timer untuk giliran manusia
        humanTimerSeconds = 15;
        humanTimer = new Timer(1000, e -> {
            if (humanTimerSeconds > 0) {
                humanTimerSeconds--;
                statusBar.setText("Time left for " + (currentPlayer == Seed.CROSS ? "Spongebob" : "Patrick") + ": " + humanTimerSeconds + "s");
            } else {
                humanTimer.stop();
                // Pindah ke giliran AI setelah waktu habis
                if (currentPlayer == Seed.CROSS) {
                    currentPlayer = Seed.NOUGHT;
                    performAIMove();
                } else {
                    currentPlayer = Seed.CROSS;
                }
                repaint();
                startTimerForHumanTurn(); // Mulai lagi timer untuk giliran berikutnya
            }
        });
        humanTimer.start();
    }

    private void startAITimer() {
        // Timer untuk giliran AI (3 detik setelah giliran manusia)
        aiTimerSeconds = 3;
        aiTimer = new Timer(1000, e -> {
            if (aiTimerSeconds > 0) {
                aiTimerSeconds--;
                statusBar.setText("AI is thinking... " + aiTimerSeconds + "s");
            } else {
                aiTimer.stop();
                performAIMove(); // AI melakukan langkah setelah 3 detik
            }
        });
        aiTimer.start();
    }


    private void handleMouseClick(MouseEvent e) {
        if (currentState != State.PLAYING) {
            stopTimers(); // Hentikan timer saat permainan selesai
            newGame(); // Jika permainan selesai, mulai ulang
            repaint(); // Perbarui UI setelah reset
            return;
        }

        // Hitung offset dari pusat
        int boardWidth = Board.CANVAS_WIDTH;
        int boardHeight = Board.CANVAS_HEIGHT;
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int offsetX = (panelWidth - boardWidth) / 2;
        int offsetY = (panelHeight - boardHeight) / 2;

        // Hitung posisi klik relatif terhadap papan permainan
        int mouseX = e.getX() - offsetX;
        int mouseY = e.getY() - offsetY;

        // Validasi apakah klik berada dalam batas papan permainan
        if (mouseX >= 0 && mouseX < boardWidth && mouseY >= 0 && mouseY < boardHeight) {
            int row = mouseY / Cell.SIZE;
            int col = mouseX / Cell.SIZE;

            if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                    && board.cells[row][col].content == Seed.NO_SEED) {

                // Pemain manusia membuat langkah
                currentState = board.stepGame(currentPlayer, row, col);
                currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                repaint();

                // Jika ada pemenang atau hasil seri, otomatis reset game
                if (currentState != State.PLAYING) {
                    stopTimers(); // Hentikan timer
                    // Setel permainan baru setelah beberapa detik (opsional)
                    Timer resetTimer = new Timer(0500, e1 -> {
                        newGame();  // Reset game setelah 2 detik
                        repaint();  // Update tampilan UI
                    });
                    resetTimer.setRepeats(false);  // Hanya sekali
                    resetTimer.start();
                }
            }
        }// Setelah manusia bermain, mulai timer untuk giliran AI
        if (currentState == State.PLAYING && currentPlayer == Seed.NOUGHT && isAIForO) {
            startAITimer();
        } else if (currentState == State.PLAYING && currentPlayer == Seed.CROSS && isAIForX) {
            startAITimer();

        }

    }

    private void stopTimers() {
        if (humanTimer != null) {
            humanTimer.stop();
        }
        if (aiTimer != null) {
            aiTimer.stop();
        }
    }

    private void performAIMove() {
        // Proses langkah AI
        int[] aiMove = aiPlayer.getBestMove(board);
        if (aiMove != null) {
            currentState = board.stepGame(currentPlayer, aiMove[0], aiMove[1]);
            repaint();
            currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
            startTimerForHumanTurn(); // Mulai timer untuk giliran manusia berikutnya
        }
    }



    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Menggambar background
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // Hitung offset untuk menempatkan board di tengah
        int boardWidth = Board.CANVAS_WIDTH;
        int boardHeight = Board.CANVAS_HEIGHT;
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Offset untuk memposisikan di tengah
        int offsetX = (panelWidth - boardWidth) / 2;
        int offsetY = (panelHeight - boardHeight) / 2;

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(offsetX, offsetY); // Geser koordinat gambar
        board.paint(g2d); // Gambar board dengan offset
        g2d.translate(-offsetX, -offsetY); // Kembalikan ke posisi semula jika diperlukan

        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            statusBar.setText((currentPlayer == Seed.CROSS) ? "Spongebob's Turn" : "Patrick's Turn");
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'Spongebob' Won! Click to play again.");
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'Patrick' Won! Click to play again.");
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(TITLE);
            frame.setContentPane(new WelcomePage(frame));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
