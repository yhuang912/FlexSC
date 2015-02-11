package ast.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ast.expr.ASTExpression;

public class ASTRecType extends ASTType {

	public String name;
	public ASTLabel lab;
	public List<ASTType> typeVariables;
	public List<ASTExpression> bitVariables;
	public List<String> fields;
	public Map<String, ASTType> fieldsType;
	
	public ASTRecType(String name, ASTLabel lab) {
		this.name = name;
		this.lab = lab;
		this.typeVariables = null;
		this.bitVariables = new ArrayList<ASTExpression>();
		this.fieldsType = new HashMap<String, ASTType>();
		this.fields = new ArrayList<String>();
	}

	@Override
	public String toString(int indent) {
		return toString();
	}

	public boolean isDefine() {
		return this.fields != null;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("struct "+this.shortName());
		sb.append(" {\n");
		for(String i : this.fields) {
			sb.append("\t"+this.fieldsType.get(i)+" "+i+";\n");
		}
		sb.append("};\n");
		return sb.toString();
	}

	@Override
	public boolean match(ASTType type) {
		if(!(type instanceof ASTRecType))
			return false;
		ASTRecType rt = (ASTRecType)type;
		
		if(!name.equals(rt.name) || 
				!((typeVariables == null && rt.typeVariables == null) 
						|| (typeVariables != null && rt.typeVariables != null && typeVariables.size() == rt.typeVariables.size())))
			return false;
		
		if(typeVariables != null) {
			for(int i=0; i<typeVariables.size(); ++i)
				if(!typeVariables.get(i).match(rt.typeVariables.get(i)))
					return false;
		}
		
		for(String s : fields) {
			if(!fieldsType.containsKey(s) || !rt.fieldsType.containsKey(s) || !fieldsType.get(s).match(rt.fieldsType.get(s)))
				return false;
		}
		
		return true;
	}

	@Override
	public String shortName() {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		for(int i=0; i<this.bitVariables.size(); ++i)
			sb.append("@"+this.bitVariables.get(i));
		if(typeVariables != null) {
			sb.append("<");
			for(int i=0; i<typeVariables.size(); ++i) {
				if(i > 0)
					sb.append(",");
				sb.append(typeVariables.get(i).shortName());
			}
			sb.append(">");
		}
		return sb.toString();
	}
	
	public boolean instance(ASTType type) {
		if(!(type instanceof ASTRecType))
			return false;
		ASTRecType rt = (ASTRecType)type;
		if(rt.typeVariables != null) {
			if(this.typeVariables == null || this.typeVariables.size() != rt.typeVariables.size())
				return false;
		}
		return rt.name.equals(this.name) && rt.bitVariables.size() == this.bitVariables.size();
	}
}
