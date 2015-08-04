/*******************************************************************************

ADL SCORM 2004 4th Edition Test Suite Software

The ADL SCORM 2004 4th Ed. Test Suite Software is licensed under
Creative Commons Attribution-Noncommercial-No Derivative Works 3.0 United States.

The Advanced Distributed Learning Initiative allows you to:
  *  Share - to copy, distribute, display, and perform the work.

Under the following conditions:
  *  Attribution. You must attribute the work in the manner specified by the author
     or licensor (but not in any way that suggests that they endorse you or your use
     of the work).
  *  Noncommercial. You may not use this work for commercial purposes.
  *  No Derivative Works. You may not alter, transform, or build upon this work.
  
For any reuse or distribution, you must make clear to others the license terms of this
work. The best way to do this is with a link to this web page.

Any of the above conditions can be waived if you get permission from the copyright
holder. Nothing in this license impairs or restricts the author's moral rights.

*******************************************************************************/
package org.adl.util.debug;


import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * <strong>Filename: </strong> LogConfig.java<br><br>
 *
 * <strong>Description:</strong><br>
 * This class sets up the configuration of how logging is handled based on the
 * contents of the configuration file provided with the software.  This config
 * file is modifiable to change the handling of logging without the need to
 * re-build the software.<br><br>
 * 
 * This class sets up the configuration for outputting the debug logging 
 * messages.  More specifically, if reads adllog.properties file and sets:
 * <ul>
 *    <li>the level of logging requested by the user;</li>
 *    <li>whether or not the logging messages should be displayed to the 
 *        Console;</li>
 *    <li>whether or not the logging messages should be written to a file;</li>
 *    <li>whether to print the default timestamp to the console and/or to
 *        the file.</li>
 * </ul>
 *
 * <strong>Design Issues:</strong><br>
 * None<br>
 * <br>
 *
 * <strong>Implementation Issues:</strong><br>
 * The <code>configure()</code> method must be called to instantiate the logger 
 * for the log management/handling properties to be read from the properties 
 * file and initialized.<br><br>
 *
 * <strong>Known Problems:</strong><br>
 * None<br><br>
 *
 * <strong>Side Effects:</strong><br>
 * None<br><br>
 *
 * <strong>References:</strong><br>
 * None<br><br>
 *
 * @author ADL Technical Team
 */
public class LogConfig
{
   /**
    * Manages the log
    */
   LogManager mLogManager;

   /**
    *  ADL Logger object for the Test Suite
    */
   Logger mLoggerTestSuite;

   /**
    * Setup ADL Logger object for the Sequencer
    */
   Logger mLoggerSequencer;

   /**
    * Setup ADL Logger object for the SampleRTE
    */
   Logger mLoggerSampleRTE;

   /**
    *  Setup ADL Logger object for the Validator
    */
   Logger mLoggerValidator;

   /**
    *  Setup ADL Logger object for the Data Model
    */
   Logger mLoggerDataModel;

   /**
    *  Setup ADL Logger object for Miscellaneous
    */
   Logger mLoggerMisc;

   /**
    * Default constructor for the LogConfig class.
    */
   public LogConfig()
   {
      // Initialize mLogManager
      mLogManager = LogManager.getLogManager();

      // Setup ADL Logger for the Test Suite
      mLoggerTestSuite = Logger.getLogger("org.adl.util.debug.testsuite");

      // Setup ADL Logger for the Sequencer
      mLoggerSequencer = Logger.getLogger("org.adl.util.debug.sequencer");

      // Setup ADL Logger for the SampleRTE
      mLoggerSampleRTE = Logger.getLogger("org.adl.util.debug.samplerte");

      // Setup ADL Logger for the Validator
      mLoggerValidator = Logger.getLogger("org.adl.util.debug.validator");

      // Setup ADL Logger for the Data Model
      mLoggerDataModel = Logger.getLogger("org.adl.util.debug.datamodel");

      // Setup ADL Logger for Miscellaneous
      mLoggerMisc = Logger.getLogger("org.adl.util.debug.misc");
   }

