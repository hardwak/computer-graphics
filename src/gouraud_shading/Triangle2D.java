package gouraud_shading;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Triangle2D {
    private int[] xPoints;
    private int[] yPoints;
    private Color[] colors;

    public Triangle2D(int[] x, int[] y, Color[] c) {
        this.xPoints = x.clone();
        this.yPoints = y.clone();
        this.colors = c.clone();

        sortVerticesByY();
    }

    public void gouraudShadeToImage(BufferedImage image) {
        int minX = Math.min(Math.min(xPoints[0], xPoints[1]), xPoints[2]);
        int maxX = Math.max(Math.max(xPoints[0], xPoints[1]), xPoints[2]);
        int minY = Math.min(Math.min(yPoints[0], yPoints[1]), yPoints[2]);
        int maxY = Math.max(Math.max(yPoints[0], yPoints[1]), yPoints[2]);

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                if (isPointInTriangle(x, y)) {
                    Color interpolatedColor = interpolateColor(x, y);
                    image.setRGB(x, y, interpolatedColor.getRGB());
                }
            }
        }
    }

    public void gouraudShadeToScreen(Graphics g) {
        int minX = Math.min(Math.min(xPoints[0], xPoints[1]), xPoints[2]);
        int maxX = Math.max(Math.max(xPoints[0], xPoints[1]), xPoints[2]);
        int minY = Math.min(Math.min(yPoints[0], yPoints[1]), yPoints[2]);
        int maxY = Math.max(Math.max(yPoints[0], yPoints[1]), yPoints[2]);

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                if (isPointInTriangle(x, y)) {
                    Color interpolatedColor = interpolateColor(x, y);
                    g.setColor(interpolatedColor);
                    g.fillRect(x, y, 1, 1);
                }
            }
        }
    }

    public void optimizedGouraudShadeToImage(BufferedImage image) {
        int x0 = xPoints[0], y0 = yPoints[0];
        int x1 = xPoints[1], y1 = yPoints[1];
        int x2 = xPoints[2], y2 = yPoints[2];

        Color c0 = colors[0];
        Color c1 = colors[1];
        Color c2 = colors[2];

        float slopeUpToMid = (float)(x1 - x0) / (y1 - y0);
        float slopeUpToDown = (float)(x2 - x0) / (y2 - y0);

        if (y1 > y0) {
            float curX1 = x0;
            float curX2 = x0;

            for (int lineY = y0; lineY < y1; lineY++) {
                int startX = (int)Math.min(curX1, curX2);
                int endX = (int)Math.max(curX1, curX2);

                float g1 = (lineY - y0) / (float)(y2 - y0);
                float g2 = (lineY - y0) / (float)(y1 - y0);

                Color leftColor, rightColor;

                if (curX1 < curX2) {
                    leftColor = interpolateColors(c0, c1, g2);
                    rightColor = interpolateColors(c0, c2, g1);
                } else {
                    leftColor = interpolateColors(c0, c2, g1);
                    rightColor = interpolateColors(c0, c1, g2);
                }

                drawGradientLine(image, lineY, startX, endX, leftColor, rightColor);

                curX1 += slopeUpToMid;
                curX2 += slopeUpToDown;
            }
        }

        float slopeMidToDown = (float)(x2 - x1) / (y2 - y1);

        if (y2 > y1) {
            float curX1 = x1;
            float curX2 = x0 + slopeUpToDown * (y1 - y0);

            for (int lineY = y1; lineY <= y2; lineY++) {
                int startX = (int) Math.min(curX1, curX2);
                int endX = (int)Math.max(curX1, curX2);

                float g1 = (lineY - y1) / (float)(y2 - y1);
                float g2 = (lineY - y0) / (float)(y2 - y0);

                Color leftColor, rightColor;

                if (curX1 < curX2) {
                    leftColor = interpolateColors(c1, c2, g1);
                    rightColor = interpolateColors(c0, c2, g2);
                } else {
                    leftColor = interpolateColors(c0, c2, g2);
                    rightColor = interpolateColors(c1, c2, g1);
                }

                drawGradientLine(image, lineY, startX, endX, leftColor, rightColor);

                curX1 += slopeMidToDown;
                curX2 += slopeUpToDown;
            }
        }
    }

    public void optimizedGouraudShadeToScreen(Graphics g) {
        int x0 = xPoints[0], y0 = yPoints[0];
        int x1 = xPoints[1], y1 = yPoints[1];
        int x2 = xPoints[2], y2 = yPoints[2];

        Color c0 = colors[0];
        Color c1 = colors[1];
        Color c2 = colors[2];

        float slopeUpToMid = (float)(x1 - x0) / (y1 - y0);
        float slopeUpToDown = (float)(x2 - x0) / (y2 - y0);

        if (y1 > y0) {
            float curX1 = x0;
            float curX2 = x0;

            for (int lineY = y0; lineY < y1; lineY++) {
                int startX = (int)Math.min(curX1, curX2);
                int endX = (int)Math.max(curX1, curX2);

                float g1 = (lineY - y0) / (float)(y2 - y0);
                float g2 = (lineY - y0) / (float)(y1 - y0);

                Color leftColor, rightColor;

                if (curX1 < curX2) {
                    leftColor = interpolateColors(c0, c1, g2);
                    rightColor = interpolateColors(c0, c2, g1);
                } else {
                    leftColor = interpolateColors(c0, c2, g1);
                    rightColor = interpolateColors(c0, c1, g2);
                }

                drawGradientLine(g, lineY, startX, endX, leftColor, rightColor);

                curX1 += slopeUpToMid;
                curX2 += slopeUpToDown;
            }
        }

        float slopeMidToDown = (float)(x2 - x1) / (y2 - y1);

        if (y2 > y1) {
            float curX1 = x1;
            float curX2 = x0 + slopeUpToDown * (y1 - y0);

            for (int lineY = y1; lineY <= y2; lineY++) {
                int startX = (int)Math.min(curX1, curX2);
                int endX = (int)Math.max(curX1, curX2);

                float g1 = (lineY - y1) / (float)(y2 - y1);
                float g2 = (lineY - y0) / (float)(y2 - y0);

                Color leftColor, rightColor;

                if (curX1 < curX2) {
                    leftColor = interpolateColors(c1, c2, g1);
                    rightColor = interpolateColors(c0, c2, g2);
                } else {
                    leftColor = interpolateColors(c0, c2, g2);
                    rightColor = interpolateColors(c1, c2, g1);
                }

                drawGradientLine(g, lineY, startX, endX, leftColor, rightColor);

                curX1 += slopeMidToDown;
                curX2 += slopeUpToDown;
            }
        }
    }

    private void drawGradientLine(BufferedImage image, int y, int startX, int endX, Color startColor, Color endColor) {
        for (int x = startX; x <= endX; x++) {
            float t = (startX == endX) ? 0 : (float)(x - startX) / (endX - startX);
            Color interpolatedColor = interpolateColors(startColor, endColor, t);
            image.setRGB(x, y, interpolatedColor.getRGB());
        }
    }

    private void drawGradientLine(Graphics g, int y, int startX, int endX, Color startColor, Color endColor) {
        for (int x = startX; x <= endX; x++) {
            float t = (startX == endX) ? 0 : (float)(x - startX) / (endX - startX);
            Color interpolatedColor = interpolateColors(startColor, endColor, t);
            g.setColor(interpolatedColor);
            g.fillRect(x, y, 1, 1);
        }
    }

    private void sortVerticesByY() {
        for (int i = 0; i < 2; i++) {
            int minIndex = i;
            for (int j = i + 1; j < 3; j++) {
                if (yPoints[j] < yPoints[minIndex]) {
                    minIndex = j;
                }
            }

            if (minIndex != i) {
                swapVertices(i, minIndex);
            }
        }
    }

    private void swapVertices(int i, int j) {
        int tempX = xPoints[i];
        int tempY = yPoints[i];
        Color tempColor = colors[i];

        xPoints[i] = xPoints[j];
        yPoints[i] = yPoints[j];
        colors[i] = colors[j];

        xPoints[j] = tempX;
        yPoints[j] = tempY;
        colors[j] = tempColor;
    }

    private boolean isPointInTriangle(int x, int y) {
        double[] cords = calculateBarycentricCoordinates(x,y);
        double a = cords[0], b = cords[1], c = cords[2];

        return a >= 0 && a <= 1 && b >= 0 && b <= 1 && c >= 0 && c <= 1;
    }

    private double[] calculateBarycentricCoordinates(int x, int y) {
        /*
        P = α·A + β·B + γ·C

        x = α·x₁ + β·x₂ + γ·x₃
        y = α·y₁ + β·y₂ + γ·y₃
        1 = α + β + γ

        x = α·x₁ + β·x₂ + (1-α-β)·x₃
        y = α·y₁ + β·y₂ + (1-α-β)·y₃

        x = x₃ + α(x₁-x₃) + β(x₂-x₃)
        y = y₃ + α(y₁-y₃) + β(y₂-y₃)
         */

        //(y₂-y₃)(x₁-x₃) + (x₃-x₂)(y₁-y₃)
        double d = (yPoints[1] - yPoints[2]) * (xPoints[0] - xPoints[2]) +
                (xPoints[2] - xPoints[1]) * (yPoints[0] - yPoints[2]);

        //α = ((y₂-y₃)(x-x₃) + (x₃-x₂)(y-y₃)) / d
        double a = ((yPoints[1] - yPoints[2]) * (x - xPoints[2]) +
                (xPoints[2] - xPoints[1]) * (y - yPoints[2])) / d;

        //β = ((y₃-y₁)(x-x₃) + (x₁-x₃)(y-y₃)) / d
        double b = ((yPoints[2] - yPoints[0]) * (x - xPoints[2]) +
                (xPoints[0] - xPoints[2]) * (y - yPoints[2])) / d;

        double c = 1 - a - b;

        return new double[] {a, b, c};
    }

    private Color interpolateColor(int x, int y) {
        double[] baryCoords = calculateBarycentricCoordinates(x, y);

        int r = (int)(baryCoords[0] * colors[0].getRed() +
                baryCoords[1] * colors[1].getRed() +
                baryCoords[2] * colors[2].getRed());

        int g = (int)(baryCoords[0] * colors[0].getGreen() +
                baryCoords[1] * colors[1].getGreen() +
                baryCoords[2] * colors[2].getGreen());

        int b = (int)(baryCoords[0] * colors[0].getBlue() +
                baryCoords[1] * colors[1].getBlue() +
                baryCoords[2] * colors[2].getBlue());

        return new Color(r, g, b);
    }

    private Color interpolateColors(Color c1, Color c2, float grad) {
        grad = Math.max(0.0f, Math.min(1.0f, grad));

        int r = (int)(c1.getRed() * (1 - grad) + c2.getRed() * grad);
        int g = (int)(c1.getGreen() * (1 - grad) + c2.getGreen() * grad);
        int b = (int)(c1.getBlue() * (1 - grad) + c2.getBlue() * grad);

        return new Color(r, g, b);
    }

    public int countPixelsInside() {
        int count = 0;

        int minX = Math.min(Math.min(xPoints[0], xPoints[1]), xPoints[2]);
        int maxX = Math.max(Math.max(xPoints[0], xPoints[1]), xPoints[2]);
        int minY = Math.min(Math.min(yPoints[0], yPoints[1]), yPoints[2]);
        int maxY = Math.max(Math.max(yPoints[0], yPoints[1]), yPoints[2]);

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                if (isPointInTriangle(x, y)) {
                    count++;
                }
            }
        }

        return count;
    }
}
