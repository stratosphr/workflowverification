/* Copyright(C) 1997, Swedish Institute of Computer Science */

:- meta_predicate
	labeling(:, ?),
	minimize(0, ?),
	minimize(0, ?, ?),
	maximize(0, ?),
	maximize(0, ?, ?).

:- dynamic incumbent/2.
:- volatile incumbent/2.

:- public
	fdbg_start_labeling/1,	% called by fdbg
	fdbg_labeling_step/2.

labeling_options([], Opt, Opt, _).
labeling_options([X|L], Opt0, Opt, Goal) :- !,
	(   nonvar(X),
	    labeling_option(X, Opt0, Opt1) -> true
        ;   illarg(domain(term,labeling_option), Goal, 1, X)
        ),
	labeling_options(L, Opt1, Opt, Goal).

% statistics option:
labeling_option(assumptions(K), 
		opt(Search,Sel,Enum,_,DU,TO,Module),
		opt(Search,Sel,Enum,K,DU,TO,Module)).
% variable choice options:
labeling_option(leftmost, 
		opt(Search,_       ,Enum,K,DU,TO,Module),
		opt(Search,leftmost,Enum,K,DU,TO,Module)).
labeling_option(min, 
		opt(Search,_  ,Enum,K,DU,TO,Module),
		opt(Search,min,Enum,K,DU,TO,Module)).
labeling_option(max, 
		opt(Search,_  ,Enum,K,DU,TO,Module),
		opt(Search,max,Enum,K,DU,TO,Module)).
labeling_option(ff, 
		opt(Search,_ ,Enum,K,DU,TO,Module),
		opt(Search,ff,Enum,K,DU,TO,Module)).
labeling_option(ffc, 
		opt(Search,_  ,Enum,K,DU,TO,Module),
		opt(Search,ffc,Enum,K,DU,TO,Module)).
labeling_option(input_order, 
		opt(Search,_       ,Enum,K,DU,TO,Module),
		opt(Search,leftmost,Enum,K,DU,TO,Module)).
labeling_option(first_fail,	% 4.3
		opt(Search,_ ,Enum,K,DU,TO,Module),
		opt(Search,ff,Enum,K,DU,TO,Module)).
labeling_option(anti_first_fail, % 4.3
		opt(Search,_              ,Enum,K,DU,TO,Module),
		opt(Search,anti_first_fail,Enum,K,DU,TO,Module)).
labeling_option(smallest,	% 4.3
		opt(Search,_  ,Enum,K,DU,TO,Module),
		opt(Search,min,Enum,K,DU,TO,Module)).
labeling_option(largest,	% 4.3
		opt(Search,_  ,Enum,K,DU,TO,Module),
		opt(Search,max,Enum,K,DU,TO,Module)).
labeling_option(occurrence,	% 4.3
		opt(Search,_         ,Enum,K,DU,TO,Module),
		opt(Search,occurrence,Enum,K,DU,TO,Module)).
labeling_option(most_constrained, % 4.3
		opt(Search,_  ,Enum,K,DU,TO,Module),
		opt(Search,ffc,Enum,K,DU,TO,Module)).
labeling_option(max_regret,	% 4.3
		opt(Search,_         ,Enum,K,DU,TO,Module),
		opt(Search,max_regret,Enum,K,DU,TO,Module)).
labeling_option(variable(Sel),
		opt(Search,_                   ,Enum,K,DU,TO,Module),
		opt(Search,variable(Module:Sel),Enum,K,DU,TO,Module)).
% value choice options
labeling_option(enum,
		opt(Search,Sel,Enum   ,K,DU,TO,Module),
		opt(Search,Sel,enum(A),K,DU,TO,Module)) :-
	arg(1, Enum, A).
labeling_option(step,
		opt(Search,Sel,Enum   ,K,DU,TO,Module),
		opt(Search,Sel,step(A),K,DU,TO,Module)) :-
	arg(1, Enum, A).
labeling_option(bisect,
		opt(Search,Sel,Enum     ,K,DU,TO,Module),
		opt(Search,Sel,bisect(A),K,DU,TO,Module)) :-
	arg(1, Enum, A).
labeling_option(up,
		opt(Search,Sel,Enum0,K,DU,TO,Module),
		opt(Search,Sel,Enum ,K,DU,TO,Module)) :-
	Enum0 =.. [F|_], Enum =.. [F,up].
labeling_option(down,
		opt(Search,Sel,Enum0,K,DU,TO,Module),
		opt(Search,Sel,Enum ,K,DU,TO,Module)) :-
	Enum0 =.. [F|_], Enum =.. [F,down].
labeling_option(median,	% 4.3
		opt(Search,Sel,Enum0,K,DU,TO,Module),
		opt(Search,Sel,Enum ,K,DU,TO,Module)) :-
	Enum0 =.. [F|_], Enum =.. [F,median].
labeling_option(middle,	% 4.3
		opt(Search,Sel,Enum0,K,DU,TO,Module),
		opt(Search,Sel,Enum ,K,DU,TO,Module)) :-
	Enum0 =.. [F|_], Enum =.. [F,middle].
labeling_option(value(Enum), 
		opt(Search,Sel,_                 ,K,DU,TO,Module),
		opt(Search,Sel,value(Module:Enum),K,DU,TO,Module)).
% solution options
labeling_option(discrepancy(DU),
		opt(Search,Sel,Enum,K,_ ,TO,Module),
		opt(Search,Sel,Enum,K,DU,TO,Module)).
labeling_option(all,
		opt(search(SMM,_  ,BR),Sel,Enum,K,DU,TO,Module),
		opt(search(SMM,all,BR),Sel,Enum,K,DU,TO,Module)).
labeling_option(best,
		opt(search(SMM,_   ,BR),Sel,Enum,K,DU,TO,Module),
		opt(search(SMM,best,BR),Sel,Enum,K,DU,TO,Module)).
labeling_option(bab,
		opt(search(SMM,AB,_  ),Sel,Enum,K,DU,TO,Module),
		opt(search(SMM,AB,bab),Sel,Enum,K,DU,TO,Module)).
labeling_option(restart,
		opt(search(SMM,AB,_      ),Sel,Enum,K,DU,TO,Module),
		opt(search(SMM,AB,restart),Sel,Enum,K,DU,TO,Module)).
labeling_option(satisfy,
		opt(search(_,      AB,BR),Sel,Enum,K,DU,TO,Module),
		opt(search(satisfy,AB,BR),Sel,Enum,K,DU,TO,Module)).
