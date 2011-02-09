package smart.updater.searches;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.InstructionFinder;

import smart.updater.RSClass;
import smart.updater.RSClient;
import smart.updater.Search;
import smart.updater.Search.SearchResult;

public class QueueXY extends Search{
	
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
        for (Entry<String, ClassGen> c : classes.entrySet()) {
            for (Method m : c.getValue().getMethods()) {
            	if(m.isFinal() && m.isStatic() && m.getReturnType().equals(Type.VOID)){
                    ConstantPoolGen cpg = c.getValue().getConstantPool();
                    MethodGen gen = new MethodGen(m, c.getValue().getClassName(), cpg);
                    InstructionList il = gen.getInstructionList();
                    if (il == null) {
                        continue;
                    }
                    InstructionFinder f = new InstructionFinder(il);
                    Iterator e = f.search("GETSTATIC GETFIELD ICONST IALOAD LDC ISHR ISTORE GETSTATIC GETFIELD ICONST IALOAD LDC");
                    if (e.hasNext()) {
                        InstructionHandle[] handles = (InstructionHandle[]) e.next();
                        String queueX = ((GETFIELD) handles[8].getInstruction()).getFieldName(cpg);
                        String queueY = ((GETFIELD) handles[1].getInstruction()).getFieldName(cpg);
                        RSClass characterClass = data.getProperClass("Character");
                        characterClass.addField("WalkQueueX", queueX);
                        characterClass.addField("WalkQueueY", queueY);
                        return SearchResult.Success;
                    }
                }
            }
        }
        return SearchResult.Failure;
    }

}
