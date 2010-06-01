package smart.updater.searches;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import smart.updater.RSClass;
import smart.updater.RSClient;
import smart.updater.Search;

public class NPCNodes extends Search{

	@Override
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
		for(Entry<String, ClassGen> c : classes.entrySet()){
			RSClass npcNode = data.getProperClass("NPCNode");
			//if(c.getValue()){
				ConstantPoolGen cpg = c.getValue().getConstantPool();
				for(Field f : c.getValue().getFields()){
					if(f.isStatic() && f.getType().toString().equals(npcNode.className+"[]")){
						data.addField("NPCNodes", c.getValue().getClassName()+'.'+f.getName());
						return SearchResult.Success;
					}
				}
			//}
		}
		return SearchResult.Failure;
	}

}
