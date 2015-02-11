package type.manage;

public class ArrayType extends Type {
	public VariableConstant size;
	public Label2Infer indexLab;
	public Type type;
	
	public ArrayType(VariableConstant size, Label indexLab, Type type) {
		super(type.toString()+"["+size+"]");
		this.size = size;
		this.type = type;
		this.indexLab = new Label2Infer(indexLab);
	}

	@Override
	public VariableConstant getBits() {
		
		if(this.indexLab.lab == Label.Pub) {
			VariableConstant vc = this.type.getBits();
			if(vc == null)
				return null;
			return null;
//			return new BOPVariableConstant(vc, scvm.BopExp.Op.Mul, size);
		} else 
			return null;
	}

	@Override
	public Label getLabel() {
		return type.getLabel().meet(indexLab.lab);
	}
	
}