labeling_option(minimize(X),
		opt(search(_,          AB,BR),Sel,Enum,K,DU,TO,Module),
		opt(search(minimize(X),AB,BR),Sel,Enum,K,DU,TO,Module)).
labeling_option(maximize(X),
		opt(search(_,          AB,BR),Sel,Enum,K,DU,TO,Module),
		opt(search(maximize(X),AB,BR),Sel,Enum,K,DU,TO,Module)).
labeling_option(time_out(Limit,Flag),
		opt(Search,Sel,Enum,K,DU,_                   ,Module),
		opt(Search,Sel,Enum,K,DU,time_out(Limit,Flag),Module)).

nobb_labeling_top(any, L, Param, I, K, BB) :-
	nobb_labeling_top(L, Param, I, K, BB).
nobb_labeling_top(time_out(Time,Flag), L, Param, I, K, BB) :-
	time_out(clpfd:nobb_labeling_top(L, Param, I, K, BB), Time, Flag).

nobb_labeling_top(L1, Param, I, K, nobb(33554431)) :-
	labeling_accelerator(Param, A, L1, L2), !,
	nobb_labeling_fast(A, L2, I, K).
nobb_labeling_top(L, Param, I, K, BB) :-
	nobb_labeling(L, Param, I, K, BB).

labeling_accelerator(param(Sel,Enum,off), Case, L1, L2) :-
	labeling_accelerator(Sel, Enum, Case, L1, L2).

labeling_accelerator(leftmost, enum(X), leftmost_enum(X), L, L).
labeling_accelerator(leftmost, step(X), leftmost_step(X), L, L).
labeling_accelerator(leftmost, bisect(X), leftmost_bisect(X), L, L).
labeling_accelerator(ff, enum(X), ff_enum(X), L, L).
labeling_accelerator(ff, step(X), ff_step(X), L, L).
labeling_accelerator(ff, bisect(X), ff_bisect(X), L, L).
% CONSIDER changing semantics of 'ffc': presort by static degree
% labeling_accelerator(ffc, enum(X), ff_enum(X), L1, L2) :-
% 	ffc_reorder(L1, L2).
% labeling_accelerator(ffc, step(X), ff_step(X), L1, L2) :-
% 	ffc_reorder(L1, L2).
% labeling_accelerator(ffc, bisect(X), ff_bisect(X), L1, L2) :-
% 	ffc_reorder(L1, L2).

% ffc_reorder(L1, L2) :-
% 	(   foreach(X,L1),
% 	    foreach(Tag-X,KL1),
% 	    foreach(_-Y,KL2),
% 	    foreach(Y,L2)
% 	do  (   integer(X) -> Tag = 0
% 	    ;   get_fd_suspensions(X, Lists),
% 		arg(1, Lists, Degree),
% 		Tag is -Degree
% 	    )
% 	),
% 	keysort(KL1, KL2).

nobb_labeling_fast(leftmost_enum(X), L, I, K) :-
	nobb_labeling_leftmost_enum(L, X, I, K).
nobb_labeling_fast(leftmost_step(X), L, I, K) :-
	nobb_labeling_leftmost_step(L, X, I, K).
nobb_labeling_fast(leftmost_bisect(X), L, I, K) :-
	nobb_labeling_leftmost_bisect(L, X, I, K).
nobb_labeling_fast(ff_enum(X), L, I, K) :-
	nobb_labeling_ff_enum(L, X, I, K).
nobb_labeling_fast(ff_step(X), L, I, K) :-
	nobb_labeling_ff_step(L, X, I, K).
nobb_labeling_fast(ff_bisect(X), L, I, K) :-
	nobb_labeling_ff_bisect(L, X, I, K).

nobb_labeling_leftmost_enum([], _, K, K).
nobb_labeling_leftmost_enum([X|L], Param, I, K) :-
	var(X), !,
	get_atts(X, fd_attribute(_,DomM,_)),
	get_mutable(Dom, DomM),
	Dom = dom(Set,_Min,_Max,_Size),
	indomain_fast(Param, X, Set),
	J is I+1,
	nobb_labeling_leftmost_enum(L, Param, J, K).
nobb_labeling_leftmost_enum([_|L], Param, I, K) :-
	nobb_labeling_leftmost_enum(L, Param, I, K).

nobb_labeling_leftmost_step([], _, K, K).
nobb_labeling_leftmost_step([X|L], Param, I, K) :-
	var(X), !,
	get_atts(X, fd_attribute(_,DomM,_)),
	get_mutable(Dom, DomM),
	Dom = dom(Set,Min,Max,_Size),
	labeling_step_fast(Param, Set, Min, Max, X),
	J is I+1,
	nobb_labeling_leftmost_step([X|L], Param, J, K).
nobb_labeling_leftmost_step([_|L], Param, I, K) :-
	nobb_labeling_leftmost_step(L, Param, I, K).

nobb_labeling_leftmost_bisect([], _, K, K).
nobb_labeling_leftmost_bisect([X|L], Param, I, K) :-
	var(X), !,
	get_atts(X, fd_attribute(_,DomM,_)),
	get_mutable(Dom, DomM),
	Dom = dom(_Set,Min,Max,_Size),
	N is (Min+Max)>>1,
	labeling_bisect_fast(Param, N, X),
	J is I+1,
	nobb_labeling_leftmost_bisect([X|L], Param, J, K).
nobb_labeling_leftmost_bisect([_|L], Param, I, K) :-
	nobb_labeling_leftmost_bisect(L, Param, I, K).

nobb_labeling_ff_enum([], _, K, K).
nobb_labeling_ff_enum([X0|L], Param, I, K) :-
	var(X0), !,
	'$fd_delete'([X0|L], X, ff),
	get_atts(X, fd_attribute(_,DomM,_)),
	get_mutable(Dom, DomM),
	Dom = dom(Set,_Min,_Max,_Size),
	indomain_fast(Param, X, Set),
	J is I+1,
	nobb_labeling_ff_enum([X0|L], Param, J, K).
nobb_labeling_ff_enum([_|L], Param, I, K) :-
	nobb_labeling_ff_enum(L, Param, I, K).

nobb_labeling_ff_step([], _, K, K).
nobb_labeling_ff_step([X0|L], Param, I, K) :-
	var(X0), !,
	'$fd_delete'([X0|L], X, ff),
	get_atts(X, fd_attribute(_,DomM,_)),
	get_mutable(Dom, DomM),
	Dom = dom(Set,Min,Max,_Size),
	labeling_step_fast(Param, Set, Min, Max, X),
	J is I+1,
	nobb_labeling_ff_step([X0|L], Param, J, K).
