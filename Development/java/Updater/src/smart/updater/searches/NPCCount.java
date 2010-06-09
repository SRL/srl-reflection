package smart.updater.searches;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import smart.updater.InstructionSearcher2;
import smart.updater.RSClient;
import smart.updater.Search;

public class NPCCount extends Search{

	@Override
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
		for(Entry<String, ClassGen> c : classes.entrySet()){
			ClassGen cg = new ClassGen(c.getValue().getJavaClass());
			ConstantPoolGen cpg = c.getValue().getConstantPool();
			for(Method m : c.getValue().getMethods()){
				if(m.isFinal() && m.isStatic() && m.getReturnType().equals(Type.VOID)){
					MethodGen mg = new MethodGen(m, cg.getClassName(), cpg);
					InstructionList il = mg.getInstructionList();
					if(il != null){
						InstructionSearcher2 iS = new InstructionSearcher2(cg, m, il);
						if(iS.nextLDC("gnp2 pos:") != null){
							FieldInstruction fi = iS.nextFieldInstruction();
							data.addField("NPCCount", fi.getClassName(cpg)+'.'+fi.getFieldName(cpg));
							return SearchResult.Success;
						}
					}
				}
			}
		}
		return SearchResult.Failure;
	}

}
