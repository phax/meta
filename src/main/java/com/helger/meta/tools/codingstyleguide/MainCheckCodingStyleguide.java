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
package com.helger.meta.tools.codingstyleguide;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.ReturnsImmutableObject;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.file.FileSystemRecursiveIterator;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.state.EContinue;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.asm.ASMHelper;
import com.helger.meta.project.EProject;
import com.helger.meta.project.EProjectDeprecated;
import com.helger.meta.project.EProjectType;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

public final class MainCheckCodingStyleguide extends AbstractProjectMain
{
  private static final Locale LOCALE_SYSTEM = Locale.US;

  private static void _checkClassNaming (@Nonnull final IProject aProject, @Nonnull final ClassNode cn)
  {
    final String sClassLocalName = ClassHelper.getClassLocalName (ClassHelper.getClassFromPath (cn.name));
    final boolean bIsSpecialCase = sClassLocalName.equals ("package-info");
    if (bIsSpecialCase)
      return;

    String sInnerClassLocalName = StringHelper.getFromLastExcl (sClassLocalName, '$');
    boolean bIsAnonymousInnerClass = false;
    if (sInnerClassLocalName == null)
      sInnerClassLocalName = sClassLocalName;
    else
      if (StringParser.isUnsignedInt (sInnerClassLocalName))
      {
        // It's an anonymous inner class - use the full name
        bIsAnonymousInnerClass = true;
        if (false)
          sInnerClassLocalName = sClassLocalName;
      }

    final String sPrefix = "[" + sClassLocalName + "] ";
    final boolean bClassIsAbstract = Modifier.isAbstract (cn.access);
    final boolean bClassIsAnnotation = (cn.access & Opcodes.ACC_ANNOTATION) != 0;
    final boolean bClassIsEnum = (cn.access & Opcodes.ACC_ENUM) != 0;
    final boolean bClassIsInterface = Modifier.isInterface (cn.access);

    if (!Character.isUpperCase (sClassLocalName.charAt (0)))
      _warn (aProject,
             sPrefix + "Class/interface/enum/annotation names should always start with an uppercase character");

    if (!bIsAnonymousInnerClass)
    {
      if (bClassIsInterface)
      {
        if (bClassIsAnnotation)
        {
          // TODO
        }
        else
        {
          if (sInnerClassLocalName.startsWith ("I"))
          {
            if (sInnerClassLocalName.length () > 1 && !Character.isUpperCase (sInnerClassLocalName.charAt (1)))
              _warn (aProject, sPrefix + "Interface names should have an upper case second letter");
          }
          else
            if (!sInnerClassLocalName.startsWith ("I") && !sClassLocalName.endsWith ("MBean"))
              _warn (aProject, sPrefix + "Interface names should start with an uppercase 'I'");
        }
      }
      else
      {
        if (bClassIsEnum)
        {
          if (!sInnerClassLocalName.startsWith ("E"))
            _warn (aProject, sPrefix + "enum classes should start with 'E'");
        }
        else
        {
          if (bClassIsAbstract)
          {
            if (!sInnerClassLocalName.startsWith ("Abstract") && !sInnerClassLocalName.equals ("NamespacePrefixMapper"))
              _warn (aProject, sPrefix + "Abstract classes should start with 'Abstract'");
          }
        }
      }
    }

    if (sInnerClassLocalName.startsWith ("Abstract") && !bClassIsAbstract && !sInnerClassLocalName.endsWith ("Test"))
      _warn (aProject, sPrefix + "Class name denotes an abstract class but the class is not abstract!");

    if (sInnerClassLocalName.contains ("Readonly"))
      _warn (aProject, sPrefix + "'read-only' should be spelled 'ReadOnly'");
    if (sInnerClassLocalName.endsWith ("Utils"))
      _warn (aProject, sPrefix + "Please make the *Utils class a *Helper class");
    if (sInnerClassLocalName.contains ("Masterdata"))
      _warn (aProject, sPrefix + "'master data' should be spelled 'MasterData'");
    if (sInnerClassLocalName.contains ("MultiLingual"))
      _warn (aProject, sPrefix + "'multilingual' should be spelled 'Multilingual'");
  }

