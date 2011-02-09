package smart.updater.searches;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.Type;

import smart.updater.RSClass;
import smart.updater.RSClient;
import smart.updater.Search;

public class NPC extends Search{

	@Override
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
		RSClass character = data.getProperClass("Character"); 
		RSClass player = data.getProperClass("Player");
		for(Entry<String, ClassGen> c : classes.entrySet()){
			if(c.getValue().getSuperclassName().equals(character.className) &&
					!c.getValue().getClassName().equals(player.className)){
				RSClass npc = data.addClass("NPC", c.getValue().getClassName());
				for(Field f : c.getValue().getFields()){
					if(!f.isStatic() && !f.getType().equals(Type.INT))
						for(Entry<String, ClassGen> c2 : classes.entrySet()){
							if(f.getType().toString().equals(c2.getValue().getClassName())){
								npc.addField("NPCDef", f.getName());
								data.addClass("NPCDef", f.getType().toString());
								return SearchResult.Success; 
							}
						}
				}
			}
		}
		return SearchResult.Failure;
	}

}
