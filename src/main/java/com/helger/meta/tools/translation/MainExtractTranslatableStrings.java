/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.meta.tools.translation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Iterator;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.lang.clazz.ClassHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsLinkedHashSet;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsOrderedSet;
import com.helger.io.file.FileHelper;
import com.helger.io.file.FileSystemRecursiveIterator;
import com.helger.io.file.SimpleFileIO;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.CMeta;
import com.helger.meta.asm.ASMHelper;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;
import com.helger.meta.translation.StringTable;
import com.helger.meta.translation.StringTableSerializer;
import com.helger.text.IMultilingualText;
import com.helger.text.util.TextHelper;

public final class MainExtractTranslatableStrings extends AbstractProjectMain
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainExtractTranslatableStrings.class);
  private static final ICommonsOrderedSet <String> ACTIONS = new CommonsLinkedHashSet <> ();

  @Nullable
  private static StringTable _extractSTFromFile (@NonNull final IProject eProject, @NonNull final ClassNode cn)
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
          if (fn.desc.equals (Type.getDescriptor (IMultilingualText.class)))
          {
            if (!Modifier.isFinal (fn.access))
              _warn (eProject, cn.name + " field m_aTP is not final");
          }
          else
          {
            _warn (eProject,
                   cn.name +
                             " field m_aTP is of wrong type " +
                             Type.getType (fn.desc) +
                             " and not of type IMultilingualText");
          }
        }
        else
        {
          // Translation enums may not have any other member, because
          // this indicates a potentially invalid translation table!
          _warn (eProject,
                 cn.name +
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
    final MethodNode aCtor = ASMHelper.findMethod (cn, "<init>");
    if (!ASMHelper.containsStaticCall (aCtor, TextHelper.class))
      _warn (eProject, cn.name + " should use the TextHelper static factory methods in the constructor");

    // Second find the initialization calls in the static ctor
    final MethodNode aStaticInit = ASMHelper.findMethod (cn, "<clinit>");
    final ICommonsList <String> aAllConstantStrings = new CommonsArrayList <> ();
    final String sIDPrefix = ClassHelper.getClassFromPath (cn.name) + ".";
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
          ret.setText (sID, TextHelper.DE, aAllConstantStrings.get (1));
          ret.setText (sID, TextHelper.EN, aAllConstantStrings.get (2));
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

  private static void _scanProject (@NonNull final IProject eProject) throws IOException
  {
    if (false)
      LOGGER.info ("  " + eProject.getProjectName ());
    final File aTargetDir = FileHelper.getCanonicalFile (new File (eProject.getBaseDir (), "target/classes"));

    final StringTable aSTProject = new StringTable ();

    // Find all class files
    for (final File aClassFile : new FileSystemRecursiveIterator (aTargetDir))
      if (aClassFile.isFile () && aClassFile.getName ().endsWith (".class"))
      {
        // Interpret byte code
        final ClassNode cn = ASMHelper.readClassFile (aClassFile);
        final boolean bIsEnum = ClassHelper.getPathFromClass (Enum.class).equals (cn.superName);
        if (bIsEnum)
        {
          // Okay, it's an enumeration
          final boolean bIsRelevant = ASMHelper.containsRequiresTranslationAnnotation (cn) &&
                                      !ASMHelper.containsNoTranslationRequiredAnnotation (cn);
          if (bIsRelevant)
          {
            // Enumeration and annotated
            final StringTable aSTFile = _extractSTFromFile (eProject, cn);
            if (aSTFile != null)
              aSTProject.addAll (aSTFile);
          }
        }
      }

    if (!aSTProject.isEmpty () && eProject.isBuildInProject ())
    {
      final File aDstFileXML = new File (eProject.getBaseDir (),
                                         "src/main/resources/translation/translatable-texts.xml");
      if (StringTableSerializer.writeStringTableAsXML (aDstFileXML, aSTProject).isSuccess ())
        ACTIONS.add ("cd " + eProject.getProjectName () + " && call mvn license:format && cd..");
      else
        _warn (eProject, "Failed to writing translatable-texts.xml");
    }
  }

  public static void main (final String [] args) throws IOException
  {
    LOGGER.info ("Start extracting text from .class files!");
    for (final IProject aProject : ProjectList.getAllProjects (p -> p.getProjectType ().hasJavaCode () &&
                                                                    !p.isDeprecated ()))
      _scanProject (aProject);
    LOGGER.info ("Done - " + getWarnCount () + " warning(s)");
    if (!ACTIONS.isEmpty ())
    {
      final StringBuilder aSB = new StringBuilder (BATCH_HEADER);
      for (final String sAction : ACTIONS)
        aSB.append (sAction).append ('\n');
      aSB.append (BATCH_FOOTER);
      final File aDestFile = new File (CMeta.GIT_BASE_DIR, "translation-actions.cmd");
      SimpleFileIO.writeFile (aDestFile, aSB.toString (), BATCH_CHARSET);
      LOGGER.info ("Execute " + aDestFile.getAbsolutePath ());
    }
  }
}