  private static boolean _isArrayClass (@Nonnull final Type aType)
  {
    return aType.getSort () == Type.ARRAY;
  }

  private static boolean _isJDKCollectionClass (@Nonnull final Type aType)
  {
    if (aType.getSort () != Type.OBJECT)
      return false;

    final String sClassName = aType.getClassName ();
    return java.util.Collection.class.getName ().equals (sClassName) ||
           // list
           java.util.List.class.getName ().equals (sClassName) ||
           java.util.ArrayList.class.getName ().equals (sClassName) ||
           java.util.Vector.class.getName ().equals (sClassName) ||
           java.util.LinkedList.class.getName ().equals (sClassName) ||
           java.util.Stack.class.getName ().equals (sClassName) ||
           java.util.concurrent.CopyOnWriteArrayList.class.getName ().equals (sClassName) ||
           // set
           java.util.Set.class.getName ().equals (sClassName) ||
           java.util.NavigableSet.class.getName ().equals (sClassName) ||
           java.util.SortedSet.class.getName ().equals (sClassName) ||
           // java.util.EnumSet.class.getName ().equals (sClassName) ||
           java.util.HashSet.class.getName ().equals (sClassName) ||
           java.util.LinkedHashSet.class.getName ().equals (sClassName) ||
           java.util.TreeSet.class.getName ().equals (sClassName) ||
           // Map
           java.util.Map.class.getName ().equals (sClassName) ||
           java.util.NavigableMap.class.getName ().equals (sClassName) ||
           java.util.SortedMap.class.getName ().equals (sClassName) ||
           java.util.HashMap.class.getName ().equals (sClassName) ||
           java.util.IdentityHashMap.class.getName ().equals (sClassName) ||
           java.util.WeakHashMap.class.getName ().equals (sClassName) ||
           java.util.LinkedHashMap.class.getName ().equals (sClassName) ||
           java.util.TreeMap.class.getName ().equals (sClassName) ||
           java.util.Hashtable.class.getName ().equals (sClassName) ||
           java.util.EnumMap.class.getName ().equals (sClassName) ||
           java.util.Properties.class.getName ().equals (sClassName) ||
           java.util.concurrent.ConcurrentHashMap.class.getName ().equals (sClassName) ||
           // Queue
           java.util.Queue.class.getName ().equals (sClassName) ||
           java.util.Deque.class.getName ().equals (sClassName) ||
           java.util.PriorityQueue.class.getName ().equals (sClassName) ||
           java.util.concurrent.LinkedBlockingDeque.class.getName ().equals (sClassName) ||
           java.util.concurrent.LinkedBlockingQueue.class.getName ().equals (sClassName) ||
           java.util.concurrent.LinkedTransferQueue.class.getName ().equals (sClassName);
  }

  private static boolean _isPhCollectionClass (@Nonnull final Type aType)
  {
    if (aType.getSort () != Type.OBJECT)
      return false;

    final String sClassName = aType.getClassName ();
    final String sPackageName = ClassHelper.getClassPackageName (sClassName);
    return "com.helger.commons.collection.ext".equals (sPackageName) ||
           "com.helger.commons.collection.impl".equals (sPackageName);
  }

