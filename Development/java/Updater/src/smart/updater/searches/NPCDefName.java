package smart.updater.searches;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import smart.updater.RSClass;
import smart.updater.RSClient;
import smart.updater.Search;

public class NPCDefName extends Search{

	@Override
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
		RSClass npcDef = data.getProperClass("NPCDef");
		for(Entry<String, ClassGen> c : classes.entrySet()){
			if(c.getValue().getClassName().equals(npcDef.className)){
				int count = 0;
				for(Field f : c.getValue().getFields()){
					if(!f.isStatic()){
						if(f.getType().equals(Type.STRING)){
							npcDef.addField("GetName", f.getName());
							count++;
						}
						if(f.getType().toString().equals("java.lang.String[]")){
							npcDef.addField("GetActions", f.getName());
							count++;
						}
						if(count == 2)
							return SearchResult.Success;
					}
				}
			}
		}
		return SearchResult.Failure;
	}

}
