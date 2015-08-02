%-----------------------------------------------------------------------------%
% Requires that the array 'x' is lexicographically less than or equal to
% array 'y'.  Compares them from first to last element, regardless of indices.
%-----------------------------------------------------------------------------%

include "lex_lesseq_bool.mzn";
include "lex_lesseq_int.mzn";

predicate lex_lesseq(array[int] of var int: x,
                     array[int] of var int: y) =
    lex_lesseq_int(x, y);

predicate lex_lesseq(array[int] of var bool: x,
                     array[int] of var bool: y) =
    lex_lesseq_bool(x, y);

% Alternative names for the above.
%
predicate lex_leq(array[int] of var int: x, array[int] of var int: y) =
	lex_lesseq(x, y);

predicate lex_leq(array[int] of var bool: x, array[int] of var bool: y) =
	lex_lesseq(x, y);