   /**
    * Reads the ADL specific properties file.  This is used to configure the
    * debug logging output.<br>
    * 
    * @param iEnvironmentVariable Envrionment variable used to locate the 
    * Test Suite properties file
    * 
    * @param iAppendFile A boolean flag indicating whether or not to append 
    * to the file
    */
   public void configure(String iEnvironmentVariable, boolean iAppendFile)
   {
      String propFileName;
      String propFile;
      // Use our own file, NOT the default logging file.
      if (!System.getProperty("os.name").toLowerCase().startsWith("windows")){
          propFileName = "//adllog.properties";
          propFile = iEnvironmentVariable + "//config" + propFileName;
      }
      else{
          propFileName = "\\adllog.properties";
          propFile = iEnvironmentVariable + "\\config" + propFileName;
      }


      System.setProperty("java.util.logging.config.file", propFile);

      // Read the configuration just modified with setProperty
      try
      {
         mLogManager.readConfiguration();
      }
      catch ( IOException ioe )
      {
         System.out.println("IOException Error: "+ioe);
      }
      catch ( SecurityException se )
      {
         System.out.println("SecurityException Error: "+se);
      }

      Properties props = new Properties();

      // Will be used to hold the boolean values in the properties file for:
      // Test Suite Logger
      String consoleOutputTestSuite = new String();
      String consoleTimestampTestSuite = new String();
      String fileOutputTestSuite = new String();
      String fileTimestampTestSuite = new String();

      // Sequencer Logger
      String consoleOutputSequencer = new String();
      String consoleTimestampSequencer = new String();
      String fileOutputSequencer = new String();
      String fileTimestampSequencer = new String();

      // SampleRTE Logger
      String consoleOutputSampleRTE = new String();
      String consoleTimestampSampleRTE = new String();
      String fileOutputSampleRTE = new String();
      String fileTimestampSampleRTE = new String();

      // Validator Logger
      String consoleOutputValidator = new String();
      String consoleTimestampValidator = new String();
      String fileOutputValidator = new String();
      String fileTimestampValidator = new String();

      // Data Model Logger
      String consoleOutputDataModel = new String();
      String consoleTimestampDataModel = new String();
      String fileOutputDataModel = new String();
      String fileTimestampDataModel = new String();

      // Miscellaneous Catchall Loggers
      String consoleOutputMisc = new String();
      String consoleTimestampMisc = new String();
      String fileOutputMisc = new String();
      String fileTimestampMisc = new String();

      try
      {
         // Create InputStream of the adlog.properties file name
         InputStream instream = new FileInputStream(propFile);
         props.load(instream);
         // Get the values set in adllog.properties for:
         // Test Suite Logger
         consoleOutputTestSuite = props.getProperty("org.adl.util.debug." +
                                                    "print.console.testsuite");
         consoleTimestampTestSuite = props.getProperty("org.adl.util.debug." +
                                                 "timestamp.console.testsuite");
         fileOutputTestSuite = props.getProperty("org.adl.util.debug.print." +
                                                 "file.testsuite");
         fileTimestampTestSuite = props.getProperty("org.adl.util.debug." +
                                                    "timestamp.file.testsuite");

         // Sequencer Logger
         consoleOutputSequencer = props.getProperty("org.adl.util.debug." +
                                                    "print.console.sequencer");
         consoleTimestampSequencer = props.getProperty("org.adl.util.debug." +
                                                 "timestamp.console.sequencer");
         fileOutputSequencer = props.getProperty("org.adl.util.debug.print." +
                                                 "file.sequencer");
         fileTimestampSequencer = props.getProperty("org.adl.util.debug." +
                                                    "timestamp.file.sequencer");

         // SampleRTE Logger
         consoleOutputSampleRTE = props.getProperty("org.adl.util.debug." +
                                                    "print.console.samplerte");
         consoleTimestampSampleRTE = props.getProperty("org.adl.util.debug." +
                                                "timestamp.console.samplerte");
         fileOutputSampleRTE = props.getProperty("org.adl.util.debug.print." +
                                                 "file.samplerte");
         fileTimestampSampleRTE = props.getProperty("org.adl.util.debug." +
                                                    "timestamp.file.samplerte");

         // Validator Logger
         consoleOutputValidator = props.getProperty("org.adl.util.debug." +
                                                    "print.console.validator");
         consoleTimestampValidator = props.getProperty("org.adl.util.debug." +
                                                 "timestamp.console.validator");
         fileOutputValidator = props.getProperty("org.adl.util.debug.print." +
                                                 "file.validator");
         fileTimestampValidator = props.getProperty("org.adl.util.debug." +
                                                    "timestamp.file.validator");

         // Data Model Logger
         consoleOutputDataModel = props.getProperty("org.adl.util.debug." +
                                                    "print.console.datamodel");
         consoleTimestampDataModel = props.getProperty("org.adl.util.debug." +
                                               "timestamp.console.datamodel");
         fileOutputDataModel = props.getProperty("org.adl.util.debug.print." +
                                                 "file.datamodel");
         fileTimestampDataModel = props.getProperty("org.adl.util.debug." +
                                                    "timestamp.file.datamodel");

         // Miscellaneous Catchall Loggers
         consoleOutputMisc = props.getProperty("org.adl.util.debug.print." +
                                               "console.misc");
         consoleTimestampMisc = props.getProperty("org.adl.util.debug." +
                                                  "timestamp.console.misc");
         fileOutputMisc = props.getProperty("org.adl.util.debug.print.file." +
                                                                     "misc");
         fileTimestampMisc = props.getProperty("org.adl.util.debug.timestamp." +
                                               "file.misc");

         instream.close();
      }
      catch ( FileNotFoundException fnfe)
      {
         System.out.println("FileNotFoundException " + fnfe);
      }
      catch ( IOException ioe)
      {
         System.out.println("IOException " + ioe);
      }
      catch ( SecurityException se )
      {
         System.out.println("SecurityException Error: "+se);
      }

      // Test Suite Logger
      // If true - will be used to indicate: Print to the Console.
      if (consoleOutputTestSuite.equalsIgnoreCase("true"))
      {
         ConsoleHandler ch = new ConsoleHandler();

         if (consoleTimestampTestSuite.equalsIgnoreCase("true"))
         {
            // Use Java's formatter which displays the date
            ch.setFormatter(new SimpleFormatter());
         }
         else
         {
            // Use ADL's overridden formatter - has NO date/timestamp
            ch.setFormatter(new ADLSimpleFormatter());
         }
         mLoggerTestSuite.addHandler(ch);
      }

      // If true - will be used to indicate: Print to a file.
      if (fileOutputTestSuite.equalsIgnoreCase("true"))
      {
         // Setup output log file
         try
         {
            String fileName = new String();
            // Output filename
            fileName = iEnvironmentVariable + File.separator + "config" +
               File.separator + "debug_testsuite.log";

            FileHandler fh = new FileHandler(fileName, iAppendFile);

            if (fileTimestampTestSuite.equalsIgnoreCase("true"))
            {
               // Use Java's formatter which displays the date
               fh.setFormatter(new SimpleFormatter());
            }
            else
            {
               // Use ADL's overridden formatter - has NO date/timestamp
               fh.setFormatter(new ADLSimpleFormatter());
            }
            mLoggerTestSuite.addHandler(fh);
         }
         catch ( IOException ioe )
         {
            System.out.println("Handler IOException Error TestSuite: "+ioe);
         }
      }

      // Sequencer Logger
      // If true - will be used to indicate: Print to the Console.
      if (consoleOutputSequencer.equalsIgnoreCase("true"))
      {
         ConsoleHandler ch = new ConsoleHandler();

         if (consoleTimestampSequencer.equalsIgnoreCase("true"))
         {
            // Use Java's formatter which displays the date
            ch.setFormatter(new SimpleFormatter());
         }
         else
         {
            // Use ADL's overridden formatter - has NO date/timestamp
            ch.setFormatter(new ADLSimpleFormatter());
         }
         mLoggerSequencer.addHandler(ch);
      }

      // If true - will be used to indicate: Print to a file.
      if (fileOutputSequencer.equalsIgnoreCase("true"))
      {
         // Setup output log file
         try
         {
            String fileName = new String();
            // Output filename
            fileName = iEnvironmentVariable + File.separator + "config" +
               File.separator + "debug_sequencer.log";

            FileHandler fh = new FileHandler(fileName, iAppendFile);

            if (fileTimestampSequencer.equalsIgnoreCase("true"))
            {
               // Use Java's formatter which displays the date
               fh.setFormatter(new SimpleFormatter());
            }
            else
            {
               // Use ADL's overridden formatter - has NO date/timestamp
               fh.setFormatter(new ADLSimpleFormatter());
            }
            mLoggerSequencer.addHandler(fh);
         }
         catch ( IOException ioe )
         {
            System.out.println("Handler IOException Error Sequencer: "+ioe);
         }
      }

      // SampleRTE Logger
      // If true - will be used to indicate: Print to the Console.
      if (consoleOutputSampleRTE.equalsIgnoreCase("true"))
      {
         ConsoleHandler ch = new ConsoleHandler();

         if (consoleTimestampSampleRTE.equalsIgnoreCase("true"))
         {
            // Use Java's formatter which displays the date
            ch.setFormatter(new SimpleFormatter());
         }
         else
         {
            // Use ADL's overridden formatter - has NO date/timestamp
            ch.setFormatter(new ADLSimpleFormatter());
         }
         mLoggerSampleRTE.addHandler(ch);
      }

      // If true - will be used to indicate: Print to a file.
      if (fileOutputSampleRTE.equalsIgnoreCase("true"))
      {
         // Setup output log file
         try
         {
            String fileName = new String();
            // Output filename
            fileName = iEnvironmentVariable + File.separator + "config" +
               File.separator + "debug_samplerte.log";

            FileHandler fh = new FileHandler(fileName, iAppendFile);

            if (fileTimestampSampleRTE.equalsIgnoreCase("true"))
            {
               // Use Java's formatter which displays the date
               fh.setFormatter(new SimpleFormatter());
            }
            else
            {
               // Use ADL's overridden formatter - has NO date/timestamp
               fh.setFormatter(new ADLSimpleFormatter());
            }
            mLoggerSampleRTE.addHandler(fh);
         }
         catch ( IOException ioe )
         {
            System.out.println("Handler IOException Error: "+ioe);
         }
      }

      // Validator Logger
      // If true - will be used to indicate: Print to the Console.
      if (consoleOutputValidator.equalsIgnoreCase("true"))
      {
         ConsoleHandler ch = new ConsoleHandler();

         if (consoleTimestampValidator.equalsIgnoreCase("true"))
         {
            // Use Java's formatter which displays the date
            ch.setFormatter(new SimpleFormatter());
         }
         else
         {
            // Use ADL's overridden formatter - has NO date/timestamp
            ch.setFormatter(new ADLSimpleFormatter());
         }
         mLoggerValidator.addHandler(ch);
      }

      // If true - will be used to indicate: Print to a file.
      if (fileOutputValidator.equalsIgnoreCase("true"))
      {
         // Setup output log file
         try
         {
            String fileName = new String();
            // Output filename
            fileName = iEnvironmentVariable + File.separator + "config" +
               File.separator + "debug_validator.log";

            FileHandler fh = new FileHandler(fileName, iAppendFile);

            if (fileTimestampValidator.equalsIgnoreCase("true"))
            {
               // Use Java's formatter which displays the date
               fh.setFormatter(new SimpleFormatter());
            }
            else
            {
               // Use ADL's overridden formatter - has NO date/timestamp
               fh.setFormatter(new ADLSimpleFormatter());
            }
            mLoggerValidator.addHandler(fh);
         }
         catch ( IOException ioe )
         {
            System.out.println("Handler IOException Error: "+ioe);
         }
      }

      // Data Model Logger
      // If true - will be used to indicate: Print to the Console.
      if (consoleOutputDataModel.equalsIgnoreCase("true"))
      {
         ConsoleHandler ch = new ConsoleHandler();

         if (consoleTimestampDataModel.equalsIgnoreCase("true"))
         {
            // Use Java's formatter which displays the date
            ch.setFormatter(new SimpleFormatter());
         }
         else
         {
            // Use ADL's overridden formatter - has NO date/timestamp
            ch.setFormatter(new ADLSimpleFormatter());
         }
         mLoggerDataModel.addHandler(ch);
      }

      // If true - will be used to indicate: Print to a file.
      if (fileOutputDataModel.equalsIgnoreCase("true"))
      {
         // Setup output log file
         try
         {
            String fileName = new String();
            // Output filename
            fileName = iEnvironmentVariable + File.separator + "config" +
               File.separator + "debug_datamodel.log";

            FileHandler fh = new FileHandler(fileName, iAppendFile);

            if (fileTimestampDataModel.equalsIgnoreCase("true"))
            {
               // Use Java's formatter which displays the date
               fh.setFormatter(new SimpleFormatter());
            }
            else
            {
               // Use ADL's overridden formatter - has NO date/timestamp
               fh.setFormatter(new ADLSimpleFormatter());
            }
            mLoggerDataModel.addHandler(fh);
         }
         catch ( IOException ioe )
         {
            System.out.println("Handler IOException Error: "+ioe);
         }
      }

      // Miscellaneous Catchall Logger
      // If true - will be used to indicate: Print to the Console.
      if (consoleOutputMisc.equalsIgnoreCase("true"))
      {
         ConsoleHandler ch = new ConsoleHandler();

         if (consoleTimestampMisc.equalsIgnoreCase("true"))
         {
            // Use Java's formatter which displays the date
            ch.setFormatter(new SimpleFormatter());
         }
         else
         {
            // Use ADL's overridden formatter - has NO date/timestamp
            ch.setFormatter(new ADLSimpleFormatter());
         }
         mLoggerMisc.addHandler(ch);
      }

      // If true - will be used to indicate: Print to a file.
      if (fileOutputMisc.equalsIgnoreCase("true"))
      {
         // Setup output log file
         try
         {
            String fileName = new String();
            // Output filename
            fileName = iEnvironmentVariable + File.separator + "config" +
               File.separator + "debug_misc.log";

            FileHandler fh = new FileHandler(fileName, iAppendFile);

            if (fileTimestampMisc.equalsIgnoreCase("true"))
            {
               // Use Java's formatter which displays the date
               fh.setFormatter(new SimpleFormatter());
            }
            else
            {
               // Use ADL's overridden formatter - has NO date/timestamp
               fh.setFormatter(new ADLSimpleFormatter());
            }
            mLoggerMisc.addHandler(fh);
         }
         catch ( IOException ioe )
         {
            System.out.println("Handler IOException Error: "+ioe);
         }
      }

      return;
   }
}
