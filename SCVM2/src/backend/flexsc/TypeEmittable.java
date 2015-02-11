package backend.flexsc;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import type.manage.ArrayType;
import type.manage.BitVariable;
import type.manage.FloatType;
import type.manage.IntType;
import type.manage.Label;
import type.manage.Method;
import type.manage.RecordType;
import type.manage.RndType;
import type.manage.Type;
import type.manage.Unknown;
import type.manage.VariableConstant;
import type.manage.VariableType;
import util.Pair;

public class TypeEmittable extends Emittable {

	CodeGenVisitor codeGen;
	RecordType type;
	
	public TypeEmittable(Config config, CodeGenVisitor codeGen, RecordType type) throws IOException {
		super(new PrintStream(new File(Config.path+"/"+type.name+".java")), config, type.name);
		this.codeGen = codeGen;
		this.type = type;
		
		if(type.typeParameter != null) {
			for(int i = 0; i<type.typeParameter.size(); ++i) {
				this.typeParameters.add(
						((VariableType)type.typeParameter.get(i)).name
						+" extends IWritable<"+((VariableType)type.typeParameter.get(i)).name+","+codeGen.dataType+">"
						);
			}
		}
		
		codeGen.isDefine = false;
		if(this.inheritWritable(type)) {
			this.implementInterfaces.add("IWritable<"+codeGen.visit(type)+", "+codeGen.dataType+">");
		}
	}
	

	public void emitClassHeader() {
		if(isClosed)
			throw new RuntimeException("Output stream is closed!");
		if(isAbstract)
			out.print("abstract ");
		out.print("public class "+className);
		if(Config.useTemplate) {
			if(typeParameters.size() > 0) {
				out.print("<");
				out.print(codeGen.dataType);
				for(int i=0; i<typeParameters.size(); ++i) {
					out.print(", ");
					out.print(typeParameters.get(i));
				}
				out.print(">");
			}  else {
				out.print("<"+codeGen.dataType+">");
			}
		} else {
			if(typeParameters.size() > 0) {
				out.print("<");
				for(int i=0; i<typeParameters.size(); ++i) {
					if(i > 0) out.print(", ");
					out.print(typeParameters.get(i));
				}
				out.print(">");
			}
		}
		if(superClass != null)
			out.print(" extends "+superClass);
		if(implementInterfaces.size() > 0) {
			out.print(" implements ");
			for(int i=0; i<implementInterfaces.size(); ++i) {
				if(i > 0)
					out.print(", ");
				out.print(implementInterfaces.get(i));
			}
		}
		out.println(" {");
	}
	
	
	@Override
	public void emitFieldsInternal() {
		// Local variables
		for(Map.Entry<String, Type> ent : type.fields.entrySet()) {
			out.println("\tpublic "+codeGen.visit(ent.getValue())+" "+ent.getKey()+";");
		}
		out.println();

		// Environment
		if(type.name.equals("NoClass")) {
			for(int i=0; i<Config.types.length; ++i)
				out.println("\tpublic "+Config.types[i].replace("@dataType", codeGen.dataType)+" "+Config.fields[i]+";");
		} else {
			for(int i=0; i<Config.types.length; ++i)
				out.println("\tpublic "+Config.types[i].replace("@dataType", codeGen.dataType)+" "+Config.fields[i]+";");
		}

		// Factories
		for(Type tt : type.typeParameter) {
			VariableType vt = (VariableType)tt;
			out.println("\tprivate "+vt.name+" factory"+vt.name+";");
		}

		// Bit variables
		for(VariableConstant vc : type.bits) {
			BitVariable bit = (BitVariable)vc;
			out.println("\tprivate int "+bit.var+";");
		}
		out.println();
	}

	@Override
	public void emitConstructorInternal() {
		// Constructor
		out.println("\tpublic "+codeGen.constructorWithNonStruct(type)+" throws Exception {");
		
		for(int i=0; i<Config.fields.length; ++i) {
			out.println("\t\tthis."+Config.fields[i]+" = "+Config.fields[i]+";");
			break;
		}
		// Hard Code Here!
		out.println("\t\tthis.intLib = new IntegerLib<"+codeGen.dataType+">(env);");
		out.println("\t\tthis.floatLib = new FloatLib<"+codeGen.dataType+">(env, 24, 8);");
		// Hard Code Finished!
		
		for(VariableConstant vc : type.bits) {
			BitVariable bit = (BitVariable)vc;
			out.println("\t\tthis."+bit.var+" = "+bit.var+";");
		}
		if(type.typeParameter != null)
			for(Type tt : type.typeParameter) {
				VariableType vt = (VariableType)tt;
				out.println("\t\tthis.factory"+vt.name+" = factory"+vt.name+";");
			}
		for(Map.Entry<String, Type> ent : type.fields.entrySet()) {
			String cons = codeGen.constructor(ent.getValue());
			if(ent.getValue().getBits() == null)
				out.println("\t\tthis."+ent.getKey()+" = "+ent.getKey()+";");
			else {
				if(cons == null)
					continue;
				out.println("\t\tthis."+ent.getKey()+" = "+cons+";");
			}
		}
		out.println("\t}");

		out.println();
	}

