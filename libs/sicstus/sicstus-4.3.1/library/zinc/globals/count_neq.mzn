%-----------------------------------------------------------------------------%
% Constrains 'c' to be the number of occurrences of 'y' in 'x'. This
% is less general than the Beldiceanu version, which takes an extra
% relational operation argument, and so allows "equal" in the previous
% sentence to be something else, eg. greater-than.
%-----------------------------------------------------------------------------%
predicate count_neq(array[int] of var int: x, var int: y, var int: c) =
    % let {
    %         var int : sum
    % } in
    % count_sicstus(x, y, sum) /\ sum != c
    c != sum(i in index_set(x)) ( bool2int(x[i] == y) )
    ;

