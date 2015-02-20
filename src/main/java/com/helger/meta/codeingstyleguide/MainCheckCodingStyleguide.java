/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.meta.codeingstyleguide;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Locale;

import javax.annotation.Nonnull;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import com.helger.commons.annotations.CodingStyleguideUnaware;
import com.helger.commons.io.file.FileUtils;
import com.helger.commons.io.file.iterate.FileSystemRecursiveIterator;
import com.helger.commons.lang.CGStringHelper;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.asm.ASMUtils;
import com.helger.meta.project.EProject;
import com.helger.meta.project.EProjectType;
import com.helger.meta.project.IProject;

public final class MainCheckCodingStyleguide extends AbstractProjectMain
{
  private static final Locale LOCALE_SYSTEM = Locale.US;

  private static void _checkClass (@Nonnull final IProject eProject, @Nonnull final ClassNode cn)
  {
    final String sClassLocalName = CGStringHelper.getClassLocalName (CGStringHelper.getClassFromPath (cn.name));
    final boolean bIsSpecialCase = sClassLocalName.equals ("package-info");
    if (bIsSpecialCase)
      return;

    final String sPrefix = "[" + sClassLocalName + "] ";
    final boolean bClassIsInterface = Modifier.isInterface (cn.access);
    final boolean bClassIsAnnotation = (cn.access & Opcodes.ACC_ANNOTATION) != 0;

    if (bClassIsInterface)
    {
      if (bClassIsAnnotation)
      {}
      else
      {
        if (sClassLocalName.startsWith ("I"))
        {
          if (sClassLocalName.length () > 1 && !Character.isUpperCase (sClassLocalName.charAt (1)))
            _warn (eProject, sPrefix + "Interface names should have an upper case second letter");
        }
        else
          if (!sClassLocalName.contains ("$I") && !sClassLocalName.endsWith ("MBean"))
            _warn (eProject, sPrefix + "Interface names should start with an uppercase 'I'");
      }
    }
  }

  private static void _checkMethods (@Nonnull final IProject eProject, @Nonnull final ClassNode cn)
  {
    final String sClassLocalName = CGStringHelper.getClassLocalName (CGStringHelper.getClassFromPath (cn.name));
    final boolean bIsSpecialCase = false;
    if (bIsSpecialCase)
      return;

    final boolean bClassIsFinal = Modifier.isFinal (cn.access);

    for (final Object oMethod : cn.methods)
    {
      final MethodNode mn = (MethodNode) oMethod;

      if (ASMUtils.containsAnnotation (mn, CodingStyleguideUnaware.class))
        continue;

      final String sPrefix = "[" + sClassLocalName + "::" + mn.name + "] ";

      final boolean bIsConstructor = mn.name.equals ("<init>");
      final boolean bIsPrivate = Modifier.isPrivate (mn.access);
      final boolean bIsFinal = Modifier.isFinal (mn.access);

      if (bIsPrivate)
      {
        if (!bIsConstructor &&
            !mn.name.startsWith ("_") &&
            !mn.name.equals ("readObject") &&
            !mn.name.equals ("writeObject") &&
            !mn.name.equals ("readResolve") &&
            !mn.name.startsWith ("lambda$"))
          _warn (eProject, sPrefix + "Privat methods should start with an underscore");
      }

      if (bIsFinal)
      {
        if (bClassIsFinal)
          _warn (eProject, sPrefix + "final method in final class");
      }
    }
  }

