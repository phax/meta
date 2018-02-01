/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
