/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.meta.tools.sysinfo;

import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

import com.helger.base.string.StringParser;

public class MainListSecurity
{
  public static void main (final String [] args)
  {
    final List <Provider> aSortedProviders = Arrays.asList (Security.getProviders ());
    Collections.sort (aSortedProviders,
                      Comparator.comparing (Provider::getName)
                                .thenComparing (Comparator.comparingDouble (p -> StringParser.parseDouble (p.getVersionStr (),
                                                                                                           -1))));

    // show all providers
    System.out.println ("All security providers");
    System.out.println ("  Name\tVersion\tInfo");

    for (final Provider aSecurityProvider : aSortedProviders)
      System.out.println ("    " +
                          aSecurityProvider.getName () +
                          "\t" +
                          aSecurityProvider.getVersionStr () +
                          "\t" +
                          aSecurityProvider.getInfo ());

    // Show all algorithms of all providers
    System.out.println ("All algorithms");
    System.out.println ("  Provider\tType\tAlgorithm\tClass name");
    for (final Provider aSecurityProvider : aSortedProviders)
    {
      final String sProviderName = aSecurityProvider.getName () + " " + aSecurityProvider.getVersionStr ();

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
      final String sProviderName = aSecurityProvider.getName () + " " + aSecurityProvider.getVersionStr ();

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
          catch (final Exception ex)
          {
            System.out.println ();
            ex.printStackTrace ();
          }
        }
    }
  }
}
