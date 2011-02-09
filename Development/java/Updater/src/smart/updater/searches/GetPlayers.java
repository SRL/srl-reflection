package smart.updater.searches;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import smart.updater.InstructionSearcher2;
import smart.updater.RSClass;
import smart.updater.RSClient;
import smart.updater.Search;

public class GetPlayers extends Search{

	@Override
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
		RSClass player = data.getProperClass("Player");
		for(Entry<String, ClassGen> c : classes.entrySet()){
			for(Method m : c.getValue().getMethods()){
				if(m.getName().equals("<clinit>")){
					ClassGen cg = new ClassGen(c.getValue().getJavaClass());
					ConstantPoolGen cpg = cg.getConstantPool();
					MethodGen mg = new MethodGen(m, cg.getClassName(), cpg);
					InstructionList il = mg.getInstructionList();
					if(il == null)
						continue;
					InstructionSearcher2 iS = new InstructionSearcher2(cg, m, il);
					if(iS.nextSIPUSH(2048) != null){
						ANEWARRAY na = iS.nextANEWARRAY();
						if(na != null && na.getLoadClassType(cpg).toString().equals(player.className)){
							FieldInstruction fi = iS.nextFieldInstruction();
							data.addField("GetPlayers", fi.getClassName(cpg)+'.'+fi.getFieldName(cpg));
							return SearchResult.Success;
						}
					}
				}
			}
		}
		return SearchResult.Failure;
	}

}
