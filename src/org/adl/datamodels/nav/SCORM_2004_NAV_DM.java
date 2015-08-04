/******************************************************************************

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

******************************************************************************/

package org.adl.datamodels.nav;

import org.adl.datamodels.DataModel;
import org.adl.datamodels.DMElementDescriptor;
import org.adl.datamodels.DMDelimiterDescriptor;
import org.adl.datamodels.DMRequest;
import org.adl.datamodels.DMErrorCodes;
import org.adl.datamodels.DMProcessingInfo;
import org.adl.datamodels.RequestToken;
import org.adl.datamodels.DMElement;
import org.adl.datamodels.datatypes.SPMRangeValidator;
import org.adl.datamodels.datatypes.VocabularyValidator;
import org.adl.datamodels.datatypes.URIValidator;
import org.adl.datamodels.ieee.SCORM_2004_DM;
import org.adl.sequencer.ADLValidRequests;
import org.adl.sequencer.SeqNavRequests;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.io.Serializable;

/**
 * <strong>Filename:</strong> SCORM_2004_NAV_DM.java<br><br>
 * 
 * <strong>Description:</strong> This class implements the set of navigation events 
 * defined in the SCORM 2004.
 * 
 * @author ADL Technical Team
 */
public class SCORM_2004_NAV_DM extends DataModel implements Serializable
{
   /**
    * Constant for the smallest permitted maximum for data store
    */
   public static final int DATA_STORE_SPM = 64000;
   
   /**
    * Constant for the smallest permitted maximum for the data collection.
    */
   public static final int DATA_SPM = 250;
   
   /**
    * Describes the current known 'valid' set of navigation requests.
    */
   ADLValidRequests mNavRequests = null;
 
   /** 
    * Describes the current navigation request.
    */
   String mCurRequest = null;

   /**
    * Describes the jump location.
    */
   String mJumpLocation = null;
   
   /**
    * Describes the dot-notation binding string for this data model.
    */
   private String mBinding = "adl";

   /**
    * Describes the data model elements managed by this data model.
    */
   private Hashtable mElements = null;
   
   /**
    * Default constructor required for serialization support.  Creates a 
    * complete set of navigation data model information.
    */
   public SCORM_2004_NAV_DM()
   {
      Vector children = null;

      DMElement element = null;
      DMElementDescriptor desc = null;
      DMDelimiterDescriptor del = null;

      mElements = new Hashtable();

      children = new Vector();

      // request
      String [] vocab = { "continue", "previous", "choice", "exit", "exitAll",
         "abandon", "abandonAll", "suspendAll", "_none_", "jump"};

      desc = new DMElementDescriptor("request", "_none_", 
                                     new VocabularyValidator(vocab));

      // The 'choice' request will include a delimiter
      del = new DMDelimiterDescriptor("target", null, 
                                      new URIValidator()); 

      desc.mDelimiters = new Vector();
      desc.mDelimiters.add(del);

      children.add(desc);

      Vector subChildren = new Vector();

      // continue
      String [] status = { "true", "false", "unknown"};
      desc = new DMElementDescriptor("continue", "unknown", 
                                     new VocabularyValidator(status));

      desc.mIsWriteable = false;
      subChildren.add(desc);

      // previous
      desc = new DMElementDescriptor("previous", "unknown", 
                                     new VocabularyValidator(status));

      desc.mIsWriteable = false;
      subChildren.add(desc);

      // choice
      desc = new DMElementDescriptor("choice", "unknown", 
                                     new VocabularyValidator(status));

      desc.mIsWriteable = false;
      subChildren.add(desc);
      
      // jump
      desc = new DMElementDescriptor("jump", "unknown", 
                                     new VocabularyValidator(status));

      desc.mIsWriteable = false;
      subChildren.add(desc);
      
      // request_valid
      desc = new DMElementDescriptor("request_valid", subChildren);
      children.add(desc);

      desc = new DMElementDescriptor("nav", children);

      // Create and add this element to the data model
      element = new SCORM_2004_NAV_DMElement(desc, null, this);

      mElements.put(desc.mBinding, element);
      
      // data
      children = new Vector();

      // data id
      desc = new DMElementDescriptor("id", null, LONG_SPM,
                       new URIValidator(LONG_SPM, "long_identifier_type"));
      
      desc.mIsWriteable = false;
      desc.mIsUnique = true;

      children.add(desc);
      
      desc = new DMElementDescriptor("store", null, DATA_STORE_SPM, 
                                     new SPMRangeValidator(DATA_STORE_SPM));
      
      desc.mDependentOn = new Vector();
      desc.mDependentOn.add(new String("id"));
      
      children.add(desc);
      
      // adl.data
      desc = new DMElementDescriptor("data", children, DATA_SPM);
      
      // Create and add to data model
      element = new SCORM_2004_NAV_DMElement(desc, null, this);
      
      mElements.put(desc.mBinding, element);
   }

