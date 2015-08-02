export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:`pwd`/libs/sicstus/:`pwd`/libs/z3 && 
cd target && 
unzip -o -q -d . *.jar resources/\* &&
java -jar *.jar
