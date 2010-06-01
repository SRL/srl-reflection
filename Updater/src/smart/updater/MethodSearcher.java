package smart.updater;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

public class MethodSearcher {
	
	private Method method;
	private MethodGen mg;
	private ConstantPoolGen cpg;
	private ClassGen cg;
	private Type[] types;
	
	public MethodSearcher(Method m, ClassGen cg, ConstantPoolGen cpg){
		method = m;
		types = m.getArgumentTypes();
		this.cpg = cpg;
		this.cg = cg;
		mg = new MethodGen(method, this.cg.getClassName(), cpg);
	}
	
	public boolean hasArgInt(){
		for(Type t : types)
			if(t.toString().toLowerCase().equals("int"))
				return true;
		return false;
	}
	
	public int getArgCount(){
		return types.length;
	}
	
	public int getTypeCount(String argType){
		int count = 0;
		for(Type t : types){
			if(t.toString().equals(argType))
				count++;
		}
		return count;
	}
	
	@SuppressWarnings("deprecation")
	public boolean hasField(String path){
		for(Instruction i : mg.getInstructionList().getInstructions()){
			if((i instanceof FieldInstruction)){
				String cPath = ((FieldInstruction) i).getClassName(cpg)+'.'+((FieldInstruction) i).getFieldName(cpg);
				if(cPath.equals(path))
					return true;
			}
		}
		return false;
	}

}