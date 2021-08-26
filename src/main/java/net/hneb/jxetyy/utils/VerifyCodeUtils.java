package net.hneb.jxetyy.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Created by zhangshuai on 2020/2/27.
 */
public class VerifyCodeUtils {
    private static int WIDTH = 100;
    private static int HEIGHT = 34;
    private static Random random = new Random();

    private static void addFilter(Graphics2D g2, BufferedImage image) {
        g2.setStroke( new BasicStroke( 2.5f ) );
        //绘制干扰线
        for (int i = 0; i < 8; i++) {
            g2.setColor(getRandColor(100, 200));// 设置线条的颜色
            g2.drawLine(
                    random.nextInt(WIDTH - 5), random.nextInt(HEIGHT - 2),
                    random.nextInt(WIDTH - 5), random.nextInt(HEIGHT - 2));
        }

        // 添加噪点
        float yawpRate = 0.05f;// 噪声率
        int area = (int) (yawpRate * WIDTH * HEIGHT);
        for (int i = 0; i < area; i++) {
            image.setRGB(random.nextInt(WIDTH),
                    random.nextInt(HEIGHT),
                    getRandColor(50, 200).getRGB());
        }

    }

    private static void drawBackground(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
    }

    private static void drawRands(Graphics2D g, char[] chars) {
        int fontSize = 20;
        int verifySize = chars.length;

        Font font = new Font("Comic Sans MS", Font.ITALIC | Font.BOLD, fontSize);
        g.setFont(font);
        for (int i = 0; i < verifySize; i++) {
            g.setColor(getRandColor(0, 120));
            AffineTransform affine = new AffineTransform();
            affine.setToRotation(Math.PI / verifySize * random.nextDouble() * (random.nextBoolean() ? 1 : -1),
                    (WIDTH / 4) * i + fontSize / 2   // fontsize /2
                    , HEIGHT / 2);
            g.setTransform(affine);
            g.drawChars(chars, i, 1, ((WIDTH - 10) / verifySize) * i + 5, HEIGHT / 2 + fontSize / 2 - 4);
        }
    }

    public static char[] generateCheckCode() {
        String chars = "0123456789";
        char[] rands = new char[4];

        for (int i = 0; i < 4; ++i) {
            int rand = (int) (Math.random() * (double) chars.length());
            rands[i] = chars.charAt(rand);
        }

        return rands;
    }

    public static byte[] getCodeImage(char[] code) throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, 1);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawBackground(g);
        drawRands(g, code);
        addFilter(g, image);
        g.dispose();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "JPEG", bos);
        return bos.toByteArray();
    }

    private static Color getRandColor(int fc, int bc) {
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
}