  private static void _checkMainMethods (@Nonnull final IProject aProject, @Nonnull final ClassNode cn)
  {
    final String sClassLocalName = ClassHelper.getClassLocalName (ClassHelper.getClassFromPath (cn.name));
    final boolean bIsSpecialCase = aProject.getProjectType () == EProjectType.MAVEN_PLUGIN &&
                                   sClassLocalName.equals ("HelpMojo");
    if (bIsSpecialCase)
      return;

    final boolean bClassIsAbstract = Modifier.isAbstract (cn.access);
    final boolean bClassIsEnum = (cn.access & Opcodes.ACC_ENUM) != 0;
    final boolean bClassIsFinal = Modifier.isFinal (cn.access);
    final boolean bClassIsInterface = Modifier.isInterface (cn.access);

    final ICommonsList <MethodNode> aAllCtors = new CommonsArrayList <> ();
    for (final Object oMethod : cn.methods)
    {
      final MethodNode mn = (MethodNode) oMethod;

      if (ASMHelper.containsAnnotation (mn, CodingStyleguideUnaware.class))
        continue;

      if (mn.name.startsWith ("$SWITCH_TABLE$"))
        continue;

      final String sPrefix = "[" + sClassLocalName + "::" + mn.name + "] ";

      final Type aReturnType = Type.getReturnType (mn.desc);
      final boolean bReturnsArray = _isArrayClass (aReturnType);
      final boolean bReturnsJdkCollection = _isJDKCollectionClass (aReturnType);
      final boolean bReturnsCollection = bReturnsJdkCollection || _isPhCollectionClass (aReturnType);
      final boolean bIsConstructor = mn.name.equals ("<init>");
      final boolean bIsPrivate = Modifier.isPrivate (mn.access);
      final boolean bIsFinal = Modifier.isFinal (mn.access);

      if (bIsConstructor)
        aAllCtors.add (mn);

      if (bIsPrivate)
      {
        if (!bIsConstructor &&
            !mn.name.startsWith ("_") &&
            !mn.name.equals ("readObject") &&
            !mn.name.equals ("readResolve") &&
            !mn.name.equals ("writeObject") &&
            !mn.name.equals ("writeReplace") &&
            !mn.name.startsWith ("lambda$") &&
            !mn.name.endsWith ("$deserializeLambda$"))
          _warn (aProject, sPrefix + "Private methods should start with an underscore");
      }

      if (bIsFinal)
      {
        if (bClassIsFinal)
          _warn (aProject, sPrefix + "final method in final class");

        if (ASMHelper.containsAnnotation (mn, OverrideOnDemand.class))
          _warn (aProject, sPrefix + "final method uses @OverrideOnDemand annotation");
      }
      else
      {
        if (bClassIsFinal && ASMHelper.containsAnnotation (mn, OverrideOnDemand.class))
          _warn (aProject, sPrefix + "final class uses @OverrideOnDemand annotation");
      }

      // Too many variations
      if (false)
      {
        if (bReturnsArray)
        {
          if (!mn.name.startsWith ("_") &&
              !mn.name.startsWith ("new") &&
              !mn.name.startsWith ("getAll") &&
              !mn.name.startsWith ("getAs") &&
              !mn.name.startsWith ("internalGetAll") &&
              !mn.name.contains ("::lambda$") &&
              !mn.name.equals ("values") &&
              !mn.name.equals ("toArray"))
            _warn (aProject, sPrefix + "returns a array but uses a non-standard name");
        }
        else
          if (bReturnsCollection)
          {
            if (!mn.name.startsWith ("_") &&
                !mn.name.startsWith ("new") &&
                !mn.name.startsWith ("getAll") &&
                !mn.name.startsWith ("directGetAll") &&
                !mn.name.startsWith ("internalGetAll") &&
                !mn.name.startsWith ("readAll") &&
                !mn.name.contains ("::lambda$") &&
                !mn.name.equals ("keySet") &&
                !mn.name.equals ("values") &&
                !mn.name.equals ("entrySet"))
              _warn (aProject, sPrefix + "returns a collection but uses a non-standard name");
          }
      }

      // Fails to often but may give a nice overview
      if (false)
        if (bReturnsArray || bReturnsCollection)
        {
          // Special name checks
          if (!mn.name.equals ("values"))
            if (!ASMHelper.containsAnnotation (mn, ReturnsMutableCopy.class) &&
                !ASMHelper.containsAnnotation (mn, ReturnsMutableObject.class) &&
                !ASMHelper.containsAnnotation (mn, ReturnsImmutableObject.class))
              _warn (aProject,
                     sPrefix +
                               "returns a collection/array and therefore should be annotated with @ReturnsMutableCopy/@ReturnsMutableObject/@ReturnsImmutableObject");
        }

      if (false)
        if (bReturnsJdkCollection)
          if (!mn.name.equals ("getAsUnmodifiable"))
            _warn (aProject,
                   sPrefix + "returns a JDK Collection (" + mn.desc + ") - consider returning an ICommons* collection");
    }

    if (bClassIsAbstract && !bClassIsInterface && !bClassIsEnum)
    {
      boolean bAnyNonPrivateCtor = false;
      for (final MethodNode aCtor : aAllCtors)
        if (!Modifier.isPrivate (aCtor.access))
        {
          bAnyNonPrivateCtor = true;
          break;
        }
      if (!bAnyNonPrivateCtor)
        _warn (aProject, "[" + sClassLocalName + "] The abstract class contains only private constructors!");
    }
  }

