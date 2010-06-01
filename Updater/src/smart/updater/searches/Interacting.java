package smart.updater.searches;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import smart.updater.InstructionSearcher2;
import smart.updater.MethodSearcher;
import smart.updater.RSClass;
import smart.updater.RSClient;
import smart.updater.Search;

public class Interacting extends Search{
	
	@Override
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
		RSClass character = data.getProperClass("Character"); 
		for(Entry<String, ClassGen> c : classes.entrySet()){
			ClassGen cg = new ClassGen(c.getValue().getJavaClass());
			ConstantPoolGen cpg = cg.getConstantPool();
			for(Method m : cg.getMethods()){
				MethodGen mg = new MethodGen(m, c.getValue().getClassName(), cpg);
				MethodSearcher mS = new MethodSearcher(m, cg, cpg);
				if(m.isStatic() && m.isFinal() && m.getReturnType().equals(Type.INT) && mS.getArgCount() == 2
						&& mS.getTypeCount(character.className) == 1){
					InstructionList il = mg.getInstructionList();
					if(il == null)
						continue;
					InstructionSearcher2 iS = new InstructionSearcher2(cg, m, il);
					while(iS.nextLDC() != null){
						LDC i = (LDC) iS.current();
						if(i.getValue(cpg).equals(-32769) || i.getValue(cpg).equals(32768)){
							FieldInstruction fi = iS.previousFieldInstruction();
							if(fi.getClassName(cpg).equals(character.className)){
								character.addField("GetInteracting", fi.getFieldName(cpg));
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
