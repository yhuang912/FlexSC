package type.manage;

public class RndType extends Type {

	public VariableConstant bit;
	private Label2Infer lab; 
	
	public void setLabel(Label lab) {
		System.out.println("Set label here: "+this.lab.lab+" -> "+lab);
		this.lab.lab = lab;
	}
	
	public RndType(VariableConstant bit, Label lab) {
		super(bit == null ? "rnd" : "rnd@"+bit);
		this.bit = bit;
		this.lab = new Label2Infer(lab);
	}

	
	public boolean equals(Object obj) {
		if(!(obj instanceof RndType))
			return false;
		RndType other = (RndType)obj;
		return bit.equals(other.bit) && lab.lab == other.lab.lab;
	}

	@Override
	public VariableConstant getBits() {
		return bit;
	}

	@Override
	public Label getLabel() {
		return lab.lab;
	}
	
	public String toString() {
		return this.lab.lab + " " + this.name;
	}
}
