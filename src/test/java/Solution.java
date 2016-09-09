import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Solution
{
  public static void main (final String [] args) throws IOException
  {
    String sFile = " /*This is a program to calculate area of a circle after getting the radius as input from the user*/\r\n" +
                   "#include<stdio.h>\r\n" +
                   "int main()\r\n" +
                   "{\r\n" +
                   "   double radius,area;//variables for storing radius and area\r\n" +
                   "   printf(\"Enter the radius of the circle whose area is to be calculated\\n\");\r\n" +
                   "   scanf(\"%lf\",&radius);//entering the value for radius of the circle as float data type\r\n" +
                   "   area=(22.0/7.0)*pow(radius,2);//Mathematical function pow is used to calculate square of radius\r\n" +
                   "   printf(\"The area of the circle is %lf\",area);//displaying the results\r\n" +
                   "   getch();\r\n" +
                   "}\r\n" +
                   "/*A test run for the program was carried out and following output was observed\r\n" +
                   "If 50 is the radius of the circle whose area is to be calculated\r\n" +
                   "The area of the circle is 7857.1429*/";
    if (sFile == null)
    {
      final StringBuilder aSB = new StringBuilder ();
      final BufferedReader aBR = new BufferedReader (new InputStreamReader (System.in));
      String s;
      while ((s = aBR.readLine ()) != null)
        aSB.append (s).append ('\n');

      sFile = aSB.toString ();
    }

    final Pattern p = Pattern.compile ("(/\\*(?:[^*]|(?:\\*+[^*/]))*\\*/|//.*)");
    final Matcher m = p.matcher (sFile);
    if (m.find ())
    {
      final int gc = m.groupCount ();
      for (int i = 0; i < gc; ++i)
        System.out.println (m.group (i + 1));
    }
  }
}
