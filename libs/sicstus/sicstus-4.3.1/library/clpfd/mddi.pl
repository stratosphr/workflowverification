% Consistency rules:
% VARS(DAG) = VARS(Template)
% VARS(Tuples) \disjoint VARS(Template)
% every ID integer and unique
% all paths complete

case(Template, Tuples, Dag) :-
	case(Template, Tuples, Dag, [], case(Template,Tuples,Dag)).

case(Template, Tuples, Dag, Options) :-
	case(Template, Tuples, Dag, Options, case(Template,Tuples,Dag,Options)).

% A "source" is in fact an interval B..E
case(Template, Tuples, Dag1, Options, Posted) :-
	(   foreach(node(ID,Var,Edges1),Dag1),
	    foreach(node(ID,Var,Edges2),Dag2)
	do  (   foreach(Edge1,Edges1),
		foreach(edge(Source,Linles,Target),Edges2)
	    do  (   Edge1 = Source-Linles-Target
		->  true
		;   Edge1 = Source-Target,
		    simple(Target)
		->  Linles = []
		;   Edge1 = Source-Linles
		->  Target = []
		;   Edge1 = Source, Source = (_.._)
		->  Linles = [],
		    Target = []
		;   Edge1 = Linles
		->  Source = [],
		    Target = []
		)
	    )
	),
	(   foreach(O,Options),
	    fromto(SCPs,SCPs1,SCPs2,[])
	do  (   O = scalar_product(_,_,_,_)
	    ->  SCPs1 = [O|SCPs2]
	    ;   SCPs1 = SCPs2
	    )
	),
	mddi(Template, Tuples, Dag2, SCPs, Posted).

mddi(Template, Tuples, Dag, SCPs, Posted) :-
	Goal = mddi(Template,Tuples,Dag,Posted),
	prolog:term_variables_dfs(Template, Vars), % prolog:term_variables_set would NOT work
	sort(Vars, TemplateVars),
	prolog:term_variables_set(Dag, DagVars),
	prolog:term_variables_set(Tuples, TuplesVars),
	(   DagVars==TemplateVars -> true
	;   illarg(consistency(Template,Dag,''), Posted, 3)
	),
	(   ord_disjoint(TuplesVars, TemplateVars) -> true
	;   illarg(consistency(Template,Tuples,''), Posted, 2)
	),
	(   mddi_compile(Dag, Vars, Nodes, Edges, VarVals, Linles2, Sets, Posted) -> true
	;   illarg(consistency(Template,Dag,inconsistent_paths), Posted, 3)
	),
	mddi_scps(SCPs, Vars, -1, Linles1, Linles2),
	sort(Linles1, Linles),	% by increasing edge index
	mddi_post(Tuples, Template, Vars, Nodes, Edges, VarVals, Linles, Sets, Goal, Posted).
	
mddi_post(Tuples1, Template, Vars, Nodes, Edges, VarVals, Linles, Sets, Goal, Posted) :-
	(   foreach(Row1,Tuples1),
	    foreach(Row3,Tuples3),
	    fromto(Susp,S0,S,[]),
	    param(Template,Vars,Sets,Goal)
	do  copy_term(Template-Vars, Row1-Row2),
	    (   foreach(X,Row2),
		foreach(X-XA,Row3),
		foreach(Set,Sets),
		fromto(S0,[none(X)|S1],S1,S),
		param(Goal)
	    do  arg_attribute(X, XA, Goal, 0),
		prune_and_propagate(X, Set)
	    )
	),
	length(Vars, NVars),
	(Linles = [] -> Flag = 0 ; Flag = 4), % idempotent or not
	fd_global_internal(Goal,
			   state(f(NVars,Nodes,Edges,VarVals,Linles,Tuples3),
				 0/*trail_top*/,_Handle,0),
			   Susp, _, clpfd:Posted, Flag).