  private static void _checkMainVariables (@Nonnull final IProject aProject, @Nonnull final ClassNode cn)
  {
    final String sClassLocalName = ClassHelper.getClassLocalName (ClassHelper.getClassFromPath (cn.name));
    final boolean bIsSpecialCase = (aProject.getProjectType () == EProjectType.MAVEN_PLUGIN &&
                                    sClassLocalName.endsWith ("Mojo"));
    if (bIsSpecialCase)
      return;

    final String sPrefix = "[" + sClassLocalName + "] ";
    final boolean bClassIsFinal = Modifier.isFinal (cn.access);

    for (final Object oField : cn.fields)
    {
      final FieldNode fn = (FieldNode) oField;

      if (ASMHelper.containsAnnotation (fn, CodingStyleguideUnaware.class))
        continue;

      final boolean bIsStatic = Modifier.isStatic (fn.access);
      final boolean bIsFinal = Modifier.isFinal (fn.access);
      final boolean bIsPrivate = Modifier.isPrivate (fn.access);
      final boolean bIsJdkCollection = _isJDKCollectionClass (Type.getType (fn.desc));

      if (bIsStatic)
      {
        // Internal generated variable names
        if (fn.name.startsWith ("$SWITCH_TABLE$") ||
            fn.name.equals ("$assertionsDisabled") ||
            fn.name.startsWith ("$SwitchMap$"))
          continue;

        if (bIsFinal)
        {
          if (!fn.name.startsWith ("s_") &&
              !fn.name.equals (fn.name.toUpperCase (LOCALE_SYSTEM)) &&
              !fn.name.equals ("serialVersionUID"))
            _warn (aProject, sPrefix + "Static final member name '" + fn.name + "' does not match naming conventions");
        }
        else
        {
          if (!fn.name.startsWith ("s_"))
            _warn (aProject,
                   sPrefix + "Static non-final member name '" + fn.name + "' does not match naming conventions");

          if (!bIsPrivate)
            _warn (aProject, sPrefix + "Static non-final member '" + fn.name + "' is not private");
        }
      }
      else
      {
        // Internal generated variable names
        if (fn.name.startsWith ("this$") || fn.name.startsWith ("val$"))
          continue;

        if (!fn.name.startsWith ("m_"))
          _warn (aProject, sPrefix + "Instance member name '" + fn.name + "' does not match naming conventions");

        if (bClassIsFinal && !bIsPrivate)
          _warn (aProject, sPrefix + "Instance member '" + fn.name + "' is not private");
      }

      if (bIsJdkCollection)
        _warn (aProject,
               sPrefix + "Member '" + fn.name + "' is a JDK Collection - consider using an ICommons* collection");
    }
  }

