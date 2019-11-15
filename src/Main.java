import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Main {
    private static Color[] colorTable256;
    private static Random rnd;
    public static void main(String[] args) {
        getTable(args[0].toLowerCase());
        String ipath = args[1];
        String iname = args[2];
        String iext = args[3];
        int addRate = 1;
        BufferedImage currentBitmap;
        try {
            var file = new File(ipath +"/"+ iname +"."+ iext);
            currentBitmap = ImageIO.read(file);
        }
        catch (IOException e)
        {
            throw new Error("Wrong file: " + ipath +"/"+ iname +"."+ iext);
        }
        if (args.length == 6)
        {
            try {
                addRate = Integer.parseInt(args[5]);
            }
            catch (NumberFormatException ignored){}
            if ( !(addRate > 0 && addRate <= 150) )
                throw new Error("LineSkip error");
        }
        var result = getBashString(currentBitmap, args[4], addRate);
        try {
            var filewr = new FileWriter(ipath +"/"+ iname +"_"+args[0]+".txt");
            filewr.write(result);
            filewr.flush();
        }
        catch (IOException e)
        {
            throw new Error("File write error: " + ipath +"/"+ iname +"_"+args[0]+".txt");
        }
    }

    private static void getTable(String tableType) {
        switch (tableType)
        {
            case "256colors":
                colorTable256 = gen256Table();
                return;
            case "16colors":
                colorTable256 = gen16Table();
                return;
            case "grayscale":
                colorTable256 = genGrayscale();
                return;
            default:
                throw new Error("Wrong argument error: " + tableType);
        }
    }

    private static String getBashString(BufferedImage currentBitmap, String settedDelta, int addRate) {
        var builder = new StringBuilder();
        var currentHeight = currentBitmap.getHeight();
        var currentWidth = currentBitmap.getWidth();
        int maxDelta;
        var cieLab256 = new CieLab[colorTable256.length];
        for (var i = 0; i < colorTable256.length; i++)
        {
            cieLab256[i] = new CieLab(new XYZ(colorTable256[i]));
        }
        if (settedDelta.equals("a"))
        {
            maxDelta = getMaxMinDelta(currentBitmap, cieLab256, addRate);
        }
        else
        {
            try {
                maxDelta=Integer.parseInt(settedDelta);
            }
            catch (NumberFormatException e)
            {
                throw new Error("ColorDifference error: value "+ settedDelta +" is not a positive integer");
            }
            if (!(maxDelta>0 && maxDelta<=150))
                throw new Error("ColorDifference error: number is either greater than 150 or less than 0");
        }
        for (var y = 0; y < currentHeight; y+=addRate)
        {
            for (var x = 0; x < currentWidth; x++)
            {
                var currentColor = new Color(currentBitmap.getRGB(x, y));
                var currentCieLab = new CieLab(new XYZ(currentColor));
                var minDelta = Double.MAX_VALUE;
                var currentBgColor = 0;
                for (var i = 0; i < cieLab256.length; i++)
                {
                    var newDelta = CieLab.calcDelta(currentCieLab, cieLab256[i]);
                    if (newDelta < minDelta)
                    {
                        minDelta = newDelta;
                        currentBgColor = i;
                    }
                }
                var currentChar = getCurChar(minDelta, maxDelta);//256 - 33, 16 - 43, gray - 82
                var currentCharColor = 0;
                var currentCharCie = new CieLab(new XYZ(getCDiff(currentColor, colorTable256[currentBgColor])));
                minDelta = Double.MAX_VALUE;
                for (var i = 0; i < cieLab256.length; i++)
                {
                    var newDelta = CieLab.calcDelta(currentCharCie, cieLab256[i]);
                    if (newDelta < minDelta)
                    {
                        minDelta = newDelta;
                        currentCharColor = i;
                    }
                }
                builder.append("\\e[48;05;")
                        .append(currentBgColor)
                        .append(";38;05;")
                        .append(currentCharColor)
                        .append("m")
                        .append(currentChar);
            }
            builder.append("\\e[48;05;0;38;05;0m\\n");
        }
        return builder.toString();
    }

    private static int getMaxMinDelta(BufferedImage bitmap, CieLab[] cieLab256, int addRate)
    {
        var currentHeight = bitmap.getHeight();
        var currentWidth = bitmap.getWidth();
        var maxmin = Double.MIN_VALUE;
        int maxminInt, maxminy = 0, maxminx = 0, maxminr = 0, maxming = 0, maxminb = 0;
        for (var y = 0; y < currentHeight; y += addRate) {
            for (var x = 0; x < currentWidth; x++) {
                var currentColor = new Color(bitmap.getRGB(x, y));
                var currentCieLab = new CieLab(new XYZ(currentColor));
                var minDelta = Double.MAX_VALUE;
                for (CieLab cieLab : cieLab256) {
                    var newDelta = CieLab.calcDelta(currentCieLab, cieLab);
                    if (newDelta < minDelta) {
                        minDelta = newDelta;
                    }
                }
                if (minDelta > maxmin) {
                    maxmin = minDelta;
                    maxminx = x;
                    maxminy = y;
                    maxminr = currentColor.getRed();
                    maxming = currentColor.getGreen();
                    maxminb = currentColor.getBlue();
                }
            }
        }
        maxminInt = (int)(Math.ceil(maxmin));
        System.out.println("Max min delta: " + maxmin + ", recommended: " + maxminInt + ", x: " + maxminx + ", y: " + maxminy + ", rgb: " + maxminr + "." + maxming + "." + maxminb);
        return maxminInt;
    }

    private static Color getCDiff(Color center, Color start)
    {
        return new Color(getCDiffOneC(center.getRed(), start.getRed())
                , getCDiffOneC(center.getGreen(), start.getGreen())
                , getCDiffOneC(center.getBlue(), start.getBlue()));
    }
    private static int getCDiffOneC(int centc, int starc)
    {
        var d = (starc - centc)*2;
        var res = starc - d;
        if (res < 0)
            res = 0;
        else if (res > 255)
            res = 255;
        return res;
    }
    private static String getCurChar(double minDelta, double maxDelta)
    {
        rnd = new Random((long)minDelta*10000);
        var perCent = map(minDelta, 0, maxDelta, 0, 100);
        var str = "";
        if (perCent <= 12.5) {
            str = " ";
        } else if (perCent <= 25) {
            str = getRandomChar(".',^:~");
        } else if (perCent <= 37.5) {
            str = getRandomChar("-_+<>i!lI");
        } else if (perCent <= 50) {
            str = getRandomChar("/|1{}[");
        } else if (perCent <= 62.5) {
            str = getRandomChar("rcvunxzjf");
        } else if (perCent <= 75) {
            str = getRandomChar("LCJUYXZO0");
        } else if (perCent <= 87.5) {
            str = getRandomChar("oahkbdpqw");
        } else if (perCent <= 100) {
            str = getRandomChar("WMB8&%$#@");
        }
        return str;
    }

    private static String getRandomChar(String s){
        return Character.toString(s.charAt(rnd.nextInt(s.length())));
    }
    private static double map(double val, double fromMin, double fromMax, double toMin, double toMax)
    {
        return (val-fromMin)/(fromMax-fromMin)*(toMax-toMin)+toMin;
    }
    private static Color[] gen256Table()
    {
        var colors = new Color[256];
        colors[0] = new Color(0,0,0);
        colors[1] = new Color(128,0,0);
        colors[2] = new Color(0,128,0);
        colors[3] = new Color(128,128,0);
        colors[4] = new Color(0,0,128);
        colors[5] = new Color(128,0,128);
        colors[6] = new Color(0,128,128);
        colors[7] = new Color(192,192,192);
        colors[8] = new Color(128,128,128);
        colors[9] = new Color(255,0,0);
        colors[10] = new Color(0,255,0);
        colors[11] = new Color(255,255,0);
        colors[12] = new Color(0,0,255);
        colors[13] = new Color(255,0,255);
        colors[14] = new Color(0,255,255);
        colors[15] = new Color(255,255,255);
        var colorIndex = 16;
        var ri=0;
        var gi=0;
        var bi=0;
        for (var r = 0; r < 6; r++)
        {
            gi = 0;
            for (var g = 0; g < 6; g++)
            {
                bi = 0;
                for (var b = 0; b < 6; b++)
                {
                    colors[colorIndex] = new Color(95*ri+(r-ri)*40,95*gi+(g-gi)*40,95*bi+(b-bi)*40);
                    colorIndex++;
                    bi=1;
                }
                gi=1;
            }
            ri=1;
        }
        for (var rgb = 0; rgb < 24; rgb++)
        {
            var c = rgb*10+8;
            colors[colorIndex] = new Color(c,c,c);
            colorIndex++;
        }
        return colors;
    }
    private static Color[] gen16Table()
    {
        var colors = new Color[256];
        colors[0] = new Color(0,0,0);
        colors[1] = new Color(128,0,0);
        colors[2] = new Color(0,128,0);
        colors[3] = new Color(128,128,0);
        colors[4] = new Color(0,0,128);
        colors[5] = new Color(128,0,128);
        colors[6] = new Color(0,128,128);
        colors[7] = new Color(192,192,192);
        colors[8] = new Color(128,128,128);
        colors[9] = new Color(255,0,0);
        colors[10] = new Color(0,255,0);
        colors[11] = new Color(255,255,0);
        colors[12] = new Color(0,0,255);
        colors[13] = new Color(255,0,255);
        colors[14] = new Color(0,255,255);
        colors[15] = new Color(255,255,255);
        for (var colorIndex = 16 ;colorIndex < colors.length; colorIndex++)
        {
            colors[colorIndex] = new Color(0,0,0);
        }
        return colors;
    }
    private static Color[] genGrayscale()
    {
        var colors = new Color[256];
        for (var i = 0; i < 15; i++)
            colors[i] = new Color(0,0,0);
        colors[15] = new Color(255,255,255);
        for (var i = 16; i < 232; i++)
            colors[i] = new Color(0,0,0);
        var colorIndex = 232;
        for (var rgb = 0; rgb < 24; rgb++)
        {
            var c = rgb*10+8;
            colors[colorIndex] = new Color(c,c,c);
            colorIndex++;
        }
        return colors;
    }
}
