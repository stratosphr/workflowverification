:- use_module(library(clpfd)).

w(Name, Value):-
    write(Name),
    write(' = '),
    write(Value),
    nl.

separator:-
    write('------------------------------------'),
    nl.

initialMarking([1, 0, 0, 0, 0, 0, 0]).

finalMarking([0, 1, 0, 0, 0, 0, 0]).

stateEquation(VMax, [MA_i, MA_o, MA_P1, MA_P2, MA_P3, MA_P5, MA_P6], [MB_i, MB_o, MB_P1, MB_P2, MB_P3, MB_P5, MB_P6], [VP_i, VP_o, VP_P1, VP_P2, VP_P3, VP_P5, VP_P6], [VT_T0, VT_T1, VT_T2, VT_T3, VT_T4, VT_T5]):-
	domain([MA_i, MA_o, MA_P1, MA_P2, MA_P3, MA_P5, MA_P6], 0, sup),
	domain([MB_i, MB_o, MB_P1, MB_P2, MB_P3, MB_P5, MB_P6], 0, sup),
	domain([VP_i, VP_o, VP_P1, VP_P2, VP_P3, VP_P5, VP_P6], 0, VMax),
	domain([VT_T0, VT_T1, VT_T2, VT_T3, VT_T4, VT_T5], 0, VMax),
	sum([MA_i], #=, VP_i),
	sum([MB_i, VT_T0], #=, VP_i),
	sum([MA_o, VT_T5], #=, VP_o),
	sum([MB_o], #=, VP_o),
	sum([MA_P1, VT_T0, VT_T3], #=, VP_P1),
	sum([MB_P1, VT_T1, VT_T4], #=, VP_P1),
	sum([MA_P2, VT_T1], #=, VP_P2),
	sum([MB_P2, VT_T2], #=, VP_P2),
	sum([MA_P3, VT_T2], #=, VP_P3),
	sum([MB_P3, VT_T3], #=, VP_P3),
	sum([MA_P5, VT_T4], #=, VP_P5),
	sum([MB_P5, VT_T5], #=, VP_P5),
	sum([MA_P6, VT_T2], #=, VP_P6),
	sum([MB_P6, VT_T5], #=, VP_P6).

formula([_, _, _, VT_T3, _, _]):-
	(#\ (VT_T3 #> 0)).

noSiphon(MAs, MBs, VPs, VTs):-
	\+(siphon(MAs, MBs, VPs, VTs, XIs)).
siphon([MA_i, MA_o, MA_P1, MA_P2, MA_P3, MA_P5, MA_P6], [MB_i, MB_o, MB_P1, MB_P2, MB_P3, MB_P5, MB_P6], [VP_i, VP_o, VP_P1, VP_P2, VP_P3, VP_P5, VP_P6], [VT_T0, VT_T1, VT_T2, VT_T3, VT_T4, VT_T5], [XI_i, XI_o, XI_P1, XI_P2, XI_P3, XI_P5, XI_P6]):-
	domain([XI_i, XI_o, XI_P1, XI_P2, XI_P3, XI_P5, XI_P6], 0, 1),
	sum([XI_i, XI_o, XI_P1, XI_P2, XI_P3, XI_P5, XI_P6], #>, 0),
	(((MA_i #> 0) #\/ (MB_i #> 0) #\/ (VP_i #= 0)) #=> (XI_i #= 0)),
	(((MA_o #> 0) #\/ (MB_o #> 0) #\/ (VP_o #= 0)) #=> (XI_o #= 0)),
	(((MA_P1 #> 0) #\/ (MB_P1 #> 0) #\/ (VP_P1 #= 0)) #=> (XI_P1 #= 0)),
	(((MA_P2 #> 0) #\/ (MB_P2 #> 0) #\/ (VP_P2 #= 0)) #=> (XI_P2 #= 0)),
	(((MA_P3 #> 0) #\/ (MB_P3 #> 0) #\/ (VP_P3 #= 0)) #=> (XI_P3 #= 0)),
	(((MA_P5 #> 0) #\/ (MB_P5 #> 0) #\/ (VP_P5 #= 0)) #=> (XI_P5 #= 0)),
	(((MA_P6 #> 0) #\/ (MB_P6 #> 0) #\/ (VP_P6 #= 0)) #=> (XI_P6 #= 0)),
	((VT_T0 #> 0) #=> (((XI_i) #>= XI_P1))),
	((VT_T1 #> 0) #=> (((XI_P1) #>= XI_P2))),
	((VT_T2 #> 0) #=> (((XI_P2) #>= XI_P3) #/\ ((XI_P2) #>= XI_P6))),
	((VT_T3 #> 0) #=> (((XI_P3) #>= XI_P1))),
	((VT_T4 #> 0) #=> (((XI_P1) #>= XI_P5))),
	((VT_T5 #> 0) #=> (((XI_P5 + XI_P6) #>= XI_o))),
	labeling([], [XI_i, XI_o, XI_P1, XI_P2, XI_P3, XI_P5, XI_P6]).

markedGraph(VPs):-
	domain(VPs, 0, 1).

overApproximation1(VMax, MAs, MBs, VPs, VTs):-
	initialMarking(MAs),
	finalMarking(MBs),
	stateEquation(VMax, MAs, MBs, VPs, VTs),
	formula(VTs).

overApproximation2(VMax, MAs, MBs, VPs, VTs):-
	initialMarking(MAs),
	finalMarking(MBs),
	stateEquation(VMax, MAs, MBs, VPs, VTs).
    %formula(VTs).
    %noSiphon(MAs, MBs, VPs, VTs).

test:-
    overApproximation2(42, MAs, MBs, VPs, VTs),
    w('MAs', MAs),
    w('MBs', MBs),
    w('VPs', VPs),
    w('VTs', VTs).

