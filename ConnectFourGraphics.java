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
import java.net.URL;

/**
 * Tic-Tac-Toe: Two-player Graphics version with Simple-OO in one class
 */
public class ConnectFourGraphics extends JFrame {
    private static final long serialVersionUID = 1L; // to prevent serializable warning

    // Define named constants for the game board
    public static final int ROWS = 6;  // ROWS x COLS cells
    public static final int COLS = 7;

    // Define named constants for the drawing graphics
    public static final int CELL_SIZE = 120; // cell width/height (square)
    public static final int BOARD_WIDTH  = CELL_SIZE * COLS; // the drawing canvas
    public static final int BOARD_HEIGHT = CELL_SIZE * ROWS;
    public static final int GRID_WIDTH = 10;                  // Grid-line's width
    public static final int GRID_WIDTH_HALF = GRID_WIDTH / 2;
    public static final int CELL_PADDING = CELL_SIZE / 5;
    public static final int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2; // width/height
    public static final int SYMBOL_STROKE_WIDTH = 8; // pen's stroke width
    public static final Color COLOR_BG = Color.WHITE;  // background
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_GRID   = Color.LIGHT_GRAY;  // grid lines
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    // This enum (inner class) contains the various states of the game
    public enum State {
        PLAYING, DRAW, CROSS_WON, NOUGHT_WON
    }
    private State currentState;  // the current game state

    // This enum (inner class) is used for:
    // 1. Player: CROSS, NOUGHT
    // 2. Cell's content: CROSS, NOUGHT and NO_SEED
    public enum Seed {
        CROSS, NOUGHT, NO_SEED
    }
    private Seed currentPlayer; // the current player
    private Seed[][] board;     // Game board of ROWS-by-COLS cells

    // UI Components
    private GamePanel gamePanel; // Drawing canvas (JPanel) for the game board
    private JLabel statusBar;  // Status Bar

    // ImageIcons for 'X' and 'O'
    private ImageIcon xIcon;
    private ImageIcon oIcon;

