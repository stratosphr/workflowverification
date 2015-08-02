#
# Copyright (C) 2004 Swedish Institute of Computer Science.
#
# NOTE: This Makefile requires GNU make.
#

SRCDIR=.

.PHONY: default
default: all

# Include common Makefile parts.
# [PM] 4.2 $(SRCDIR)/... should really be configure-output-directory or some such
include $(SRCDIR)/../../Common
vpath config.status $(SRCDIR)/../../
vpath env_vsnet.sh.in $(SRCDIR)/../../Distrib/Win32

ifeq ($(WIN32),no)		# unix
# No .NET on unix yet.
else				# win32

SP_ROOTLIBDIR=$(SP_BINDIR)

# [PM] 3.11.3 this is now done in Utils/Makefile library-post
PBDLL := 
# PBDLL=$(SP_ROOTLIBDIR)/prologbeans.dll

.PHONY: all
all:  $(PBDLL) examples


# .LOW_RESOLUTION_TIME: $(PBDLL)
$(PBDLL): prologbeans.dll
	$(INSTALL) "$<" "$@"
GENERATED_FILES += $(PBDLL)

env_vsnet.sh := env_vsnet.sh

# [PM] 4.2 generate on demand.
env_vsnet.sh : % : %.in config.status
	$(POSIX_SHELL) $(filter %config.status,$^) '--file=$@:$<'
	if test -f '$@'; then : ; else echo 'Did not create $@' 1>&2; exit 1 ; fi
	if test -s '$@'; then : ; else echo 'Created empty $@' 1>&2; exit 1 ; fi
GENERATED_FILES += env_vsnet.sh


OutputDirectoryRoot = ./docs
GENERATED_FILES += $(OutputDirectoryRoot)

HtmlHelp2OutputDirectory = $(OutputDirectoryRoot)/HtmlHelp2
GENERATED_FILES += $(HtmlHelp2OutputDirectory)
JavaDocOutputDirectory = $(OutputDirectoryRoot)/JavaDoc
GENERATED_FILES += $(JavaDocOutputDirectory)

