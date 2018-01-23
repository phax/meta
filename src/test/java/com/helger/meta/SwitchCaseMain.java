package com.helger.meta;

public class SwitchCaseMain
{
  public static void main (final String [] args)
  {
    final int i = 2;
    switch (i)
    {
      case 2:
      {
        final long x = i * 2;
        System.out.println ("Ich bin " + x / 2);
      }
      case 1:
        final long y = i * 2;
        System.out.println ("Ich bin " + y / 2);
      case 3:
        System.out.println ("Ich bin 3");
      default:
        System.out.println ("Ich bin alles andere");
    }
  }
}
