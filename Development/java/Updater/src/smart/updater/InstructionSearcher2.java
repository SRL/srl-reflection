package smart.updater;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

/*
* @author The Bank, Ollie
*/
public class InstructionSearcher2 {
	public Instruction[] instructions = null;
    public ConstantPoolGen cp = null;
    public ClassGen classGen = null;
    public int index = -1;


    public InstructionSearcher2(ClassGen cg, Method m) {
        index = -1;
        this.cp = cg.getConstantPool();
        this.classGen = cg;
        this.instructions = new MethodGen(m, cg.getClassName(), cp).getInstructionList().getInstructions();
    }
    
    public InstructionSearcher2(ClassGen cg, Method m, InstructionList il) {
        index = -1;
        this.cp = cg.getConstantPool();
        this.classGen = cg;
        this.instructions = il.getInstructions();
    }

    public Instruction[] getInstructions() {
        return instructions;
    }

    public int index() {
        return index;
    }

    public Instruction current() {
        return instructions[index];
    }

    public Instruction next() {
        index++;
        return current();
    }

    public Instruction previous() {
        index--;
        return current();
    }

    public void setPosition(int index) {
        this.index = index;
    }

    public Instruction next(String search) {
        for(++index; index < instructions.length; ++index) {
            if(current().getName().equalsIgnoreCase(search))
                return current();
        }
        return null;
    }

    public Instruction previous(String search) {
        for(--index; index > -1; --index) {
            if(current().getName().equalsIgnoreCase(search))
                return current();
        }
        return null;
    }

    public IfInstruction nextIfInstruction() {
        for(++index; index < instructions.length; ++index) {
            if(current() instanceof IfInstruction)
                return (IfInstruction) current();
        }
        return null;
    }

    public BIPUSH nextBIPUSH(int... values) {
        BIPUSH toReturn;
        int startIDX = index;
        for(int value : values) {
            if((toReturn = nextBIPUSH(value)) != null)
                return toReturn;
            setPosition(startIDX);
        }
        return null;
    }

    public SIPUSH previousSIPUSH(int... values) {
        SIPUSH toReturn;
        int startIndex = index;
        for(int value : values) {
            if((toReturn = previousSIPUSH(value)) != null)
                return toReturn;
            setPosition(startIndex);
        }
        return null;
    }

    public SIPUSH nextSIPUSH(int... values) {
        SIPUSH toReturn;
        int startIDX = index;
        for(int value : values) {
            if((toReturn = nextSIPUSH(value)) != null)
                return toReturn;
            setPosition(startIDX);
        }
        return null;
    }
    
    public BIPUSH nextBIPUSH() {
        for(++index; index < instructions.length; ++index) {
            if(current() instanceof BIPUSH) {
            	return (BIPUSH) current();
            }
        }
        return null;
    }

    public BIPUSH previousBIPUSH() {
        for(--index; index > -1; --index) {
            if(current() instanceof BIPUSH) {
                return (BIPUSH) current();
            }
        }
        return null;
    }

    public BIPUSH nextBIPUSH(int value) {
        for(++index; index < instructions.length; ++index) {
            if(current() instanceof BIPUSH) {
                if(((BIPUSH) current()).getValue().intValue() == value)
                    return (BIPUSH) current();
            }
        }
        return null;
    }

    public BIPUSH previousBIPUSH(int value) {
        for(--index; index > -1; --index) {
            if(current() instanceof BIPUSH) {
                if(((BIPUSH) current()).getValue().intValue() == value)
                    return (BIPUSH) current();
            }
        }
        return null;
    }

    public SIPUSH nextSIPUSH(int value) {
        for(++index; index < instructions.length; ++index) {
            if(current() instanceof SIPUSH) {
                if(((SIPUSH) current()).getValue().intValue() == value)
                    return (SIPUSH) current();
            }
        }
        return null;
    }

    public SIPUSH previousSIPUSH(int value) {
        for(--index; index > -1; --index) {
            if(current() instanceof SIPUSH) {
                if(((SIPUSH) current()).getValue().intValue() == value)
                    return (SIPUSH) current();
            }
        }
        return null;
    }
    
    public LDC nextLDC() {
    	for(++index; index < instructions.length; ++index) {
            if(current() instanceof LDC) {
                return (LDC) current();
            }
        }
        return null;
    }