nobb_labeling_ff_step([_|L], Param, I, K) :-
	nobb_labeling_ff_step(L, Param, I, K).

nobb_labeling_ff_bisect([], _, K, K).
nobb_labeling_ff_bisect([X0|L], Param, I, K) :-
	var(X0), !,
	'$fd_delete'([X0|L], X, ff),
	get_atts(X, fd_attribute(_,DomM,_)),
	get_mutable(Dom, DomM),
	Dom = dom(_Set,Min,Max,_Size),
	N is (Min+Max)>>1,
	labeling_bisect_fast(Param, N, X),
	J is I+1,
	nobb_labeling_ff_bisect([X0|L], Param, J, K).
nobb_labeling_ff_bisect([_|L], Param, I, K) :-
	nobb_labeling_ff_bisect(L, Param, I, K).

labeling_step_fast(up, _, Min, _, X) :-
	labeling_singleton(X, Min, step).
labeling_step_fast(up, _, Min, _, X) :-
	Min1 is Min+1, 
	labeling_min(X, Min1, step).
labeling_step_fast(down, _, _, Max, X) :-
	labeling_singleton(X, Max, step).
labeling_step_fast(down, _, _, Max, X) :-
	Max1 is Max-1, 
	labeling_max(X, Max1, step).
labeling_step_fast(median, Set, _, _, X) :- % 4.3
	'$fd_median'(Set, Val),
	(   labeling_singleton(X, Val, step)
	;   labeling_except(X, Val, step)
	).
labeling_step_fast(middle, Set, _, _, X) :- % 4.3
	'$fd_middle'(Set, Val),
	(   labeling_singleton(X, Val, step)
	;   labeling_except(X, Val, step)
	).

labeling_bisect_fast(up, N, X) :-
	labeling_max(X, N, bisect).
labeling_bisect_fast(up, N, X) :-
	N1 is N+1, 
	labeling_min(X, N1, bisect).
labeling_bisect_fast(down, N, X) :-
	N1 is N+1, 
	labeling_min(X, N1, bisect).
labeling_bisect_fast(down, N, X) :-
	labeling_max(X, N, bisect).

indomain_fast(up, X, [[A|B]|R]) :-
	indomain_fast(R, A, B, Val),
	labeling_singleton(X, Val, indomain_up).
indomain_fast(down, X, R) :-
	reverse(R, [[A|B]|R1]),
	indomain_fast_rev(R1, A, B, Val),
	labeling_singleton(X, Val, indomain_down).

indomain_fast([], A, A, V) :- !,
	V = A.
indomain_fast(_, A, _, A).
indomain_fast(R, A, B, V) :-
	A < B, !,
	A1 is A+1,
	indomain_fast(R, A1, B, V).
indomain_fast([[A|B]|R], _, _, V) :-
	indomain_fast(R, A, B, V).

indomain_fast_rev([], B, B, V) :- !,
	V = B.
indomain_fast_rev(_, _, B, B).
indomain_fast_rev(R, A, B, V) :-
	A < B, !,
	B1 is B-1,
	indomain_fast_rev(R, A, B1, V).
indomain_fast_rev([[A|B]|R], _, _, V) :-
	indomain_fast_rev(R, A, B, V).


% labeling, general case
nobb_labeling([], _, K, K, _).
nobb_labeling(LL, Param, I, K, BB) :-
	LL = [X|_],
	var(X), !,
	Param = param(Selector,Enum,FDBG),
	delete(Selector, LL, X1, L1),
	fdbg_start_labeling(FDBG, X1),
	labeling_cont(Enum, X1, L1, LL, R, BB, BB1),
	J is I+1,
	nobb_labeling(R, Param, J, K, BB1).
nobb_labeling([_|L], Param, I, K, BB) :-
	nobb_labeling(L, Param, I, K, BB).


%% SzT 2001.09.10, changes for FDBG

fdbg_start_labeling(Var) :-
	'$fd_debug'(FDBG,FDBG,1),
	fdbg_start_labeling(FDBG, Var).

% to indicate the start and failure of labeling
% FDBG puts advice on this!
fdbg_start_labeling(off, _Var).
fdbg_start_labeling(on, _Var).
fdbg_start_labeling(on, _Var) :- fail.

% to indicate one labeling step in user-defined labeling
% FDBG puts advice on this!
fdbg_labeling_step(_Var, _Step).

% the built-in labeling uses the following predicates to indicate a
% labeling step and to make the appropriate narrowing (reducing the
% domain to a singleton, changing the maximum, or changing the minimum,
% respectively)

% FDBG puts advice on this!
labeling_singleton(T, C, _Mode) :-
	fd_unify(T, C).

% FDBG puts advice on this!
labeling_max(T, C, _Mode) :-
	'$fd_in_interval'(T, inf, C, 1),
	'$fd_evaluate_indexical'(RC, Global),
	evaluate(RC, Global).

% FDBG puts advice on this!
labeling_min(T, C, _Mode) :-
	'$fd_in_interval'(T, C, sup, 1),
	'$fd_evaluate_indexical'(RC, Global),
	evaluate(RC, Global).

labeling_except(T, C, _Mode) :-
	'$fd_range'(C, C, R, 1),
	'$fd_dom_complement'(R, R1),
	'$fd_in_set'(T, R1, 1),
	'$fd_evaluate_indexical'(RC, Global),
	evaluate(RC, Global).

labeling_cont(value(Enum), X, L, LL, LL, BB, BB1) :-
	call(Enum, X, L, BB, BB1).
labeling_cont(enum(Arg), X, L, _LL, L, BB, BB1) :-
	get_atts(X, fd_attribute(_,DomM,_)),
	get_mutable(Dom, DomM),
	Dom = dom(Set,_Min,_Max,_Size),
	indomain(Arg, X, Set, BB, BB1).
labeling_cont(step(Arg), X, L, LL, R, BB, BB1) :-
	get_atts(X, fd_attribute(_,DomM,_)),
	get_mutable(Dom, DomM),
	Dom = dom(Set,Min,Max,_Size),
	labeling_step(Arg, Set, Min, Max, X, L, LL, R, BB, BB1).
labeling_cont(bisect(Arg), X, _L, LL, LL, BB, BB1) :-
	get_atts(X, fd_attribute(_,DomM,_)),
	get_mutable(Dom, DomM),
	Dom = dom(_Set,Min,Max,_Size),
	labeling_bisect(Arg, Min, Max, X, BB, BB1).

labeling_step(up, _, Min, _, X, L, _,  L, BB, BB) :-
	labeling_singleton(X, Min, step).
