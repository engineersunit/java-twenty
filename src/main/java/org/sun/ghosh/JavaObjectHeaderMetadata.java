package org.sun.ghosh;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;
/**
 * JEP 450: Compact Object Headers (Experimental)
 * <a href="https://openjdk.org/jeps/450">JEP 450: Compact Object Headers (Experimental)</a>
 * <a href="https://www.baeldung.com/java-memory-layout">Memory Layout of Objects in Java</a>
 * <a href="https://shipilev.net/jvm/objects-inside-out/">Java Objects Inside Out</a>
 * <a href="https://hg.openjdk.org/code-tools/jol/file/tip/jol-samples/src/main/java/org/openjdk/jol/samples/">Java Object Layout (JOL)</a>
 */
public class JavaObjectHeaderMetadata {

    public static void main(String[] args) {
        System.out.println(VM.current().details());

        String s = "Java";
        System.out.println(s);
        System.out.println(s.getClass().getName());
        System.out.println(s.hashCode());

        System.out.println("String Class Layout");
        System.out.println(ClassLayout.parseClass(String.class).toPrintable());

        System.out.println("String Object Layout");
        System.out.println(ClassLayout.parseInstance(s).toPrintable());

    }
}