  private static void _checkVariables (@Nonnull final IProject eProject, @Nonnull final ClassNode cn)
  {
    final String sClassLocalName = CGStringHelper.getClassLocalName (CGStringHelper.getClassFromPath (cn.name));
    final boolean bIsSpecialCase = (eProject.getProjectType () == EProjectType.MAVEN_PLUGIN && sClassLocalName.endsWith ("Mojo"));
    if (bIsSpecialCase)
      return;

    final String sPrefix = "[" + sClassLocalName + "] ";
    final boolean bClassIsFinal = Modifier.isFinal (cn.access);

    for (final Object oField : cn.fields)
    {
      final FieldNode fn = (FieldNode) oField;

      if (ASMUtils.containsAnnotation (fn, CodingStyleguideUnaware.class))
        continue;

      final boolean bIsStatic = Modifier.isStatic (fn.access);
      final boolean bIsFinal = Modifier.isFinal (fn.access);
      final boolean bIsPrivate = Modifier.isPrivate (fn.access);

      if (bIsStatic)
      {
        // Internal variable names
        if (fn.name.startsWith ("$SWITCH_TABLE$") ||
            fn.name.equals ("$assertionsDisabled") ||
            fn.name.startsWith ("$SwitchMap$"))
          continue;

        if (bIsFinal)
        {
          if (!fn.name.startsWith ("s_") &&
              !fn.name.equals (fn.name.toUpperCase (LOCALE_SYSTEM)) &&
              !fn.name.equals ("serialVersionUID"))
            _warn (eProject, sPrefix + "Static final member name '" + fn.name + "' does not match");
        }
        else
        {
          if (!fn.name.startsWith ("s_"))
            _warn (eProject, sPrefix + "Static member name '" + fn.name + "' does not match");

          if (!bIsPrivate)
            _warn (eProject, sPrefix + "Static member '" + fn.name + "' is not private");
        }
      }
      else
      {
        if (fn.name.startsWith ("this$") || fn.name.startsWith ("val$"))
          continue;

        if (!fn.name.startsWith ("m_"))
          _warn (eProject, sPrefix + "Instance member name '" + fn.name + "' does not match");

        if (bClassIsFinal && !bIsPrivate)
          _warn (eProject, sPrefix + "Instance member '" + fn.name + "' is not private");
      }
    }
  }

  private static void _scanProject (@Nonnull final IProject eProject) throws IOException
  {
    if (false)
      s_aLogger.info ("  " + eProject.getProjectName ());
    final File aTargetDir = FileUtils.getCanonicalFile (new File (eProject.getBaseDir (), "target/classes"));

    // Find all class files
    for (final File aClassFile : new FileSystemRecursiveIterator (aTargetDir))
      if (aClassFile.isFile () && aClassFile.getName ().endsWith (".class"))
      {
        // Interpret byte code
        final ClassNode cn = ASMUtils.readClassFile (aClassFile);

        // Ignore classes explicitly marked as unaware
        if (ASMUtils.containsAnnotation (cn, CodingStyleguideUnaware.class))
          continue;

        final String sClassLocalName = CGStringHelper.getClassLocalName (CGStringHelper.getClassFromPath (cn.name));

        // Special generated classes
        if (eProject == EProject.PH_CSS &&
            (sClassLocalName.equals ("CharStream") ||
             sClassLocalName.equals ("ParseException") ||
             sClassLocalName.startsWith ("ParserCSS21") ||
             sClassLocalName.startsWith ("ParserCSS30") ||
             sClassLocalName.startsWith ("ParserCSSCharsetDetector") ||
             sClassLocalName.equals ("Token") ||
             sClassLocalName.equals ("TokenMgrError") ||
             sClassLocalName.startsWith ("JJTParser") ||
             sClassLocalName.equals ("Node") ||
             (sClassLocalName.startsWith ("Parser") && sClassLocalName.endsWith ("Constants")) || sClassLocalName.equals ("SimpleNode")))
          continue;

        if (eProject == EProject.PH_JSON &&
            (sClassLocalName.equals ("CharStream") ||
             sClassLocalName.equals ("ParseException") ||
             sClassLocalName.startsWith ("ParserJson") ||
             sClassLocalName.equals ("Token") || sClassLocalName.equals ("TokenMgrError")))
          continue;

        if (eProject == EProject.JCODEMODEL)
          continue;

        _checkClass (eProject, cn);
        _checkVariables (eProject, cn);
        _checkMethods (eProject, cn);
      }
  }

  public static void main (final String [] args) throws IOException
  {
    s_aLogger.info ("Start checking coding style guide in .class files!");
    for (final IProject eProject : EProject.values ())
      if (eProject.getProjectType ().hasJavaCode () &&
          eProject != EProject.PH_JAVACC_MAVEN_PLUGIN &&
          !eProject.isDeprecated ())
        _scanProject (eProject);
    s_aLogger.info ("Done - " + getWarnCount () + " warning(s)");
  }
}
