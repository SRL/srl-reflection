package smart.updater;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import java.util.NoSuchElementException;

/**
 * a class which searches through instruction lists, has a lot of overloaded methods
 * it saves its current position, so multiple calls of its method find differant instructions
 * 
 * for the methods which iterate (eg. nextFieldInstruction()), if they return dont find
 * and return null, the current instruction will be at the first/last of the list, so more
 * searching wont work, to save the current position, use getCurrentIndex()
 * and setCurrentIndex(), or resetToStart() and resetToEnd()
 * 
 * for methods which iterate (eg. nextFieldInstruction()), they start searching from the next
 * instruction, not the current one. so if you're at a field instruction, nextFieldInstruction wont
 * find it, but carry on.
 */
public class InstructionSearcher {
        
        public static final byte MODE_START = 0x01;
        public static final byte MODE_END = 0x02;
        
        protected InstructionList list;
        protected InstructionHandle current;
        protected ConstantPoolGen theCPool;
        
        /* Constructors */
        /**
         * @param mode Whether to have the current be the first, or last in the list
         */
        public InstructionSearcher(InstructionList list, ConstantPoolGen theCPool, byte mode) {
                if(list == null)
                        throw new IllegalArgumentException("list cant be null");
                if(theCPool == null)
                        throw new IllegalArgumentException("theCPool cant be null");
                if(mode != MODE_START && mode != MODE_END)
                        throw new IllegalArgumentException("mode has to be MODE_START or MODE_END");
                current = (mode == MODE_START ? list.getStart() : list.getEnd()); 
                this.list = list;
                this.theCPool = theCPool;
        }
        
        public InstructionSearcher(InstructionList list, ConstantPoolGen theCPool) {
                this(list, theCPool, MODE_START);
        }
        
        public InstructionSearcher(Method theMethod, String className, ConstantPoolGen theCPool, byte mode) {
                this(new MethodGen(theMethod, className, theCPool).getInstructionList(), theCPool, mode);
        }
        
        public InstructionSearcher(Method theMethod, String className, ConstantPoolGen theCPool) {
                this(theMethod, className, theCPool, MODE_START);
        }
        
        public InstructionSearcher(Method theMethod, ClassGen theClassGen, byte mode) {
                this(theMethod, theClassGen.getClassName(), theClassGen.getConstantPool(), mode);
        }

        public InstructionSearcher(Method theMethod, ClassGen theClassGen) {
                this(theMethod, theClassGen, MODE_START);
        }
        
        /* Little useful methods */

        public InstructionList getList() { return list; }
        public ConstantPoolGen getConstantPoolGen() { return theCPool; }

        public String toString() {
                return getClass().getName() + " index = " + getCurrentIndex() + " current = " + current.getPosition() + ": " + current().toString(theCPool.getConstantPool());
        }
        
        private boolean hasNext() {
                return current != list.getEnd();
        }
        
        private boolean hasPrev() {
                return current != list.getStart();
        }
        
        private void checkNext() {
                if(!hasNext())
                        throw new NoSuchElementException("No more instructions in " + this);
        }
        
        private void checkPrev() {
                if(!hasPrev())
                        throw new NoSuchElementException("No more instructions in " + this);
        }
        
        /* set/get methods */
        /**
         * sets a new current, must be contained in the InstructionList
         */
        public void setCurrent(InstructionHandle handle) {
                if(!list.contains(handle))
                        throw new IllegalArgumentException("handle not contained in list");
                current = handle;
        }

        /**
         * throws ArrayIndexOutOfBoundsException if needed
         */
        public void setCurrentIndex(int index) {
                setCurrent(list.getInstructionHandles()[index]);
        }

        /**
         * gets the index of the current instruction
         * uses a linear search
         */
        public int getCurrentIndex() {
                InstructionHandle c = list.getStart();
                for(int f = 0;; f++, c = c.getNext()) {
                        if(c == current)
                                return f;
                        else
                        if(c == list.getEnd())
                                break;
                }
                throw new IndexOutOfBoundsException("current handle is not contained in list!");
                //should be not possible
        }
        
        public void resetToStart() {
                current = list.getStart();
        }
        
        public void resetToEnd() {
                current = list.getEnd();
        }
        
        public Instruction current() {
                return current.getInstruction();
        }
        
        public Instruction next() {
                checkNext();
                current = current.getNext();
                return current.getInstruction();
        }
        
        public Instruction prev() {
                checkPrev();
                current = current.getPrev();
                return current.getInstruction();
        }
        
