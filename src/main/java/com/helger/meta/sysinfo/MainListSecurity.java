package com.helger.meta.sysinfo;

import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

public class MainListSecurity
{
  public static void main (final String [] args)
  {
    final List <Provider> aSortedProviders = Arrays.asList (Security.getProviders ());
    Collections.sort (aSortedProviders, new Comparator <Provider> ()
    {
      @Override
      public int compare (final Provider aElement1, final Provider aElement2)
      {
        int ret = aElement1.getName ().compareTo (aElement2.getName ());
        if (ret == 0)
          ret = Double.compare (aElement1.getVersion (), aElement2.getVersion ());
        return ret;
      }
    });

    // show all providers
    System.out.println ("All security providers");
    System.out.println ("  Name\tVersion\tInfo");

    for (final Provider aSecurityProvider : aSortedProviders)
      System.out.println ("    " +
                          aSecurityProvider.getName () +
                          "\t" +
                          aSecurityProvider.getVersion () +
                          "\t" +
                          aSecurityProvider.getInfo ());

    // Show all algorithms of all providers
    System.out.println ("All algorithms");
    System.out.println ("  Provider\tType\tAlgorithm\tClass name");
    for (final Provider aSecurityProvider : aSortedProviders)
    {
      final String sProviderName = aSecurityProvider.getName () + " " + aSecurityProvider.getVersion ();

      for (final Service aService : aSecurityProvider.getServices ())
        System.out.println ("    " +
                            sProviderName +
                            "\t" +
                            aService.getType () +
                            "\t" +
                            aService.getAlgorithm () +
                            "\t" +
                            aService.getClassName ());
    }

    // List details of all SSLContexts
    System.out.println ("All SSLContexts");
    System.out.println ("  Provider\tType\tAlgorithm\tDefault protocols\tDefault cipher suites\tSupported protocols\tSupported cipher suites");
    for (final Provider aSecurityProvider : aSortedProviders)
    {
      final String sProviderName = aSecurityProvider.getName () + " " + aSecurityProvider.getVersion ();

      for (final Service aService : aSecurityProvider.getServices ())
        if ("SSLContext".equals (aService.getType ()))
        {
          System.out.print ("    " + sProviderName + "\t" + aService.getAlgorithm ());
          try
          {
            final SSLContext aSSLCtx = SSLContext.getInstance (aService.getAlgorithm ());
            if (!"Default".equals (aService.getAlgorithm ()))
            {
              // Default SSLContext is initialized automatically
              aSSLCtx.init (null, null, null);
            }
            final SSLParameters aDefParams = aSSLCtx.getDefaultSSLParameters ();
            final SSLParameters aSuggParams = aSSLCtx.getSupportedSSLParameters ();
            System.out.println ("\t" +
                                Arrays.toString (aDefParams.getProtocols ()) +
                                "\t" +
                                Arrays.toString (aDefParams.getCipherSuites ()) +
                                "\t" +
                                Arrays.toString (aSuggParams.getProtocols ()) +
                                "\t" +
                                Arrays.toString (aSuggParams.getCipherSuites ()));
          }
          catch (final Throwable ex)
          {
            System.out.println ();
            ex.printStackTrace ();
          }
        }
    }
  }
}
