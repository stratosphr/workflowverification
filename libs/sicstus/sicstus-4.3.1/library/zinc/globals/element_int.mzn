%-----------------------------------------------------------------------------%
% Requires that 'y' is the ith element of the array 'x'.
%-----------------------------------------------------------------------------%

include "sicstus.mzn";

predicate element_int(var int: i, array[int] of var int: x, var int: y) =
    element_int_sicstus(i, x, y, min(index_set(x)));


