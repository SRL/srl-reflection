package smart.updater.searches;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import smart.updater.InstructionSearcher2;
import smart.updater.RSClass;
import smart.updater.RSClient;
import smart.updater.Search;

public class HPRatio extends Search{

	@Override
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
		RSClass character = data.getProperClass("Character");
		for(Entry<String, ClassGen> c : classes.entrySet()){
			ClassGen cg = new ClassGen(c.getValue().getJavaClass());
			ConstantPoolGen cpg = cg.getConstantPool();
			for(Method m : cg.getMethods()){
				if(m.isStatic() && m.isFinal() && m.getReturnType().equals(Type.VOID) && m.getArgumentTypes().length == 7){
					MethodGen mg = new MethodGen(m, cg.getClassName(), cpg);
					InstructionList il = mg.getInstructionList();
					if(il == null)
						continue;
					InstructionSearcher2 iS = new InstructionSearcher2(cg, m, il);
					if(iS.nextSIPUSH(255) != null){
						FieldInstruction fi = iS.previousGETFIELD();
						if(fi != null && fi.getClassName(cpg).equals(character.className)){
							fi = iS.nextGETFIELD();
							if(fi != null && fi.getClassName(cpg).equals(character.className)){
								character.addField("HPRatio", fi.getFieldName(cpg));
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
