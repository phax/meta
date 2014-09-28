package com.helger.meta.asm.translation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.io.file.iterate.FileSystemRecursiveIterator;
import com.helger.commons.lang.CGStringHelper;
import com.helger.commons.text.impl.TextProvider;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.EProject;
import com.helger.meta.asm.ASMUtils;

public final class MainExtractTranslatableStrings extends AbstractProjectMain
{
  public static final class ExtractItem
  {
    private final String m_sID;
    private final String m_sDE;
    private final String m_sEN;

    public ExtractItem (@Nonnull final String sID, @Nonnull final String sDE, @Nonnull final String sEN)
    {
      m_sID = sID;
      m_sDE = sDE;
      m_sEN = sEN;
    }

    @Nonnull
    public String getID ()
    {
      return m_sID;
    }

    @Nonnull
    public String getDE ()
    {
      return m_sDE;
    }

    @Nonnull
    public String getEN ()
    {
      return m_sEN;
    }
  }

  @Nonnull
  @ReturnsMutableCopy
  private static List <ExtractItem> _extractFromFile (@Nonnull final EProject eProject, @Nonnull final ClassNode cn)
  {
    final List <ExtractItem> ret = new ArrayList <ExtractItem> ();

    // First extract static and non-static fields
    final List <String> aAllEnumConstants = new ArrayList <String> ();
    final List <String> aAllFields = new ArrayList <String> ();
    for (final Object oField : cn.fields)
    {
      final FieldNode fn = (FieldNode) oField;
      if (Modifier.isStatic (fn.access))
      {
        // $ is used for compiler generated constants
        if (fn.name.contains ("$"))
          aAllEnumConstants.add (fn.name);
      }
      else
      {
        aAllFields.add (fn.name);
        if (fn.name.equals ("m_aTP"))
        {
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
        }
      }
    }
    return ret;
  }

  private static void _scanProject (@Nonnull final EProject eProject) throws IOException
  {
    if (false)
      s_aLogger.info ("  " + eProject.getProjectName ());
    final File aTargetDir = new File (eProject.getBaseDir (), "target/classes").getCanonicalFile ();
    final String sTargetDir = aTargetDir.getAbsolutePath ();

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
            _extractFromFile (eProject, cn);
          }
        }
      }
  }

  public static void main (final String [] args) throws IOException
  {
    s_aLogger.info ("Start extracting text from .class files!");
    for (final EProject eProject : EProject.values ())
      if (eProject.getProjectType ().hasJavaCode ())
        _scanProject (eProject);
    s_aLogger.info ("Done - " + getWarnCount () + " warning(s)");
  }
}