  @Nonnull
  private static EContinue _doScanMainClass (@Nonnull final IProject aProject,
                                             @Nonnull final String sPackageName,
                                             @Nonnull @Nonempty final String sClassLocalName)
  {
    if (aProject == EProject.ERECHNUNG_WS_CLIENT &&
        (sPackageName.equals ("at.gv.brz.eproc.erb.ws.documentupload._20121205") ||
         sPackageName.equals ("at.gv.brz.eproc.erb.ws.invoicedelivery._201306") ||
         sPackageName.equals ("at.gv.brz.eproc.erb.ws.invoicedeliverycallback._201305") ||
         sPackageName.equals ("at.gv.brz.schema.eproc.invoice_uploadstatus_1_0")))
      return EContinue.BREAK;

    if (aProject == EProject.JCODEMODEL)
      return EContinue.BREAK;

    if (aProject == EProject.PEPPOL_PRACTICAL && sPackageName.equals ("com.helger.peppol.wsclient"))
      return EContinue.BREAK;

    if (aProject == EProject.PH_CSS &&
        (sClassLocalName.equals ("CharStream") ||
         sClassLocalName.equals ("ParseException") ||
         sClassLocalName.startsWith ("ParserCSS21") ||
         sClassLocalName.startsWith ("ParserCSS30") ||
         sClassLocalName.startsWith ("ParserCSSCharsetDetector") ||
         sClassLocalName.equals ("Token") ||
         sClassLocalName.equals ("TokenMgrError") ||
         sClassLocalName.equals ("TokenMgrException") ||
         sClassLocalName.startsWith ("JJTParser") ||
         sClassLocalName.equals ("Node") ||
         sClassLocalName.equals ("Provider") ||
         sClassLocalName.equals ("StreamProvider") ||
         sClassLocalName.equals ("StringProvider") ||
         (sClassLocalName.startsWith ("Parser") && sClassLocalName.endsWith ("Constants")) ||
         sClassLocalName.equals ("SimpleNode")))
      return EContinue.BREAK;

    if (aProject == EProject.PH_EBINTERFACE &&
        (sPackageName.startsWith ("com.helger.ebinterface.v30") ||
         sPackageName.startsWith ("com.helger.ebinterface.v40") ||
         sPackageName.startsWith ("com.helger.ebinterface.v41") ||
         sPackageName.equals ("com.helger.ebinterface.xmldsig")))
      return EContinue.BREAK;

    if (aProject == EProject.PH_GENERICODE &&
        (sPackageName.equals ("com.helger.cva.v10") ||
         sPackageName.equals ("com.helger.genericode.v04") ||
         sPackageName.equals ("com.helger.genericode.v10")))
      return EContinue.BREAK;

    if (aProject == EProject.PH_ISORELAX &&
        (sPackageName.startsWith ("jp.gr.xml.relax.") || sPackageName.startsWith ("org.iso_relax.")))
      return EContinue.BREAK;

    if (aProject == EProject.PH_JSON &&
        (sClassLocalName.equals ("CharStream") ||
         sClassLocalName.equals ("ParseException") ||
         sClassLocalName.startsWith ("ParserJson") ||
         sClassLocalName.equals ("Token") ||
         sClassLocalName.equals ("TokenMgrError")))
      return EContinue.BREAK;

    if (aProject == EProject.PH_MINI_QUARTZ)
      return EContinue.BREAK;

    if (aProject == EProject.PH_SBDH && sPackageName.equals ("org.unece.cefact.namespaces.sbdh"))
      return EContinue.BREAK;

    if (aProject == EProject.PH_SCHEMATRON && sPackageName.equals ("org.oclc.purl.dsdl.svrl"))
      return EContinue.BREAK;

    if (aProject == EProject.PH_UBL20 &&
        (sPackageName.startsWith ("oasis.names.specification.ubl.schema.xsd.") ||
         sPackageName.startsWith ("un.unece.uncefact.codelist.specification.") ||
         sPackageName.startsWith ("un.unece.uncefact.data.specification.")))
      return EContinue.BREAK;

    if (aProject == EProject.PH_UBL21 &&
        (sPackageName.startsWith ("oasis.names.specification.ubl.schema.xsd.") ||
         sPackageName.startsWith ("org.etsi.uri.") ||
         sPackageName.equals ("org.w3._2000._09.xmldsig") ||
         sPackageName.equals ("un.unece.uncefact.data.specification.corecomponenttypeschemamodule._21")))
      return EContinue.BREAK;

    if (aProject == EProject.PH_XPATH2 &&
        (sClassLocalName.equals ("CharStream") ||
         sClassLocalName.equals ("ParseException") ||
         sClassLocalName.startsWith ("ParserXP2") ||
         sClassLocalName.equals ("Node") ||
         sClassLocalName.equals ("Token") ||
         sClassLocalName.equals ("TokenMgrError") ||
         sClassLocalName.startsWith ("JJTParser") ||
         sClassLocalName.equals ("SimpleNode")))
      return EContinue.BREAK;

    if (aProject == EProjectDeprecated.PH_STX_ENGINE)
      return EContinue.BREAK;

    if (aProject == EProjectDeprecated.PH_STX_PARSER &&
        (sClassLocalName.equals ("CharStream") ||
         sClassLocalName.equals ("ParseException") ||
         sClassLocalName.startsWith ("ParserSTX") ||
         sClassLocalName.equals ("Token") ||
         sClassLocalName.equals ("TokenMgrError") ||
         sClassLocalName.startsWith ("JJTParser") ||
         sClassLocalName.equals ("SimpleNode")))
      return EContinue.BREAK;

    return EContinue.CONTINUE;
  }

