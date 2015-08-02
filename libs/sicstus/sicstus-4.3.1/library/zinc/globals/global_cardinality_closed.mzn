%-----------------------------------------------------------------------------%
% Requires that the number of occurrences of 'cover[i]' in 'x' is 'counts[i]'.
% The elements of 'x' must take their values from 'cover'.
%-----------------------------------------------------------------------------%

include "sicstus.mzn";

predicate global_cardinality_closed(array[int] of var int: x,
                                    array[int] of int: cover,
                                    array[int] of var int: counts) =
    assert(index_set(cover) = index_set(counts),
        "global_cardinality_closed: " ++
        "cover and counts must have identical index sets",
    global_cardinality_sicstus(x, cover, counts, 1));