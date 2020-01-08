/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
package com.helger.meta.compilererror;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * This is a dummy class to test for generic exceptions in combinations with
 * Lambdas<br>
 * Wait for http://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8164613
 *
 * @author Philip Helger
 */
public class LambdaExceptionDeductionFuncTest
{
  public static interface IBaseGetter
  {
    String get () throws Exception;
  }

  public static interface IBaseGetterManual extends IBaseGetter
  {
    String get () throws IOException;
  }

  public static interface IThrowingGetter <EX extends Exception> extends IBaseGetter
  {
    String get () throws EX;
  }

  public static interface IThrowingGetterWorking <EX extends Exception>
  {
    String get () throws EX;
  }

  public static void main (final String [] args) throws IOException
  {
    final InputStream aIS = new ByteArrayInputStream ("abc".getBytes (StandardCharsets.UTF_8));
    // This fails when compiling on the commandline!
    if (false)
    {
      final IThrowingGetter <IOException> aGetter = () -> Character.toString ((char) aIS.read ());
      aGetter.get ();
    }
    final IBaseGetterManual aGetterManual = () -> Character.toString ((char) aIS.read ());
    aGetterManual.get ();
    final IThrowingGetterWorking <IOException> aGetterWorking = () -> Character.toString ((char) aIS.read ());
    aGetterWorking.get ();
  }
}
