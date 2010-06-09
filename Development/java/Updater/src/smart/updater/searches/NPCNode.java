package smart.updater.searches;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.PUTFIELD;

import smart.updater.InstructionSearcher;
import smart.updater.MethodSearcher;
import smart.updater.RSClass;
import smart.updater.RSClient;
import smart.updater.Search;

public class NPCNode extends Search{

	@Override
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
		RSClass npc = data.getProperClass("NPC");
		for(Entry<String, ClassGen> c : classes.entrySet()){
			if(c.getValue().isFinal()){
				ClassGen cg = new ClassGen(c.getValue().getJavaClass());
				ConstantPoolGen cpg = cg.getConstantPool();
				for(Method m : c.getValue().getMethods()){
					MethodSearcher mS = new MethodSearcher(m, cg, cpg);
					if(m.getName().equals("<init>") && mS.getTypeCount(npc.className) == 1 &&
							mS.getArgCount() == 1){
						RSClass npcnode = data.addClass("NPCNode", c.getValue().getClassName());
						MethodGen mg = new MethodGen(m, cg.getClassName(), cpg);
						InstructionList il = mg.getInstructionList();
						InstructionSearcher iS = new InstructionSearcher(il, cpg);
						Instruction i = iS.nextPUTFIELD();
						npcnode.addField("GetNPC", ((PUTFIELD) i).getFieldName(cpg));
						return SearchResult.Success;
					}
				}
			}
		}
		return SearchResult.Failure;
	}

}