	public boolean inheritWritable(Type type) {
		// TODO support arrays
		return type.getBits() != null;
	}
	
	public void emitNumBits() {
		out.println("\tpublic int numBits() {");
		if(!(type.getBits() instanceof Unknown)) {
			out.println("\t\treturn "+type.getBits()+";\n\t}");
		} else {
			out.println("\t\tint sum = 0;");
			for(Map.Entry<String, Type> ent : type.fields.entrySet()) {
				if(ent.getValue() instanceof VariableType) {
					VariableType vt = (VariableType)ent.getValue();
					out.println("\t\tsum += factory"+vt.name+".numBits();");
				} else if (ent.getValue() instanceof IntType ||
						ent.getValue() instanceof RndType ||
						ent.getValue() instanceof FloatType
						) {
					out.println("\t\tsum += "+ent.getKey()+".length;");
				} else {
					out.println("\t\tsum += "+ent.getKey()+".numBits();");
				}
			}
			out.println("\t\treturn sum;");
			out.println("\t}\n");
		}
	}

	public void emitGetBits() {
		out.println("\tpublic "+codeGen.dataType+"[] getBits() {");
//		out.println("\t\t"+codeGen.dataType+"[] ret = new "+codeGen.dataType+"[this.numBits()];");
		out.println("\t\t"+codeGen.dataType+"[] ret = env.newTArray(this.numBits());");
		out.println("\t\t"+codeGen.dataType+"[] tmp_b;");
		out.println("\t\t"+codeGen.dataType+" tmp;");
		out.println("\t\tint now = 0;");
		for(Map.Entry<String, Type> ent : type.fields.entrySet()) {
			String var = "tmp_b";
			if(ent.getValue().getBits().isConstant(1))
				var = "tmp";
			if(ent.getValue() instanceof IntType ||
					ent.getValue() instanceof FloatType ||
					ent.getValue() instanceof RndType
					)
				out.println("\t\t"+var+" = "+ent.getKey()+";");
			else {
				out.print("\t\t"+var+" = this."+ent.getKey()+".getBits()");
				if(var.equals("tmp"))
					out.print("[0]");
				out.println(";");
			}
			if(var.equals("tmp")) {
				out.println("\t\tret[now] = tmp;");
				out.println("\t\tnow ++;");
			} else {
				out.println("\t\tSystem.arraycopy(tmp_b, 0, ret, now, tmp_b.length);");
				out.println("\t\tnow += tmp_b.length;");
			}
		}
		out.println("\t\treturn ret;");
		out.println("}\n");
	}

	public void emitNewObj() {
		out.println("\tpublic "+codeGen.visit(type)+" newObj("+codeGen.dataType+"[] data) throws Exception {");
		out.println("\t\tif(data == null) {");
//		out.println("\t\t\tdata = new "+codeGen.dataType+"[this.numBits()];");
		out.println("\t\t\tdata = env.newTArray(this.numBits());");
		out.println("\t\t\tfor(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }");
		out.println("\t\t}");
		out.println("\t\tif(data.length != this.numBits()) return null;");
		out.println("\t\t"+codeGen.visit(type)+" ret = new "+codeGen.constructor(type, false)+";");
		out.println("\t\t"+codeGen.dataType+"[] tmp;");
		out.println("\t\tint now = 0;");
		for(Map.Entry<String, Type> ent : type.fields.entrySet()) {
			if(ent.getValue() instanceof IntType) {
				IntType it = (IntType)ent.getValue();
				if(it.bit.isConstant(1)) {
					out.println("\t\tret."+ent.getKey()+" = data[now];");
					out.println("\t\tnow ++;");
				} else {
					out.println("\t\tret."+ent.getKey()+" = env.newTArray("+it.bit+");");
					out.println("\t\tSystem.arraycopy(data, now, ret."+ent.getKey()+", 0, "+it.bit+");");
					out.println("\t\tnow += "+it.bit+";");
				}
			} else if(ent.getValue() instanceof RndType) {
				RndType it = (RndType)ent.getValue();
				if(it.bit.isConstant(1)) {
					out.println("\t\tret."+ent.getKey()+" = data[now];");
					out.println("\t\tnow ++;");
				} else {
					out.println("\t\tret."+ent.getKey()+" = env.newTArray("+it.bit+");");
					out.println("\t\tSystem.arraycopy(data, now, ret."+ent.getKey()+", 0, "+it.bit+");");
					out.println("\t\tnow += "+it.bit+";");
				}
			} else if(ent.getValue() instanceof FloatType) {
				FloatType it = (FloatType)ent.getValue();
				if(it.bit.isConstant(1)) {
					out.println("\t\tret."+ent.getKey()+" = data[now];");
					out.println("\t\tnow ++;");
				} else {
					out.println("\t\tret."+ent.getKey()+" = env.newTArray("+it.bit+");");
					out.println("\t\tSystem.arraycopy(data, now, ret."+ent.getKey()+", 0, "+it.bit+");");
					out.println("\t\tnow += "+it.bit+";");
				}
			} else if (ent.getValue() instanceof RecordType){
				RecordType rt = (RecordType)ent.getValue();
				out.println("\t\tret."+ent.getKey()+" = new "+codeGen.constructor(rt, false)+";");
				out.println("\t\ttmp = env.newTArray(this."+ent.getKey()+".numBits());");
				out.println("\t\tSystem.arraycopy(data, now, tmp, 0, tmp.length);");
				out.println("\t\tnow += tmp.length;");
				out.println("\t\tret."+ent.getKey()+" = ret."+ent.getKey()+".newObj(tmp);");
			} else {
				VariableType vt = (VariableType)ent.getValue();
				out.println("\t\ttmp = env.newTArray(this.factory"+vt.name+".numBits());");
				out.println("\t\tSystem.arraycopy(data, now, tmp, 0, tmp.length);");
				out.println("\t\tnow += tmp.length;");
				out.println("\t\tret."+ent.getKey()+" = ret.factory"+vt.name+".newObj(tmp);");
			}
		}
		out.println("\t\treturn ret;");
		out.println("}\n");
	}
	