labeling_step(up, _, Min, _, X,   _, LL, LL, BB, BB1) :-
	later_bound(BB, BB1),	% TODO: keeps X
	Min1 is Min+1, 
	labeling_min(X, Min1, step).
labeling_step(down, _, _, Max, X, L, _,  L, BB, BB) :-
	labeling_singleton(X, Max, step).
labeling_step(down, _, _, Max, X,   _, LL, LL, BB, BB1) :-
	later_bound(BB, BB1),	% TODO: keeps X
	Max1 is Max-1, 
	labeling_max(X, Max1, step).
labeling_step(median, Set, _, _, X, L1, L2, L, BB, BB1) :- % 4.3
	'$fd_median'(Set, Val),
	(   L = L1,
	    BB1 = BB,
	    labeling_singleton(X, Val, step)
	;   L = L2,
	    later_bound(BB, BB1), % TODO: keeps X
	    labeling_except(X, Val, step)
	).
labeling_step(middle, Set, _, _, X, L1, L2, L, BB, BB1) :- % 4.3
	'$fd_middle'(Set, Val),
	(   L = L1,
	    BB1 = BB,
	    labeling_singleton(X, Val, step)
	;   L = L2,
	    later_bound(BB, BB1), % TODO: keeps X
	    labeling_except(X, Val, step)
	).

labeling_bisect(up, Min, Max, X, BB, BB) :-
	N is (Min+Max)>>1,
	labeling_max(X, N, bisect).
labeling_bisect(up, Min, Max, X, BB, BB1) :-
	later_bound(BB, BB1),	% TODO: var count?
	N is (Min+Max)>>1,
	N1 is N+1, 
	labeling_min(X, N1, bisect).

labeling_bisect(down, Min, Max, X, BB, BB) :-
	N is (Min+Max)>>1,
	N1 is N+1, 
	labeling_min(X, N1, bisect).
labeling_bisect(down, Min, Max, X, BB, BB1) :-
	later_bound(BB, BB1),	% TODO: var count?
	N is (Min+Max)>>1,
	labeling_max(X, N, bisect).


indomain(X) :-
	integer(X), !.
indomain(X) :-
	var(X),
	get_atts(X, fd_attribute(_,DomM,_)),
	get_mutable(Dom, DomM),
	Dom = dom(Set,Min,Max,_Size),
	integer(Min),
	integer(Max), !,
	Size is Max-Min+1,
	BB = nobb(Size),
	'$fd_debug'(FDBG,FDBG,1),
	fdbg_start_labeling(FDBG, X),
	indomain(up, X, Set, BB, _).
indomain(X) :-
	fd_argument_error(indomain(X), 1, X).

% precondition: X is not connected as in Constraint #<=> X
indomain(up, X, [[A|B]|R], BB, BB1) :-
	indomain(R, A, B, Val, BB, BB1),
	labeling_singleton(X, Val, indomain_up).
indomain(down, X, R, BB, BB1) :-
	reverse(R, [[A|B]|R1]),
	indomain_rev(R1, A, B, Val, BB, BB1),
	labeling_singleton(X, Val, indomain_down).

% the first clause is redundant, but avoids leaving choicepoint behind
indomain([], A, A, V, BB, BB1) :- !,
	BB1 = BB,
	V = A.
indomain(_, A, _, A, BB, BB).
indomain(R, A, B, V, BB, BB1) :-
	A < B, !,
	A1 is A+1,
	indomain_later(R, A1, B, V, BB, BB1).
indomain([[A|B]|R], _, _, V, BB, BB1) :-
	indomain_later(R, A, B, V, BB, BB1).

% the first clause is redundant, but avoids leaving choicepoint behind
indomain_rev([], B, B, V, BB, BB1) :- !,
	BB1 = BB,
	V = B.
indomain_rev(_, _, B, B, BB, BB).
indomain_rev(R, A, B, V, BB, BB1) :-
	A < B, !,
	B1 is B-1,
	indomain_rev_later(R, A, B1, V, BB, BB1).
indomain_rev([[A|B]|R], _, _, V, BB, BB1) :-
	indomain_rev_later(R, A, B, V, BB, BB1).

% the first clause is redundant, but avoids leaving choicepoint behind
indomain_later([], A, A, V, BB, BB1) :- !,
	later_bound(BB, BB1),
	V = A.
indomain_later(_, A, _, V, BB, BB1) :-
	later_bound(BB, BB1),
	V = A.
indomain_later(R, A, B, V, BB, BB1) :-
	A < B, !,
	A1 is A+1,
	indomain_later(R, A1, B, V, BB, BB1).
indomain_later([[A|B]|R], _, _, V, BB, BB1) :-
	indomain_later(R, A, B, V, BB, BB1).

% the first clause is redundant, but avoids leaving choicepoint behind
indomain_rev_later([], B, B, V, BB, BB1) :- !,
	later_bound(BB, BB1),
	V = B.
indomain_rev_later(_, _, B, V, BB, BB1) :-
	later_bound(BB, BB1),
	V = B.
indomain_rev_later(R, A, B, V, BB, BB1) :-
	A < B, !,
	B1 is B-1,
	indomain_rev_later(R, A, B1, V, BB, BB1).
indomain_rev_later([[A|B]|R], _, _, V, BB, BB1) :-
	indomain_rev_later(R, A, B, V, BB, BB1).

%%% The bounding rule if branch-and-bound search.

first_bound(BB, BB).

%% [MC] 3.8.6: made determinate
later_bound(nobb(DU0), nobb(DU)) :-
	DU0>0, DU is DU0-1.
later_bound(bb(minimize(Value),Solns,Ref,Goal,DU0),
	    bb(minimize(Value),Solns,Ref,Goal,DU)) :- !,
	DU0>0, DU is DU0-1,
	'$fd_incumbent_bound'(Ref, Bound),
	Min is Bound-1,
	'$fd_in_interval'(Value, inf, Min, 1),
	'$fd_evaluate_indexical'(RC, Global),
	evaluate(RC, Global).
later_bound(bb(maximize(Value),Solns,Ref,Goal,DU0),
	    bb(maximize(Value),Solns,Ref,Goal,DU)) :-
	DU0>0, DU is DU0-1,
	'$fd_incumbent_bound'(Ref, Bound),
	Max is Bound+1,
	'$fd_in_interval'(Value, Max, sup, 1),
	'$fd_evaluate_indexical'(RC, Global),
	evaluate(RC, Global).


domain_set(dom(X,_,_,_), X).

domain_min(dom(_,X,_,_), X).

