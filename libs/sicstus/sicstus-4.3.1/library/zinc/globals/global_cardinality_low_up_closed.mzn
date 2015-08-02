%-----------------------------------------------------------------------------%
% Requires that the number of occurences of 'cover[i]' in 'x' is between
% 'lbound[i]' and 'ubound[i]'.
% The elements of 'x' must take their values from 'cover'.
%-----------------------------------------------------------------------------%

include "sicstus.mzn";

predicate global_cardinality_low_up_closed(array[int] of var int: x,
                                           array[int] of int: cover,
				           array[int] of int: lbound,
                                           array[int] of int: ubound) =
    global_cardinality_low_up_sicstus(x, cover, lbound, ubound, 1);