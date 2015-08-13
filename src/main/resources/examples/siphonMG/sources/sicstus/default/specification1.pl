:- use_module(library(clpfd)).

initialMarking([1, 0, 0, 0, 0, 0, 0, 0]).

finalMarking([0, 1, 0, 0, 0, 0, 0, 0]).

stateEquation(VMax, [MA_i, MA_o, MA_P1, MA_P2, MA_P3, MA_P4, MA_P5, MA_P6], [MB_i, MB_o, MB_P1, MB_P2, MB_P3, MB_P4, MB_P5, MB_P6], [VP_i, VP_o, VP_P1, VP_P2, VP_P3, VP_P4, VP_P5, VP_P6], [VT_T0, VT_T1, VT_T2, VT_T3]):-
	domain([MA_i, MA_o, MA_P1, MA_P2, MA_P3, MA_P4, MA_P5, MA_P6], 0, sup),
	domain([MB_i, MB_o, MB_P1, MB_P2, MB_P3, MB_P4, MB_P5, MB_P6], 0, sup),
	domain([VP_i, VP_o, VP_P1, VP_P2, VP_P3, VP_P4, VP_P5, VP_P6], 0, VMax),
	domain([VT_T0, VT_T1, VT_T2, VT_T3], 0, VMax),
	sum([MA_i], #=, VP_i),
	sum([MB_i, VT_T0], #=, VP_i),
	sum([MA_o, VT_T3], #=, VP_o),
	sum([MB_o], #=, VP_o),
	sum([MA_P1, VT_T0], #=, VP_P1),
	sum([MB_P1, VT_T1], #=, VP_P1),
	sum([MA_P2, VT_T0], #=, VP_P2),
	sum([MB_P2, VT_T2], #=, VP_P2),
	sum([MA_P3, VT_T1], #=, VP_P3),
	sum([MB_P3, VT_T2], #=, VP_P3),
	sum([MA_P4, VT_T2], #=, VP_P4),
	sum([MB_P4, VT_T1], #=, VP_P4),
	sum([MA_P5, VT_T1], #=, VP_P5),
	sum([MB_P5, VT_T3], #=, VP_P5),
	sum([MA_P6, VT_T2], #=, VP_P6),
	sum([MB_P6, VT_T3], #=, VP_P6).

formula([VT_T0, VT_T1, VT_T2, VT_T3]):-
	((VT_T0 #> 0) #\/ (VT_T1 #> 0) #\/ (VT_T2 #> 0) #\/ (VT_T3 #> 0)).

indexToTransition(Index, Transition):-
    nth0(Index, [t0, t1, t2, t3], Transition).

getSTs(VTs, STs):-
    getSTs_(0, VTs, STs).
getSTs_(_, [], []):-!.
getSTs_(Index, [VT|VTs], STs):-
    VT #= 0,
    NextIndex is Index + 1,
    getSTs_(NextIndex, VTs, STs).
getSTs_(Index, [VT|VTs], [ST|STs]):-
    VT #> 0,
    indexToTransition(Index, ST),
    NextIndex is Index + 1,
    getSTs_(NextIndex, VTs, STs).

noSiphon(MAs, MBs, VPs, VTs):-
    noSiphon_(MAs, MBs, VPs, XPs),
    domain(XPs, 0, 1),
    getSTs(VTs, STs),
    \+ noSiphon__(STs, XPs),
    labeling([], XPs),
    w('XPs', XPs).

noSiphon__([], XPs):-
    sum(XPs, #>, 0).
noSiphon__([ST|STs], XPs):-
    noSiphon___(ST, XPs),
    noSiphon__(STs, XPs).
noSiphon___(t0, [Xi, Xo, Xp1, Xp2, Xp3, Xp4, Xp5, Xp6]):-
    Xi #>= Xp1,
    Xi #>= Xp2.
noSiphon___(t1, [Xi, Xo, Xp1, Xp2, Xp3, Xp4, Xp5, Xp6]):-
    Xp1 + Xp4 #>= Xp3,
    Xp1 + Xp4 #>= Xp5.
noSiphon___(t2, [Xi, Xo, Xp1, Xp2, Xp3, Xp4, Xp5, Xp6]):-
    Xp2 + Xp3 #>= Xp4,
    Xp2 + Xp3 #>= Xp6.
noSiphon___(t3, [Xi, Xo, Xp1, Xp2, Xp3, Xp4, Xp5, Xp6]):-
    Xp5 + Xp6 #>= Xo.

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
	stateEquation(VMax, MAs, MBs, VPs, VTs),
	formula(VTs),
	noSiphon(MAs, MBs, VPs, VTs).

test:-
    overApproximation2(42, MAs, MBs, VPs, VTs).
