package backend.flexsc;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public abstract class Emittable {
	PrintStream out;
	Config config;

	protected boolean isClosed = false;
	
	boolean isAbstract = false;
	
	String superClass = null;
	List<String> implementInterfaces = new ArrayList<String>();
	String className = null;
	List<String> typeParameters = new ArrayList<String>(); 
	
	public Emittable(PrintStream out, Config config, String name) {
		this.out = out;
		this.config = config;
		this.className = name;
		this.isClosed = false;
	}
	
	public void emitHeader() {
		if(isClosed)
			throw new RuntimeException("Output stream is closed!");
		out.println("package "+Config.packageName+";");
		for(String s : Config.importPackage)
			out.println("import "+s+";");
		out.println("import " + Config.implementedInterface + ";");

	}

	public void emitClassHeader() {
		if(isClosed)
			throw new RuntimeException("Output stream is closed!");
		if(isAbstract)
			out.print("abstract ");
		out.print("public class "+className);
		if(typeParameters.size() > 0) {
			out.print("<");
			for(int i=0; i<typeParameters.size(); ++i) {
				if(i > 0)
					out.print(", ");
				out.print(typeParameters.get(i));
			}
			out.print(">");
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
	
	public void emitClassEnd() {
		if(isClosed)
			throw new RuntimeException("Output stream is closed!");
		out.println("}");
	}
	
	public void close() {
		if(isClosed)
			throw new RuntimeException("Output stream is closed!");
		out.close();
		this.isClosed = true;
	}
	
	public void emitFields() {
		if(isClosed)
			throw new RuntimeException("Output stream is closed!");
		this.emitFieldsInternal();
	}
	
	public void emitConstructor() {
		if(isClosed)
			throw new RuntimeException("Output stream is closed!");
		this.emitConstructorInternal();
	}
	
	public void emitMethods() {
		if(isClosed)
			throw new RuntimeException("Output stream is closed!");
		this.emitMethodsInternal();
	}
	
	public abstract void emitFieldsInternal();
	
	public abstract void emitConstructorInternal();
	
	public abstract void emitMethodsInternal();

	public void emit() {
		emitHeader();
		emitClassHeader();
		emitFields();
		emitConstructor();
		emitMethods();
		emitClassEnd();
		close();
	}

}
