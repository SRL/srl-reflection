package smart.updater.searches;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.util.InstructionFinder;

import smart.updater.InstructionSearcher2;
import smart.updater.RSClass;
import smart.updater.RSClient;
import smart.updater.Search;

public class PlayerDef extends Search{

	public RSClass getPlayerDefClass(RSClient data, HashMap<String, ClassGen> classes){
		for(Entry<String, ClassGen> c : classes.entrySet()){
			ClassGen cg = new ClassGen(c.getValue().getJavaClass());
			ConstantPoolGen cpg = cg.getConstantPool();
			int matches = 0;
			for(Method m : cg.getMethods()){
				MethodGen mg = new MethodGen(m, cg.getClassName(), cpg);
				if(m.getArgumentTypes().length == 19 && m.isFinal() && !m.isStatic()){
					matches++;
				} else
				if(m.getArgumentTypes().length == 12 && m.isFinal() && !m.isStatic()){
					matches++;
				}
				if(m.getName().equals("<init>")){
					InstructionList il = mg.getInstructionList();
					if(il == null || il.getInstructions().length != 6)
						continue;
					InstructionFinder f = new InstructionFinder(il);
                    Iterator e = f.search("ALOAD INVOKESPECIAL ALOAD ICONST PUTFIELD");
                    if (e.hasNext()) {
                    	matches++;
                    }
				}
			}
			if(matches == 4)
				return data.addClass("PlayerDef", cg.getClassName());
		}
		return null;
	}
	@Override
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
		RSClass player = data.getProperClass("Player");
		RSClass playerDef = getPlayerDefClass(data, classes);
		if(playerDef == null)
			return SearchResult.Failure;
		for(Entry<String, ClassGen> c : classes.entrySet()){
			if(c.getValue().getClassName().equals(player.className)){
				for(Field f : c.getValue().getFields()){
					if(f.getType().toString().equals(playerDef.className)){
						player.addField("PlayerDef", f.getName());
						return SearchResult.Success;
					}
				}
			}
		}
		return SearchResult.Failure;
	}

}
