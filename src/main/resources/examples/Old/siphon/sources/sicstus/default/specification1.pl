:- use_module(library(clpfd)).

initialMarking([1, 0, 0, 0, 0, 0, 0]).

finalMarking([0, 1, 0, 0, 0, 0, 0]).

stateEquation(VMax, [MA_i, MA_o, MA_P1, MA_P2, MA_P3, MA_P4, MA_P5], [MB_i, MB_o, MB_P1, MB_P2, MB_P3, MB_P4, MB_P5], [VP_i, VP_o, VP_P1, VP_P2, VP_P3, VP_P4, VP_P5], [VT_T0, VT_T1, VT_T2, VT_T3]):-
	domain([MA_i, MA_o, MA_P1, MA_P2, MA_P3, MA_P4, MA_P5], 0, sup),
	domain([MB_i, MB_o, MB_P1, MB_P2, MB_P3, MB_P4, MB_P5], 0, sup),
	domain([VP_i, VP_o, VP_P1, VP_P2, VP_P3, VP_P4, VP_P5], 0, VMax),
	domain([VT_T0, VT_T1, VT_T2, VT_T3], 0, VMax),
	sum([MA_i], #=, VP_i),
	sum([MB_i, VT_T0], #=, VP_i),
	sum([MA_o, VT_T3], #=, VP_o),
	sum([MB_o], #=, VP_o),
	sum([MA_P1, VT_T0], #=, VP_P1),
	sum([MB_P1, VT_T1], #=, VP_P1),
	sum([MA_P2, VT_T0], #=, VP_P2),
	sum([MB_P2, VT_T2], #=, VP_P2),
	sum([MA_P3, VT_T1, VT_T2], #=, VP_P3),
	sum([MB_P3, VT_T1, VT_T2], #=, VP_P3),
	sum([MA_P4, VT_T1], #=, VP_P4),
	sum([MB_P4, VT_T3], #=, VP_P4),
	sum([MA_P5, VT_T2], #=, VP_P5),
	sum([MB_P5, VT_T3], #=, VP_P5).

formula([VT_T0, VT_T1, VT_T2, VT_T3]):-
	((VT_T0 #> 0) #\/ (VT_T1 #> 0) #\/ (VT_T2 #> 0) #\/ (VT_T3 #> 0)).

subnetInitialization([MA_i, MA_o, MA_P1, MA_P2, MA_P3, MA_P4, MA_P5], [MB_i, MB_o, MB_P1, MB_P2, MB_P3, MB_P4, MB_P5], [VP_i, VP_o, VP_P1, VP_P2, VP_P3, VP_P4, VP_P5], [VT_T0, VT_T1, VT_T2, VT_T3], [XI_i, XI_o, XI_P1, XI_P2, XI_P3, XI_P4, XI_P5]):-
	domain([XI_i, XI_o, XI_P1, XI_P2, XI_P3, XI_P4, XI_P5], 0, 1),
	(((MA_i #> 0) #\/ (MB_i #> 0) #\/ (VP_i #= 0)) #=> (XI_i #= 0)),
	(((MA_o #> 0) #\/ (MB_o #> 0) #\/ (VP_o #= 0)) #=> (XI_o #= 0)),
	(((MA_P1 #> 0) #\/ (MB_P1 #> 0) #\/ (VP_P1 #= 0)) #=> (XI_P1 #= 0)),
	(((MA_P2 #> 0) #\/ (MB_P2 #> 0) #\/ (VP_P2 #= 0)) #=> (XI_P2 #= 0)),
	(((MA_P3 #> 0) #\/ (MB_P3 #> 0) #\/ (VP_P3 #= 0)) #=> (XI_P3 #= 0)),
	(((MA_P4 #> 0) #\/ (MB_P4 #> 0) #\/ (VP_P4 #= 0)) #=> (XI_P4 #= 0)),
	(((MA_P5 #> 0) #\/ (MB_P5 #> 0) #\/ (VP_P5 #= 0)) #=> (XI_P5 #= 0)).

siphon([MA_i, MA_o, MA_P1, MA_P2, MA_P3, MA_P4, MA_P5], [MB_i, MB_o, MB_P1, MB_P2, MB_P3, MB_P4, MB_P5], [VP_i, VP_o, VP_P1, VP_P2, VP_P3, VP_P4, VP_P5], [VT_T0, VT_T1, VT_T2, VT_T3], [XI_i, XI_o, XI_P1, XI_P2, XI_P3, XI_P4, XI_P5]):-
	sum([XI_i, XI_o, XI_P1, XI_P2, XI_P3, XI_P4, XI_P5], #>, 0),
	((VT_T0 #> 0) #=> (((XI_i) #>= XI_P1) #/\ ((XI_i) #>= XI_P2))),
	((VT_T1 #> 0) #=> (((XI_P1 + XI_P3) #>= XI_P3) #/\ ((XI_P1 + XI_P3) #>= XI_P4))),
	((VT_T2 #> 0) #=> (((XI_P2 + XI_P3) #>= XI_P3) #/\ ((XI_P2 + XI_P3) #>= XI_P5))),
	((VT_T3 #> 0) #=> (((XI_P4 + XI_P5) #>= XI_o))),
	labeling([], [XI_i, XI_o, XI_P1, XI_P2, XI_P3, XI_P4, XI_P5]).
noSiphon(MAs, MBs, VPs, VTs, XIs):-
	subnetInitialization(MAs, MBs, VPs, VTs, XIs),
	labeling([], VTs),
	\+(siphon(MAs, MBs, VPs, VTs, XIs)).

markedGraph([], []).
markedGraph([MA|MAs], [VP|VPs]):-
	((VP - MA) #=< 1),
	markedGraph(MAs, VPs).

pairwiseSum([], [], []).
pairwiseSum([H1|T1], [H2|T2], [H3|T3]):-
	(H3 #= (H1 + H2)),
	pairwiseSum(T1, T2, T3).
pairwiseSum([L], L).
pairwiseSum([L1, L2|Ls], R):-
	pairwiseSum(L1, L2, R1),
	pairwiseSum([R1|Ls], R).

overApproximation1(VMax, MAs, MBs, VPs, VTs):-
	initialMarking(MAs),
	finalMarking(MBs),
	stateEquation(VMax, MAs, MBs, VPs, VTs),
	formula(VTs),
	labeling([], VTs),
	labeling([], VPs).

overApproximation2(VMax, MAs, MBs, VPs, VTs):-
	initialMarking(MAs),
	finalMarking(MBs),
	stateEquation(VMax, MAs, MBs, VPs, VTs),
	formula(VTs),
	noSiphon(MAs, MBs, VPs, VTs, XIs),
	labeling([], VTs),
	labeling([], VPs).

