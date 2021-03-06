package com.redscraf.wxjump.service.finder;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 直接根据色差来定位下一个中心点
 */
public class ColorFilterFinder {

    static Color bgColor = Color.RED;

    static Point startCenterPoint;

    public static Point findEndCenter(BufferedImage bufferedImage, Point startCenterPoint) {
        ColorFilterFinder.startCenterPoint = startCenterPoint;
        bgColor = new Color(bufferedImage.getRGB(540, 700));

        Point tmpStartCenterPoint;
        Point tmpEndCenterPoint;

        //排除小人所在的位置的整个柱状区域检测,为了排除某些特定情况的干扰.
        Rectangle rectangle = new Rectangle((int) (startCenterPoint.getX() - 75), 0, 150, (int) startCenterPoint.getY());


        Color lastColor = bgColor;
        for (int y = 600; y < startCenterPoint.y; y++) {
            for (int x = 10; x < bufferedImage.getWidth(); x++) {
                if (rectangle.contains(x, y)) {
                    continue;
                }
                Color newColor = new Color(bufferedImage.getRGB(x, y));
                if ((Math.abs(newColor.getRed() - lastColor.getRed()) +
                        Math.abs(newColor.getBlue() - lastColor.getBlue()) +
                        Math.abs(newColor.getGreen() - lastColor.getGreen()) >= 20)
                        ||
                        (Math.abs(newColor.getRed() - lastColor.getRed()) >= 15
                                || Math.abs(newColor.getBlue() - lastColor.getBlue()) >= 15
                                || Math.abs(newColor.getGreen() - lastColor.getGreen()) >= 15)) {
//                    System.out.println(BufferImageTest.toHexFromColor(newColor));
//                    System.out.println(BufferImageTest.toHexFromColor(lastColor));
//                    System.out.println("y = " + y + " x = " + x);
                    tmpStartCenterPoint = findStartCenterPoint(bufferedImage, x, y);
//                    System.out.println(tmpStartCenterPoint);
                    tmpEndCenterPoint = findEndCenterPoint(bufferedImage, tmpStartCenterPoint);
                    return new Point(tmpStartCenterPoint.x, (tmpEndCenterPoint.y + tmpStartCenterPoint.y) / 2);
                }
            }
        }
        return null;
    }

    /**
     * 查找新方块/圆的有效结束最低位置
     *
     * @param bufferedImage
     * @param tmpStartCenterPoint
     * @return
     */
    private static Point findEndCenterPoint(BufferedImage bufferedImage, Point tmpStartCenterPoint) {
        Color startColor = new Color(bufferedImage.getRGB(tmpStartCenterPoint.x, tmpStartCenterPoint.y));
        Color lastColor = startColor;
        int centX = tmpStartCenterPoint.x, centY = tmpStartCenterPoint.y;
        for (int i = tmpStartCenterPoint.y; i < bufferedImage.getHeight() && i < startCenterPoint.y - 10; i++) {
            //-2是为了避开正方体的右边墙壁的影响
            Color newColor = new Color(bufferedImage.getRGB(tmpStartCenterPoint.x, i));
            if (Math.abs(newColor.getRed() - lastColor.getRed()) <= 8
                    && Math.abs(newColor.getGreen() - lastColor.getGreen()) <= 8
                    && Math.abs(newColor.getBlue() - lastColor.getBlue()) <= 8) {
                centY = i;
            }
        }
        if (centY - tmpStartCenterPoint.y < 40) {
            centY = centY + 40;
        }
        if (centY - tmpStartCenterPoint.y > 230) {
            centY = tmpStartCenterPoint.y + 230;
        }
        return new Point(centX, centY);
    }

    //查找下一个方块的最高点的中点
    private static Point findStartCenterPoint(BufferedImage bufferedImage, int x, int y) {
        Color lastColor = new Color(bufferedImage.getRGB(x - 1, y));
        int centX = x, centY = y;
        for (int i = x; i < bufferedImage.getWidth(); i++) {
            Color newColor = new Color(bufferedImage.getRGB(i, y));
            if ((Math.abs(newColor.getRed() - lastColor.getRed()) +
                    Math.abs(newColor.getBlue() - lastColor.getBlue()) +
                    Math.abs(newColor.getGreen() - lastColor.getGreen()) >= 20)
                    ||
                    (Math.abs(newColor.getRed() - lastColor.getRed()) >= 15
                            || Math.abs(newColor.getBlue() - lastColor.getBlue()) >= 15
                            || Math.abs(newColor.getGreen() - lastColor.getGreen()) >= 15)) {
                centX = x + (i - x) / 2;
            } else {
                break;
            }
        }
        return new Point(centX, centY);
    }

//    public static void main(String[] args) throws IOException {
//        BufferedImage bufferedImage = ImageIO.read(new File("/Users/leejohn/Desktop/tmp/665_908.png"));
//        Point point = StartCenterFinder.findStartCenter(bufferedImage);
//        System.out.println(point);
//        Point point2 = findEndCenter(bufferedImage, point);
//        System.out.println(point2);
//    }

}