domain_max(dom(_,_,X,_), X).

domain_min_max(dom(_,X,Y,_), X, Y).

domain_size(dom(_,_,_,X), X).

fd_min(X, Min) :-
	var(X), !,
	(   get_fd_domain(X, Dom)
	->  domain_min(Dom, Min)
	;   Min = inf
	).
fd_min(X, Min) :-
	integer(X), !, Min = X.
fd_min(X, Min) :-
	fd_argument_error(fd_min(X,Min), 1, X).

fd_max(X, Max) :-
	var(X), !,
	(   get_fd_domain(X, Dom)
	->  domain_max(Dom, Max)
	;   Max = sup
	).
fd_max(X, Max) :-
	integer(X), !, Max = X.
fd_max(X, Max) :-
	fd_argument_error(fd_max(X,Max), 1, X).

fd_min_max(X, Min, Max) :-
	var(X), !,
	(   get_fd_domain(X, Dom)
	->  domain_min_max(Dom, Min, Max)
	;   Min = inf,
	    Max = sup
	).
fd_min_max(X, X, X).

fd_size(X, Size) :-
	var(X), !,
	(   get_fd_domain(X, Dom)
	->  domain_size(Dom, Size)
	;   Size = sup
	).
fd_size(X, Size) :-
	integer(X), !, Size = 1.
fd_size(X, Size) :-
	fd_argument_error(fd_size(X,Size), 1, X).

fd_degree(X, Degree) :-
	var(X), !,
	(   get_fd_suspensions(X, Lists)
	->  arg(1, Lists, Degree)
	;   Degree = 0
	).
fd_degree(X, Degree) :-
	integer(X), !, Degree = 0.
fd_degree(X, Degree) :-
	fd_argument_error(fd_degree(X,Degree), 1, X).

fd_set(X, Copy) :-
	var(X), !,
	(   get_fd_domain(X, Dom)
	->  domain_set(Dom, Set),
	    '$fd_dom_union'([], Set, Copy) % [MC] SPRM 10707 avoid structure sharing
	;   Copy = [[inf|sup]]
	).
fd_set(X, Set) :-
	integer(X),
	\+prolog:'$large_data'(0, X, _), !,
	Set = [[X|X]].
fd_set(X, Set) :-
	fd_argument_error(fd_set(X,Set), 1, X).

fd_dom(X, R) :-
	var(X), !,
	(   get_fd_domain(X, Dom)
	->  domain_set(Dom, Set),
	    fdset_to_range(Set, R)
	;   R = (inf..sup)
	).
fd_dom(X, R) :-
	integer(X),
	\+prolog:'$large_data'(0, X, _), !,
	R = {X}.
fd_dom(X, Dom) :-
	fd_argument_error(fd_dom(X,Dom), 1, X).

delete(leftmost, [X|L], X, L).
delete(min, LL, X1, LL) :-
	'$fd_delete'(LL, X1, min).
delete(max, LL, X1, LL) :-
	'$fd_delete'(LL, X1, max).
delete(ff, LL, X1, LL) :-
	'$fd_delete'(LL, X1, ff).
delete(ffc, LL, X1, LL) :-
	'$fd_delete'(LL, X1, ffc).
delete(anti_first_fail, LL, X1, LL) :- % 4.3
	'$fd_delete'(LL, X1, anti_first_fail).
delete(occurrence, LL, X1, LL) :- % 4.3
	'$fd_delete'(LL, X1, occurrence).
delete(max_regret, LL, X1, LL) :- % 4.3
	'$fd_delete'(LL, X1, max_regret).
delete(variable(Sel), LL, X1, L1) :-
	call(Sel, LL, X1, L1).

minimize(Goal, Var) :-
	minimize(Goal, Var, []).

