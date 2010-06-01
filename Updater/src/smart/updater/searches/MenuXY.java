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

import smart.updater.RSClient;
import smart.updater.Search;
import smart.updater.Search.SearchResult;

public class MenuXY extends Search{

	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
        for (Entry<String, ClassGen> c : classes.entrySet()) {
        	for (Method m : c.getValue().getMethods()) {
		        if (m.isStatic() && m.isFinal() && m.getReturnType().equals(Type.VOID)) {
		          	ConstantPoolGen cpg = c.getValue().getConstantPool();
		            MethodGen gen = new MethodGen(m, c.getValue().getClassName(), cpg);
		            InstructionList il = gen.getInstructionList();
		            if (il == null) {
		             	continue;
		            }
		            InstructionFinder f = new InstructionFinder(il);
		            						// ISTORE GETSTATIC ISTORE GETSTATIC
		            Iterator e = f.search("GETSTATIC ISTORE GETSTATIC ICONST IADD PUTSTATIC GETSTATIC ISTORE GETSTATIC");
		            if (e.hasNext()) {
		            	InstructionHandle[] handles = (InstructionHandle[]) e.next();
                    	String menuX = ((GETSTATIC) handles[6].getInstruction()).getClassName(cpg) + "." + ((GETSTATIC) handles[6].getInstruction()).getFieldName(cpg);
                    	String menuY = ((GETSTATIC) handles[8].getInstruction()).getClassName(cpg) + "." + ((GETSTATIC) handles[8].getInstruction()).getFieldName(cpg);
                    	data.addField("MenuX", menuX);
                    	data.addField("MenuY", menuY);
		                return SearchResult.Success;
		            }
		        }
	        }
        }
        return SearchResult.Failure;
    }
	
}
