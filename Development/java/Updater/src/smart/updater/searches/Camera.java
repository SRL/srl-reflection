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
import org.apache.bcel.generic.PUTSTATIC;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.InstructionFinder;

import smart.updater.InstructionSearcher;
import smart.updater.InstructionSearcher2;
import smart.updater.MethodSearcher;
import smart.updater.RSClass;
import smart.updater.RSClient;
import smart.updater.RSField;
import smart.updater.Search;
import smart.updater.Search.SearchResult;

public class Camera extends Search{
	
	public static boolean hasBVIIII(MethodGen mg)
	{
		Type[] types = mg.getArgumentTypes();
		int bytes = 0;
		int booleans = 0;
		int ints = 0;
		for(int i = 0; i < types.length; i++)
		{
			if(types[i].toString().equals("byte"))
				bytes++;
			if(types[i].toString().equals("boolean"))
				booleans++;
			if(types[i].toString().equals("int"))
				ints++;
			//System.out.println(types[i]);
		}
		
		//System.out.println(" ");
		
		return bytes == 1 && booleans == 1 && ints == 4;
	}
	
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
		RSField myplayer = data.getProperField("MyPlayer");
		RSField pixelX = data.getProperField("PixelX");
		RSField pixelY = data.getProperField("PixelY");
        for (Entry<String, ClassGen> c : classes.entrySet()) {
            for (Method m : c.getValue().getMethods()) {
            	ClassGen cg = new ClassGen(c.getValue().getJavaClass());
            	ConstantPoolGen cpg = c.getValue().getConstantPool();
            	MethodGen gen = new MethodGen(m, c.getValue().getClassName(), cpg);
            	InstructionList list = gen.getInstructionList();
            	if(list == null)
            		continue;
            	MethodSearcher mS = new MethodSearcher(m, cg, cpg);
            	if(m.isStatic() && m.isFinal() && m.getReturnType().equals(Type.VOID) && m.getArgumentTypes().length == 6
            			&& mS.hasField(myplayer.path))
				{
					InstructionList il = gen.getInstructionList();
                    if (il == null) {
                        continue;
                    }
                    String sDestX;
                    int count = 0;
                    InstructionSearcher2 iS = new InstructionSearcher2(cg, m);
                    while(iS.nextGETSTATIC() != null){
                    	FieldInstruction fi = (FieldInstruction)iS.current();
                    	if((fi.getClassName(cpg)+'.'+fi.getFieldName(cpg)).equals(myplayer.path)){
                    		count++;
                    		if(count==7){
	                    		fi = iS.nextGETSTATIC();
	                    		sDestX = fi.getClassName(cpg)+'.'+fi.getFieldName(cpg);
	                    		data.addField("DestX", sDestX);
	                    		InstructionFinder f = new InstructionFinder(il);
	                    		Iterator e = f.search("IF_ICMPNE ICONST_M1 PUTSTATIC ICONST_M1 PUTSTATIC");
	                            if(e.hasNext()){
	                            	InstructionHandle[] handles = (InstructionHandle[]) e.next();
	                            	String temp = ((FieldInstruction) ((PUTSTATIC) handles[2].getInstruction())).getClassName(cpg) + "." + ((PUTSTATIC) handles[2].getInstruction()).getFieldName(cpg);
	                            	if(!temp.equals(sDestX)){
	                            		data.addField("DestY", temp);
	                            	} else {
	                            		temp = ((FieldInstruction) ((PUTSTATIC) handles[4].getInstruction())).getClassName(cpg) + "." + ((PUTSTATIC) handles[4].getInstruction()).getFieldName(cpg);
	                            		data.addField("DestY", temp);
	                            	}
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
