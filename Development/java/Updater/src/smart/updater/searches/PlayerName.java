package smart.updater.searches;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import smart.updater.InstructionSearcher2;
import smart.updater.RSClass;
import smart.updater.RSClient;
import smart.updater.RSField;
import smart.updater.Search;

public class PlayerName extends Search{

	@Override
	public SearchResult run(RSClient data, HashMap<String, ClassGen> classes) {
		RSClass player = data.getProperClass("Player");
		RSField myplayer = data.getProperField("MyPlayer");
		for(Entry<String, ClassGen> c : classes.entrySet()){
			ClassGen cg = new ClassGen(c.getValue().getJavaClass());
			ConstantPoolGen cpg = cg.getConstantPool();
			int count = 0;
			for(Method m : cg.getMethods()){
				if(m.isPrivate() && m.isStatic() && m.isFinal() && m.getReturnType().equals(Type.VOID)
						&& m.getArgumentTypes().length == 2){
					MethodGen mg = new MethodGen(m, cg.getClassName(), cpg);
					InstructionList il = mg.getInstructionList();
					if(il == null)
						continue;
					InstructionSearcher2 iS = new InstructionSearcher2(cg, m, il);
					if(iS.nextSIPUSH(3326) != null){
						FieldInstruction fi = iS.nextGETFIELD();
						FieldInstruction tester = iS.previousGETSTATIC();
						if((tester.getClassName(cpg)+'.'+tester.getFieldName(cpg)).equals(myplayer.path)){
							player.addField("GetLevel", fi.getFieldName(cpg));
							count++;
							if(count==2)
								return SearchResult.Success;
						}
					}
					iS = new InstructionSearcher2(cg, m, il);
					if(iS.nextSIPUSH(5015) != null){
						FieldInstruction fi = iS.nextGETSTATIC();
						if(fi != null && (fi.getClassName(cpg)+'.'+fi.getFieldName(cpg)).equals(myplayer.path)){
							fi = iS.nextGETFIELD();
							if(fi != null && fi.getType(cpg).equals(Type.STRING)){
								player.addField("GetName", fi.getFieldName(cpg));
								count++;
								if(count==2)
									return SearchResult.Success;
							}	
						}
					}
				}
			}
		}
		return SearchResult.Failure;
	}

}
