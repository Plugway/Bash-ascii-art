class CieLab {
    CieLab(XYZ c)
    {
        var X = c.X / 95.047;
        var Y = c.Y / 100.000;
        var Z = c.Z / 108.883;

        if ( X > 0.008856 )
            X = Math.pow(X, (1/3.0));
        else
            X = ( 7.787 * X ) + ( 16 / 116.0 );
        if ( Y > 0.008856 )
            Y = Math.pow(Y, (1/3.0));
        else
            Y = ( 7.787 * Y ) + ( 16 / 116.0 );
        if ( Z > 0.008856 )
            Z = Math.pow(Z, (1/3.0));
        else
            Z = ( 7.787 * Z ) + ( 16 / 116.0 );

        L = ( 116 * Y ) - 16;
        a = 500 * ( X - Y );
        b = 200 * ( Y - Z );
    }
    private double L;
    private double a;
    private double b;

    static double calcDelta(CieLab c1, CieLab c2)
    {
        return Math.sqrt(( Math.pow(( c1.L - c2.L ), 2) + Math.pow(( c1.a - c2.a), 2) + Math.pow(( c1.b - c2.b), 2)));
    }
}
