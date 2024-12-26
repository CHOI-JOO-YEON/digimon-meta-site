package com.joo.digimon.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ImageUtil {

    public static BufferedImage getImageData(String imageUrl) throws IOException {
        BufferedImage image = ImageIO.read(new URL(imageUrl));
        return trimImageTransparent(image);
    }

    private static BufferedImage trimImageTransparent(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int top = 0, left = 0, right = width - 1, bottom = height - 1;

        while (top < bottom) {
            int[] pixels = new int[width];
            image.getRGB(0, top, width, 1, pixels, 0, width);
            if (hasNonTransparentPixel(pixels)) {
                break;
            }
            top++;
        }

        while (bottom > top) {
            int[] pixels = new int[width];
            image.getRGB(0, bottom, width, 1, pixels, 0, width);
            if (hasNonTransparentPixel(pixels)) {
                break;
            }
            bottom--;
        }

        while (left < right) {
            int[] pixels = new int[height];
            image.getRGB(left, 0, 1, height, pixels, 0, 1);
            if (hasNonTransparentPixel(pixels)) {
                break;
            }
            left++;
        }

        while (right > left) {
            int[] pixels = new int[height];
            image.getRGB(right, 0, 1, height, pixels, 0, 1);
            if (hasNonTransparentPixel(pixels)) {
                break;
            }
            right--;
        }

        return image.getSubimage(left, top, right - left + 1, bottom - top + 1);
    }

    private static boolean hasNonTransparentPixel(int[] pixels) {
        for (int pixel : pixels) {
            int alpha = (pixel >> 24) & 0xff;
            if (alpha != 0) {
                return true;
            }
        }
        return false;
    }
}
