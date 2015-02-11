package ast.type;

public class ASTLabel {
	public static final ASTLabel Pub = new ASTLabel("public", 0);
	public static final ASTLabel Alice = new ASTLabel("alice", 1);
	public static final ASTLabel Bob = new ASTLabel("bob", 2);
	public static final ASTLabel Secure = new ASTLabel("secure", 3);
//	public static final ASTLabel Affine = new ASTLabel("affine", 4);
//	public static final ASTLabel Infer = new ASTLabel("", 5);
	
	private String name;
	private int id;
	
	public static int getLabelNumber() {
		return 6;
	}
	
	private ASTLabel() {};
	private ASTLabel(String name, int id) { this.name = name; this.id = id; };
	
	public String toString() {
		return name;
	}
	
	public int getId() {
		return id;
	}
	
	public ASTLabel meet(ASTLabel lab) {
		if(this == ASTLabel.Pub)
			return lab;
		if(lab == ASTLabel.Pub)
			return this;
		// TODO
		return ASTLabel.Secure;
	}
}