    /** Constructor to setup the game and the GUI components */
    public ConnectFourGraphics() {
        // Load images from resources folder
        xIcon = new ImageIcon(getClass().getResource("images/spongebob.png"));
        oIcon = new ImageIcon(getClass().getResource("images/patrick.png"));

        // Check if the images were loaded correctly
        if (xIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
            System.err.println("Error loading x_image.png");
        }
        if (oIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
            System.err.println("Error loading o_image.png");
        }

        // Initialize the game objects
        initGame();

        // Set up GUI components
        gamePanel = new GamePanel();  // Construct a drawing canvas (a JPanel)
        gamePanel.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

        // The canvas (JPanel) fires a MouseEvent upon mouse-click
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {  // mouse-clicked handler
                int mouseX = e.getX();
                int mouseY = e.getY();
                // Get the column clicked (based on X coordinate)
                int col = mouseX / CELL_SIZE;

                if (currentState == State.PLAYING) {
                    if (col >= 0 && col < COLS) {
                        // Temukan baris terendah yang kosong di kolom yang dipilih
                        for (int row = ROWS - 1; row >= 0; row--) {
                            if (board[row][col] == Seed.NO_SEED) {
                                board[row][col] = currentPlayer;  // Tempatkan disk
                                currentState = stepGame(currentPlayer, row, col);  // Periksa kemenangan
                                currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS; // Ganti pemain
                                repaint(); // Refresh tampilan
                                return;
                            }
                        }
                    }
                } else {
                    newGame();  // Mulai ulang jika permainan sudah selesai
                    repaint();
                }
            }
        });

        // Setup the status bar (JLabel) to display status message
        statusBar = new JLabel("       ");
        statusBar.setFont(FONT_STATUS);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));
        statusBar.setOpaque(true);
        statusBar.setBackground(COLOR_BG_STATUS);

        // Set up content pane
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(gamePanel, BorderLayout.CENTER);
        cp.add(statusBar, BorderLayout.PAGE_END); // same as SOUTH

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();  // pack all the components in this JFrame
        setTitle("Connect Four");
        setVisible(true);  // show this JFrame

        newGame();
    }

    /** Initialize the Game (run once) */
    public void initGame() {
        board = new Seed[ROWS][COLS]; // allocate array
    }

    /** Reset the game-board contents and the status, ready for new game */
    public void newGame() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                board[row][col] = Seed.NO_SEED; // all cells empty
            }
        }
        currentPlayer = Seed.CROSS;    // cross plays first
        currentState  = State.PLAYING; // ready to play
    }

    /**
     *  The given player makes a move on (selectedRow, selectedCol).
     *  Update cells[selectedRow][selectedCol]. Compute and return the
     *  new game state (PLAYING, DRAW, CROSS_WON, NOUGHT_WON).
     */
    public State stepGame(Seed player, int selectedRow, int selectedCol) {
        // Update papan
        board[selectedRow][selectedCol] = player;

        // Cek kemenangan horizontal (baris)
        for (int col = 0; col <= COLS - 4; col++) {
            if (board[selectedRow][col] == player &&
                    board[selectedRow][col + 1] == player &&
                    board[selectedRow][col + 2] == player &&
                    board[selectedRow][col + 3] == player) {
                return (player == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
            }
        }

        // Cek kemenangan vertikal (kolom)
        for (int row = 0; row <= ROWS - 4; row++) {
            if (board[row][selectedCol] == player &&
                    board[row + 1][selectedCol] == player &&
                    board[row + 2][selectedCol] == player &&
                    board[row + 3][selectedCol] == player) {
                return (player == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
            }
        }

        // Cek kemenangan diagonal (diagonal kiri atas ke kanan bawah)
        for (int row = 0; row <= ROWS - 4; row++) {
            for (int col = 0; col <= COLS - 4; col++) {
                if (board[row][col] == player &&
                        board[row + 1][col + 1] == player &&
                        board[row + 2][col + 2] == player &&
                        board[row + 3][col + 3] == player) {
                    return (player == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
                }
            }
        }

        // Cek kemenangan diagonal (diagonal kanan atas ke kiri bawah)
        for (int row = 0; row <= ROWS - 4; row++) {
            for (int col = 3; col < COLS; col++) {
                if (board[row][col] == player &&
                        board[row + 1][col - 1] == player &&
                        board[row + 2][col - 2] == player &&
                        board[row + 3][col - 3] == player) {
                    return (player == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
                }
            }
        }

        // Jika tidak ada pemenang, cek apakah sudah DRAW atau masih bisa bermain
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (board[row][col] == Seed.NO_SEED) {
                    return State.PLAYING;  // Masih ada ruang kosong
                }
            }
        }
        return State.DRAW;  // Tidak ada ruang kosong, hasil DRAW
    }

    /**
     *  Inner class DrawCanvas (extends JPanel) used for custom graphics drawing.
     */
    class GamePanel extends JPanel {
        private static final long serialVersionUID = 1L; // to prevent serializable warning

        @Override
        public void paintComponent(Graphics g) {  // Callback via repaint()
            super.paintComponent(g);
            setBackground(COLOR_BG);  // set its background color

            // Draw the grid lines
            g.setColor(COLOR_GRID);
            for (int row = 1; row < ROWS; ++row) {
                g.fillRoundRect(0, CELL_SIZE * row - GRID_WIDTH_HALF,
                        BOARD_WIDTH-1, GRID_WIDTH, GRID_WIDTH, GRID_WIDTH);
            }
            for (int col = 1; col < COLS; ++col) {
                g.fillRoundRect(CELL_SIZE * col - GRID_WIDTH_HALF, 0,
                        GRID_WIDTH, BOARD_HEIGHT-1, GRID_WIDTH, GRID_WIDTH);
            }

            // Draw the Seeds of all the cells if they are not empty
            // Use Graphics2D which allows us to set the pen's stroke
            Graphics2D g2d = (Graphics2D)g;
            g2d.setStroke(new BasicStroke(SYMBOL_STROKE_WIDTH,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int row = 0; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    int x1 = col * CELL_SIZE + CELL_PADDING;
                    int y1 = row * CELL_SIZE + CELL_PADDING;
                    if (board[row][col] == Seed.CROSS) {  // Draw a red disc
                        g.drawImage(xIcon.getImage(), x1, y1, SYMBOL_SIZE, SYMBOL_SIZE, this);
                    } else if (board[row][col] == Seed.NOUGHT) {  // Draw a yellow disc
                        g.drawImage(oIcon.getImage(), x1, y1, SYMBOL_SIZE, SYMBOL_SIZE, this);
                    }
                }
            }

            // Print status message
            if (currentState == State.PLAYING) {
                statusBar.setForeground(Color.BLACK);
                statusBar.setText((currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn");
            } else if (currentState == State.DRAW) {
                statusBar.setForeground(Color.RED);
                statusBar.setText("It's a Draw! Click to play again");
            } else if (currentState == State.CROSS_WON) {
                statusBar.setForeground(Color.RED);
                statusBar.setText("'X' Won! Click to play again");
            } else if (currentState == State.NOUGHT_WON) {
                statusBar.setForeground(Color.RED);
                statusBar.setText("'O' Won! Click to play again");
            }
        }
    }

    /** The entry main() method */
    public static void main(String[] args) {
        // Run GUI codes in the Event-Dispatching thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ConnectFourGraphics(); // Let the constructor do the job
            }
        });
    }
}
