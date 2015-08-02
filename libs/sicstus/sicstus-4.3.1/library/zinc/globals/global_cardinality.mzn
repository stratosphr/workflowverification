%-----------------------------------------------------------------------------%
% Requires that the number of occurrences of 'cover[i]' in 'x' is 'counts[i]'.
%-----------------------------------------------------------------------------%

include "sicstus.mzn";

predicate global_cardinality(array[int] of var int: x,
	  		     array[int] of int: cover,
                             array[int] of var int: counts) =
    assert(index_set(cover) = index_set(counts),
        "global_cardinality: cover and counts must have identical index sets",
    global_cardinality_sicstus(x, cover, counts, 0));

