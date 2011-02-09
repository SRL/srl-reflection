package smart.updater.searches;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.InstructionFinder;

import smart.updater.InstructionSearcher;
import smart.updater.MethodSearcher;
import smart.updater.RSClient;
import smart.updater.RSField;
import smart.updater.Search;
import smart.updater.Search.SearchResult;

public class MenuNodeList extends Search{
	
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
        for (Entry<String, ClassGen> c : classes.entrySet()) {
        	for (Method m : c.getValue().getMethods()) {
        		ConstantPoolGen cpg = c.getValue().getConstantPool();
        		ClassGen cg = new ClassGen(c.getValue().getJavaClass());
        		MethodGen gen = new MethodGen(m, c.getValue().getClassName(), cpg);
	            InstructionList il = gen.getInstructionList();
	            if (il == null) {
	             	continue;
	            }
	            InstructionSearcher iS = new InstructionSearcher(il, cpg);
        		MethodSearcher mS = new MethodSearcher(m, cg, cpg);
        		RSField menuOpCount = data.getProperField("MenuOptionCount");
        		if (m.isStatic() && m.isFinal() && m.getReturnType().equals(Type.VOID) && mS.getArgCount() == 11 &&
        				mS.getTypeCount("int")>= 3 && mS.hasField(menuOpCount.path)) {
		            if(iS.nextFieldInstructionOf(menuOpCount.path)!=null){
		            	FieldInstruction i = null;
		            	i = (FieldInstruction)iS.prevGETSTATIC();
		            	data.addField("MenuOpen", i.getClassName(cpg)+'.'+i.getFieldName(cpg));
		            	iS.nextGETSTATIC();
		            }
		            if(iS.nextFieldInstructionOf(menuOpCount.path) != null){
		            	FieldInstruction i = null;
		            	i = (FieldInstruction)iS.prevGETSTATIC();
		            	data.addField("MenuNodeList", i.getClassName(cpg)+'.'+i.getFieldName(cpg));
		            	return SearchResult.Success;
		            }
        		}
        	}
        }
        return SearchResult.Failure;
    }
}