    public LDC nextLDC(int ref) {
        for(++index; index < instructions.length; ++index) {
            if(current() instanceof LDC) {
                if(((LDC) current()).getIndex() == (ref))
                    return (LDC) current();
            }
        }
        return null;
    }

    public LDC nextIntLDC(int... values) {
        LDC toReturn;
        int startIDX = index;
        for(int value : values) {
            if((toReturn = nextIntLDC(value)) != null)
                return toReturn;
            setPosition(startIDX);
        }
        return null;
    }

    public LDC nextIntLDC(int value) {
        for(++index; index < instructions.length; ++index) {
            if(current() instanceof LDC) {
                if(((LDC) current()).getValue(cp).equals(value)) {
                    return (LDC) current();
                }
            }
        }
        return null;
    }

    public LDC nextLDC(Object value) {
        for(++index; index < instructions.length; ++index) {
            if(current() instanceof LDC) {
                if(((LDC) current()).getValue(cp).equals(value)) {
                    return (LDC) current();
                }
            }
        }
        return null;
    }
    
    public LDC previousLDC() {
        for(--index; index > -1; --index) {
            if(current() instanceof LDC) {
                return (LDC) current();
            }
        }
        return null;
    }

    public LDC previousLDC(String value) {
        for(--index; index > -1; --index) {
            if(current() instanceof LDC) {
                if(((LDC) current()).getValue(cp).equals(value))
                    return (LDC) current();
            }
        }
        return null;
    }
    
    public NEWARRAY nextNEWARRAY() {
        for(++index; index < instructions.length; ++index) {
            if(current() instanceof NEWARRAY) {
            	return (NEWARRAY) current();
            }
        }
        return null;
    }
    
    public NEWARRAY previousNEWARRAY() {
        for(--index; index > -1; --index) {
            if(current() instanceof NEWARRAY) {
                return (NEWARRAY) current();
            }
        }
        return null;
    }
    
    public ANEWARRAY nextANEWARRAY() {
        for(++index; index < instructions.length; ++index) {
            if(current() instanceof ANEWARRAY) {
            	return (ANEWARRAY) current();
            }
        }
        return null;
    }
    
    public ANEWARRAY previousANEWARRAY() {
        for(--index; index > -1; --index) {
            if(current() instanceof ANEWARRAY) {
                return (ANEWARRAY) current();
            }
        }
        return null;
    }

    public FieldInstruction nextFieldInstruction() {
        for(++index; index < instructions.length; ++index) {
            if(current() instanceof FieldInstruction) {
                return (FieldInstruction) current();
            }
        }
        return null;
    }

    public FieldInstruction previousFieldInstruction() {
        for(--index; index > -1; --index) {
            if(current() instanceof FieldInstruction) {
                return (FieldInstruction) current();
            }
        }
        return null;
    }

    public FieldInstruction nextFieldInstruction(int ref) {
        for(++index; index < instructions.length; ++index) {
            if(current() instanceof FieldInstruction) {
                if(((FieldInstruction) current()).getIndex() == (ref))
                    return (FieldInstruction) current();
            }
        }
        return null;
    }

    public FieldInstruction previousFieldInstruction(int ref) {
        for(--index; index > -1; --index) {
            if(current() instanceof FieldInstruction) {
                if(((FieldInstruction) current()).getIndex() == (ref))
                    return (FieldInstruction) current();
            }
        }
        return null;
    }

    public FieldInstruction nextFieldInstructionType(Type type) {
        for(++index; index < instructions.length; ++index) {
            if(current() instanceof FieldInstruction) {
                if(((FieldInstruction) current()).getFieldType(cp).equals(type))
                    return (FieldInstruction) current();
            }
        }
        return null;
    }

    public FieldInstruction previousFieldInstructionType(Type type) {
        for(--index; index > -1; --index) {
            if(current() instanceof FieldInstruction) {
                if(((FieldInstruction) current()).getFieldType(cp).equals(type))
                    return (FieldInstruction) current();
            }
        }
        return null;
    }
    
    public FieldInstruction nextPUTFIELD() {
        for(++index; index < instructions.length; ++index) {
            if(current() instanceof PUTFIELD) {
                return (PUTFIELD) current();
            }
        }
        return null;
    }

