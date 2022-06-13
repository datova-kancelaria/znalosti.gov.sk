package sk.gov.knowledgegraph.common;

public class URIView {

	public String formatResultString(String inString)
	{
		
		if(inString.contains("http"))
			return "<<a href=resource?uri="+inString.replace("#","%23")+">"+inString+"</a>>";
		else
		    return inString;
	}
}
