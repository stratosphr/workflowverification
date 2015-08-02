%-----------------------------------------------------------------------------%
% Constrains two arrays of int variables, 'f' and 'invf', to represent
% inverse functions.  All the values in each array must be within the index
% set of the other array.
%-----------------------------------------------------------------------------%

include "sicstus.mzn";

predicate inverse(array[int] of var int: f, array[int] of var int: invf) = 
    inverse_sicstus(f, invf, min(index_set(f)), min(index_set(invf)));

    