   /**
    * Processes an equals() request against this data model. Compares two 
    * values of the same data model element for equality.
    * 
    * @param iRequest The request (<code>DMRequest</code>) being processed.
    * 
    * @param iValidate Indicates if the provided value should be validated.
    * 
    * @return An abstract data model error code indicating the result of this
    *         operation.
    */
   public int equals(DMRequest iRequest, boolean iValidate)
   {

      // Assume no processing errors
      int result = DMErrorCodes.NO_ERROR;

      // Create an 'out' variable
      DMProcessingInfo pi = new DMProcessingInfo();

      // Process this request
      result = findElement(iRequest, pi);

      // If we found the 'leaf' elmeent, finish the request
      if ( result == DMErrorCodes.NO_ERROR )
      {
         RequestToken tok = iRequest.getNextToken();

         // Before processing, make sure this is the last token in the request
         if ( !iRequest.hasMoreTokens() )
         {
            // Make sure this is a  Value token
            if ( tok.getType() == RequestToken.TOKEN_VALUE )
            {
               result = pi.mElement.equals(tok);
            }
            else
            {
               // Wrong type of token -- value expected
               result = DMErrorCodes.INVALID_REQUEST;
            }
         }
         else
         {
            // Too many tokens
            result = DMErrorCodes.INVALID_REQUEST;
         }
      }

      return result;
   }

   /**
    * Describes this data model's binding string.
    * 
    * @return This data model's binding string.
    */
   public String getDMBindingString()
   {
      return mBinding;
   }

   /**
    * Provides the requested data model element.
    * 
    * @param iElement Describes the requested element's dot-notation bound name.
    * 
    * @return The <code>DMElement</code> corresponding to the requested element
    *         or <code>null</code> if the element does not exist in the data
    *         model.
    */
   public DMElement getDMElement(String iElement)
   {
      DMElement element = (DMElement)mElements.get(iElement);

      return element;
   }


   /**
    * Provides the current navigation request communicated by the SCO.
    * 
    * @return The current navigation request.
    */
   public String getNavRequest()
   {
      String request = null;
      int navEvent = SeqNavRequests.NAV_NONE;

      if ( mCurRequest != null )
      {
         if ( mCurRequest.equals("continue") )
         {
            navEvent = SeqNavRequests.NAV_CONTINUE;
         }
         else if ( mCurRequest.equals("previous") )
         {
            navEvent = SeqNavRequests.NAV_PREVIOUS;
         }
         else if ( mCurRequest.equals("exit") )
         {
            navEvent = SeqNavRequests.NAV_EXIT;
         }
         else if ( mCurRequest.equals("exitAll") )
         {
            navEvent = SeqNavRequests.NAV_EXITALL;
         }
         else if ( mCurRequest.equals("abandon") )
         {
            navEvent = SeqNavRequests.NAV_ABANDON;
         }
         else if ( mCurRequest.equals("abandonAll") )
         {
            navEvent = SeqNavRequests.NAV_ABANDONALL;
         }
         else if ( mCurRequest.equals("suspendAll") )
         {
            navEvent = SeqNavRequests.NAV_SUSPENDALL;
         }
         else if ( mCurRequest.equals("_none_") )
         {
            navEvent = SeqNavRequests.NAV_NONE;
         }
         else if ( mCurRequest.equals("jump") )
         {
            navEvent = SeqNavRequests.NAV_JUMP;
         }
         else
         {
            // This must be a target for choice
            request = mCurRequest;
         }
      }

      if ( request == null )
      {
         request = Integer.toString(navEvent);
      }

      return request;
   }


