package smart.updater.searches;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.InstructionFinder;

import smart.updater.InstructionSearcher;
import smart.updater.MethodSearcher;
import smart.updater.RSClass;
import smart.updater.RSClient;
import smart.updater.RSField;
import smart.updater.Search;
import smart.updater.Search.SearchResult;

public class NodeNext extends Search{

	public String findNextNode(RSClient data, HashMap<String, ClassGen> classes){
		RSClass nodeClass = data.getProperClass("Node");
		for(Entry<String, ClassGen> c : classes.entrySet()){
			ClassGen cg = new ClassGen(c.getValue().getJavaClass());
			ConstantPoolGen cpg = c.getValue().getConstantPool();
			for(Method m : c.getValue().getMethods()){
				MethodSearcher mS = new MethodSearcher(m, cg, cpg);
				if(m.isSynchronized() && m.isFinal() && m.getReturnType().equals(Type.VOID)
						&& mS.getTypeCount("int[]") == 1){
					MethodGen mg = new MethodGen(m, c.getValue().getClassName(), cpg);
					InstructionList il = mg.getInstructionList();
					for(Instruction i : il.getInstructions()){
						if(i instanceof FieldInstruction){
							if(((FieldInstruction) i).getClassName(cpg).equals(nodeClass.className)){
								nodeClass.addField("Next", ((FieldInstruction) i).getFieldName(cpg));
								return ((FieldInstruction) i).getFieldName(cpg);
							}	
						}
					}
				}
			}
		}
		return null;
	}
	
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes)
	{
		String nextNode = findNextNode(data, classes);
		if (nextNode == null)
			return SearchResult.Failure;
		RSClass nodeClass = data.getProperClass("Node");
		for(Entry<String, ClassGen> c : classes.entrySet()){
			if(c.getValue().getClassName().equals(nodeClass.className)){
				for(Field f : c.getValue().getFields()){
					if(f.getType().toString().equals(nodeClass.className) &&
							!f.getName().equals(nextNode)){
						nodeClass.addField("Previous", f.getName());
						return SearchResult.Success;
					}
					if(f.getType().equals(Type.LONG))
						nodeClass.addField("GetID", f.getName());
				}
			}
		}
		return SearchResult.Failure;
	}
	
}
