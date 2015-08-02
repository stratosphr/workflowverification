include "sicstus.mzn";

%-----------------------------------------------------------------------------%
% Requires that 'card[i]' is the number of occurences of 'representation[i]' in 'base'.
%
% XXX: currently the values in 'representation' need not be distinct.  Perhaps they
%      should be?
%-----------------------------------------------------------------------------%
predicate distribute(array[int] of var int: card,
                     array[int] of var int: representation,
                     array[int] of var int: base) =
	distribute_sicstus(card, representation, base);
    % assert(index_set(card) == index_set(representation),
    %     "distribute: card and representation arrays must have identical index sets",
    %     forall (i in index_set(card)) (
    %         card[i] == sum(j in index_set(base)) (
    %                         bool2int(representation[i] = base[j])
    %                    )
    %     )
    % );

