include "sicstus.mzn";

%-----------------------------------------------------------------------------%
% Constrains the array of objects 'x' to form a circuit.
%-----------------------------------------------------------------------------%

predicate circuit(array[int] of var int: x) =
	    circuit_sicstus(x, index_set(x));
