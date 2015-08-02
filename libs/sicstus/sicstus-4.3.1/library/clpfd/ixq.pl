/* Copyright(C) 1997, Swedish Institute of Computer Science */
:- public
        solve_fd/2,
        overflow_action/2,
        'x=y'/3,
        'x\\=y'/3,
        'x=<y'/3,
        iff_aux/2,
        iff_aux/3,
        silent_prune_and_propagate/2,
        propagate_interval_chk/3,
        propagate_value/1.

:- meta_predicate
	fd_batch(:),
	fd_global(:,*,*),
	fd_global(:,*,*,*).

:- attribute fd_attribute/3.
%% index, domain mutable, suspensions mutable.

purify_var(X) :-
	nonvar(X).
purify_var(X) :-
	var(X),
	put_atts(X, -fd_attribute(_,_,_)).

fd_var(X) :-
	var(X),
	is_fd_variable(X).

is_fd_variable(X) :-
	get_atts(X, fd_attribute(_,_,_)).

get_fd_domain(X, Dom) :-
	get_atts(X, fd_attribute(_,DomM,_)),
	get_mutable(Dom, DomM).

get_fd_suspensions(X, Lists) :-
	get_atts(X, fd_attribute(_,_,ListsM)),
	get_mutable(Lists, ListsM).

solve_fd(Constraint, DefPtr) :-
	check_arguments(Constraint, Attv),
	iff_aux(DefPtr, Constraint, Attv, 1).

%%% Reified constraints, accelerated in 4.0.5

'x=y'(X, Y, B) :-
	integer(X),
	integer(Y),
	X=:=Y, !,
	B = 1.
'x=y'(X, Y, B) :-
	fd_minmax(X, Xmin, Xmax),
	fd_minmax(Y, Ymin, Ymax),
	(   Xmax < Ymin -> B = 0
	;   Xmin > Ymax -> B = 0
	), !.
'x=y'(X, Y, B) :-
	iff_aux('t=u IND'(X,Y), clpfd, B).

'x\\=y'(X, Y, B) :-
	integer(X),
	integer(Y),
	X=:=Y, !,
	B = 0.
'x\\=y'(X, Y, B) :-
	fd_minmax(X, Xmin, Xmax),
	fd_minmax(Y, Ymin, Ymax),
	(   Xmax < Ymin -> B = 1
	;   Xmin > Ymax -> B = 1
	), !.
'x\\=y'(X, Y, B) :-
	iff_aux('x\\=y IND'(X,Y), clpfd, B).

'x=<y'(X, Y, B) :- 
	fd_minmax(X, Xmin, Xmax),
	fd_minmax(Y, Ymin, Ymax),
	(   Xmax =< Ymin -> B = 1
	;   Xmin  > Ymax -> B = 0
	), !.
'x=<y'(X, Y, B) :- 
	iff_aux('x=<y IND'(X,Y), clpfd, B).

% accellerated version for finite bounds, merely fail if error
fd_minmax(X, Min, Max) :-
	var(X), !,
	get_fd_domain(X, Dom),
	Dom = dom(_,Min,Max,_),
	integer(Min),
	integer(Max).
fd_minmax(X, Min, Max) :-
	integer(X),
	Min = X,
	Max = X.

iff_aux(Constraint0, B) :-
	prolog:get_module_meta(Constraint0, Constraint, Module),
	iff_aux(Constraint, Module, B).

iff_aux('x=y'(X,Y), clpfd, B) :- !, % obsolescent
	'x=y'(X, Y, B).
iff_aux('x\\=y'(X,Y), clpfd, B) :- !, % obsolescent
	'x\\=y'(X, Y, B).
iff_aux('x=<y'(X,Y), clpfd, B) :- !, % obsolescent
	'x=<y'(X, Y, B).
