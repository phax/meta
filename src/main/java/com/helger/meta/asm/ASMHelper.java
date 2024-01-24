/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.meta.asm;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.helger.commons.annotation.NoTranslationRequired;
import com.helger.commons.annotation.Translatable;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.commons.lang.ClassHelper;

@Immutable
public final class ASMHelper
{
  private ASMHelper ()
  {}

  @Nullable
  public static MethodNode findMethod (@Nonnull final ClassNode cn, final String sMethodName)
  {
    if (cn.methods != null)
      for (final Object o : cn.methods)
      {
        final MethodNode mn = (MethodNode) o;
        if (mn.name.equals (sMethodName))
          return mn;
      }
    return null;
  }

  public static boolean containsAnnotation (final FieldNode fn, final Class <? extends Annotation> aClass)
  {
    final String sDescriptor = Type.getDescriptor (aClass);

    // Invisible annotations (retention policy: class)
    if (fn.invisibleAnnotations != null)
      for (final Object o : fn.invisibleAnnotations)
      {
        final String sDesc = ((AnnotationNode) o).desc;
        if (sDesc.equals (sDescriptor))
          return true;
      }

    // Invisible annotations (retention policy: runtime)
    if (fn.visibleAnnotations != null)
      for (final Object o : fn.visibleAnnotations)
      {
        final String sDesc = ((AnnotationNode) o).desc;
        if (sDesc.equals (sDescriptor))
          return true;
      }
    return false;
  }

  public static boolean containsAnnotation (final MethodNode mn, final Class <? extends Annotation> aClass)
  {
    final String sDescriptor = Type.getDescriptor (aClass);
    return containsAnnotation (mn, sDescriptor);
  }

  public static boolean containsAnnotation (final MethodNode mn, final String sDescriptor)
  {
    // Invisible annotations (retention policy: class)
    if (mn.invisibleAnnotations != null)
      for (final Object o : mn.invisibleAnnotations)
      {
        final String sDesc = ((AnnotationNode) o).desc;
        if (sDesc.equals (sDescriptor))
          return true;
      }

    // Invisible annotations (retention policy: runtime)
    if (mn.visibleAnnotations != null)
      for (final Object o : mn.visibleAnnotations)
      {
        final String sDesc = ((AnnotationNode) o).desc;
        if (sDesc.equals (sDescriptor))
          return true;
      }
    return false;
  }

  public static boolean containsAnnotation (final ClassNode cn, final Class <? extends Annotation> aClass)
  {
    final String sDescriptor = Type.getDescriptor (aClass);

    if (cn.visibleAnnotations != null)
      for (final Object o : cn.visibleAnnotations)
      {
        final String sDesc = ((AnnotationNode) o).desc;
        if (sDesc.equals (sDescriptor))
          return true;
      }

    if (cn.invisibleAnnotations != null)
      for (final Object o : cn.invisibleAnnotations)
      {
        final String sDesc = ((AnnotationNode) o).desc;
        if (sDesc.equals (sDescriptor))
          return true;
      }
    return false;
  }

  public static boolean containsRequiresTranslationAnnotation (@Nonnull final ClassNode cn)
  {
    return containsAnnotation (cn, Translatable.class);
  }

  public static boolean containsNoTranslationRequiredAnnotation (@Nonnull final ClassNode cn)
  {
    return containsAnnotation (cn, NoTranslationRequired.class);
  }

  public static boolean containsStaticCall (@Nonnull final MethodNode mn, @Nullable final String sOwner)
  {
    final Iterator <?> itinsn = mn.instructions.iterator ();
    while (itinsn.hasNext ())
    {
      final AbstractInsnNode in = (AbstractInsnNode) itinsn.next ();
      if (in instanceof MethodInsnNode)
      {
        final MethodInsnNode min = (MethodInsnNode) in;
        if (min.owner.equals (sOwner))
          return true;
      }
    }
    return false;
  }

  public static boolean containsStaticCall (@Nonnull final MethodNode mn, @Nonnull final Class <?> aOwner)
  {
    return containsStaticCall (mn, ClassHelper.getPathFromClass (aOwner));
  }

  /**
   * Read the passed class file using ASM and build a {@link ClassNode}.
   *
   * @param aClassFile
   *        The file to read. May not be <code>null</code>.
   * @return The read {@link ClassNode}.
   */
  @Nonnull
  public static ClassNode readClassFile (@Nonnull final File aClassFile)
  {
    // Read and interpret the class file
    return readClass (SimpleFileIO.getAllFileBytes (aClassFile));
  }

  /**
   * Read the passed class file using ASM and build a {@link ClassNode}.
   *
   * @param aBytes
   *        The bytes representing the content of the class file to read. May
   *        not be <code>null</code>.
   * @return The read {@link ClassNode}.
   */
  @Nonnull
  public static ClassNode readClass (@Nonnull final byte [] aBytes)
  {
    // Interpret the class file
    final ClassReader cr = new ClassReader (aBytes);
    final ClassNode cn = new ClassNode ();
    cr.accept (cn, 0);
    return cn;
  }
}