  private static void _scanMainCode (@Nonnull final IProject aProject)
  {
    // Find all main class files
    final File aMainClasses = new File (aProject.getBaseDir (), "target/classes");
    for (final File aClassFile : new FileSystemRecursiveIterator (aMainClasses))
      if (aClassFile.isFile () && aClassFile.getName ().endsWith (".class"))
      {
        // Interpret byte code
        final ClassNode cn = ASMHelper.readClassFile (aClassFile);

        // Ignore classes explicitly marked as unaware
        if (ASMHelper.containsAnnotation (cn, CodingStyleguideUnaware.class))
          continue;

        final String sClassName = ClassHelper.getClassFromPath (cn.name);
        final String sPackageName = ClassHelper.getClassPackageName (sClassName);
        final String sClassLocalName = ClassHelper.getClassLocalName (sClassName);

        // Special generated classes
        if (_doScanMainClass (aProject, sPackageName, sClassLocalName).isBreak ())
          continue;

        _checkClassNaming (aProject, cn);
        _checkMainVariables (aProject, cn);
        _checkMainMethods (aProject, cn);
      }
  }

  private static void _checkTestClass (@Nonnull final IProject aProject,
                                       @Nullable final String sBaseName,
                                       @Nullable final String sTestClass)
  {
    if (sTestClass != null &&
        sTestClass.endsWith ("Test") &&
        !sTestClass.endsWith ("FuncTest") &&
        (sBaseName == null || !sBaseName.startsWith ("Abstract")))
    {
      final String sMainClass = StringHelper.trimEnd (sTestClass, 4);
      final File aMainClass = new File (aProject.getBaseDir (), "target/classes/" + sMainClass + ".class");
      if (!aMainClass.exists ())
        _warn (aProject, "Test class " + sTestClass + " has no matching java/main class");
    }
    else
      if (sBaseName != null && sBaseName.startsWith ("FuncTest"))
      {
        _warn (aProject, "Test class " + sTestClass + " should end with FuncTest instead of starting with it");
      }
  }

