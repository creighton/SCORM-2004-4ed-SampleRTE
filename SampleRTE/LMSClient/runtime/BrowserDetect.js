/*************************************************************************
**
** Filename:  BrowserDetect.js
**
** File Description:
**    Detects the browser version and alerts the user if it is not
**    supported by this version of the Test Suite.
**
** Author: ADLI Project
**
** Module/Package Name:  none
** Module/Package Description: none
**
** Design Issues:
**
** Implementation Issues:
** Known Problems:
** Side Effects:
**
** References: ADL SCORM
**
***************************************************************************
  
ADL SCORM 2004 4th Edition Sample Run-Time Environment

The ADL SCORM 2004 4th Ed. Sample Run-Time Environment is licensed under
Creative Commons Attribution-Noncommercial-Share Alike 3.0 United States.

The Advanced Distributed Learning Initiative allows you to:
  *  Share - to copy, distribute and transmit the work.
  *  Remix - to adapt the work. 

Under the following conditions:
  *  Attribution. You must attribute the work in the manner specified by the author or
     licensor (but not in any way that suggests that they endorse you or your use
     of the work).
  *  Noncommercial. You may not use this work for commercial purposes. 
  *  Share Alike. If you alter, transform, or build upon this work, you may distribute
     the resulting work only under the same or similar license to this one. 

For any reuse or distribution, you must make clear to others the license terms of this work. 

Any of the above conditions can be waived if you get permission from the ADL Initiative. 
Nothing in this license impairs or restricts the author's moral rights.

**************************************************************************/

    var Netscape = false;
    var IE = false;
	var Firefox = false;
    var browserName = null;
    var browserVersion = 0;
    var browserOK = null;
    var fullVersion = 0;
    
/*******************************************************************************
**
** Function: DetectBrowser()
** Inputs:  None
** Return:  None
**
** Description:
**    Determines the browser information, i.e. name and version.
**    
**    This function can detect the following browsers:
**
**    o Internet Explorer
**    o Netscape
**    
**    Note: If the browser is not recognised, empty strings are returned.
**
*******************************************************************************/
    function DetectBrowser()
    {
        var browser = navigator.userAgent.toLowerCase();
                       
        if (browser.indexOf("netscape") > -1)
        {
            browserName = "Netscape";
            browserVersion = ExtractFullVersion(browser, "netscape");
            Netscape = true;
        }       
        else if (browser.indexOf("msie") > -1)
        {   
            browserName = "Internet Explorer";
            fullVersion = ExtractFullVersion(browser, "msie");
            browserVersion = ExtractShortVersion(fullVersion);
            IE = true;
        } 
		else if (browser.indexOf("firefox") > -1)
		{
			browserName = "Mozilla Firefox";
			browserVersion = ExtractFullVersion(browser, "firefox");
			Firefox = true;
		}
        else
        {
            browserName = "";
            browserVersion = "";
        }
        browserOK = DetectUnsupportedBrowser();
		
    }

/*******************************************************************************
**
** Function: DetectUnsupportedBrowser()
** Inputs:  None
** Return:  true/false
**
** Description:
**    Alerts the user if they are using a browser not supported by the current
**    version of the Test Suite.
**
*******************************************************************************/
	function DetectUnsupportedBrowser()
	{
	   // see if the browser and version are supported
//	   if( ( (!IE) && (!Netscape) && (!Firefox) ) || 
//	       ( (IE) && (navigator.userAgent.indexOf("Opera") > -1) ) ||
//	       ( (IE) && (browserVersion < 6) ) ||
//	       ( (Netscape) && (browserVersion < 7) ) )
//	   {
//	      alert("Browser not tested.");
//	      return false;
//	   }
//	   else
//	   {
//	      return true;
//	   }
	   return true;
	}    

/*******************************************************************************
**
** Function: ExtractShortVersion()
** Inputs:  version
** Return:  number
**
** Description:
**    Extracts a number from a version, e.g. "1.2.3.4" => 1.2.
**
*******************************************************************************/
    function ExtractShortVersion(version)
    {
        var number = "0";
        var numberList = new Array();
        
        if (version != "")
        {
            numberList = version.split(".");
            if (numberList.length > 1)
            {
                 number = numberList[0] + "." + numberList[1]; 
            }
            else
            {
                number = numberList[0]; 
            }
        }
        return number;
    }

/*******************************************************************************
**
** Function: ExtractFullVersion()
** Inputs:  source, pattern
** Return:  version
**
** Description:
**    Extracts the first version number after the specified pattern
**    in the specified source.  A version number is of the format
**    x[.y]*, hence 1.2 and 1.2.3 are both valid version numbers.
**
*******************************************************************************/
    function ExtractFullVersion(source, pattern)
    {
        var character;
        var found;
        var index = source.indexOf(pattern);
        var version = "";               
        if (index > 0)
        {
            found = false;
            index += pattern.length;
                while (index <= source.length)
                {
                    character = source.substr(index, 1);
                    if(((character >= "0") && (character <= "9")) || (character == "."))
                    {
                        version += character;
                    }
                    index++;
                }
        }
        return version;
    }