	public void emitWritableFunctions() {
		this.emitNumBits();
		this.emitGetBits();
		this.emitNewObj();
	}
	
	public void emitAMethod(Method method) {
		out.print("\tpublic ");
		out.print(codeGen.visit(method.returnType)+" "+method.name+"(");
		boolean f = true;
		for(String s : method.bitParameters) {
			if(f) f = false;
			else out.print(", ");
			out.print("int "+s);
		}
		for(Pair<Type, String> ent : method.parameters) {
			if(f) f = false;
			else out.print(", ");
			out.print(codeGen.visit(ent.left)+" "+ent.right);
		}
		if(method.isPhantom) {
			if(f) f = false;
			else out.print(", ");
			out.print(codeGen.dataType+" __isPhantom");
		}
		out.println(") throws Exception {");
		if(method.isPhantom) {
			// Phantomize the input
			for(Pair<Type, String> ent : method.parameters) {
				Type type = ent.left;
				if(type.getLabel() == Label.Pub)
					continue;
				// TODO check if type is not affine, then continue
				String ret = "";
				if(type instanceof RecordType) {
					ret += "new "+codeGen.constructor((RecordType)type, false)+".newObj(";
				} else if (type instanceof VariableType) {
					ret += "this.factory"+((VariableType)type).name+".newObj(";
				}
				ret += "intLib.mux("+ent.right;
				if(!(type instanceof IntType) &&
						!(type instanceof FloatType) &&
						!(type instanceof RndType))
					ret += ".getBits(), ";
				else
					ret += ", ";
				ret += codeGen.constructor(ent.left);
				ret += ", __isPhantom)";
				if(type instanceof RecordType) {
					ret += ")";
				} else if (type instanceof VariableType) {
					ret += ")";
				}
				out.println("\t\t"+ent.right+" = "+ret+";");
			}
		}
		
		for(Pair<Type, String> ent : method.localVariables) {
			String cons = codeGen.constructor(ent.left);
			if(cons != null)
				cons = " = "+cons;
			else
				cons = "";
			out.println("\t\t"+codeGen.visit(ent.left)+" "+ent.right+cons+";");
			initialize(ent.right, ent.left, 2);
		}
		codeGen.indent = 2;
		out.println(codeGen.visit(method.code));
		out.println("\t}");
	}
	
	private String indent(int lvl) {
		if(lvl == 0)
			return "";
		else
			return "\t"+indent(lvl-1);
	}
	
	private void initialize(String field, Type type, int level) {
		if(type instanceof ArrayType) {
			ArrayType at = (ArrayType)type;
			String cons = codeGen.constructor(at.type);
			String li = "_j_"+level;
			if(cons != null) {
				out.println(indent(level)+"for(int "+li+" = 0; "+li+" < "+at.size+"; ++"+li+") {");
				out.println(indent(level+1)+field+"["+li+"] = "+cons+";");
				initialize(field+"["+li+"]", at.type, level + 1);
				out.println(indent(level)+"}");
			}
		} else if(type instanceof RecordType) {
			RecordType rt = (RecordType)type;
			for(Map.Entry<String, Type> ent : rt.fields.entrySet()) {
				initialize(field+"."+ent.getKey(), ent.getValue(), level);
			}
		}
	}
	
	@Override
	public void emitMethodsInternal() {
		if(this.inheritWritable(type))
			emitWritableFunctions();
		
		for(Method meth : type.getMethods())
			emitAMethod(meth);
	}

}