        public Instruction next(int hopCount) {
                for(int f = 1; f < hopCount; f++)
                        next();
                return next();
        }
        
        public Instruction prev(int hopCount) {
                for(int f = 1; f < hopCount; f++)
                        prev();
                return prev();
        }
        
        /* Searching methods */
        /**
         * Is passed to methods nextConstraint and prevConstraint.
         */
        public static interface Constraint {
                
                boolean matches(InstructionHandle current, ConstantPoolGen theCPool);
        }
        
        public Instruction nextConstraint(Constraint con) {
                while(hasNext()) {
                        current = current.getNext();
                        if(con.matches(current, theCPool))
                                return current.getInstruction();
                }
                return null;
        }
        
        public Instruction prevConstraint(Constraint con) {
                while(hasPrev()) {
                        current = current.getPrev();
                        if(con.matches(current, theCPool))
                                return current.getInstruction();
                }
                return null;
        }
        
        /**
         * starts at the beginning and searchs the entire InstructionList for
         * the Constraint, returns its index or -1 if not found
         */
        public int containsConstraint(Constraint con) {
                InstructionHandle c = list.getStart();
                for(int f = 0;;f++, c = c.getNext()) {
                        if(con.matches(c, theCPool))
                                return f;
                        if(c == list.getEnd())
                                break;
                }
                return -1;
        }
        
        /**
         * iterates until it finds the next instruction which is an instance of the param
         * or null if not found
         * note: it is not  Class<? extends Instruction> to allow for searching for
         * interfaces such as ConstantPushInstruction
         * @param instructionClass The class to check instance for
         */
        public Instruction nextOfType(Class<?> instructionClass) {
                //TODO try using generic methods here, it might work
                while(hasNext()) {
                        Instruction i = next();
                        if(instructionClass.isInstance(i))
                                return i;
                }
                return null;
        }
        
        /**
         * iterates until it finds the prev instruction which is an instance of the param
         * or null if not found
         * note: it is not  Class<? extends Instruction> to allow for searching for
         * interfaces such as ConstantPushInstruction
         * @param instructionClass The class to check instance for
         */
        public Instruction prevOfType(Class<?> instructionClass) {
                while(hasPrev()) {
                        Instruction i = prev();
                        if(instructionClass.isInstance(i))
                                return i;
                }
                return null;
        }
        
        /**
         * iterates until it finds the next fieldinstruction which points to the
         * constant pool index parameter
         */
        public FieldInstruction nextFieldInstruction(int cpIndex) {
                while(hasNext()) {
                        Instruction i = next();
                        if(i instanceof FieldInstruction && ((FieldInstruction)i).getIndex() == cpIndex)
                                return (FieldInstruction)i;
                }
                return null;
        }
        
        /**
         * iterates until it finds the prev fieldinstruction which points to the
         * constant pool index parameter
         */
        public FieldInstruction prevFieldInstruction(int cpIndex) {
                while(hasPrev()) {
                        Instruction i = prev();
                        if(i instanceof FieldInstruction && ((FieldInstruction)i).getIndex() == cpIndex)
                                return (FieldInstruction)i;
                }
                return null;
        }
        
        public FieldInstruction nextFieldInstruction() {
            while(hasNext()) {
                    Instruction i = next();
                    if(i instanceof FieldInstruction)
                            return (FieldInstruction)i;
            }
            return null;
        }
        
        public FieldInstruction prevFieldInstruction() {
            while(hasPrev()) {
                    Instruction i = prev();
                    if(i instanceof FieldInstruction)
                            return (FieldInstruction)i;
            }
            return null;
        }
        
        public FieldInstruction nextFieldInstructionOf(String fieldPath) {
            while(hasNext()) {
                    Instruction i = next();
                    if(i instanceof FieldInstruction)
                    {
                    	String f = ((FieldInstruction) i).getClassName(theCPool)+'.'+((FieldInstruction) i).getFieldName(theCPool);
                    	if(f.equals(fieldPath))
                            return (FieldInstruction)i;
                    }
            }
            return null;
        }
        
        public FieldInstruction prevFieldInstructionOf(String fieldPath) {
        	while(hasPrev()) {
                Instruction i = prev();
                if(i instanceof FieldInstruction)
                {
                	String f = ((FieldInstruction) i).getClassName(theCPool)+'.'+((FieldInstruction) i).getFieldName(theCPool);
                	if(f.equals(fieldPath))
                        return (FieldInstruction)i;
                }
        	}
        	return null;
        }
        
