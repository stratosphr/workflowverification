:- use_module(library(clpfd)).

initialMarking([1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]).

finalMarking([0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]).

stateEquation(VMax, [MA_i, MA_o, MA_P1, MA_P10, MA_P11, MA_P12, MA_P2, MA_P3, MA_P5, MA_P6, MA_P7, MA_P8, MA_P9], [MB_i, MB_o, MB_P1, MB_P10, MB_P11, MB_P12, MB_P2, MB_P3, MB_P5, MB_P6, MB_P7, MB_P8, MB_P9], [VP_i, VP_o, VP_P1, VP_P10, VP_P11, VP_P12, VP_P2, VP_P3, VP_P5, VP_P6, VP_P7, VP_P8, VP_P9], [VT_T0, VT_T1, VT_T10, VT_T11, VT_T2, VT_T3, VT_T4, VT_T5, VT_T6, VT_T7, VT_T8, VT_T9]):-
	domain([MA_i, MA_o, MA_P1, MA_P10, MA_P11, MA_P12, MA_P2, MA_P3, MA_P5, MA_P6, MA_P7, MA_P8, MA_P9], 0, sup),
	domain([MB_i, MB_o, MB_P1, MB_P10, MB_P11, MB_P12, MB_P2, MB_P3, MB_P5, MB_P6, MB_P7, MB_P8, MB_P9], 0, sup),
	domain([VP_i, VP_o, VP_P1, VP_P10, VP_P11, VP_P12, VP_P2, VP_P3, VP_P5, VP_P6, VP_P7, VP_P8, VP_P9], 0, VMax),
	domain([VT_T0, VT_T1, VT_T10, VT_T11, VT_T2, VT_T3, VT_T4, VT_T5, VT_T6, VT_T7, VT_T8, VT_T9], 0, VMax),
	sum([MA_i], #=, VP_i),
	sum([MB_i, VT_T0], #=, VP_i),
	sum([MA_o, VT_T11], #=, VP_o),
	sum([MB_o], #=, VP_o),
	sum([MA_P1, VT_T0, VT_T3], #=, VP_P1),
	sum([MB_P1, VT_T1, VT_T4], #=, VP_P1),
	sum([MA_P10, VT_T8], #=, VP_P10),
	sum([MB_P10, VT_T9], #=, VP_P10),
	sum([MA_P11, VT_T10], #=, VP_P11),
	sum([MB_P11, VT_T11], #=, VP_P11),
	sum([MA_P12, VT_T8], #=, VP_P12),
	sum([MB_P12, VT_T11], #=, VP_P12),
	sum([MA_P2, VT_T1], #=, VP_P2),
	sum([MB_P2, VT_T2], #=, VP_P2),
	sum([MA_P3, VT_T2], #=, VP_P3),
	sum([MB_P3, VT_T3], #=, VP_P3),
	sum([MA_P5, VT_T4], #=, VP_P5),
	sum([MB_P5, VT_T5], #=, VP_P5),
	sum([MA_P6, VT_T2], #=, VP_P6),
	sum([MB_P6, VT_T5], #=, VP_P6),
	sum([MA_P7, VT_T5], #=, VP_P7),
	sum([MB_P7, VT_T6], #=, VP_P7),
	sum([MA_P8, VT_T6, VT_T9], #=, VP_P8),
	sum([MB_P8, VT_T10, VT_T7], #=, VP_P8),
	sum([MA_P9, VT_T7], #=, VP_P9),
	sum([MB_P9, VT_T8], #=, VP_P9).

formula([_, _, _, _, _, VT_T3, _, _, _, _, _, _]):-
	(VT_T3 #> 0).

noSiphon(MAs, MBs, VPs, VTs):-
	labeling([], VTs),
	\+(siphon(MAs, MBs, VPs, VTs, XIs)).
siphon([MA_i, MA_o, MA_P1, MA_P10, MA_P11, MA_P12, MA_P2, MA_P3, MA_P5, MA_P6, MA_P7, MA_P8, MA_P9], [MB_i, MB_o, MB_P1, MB_P10, MB_P11, MB_P12, MB_P2, MB_P3, MB_P5, MB_P6, MB_P7, MB_P8, MB_P9], [VP_i, VP_o, VP_P1, VP_P10, VP_P11, VP_P12, VP_P2, VP_P3, VP_P5, VP_P6, VP_P7, VP_P8, VP_P9], [VT_T0, VT_T1, VT_T10, VT_T11, VT_T2, VT_T3, VT_T4, VT_T5, VT_T6, VT_T7, VT_T8, VT_T9], [XI_i, XI_o, XI_P1, XI_P10, XI_P11, XI_P12, XI_P2, XI_P3, XI_P5, XI_P6, XI_P7, XI_P8, XI_P9]):-
	domain([XI_i, XI_o, XI_P1, XI_P10, XI_P11, XI_P12, XI_P2, XI_P3, XI_P5, XI_P6, XI_P7, XI_P8, XI_P9], 0, 1),
	sum([XI_i, XI_o, XI_P1, XI_P10, XI_P11, XI_P12, XI_P2, XI_P3, XI_P5, XI_P6, XI_P7, XI_P8, XI_P9], #>, 0),
	(((MA_i #> 0) #\/ (MB_i #> 0) #\/ (VP_i #= 0)) #=> (XI_i #= 0)),
	(((MA_o #> 0) #\/ (MB_o #> 0) #\/ (VP_o #= 0)) #=> (XI_o #= 0)),
	(((MA_P1 #> 0) #\/ (MB_P1 #> 0) #\/ (VP_P1 #= 0)) #=> (XI_P1 #= 0)),
	(((MA_P10 #> 0) #\/ (MB_P10 #> 0) #\/ (VP_P10 #= 0)) #=> (XI_P10 #= 0)),
	(((MA_P11 #> 0) #\/ (MB_P11 #> 0) #\/ (VP_P11 #= 0)) #=> (XI_P11 #= 0)),
	(((MA_P12 #> 0) #\/ (MB_P12 #> 0) #\/ (VP_P12 #= 0)) #=> (XI_P12 #= 0)),
	(((MA_P2 #> 0) #\/ (MB_P2 #> 0) #\/ (VP_P2 #= 0)) #=> (XI_P2 #= 0)),
	(((MA_P3 #> 0) #\/ (MB_P3 #> 0) #\/ (VP_P3 #= 0)) #=> (XI_P3 #= 0)),
	(((MA_P5 #> 0) #\/ (MB_P5 #> 0) #\/ (VP_P5 #= 0)) #=> (XI_P5 #= 0)),
	(((MA_P6 #> 0) #\/ (MB_P6 #> 0) #\/ (VP_P6 #= 0)) #=> (XI_P6 #= 0)),
	(((MA_P7 #> 0) #\/ (MB_P7 #> 0) #\/ (VP_P7 #= 0)) #=> (XI_P7 #= 0)),
	(((MA_P8 #> 0) #\/ (MB_P8 #> 0) #\/ (VP_P8 #= 0)) #=> (XI_P8 #= 0)),
	(((MA_P9 #> 0) #\/ (MB_P9 #> 0) #\/ (VP_P9 #= 0)) #=> (XI_P9 #= 0)),
	((VT_T0 #> 0) #=> (((XI_i) #>= XI_P1))),
	((VT_T1 #> 0) #=> (((XI_P1) #>= XI_P2))),
	((VT_T10 #> 0) #=> (((XI_P8) #>= XI_P11))),
	((VT_T11 #> 0) #=> (((XI_P11 + XI_P12) #>= XI_o))),
	((VT_T2 #> 0) #=> (((XI_P2) #>= XI_P3) #/\ ((XI_P2) #>= XI_P6))),
	((VT_T3 #> 0) #=> (((XI_P3) #>= XI_P1))),
	((VT_T4 #> 0) #=> (((XI_P1) #>= XI_P5))),
	((VT_T5 #> 0) #=> (((XI_P5 + XI_P6) #>= XI_P7))),
	((VT_T6 #> 0) #=> (((XI_P7) #>= XI_P8))),
	((VT_T7 #> 0) #=> (((XI_P8) #>= XI_P9))),
	((VT_T8 #> 0) #=> (((XI_P9) #>= XI_P10) #/\ ((XI_P9) #>= XI_P12))),
	((VT_T9 #> 0) #=> (((XI_P10) #>= XI_P8))),
	labeling([], [XI_i, XI_o, XI_P1, XI_P10, XI_P11, XI_P12, XI_P2, XI_P3, XI_P5, XI_P6, XI_P7, XI_P8, XI_P9]).

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
	noSiphon(MAs, MBs, VPs, VTs),
	labeling([], VTs),
	labeling([], VPs).