mddi_compile(Dag, Vars, Nodes, Edges, VarVals, Linles, Sets, Goal) :-
	empty_avl(ID2Index0),
	mddi_map_nodes(Dag, Nodes, Pivots0, 0, ID2Index0, ID2Index, Vars, Goal),
	sort(Pivots0, Pivots1),
	keyclumped(Pivots1, Pivots2),
	compensate_for_inf(Pivots2, Pivots3),
	ord_list_to_avl(Pivots3, Node2P),
	mddi_map_rest(Dag, Nodes, Edges1, VarVals1, 0, _, ID2Index, Node2P, Goal),
	keysort(VarVals1, VarVals2),
	keyclumped(VarVals2, VarVals3),
	(   foreach(varval(Var,_,B,E)-Cl,VarVals3),
	    foreach(varval(Var,B,E),VarVals),
	    foreach(Var-(B..E),KL1),
	    count(Ix,0,_)
	do  (   foreach(Ix,Cl),
		param(Ix)
	    do  true
	    )
	),
	keysort(Edges1, Edges2), % sort by inc. varval
	(   foreach(_-Edge2,Edges2),
	    foreach(Edge,Edges),
	    fromto(Linles,Linles1,Linles2,[]),
	    count(EdgeIx,0,_),
	    param(Vars)
	do  Edge2 = edge(Source,SCPs,Dest,U),
	    Edge = edge(Source,Dest,U),
	    mddi_scps(SCPs, Vars, EdgeIx, Linles1, Linles2)
	),
	keysort(KL1, KL2),
	keyclumped(KL2, KL3),
	(   foreach(_-Vals,KL3),
	    foreach(Set,Sets)
	do  (   foreach(R1,Vals),
		foreach(S1,SetParts)
	    do  range_to_fdset(R1, S1)
	    ),
	    fdset_union(SetParts, Set)
	).

