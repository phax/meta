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
package com.helger.meta.tools.buildsystem;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.CGlobal;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.string.StringHelper;
import com.helger.base.string.StringImplode;
import com.helger.base.version.Version;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.CommonsLinkedHashSet;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.collection.commons.ICommonsOrderedSet;
import com.helger.io.file.SimpleFileIO;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.EExternalDependency;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

/**
 * Apply the output of <code>deps/check-updates.sh</code> (the versions-maven-plugin goal
 * <code>display-dependency-updates</code>) to the version numbers in the Java source files of
 * {@link EExternalDependency} and the project enums (like <code>EProject</code>).<br>
 * Usage:
 * <ul>
 * <li>Without arguments: {@link MainCreateKnownDependencyPOM} is invoked to regenerate
 * <code>deps/pom.xml</code>, then <code>deps/check-updates.sh</code> is executed and the created
 * <code>deps/dependency-updates.txt</code> is applied.</li>
 * <li>With one argument: the argument is the path to a file containing the saved output of
 * <code>deps/check-updates.sh</code> or of <code>mvn versions:display-dependency-updates</code>,
 * which is applied.</li>
 * </ul>
 * Must be run with the meta project directory as the working directory.
 *
 * @author Philip Helger
 */
public final class MainApplyDependencyUpdates extends AbstractProjectMain
{
  /**
   * One line of the versions-maven-plugin output
   */
  private static record DependencyUpdate (String groupID, String artifactID, String oldVersion, String newVersion)
  {
    @NonNull
    String getGA ()
    {
      return groupID + ":" + artifactID;
    }

    @Override
    public String toString ()
    {
      return getGA () + " " + oldVersion + " -> " + newVersion;
    }
  }