        /**
         * iterates until it finds the next fieldinstruction which has the classname, 
         * fieldname and signature of the parameters, if the constant pool fieldref of those
         * parameters does not exist, null is returned
         */
        public FieldInstruction nextFieldInstruction(String class_name, String field_name, String signature) {
                int index = theCPool.lookupFieldref(class_name, field_name, signature);
                if(index == -1)
                        return null;
                return nextFieldInstruction(index);
        }
        
        /**
         * iterates until it finds the prev fieldinstruction which has the classname, 
         * fieldname and signature of the parameters, if the constant pool fieldref of those
         * parameters does not exist, null is returned
         */
        public FieldInstruction prevFieldInstruction(String class_name, String field_name, String signature) {
                int index = theCPool.lookupFieldref(class_name, field_name, signature);
                if(index == -1)
                        return null;
                return prevFieldInstruction(index);
        }
        
        /**
         * iterates until it finds the next fieldinstruction which has the type signature, 
         * of the parameter
         */
        public FieldInstruction nextFieldInstruction(String typeSignature) {
                while(hasNext()) {
                        Instruction i = next();
                        if(i instanceof FieldInstruction && ((FieldInstruction)i).getType(theCPool).getSignature().equals(typeSignature))
                                return (FieldInstruction)i;
                }
                return null;
        }
        
        /**
         * iterates until it finds the prev fieldinstruction which has the type signature, 
         * of the parameter
         */
        public FieldInstruction prevFieldInstruction(String typeSignature) {
                while(hasPrev()) {
                        Instruction i = prev();
                        if(i instanceof FieldInstruction && ((FieldInstruction)i).getType(theCPool).getSignature().equals(typeSignature))
                                return (FieldInstruction)i;
                }
                return null;
        }
        
        public FieldInstruction nextFieldInstruction(Type type) {
                return nextFieldInstruction(type.getSignature());
        }
        
        public FieldInstruction prevFieldInstruction(Type type) {
                return prevFieldInstruction(type.getSignature());
        }
        
        /**
         * iterates until it finds the next invokeinstruction which points to the
         * constant pool index parameter
         * note: this family of method doesnt have nextInvokeInstruction(ClassName, MethodName, Signature)
         * because INVOKEINTERFACE works slightly differantly, its better the user just does a lookupMethodref
         */
        public InvokeInstruction nextInvokeInstruction(int cpIndex) {
                while(hasNext()) {
                        Instruction i = next();
                        if(i instanceof InvokeInstruction && ((InvokeInstruction)i).getIndex() == cpIndex)
                                return (InvokeInstruction)i;
                }
                return null;
        }
        
        public InvokeInstruction prevInvokeInstruction(int cpIndex) {
                while(hasPrev()) {
                        Instruction i = prev();
                        if(i instanceof InvokeInstruction && ((InvokeInstruction)i).getIndex() == cpIndex)
                                return (InvokeInstruction)i;
                }
                return null;
        }
        
        public InvokeInstruction nextInvokeInstruction(Type returnType) {
                while(hasNext()) {
                        Instruction i = next();
                        if(i instanceof InvokeInstruction && ((InvokeInstruction)i).getReturnType(theCPool).equals(returnType))
                                return (InvokeInstruction)i;
                }
                return null;
        }
        
        public InvokeInstruction prevInvokeInstruction(Type returnType) {
                while(hasPrev()) {
                        Instruction i = prev();
                        if(i instanceof InvokeInstruction && ((InvokeInstruction)i).getReturnType(theCPool).equals(returnType))
                                return (InvokeInstruction)i;
                }
                return null;
        }
        
        public ConstantPushInstruction nextConstantPushInstruction(Number value) {
                while(hasNext()) {
                        Instruction i = next();
                        if(i instanceof ConstantPushInstruction && ((ConstantPushInstruction)i).getValue().equals(value))
                                return (ConstantPushInstruction)i;
                }
                return null;
        }
        
        public ConstantPushInstruction prevConstantPushInstruction(Number value) {
                while(hasPrev()) {
                        Instruction i = prev();
                        if(i instanceof ConstantPushInstruction && ((ConstantPushInstruction)i).getValue().equals(value))
                                return (ConstantPushInstruction)i;
                }
                return null;
        }
        
        /**
         * finds the next LDC which has the value
         * it can be of type String, Integer, Float ect..
         */
        public LDC nextLDC(Object value) {
                while(hasNext()) {
                        Instruction i = next();
                        if(i instanceof LDC && ((LDC)i).getValue(theCPool).equals(value))
                                return (LDC)i;
                }
                return null;
        }
        