  /**
   * @param aProject
   *        Base project
   * @param sPackageName
   *        Package name
   * @param sClassLocalName
   *        Class name without package
   * @return {@link EContinue}
   */
  @Nonnull
  private static EContinue _doScanTestClass (@Nonnull final IProject aProject,
                                             @Nonnull final String sPackageName,
                                             @Nonnull @Nonempty final String sClassLocalName)
  {
    if (aProject == EProject.PH_MINI_QUARTZ)
      return EContinue.BREAK;

    if (aProject == EProjectDeprecated.PH_STX_ENGINE)
      return EContinue.BREAK;

    return EContinue.CONTINUE;
  }

  private static void _scanTestCode (@Nonnull final IProject aProject)
  {
    final File aTestClassDir = new File (aProject.getBaseDir (), "target/test-classes");
    for (final File aClassFile : new FileSystemRecursiveIterator (aTestClassDir))
      if (aClassFile.isFile () && aClassFile.getName ().endsWith (".class"))
      {
        final String sName = aClassFile.getName ();

        // Interpret byte code
        final ClassNode cn = ASMHelper.readClassFile (aClassFile);
        final boolean bClassIsAbstract = Modifier.isAbstract (cn.access);
        final boolean bClassIsFinal = Modifier.isFinal (cn.access);

        boolean bContainsTestMethod = false;
        for (final Object oMethod : cn.methods)
        {
          final MethodNode mn = (MethodNode) oMethod;
          if (ASMHelper.containsAnnotation (mn, "Lorg/junit/Test;"))
          {
            bContainsTestMethod = true;
            break;
          }
        }

        final String sClassName = ClassHelper.getClassFromPath (cn.name);
        final String sPackageName = ClassHelper.getClassPackageName (sClassName);
        final String sClassLocalName = ClassHelper.getClassLocalName (sClassName);

        // Special generated classes
        if (_doScanTestClass (aProject, sPackageName, sClassLocalName).isBreak ())
          continue;

        final String sBaseName = FilenameHelper.getWithoutExtension (sName);
        if (bContainsTestMethod && !sBaseName.endsWith ("Test"))
          _warn (aProject, "Class '" + sName + "' contains @Test annotation but is named inconsistent");

        if (sBaseName.contains ("$") ||
            sBaseName.equals ("SPITest") ||
            sBaseName.equals ("SunJaxWSTest") ||
            sBaseName.equals ("JettyMonitor") ||
            sBaseName.startsWith ("JettyStop") ||
            sBaseName.startsWith ("RunInJetty") ||
            sBaseName.startsWith ("IMock") ||
            sBaseName.startsWith ("Mock") ||
            sBaseName.endsWith ("Mock") ||
            sBaseName.startsWith ("Benchmark") ||
            sBaseName.startsWith ("Issue") ||
            sBaseName.startsWith ("Main") ||
            sBaseName.endsWith ("TestRule"))
        {
          continue;
        }

        if (!bClassIsFinal && !bClassIsAbstract)
          _warn (aProject, "Test class '" + sName + "' is not final");

        _checkClassNaming (aProject, cn);

        final String sTestClass = FilenameHelper.getWithoutExtension (FilenameHelper.getRelativeToParentDirectory (aClassFile,
                                                                                                                   aTestClassDir));
        _checkTestClass (aProject, sBaseName, sTestClass);
      }
  }

  private static void _scanProject (@Nonnull final IProject aProject)
  {
    if (false)
      LOGGER.info ("  " + aProject.getProjectName ());

    _scanMainCode (aProject);
    _scanTestCode (aProject);
  }

  public static void main (final String [] args)
  {
    LOGGER.info ("Start checking coding style guide in .class files!");
    for (final IProject aProject : ProjectList.getAllProjects (p -> p.getProjectType ().hasJavaCode () &&
                                                                    p != EProject.PH_JAVACC_MAVEN_PLUGIN &&
                                                                    !p.isDeprecated ()))
      _scanProject (aProject);
    LOGGER.info ("Done - " + getWarnCount () + " warning(s) for " + ProjectList.size () + " projects");
  }
}
