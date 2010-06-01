package smart.updater.searches;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.apache.bcel.util.InstructionFinder;

import smart.updater.InstructionSearcher;
import smart.updater.RSClass;
import smart.updater.RSClient;
import smart.updater.Search;

public class NPCIndexArray extends Search {

	@Override
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
		for(Entry<String, ClassGen> c : classes.entrySet()){
			if(c.getValue().getClassName().equals("client")){
				for(Method m : c.getValue().getMethods()){
					if(m.isStatic() && m.isFinal() && m.getReturnType().equals(Type.VOID) &&
							m.getArgumentTypes().length == 0){
						ConstantPoolGen cpg = c.getValue().getConstantPool();
						MethodGen mg = new MethodGen(m, c.getValue().getClassName(), cpg);
						InstructionList il = mg.getInstructionList();
						InstructionFinder f = new InstructionFinder(il);
						Iterator e = f.search("INVOKEVIRTUAL CHECKCAST GETFIELD ASTORE ALOAD GETFIELD IFEQ");
						if(e.hasNext()){
							InstructionHandle[] handles = (InstructionHandle[]) e.next();
							String getHits = ((GETFIELD) handles[5].getInstruction()).getFieldName(cpg);
							InstructionSearcher iS = new InstructionSearcher(il, cpg);
							iS.nextGETSTATIC();
							iS.nextGETSTATIC();
							String npcIndexArray = ((GETSTATIC)iS.current()).getClassName(cpg)+'.'+((GETSTATIC)iS.current()).getFieldName(cpg);
							data.addField("NPCIndexArray", npcIndexArray);
							RSClass character = data.getProperClass("Character");
							character.addField("GetHitDamges", getHits);
							return SearchResult.Success;
						}
					}
				}
			}
		}
		return SearchResult.Failure;
	}

}