   /**
    * Processes a GetValue() request against this data model.
    * 
    * @param iRequest The (<code>DMRequest</code>) being processed.
    * 
    * @param oInfo    Provides the value returned by this request.
    * 
    * @return A data model error code indicating the result of this
    *         operation.
    */
   public int getValue(DMRequest iRequest, DMProcessingInfo oInfo)
   {
      // Assume no processing errors
      int result = DMErrorCodes.NO_ERROR;

      // Create an 'out' variable
      DMProcessingInfo pi = new DMProcessingInfo();

      // Process this request
      result = findElement(iRequest, pi);

      // If we found the 'leaf' elmeent, finish the request
      if ( result == DMErrorCodes.NO_ERROR )
      {
         RequestToken tok = iRequest.getNextToken();

         // Before processing, make sure this is the last token in the request
         if ( !iRequest.hasMoreTokens() )
         {
            result = pi.mElement.getValue(tok,
                                          iRequest.isAdminRequest(),
                                          iRequest.supplyDefaultDelimiters(), 
                                          oInfo);
         }
         else
         {
            // Too many tokens
            result = DMErrorCodes.INVALID_REQUEST;
         }
      }

      return result;
   }

   /**
    * Performs data model specific initialization.
    * 
    * @return An abstract data model error code indicating the result of this
    *         operation.
    */
   public int initialize()
   {
      return DMErrorCodes.NO_ERROR;
   }


   /**
    * Sets the current 'known' set of valid navigation requests for the SCO
    * to the SCO's instance of the SCORM Navigation Data Model.
    * 
    * @param iValid  The current 'known' set of valid navigation requests.
    */
   public void setValidRequests(ADLValidRequests iValid)
   {
      mNavRequests = iValid;
   }


   /**
    * Processes a SetValue() request against this data model.  Checks the 
    * request for validity.
    * 
    * @param iRequest The request (<code>DMRequest</code>) being processed.
    * 
    * @return A data model error code indicating the result of this
    *         operation.
    */
   public int setValue(DMRequest iRequest)
   {
      return setValue(iRequest, false);
   }
   
   /**
    * Processes a SetValue() request against this data model.  Checks the 
    * request for validity.
    * 
    * @param iRequest The request (<code>DMRequest</code>) being processed.
    * 
    * @param iSetBySCO Identifies whether or not this value was set by the SCO.
    * 
    * @return A data model error code indicating the result of this
    *         operation.
    */
   public int setValue(DMRequest iRequest, boolean iSetBySCO)
   {
      // Assume no processing errors
      int result = DMErrorCodes.NO_ERROR;

      // Create an 'out' variable
      DMProcessingInfo pi = new DMProcessingInfo();

      // Process this request
      result = findElement(iRequest, pi);

      // If we found the 'leaf' element, finish the request
      if ( result == DMErrorCodes.NO_ERROR )
      {
         RequestToken tok = iRequest.getNextToken();
         
         // if the request was for adl.data.n.store._access.. tok will be null
         // this is already handled in the processrequest in the Nav DM Element for store
         if ( tok == null && pi.mValue.equals("_access") )
         {
            return result;
         }
         
         // Before processing, make sure this is the last token in the requset
         if ( !iRequest.hasMoreTokens() )
         {
            
            // Make sure this is a Value token
            if ( tok.getType() == RequestToken.TOKEN_VALUE )
            {
               if ( result == DMErrorCodes.NO_ERROR )
               {
                  result = pi.mElement.setValue(tok, iRequest.isAdminRequest());
                  if ( result == DMErrorCodes.NO_ERROR )
                  {
                   pi.mElement.setBySCO(iSetBySCO);
                  }
               }
            }
            else
            {
               // Wrong type of token -- value expected
               result = DMErrorCodes.INVALID_REQUEST;
            }
         }
         else
         {
            // Too many tokens
            result = DMErrorCodes.INVALID_REQUEST;
         }
      }

      return result;
   }