minimize(Goal, Var, Options) :-
	ErrGoal = minimize(Goal, Var, Options),
	must_be(Options, proper_list, ErrGoal, 3),
	minimize_options(Options, best, AllSolutions, ErrGoal),
	minimax(Goal, Var, #<, AllSolutions).

maximize(Goal, Var) :-
	maximize(Goal, Var, []).

maximize(Goal, Var, Options) :-
	ErrGoal = maximize(Goal, Var, Options),
	must_be(Options, proper_list, ErrGoal, 3),
	maximize_options(Options, best, AllSolutions, ErrGoal),
	minimax(Goal, Var, #>, AllSolutions).

minimize_options([], All, All, _).
minimize_options([X|L], _, All, ErrGoal) :-
	(   nonvar(X)
        ->  must_be(X, oneof([all,best]), ErrGoal, 3)
	;   illarg(domain(term,minimize_option), ErrGoal, 3)
	),
	minimize_options(L, X, All, ErrGoal).

maximize_options([], All, All, _).
maximize_options([X|L], _, All, ErrGoal) :-
	(   nonvar(X)
        ->  must_be(X, oneof([all,best]), ErrGoal, 3)
	;   illarg(domain(term,maximize_option), ErrGoal, 3)
	),
	maximize_options(L, X, All, ErrGoal).

minimax(Goal, Var, Cmp, All) :-
	findall(Goal-Var, (Goal -> true), [Best1-Bound1]),
	minimax(Goal, Var, Cmp, All, Best1, Bound1).

minimax(Goal, Var, Cmp, _, _, Bound) :- var(Bound), !,
	(   Cmp = (#<) -> ErrGoal = minimize(Goal,Var)
	;   ErrGoal = maximize(Goal,Var)
	),
	illarg(var, ErrGoal, 2).
minimax(Goal, Var, _, all, Best, Bound) :-
	fd_unify(Goal-Var, Best-Bound).
minimax(Goal, Var, Cmp, All, _, Bound) :-
	call(Cmp, Var, Bound),
	findall(Goal-Var, (Goal -> true), [Best1-Bound1]), !,
	minimax(Goal, Var, Cmp, All, Best1, Bound1).
minimax(Goal, Var, _, best, Best, Bound) :-
	fd_unify(Goal-Var, Best-Bound).

%%% new as of 4.3:

:- meta_predicate
	solve(:, :),
        % 3rd arg must not be meta -- screws up Zinc tests
	solve(:, :, ?).

labeling(Options, Variables) :-
	ErrGoal = labeling(Options,Variables),
	solve(Options, labeling([],Variables), ErrGoal).

solve(Options, Labelings) :-
	ErrGoal = solve(Options, Labelings),
	solve(Options, Labelings, ErrGoal).

solve(Options0, Labelings0, ErrGoal) :-
	prolog:get_module_meta_arg(Options0, Options1, Module),
	ensure_list(Options1, Options, ErrGoal, 1),
	must_be(Options, proper_list, ErrGoal, 1),
	labeling_options(Options, opt(search(satisfy,best,bab),leftmost,step(up),_  ,33554431,any    ,Module),
				  opt(Solver                  ,DefSel  ,DefEnum ,Ass,DefDiscs,TimeOut,_),
				  ErrGoal),
	prolog:get_module_meta_arg(Labelings0, Labelings1, _),
	ensure_list(Labelings1, Labelings, ErrGoal, 2),
	must_be(Labelings, proper_list(callable), ErrGoal, 2),
	(   foreach(Search,Labelings),
	    foreach(Sel,Sels),
	    foreach(Enum,Enums),
	    foreach(Disc,Discs),
	    foreach(LVars,LVarss),
	    param(Module,Solver,DefSel,DefEnum,DefDiscs,ErrGoal)
	do  (   Search = labeling(LOpt0,LVars)
	    ->  must_be(LOpt0, proper_list, ErrGoal, 2),
		must_be_list_of_finite_dvar(LVars, ErrGoal, 2),
		labeling_options(LOpt0,
				 opt(Solver ,DefSel,DefEnum,_ ,DefDiscs,any     ,Module),
				 opt(_Solver,Sel   ,Enum   ,_K,Disc    ,_TimeOut,_),
				 ErrGoal)
	    ;   Search = indomain(X)
	    ->  Sel = leftmost,
		Enum = step(up),
		Disc = 33554431,
		LVars = [X]
	    )
	),
	solve(Solver, TimeOut, Ass, Discs, Sels, Enums, LVarss, ErrGoal).

solve(search(SatMinMax,Solns,Method), TimeOut, Ass, Discs, Sels, Enums, LVarss, ErrGoal) :-
	solve(SatMinMax, Solns, Method, TimeOut, Ass, Discs, Sels, Enums, LVarss, ErrGoal).

solve(satisfy, _, _, TimeOut, Ass, Discs, Sels, Enums, LVarss, _) :-
	solve_satisfy(TimeOut, Ass, Discs, Sels, Enums, LVarss).
solve(minimize(X), Solns, Method, TimeOut, Ass, Discs, Sels, Enums, LVarss, ErrGoal) :-
	arg_attribute(X, _, ErrGoal, 1),
	append(LVarss, Variables),
	(   foreach(_,Variables),
	    foreach(0,Zeros)
	do  true
	),
	prolog_flag(max_tagged_integer, Maxint),
	asserta(incumbent(Maxint,Zeros), Ref),
	call_cleanup(solve_optimize(Method, TimeOut,
				    param(Variables, Ass, Discs, Sels, Enums, LVarss,
					  minimize(X), Solns, Ref, ErrGoal)),
	             erase(Ref)).
solve(maximize(X), Solns, Method, TimeOut, Ass, Discs, Sels, Enums, LVarss, ErrGoal) :-
	arg_attribute(X, _, ErrGoal, 1),
	append(LVarss, Variables),
	(   foreach(_,Variables),
	    foreach(0,Zeros)
	do  true
	),
	prolog_flag(min_tagged_integer, Minint),
	asserta(incumbent(Minint,Zeros), Ref),
	call_cleanup(solve_optimize(Method, TimeOut,
				    param(Variables, Ass, Discs, Sels, Enums, LVarss,
					  maximize(X), Solns, Ref, ErrGoal)),
	             erase(Ref)).

solve_satisfy(any, Ass, Discs, Sels, Enums, LVarss) :-
	solve_satisfy(Ass, Discs, Sels, Enums, LVarss).
solve_satisfy(time_out(Time,Flag), Ass, Discs, Sels, Enums, LVarss) :-
	time_out(clpfd:solve_satisfy(Ass, Discs, Sels, Enums, LVarss), Time, Flag).

solve_satisfy(Ass, Discs, Sels, Enums, LVarss) :-
	'$fd_debug'(FDBG, FDBG, 1),
	(   foreach(Disc,Discs),
	    foreach(Sel,Sels),
	    foreach(Enum,Enums),
	    foreach(LVars,LVarss),
	    fromto(0,A1,A2,Ass),
	    param(FDBG)
	do  nobb_labeling_top(LVars, param(Sel,Enum,FDBG), A1, A2, nobb(Disc))
	).

solve_optimize(bab, TimeOut, Param) :-
	Param = param(_,_,_,_,_,_,_,Solns, _,_),
	(   solve_bab(TimeOut, Param, TOFlag),
	    (   TOFlag = time_out
	    ->  !, solve_bab_time_out(Solns, Param)
	    ;   Solns = all
	    )
	;   Solns = best,
	    solve_success(TimeOut),		% [MC] SPRM 14125
	    solve_bab_done(Param)
	).
solve_optimize(restart, TimeOut, Param) :-
	solve_restart(TimeOut, Param).

solve_bab_time_out(all, _).
solve_bab_time_out(best, Param) :-
	solve_bab_done(Param).

solve_success(any).
solve_success(time_out(_,success)).

solve_bab(any, Param, success) :-
	solve_bab(Param).
solve_bab(time_out(Time,Flag), Param, Flag) :-
	time_out(clpfd:solve_bab(Param), Time, Flag).

solve_bab_done(param(VariablesV, _, _, _, _, _, minimize(ValueV), best, Ref, _)) :- !,
	clause(incumbent(Value,Variables), true, Ref),
	prolog_flag(max_tagged_integer, Maxint),
	Value < Maxint,
	fd_unify([ValueV|VariablesV], [Value|Variables]).
solve_bab_done(param(VariablesV, _, _, _, _, _, maximize(ValueV), best, Ref, _)) :- !,
	clause(incumbent(Value,Variables), true, Ref),
	prolog_flag(min_tagged_integer, Minint),
	Value > Minint,
	fd_unify([ValueV|VariablesV], [Value|Variables]).

solve_bab(param(Variables, Ass, Discs, Sels, Enums, LVarss, MinMax, Solns, Ref, Goal)) :-
	arg(1, MinMax, Value),
	'$fd_debug'(FDBG, FDBG, 1),
	(   foreach(Disc,Discs),
	    foreach(Sel,Sels),
	    foreach(Enum,Enums),
	    foreach(LVars,LVarss),
	    fromto(0,A1,A2,Ass),
	    param(FDBG,MinMax,Solns,Ref,Goal)
	do  BB5 = bb(MinMax,Solns,Ref,Goal,Disc),
	    nobb_labeling(LVars, param(Sel,Enum,FDBG), A1, A2, BB5)
	),
	(   var(Value) -> illarg(var, Goal, 1)
        ;   '$fd_update_incumbent'(Ref, Value, Variables)
        ),
	Solns = all.

solve_restart(any, Param) :-
	solve_restart(Param).
solve_restart(time_out(Time,Flag), Param) :-
	time_out(clpfd:solve_restart(Param), Time, Flag).

solve_restart(Param) :-
	Param = param(Variables, _,_,_,_,_, MinMax, _, _, _),
	findall(Variables-MinMax, solve_restart_step(Param), [Best1-Bound1]),
	solve_restart(Param, Best1, Bound1).

solve_restart(Param, Best, Bound) :-
	Param = param(Variables, _,_,_,_,_, MinMax, all, _, _),
	fd_unify(Variables-MinMax, Best-Bound).
solve_restart(Param, _, minimize(X)) :-
	Param = param(Variables, _,_,_,_,_, minimize(Var), _, _, _),
	Var #< X,
	findall(Variables-minimize(Var), solve_restart_step(Param), [Best1-Bound1]), !,
	solve_restart(Param, Best1, Bound1).
solve_restart(Param, _, maximize(X)) :-
	Param = param(Variables, _,_,_,_,_, maximize(Var), _, _, _),
	Var #> X,
	findall(Variables-maximize(Var), solve_restart_step(Param), [Best1-Bound1]), !,
	solve_restart(Param, Best1, Bound1).
solve_restart(Param, Best, Bound) :-
	Param = param(Variables, _,_,_,_,_, MinMax, best, _, _),
	fd_unify(Variables-MinMax, Best-Bound).

solve_restart_step(param(_, Ass, Discs, Sels, Enums, LVarss, MinMax, _,_, Goal)) :-
	arg(1, MinMax, Value),
	'$fd_debug'(FDBG, FDBG, 1),
	(   foreach(Disc,Discs),
	    foreach(Sel,Sels),
	    foreach(Enum,Enums),
	    foreach(LVars,LVarss),
	    fromto(0,A1,A2,Ass),
	    param(FDBG)
	do  nobb_labeling(LVars, param(Sel,Enum,FDBG), A1, A2, nobb(Disc))
	), !,
	(var(Value) -> illarg(var, Goal, 1) ; true).
	

ensure_list(X, [X], _, _) :-
	nonvar(X),
	X \== [],
	\+ (X = [_|_]), !.
ensure_list(L, L, ErrGoal, ArgNo) :-
	must_be(L, proper_list, ErrGoal, ArgNo).

end_of_file.

/* To be replaced by something more general. */

%   order_resource(+Options, +Resource) 
%   where Options is a list of key words specifying a heuristic,
%   Resource represents a resource as returned by the resource(-Resource)
%   option to cumulative/3 on which tasks must be serialized.  True if
%   a total ordering can be imposed on the task; enumerating all such
%   orderings via backtracking.
%   [first,est] (the default) and [last,lct] can be good heuristics.

order_resource(Options, Resource) :-
	must_be(Options, proper_list(callable), order_resource(Options,Resource), 1),
	order_options(Options, opt(first,est), opt(Edge,Key)),
	(   foreach(Option,Options),
	    fromto(opt(first,est),Opt0,Opt,opt(Edge,Key))
	do  order_option(Option, Opt0, Opt)
	),
	order_resource(Edge, Key, Resource).

order_option(first, opt(_,Key), opt(first,Key)).
order_option(last, opt(_,Key), opt(last,Key)).
order_option(est, opt(Edge,_), opt(Edge,est)).
order_option(lst, opt(Edge,_), opt(Edge,lst)).
order_option(ect, opt(Edge,_), opt(Edge,ect)).
order_option(lct, opt(Edge,_), opt(Edge,lct)).


order_resource(first, Key, resource(Tasks,Matrix)) :-
	order_resource_first(Tasks, Matrix, Key).
order_resource(last, Key, resource(Tasks,Matrix)) :-
	order_resource_last(Tasks, Matrix, Key).

tasks_slack(Tasks, Slack) :-
	prolog_flag(min_tagged_integer, Minint),
	prolog_flag(max_tagged_integer, Maxint),
	tasks_slack(Tasks, 0, Dur, Maxint, EST, Minint, LCT),
	Slack is LCT-EST-Dur.

% useful in JSP heuristics
tasks_slack([], Dur, Dur, EST, EST, LCT, LCT).
tasks_slack([T|Tasks], Dur0, Dur, EST0, EST, LCT0, LCT) :-
	task(dur, T, Dur1),
	task(est, T, EST1),
	task(lct, T, LCT1),
	Dur2 is Dur0+Dur1,
	EST2 is min(EST0,EST1),
	LCT2 is max(LCT0,LCT1),
	tasks_slack(Tasks, Dur2, Dur, EST2, EST, LCT2, LCT).

tasks_duration([], Dur, Dur).
tasks_duration([T|Tasks], Dur0, Dur) :-
	task(dur, T, Dur1),
	Dur2 is Dur0 + Dur1,
	tasks_duration(Tasks, Dur2, Dur).

% Val1 (Val2) is the smallest (next smallest) Key value of Tasks
tasks_smallest([], _, Val1, Val1, Val2, Val2).
tasks_smallest([T|Tasks], Key, Val1a, Val1, Val2a, Val2) :-
	task(Key, T, New),
	Val1b is min(Val1a,New),
	Val2b is max(Val1a,min(Val2a,New)),
	tasks_smallest(Tasks, Key, Val1b, Val1, Val2b, Val2).

% Val1 (Val2) is the greatest (next greatest) Key value of Tasks
tasks_greatest([], _, Val1, Val1, Val2, Val2).
tasks_greatest([T|Tasks], Key, Val1a, Val1, Val2a, Val2) :-
	task(Key, T, New),
	Val1b is max(Val1a,New),
	Val2b is min(Val1a,max(Val2a,New)),
	tasks_greatest(Tasks, Key, Val1b, Val1, Val2b, Val2).

% {t in Tasks | total duration =< LCT(Tasks)-EST(t)} can be first in Tasks
order_resource_first([], _Mat, _Key) :- !.
order_resource_first([_], _Mat, _Key) :- !.
order_resource_first(Tasks, Mat, Key) :-
	can_be_first(Tasks, [], Key, First0),
	keysort(First0, First),
	order_resource_first(Tasks, First, Mat, Key).

% {t in Tasks | total duration =< LCT(t)-EST(Tasks)} can be last in Tasks
order_resource_last([], _Mat, _Key) :- !.
order_resource_last([_], _Mat, _Key) :- !.
order_resource_last(Tasks, Mat, Key) :-
	can_be_last(Tasks, [], Key, Last0),
	keysort(Last0, Last),
	order_resource_last(Tasks, Last, Mat, Key).

order_resource_first(Tasks, [_-F|_], Mat, Key) :-
	task(id, F, I),
	task(dur, F, D),
	'$fd_begin',
	tell_after(Tasks, I, D, Mat, Rest),
	'$fd_evaluate_indexical'(RC, Global),
	evaluate(RC, Global),
	order_resource_first(Rest, Mat, Key).
order_resource_first(Tasks, [_|First], Mat, Key) :-
	order_resource_first(Tasks, First, Mat, Key).

order_resource_last(Tasks, [_-F|_], Mat, Key) :-
	task(id, F, I),
	'$fd_begin',
	tell_before(Tasks, I, Mat, Rest),
	'$fd_evaluate_indexical'(RC, Global),
	evaluate(RC, Global),
	order_resource_last(Rest, Mat, Key).
order_resource_last(Tasks, [_|Last], Mat, Key) :-
	order_resource_last(Tasks, Last, Mat, Key).

can_be_first(Tasks, Order, Key, First0) :-
	tasks_duration(Tasks, 0, Dur),
	prolog_flag(min_tagged_integer, Minint),
	prolog_flag(max_tagged_integer, Maxint),
	tasks_smallest(Tasks, lst, Maxint, LST1, Maxint, LST2),
	tasks_greatest(Tasks, lct, Minint, LCT1, Minint, LCT2),
	can_be_first(Tasks, Order, Dur, LCT1, LCT2, LST1, LST2, Key, First0).

can_be_first([], _, _, _, _, _, _, _, []).
can_be_first([T|Rest], Order, Dur, LCT1, LCT2, LST1, LST2, Key, [Rank-T|First]) :-
	task(all, T, _Id, EST, LST, ECT, LCT),
	(   LCT=:=LCT1 -> LCT2-EST >= Dur; LCT1-EST >= Dur   ),
	(   LST=:=LST1 -> ECT =< LST2; ECT =< LST1   ), !,
	task(Key, T, Rank),
	can_be_first(Rest, Order, Dur, LCT1, LCT2, LST1, LST2, Key, First).
can_be_first([_|Rest], Order, Dur, LCT1, LCT2, LST1, LST2, Key, First) :-
	can_be_first(Rest, Order, Dur, LCT1, LCT2, LST1, LST2, Key, First).

can_be_last(Tasks, Order, Key, Last0) :-
	tasks_duration(Tasks, 0, Dur),
	prolog_flag(min_tagged_integer, Minint),
	prolog_flag(max_tagged_integer, Maxint),
	tasks_greatest(Tasks, ect, Minint, ECT1, Minint, ECT2),
	tasks_smallest(Tasks, est, Maxint, EST1, Maxint, EST2),
	can_be_last(Tasks, Order, Dur, EST1, EST2, ECT1, ECT2, Key, Last0).

can_be_last([], _, _, _, _, _, _, _, []).
can_be_last([T|Rest], Order, Dur, EST1, EST2, ECT1, ECT2, Key, [Rank-T|Last]) :-
	task(all, T, _Id, EST, LST, ECT, LCT),
	(   EST=:=EST1 -> LCT-EST2 >= Dur; LCT-EST1 >= Dur   ),
	(   ECT=:=ECT1 -> LST >= ECT2; LST >= ECT1   ), !,
	task(Key, T, Rank0), Rank is -Rank0,
	can_be_last(Rest, Order, Dur, EST1, EST2, ECT1, ECT2, Key, Last).
can_be_last([_|Rest], Order, Dur, EST1, EST2, ECT1, ECT2, Key, Last) :-
	can_be_last(Rest, Order, Dur, EST1, EST2, ECT1, ECT2, Key, Last).

% Enforce: all tasks after I
tell_after([], _I, _D, _Mat, []).
tell_after([Tj|Tasks], I, D, Mat, [Tj|Rest]) :-
	task(id, Tj, J),
	I =\= J, !,
	dvar_gtz(I, D, J, Mat),	% tell Dji #>= D
	tell_after(Tasks, I, D, Mat, Rest).
tell_after([_|Tasks], I, D, Mat, Rest) :-
	tell_after(Tasks, I, D, Mat, Rest).

% Enforce: all tasks before I
tell_before([], _I, _Mat, []).
tell_before([Tj|Tasks], I, Mat, [Tj|Rest]) :-
	task(id, Tj, J),
	I =\= J, !,
	task(dur, Tj, D),
	dvar_gtz(J, D, I, Mat),	% tell Dij #>= D
	tell_before(Tasks, I, Mat, Rest).
tell_before([_|Tasks], I, Mat, Rest) :-
	tell_before(Tasks, I, Mat, Rest).

dvar_gtz(I, D, J, Diffs) :-
	avl_fetch(J-I, Diffs, Dji), !,
	'$fd_in_interval'(Dji, D, sup, 0).
dvar_gtz(I, D, J, Diffs) :-
	avl_fetch(I-J, Diffs, Dij), !,
	MD is -D,
	'$fd_in_interval'(Dij, inf, MD, 0).

task(id, task(_,_,_,_,I), I).
task(dur, task(_,_-DurM,_,_,_), Dur) :-
	get_mutable_rec(DurD, DurM),
	DurD = dom(_,Dur,_,_).
task(est, task(_-SM,_,_,_,_), Est) :-
	get_mutable_rec(SD, SM),
	SD = dom(_,Est,_,_).
task(lst, task(_-SM,_,_,_,_), Lst) :-
	get_mutable_rec(SD, SM),
	SD = dom(_,_,Lst,_).
task(ect, task(_,_,_-EM,_,_), Ect) :-
	get_mutable_rec(ED, EM),
	ED = dom(_,Ect,_,_).
task(lct, task(_,_,_-EM,_,_), Lct) :-
	get_mutable_rec(ED, EM),
	ED = dom(_,_,Lct,_).
task(all, task(_-SM,_,_-EM,_,Id), Id, Est, Lst, Ect, Lct) :-
	get_mutable_rec(SD, SM),
	SD = dom(_,Est,Lst,_),
	get_mutable_rec(ED, EM),
	ED = dom(_,Ect,Lct,_).

get_mutable_rec(Val, v(_,_,Mut,_)) :- !,
	get_mutable(Mid, Mut),
	get_mutable_rec(Val, Mid).
get_mutable_rec(Val, Val).
