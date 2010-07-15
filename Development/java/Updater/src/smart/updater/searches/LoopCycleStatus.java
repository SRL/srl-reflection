package smart.updater.searches;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import smart.updater.InstructionSearcher2;
import smart.updater.MethodSearcher;
import smart.updater.RSClass;
import smart.updater.RSClient;
import smart.updater.RSField;
import smart.updater.Search;

public class LoopCycleStatus extends Search{

	@Override
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
		RSField loopcycle = data.getProperField("LoopCycle");
		RSClass player = data.getProperClass("Player");
		RSClass character = data.getProperClass("Character");
		if(loopcycle == null)
			return SearchResult.Failure;
		for(Entry<String, ClassGen> c : classes.entrySet()){
			ClassGen cg = new ClassGen(c.getValue().getJavaClass());
			ConstantPoolGen cpg = cg.getConstantPool();
			for(Method m : cg.getMethods()){
				MethodGen mg = new MethodGen(m, cg.getClassName(), cpg);
				MethodSearcher mS = new MethodSearcher(m, cg, cpg);
				if(m.isStatic() && m.isFinal() && m.getReturnType().equals(Type.VOID) &&
						mS.getTypeCount(player.className) == 1){
					InstructionList il = mg.getInstructionList();
					if(il == null)
						continue;
					InstructionSearcher2 iS = new InstructionSearcher2(cg, m, il);
					if(iS.nextSIPUSH(-300) != null){
						FieldInstruction fi = iS.previousGETSTATIC();
						if(fi != null && (fi.getClassName(cpg)+'.'+fi.getFieldName(cpg)).equals(loopcycle.path)){
							fi = iS.nextPUTFIELD();
							if(fi != null && fi.getClassName(cpg).equals(character.className)){
								character.addField("LoopCycleStatus", fi.getFieldName(cpg));
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
