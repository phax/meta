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
package com.helger.meta.translation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.helger.commons.io.file.SimpleFileIO;
import com.helger.commons.io.file.iterate.FileSystemRecursiveIterator;
import com.helger.commons.lang.CGStringHelper;
import com.helger.commons.text.impl.TextProvider;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.CMeta;
import com.helger.meta.EProject;
import com.helger.meta.asm.ASMUtils;

public final class MainExtractTranslatableStrings extends AbstractProjectMain
{
  private static Set <String> s_aActions = new LinkedHashSet <String> ();

  @Nullable
  private static StringTable _extractSTFromFile (@Nonnull final EProject eProject, @Nonnull final ClassNode cn)
  {
    final StringTable ret = new StringTable ();

    // First extract static and non-static fields
    boolean m_bHasTP = false;
    for (final Object oField : cn.fields)
    {
      final FieldNode fn = (FieldNode) oField;
      if (!Modifier.isStatic (fn.access))
      {
        if (fn.name.equals ("m_aTP"))
        {
          m_bHasTP = true;
          // We have the right field
          if (fn.desc.equals (Type.getDescriptor (TextProvider.class)))
          {
            if (!Modifier.isFinal (fn.access))
              _warn (eProject, cn.name + " field m_aTP is not final");
          }
          else
          {
            _warn (eProject, cn.name +
                             " field m_aTP is of wrong type " +
                             Type.getType (fn.desc) +
                             " and not of type TextProvider");
          }
        }
        else
        {
          // Translation enums may not have any other member, because
          // this indicates a potentially invalid translation table!
          _warn (eProject, cn.name +
                           " field " +
                           fn.name +
                           " of type " +
                           Type.getType (fn.desc) +
                           " should not be in a translatable enum - remove this member");
          return null;
        }
      }
    }

    // Small checks
    if (!m_bHasTP)
      _warn (eProject, cn.name + " is missing the standard m_aTP field");

    // Find the constructor
    final MethodNode aCtor = ASMUtils.findMethod (cn, "<init>");
    if (!ASMUtils.containsStaticCall (aCtor, TextProvider.class))
      _warn (eProject, cn.name + " should use the TextProvider static factory methods in the constructor");

    // Second find the initialization calls in the static ctor
    final MethodNode aStaticInit = ASMUtils.findMethod (cn, "<clinit>");
    final List <String> aAllConstantStrings = new ArrayList <String> ();
    final String sIDPrefix = CGStringHelper.getClassFromPath (cn.name) + ".";
    // static initializer
    final Iterator <?> aInstructionIter = aStaticInit.instructions.iterator ();
    while (aInstructionIter.hasNext ())
    {
      final AbstractInsnNode in = (AbstractInsnNode) aInstructionIter.next ();
      if (in instanceof LdcInsnNode)
      {
        // Load constant node
        final Object aConstant = ((LdcInsnNode) in).cst;
        // This is always supposed to be a String but may not be, in case any
        // other field and therefore any other argument is present
        aAllConstantStrings.add ((String) aConstant);

        if (aAllConstantStrings.size () == 3)
        {
          // We have ID, DE and EN texts
          final String sID = sIDPrefix + aAllConstantStrings.get (0);
          ret.setText (sID, TextProvider.DE, aAllConstantStrings.get (1));
          ret.setText (sID, TextProvider.EN, aAllConstantStrings.get (2));
          aAllConstantStrings.clear ();
        }
      }
    }

    if (!aAllConstantStrings.isEmpty ())
    {
      _warn (eProject, cn.name + " contains a weird number of constants. Left: " + aAllConstantStrings);
      // Avoid serious damage
      return null;
    }

    return ret;
  }

  private static void _scanProject (@Nonnull final EProject eProject) throws IOException
  {
    if (false)
      s_aLogger.info ("  " + eProject.getProjectName ());
    final File aTargetDir = new File (eProject.getBaseDir (), "target/classes").getCanonicalFile ();

    final StringTable aSTProject = new StringTable ();

    // Find all class files
    for (final File aClassFile : new FileSystemRecursiveIterator (aTargetDir))
      if (aClassFile.isFile () && aClassFile.getName ().endsWith (".class"))
      {
        // Interprete byte code
        final ClassNode cn = ASMUtils.readClassFile (aClassFile);
        final boolean bIsEnum = CGStringHelper.getPathFromClass (Enum.class).equals (cn.superName);
        if (bIsEnum)
        {
          // Okay, it's an enum
          final boolean bIsRelevant = ASMUtils.containsRequiresTranslationAnnotation (cn) &&
                                      !ASMUtils.containsNoTranslationRequiredAnnotation (cn);
          if (bIsRelevant)
          {
            // Enum and annotated
            final StringTable aSTFile = _extractSTFromFile (eProject, cn);
            if (aSTFile != null)
              aSTProject.addAll (aSTFile);
          }
        }
      }

    if (!aSTProject.isEmpty ())
    {
      final File aDstFileXML = new File (eProject.getBaseDir (),
                                         "src/main/resources/translation/translatable-texts.xml");
      if (StringTableSerializer.writeStringTableAsXML (aDstFileXML, aSTProject).isSuccess ())
        s_aActions.add ("cd " + eProject.getProjectName () + " && call mvn license:format && cd..");
      else
        _warn (eProject, "Failed to writing translatable-texts.xml");
    }
  }

  public static void main (final String [] args) throws IOException
  {
    s_aLogger.info ("Start extracting text from .class files!");
    for (final EProject eProject : EProject.values ())
      if (eProject.getProjectType ().hasJavaCode ())
        _scanProject (eProject);
    s_aLogger.info ("Done - " + getWarnCount () + " warning(s)");
    if (!s_aActions.isEmpty ())
    {
      final StringBuilder aSB = new StringBuilder (BATCH_HEADER);
      for (final String sAction : s_aActions)
        aSB.append (sAction).append ('\n');
      aSB.append (BATCH_FOOTER);
      final File aDestFile = new File (CMeta.GIT_BASE_DIR, "translation-actions.cmd");
      SimpleFileIO.writeFile (aDestFile, aSB.toString (), BATCH_CHARSET);
      s_aLogger.info ("Execute " + aDestFile.getAbsolutePath ());
    }
  }
}
