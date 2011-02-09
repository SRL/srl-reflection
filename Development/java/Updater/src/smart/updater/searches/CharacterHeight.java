package smart.updater.searches;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import smart.updater.InstructionSearcher;
import smart.updater.RSClass;
import smart.updater.RSClient;
import smart.updater.Search;

public class CharacterHeight extends Search{

	@Override
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
		RSClass character = data.getProperClass("Character"); 
		for(Entry<String, ClassGen> c : classes.entrySet()){
			if(c.getValue().getClassName().equals(character.className)){
				ConstantPoolGen cpg = c.getValue().getConstantPool();
				for(Method m : c.getValue().getMethods()){
					if(m.getName().equals("<init>")){
						MethodGen mg = new MethodGen(m, c.getValue().getClassName(), cpg);
						InstructionList il = mg.getInstructionList();
						InstructionSearcher iS = new InstructionSearcher(il, cpg);
						while(iS.next() != null){
							Instruction i = iS.current();
							if(i instanceof LDC){
								if(((LDC) i).getValue(cpg).equals(-32768)){
									i = iS.nextPUTFIELD();
									character.addField("GetHeight", ((PUTFIELD) i).getFieldName(cpg));
									return SearchResult.Success;
								}
							}
						}
					}
				}
			}
		}
		return SearchResult.Failure;
	}

}
