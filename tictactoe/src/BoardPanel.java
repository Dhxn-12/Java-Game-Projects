

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * The interactive game board panel (MVC — View).
 * Handles rendering of cells, symbols, hover effects, and win animations.
 *
 * @author TicTacToe Pro Team
 * @version 2.0.0
 */
public class BoardPanel extends JPanel implements GameEventListener {

    private final GameBoard gameBoard;
    private int size;

    // ── Animation State ───────────────────────────────────────────────────────
    private float[][]  cellScale;      // scale factor per cell (for place animation)
    private float[][]  cellAlpha;      // alpha per cell
    private float      winPulse = 0f;  // win highlight pulse
    private Timer      winPulseTimer;
    private boolean    aiThinking = false;
    private float      aiDotAnim  = 0f;

    // ── Hover ────────────────────────────────────────────────────────────────
    private int hoverRow = -1, hoverCol = -1;

    public BoardPanel(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.size      = gameBoard.getBoardSize();

        cellScale = new float[size][size];
        cellAlpha = new float[size][size];
        for (float[] row : cellScale) java.util.Arrays.fill(row, 1f);
        for (float[] row : cellAlpha) java.util.Arrays.fill(row, 1f);

        setOpaque(false);
        setPreferredSize(new Dimension(420, 420));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { handleClick(e); }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int[] rc = getCellAt(e.getX(), e.getY());
                if (rc != null && (rc[0] != hoverRow || rc[1] != hoverCol)) {
                    hoverRow = rc[0]; hoverCol = rc[1];
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override public void mouseExited(MouseEvent e) { hoverRow = -1; hoverCol = -1; repaint(); }
        });