   /**
    * Displays the contents of the entire data model.
    */
   public void showAllElements()
   {
     // Not implemented at this time
   }

   /**
    * Performs data model specific termination.
    * 
    * @return A data model error code indicating the result of this
    *         operation.
    */
   public int terminate()
   {
      // Clear the current nav request
      DMRequest req = new DMRequest("adl.nav.request", "_none_", true);
      
      // Remove the data model token, since we do not need the return
      // value, there is no need to assign it to a local variable
      req.getNextToken();

      // Invoke a SetValue() method call sending in the DMRequest.  There is
      // no need to capture the return from the setValue(), therefore there
      // is no need to assign it to a local variable
      setValue(req);
      
      mCurRequest = null;

      // Clear the current set of valid navigation requests
      mNavRequests = null;

      return DMErrorCodes.NO_ERROR;
   }

   /**
    * Processes a validate() request against this data model.
    * 
    * @param iRequest The (<code>DMRequest</code>) being processed.
    * 
    * @return A data model error code indicating the result of this
    *         operation.
    */
   public int validate(DMRequest iRequest)
   {
      // Assume no processing errors
      int result = DMErrorCodes.NO_ERROR;

      // Create an 'out' variable
      DMProcessingInfo pi = new DMProcessingInfo();

      // Process this request
      result = findElement(iRequest, pi);

      // If we found the 'leaf' element, finish the request
      if ( result == DMErrorCodes.NO_ERROR )
      {
         RequestToken tok = iRequest.getNextToken();

         // Before processing, make sure this is the last token in the request
         if ( !iRequest.hasMoreTokens() )
         {
            // Make sure this is a Value token
            if ( tok.getType() == RequestToken.TOKEN_VALUE )
            {
               result = pi.mElement.validate(tok);
            }
            else
            {
               // Wrong type of token -- value expected
               result = DMErrorCodes.INVALID_REQUEST;
            }
         }
         else
         {
            // Too many tokens
            result = DMErrorCodes.INVALID_REQUEST;
         }
      }

      return result;
   }
   
   /**
    * Gets the Jump Location
    * 
    * @return the target to be jumped too
    */
   public String getJumpLocation()
   {
      return mJumpLocation;
   }