  /**
   * The enum constant that contains the version literal in the source code. Other constants may
   * inherit their version from it.
   */
  private static record VersionOwner (@NonNull Class <?> enumClass,
                                      @NonNull String constantName,
                                      @NonNull String oldVersion,
                                      @NonNull ICommonsOrderedSet <String> newVersions,
                                      @NonNull ICommonsOrderedSet <String> allGAs)
  {
    VersionOwner (@NonNull final Class <?> aEnumClass,
                  @NonNull final String sConstantName,
                  @NonNull final String sOldVersion)
    {
      this (aEnumClass, sConstantName, sOldVersion, new CommonsLinkedHashSet <> (), new CommonsLinkedHashSet <> ());
    }

    @NonNull
    String getDisplayName ()
    {
      return enumClass.getSimpleName () + "." + constantName;
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger (MainApplyDependencyUpdates.class);
  private static final Charset SOURCE_CHARSET = StandardCharsets.UTF_8;
  private static final File DEPS_DIR = new File ("deps");
  // Must match the file name used in check-updates.sh
  private static final File UPDATES_FILE = new File (DEPS_DIR, "dependency-updates.txt");
  private static final File SOURCE_DIR = new File ("src/main/java");

  // Removes ANSI colour codes, in case the output was created without "-B"
  private static final Pattern PATTERN_ANSI = Pattern.compile ("\u001B\\[[;\\d]*m");
  // The "[INFO]" prefix is only present in the console output, not in the output file
  // "[INFO] group:artifact ....... 1.0 -> 1.1"
  private static final Pattern PATTERN_FULL = Pattern.compile ("^(?:\\[INFO\\])?\\s+([^\\s:]+):([^\\s:]+)\\s+\\.*\\s*(\\S+)\\s+->\\s+(\\S+)\\s*$");
  // "[INFO] group:artifact ..." - the versions are wrapped to the next line
  private static final Pattern PATTERN_GA_ONLY = Pattern.compile ("^(?:\\[INFO\\])?\\s+([^\\s:]+):([^\\s:]+)\\s+\\.+\\s*$");
  // "[INFO] 1.0 -> 1.1" - the wrapped part
  private static final Pattern PATTERN_VERSIONS_ONLY = Pattern.compile ("^(?:\\[INFO\\])?\\s+(\\S+)\\s+->\\s+(\\S+)\\s*$");
  // Potential references to other enum constants (e.g. "ASM" but not "EJDK.JDK8")
  private static final Pattern PATTERN_CONSTANT_REF = Pattern.compile ("(?<![\\w.])[A-Z][A-Z0-9_]*\\b");

  @Nullable
  private static String _runCheckUpdates ()
  {
    LOGGER.info ("Regenerating " + new File (DEPS_DIR, "pom.xml"));
    MainCreateKnownDependencyPOM.main (CGlobal.EMPTY_STRING_ARRAY);

    LOGGER.info ("Running check-updates.sh in '" + DEPS_DIR.getAbsolutePath () + "' - this may take a while");
    try
    {
      final Process aProcess = new ProcessBuilder ("sh", "check-updates.sh").directory (DEPS_DIR)
                                                                            .redirectErrorStream (true)
                                                                            .start ();
      final String sOutput = StreamHelper.getAllBytesAsString (aProcess.getInputStream (), StandardCharsets.UTF_8);
      final int nExitCode = aProcess.waitFor ();
      if (nExitCode != 0)
      {
        LOGGER.error ("check-updates.sh failed with exit code " + nExitCode + ":\n" + sOutput);
        return null;
      }
      if (!UPDATES_FILE.exists ())
      {
        LOGGER.error ("check-updates.sh did not create '" + UPDATES_FILE.getAbsolutePath () + "'");
        return null;
      }
      return SimpleFileIO.getFileAsString (UPDATES_FILE, StandardCharsets.UTF_8);
    }
    catch (final IOException ex)
    {
      LOGGER.error ("Failed to run check-updates.sh", ex);
      return null;
    }
    catch (final InterruptedException ex)
    {
      Thread.currentThread ().interrupt ();
      LOGGER.error ("Interrupted while running check-updates.sh", ex);
      return null;
    }
  }

  @NonNull
  @ReturnsMutableCopy
  private static ICommonsList <DependencyUpdate> _parseOutput (@NonNull final String sOutput)
  {
    final ICommonsOrderedMap <String, DependencyUpdate> ret = new CommonsLinkedHashMap <> ();
    String sPendingGroupID = null;
    String sPendingArtifactID = null;

    // Split by line
    for (final String sRawLine : sOutput.split ("\\R"))
    {
      // Remove ANSI colors
      final String sLine = PATTERN_ANSI.matcher (sRawLine).replaceAll ("");

      DependencyUpdate aUpdate = null;
      Matcher aMatcher = PATTERN_FULL.matcher (sLine);
      if (aMatcher.matches ())
        aUpdate = new DependencyUpdate (aMatcher.group (1), aMatcher.group (2), aMatcher.group (3), aMatcher.group (4));
      else
        if (sPendingGroupID != null)
        {
          aMatcher = PATTERN_VERSIONS_ONLY.matcher (sLine);
          if (aMatcher.matches ())
            aUpdate = new DependencyUpdate (sPendingGroupID,
                                            sPendingArtifactID,
                                            aMatcher.group (1),
                                            aMatcher.group (2));
        }
      sPendingGroupID = null;
      sPendingArtifactID = null;

      if (aUpdate != null)
      {
        // Same dependency may be listed in multiple sections
        ret.putIfAbsent (aUpdate.toString (), aUpdate);
      }
      else
      {
        aMatcher = PATTERN_GA_ONLY.matcher (sLine);
        if (aMatcher.matches ())
        {
          sPendingGroupID = aMatcher.group (1);
          sPendingArtifactID = aMatcher.group (2);
        }
      }
    }
    return ret.copyOfValues ();
  }

  @NonNull
  private static String _getLowerBound (@NonNull final String sVersion)
  {
    // Version ranges like "[4.0.9,5.0.0)" are used for dependencies with a maximum version
    if (sVersion.startsWith ("[") || sVersion.startsWith ("("))
    {
      final int nComma = sVersion.indexOf (',');
      if (nComma > 1)
        return sVersion.substring (1, nComma).trim ();
    }
    return sVersion;
  }

  /**
   * Find all enum constants that are the source of the provided dependency in the version to be
   * updated.
   */
  @NonNull
  @ReturnsMutableCopy
  private static ICommonsList <Enum <?>> _findMatchingConstants (@NonNull final DependencyUpdate aUpdate,
                                                                 @NonNull final ICommonsList <String> aProblems)
  {
    final ICommonsList <Enum <?>> ret = new CommonsArrayList <> ();
    final String sOldVersion = _getLowerBound (aUpdate.oldVersion ());

    for (final EExternalDependency e : EExternalDependency.values ())
      if (!e.isLegacy () &&
          e.hasGroupID (aUpdate.groupID ()) &&
          e.getArtifactID ().equals (aUpdate.artifactID ()) &&
          e.getLastPublishedVersionString ().equals (sOldVersion))
      {
        final String sMaxVersion = e.getMaxVersionString ();
        if (StringHelper.isNotEmpty (sMaxVersion) &&
            Version.parse (aUpdate.newVersion ()).compareTo (Version.parse (sMaxVersion)) >= 0)
        {
          aProblems.add (aUpdate +
                         ": " +
                         e.getClass ().getSimpleName () +
                         "." +
                         e.name () +
                         " has maximum version (exclusive) " +
                         sMaxVersion);
        }
        else
          ret.add (e);
      }

    // Same filter as in MainCreateKnownDependencyPOM
    for (final IProject aProject : ProjectList.getAllProjects (x -> x.isPhProject () &&
                                                                    x.isPublished () &&
                                                                    !x.isDeprecated ()))
      if (aProject.getMavenGroupID ().equals (aUpdate.groupID ()) &&
          aProject.getMavenArtifactID ().equals (aUpdate.artifactID ()) &&
          sOldVersion.equals (aProject.getLastPublishedVersionString ()))
      {
        if (aProject instanceof final Enum <?> aEnum)
          ret.add (aEnum);
        else
          aProblems.add (aUpdate + ": project '" + aProject.getProjectName () + "' is not defined in an enum");
      }

    return ret;
  }

  @NonNull
  private static File _getSourceFile (@NonNull final Class <?> aClass)
  {
    return new File (SOURCE_DIR, aClass.getName ().replace ('.', '/') + ".java");
  }

  /**
   * @return The start index (inclusive) and end index (exclusive) of the constructor arguments of
   *         the provided enum constant in the source code or <code>null</code> if not found.
   */
  private static int @Nullable [] _findConstantArguments (@NonNull final String sSource,
                                                          @NonNull final String sConstantName)
  {
    final Matcher aMatcher = Pattern.compile ("(?m)^[ \\t]*" + Pattern.quote (sConstantName) + "[ \\t]*\\(")
                                    .matcher (sSource);
    if (!aMatcher.find ())
      return null;

    final int nStart = aMatcher.end ();
    int nDepth = 1;
    boolean bInString = false;
    for (int i = nStart; i < sSource.length (); ++i)
    {
      final char c = sSource.charAt (i);
      if (bInString)
      {
        if (c == '\\')
          ++i;
        else
          if (c == '"')
            bInString = false;
      }
      else
        if (c == '"')
          bInString = true;
        else
          if (c == '/' && i + 1 < sSource.length () && sSource.charAt (i + 1) == '/')
          {
            // Skip line comment
            final int nEOL = sSource.indexOf ('\n', i);
            if (nEOL < 0)
              return null;
            i = nEOL;
          }
          else
            if (c == '(')
              ++nDepth;
            else
              if (c == ')')
              {
                --nDepth;
                if (nDepth == 0)
                  return new int [] { nStart, i };
              }
    }
    return null;
  }

  private static boolean _isConstantOf (@NonNull final Class <?> aEnumClass, @NonNull final String sName)
  {
    for (final Object aConstant : aEnumClass.getEnumConstants ())
      if (((Enum <?>) aConstant).name ().equals (sName))
        return true;
    return false;
  }

  /**
   * Find the name of the enum constant that contains the literal version string. Follows references
   * to other constants of the same enum (e.g. <code>ASM_TREE</code> referencing <code>ASM</code>,
   * or a child project referencing its parent project).
   */
  @Nullable
  private static String _findVersionOwnerRecursive (@NonNull final Class <?> aEnumClass,
                                                    @NonNull final String sSource,
                                                    @NonNull final String sConstantName,
                                                    @NonNull final String sVersion,
                                                    @NonNull final Set <String> aVisited)
  {
    if (!aVisited.add (sConstantName))
      return null;

    final int [] aRange = _findConstantArguments (sSource, sConstantName);
    if (aRange == null)
      return null;

    final String sArgs = sSource.substring (aRange[0], aRange[1]);
    if (sArgs.contains ("\"" + sVersion + "\""))
      return sConstantName;

    // The version is inherited from another constant
    final Matcher aMatcher = PATTERN_CONSTANT_REF.matcher (sArgs);
    while (aMatcher.find ())
    {
      final String sRef = aMatcher.group ();
      if (_isConstantOf (aEnumClass, sRef))
      {
        final String ret = _findVersionOwnerRecursive (aEnumClass, sSource, sRef, sVersion, aVisited);
        if (ret != null)
          return ret;
      }
    }
    return null;
  }

  @NonNull
  private static String _getSource (@NonNull final ICommonsOrderedMap <File, String> aSources,
                                    @NonNull final Class <?> aEnumClass)
  {
    return aSources.computeIfAbsent (_getSourceFile (aEnumClass),
                                     f -> SimpleFileIO.getFileAsString (f, SOURCE_CHARSET));
  }

  public static void main (final String [] args)
  {
    final String sOutput = _runCheckUpdates ();
    if (sOutput == null)
    {
      LOGGER.error ("No dependency update information available");
      return;
    }

    final ICommonsList <DependencyUpdate> aUpdates = _parseOutput (sOutput);
    LOGGER.info ("Found " + aUpdates.size () + " dependency update(s)");

    final ICommonsList <String> aProblems = new CommonsArrayList <> ();
    final ICommonsOrderedMap <File, String> aSources = new CommonsLinkedHashMap <> ();
    final ICommonsOrderedMap <String, VersionOwner> aOwners = new CommonsLinkedHashMap <> ();

    // Resolve all updates to the source code location
    for (final DependencyUpdate aUpdate : aUpdates)
    {
      final ICommonsList <Enum <?>> aConstants = _findMatchingConstants (aUpdate, aProblems);
      if (aConstants.isEmpty ())
      {
        aProblems.add (aUpdate + ": no enum constant with that version found");
        continue;
      }

      final String sOldVersion = _getLowerBound (aUpdate.oldVersion ());
      for (final Enum <?> aConstant : aConstants)
      {
        final Class <?> aEnumClass = aConstant.getDeclaringClass ();
        final String sOwnerName = _findVersionOwnerRecursive (aEnumClass,
                                                              _getSource (aSources, aEnumClass),
                                                              aConstant.name (),
                                                              sOldVersion,
                                                              new CommonsHashSet <> ());
        if (sOwnerName == null)
        {
          aProblems.add (aUpdate +
                         ": failed to find version literal for " +
                         aEnumClass.getSimpleName () +
                         "." +
                         aConstant.name () +
                         " in the source code");
          continue;
        }

        final VersionOwner aOwner = aOwners.computeIfAbsent (aEnumClass.getName () + "." + sOwnerName,
                                                             _ -> new VersionOwner (aEnumClass,
                                                                                    sOwnerName,
                                                                                    sOldVersion));
        aOwner.newVersions ().add (aUpdate.newVersion ());
        aOwner.allGAs ().add (aUpdate.getGA ());
      }
    }

    // Apply the changes to the source code
    final ICommonsList <String> aApplied = new CommonsArrayList <> ();
    final ICommonsOrderedSet <File> aChangedFiles = new CommonsLinkedHashSet <> ();
    for (final VersionOwner aOwner : aOwners.values ())
    {
      if (aOwner.newVersions ().size () > 1)
      {
        aProblems.add (aOwner.getDisplayName () +
                       ": conflicting new versions " +
                       aOwner.newVersions () +
                       " from " +
                       aOwner.allGAs ());
        continue;
      }

      final String sNewVersion = aOwner.newVersions ().getFirstOrNull ();
      final File aFile = _getSourceFile (aOwner.enumClass ());
      final String sSource = aSources.get (aFile);
      final int [] aRange = _findConstantArguments (sSource, aOwner.constantName ());
      final String sOldLiteral = "\"" + aOwner.oldVersion () + "\"";
      final int nIndex = aRange == null ? -1 : sSource.indexOf (sOldLiteral, aRange[0]);
      if (nIndex < 0 || nIndex >= aRange[1])
      {
        aProblems.add (aOwner.getDisplayName () + ": version literal " + sOldLiteral + " not found");
        continue;
      }

      aSources.put (aFile,
                    sSource.substring (0, nIndex) +
                           "\"" +
                           sNewVersion +
                           "\"" +
                           sSource.substring (nIndex + sOldLiteral.length ()));
      aChangedFiles.add (aFile);
      aApplied.add (aOwner.getDisplayName () +
                    ": " +
                    aOwner.oldVersion () +
                    " -> " +
                    sNewVersion +
                    " (" +
                    StringImplode.getImploded (", ", aOwner.allGAs ()) +
                    ")");
    }

    for (final File aFile : aChangedFiles)
    {
      SimpleFileIO.writeFile (aFile, aSources.get (aFile), SOURCE_CHARSET);
      LOGGER.info ("Wrote " + aFile.getPath ());
    }

    // Final report
    final StringBuilder aSB = new StringBuilder ();
    aSB.append ("Updated versions (").append (aApplied.size ()).append ("):\n");
    for (final String s : aApplied)
      aSB.append ("  ").append (s).append ('\n');
    if (aProblems.isNotEmpty ())
    {
      aSB.append ("Not applied (").append (aProblems.size ()).append ("):\n");
      for (final String s : aProblems)
        aSB.append ("  ").append (s).append ('\n');
    }
    LOGGER.info (aSB.toString ());
    if (aChangedFiles.isNotEmpty ())
      LOGGER.info ("Recompile and run " +
                   MainCreateKnownDependencyPOM.class.getSimpleName () +
                   " to update deps/pom.xml");
    LOGGER.info ("Done");
  }
}
