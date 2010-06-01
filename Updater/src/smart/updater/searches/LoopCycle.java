package smart.updater.searches;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import smart.updater.InstructionSearcher2;
import smart.updater.RSClient;
import smart.updater.Search;

public class LoopCycle extends Search{

	@Override
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
		for(Entry<String, ClassGen> c : classes.entrySet()){
			if(c.getValue().getClassName().equals("client")){
				ClassGen cg = new ClassGen(c.getValue().getJavaClass());
				ConstantPoolGen cpg = cg.getConstantPool();
				for(Method m : cg.getMethods()){
					InstructionSearcher2 iS = new InstructionSearcher2(cg, m);
					if(iS.nextSIPUSH(1500)!= null){
						FieldInstruction fi = iS.previousFieldInstruction();
						data.addField("LoopCycle", fi.getClassName(cpg)+'.'+fi.getFieldName(cpg));
						return SearchResult.Success;
					}
				}
			}
		}
		return SearchResult.Failure;
	}

}
