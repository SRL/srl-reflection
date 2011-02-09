package smart.updater.searches;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import smart.updater.InstructionSearcher2;
import smart.updater.RSClass;
import smart.updater.RSClient;
import smart.updater.Search;

public class NPCLevel extends Search{

	@Override
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
		RSClass npcDef = data.getProperClass("NPCDef");
		for(Entry<String, ClassGen> c : classes.entrySet()){
			if(c.getValue().getClassName().equals(npcDef.className)){
				ClassGen cg = new ClassGen(c.getValue().getJavaClass());
				ConstantPoolGen cpg = c.getValue().getConstantPool();
				for(Method m : c.getValue().getMethods()){
					if(m.getReturnType().equals(Type.VOID)){
						InstructionSearcher2 search = new InstructionSearcher2(cg, m);
						while(search.nextBIPUSH() != null) {
							Instruction i = search.current();
							if(((BIPUSH)i).getValue().intValue() == -98 || ((BIPUSH)i).getValue().intValue() == 97){
								FieldInstruction npcLevel = search.previousFieldInstruction();
								npcDef.addField("GetLevel", npcLevel.getFieldName(cpg));
								return SearchResult.Success;
							}
						}
					}
				}
			}
		}
		return SearchResult.Failure;
	}

}
