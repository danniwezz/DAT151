JAVAC = javac
JAVAC_FLAGS =-cp ".:javatools/:javatools/Cup"
JAVA = java
JAVA_FLAGS=-cp ".:javatools/:javatools/Cup"
CUPFILE = FUN/_cup.cup

.PHONY: all bnfc lab4 clean clean-all

lab4: 
	${JAVAC} ${JAVAC_FLAGS} lab4.java
	chmod a+x lab4

all: bnfc lab4

bnfc:
	bnfc -java FUN.cf
	${JAVA} ${JAVA_FLAGS} JLex.Main FUN/Yylex
	${JAVA} ${JAVA_FLAGS} java_cup.Main -nopositions -expect 100 $(CUPFILE)
	mv sym.java parser.java FUN

clean:
	-rm -f FUN/Absyn/*.class FUN/*.class
	-rm -f .dvi FUN.aux FUN.log FUN.ps *.class

clean-all:
	-rm -rf FUN
	-rm -f .dvi FUN.aux FUN.log FUN.ps *.class