# [PD] 4.0.0beta2 Prologbeans.NET now uses the same source code as PB/Java
#                 Eventually we should remove the *.cs files, and
#                 remove everything in this Makefile referring to them.
# [PM] 3.11.3 lazy, for now
#PROLOGBEANS_SOURCES = $(wildcard *.cs)
#PROLOGBEANS_SOURCES = $(wildcard *.java)
# [PD] 4.0.0beta2 Dummy files for classes not in .NET. All methods
#                 throw exceptions.
PROLOGBEANS_SOURCES_EXTRA = Context.java HttpSession.java HttpSessionBindingEvent.java HttpSessionBindingListener.java InitialContext.java
# [PD] 4.0.0beta2 Source files for Prologbeans. Common to both Java
#                 and .NET.
PROLOGBEANS_SOURCES = $(wildcard ../../se/sics/prologbeans/*.java) $(PROLOGBEANS_SOURCES_EXTRA)

# [PM] 4.0beta3 of course, we need to use $(foreach F, $^, $(call NATIVEPATHBACKSLASH, $(F))) or some such
# [PD] 4.0.0beta1 For some reason the second NATIVEPATHBACKSLASH (i.e. cygwin -w)
#                 fails here with the error message:
# cygpath: error converting "AppSession.cs AssemblyInfo.cs Bindings.cs FastParser.cs FastWriter.cs PBAtom.cs PBAtomic.cs PBBignum.cs PBCompound.cs PBFloat.cs PBInteger.cs PBListCell.cs PBMonitor.cs PBNil.cs PBTerm.cs PBVariable.cs PrologSession.cs QueryAnswer.cs SupportClass.cs" - File name too long
#                 Replaced the following with the foreach below.
#               $(patsubst %, '%', $(call NATIVEPATHBACKSLASH, $^))

# [PM] 4.2 updated but not tested
# prologbeans.dll: $(PROLOGBEANS_SOURCES) $(env_vsnet.sh)
# 	. $(filter %$(env_vsnet.sh),$^) && $(CSC) $(CSCFLAGS) \
#                '/out:$(call NATIVEPATHBACKSLASH, $@)' \
#                /doc:prologbeans.NET.xml \
#                /target:library \
#                $(patsubst %, '%', $(foreach PBFILE,$(filter %.cs,$^),$(call NATIVEPATHBACKSLASH, $(PBFILE))))

prologbeans.dll: $(PROLOGBEANS_SOURCES) $(env_vsnet.sh)
	. $(dir $(filter %$(env_vsnet.sh),$^))$(notdir $(filter %$(env_vsnet.sh),$^)) && $(VJC) $(VJCFLAGS) \
               '/out:$(call NATIVEPATHBACKSLASH, $@)' \
               /target:library \
               $(patsubst %, '%', $(foreach PBFILE,$(filter %.java,$^),$(call NATIVEPATHBACKSLASH, $(PBFILE))))

GENERATED_FILES += prologbeans.dll prologbeans.NET.xml


# # [PM] 3.11.3 Set this to VS.NET 2003 with NDOC 1.3, HtmlHelp2 with NDOC 1.2
NDOCHELPDOCUMENTER ?= $(error NDOCHELPDOCUMENTER should be set in Common)

DOCTYPES = javadoc

ifeq ($(ENABLE_HtmlHelp2), yes)
DOCTYPES += htmlhelp2
endif				# ENABLE_HtmlHelp2

DOCTYPES = htmlhelp2

.PHONY: doc
doc: $(addsuffix _doc, $(DOCTYPES))

# NDOCFLAGS = -verbose

# [PM] 3.11.3 NOTE: For some reason you cannot build the HtmlHelp2
# docs via Cygwin sshd. You get a
# System.UnauthorizedAccessException. Probably somthing to do with the
# hxconv COM objects.

.PHONY: htmlhelp2_doc
htmlhelp2_doc: prologbeans.NET.HtmlHelp2.ndoc prologbeans.dll
	mkdir -p $(HtmlHelp2OutputDirectory)
	$(NDOC) $(NDOCFLAGS) -documenter='$(NDOCHELPDOCUMENTER)' -project=$<
	rm -f $(HtmlHelp2OutputDirectory)/*.log

.PHONY: javadoc_doc
javadoc_doc: prologbeans.NET.JavaDoc.ndoc prologbeans.dll
	mkdir -p $(JavaDocOutputDirectory)
	$(NDOC) -documenter=JavaDoc -project=$<

.PHONY: install_docs
install_docs: HtmlHelp2OutputDirectory = $(HtmlHelp2DOCSDIR)
install_docs: JavaDocOutputDirectory = $(JavaDocDOCSDIR)
install_docs: doc


#	set -e;                                                    \
#	echo 'Installing into $(HtmlHelp2DOCSDIR)';                \
#	for f in $(HtmlHelp2OutputDirectory)/*; do                 \
#	  echo "Installing $${f}";                                 \
#	  $(INSTALL_DATA) "$${f}" "$(HtmlHelp2DOCSDIR)/$${f##*/}"; \
#	done;                                                      \
#	echo 'Installing into $(JavaDocDOCSDIR)';                  \
#	for f in $(JavaDocOutputDirectory)/*; do                   \
#	  echo "Installing $${f}";                                 \
#	  $(INSTALL_DATA) "$${f}" "$(JavaDocDOCSDIR)/$${f##*/}";   \
#	done

# [PD] 4.0.0beta2 Generate an XML-file from the Javadoc comments. This
#                 XML is processed by NDoc (above) to create
#                 API-documentations. Unfortunately NDoc does not (yet?)
#                 handle .NET 2.0 assemblies which is what we get when
#                 compiling with vjc.exe.
#                 *** FIXME: What to do, what to do?
DOCLETDIR=../../se/sics/sicstus/doctools

prologbeans.NET.xml: PBDoclet.class $(PROLOGBEANS_SOURCES)
	$(JAVADOC) -assembly prologbeans -doclet $(patsubst %.class,%,$<) -docletpath $(DOCLETDIR) $(PROLOGBEANS_SOURCES)
	cp prologbeans.xml $@
GENERATED_FILES += prologbeans.NET.xml prologbeans.xml

JAVADOC_CLASSPATH=$(call NATIVEPATH, $(JAVAC)/../../lib/tools.jar)

PBDoclet.class: $(DOCLETDIR)/PBDoclet.java
	$(JAVAC) -classpath $(JAVADOC_CLASSPATH) $<
GENERATED_FILES += $(DOCLETDIR)/PBDoclet.class

prologbeans.NET.JavaDoc.ndoc prologbeans.NET.HtmlHelp2.ndoc: prologbeans.NET.ndoc.in prologbeans.NET.xml
	sed -e 's,@HtmlHelp2OutputDirectory@,$(subst \,\\,$(call NATIVEPATHBACKSLASH, $(HtmlHelp2OutputDirectory))),g' \
	    -e 's,@JavaDocOutputDirectory@,$(subst \,\\,$(call NATIVEPATHBACKSLASH, $(JavaDocOutputDirectory))),g' < "$<" > "$@"
GENERATED_FILES += prologbeans.NET.JavaDoc.ndoc prologbeans.NET.HtmlHelp2.ndoc

EXAMPLE_EXES := examples/evaluate.NET/EvaluateGUI.exe examples/pbtest.NET/PBTest.exe

.PHONY: examples
examples: $(EXAMPLE_EXES)

$(EXAMPLE_EXES): %.exe: prologbeans.dll %.cs $(env_vsnet.sh)
	. $(dir $(filter %$(env_vsnet.sh),$^))$(notdir $(filter %$(env_vsnet.sh),$^)) && $(CSC) $(CSCFLAGS) \
                '/reference:$(call NATIVEPATHBACKSLASH, $<)' \
		$(if $(DOCLETDIR), '/reference:$(call NATIVEPATHBACKSLASH, $(DOTNET2DIR)/vjslib.dll)') \
		$(csc_flags_extra) \
                '/out:$(call NATIVEPATHBACKSLASH, $@)' \
                $(patsubst %, '%', $(foreach F, $(filter %.cs, $^), $(call NATIVEPATHBACKSLASH, $(F))))
GENERATED_FILES += $(EXAMPLE_EXES)

endif				# win32

.PHONY: clean distclean mostlyclean
clean distclean mostlyclean:
	rm -rf  $(GENERATED_FILES)
	find . -name 'prologbeans.dll' -print -exec rm {} \;
	find . -name '*.pdb' -print -exec rm {} \;
	find . -name '*.suo' -print -exec rm {} \;

.PHONY:  maintainerclean
maintainerclean: clean
	rm -f examples/pbtest.NET/pbtest.exe.config