   /**
    * Processes a data model request by finding the target leaf element.
    * If the requested value is found, it is returned in the parameter
    * oInfo.
    * 
    * @param iRequest The (<code>DMRequest</code>) being processed.
    * 
    * @param oInfo    Provides the value returned by this request.
    * 
    * @return An abstract data model error code indicating the result of this
    *         operation.
    */
   private int findElement(DMRequest iRequest, DMProcessingInfo oInfo)
   {
      // Assume no processing errors
      int result = DMErrorCodes.NO_ERROR;

      // Get the first specified element
      RequestToken tok = iRequest.getNextToken();

      if ( tok != null && tok.getType() == RequestToken.TOKEN_ELEMENT )
      {

         DMElement element = (DMElement)mElements.get(tok.getValue());

         if ( element != null )
         {

            oInfo.mElement = element;

            // Check if we need to stop before the last token
            tok = iRequest.getCurToken();
            boolean done = false;

            if ( tok != null )
            {
               if ( iRequest.isGetValueRequest() )
               {
                  if ( tok.getType() == RequestToken.TOKEN_ARGUMENT )
                  {
                     // We're done
                     done = true;
                  }
                  else if ( tok.getType() == RequestToken.TOKEN_VALUE )
                  {
                     // Get requests cannot have value tokens
                     result = DMErrorCodes.INVALID_REQUEST;

                     done = true;
                  }
               }
               else
               {
                  if ( tok.getType() == RequestToken.TOKEN_VALUE )
                  {
                     // We're done
                     done = true;
                  }
                  else if ( tok.getType() == RequestToken.TOKEN_ARGUMENT )
                  {
                     // Set requests cannot have argument tokens
                     result = DMErrorCodes.INVALID_REQUEST;

                     done = true;
                  }
               }
            }

            // Process remaining tokens
            while ( !done && iRequest.hasMoreTokens() && 
                    result == DMErrorCodes.NO_ERROR )
            {
               result = element.processRequest(iRequest, oInfo);

               // Move to the next element if processing was successful
               if ( result == DMErrorCodes.NO_ERROR )
               {
                  element = oInfo.mElement;
               }
               else
               {
                  oInfo.mElement = null;
               }

               // Check if we need to stop before the last token
               tok = iRequest.getCurToken();

               if ( tok != null )
               {
                  if ( iRequest.isGetValueRequest() )
                  {
                     if ( tok.getType() == RequestToken.TOKEN_ARGUMENT )
                     {
                        // We're done
                        done = true;
                     }
                     else if ( tok.getType() == RequestToken.TOKEN_VALUE )
                     {
                        // Get requests cannot have value tokens
                        result = DMErrorCodes.INVALID_REQUEST;
   
                        done = true;
                     }
                  }
                  else
                  {
                     if ( tok.getType() == RequestToken.TOKEN_VALUE )
                     {
                        // We're done
                        done = true;
                     }
                     else if ( tok.getType() == RequestToken.TOKEN_ARGUMENT )
                     {
                        // Set requests cannot have argument tokens
                        result = DMErrorCodes.INVALID_REQUEST;
   
                        done = true;
                     }
                  }
               }
            }
         }
         else
         {
            // Unknown element
            result = DMErrorCodes.UNDEFINED_ELEMENT;
         }
      }
      else
      {
         // No initial element specified
         result = DMErrorCodes.INVALID_REQUEST;
      }
      
      // Make sure we are at a leaf element
      if ( result == DMErrorCodes.NO_ERROR )
      {
         if ( oInfo.mElement.getDescription().mChildren != null)
         {
            // Unknown element
            result = DMErrorCodes.UNDEFINED_ELEMENT;
         }
      }

      return result;
   }
   
   public String toJSONString() {
      StringBuilder sb = new StringBuilder();
      Enumeration<String> key = mElements.keys();
      sb.append("{\n");
      sb.append("\"" + mBinding + "._version\": \"1.0\",\n");
      while (key.hasMoreElements()) {
         String k = key.nextElement();
         DMElement val = (DMElement)mElements.get(k);

         sb.append(val.toJSONString(mBinding));
      }
      sb.deleteCharAt(sb.length() -2);
      sb.append("}");
      return sb.toString();
   }
   
   public static void main(String[] args)
   {
      SCORM_2004_NAV_DM dm = new SCORM_2004_NAV_DM();
      DMRequest nav = new DMRequest("adl.nav.request", "{target=SCO1}jump");
      nav.getNextToken();
      
      DMRequest did = new DMRequest("adl.data.0.id", "foo", true);
      did.getNextToken();
      DMRequest dsa = new DMRequest("adl.data.0.store._access", true + "<>" + true, true);
      dsa.getNextToken();
      DMRequest ds = new DMRequest("adl.data.0.store.", "hello", true);
      ds.getNextToken();
      System.out.println(dm.setValue(nav));
      System.out.println(dm.setValue(did));
      System.out.println(dm.setValue(dsa));
      System.out.println(dm.setValue(ds));
      System.out.println(dm.toJSONString());
      System.out.println(dm.getJumpLocation());
   }

} // end SCORM_2004_NAV_DM