iff_aux(X in Expr, _, B) :- !,
        fd_goal_expansion(X in Expr #<=> B, clpfd, Goal),
	call(Goal).
iff_aux(Constraint, _, B) :-
	fd_expandable(Constraint, _, _, _), !,
	fd_goal_expansion(Constraint #<=> B, clpfd, Goal),
	call(Goal).
iff_aux(Constraint, Module, B) :-
	'$fd_find_definition'(Constraint, Module, DefPtr),
	(   DefPtr =\= 0
	->  check_arguments(Constraint, Attv),
	    iff_aux(DefPtr, Constraint, Attv, B)
	;   functor(Constraint, Name, Arity),
	    illarg(existence(constraint,Module:Name/Arity,0), Constraint, 0)
	).

iff_aux(Def, Constraint, Attv, B) :-
	'$fd_in_interval'(B, 0, 1, 1), !,
	arg_attribute(B, Ba, Constraint, 1),
	'$fd_post_reified'(Def, Constraint, Attv, B, Ba),
	% print_message(informational, post_indexical(Constraint,B)), % MC trace
	'$fd_evaluate_indexical'(RC, Global),
	evaluate(RC, Global).
iff_aux(_, Constraint, _, B) :- % raise error, or fail
	arg_attribute(B, _, Constraint#<=>B, 2),
	fail.

check_arguments(Constraint, Attv) :-
	'$fd_check_arguments'(Constraint, Attv), !.
check_arguments(Constraint, _) :-
	functor(Constraint, _, A),
	check_arguments_error(A, Constraint).

check_arguments_error(0, _) :- !.
check_arguments_error(A, Constraint) :-
	arg(A, Constraint, Arg),
	(   var(Arg)
	;   integer(Arg),
	    \+prolog:'$large_data'(0, Arg, _)
	), !,
	B is A-1,
	check_arguments_error(B, Constraint).
check_arguments_error(A, Constraint) :-
	arg(A, Constraint, Arg),
	fd_argument_error(Constraint, A, Arg).

fd_argument_error(Constraint, A, Arg) :-
	integer(Arg), !,
	'$fd_overflow'(error, error, 1),
	(Arg<0 -> What=min_clpfd_integer ; What=max_clpfd_integer),
	illarg(representation(What), Constraint, A, Arg).
fd_argument_error(Constraint, A, Arg) :-
	illarg(type(integer), Constraint, A, Arg).

overflow_action(Constraint, Code) :- % [MC] SPRM 13682
	Fake is 2*Code-3,
	fd_argument_error(Constraint, 0, Fake).

/* now unfolded
evaluate :-
	'$fd_evaluate_indexical'(RC, Global),
	evaluate(RC, Global).
*/
%% evaluate(RC, Global)
%% RC = -1 -- failure
%% RC = 0  -- done
%% RC = 1  -- indexicals to be run
%% RC = 2  -- Global to be run
evaluate(0, _).
evaluate(1, _) :-
	'$fd_evaluate_indexical'(RC, Global),
	evaluate(RC, Global).
evaluate(2, Propagator) :-
	dispatch_prune_and_enqueue(Propagator).

% FDBG puts advice on this!
prune_and_propagate(Pruned, Set) :-
	'$fd_in_set'(Pruned, Set, 1),
	'$fd_evaluate_indexical'(RC, Global),
	evaluate(RC, Global).

silent_prune_and_propagate(Pruned, Set) :-
	'$fd_in_set'(Pruned, Set, 1),
	'$fd_evaluate_indexical'(RC, Global),
	evaluate(RC, Global).

% FDBG puts advice on this!
prune_and_propagate_chk(Pruned, Set) :-
	'$fd_in_set'(Pruned, Set, 1), !, % SPRM 12637
	'$fd_evaluate_indexical'(RC, Global),
	evaluate(RC, Global).
prune_and_propagate_chk(Pruned, Set) :-
	arg_attribute(Pruned, _, in_set(Pruned,Set), 1), !,
	fail.
prune_and_propagate_chk(_, _).

% FDBG puts advice on this!
propagate_interval(Pruned, Min, Max) :-
	'$fd_in_interval'(Pruned, Min, Max, 1),
	'$fd_evaluate_indexical'(RC, Global),
	evaluate(RC, Global).

% FDBG puts advice on this!
propagate_interval_chk(Pruned, Min, Max) :-
	'$fd_in_interval'(Pruned, Min, Max, 1), !, % SPRM 12637
	'$fd_evaluate_indexical'(RC, Global),
	evaluate(RC, Global).
propagate_interval_chk(Pruned, Min, Max) :-
	arg_attribute(Pruned, _, in(Pruned,Min..Max), 1), !,
	fail.
propagate_interval_chk(_, _, _).

propagate_value(ListsM) :-
	'$fd_enqueue_all'(ListsM),
	'$fd_evaluate_indexical'(RC, Global),
	evaluate(RC, Global).

/* This is a more ambitious attempt to unify domain variables.
   It involves merging the suspension lists and intersecting the domains.
*/
% FDBG puts advice on this!
verify_attributes(Var, Other, Goals) :-
	get_atts(Var, fd_attribute(_,DomM,ListsM)), % now guaranteed to succeed
	check_arguments_error(2, _=Other),
	verify_attributes(Other, DomM, ListsM, Goals).

verify_attributes(Other, DomM, ListsM, Goals) :-
	integer(Other),
	get_mutable(Lists, ListsM),
	'$fd_set_singleton'(Other, DomM), % update_mutable(Other..Other, DomM),
	(   arg(2, Lists, 0) -> Goals = []
	;   Goals = [propagate_value(ListsM)]
	).
verify_attributes(Other, DomM, ListsM, Goals) :-
	var(Other),
	'$fd_arg_attribute'(Other, 0, OtherAttr),
	get_atts(Other, fd_attribute(_,OtherDomM,OtherListsM)),
	get_mutable(Dom, DomM),
	get_mutable(Lists, ListsM),
	get_mutable(OtherDom, OtherDomM),
	get_mutable(OtherLists, OtherListsM),
	update_mutable(OtherAttr, DomM),
	update_mutable([], ListsM),
	merge_lists(Lists, OtherLists, NewLists),
	update_mutable(NewLists, OtherListsM),
	arg(1, Dom, Set),
	arg(1, OtherDom, OtherSet),
	(   Set==OtherSet -> Goals = []
	;   '$fd_dom_union'(Set, OtherSet, Union),
	    '$fd_dom_intersection'(Set, OtherSet, Intersection),
	    Intersection \== [],
	    fdset_min(Union, Min),
	    fdset_max(Union, Max),
	    fdset_size(Union, Size),
	    dom_term(UDom, Union, Min, Max, Size),
	    update_mutable(UDom, OtherDomM),
	    Goals = [silent_prune_and_propagate(Other,Intersection)]
	).

fd_unify(X, Y) :-
	'$fd_unify'(X, Y, RC),
	evaluate(RC, 0).
	

dom_term(Dom, Set, Min, Max, Size) :-
	% prevent deref. chains
	Dom =.. [dom,Set,Min,Max,Size].

merge_lists(L1, L2, '$fdlists'(K,VW,C1,C2,C4,C24,C8)) :-
	L1 = '$fdlists'(_, V,A1,A2,A4,A24,A8),
	L2 = '$fdlists'(_, W,B1,B2,B4,B24,B8),
	VW is V\/W,
	fdlists_globals(L1, G1),
	fdlists_globals(L2, G2),
	ord_intersection(G1, G2, G12),
	mark_coref(G12),
	merge_sublists_ixs(A1, B1, C1, N1, Ix0, Ix1),
	merge_sublists_ixs(A2, B2, C2, N2, Ix1, Ix2),
	merge_sublists_ixs(A4, B4, C4, N4, Ix2, Ix3),
	merge_sublists_ixs(A24, B24, C24, N24, Ix3, Ix4),
	merge_sublists_ixs(A8, B8, C8, N8, Ix4, []),
	K is N1+N2+N4+N24+N8,
	keysort(Ix0, Ix5),
	(   fromto(Ix5,[IxA-StatusM|Ix6],Ix6,[]) % [MC] 4.1.3
	do  (   Ix6 = [IxB-StatusN|_],
		IxA==IxB
	    ->  get_mutable(Status0, StatusM), % STATUS FIELD
		Status1 is Status0\/4,
		update_mutable(Status1, StatusM),
		get_mutable(Status2, StatusN), % STATUS FIELD
		Status3 is Status2\/4,
		update_mutable(Status3, StatusN)
	    ;   true
	    )
	).

merge_sublists_ixs(A, B, C, N) -->
	{append(A, B, Ca)},
	{sort(Ca, C)},
	{length(C, N)},
	(   foreach(Item,C)	% [MC] 4.1.3
	do  ix_item(Item)
	).

ix_item(_-Item) --> !,
	ix_item(Item).
ix_item(iff(Item,_,_,_)) --> !,
	ix_item(Item).
ix_item(Item) -->
	{Item = ix(Ptr,Goal,StatusM,Ent,Reif,_,_)},
	{var(Ent)}, !,
	{'$fd_indexical_data'(Ptr, Code, _)},
	{ix_type(Code, Type)},
	ix_item(Type, Goal, StatusM, Reif).
ix_item(_) --> [].

ix_item(prune, Goal, StatusM, _) -->
	[Goal-StatusM].
ix_item(check, _, _, Reif) -->
	{get_fd_suspensions(Reif, Lists)},
	(   {Lists = []} -> []	% [MC] SPRM 13681 -- A #<=> A#>=B
	;   {arg(7, Lists, ValList)},
	    (   foreach(X,ValList)
	    do  ix_item(X)
	    )
	).

ix_type(0, prune).
ix_type(1, prune).
ix_type(2, check).
ix_type(3, check).

fdlists_globals('$fdlists'(_,_,A1,A2,A4,A24,A8), Set) :-
	fdlists_globals(A1, S0, S1),
	fdlists_globals(A2, S1, S2),
	fdlists_globals(A4, S2, S3),
	fdlists_globals(A24, S3, S4),
	fdlists_globals(A8, S4, []),
	sort(S0, Set).

fdlists_globals([]) --> [].
fdlists_globals([G|Gs]) --> fdlists_global(G), fdlists_globals(Gs).

fdlists_global(_-G) --> !,
	fdlists_global(G).
fdlists_global(G) -->
	{G = global(_,_,_,Ent,_), var(Ent)}, !,
	[G].
fdlists_global(daemon(Item,_,_,Ent,_)) -->
	{var(Ent)}, !,
	fdlists_global(Item).
fdlists_global(_) --> [].

mark_coref([]).
mark_coref([global(_,_,StatusM,_,_)|Gs]) :-
	get_mutable(Status, StatusM), % STATUS FIELD
	Status1 is Status\/4,
	update_mutable(Status1, StatusM),
	mark_coref(Gs).

%%% Support for global (specialized) constraints.

% Susp is a list of F(Var) terms, each representing that the constraint should be
% suspended on the variable Var.  F denotes the circumstances under which the constraint
% should be resumed:
% dom - resume when dom(Var) changes
% min - resume when min(Var) changes
% max - resume when max(Var) changes
% minmax - resume when min(Var) or max(Var) changes
% val - resume when Var becomes nonvar
fd_global(ModConstraint, State, Susp) :-
	fd_global(ModConstraint, State, Susp, []).

fd_global(ModConstraint, State, Susp, Options) :-
	Goal = fd_global(ModConstraint, State, Susp, Options),
	must_be(Options, proper_list, Goal, 4),
	fd_global_options(Options, opt(_,ModConstraint,0), Opt, Goal, 4),
	prolog:get_module_meta_arg(ModConstraint, Constraint, _Module),
	Opt = opt(Glob,Source,Base),
	fd_global_internal(Constraint, State, Susp, Glob, Source, Base).

fd_global_options([], Opt, Opt, _, _).
fd_global_options([X|L], Opt0, Opt, Goal, ArgNo) :-
	(   nonvar(X),
	    fd_global_option(X, Opt0, Opt1) -> true
        ;   illarg(domain(term,fd_global_option), Goal, ArgNo, X)
        ),
	fd_global_options(L, Opt1, Opt, Goal, ArgNo).

fd_global_option(global(Glob), opt(_,Source,Base), opt(Glob,Source,Base)).
fd_global_option(source(Source), opt(Glob,_,Base), opt(Glob,Source,Base)).
fd_global_option(idempotent(Bool), opt(Glob,Source,_), opt(Glob,Source,Base)) :-
	bool_option(Bool, B),
	Base is (1-B)<<2.

fd_global_internal(Constraint, State, Susp, Global, Source, Base) :-
	(   \+'$fd_coref'(Susp) -> Status is Base
	;   Status is Base\/4
	),
	'$fd_begin',
	'$fd_post_global'(Constraint, State, Status, Source, Susp, Global),
	% print_message(informational, post_global(Constraint)), % MC trace
	'$fd_evaluate_indexical'(RC, Global1),
	evaluate(RC, Global1).

dispatch_prune_and_enqueue(Indexical) :-
	Indexical = ix(_Ptr,Constraint,_StatusM,_Ent,_ZeroOne,_Attv,_LAttr), !,
	'$fd_eval_indexical'(Constraint, Actions, Indexical),
	enqueue_actions(Actions, Actions1),
	'$fd_prune_and_enqueue'(Actions1, Indexical), % at most one action
	'$fd_evaluate_indexical'(RC, Global1),
	evaluate(RC, Global1).
dispatch_prune_and_enqueue(Global) :-
	Global = global(StateM,Constraint,_StatusM,_Ent,_Module), !,
	get_mutable(State, StateM),
	dispatch_global_fast(Constraint, State, NewState, Actions, Global),
	update_mutable(NewState, StateM),
	enqueue_actions(Actions, Actions1),
	'$fd_prune_and_enqueue'(Actions1, Global), % at most one action
	'$fd_evaluate_indexical'(RC, Global1),
	evaluate(RC, Global1).

enqueue_actions([], []).
enqueue_actions([X=I|Actions0], Actions) :- !,
	must_be_fd_integer(I, X=I, 2),
	'$fd_range'(I, I, Set, 1),
	'$fd_in_set'(X, Set, 0),
	enqueue_actions(Actions0, Actions).
enqueue_actions([X in R|Actions0], Actions) :- !,
	set_expression_check(R, S, X in R, 2),
	'$fd_in_set'(X, S, 0),
	enqueue_actions(Actions0, Actions).
enqueue_actions([X in_set S|Actions0], Actions) :- !,
	(   '$fd_size'(S, _, 1) ->
	    '$fd_in_set'(X, S, 0)
	;   illarg(domain(term,constraint), X in_set S, 2)
	),
	enqueue_actions(Actions0, Actions).
enqueue_actions([call(Goal)|Actions0], Actions) :- !,
	call(user:Goal),	% default module, for lack of a better way
	enqueue_actions(Actions0, Actions).
enqueue_actions([A|Actions0], [A|Actions]) :-
	enqueue_actions(Actions0, Actions).

% clpfd:dispatch_global(+Constraint, +State0, -State, -Actions)
% calls a user-defined constraint solver for a particular kind of constraint.
% Constraint is the original constraint;
% State0 is a term representing aux. info about this constraint at the time of its
% latest invocation.
% State represents updated aux. info;
% Actions is a list of terms of the following form:
%	exit - the constraint has been found entailed
%	fail - the constraint has been found disentailed
%	call(G) - call the Prolog goal G
%	X = R   - call(X = R)
%	X in R   - call(X in R)
%	X in_set R  - call(X in_set R)

% FDBG puts advice on this!
dispatch_global_fast(Constraint, State, NewState, Actions, Global) :-
	'$fd_dispatch_global_fast'(Constraint, State, NewState1, Actions1, Global, RC),
	(   RC =:= 1
	->  NewState = NewState1,
	    Actions = Actions1
	;   dispatch_global(Constraint, State, NewState, Actions)
	).

:- multifile
	dispatch_global/4.

fd_batch(M:Goals) :-
	must_be(Goals, proper_list(callable), fd_batch(Goals), 1),
	'$fd_begin',
	'$fd_batch'(Old, on, 1),
	call_cleanup(post_and_eval(Goals,M,Old), '$fd_batch'(_,Old,1)).

post_and_eval(Goals, M, OnOff) :-
	(   foreach(Goal,Goals),
	    param(M)
	do  call(M:Goal)
	), !,
	'$fd_batch'(_, OnOff, 1),
	'$fd_evaluate_indexical'(RC, Global),
	evaluate(RC, Global).

/* fd_closure/2 */

% Given a list Vars of domain variables, Closure is the set
% of variables (including Vars) that can be transitively reached
% via constraints.
fd_closure(Vars, Closure) :-
	sort(Vars, Open),
	fd_closure(Open, Open, Closure).

/*** worst case O(N^2) version

fd_closure([], Closure, Closure).
fd_closure([X|Open], Closed, Closure) :-
	fd_neighbors(X, Neighs),
	ord_union(Closed, Neighs, Closed1, New),
	ord_union(Open, New, Open1),
	fd_closure(Open1, Closed1, Closure).

fd_neighbors(X, Neighs) :-
	var(X),
	clpfd:get_atts(X, fd_attribute(_,_,ListM)), !,
	get_mutable(Lists, ListM),
	Lists = '$fdlists'(_,_,L1,L2,L3,L4,L5),
	fd_neighbors(L1, S0, S1),
	fd_neighbors(L2, S1, S2),
	fd_neighbors(L3, S2, S3),
	fd_neighbors(L4, S3, S4),
	fd_neighbors(L5, S4, []),
	sort(S0, Neighs).
fd_neighbors(_, []).

fd_neighbors([]) --> [].
fd_neighbors([Item|L]) --> fd_neighbors(Item, L).

fd_neighbors(_-Item, L) --> !, fd_neighbors(Item, L).
fd_neighbors(iff(Item,_,_,_), L) --> !, fd_neighbors(Item, L).
fd_neighbors(ix(_,C,_,Ent,B,_,_), L) --> {var(Ent)}, !,
	{prolog:'$term_variables'(C, Vars)},
	list(Vars),
	(   {clpfd:'$get_attributes'(B, _, 1)} -> [B]
	;   []
	),
	fd_neighbors(L).
fd_neighbors(global(_,C,_,Ent,_), L) --> {var(Ent)}, !,
	{prolog:'$term_variables'(C, Vars)},
	list(Vars),
	fd_neighbors(L).
fd_neighbors(_, L) -->
	fd_neighbors(L).

list([]) --> [].
list([X|Xs]) --> [X], list(Xs).

***/

fd_closure([], Closure, Closure).
fd_closure([X|Open], Closed, Closure) :-
	fd_list_constraints([X|Open], Cs, []),
	prolog:term_variables_set(Cs, Neighs),
	ord_union(Closed, Neighs, Closed1, Open1),
	fd_closure(Open1, Closed1, Closure).

fd_neighbors(X, Neighs) :-
	fd_list_constraints([X], Cs, []),
	prolog:term_variables_set(Cs, Neighs).

fd_list_constraints([]) --> [].
fd_list_constraints([X|Xs]) -->
	{var(X),
	 get_atts(X, fd_attribute(_,_,ListM)), !,
	 get_mutable(Lists, ListM), % avoid '$fdlists' in source code
	 arg(3, Lists, L3),
	 arg(4, Lists, L4),
	 arg(5, Lists, L5),
	 arg(6, Lists, L6),
	 arg(7, Lists, L7)},
	fd_neighbors(L3),
	fd_neighbors(L4),
	fd_neighbors(L5),
	fd_neighbors(L6),
	fd_neighbors(L7),
	fd_list_constraints(Xs).
fd_list_constraints([_|Xs]) -->
	fd_list_constraints(Xs).

%%% similar to suspensions/3, but we cannot assume that we
%%% have all zero-one variables already

fd_neighbors([]) --> [].
fd_neighbors([Item|L]) --> 
	fd_neighbors(Item, L).

fd_neighbors(_-Item, L) --> !, 
	fd_neighbors(Item, L).
fd_neighbors(iff(Item,_,_,_), L) --> !, 
	fd_neighbors(Item, L).
fd_neighbors(ix(_,C,_,Ent,B,_,_), L) --> {var(Ent)}, !,
	[B-C],
	fd_neighbors(L).
fd_neighbors(global(_,_,_,Ent,Source), L) --> {var(Ent), Source\==true}, !,
	[Source],
	fd_neighbors(L).
fd_neighbors(daemon(Item,_,_,Ent,_), L) --> {var(Ent)}, !,
	fd_neighbors(Item, L).
fd_neighbors(_, L) -->
	fd_neighbors(L).

/* fd_flag(+Flag, ?Old, ?New)
   Flag=debug -> Old,New in [off,on]
   Flag=overflow -> Old,New in [error,fail]
*/
fd_flag(Flag, Old, New) :-
	Goal = fd_flag(Flag,Old,New),
	must_be(Flag, oneof([debug,overflow]), Goal, 1),
	fd_flag(Flag, Old, New, Goal).

fd_flag(debug, Old, New, Goal) :-
	(   New==Old -> true
	;   must_be(New, oneof([on,off]), Goal, 3)
	),
	'$fd_debug'(Old, New,1).
fd_flag(overflow, Old, New, Goal) :-
	(   New==Old -> true
	;   must_be(New, oneof([error,fail]), Goal, 3)
	),
	'$fd_overflow'(Old, New,1).

