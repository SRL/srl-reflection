package smart.updater.searches;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import smart.updater.InstructionSearcher2;
import smart.updater.RSClient;
import smart.updater.Search;

public class PlayerIndexArray extends Search{

	@Override
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
		for(Entry<String, ClassGen> c : classes.entrySet()){
			if(c.getValue().getClassName().equals("client")){
				for(Method m : c.getValue().getMethods()){
					if(m.isStatic() && m.isFinal() && m.getReturnType().equals(Type.VOID) &&
							m.getArgumentTypes().length == 0){
						ClassGen cg = new ClassGen(c.getValue().getJavaClass());
						ConstantPoolGen cpg = cg.getConstantPool();
						MethodGen mg = new MethodGen(m, cg.getClassName(), cpg);
						InstructionList il = mg.getInstructionList();
						if(il == null)
							continue;
						InstructionSearcher2 iS = new InstructionSearcher2(cg, m, il);
						if(iS.nextSIPUSH(200)!= null){
							if(iS.nextBIPUSH(50)!= null){
								iS = new InstructionSearcher2(cg, m, il);
								FieldInstruction fi = iS.nextGETSTATIC();
								data.addField("PlayerCount", fi.getClassName(cpg)+'.'+fi.getFieldName(cpg));
								fi = iS.nextGETSTATIC();
								data.addField("PlayerIndexArray", fi.getClassName(cpg)+'.'+fi.getFieldName(cpg));
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
