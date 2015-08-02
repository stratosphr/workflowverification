%-----------------------------------------------------------------------------%
% diffn: constrains rectangles, given by their origins and sizes, to be
% non-overlapping
%-----------------------------------------------------------------------------%

include "sicstus.mzn";

predicate diffn(array[int] of var int: x,
                array[int] of var int: y,
                array[int] of var int: dx,
                array[int] of var int: dy) =
    assert(
        index_set(x) = index_set(y)  /\
        index_set(x) = index_set(dx) /\
        index_set(x) = index_set(dy),
        "diffn: index set mismatch",
    diffn_sicstus(x, y, dx, dy)
    );

%-----------------------------------------------------------------------------%
