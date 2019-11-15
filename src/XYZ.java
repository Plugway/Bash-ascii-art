import java.awt.*;

class XYZ {
    XYZ(Color c)
    {
        var R = ( c.getRed() / 255.0 );
        var G = ( c.getGreen() / 255.0 );
        var B = ( c.getBlue() / 255.0 );

        if ( R > 0.04045 ) 
            R = Math.pow (( ( R + 0.055 ) / 1.055 ), 2.4);
        else
            R = R / 12.92;
        if ( G > 0.04045 )
            G = Math.pow(( ( G + 0.055 ) / 1.055 ), 2.4);
        else
            G = G / 12.92;
        if ( B > 0.04045 )
            B = Math.pow(( ( B + 0.055 ) / 1.055 ), 2.4);
        else
            B = B / 12.92;
        R = R * 100;
        G = G * 100;
        B = B * 100;

        X = R * 0.4124 + G * 0.3576 + B * 0.1805;
        Y = R * 0.2126 + G * 0.7152 + B * 0.0722;
        Z = R * 0.0193 + G * 0.1192 + B * 0.9505;
    }
    double X;
    double Y;
    double Z;
}
