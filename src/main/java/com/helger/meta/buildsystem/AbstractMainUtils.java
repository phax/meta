package com.helger.meta.buildsystem;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.meta.EProject;

/**
 * Base class for the main utilities in this package
 *
 * @author Philip Helger
 */
public abstract class AbstractMainUtils
{
  protected static final Logger s_aLogger = LoggerFactory.getLogger (AbstractMainUtils.class);
  private static int s_nWarnCount = 0;

  protected static final void _warn (@Nonnull final EProject eProject, @Nonnull final String sMsg)
  {
    s_aLogger.warn ("[" + eProject.getProjectName () + "] " + sMsg);
    s_nWarnCount++;
  }

  @Nonnegative
  protected static int getWarnCount ()
  {
    return s_nWarnCount;
  }
}