    public FieldInstruction previousPUTFIELD() {
        for(--index; index > -1; --index) {
            if(current() instanceof PUTFIELD) {
                return (PUTFIELD) current();
            }
        }
        return null;
    }
    
    public FieldInstruction nextPUTSTATIC() {
        for(++index; index < instructions.length; ++index) {
            if(current() instanceof PUTSTATIC) {
                return (PUTSTATIC) current();
            }
        }
        return null;
    }

    public FieldInstruction previousPUTSTATIC() {
        for(--index; index > -1; --index) {
            if(current() instanceof PUTSTATIC) {
                return (PUTSTATIC) current();
            }
        }
        return null;
    }
    
    public FieldInstruction nextGETSTATIC() {
        for(++index; index < instructions.length; ++index) {
            if(current() instanceof GETSTATIC) {
                return (GETSTATIC) current();
            }
        }
        return null;
    }

    public FieldInstruction previousGETSTATIC() {
        for(--index; index > -1; --index) {
            if(current() instanceof GETSTATIC) {
                return (GETSTATIC) current();
            }
        }
        return null;
    }
    
    public FieldInstruction nextGETFIELD() {
        for(++index; index < instructions.length; ++index) {
            if(current() instanceof GETFIELD) {
                return (GETFIELD) current();
            }
        }
        return null;
    }

    public FieldInstruction previousGETFIELD() {
        for(--index; index > -1; --index) {
            if(current() instanceof GETFIELD) {
                return (GETFIELD) current();
            }
        }
        return null;
    }
    
    public FieldInstruction nextGETFIELD(String fieldName) {
        for(++index; index < instructions.length; ++index) {
            if((current() instanceof GETFIELD) && ((GETFIELD)current()).getFieldName(cp).equals(fieldName)) {
                return (GETFIELD) current();
            }
        }
        return null;
    }

    public FieldInstruction previousGETFIELD(String fieldName) {
        for(--index; index > -1; --index) {
        	if((current() instanceof GETFIELD) && ((GETFIELD)current()).getFieldName(cp).equals(fieldName)) {
                return (GETFIELD) current();
            }
        }
        return null;
    }

    public InvokeInstruction nextInvokeInstruction(int ref) {
        for(++index; index < instructions.length; ++index) {
            if(current() instanceof InvokeInstruction) {
                if(((InvokeInstruction) current()).getIndex() == (ref))
                    return (InvokeInstruction) current();
            }
        }
        return null;
    }

    public InvokeInstruction nextInvokeInstruction() {
        for(++index; index < instructions.length; ++index) {
            if(current() instanceof InvokeInstruction) {
                return (InvokeInstruction) current();
            }
        }
        return null;
    }

    public InvokeInstruction previousInvokeInstruction() {
        for(--index; index > -1; --index) {
            if(current() instanceof InvokeInstruction) {
                return (InvokeInstruction) current();
            }
        }
        return null;
    }

    public InvokeInstruction nextInvokeInstruction(String value) {
        for(++index; index < instructions.length; ++index) {
            if(current() instanceof InvokeInstruction) {
                if(((InvokeInstruction) current()).getMethodName(cp).equals(value))
                    return (InvokeInstruction) current();
            }
        }
        return null;
    }

    public InvokeInstruction previousInvokeInstruction(String value) {
        for(--index; index > -1; --index) {
            if(current() instanceof InvokeInstruction) {
                if(((InvokeInstruction) current()).getMethodName(cp).equals(value))
                    return (InvokeInstruction) current();
            }
        }
        return null;
    }

    public ConstantPushInstruction nextConstantPushInstruction(int con) {
        for(++index; index < instructions.length; ++index) {
            Instruction instruction = instructions[index];
            if(instruction instanceof ConstantPushInstruction) {
                ConstantPushInstruction cpi = (ConstantPushInstruction) instruction;
                if(cpi.getValue().intValue() == con) {
                    return cpi;
                }
            }
        }
        return null;
    }

    public ConstantPushInstruction previousConstantPushInstruction(int con) {
        for(--index; index > -1; --index) {
            Instruction instruction = instructions[index];
            if(instruction instanceof ConstantPushInstruction) {
                ConstantPushInstruction cpi = (ConstantPushInstruction) instruction;
                if(cpi.getValue().intValue() == con) {
                    return cpi;
                }
            }
        }
        return null;
    }
}