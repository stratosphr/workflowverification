%-----------------------------------------------------------------------------%
% A table constraint: table(x, t) represents the constraint x in t where we
% consider each row in t to be a tuple and t as a set of tuples.
%-----------------------------------------------------------------------------%

include "sicstus.mzn";

predicate table_bool(array[int] of var bool: x, array[int, int] of bool: t2) =
        let {
            set of int: is = index_set_1of2(t2),
            set of int: js = index_set_2of2(t2),
            int : l = card(is) * card(js),
            array [1..l] of bool : t = [t2[i,j] | i in is, j in js]
        }
	in
            table_bool_sicstus(x, t);



