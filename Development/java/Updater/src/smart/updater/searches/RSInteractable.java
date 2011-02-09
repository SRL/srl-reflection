package smart.updater.searches;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.bcel.generic.ClassGen;

import smart.updater.RSClass;
import smart.updater.RSClient;
import smart.updater.Search;

public class RSInteractable extends Search{

	@Override
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
		RSClass animable = data.getProperClass("Animable");
		for(Entry<String, ClassGen> c : classes.entrySet()){
			if(c.getValue().getClassName().equals(animable.className)){
				data.addClass("RSInteractable", c.getValue().getSuperclassName());
				return SearchResult.Success;
			}	
		}
		return SearchResult.Failure;
	}

}
