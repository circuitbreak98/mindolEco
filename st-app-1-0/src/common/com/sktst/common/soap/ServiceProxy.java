package com.sktst.common.soap;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPHeaderElement;

public class ServiceProxy {
	 public  static Object CallService (String serviceUrl, String namespace, String methodName, HashMap map) throws Exception {  
	        org.apache.axis.description.OperationDesc oper;
	        org.apache.axis.description.ParameterDesc param;
	       
	        oper = new org.apache.axis.description.OperationDesc();
	        oper.setName(methodName);

	        int length =  map.size();
	        Object [] params = new Object[length];
	        Iterator keys = map.keySet().iterator();
	        int i = 0;
	        while (keys.hasNext()) {
	         String key = keys.next().toString();
	         param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(namespace, key), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
	         oper.addParameter(param);       
	         params[i++] = map.get(key).toString();        
	        }
	       
//	        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(namespace, "fname"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
//	        oper.addParameter(param);
//	        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(namespace, "lname"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
//	        oper.addParameter(param);
	       
	        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
	        oper.setReturnClass(java.lang.String.class);    
	       
//	        oper.setReturnQName(new javax.xml.namespace.QName(namespace, methodName));
//	        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
//	        oper.setUse(org.apache.axis.constants.Use.LITERAL);
	       
	        Service service = new Service();
	        Call call = (Call)service.createCall();       
	        String endpoint = serviceUrl;       
	        call.setTargetEndpointAddress(new URL(endpoint));
	        call.setOperation(oper);
	       
	        call.setUseSOAPAction(true);
	        call.setSOAPActionURI(namespace + methodName);
	        call.setEncodingStyle(null);
	        call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
	        call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
	        call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
	        call.setOperationName(new javax.xml.namespace.QName(namespace, methodName));
	        SOAPHeaderElement soapHeader = new SOAPHeaderElement("id","02930");
	        call.addHeader(soapHeader);
	              
	        return call.invoke(params);    
	 }

}