        public LDC prevLDC(Object value) {
                while(hasPrev()) {
                        Instruction i = prev();
                        if(i instanceof LDC && ((LDC)i).getValue(theCPool).equals(value))
                                return (LDC)i;
                }
                return null;
        }
        
        public Instruction nextGETSTATIC() {
        	while(hasNext()) {
                Instruction i = next();
                if(i instanceof GETSTATIC)
                        return (GETSTATIC)i;
        	}
        	return null;
        }
        
        public Instruction prevGETSTATIC() {
        	while(hasPrev()) {
                Instruction i = prev();
                if(i instanceof GETSTATIC)
                        return (GETSTATIC)i;
        	}
        	return null;
        }
        
        public Instruction nextGETFIELD() {
        	while(hasNext()) {
                Instruction i = next();
                if(i instanceof GETFIELD)
                        return (GETFIELD)i;
        	}
        	return null;
        }
        
        public Instruction prevGETFIELD() {
        	while(hasPrev()) {
                Instruction i = prev();
                if(i instanceof GETFIELD)
                        return (GETFIELD)i;
        	}
        	return null;
        }
        
        public Instruction nextPUTFIELD() {
        	while(hasNext()) {
                Instruction i = next();
                if(i instanceof PUTFIELD)
                        return (PUTFIELD)i;
        	}
        	return null;
        }
        
        public Instruction prevPUTFIELD() {
        	while(hasPrev()) {
                Instruction i = prev();
                if(i instanceof PUTFIELD)
                        return (PUTFIELD)i;
        	}
        	return null;
        }
        
        public Instruction nextGETFIELD(String path) {
        	while(hasNext()) {
                Instruction i = next();
                if(i instanceof GETFIELD)
                {
                	String fpath = ((GETFIELD) i).getClassName(theCPool)+'.'+((GETFIELD) i).getFieldName(theCPool);
                	if(fpath.equals(path))
                        return (GETFIELD)i;
                }
        	}
        	return null;
        }
        
        public Instruction prevGETFIELD(String path) {
        	while(hasPrev()) {
                Instruction i = prev();
                if(i instanceof GETFIELD)
                {
                	String fpath = ((GETFIELD) i).getClassName(theCPool)+'.'+((GETFIELD) i).getFieldName(theCPool);
                	if(fpath.equals(path))
                        return (GETFIELD)i;
                }
        	}
        	return null;
        }
        
        public Instruction nextNEW() {
        	while(hasNext()) {
                Instruction i = next();
                if(i instanceof NEW)
                        return (NEW)i;
        	}
        	return null;
        }
        
        public Instruction prevNEW() {
        	while(hasPrev()) {
                Instruction i = prev();
                if(i instanceof NEW)
                        return (NEW)i;
        	}
        	return null;
        }
        
        public Instruction nextALOAD(int index) {
        	while(hasNext()) {
                Instruction i = next();
                if(i instanceof ALOAD)
                	if(((ALOAD)i).getIndex() == index)
                        return (ALOAD)i;
        	}
        	return null;
        }
        
        public Instruction prevALOAD(int index) {
        	while(hasPrev()) {
                Instruction i = prev();
                if(i instanceof ALOAD)
                	if(((ALOAD)i).getIndex() == index)
                        return (ALOAD)i;
        	}
        	return null;
        }
        
        public CPInstruction nextCPInstruction(int index) {
                while(hasNext()) {
                        Instruction i = next();
                        if(i instanceof CPInstruction && ((CPInstruction)i).getIndex() == index)
                                return (CPInstruction)i;
                }
                return null;
        }
        
        public CPInstruction prevCPInstruction(int index) {
                while(hasPrev()) {
                        Instruction i = prev();
                        if(i instanceof CPInstruction && ((CPInstruction)i).getIndex() == index)
                                return (CPInstruction)i;
                }
                return null;
        }
        
        public LocalVariableInstruction nextLocalVariableInstruction(int index) {
                while(hasNext()) {
                        Instruction i = next();
                        if(i instanceof LocalVariableInstruction && ((LocalVariableInstruction)i).getIndex() == index)
                                return (LocalVariableInstruction)i;
                }
                return null;
        }
        
        public LocalVariableInstruction prevLocalVariableInstruction(int index) {
                while(hasPrev()) {
                        Instruction i = prev();
                        if(i instanceof LocalVariableInstruction && ((LocalVariableInstruction)i).getIndex() == index)
                                return (LocalVariableInstruction)i;
                }
                return null;
        }
}