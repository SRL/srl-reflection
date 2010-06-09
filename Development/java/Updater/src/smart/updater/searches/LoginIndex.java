package smart.updater.searches;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.InstructionFinder;

import smart.updater.InstructionSearcher;
import smart.updater.RSClass;
import smart.updater.RSClient;
import smart.updater.Search;

public class LoginIndex extends Search{

	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
        for (Entry<String, ClassGen> c : classes.entrySet()) {
        	if(c.getValue().getClassName().equals("client"))
        	{
	            for (Method m : c.getValue().getMethods()) {
	            	if(m.isFinal() && m.getReturnType().equals(Type.STRING)){
	                    ConstantPoolGen cpg = c.getValue().getConstantPool();
	                    MethodGen gen = new MethodGen(m, c.getValue().getClassName(), cpg);
	                    InstructionList il = gen.getInstructionList();
	                    if (il == null) {
	                        continue;
	                    }
	                    InstructionList iList = gen.getInstructionList();
						InstructionSearcher iS = new InstructionSearcher(iList, cpg);
						
						if(iS.nextLDC("14)") != null){
							FieldInstruction fi = iS.nextFieldInstruction();
							data.addField("LoginIndex", fi.getClassName(cpg)+'.'+fi.getFieldName(cpg));
							return SearchResult.Success;
						}
	                }
	            }
        	}
        }
        return SearchResult.Failure;
    }
	
}