mddi_scps(SCPs, Vars, EdgeIx) -->
	(   foreach(scalar_product(Cs,Xs,#=<,RHS),SCPs),
	    param(Vars,EdgeIx)
	do  [linle(EdgeIx,LHS,RHS)],
	    (   foreach(T1,Vars),
		foreach(C,LHS),
		param(Cs,Xs)
	    do  {   nth0(J, Xs, T2),
		    T1==T2
		->  nth0(J, Cs, C)
		;   C = 0
		}
	    )
	).

compensate_for_inf(KL1, KL2) :-
	(   foreach(N-Cl1,KL1),
	    foreach(N-Cl3,KL2)
	do  (   select(inf, Cl1, Cl2) -> Cl3 = slow([inf|Cl2])
	    ;   member(sup, Cl1) -> Cl3 = slow(Cl1)
	    ;   Cl3 = fast(Cl1)
	    )
	).

mddi_map_nodes([], [], [], _, A, A, _, _).
mddi_map_nodes([node(ID,Var,SymEdges)|Nodes], [N|Ns], Pivots0, I, A0, A, Vars, Goal) :-
	must_be(ID, integer, Goal, 0),
	var_nth(Var, Vars, 0, N),
	avl_store(ID, A0, I-N, A1),
	J is I+1,
	(   foreach(edge(B..E,_,_),SymEdges),
	    fromto(Pivots0,[N-B,N-E1|Pivots1],Pivots1,Pivots),
	    param(N)
	do  fdinc(E, E1, 1)
	),
	mddi_map_nodes(Nodes, Ns, Pivots, J, A1, A, Vars, Goal).

mddi_map_rest([], [], [], [], Bot, Bot, _, _, _).
mddi_map_rest([node(_,_,SymEdges)|Nodes], [N|Ns], Edges0, VarVals0, Source, Bot, ID2Index, Node2P, Goal) :-
	(   foreach(SymEdge,SymEdges),
	    fromto(VarVals0,VarVals1,VarVals3,VarVals4),
	    fromto(Edges0,Edges1,Edges3,Edges4),
	    param(N,Source,Bot,ID2Index,Node2P)
	do  (   SymEdge = edge(Key,Linles,[]) -> Dest = Bot
	    ;   SymEdge = edge(Key,Linles,Y) ->  avl_fetch(Y, ID2Index, Dest-_)
	    ),
	    avl_fetch(N, Node2P, Pivots),
	    mddi_fragment(Pivots, Key, Vals),
	    (   foreach(B..E,Vals),
		fromto(VarVals1,[varval(N,Aux,B,E)-U|VarVals2],VarVals2,VarVals3),
		fromto(Edges1,[U-edge(Source,Linles,Dest,U)|Edges2],Edges2,Edges3),
		param(N,Source,Linles,Dest)
	    do  (integer(B) -> Aux = B ; Aux = 0.0)
	    )
	),
	Source1 is Source+1,
	mddi_map_rest(Nodes, Ns, Edges4, VarVals4, Source1, Bot, ID2Index, Node2P, Goal).

mddi_fragment(_, Min..Min, [Min..Min]) :- !.
mddi_fragment(fast(Pivots), Min..Max, Vals) :-
	mddi_fragment_fast(Pivots, Min, Max, Vals, []).
mddi_fragment(slow(Pivots), Min..Max, Vals) :-
	mddi_fragment(Pivots, Min, Max, Vals, []).

mddi_fragment([P|Ps], Min, Max) --> {\+le(Min,P)}, !,
	mddi_fragment(Ps, Min, Max).
mddi_fragment([P,Q|Ps], Min, Max) --> {le(P,Max)}, !, [P..R],
	{fdinc(Q, R, -1)},
	mddi_fragment([Q|Ps], Min, Max).
mddi_fragment(_, _, _) --> [].

mddi_fragment_fast([P|Ps], Min, Max) --> {Min>P}, !,
	mddi_fragment_fast(Ps, Min, Max).
mddi_fragment_fast([P,Q|Ps], Min, Max) --> {P=<Max}, !, [P..R],
	{R is Q-1},
	mddi_fragment_fast([Q|Ps], Min, Max).
mddi_fragment_fast(_, _, _) --> [].

table(Tuples, Extension1) :-
	table(Tuples, Extension1, [], table(Tuples, Extension1)).

table(Tuples, Extension1, Options) :-
	table(Tuples, Extension1, Options, table(Tuples, Extension1, Options)).

table(Tuples, Extension1, Options, Goal) :-
	must_be(Options, proper_list(callable), Goal, 2),
	(   foreach(Opt,Options),
	    fromto(opt(global,leftmost,noaux,_),Opt0,Opt1,opt(Cons,Order,Aux,NbNodes)),
	    param(Goal)
	do  (   table_option(Opt, Opt0, Opt1) -> true
	    ;   illarg(domain(term,table_option), Goal, 2, Opt)
	    )
	),
	table_order(Order, Tuples, Tuplesa, Extension1, Extension1a),
	table_augment(Aux, Tuplesa, Tuplesb, Extension1a, Extension1b),
	Tuplesb = [Tuple|_],
	length(Tuple, N),
	(   foreach(Row1,Extension1b),
	    fromto(Extension2,Extension3,Extension4,[]),
	    param(Goal)
	do  (   foreach(X,Row1),
		foreach(Y,Row2),
		param(Goal)
	    do  (   integer(X) ->
		    union_expression_check(X, Y, Goal, 2)
		;   set_expression_check(X, Y, Goal, 2)
		)
	    ),
	    (   member([], Row2) ->
		Extension3 = Extension4
	    ;   Extension3 = [Row2|Extension4]
	    )
	),
	(   N=:=0 -> true
	;   N=:=1, Cons==global
	->  append(Extension2, List),
	    fdset_union(List, FD),
	    append(Tuplesb, Vars),
	    domain(Vars, FD)
	;   N=:=2, Cons==global
	->  table_binary(Tuplesb, Extension2, Goal)
	;   table_internal(Tuplesb, Extension2, N, Cons, NbNodes, Goal)
	).

% called by automaton/9
table_internal(Tuples, Extension, N, _Cons, NbNodes, Goal) :-
	length(Template, N),
	table_general(Extension, Dag, Template),
	length(Dag, NbNodes),
	case(Template, Tuples, Dag, [], Goal).

table_option(nodes(NbNodes), opt(Cons,Order,Aux,_), opt(Cons,Order,Aux,NbNodes)).
table_option(consistency(Arg), opt(_,Order,Aux,NbNodes), opt(Cons,Order,Aux,NbNodes)) :-
	consistency_option(Arg, Cons, _).
table_option(order(Order), opt(Cons,_,Aux,NbNodes), opt(Cons,Order,Aux,NbNodes)) :-
	memberchk(Order, [leftmost,id3]).
table_option(method(Aux), opt(Cons,Order,_,NbNodes), opt(Cons,Order,Aux,NbNodes)) :-
	memberchk(Aux, [noaux,aux]).

table_order(leftmost, Ts, Ts, Rs, Rs).
table_order(id3, Ts0, Ts, Rs0, Rs) :-
	transpose(Rs0, Cols0),
	(   foreach(Col,Cols0),
	    foreach(Rank-Var,Rank1),
	    foreach(Var,Perm1)
	do  sort(Col, Set),
	    length(Set, Len),
	    Rank is -Len
	),
	keysort(Rank1, Rank2),
	(   foreach(_-Var2,Rank2),
	    foreach(Var2,Perm2)
	do  true
	),
	(   foreach(X1,[Cols0|Ts0]),
	    foreach(X2,[Cols|Ts]),
	    param(Perm1,Perm2)
	do  copy_term(Perm1-Perm2, X1-X2)
	),
	transpose(Cols, Rs).

table_augment(noaux, Ts, Ts, Rs, Rs).
table_augment(aux, Ts0, Ts, Rs0, Rs) :-
	(   foreach(T,Ts0),
	    foreach([_|T],Ts)
	do  true
	),
	(   foreach(R,Rs0),
	    foreach([J|R],Rs),
	    count(J,1,_)
	do  true
	).

table_general(Extension, Dag, Template) :-
	Extension\==[],
	empty_avl(Empty),
	length(Template, N),
	table_tree(N, Extension, Tree, Empty, _),
	tree_to_dag(Tree, _ID, 0, Empty, A2),
	avl_to_list(A2, L1),
	dag_nodes(L1, Dag, Template, 0).

dag_nodes([], [], _, _).
dag_nodes([(D-_)-(J-Children)|L1], [node(J,Var,Children)|L2], Template, J) :-
	nth0(D, Template, Var),
	K is J+1,
	dag_nodes(L1, L2, Template, K).

tree_to_dag([], [], _, A, A) :- !.
tree_to_dag(Children, ID, Depth, A, A) :-
	avl_fetch(Depth-Children, A, ID-_), !.
tree_to_dag(Children, ID, Depth, A0, A) :-
	avl_store(Depth-Children, A0, ID-Dags, A1),
	Depth1 is Depth+1,
	children_to_dags(Children, Dags, Depth1, A1, A).

children_to_dags([], [], _, A, A).
children_to_dags([Key-Tree|L1], [Key-ID|L2], D, A1, A3) :-
	tree_to_dag(Tree, ID, D, A1, A2),
	children_to_dags(L1, L2, D, A2, A3).

table_tree(0, _, [], M, M) :- !.
table_tree(N, Rows, Tree, M0, M) :-
	Key = N-Rows,
	(   avl_fetch(Key, M0, Tree) -> M0 = M
	;   avl_store(Key, M0, Tree, M1),
	    N1 is N-1,
	    head_events(Rows, 1, Ev1, []),
	    fdsort(Ev1, Ev2),
	    table_split(Rows, Ev2, KL1, []),
	    samsort(keyrange_le_table, KL1, KL2),
	    keyclumped(KL2, KL3),
	    table_tree_parts(KL3, N1, Raw, M1, M),
	    merge_subtrees(Raw, Tree)
	).

merge_subtrees([], []).
merge_subtrees([(A..B)-T,(C..D)-T|L1], L2) :-
	integer(B),
	integer(C),
	B+1 =:= C, !,
	merge_subtrees([(A..D)-T|L1], L2).
merge_subtrees([X|L1], [X|L2]) :-
	merge_subtrees(L1, L2).

table_tree_parts([], _, [], M, M).
table_tree_parts([Head-Rows|KL1], N, [Head-Tree|KL2], M0, M) :-
	table_tree(N, Rows, Tree, M0, M1),
	table_tree_parts(KL1, N, KL2, M1, M).

head_events([], _) --> [].
head_events([[Set|_]|Rows], A) -->
	set_events(Set),
	head_events(Rows, A).

set_events([]) --> [].
set_events([[X|Y]|Set]) --> [X,Z],
	{fdinc(Y, Z, 1)},
	set_events(Set).

table_parts([], _) --> [].
table_parts([H|Hs], Tail) --> [H-Tail],
	table_parts(Hs, Tail).

table_split([], _) --> [].
table_split([Row|Tab], Ev) -->
	{Row = [Head | Tail]},
	{split_intervals(Head, Ev, Parts, [])},
	table_parts(Parts, Tail),
	table_split(Tab, Ev).

split_intervals([], _) --> [].
split_intervals([[Min|Max]|Rest], Ev0) -->
	split_interval(Ev0, Min, Max, Ev),
	split_intervals(Rest, Ev).

split_interval([X|Xs], Min, Max, Ev) -->
	{X\==Min}, !,
	split_interval(Xs, Min, Max, Ev).
split_interval([_,Y|Ys], Min, Max, Ev) -->
	{integer(Y)},
	{Max==sup -> true ; Y =< Max}, !,
	{Y1 is Y-1},
	[Min..Y1],
	split_interval([Y|Ys], Y, Max, Ev).
split_interval([_,Y|Ys], Min, Max, [Y|Ys]) -->
	[Min..Max].

%%% TODO: eliminate, value -> val
consistency_spec(global, N, Spec) :-
	(   for(_,1,N),
	    foreach(dom,Spec)
	do  true
	).
consistency_spec(bound, N, Spec) :-
	(   for(_,1,N),
	    foreach(minmax,Spec)
	do  true
	).
consistency_spec(local, N, Spec) :-
	(   for(_,1,N),
	    foreach(value,Spec)
	do  true
	).

table_binary(VarTuples, Tuples, Goal) :-
	must_be(VarTuples, proper_list(proper_list), Goal, 1),
	must_be(Tuples, proper_list(proper_list), Goal, 2),
	rel2intervals(Tuples, X2Y, Y2X, XU, YU),
	keyclumped(X2Y, X2Ycl),
	length(X2Ycl, Xsize),
	keyclumped(Y2X, Y2Xcl),
	length(Y2Xcl, Ysize),
	(   foreach([X,Y],VarTuples),
	    foreach([X-XA,Y-YA],VATuples),
	    fromto(Susp1,[none(X),none(Y)|Susp2],Susp2,[]),
	    count(_,1,NVA),
	    param(Goal,XU,YU)
	do  X in_set XU,
	    Y in_set YU,
	    arg_attribute(X, XA, Goal, 1),
	    arg_attribute(Y, YA, Goal, 1)
	),
	fd_global_internal(ac3intervals(VarTuples,Tuples),
			   state(VATuples,NVA,Xsize,Ysize,X2Y,Y2X,NVA/*nactive*/,_Handle,0),
			   Susp1, _, clpfd:Goal, 0).

rel2intervals(Extension, X2Y, Y2X, XU, YU) :-
	(   foreach([Xs,Ys],Extension),
	    foreach(Xs-Ys,ExtensionKL1),
	    foreach(Xs,Xss),
	    foreach(Ys,Yss),
	    fromto(XEv1,XEv2,XEv3,[]),
	    fromto(YEv1,YEv2,YEv3,[])
	do  ac3set_events(Xs, XEv2, XEv3),
	    ac3set_events(Ys, YEv2, YEv3)
	),
	fdset_union(Xss, XU),
	fdset_union(Yss, YU),
	sort(XEv1, XEv4),
	sort(YEv1, YEv4),
	events2sets(XEv4, XAtoms),
	events2sets(YEv4, YAtoms),
	split_extension(ExtensionKL1, XAtoms, YAtoms, X2Y0),
	(   foreach(L-R,X2Y0),
	    foreach(R-L,Y2X0)
	do  true
	),
	sort(X2Y0, X2Y1),
	sort(Y2X0, Y2X1),
	deinf(X2Y1, X2Y2),
	deinf(Y2X1, Y2X2),
	conjoin(X2Y2, X2Y),
	conjoin(Y2X2, Y2X).

deinf(L1, L2) :-
	(   foreach((A1..B)-(C1..D),L1),
	    foreach((A2..B)-(C2..D),L2)
	do  int2inf(A1, A2),
	    int2inf(C1, C2)
	).

conjoin([], []).
conjoin([P1,P2|Ps], Pt) :-
	P1 = Key-(C..D),
	P2 = Key-(E..F),
	D+1 =:= E, !,
	P3 = Key-(C..F),
	conjoin([P3|Ps], Pt).
conjoin([P1|Ps], [P1|Pt]) :-
	conjoin(Ps, Pt).

ac3set_events([]) --> [].
ac3set_events([[X|Y]|Set]) --> [X1,Y1],
	{inf2int(X, X1)},
	{fdinc(Y, Y1, 1)},
	ac3set_events(Set).

events2sets(Evs, Atoms) :-
	(   fromto(Evs,[A,B|Evs1],[B|Evs1],[_]),
	    foreach(Atom,Atoms)
	do  int2inf(A, A1),
	    fdinc(B, B1, -1),
	    fdset_parts(Atom, A1, B1, [])
	).

split_extension(Extension, XAtoms, YAtoms, X2Y0) :-
	(   foreach(Xs-Ys,Extension),
	    fromto(X2Y0,X2Y1,X2Y5,[]),
	    param(XAtoms,YAtoms)
	do  (   foreach(XA,XAtoms),
		fromto(XAs1,XAs2,XAs3,[]),
		param(Xs)
	    do  (fdset_intersect(Xs, XA) -> XAs2 = [XA|XAs3] ; XAs2 = XAs3)
	    ),
	    (   foreach(YA,YAtoms),
		fromto(YAs1,YAs2,YAs3,[]),
		param(Ys)
	    do  (fdset_intersect(Ys, YA) -> YAs2 = [YA|YAs3] ; YAs2 = YAs3)
	    ),
	    (   foreach(XA1,XAs1),
		fromto(X2Y1,X2Y2,X2Y4,X2Y5),
		param(YAs1)
	    do  s2r(XA1, A, B),
	        (   foreach(YA1,YAs1),
		    fromto(X2Y2,[(A..B)-(C..D)|X2Y3],X2Y3,X2Y4),
		    param(A,B)
		do  s2r(YA1, C, D)
		)
	    )
	).

s2r([[A0|B]], A, B) :-
	inf2int(A0, A).

inf2int(inf, Y) :- !, Y = -0x8000000000000000.
inf2int(Y, Y).

int2inf(-0x8000000000000000, Y) :- !, Y = inf.
int2inf(Y, Y).

fdinc(inf, inf, _) :- !.
fdinc(sup, sup, _) :- !.
fdinc(X, Y, C) :-
	Y is X+C.

fdsort(L0, L) :-
	sort(L0, L1),
	(   select(inf, L1, L2) -> L = [inf|L2]
	;   L = L1
	).

keyrange_le_table((A.._)-_, (C.._)-_) :-
        le(A, C).