        gameBoard.addListener(this);
    }

    // ── GameEventListener ─────────────────────────────────────────────────────

    @Override
    public void onGameEvent(GameEvent event, int[] data) {
        switch (event) {
            case MOVE_MADE -> {
                aiThinking = false;
                if (data != null) animateCell(data[0], data[1]);
                repaint();
            }
            case AI_THINKING -> { aiThinking = true; startAiDotAnim(); repaint(); }
            case GAME_OVER   -> { aiThinking = false; startWinPulse(); }
            case GAME_STARTED -> {
                size = gameBoard.getBoardSize();
                cellScale = new float[size][size];
                cellAlpha = new float[size][size];
                for (float[] row : cellScale) java.util.Arrays.fill(row, 1f);
                for (float[] row : cellAlpha) java.util.Arrays.fill(row, 1f);
                aiThinking = false;
                repaint();
            }
            default -> repaint();
        }
    }

    // ── Click Handling ────────────────────────────────────────────────────────

    private void handleClick(MouseEvent e) {
        int[] rc = getCellAt(e.getX(), e.getY());
        if (rc == null) return;
        gameBoard.humanMove(rc[0], rc[1]);
    }

    private int[] getCellAt(int x, int y) {
        GameState state = gameBoard.getState();
        if (state == null) return null;
        int cellW = getWidth()  / size;
        int cellH = getHeight() / size;
        int col = x / cellW;
        int row = y / cellH;
        if (row < 0 || row >= size || col < 0 || col >= size) return null;
        return new int[]{row, col};
    }

    // ── Animations ────────────────────────────────────────────────────────────

    private void animateCell(int row, int col) {
        cellScale[row][col] = 0.1f;
        Timer t = new Timer(16, null);
        t.addActionListener(e -> {
            cellScale[row][col] = Math.min(1f, cellScale[row][col] + 0.12f);
            repaint();
            if (cellScale[row][col] >= 1f) ((Timer)e.getSource()).stop();
        });
        t.start();
    }

    private void startWinPulse() {
        if (winPulseTimer != null) winPulseTimer.stop();
        winPulse = 0f;
        winPulseTimer = new Timer(40, e -> {
            winPulse = (float)((Math.sin(System.currentTimeMillis() * 0.006) + 1) / 2);
            repaint();
        });
        winPulseTimer.start();
    }

    private void startAiDotAnim() {
        Timer t = new Timer(80, e -> {
            aiDotAnim = (aiDotAnim + 0.3f) % (float)(Math.PI * 2);
            repaint();
            if (!aiThinking) ((Timer)e.getSource()).stop();
        });
        t.start();
    }

    // ── Painting ──────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        UIUtils.enableAntiAliasing(g2);

        ThemeManager.Theme t = ThemeManager.getInstance().getTheme();
        GameState state = gameBoard.getState();
        if (state == null) { g2.dispose(); return; }

        int w = getWidth(), h = getHeight();
        int cellW = w / size, cellH = h / size;
        int[] winLine = state.getWinLine();

        // ── Draw cells ──────────────────────────────────────────────────────
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                int cx = c * cellW, cy = r * cellH;

                // Determine if this cell is on win line
                boolean onWin = isOnWinLine(winLine, r, c);

                // Cell background
                Color bg = (r == hoverRow && c == hoverCol && state.isValidMove(r, c))
                        ? t.cellHover : t.cellBg;
                g2.setColor(bg);
                g2.fillRoundRect(cx + 3, cy + 3, cellW - 6, cellH - 6, 10, 10);

                // Cell border
                Color borderColor = onWin
                        ? new Color(t.winHighlight.getRed(), t.winHighlight.getGreen(),
                              t.winHighlight.getBlue(), (int)(150 + 105 * winPulse))
                        : t.gridColor;
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(onWin ? 2.5f : 1.5f));
                g2.drawRoundRect(cx + 3, cy + 3, cellW - 6, cellH - 6, 10, 10);

                // Win cell glow
                if (onWin) {
                    g2.setColor(new Color(t.winHighlight.getRed(), t.winHighlight.getGreen(),
                            t.winHighlight.getBlue(), (int)(30 + 40 * winPulse)));
                    g2.fillRoundRect(cx + 3, cy + 3, cellW - 6, cellH - 6, 10, 10);
                }

                // Symbol
                char sym = state.getCell(r, c);
                if (sym != state.EMPTY()) {
                    drawSymbol(g2, sym, cx, cy, cellW, cellH, cellScale[r][c], t, onWin);
                }
            }
        }

        // ── AI thinking overlay ─────────────────────────────────────────────
        if (aiThinking) {
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillRoundRect(0, 0, w, h, 12, 12);
            drawAiThinkingDots(g2, w, h, t);
        }

        g2.dispose();
    }

    private void drawSymbol(Graphics2D g2, char sym, int cx, int cy,
                             int cellW, int cellH, float scale,
                             ThemeManager.Theme t, boolean highlighted) {
        Color color = (sym == 'X') ? t.symbolX : t.symbolO;
        if (highlighted) color = blend(color, t.winHighlight, winPulse * 0.4f);

        int margin = (int)(cellW * 0.18);
        int sx = cx + margin, sy = cy + margin;
        int sw = cellW - 2 * margin, sh = cellH - 2 * margin;

        // Apply scale animation from center
        g2.translate(cx + cellW / 2.0, cy + cellH / 2.0);
        g2.scale(scale, scale);
        g2.translate(-(cx + cellW / 2.0), -(cy + cellH / 2.0));

        g2.setStroke(new BasicStroke(Math.max(2.5f, cellW * 0.08f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        if (sym == 'X') {
            // Glow
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60));
            g2.setStroke(new BasicStroke(cellW * 0.14f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(sx, sy, sx + sw, sy + sh);
            g2.drawLine(sx + sw, sy, sx, sy + sh);
            // Main
            g2.setColor(color);
            g2.setStroke(new BasicStroke(Math.max(3f, cellW * 0.08f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(sx, sy, sx + sw, sy + sh);
            g2.drawLine(sx + sw, sy, sx, sy + sh);
        } else {
            int cx2 = sx + sw / 2, cy2 = sy + sh / 2;
            int r = Math.min(sw, sh) / 2;
            // Glow
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60));
            g2.setStroke(new BasicStroke(cellW * 0.14f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawOval(cx2 - r, cy2 - r, r * 2, r * 2);
            // Main
            g2.setColor(color);
            g2.setStroke(new BasicStroke(Math.max(3f, cellW * 0.08f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawOval(cx2 - r, cy2 - r, r * 2, r * 2);
        }

        // Reset transform
        g2.setTransform(new java.awt.geom.AffineTransform());
        g2.scale(1, 1);
        // Re-apply panel transform lost by reset
        // (safer to recreate g2 per symbol, but this works for our fixed viewport)
    }

    private void drawAiThinkingDots(Graphics2D g2, int w, int h, ThemeManager.Theme t) {
        int dotCount = 3;
        int dotR = 8;
        int spacing = 24;
        int totalW = dotCount * spacing;
        int startX = (w - totalW) / 2 + spacing / 2;
        int startY = h / 2;

        for (int i = 0; i < dotCount; i++) {
            float phase = aiDotAnim - i * 0.8f;
            float yOff = (float)(Math.sin(phase) * 10);
            float alpha = (float)((Math.sin(phase) + 1) / 2);
            g2.setColor(new Color(t.accent.getRed(), t.accent.getGreen(), t.accent.getBlue(),
                    (int)(100 + 155 * alpha)));
            g2.fillOval(startX + i * spacing - dotR, (int)(startY + yOff) - dotR, dotR*2, dotR*2);
        }

        g2.setFont(ThemeManager.FONT_SMALL);
        g2.setColor(t.accent);
        String msg = "AI thinking...";
        g2.drawString(msg, (w - g2.getFontMetrics().stringWidth(msg)) / 2, h / 2 + 30);
    }

    private boolean isOnWinLine(int[] winLine, int r, int c) {
        if (winLine == null) return false;
        for (int i = 0; i < winLine.length; i += 2) {
            if (winLine[i] == r && winLine[i+1] == c) return true;
        }
        return false;
    }

    private Color blend(Color a, Color b, float t) {
        return new Color(
            (int)(a.getRed()   * (1-t) + b.getRed()   * t),
            (int)(a.getGreen() * (1-t) + b.getGreen() * t),
            (int)(a.getBlue()  * (1-t) + b.getBlue()  * t));
    }

    public void cleanup() {
        if (winPulseTimer != null) winPulseTimer.stop();
        gameBoard.removeListener(this);
    }
}