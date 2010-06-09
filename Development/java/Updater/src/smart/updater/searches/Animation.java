package smart.updater.searches;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.PUTFIELD;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.InstructionFinder;

import smart.updater.InstructionSearcher;
import smart.updater.InstructionSearcher2;
import smart.updater.MethodSearcher;
import smart.updater.RSClass;
import smart.updater.RSClient;
import smart.updater.Search;
import smart.updater.Search.SearchResult;

public class Animation extends Search{
	
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
		RSClass player = data.getProperClass("Player");
		RSClass character = data.getProperClass("Character");
        for (Entry<String, ClassGen> c : classes.entrySet()) {
            for (Method m : c.getValue().getMethods()) {
            	ClassGen cg = new ClassGen(c.getValue().getJavaClass());
            	ConstantPoolGen cpg = c.getValue().getConstantPool();
                MethodGen gen = new MethodGen(m, c.getValue().getClassName(), cpg);
                MethodSearcher mS = new MethodSearcher(m, cg, cpg);
            	if(m.isFinal() && m.isStatic() && m.getReturnType().equals(Type.VOID) &&
            			mS.getArgCount() == 4 && mS.getTypeCount(player.className) == 1){
                    InstructionList il = gen.getInstructionList();
                    if (il == null) {
                        continue;
                    }
                    InstructionSearcher2 iS = new InstructionSearcher2(cg, m);
                    while(iS.nextFieldInstruction() != null){
                    	FieldInstruction fi = (FieldInstruction)iS.current();
                    	if(fi.getClassName(cpg).equals(character.className)){
                    		String anim = fi.getFieldName(cpg);
                    		character.addField("Animation", anim);
                    		int count = 0;
                    		while(iS.nextFieldInstruction() != null){
                    			fi = (FieldInstruction)iS.current();
                    			if((fi instanceof GETFIELD) && (fi.getClassName(cpg)+'.'+fi.getFieldName(cpg)).equals(character.className+'.'+anim)){
                    				count++;
                    				if(count==2){
                    					while(iS.nextGETFIELD() != null){
                    						GETFIELD gf = ((GETFIELD)iS.current());
                    						if(gf.getClassName(cpg).equals(character.className)){
                    							character.addField("Motion", gf.getFieldName(cpg));
                    							return SearchResult.Success; 
                    						}
                    					}
                    				}
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
