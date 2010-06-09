package smart.updater.searches;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.MethodGen;

import smart.updater.InstructionSearcher;
import smart.updater.MethodSearcher;
import smart.updater.RSClass;
import smart.updater.RSClient;
import smart.updater.Search;

public class MenuOption extends Search{


	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
		RSClass nodeClass = data.getProperClass("Node");
		for(Entry<String, ClassGen> c : classes.entrySet()){
			ClassGen cg = new ClassGen(c.getValue().getJavaClass());
			ConstantPoolGen cpg = cg.getConstantPool();
			if(c.getValue().getSuperclassName().equals(nodeClass.className) && c.getValue().isFinal()){
				for(Method m : c.getValue().getMethods()){
					MethodSearcher mS = new MethodSearcher(m, cg, cpg);
					if(m.getName().equals("<init>") && mS.getArgCount() == 10 &&
							mS.getTypeCount("int") == 5 && mS.getTypeCount("java.lang.String")==2){
						MethodGen mg = new MethodGen(m, cg.getClassName(), cpg);
						RSClass menu = data.addClass("Menu", c.getValue().getClassName());
						int found = 0;
						InstructionSearcher iS = new InstructionSearcher(mg.getInstructionList(), cpg);
						Instruction i;
						FieldInstruction option;
						FieldInstruction action;
						while(iS.next() != null){
							i = iS.current();
							if(i instanceof ALOAD){
								if(((ALOAD)i).getIndex() == 2){
									option = iS.nextFieldInstruction();
									menu.addField("Option", option.getFieldName(cpg));
									found++;
									if(found==2)
										return SearchResult.Success;
								}
								if(((ALOAD)i).getIndex() == 1){
									option = iS.nextFieldInstruction();
									menu.addField("Action", option.getFieldName(cpg));
									found++;
									if(found==2)
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
