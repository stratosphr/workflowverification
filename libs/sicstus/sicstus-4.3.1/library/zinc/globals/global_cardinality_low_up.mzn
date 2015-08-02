%-----------------------------------------------------------------------------%
% Requires that for all `i', the representation `cover[i]' appears at least `lbound[i]'
% and at most `ubound[i]' times in the array `x'.
%-----------------------------------------------------------------------------%

include "sicstus.mzn";

predicate global_cardinality_low_up(array[int] of var int: x,
                                    array[int] of int: cover,
				    array[int] of int: lbound,
                                    array[int] of int: ubound) =
   global_cardinality_low_up_sicstus(x, cover, lbound, ubound, 0);
