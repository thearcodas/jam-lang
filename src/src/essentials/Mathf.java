package src.essentials;
public class Mathf
{
    public static double modulus(double x, double y)
    {
        return x - y * Math.floor(x / y);
    }
}
