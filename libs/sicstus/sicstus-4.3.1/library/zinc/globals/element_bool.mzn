%-----------------------------------------------------------------------------%
% Requires that 'y' is the ith element of the array 'x'.
%-----------------------------------------------------------------------------%

include "sicstus.mzn";

predicate element_bool(var int: i, array[int] of var bool: x, var bool: y) =
    element_bool_sicstus(i, x, y, min(index_set(x)));


