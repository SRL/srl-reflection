package smart.updater.searches;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.InstructionFinder;

import smart.updater.InstructionSearcher;
import smart.updater.MethodSearcher;
import smart.updater.RSClass;
import smart.updater.RSClient;
import smart.updater.Search;
import smart.updater.Search.SearchResult;

public class NodeListHead extends Search{

	public String getNodeListClass(RSClient data, HashMap<String, ClassGen> classes) {
        for (Entry<String, ClassGen> c : classes.entrySet()) {
        	ClassGen cg = new ClassGen(c.getValue().getJavaClass());
        	ConstantPoolGen cpg = c.getValue().getConstantPool();
        	RSClass renderClass = data.getProperClass("Toolkit");
        	RSClass sdToolkit = data.getProperClass("SDToolkit");
        	if(sdToolkit == null || renderClass == null)
        		return null;
        	if(c.getValue().getSuperclassName().equals(renderClass.className) &&
        			!(c.getValue().getClassName().equals(sdToolkit.className)) &&
        				!(cpg.lookupUtf8("microsoft") > 0) &&
        					!c.getValue().isAbstract()){
        		for (Method m : c.getValue().getMethods()){
        			MethodSearcher mS = new MethodSearcher(m, cg, cpg);
        			if(mS.getTypeCount("java.awt.Canvas") >= 1 && m.getName().equals("<init>")){
        				MethodGen mg = new MethodGen(m, c.getValue().getClassName(), cpg);
        				InstructionList il = mg.getInstructionList();
        				InstructionSearcher iS = new InstructionSearcher(il, cpg);
        				Instruction i = iS.nextNEW();
        				data.addClass("NodeList", ((NEW)i).getLoadClassType(cpg).getClassName());
        				return(((NEW)i).getLoadClassType(cpg).getClassName());
        			}
        		}
        	}
        }
        return null;
    }
	
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes){
		if(getNodeListClass(data, classes) != null){
			RSClass nodeList = data.getProperClass("NodeList");
			for(Entry<String, ClassGen> c : classes.entrySet()){
				if(c.getValue().getClassName().equals(nodeList.className)){
					for(Field fi : c.getValue().getFields()){
						if(fi.isPrivate()){
							nodeList.addField("Current", fi.getName());
							for(Field head: c.getValue().getFields()){
								if(!head.isPrivate() && head.getType().equals(fi.getType())){
									nodeList.addField("Head", head.getName());
									data.addClass("Node", head.getType().toString());
									return SearchResult.Success;
								}
							}
						}
					}
				}
			}
		}
		return SearchResult.Failure;
	}
	
}
