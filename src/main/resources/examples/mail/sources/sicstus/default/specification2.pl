:- use_module(library(clpfd)).

initialMarking([1, 0, 0, 0, 0, 0, 0, 0]).

finalMarking([0, 1, 0, 0, 0, 0, 0, 0]).

stateEquation(VMax, [MA_i, MA_o, MA_P2, MA_P3, MA_P4, MA_P5, MA_P6, MA_P7], [MB_i, MB_o, MB_P2, MB_P3, MB_P4, MB_P5, MB_P6, MB_P7], [VP_i, VP_o, VP_P2, VP_P3, VP_P4, VP_P5, VP_P6, VP_P7], [VT_consulter, VT_deconnecter, VT_mdp, VT_pseudo, VT_split, VT_valider]):-
	domain([MA_i, MA_o, MA_P2, MA_P3, MA_P4, MA_P5, MA_P6, MA_P7], 0, sup),
	domain([MB_i, MB_o, MB_P2, MB_P3, MB_P4, MB_P5, MB_P6, MB_P7], 0, sup),
	domain([VP_i, VP_o, VP_P2, VP_P3, VP_P4, VP_P5, VP_P6, VP_P7], 0, VMax),
	domain([VT_consulter, VT_deconnecter, VT_mdp, VT_pseudo, VT_split, VT_valider], 0, VMax),
	sum([MA_i], #=, VP_i),
	sum([MB_i, VT_split], #=, VP_i),
	sum([MA_o, VT_deconnecter], #=, VP_o),
	sum([MB_o], #=, VP_o),
	sum([MA_P2, VT_split], #=, VP_P2),
	sum([MB_P2, VT_pseudo], #=, VP_P2),
	sum([MA_P3, VT_split], #=, VP_P3),
	sum([MB_P3, VT_mdp], #=, VP_P3),
	sum([MA_P4, VT_pseudo], #=, VP_P4),
	sum([MB_P4, VT_valider], #=, VP_P4),
	sum([MA_P5, VT_mdp], #=, VP_P5),
	sum([MB_P5, VT_valider], #=, VP_P5),
	sum([MA_P6, VT_valider], #=, VP_P6),
	sum([MB_P6, VT_deconnecter], #=, VP_P6),
	sum([MA_P7, VT_consulter, VT_valider], #=, VP_P7),
	sum([MB_P7, VT_consulter, VT_deconnecter], #=, VP_P7).

formula([VT_consulter, VT_deconnecter, VT_mdp, VT_pseudo, VT_split, VT_valider]):-
	((VT_split #> 0) #/\ (VT_pseudo #> 0) #/\ (VT_mdp #> 0) #/\ (VT_valider #> 0) #/\ (VT_consulter #> 0) #/\ (VT_deconnecter #> 0)).

noSiphon(MAs, MBs, VPs, VTs):-
	labeling([], VTs),
	\+(siphon(MAs, MBs, VPs, VTs, XIs)).
siphon([MA_i, MA_o, MA_P2, MA_P3, MA_P4, MA_P5, MA_P6, MA_P7], [MB_i, MB_o, MB_P2, MB_P3, MB_P4, MB_P5, MB_P6, MB_P7], [VP_i, VP_o, VP_P2, VP_P3, VP_P4, VP_P5, VP_P6, VP_P7], [VT_consulter, VT_deconnecter, VT_mdp, VT_pseudo, VT_split, VT_valider], [XI_i, XI_o, XI_P2, XI_P3, XI_P4, XI_P5, XI_P6, XI_P7]):-
	domain([XI_i, XI_o, XI_P2, XI_P3, XI_P4, XI_P5, XI_P6, XI_P7], 0, 1),
	sum([XI_i, XI_o, XI_P2, XI_P3, XI_P4, XI_P5, XI_P6, XI_P7], #>, 0),
	(((MA_i #> 0) #\/ (MB_i #> 0) #\/ (VP_i #= 0)) #=> (XI_i #= 0)),
	(((MA_o #> 0) #\/ (MB_o #> 0) #\/ (VP_o #= 0)) #=> (XI_o #= 0)),
	(((MA_P2 #> 0) #\/ (MB_P2 #> 0) #\/ (VP_P2 #= 0)) #=> (XI_P2 #= 0)),
	(((MA_P3 #> 0) #\/ (MB_P3 #> 0) #\/ (VP_P3 #= 0)) #=> (XI_P3 #= 0)),
	(((MA_P4 #> 0) #\/ (MB_P4 #> 0) #\/ (VP_P4 #= 0)) #=> (XI_P4 #= 0)),
	(((MA_P5 #> 0) #\/ (MB_P5 #> 0) #\/ (VP_P5 #= 0)) #=> (XI_P5 #= 0)),
	(((MA_P6 #> 0) #\/ (MB_P6 #> 0) #\/ (VP_P6 #= 0)) #=> (XI_P6 #= 0)),
	(((MA_P7 #> 0) #\/ (MB_P7 #> 0) #\/ (VP_P7 #= 0)) #=> (XI_P7 #= 0)),
	((VT_consulter #> 0) #=> (((XI_P7) #>= XI_P7))),
	((VT_deconnecter #> 0) #=> (((XI_P6 + XI_P7) #>= XI_o))),
	((VT_mdp #> 0) #=> (((XI_P3) #>= XI_P5))),
	((VT_pseudo #> 0) #=> (((XI_P2) #>= XI_P4))),
	((VT_split #> 0) #=> (((XI_i) #>= XI_P2) #/\ ((XI_i) #>= XI_P3))),
	((VT_valider #> 0) #=> (((XI_P4 + XI_P5) #>= XI_P6) #/\ ((XI_P4 + XI_P5) #>= XI_P7))),
	labeling([], [XI_i, XI_o, XI_P2, XI_P3, XI_P4, XI_P5, XI_P6, XI_P7]).

markedGraph([], []).
markedGraph([MA|MAs], [VP|VPs]):-
	((VP - MA) #=< 1),
	markedGraph(MAs, VPs).

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

overApproximation3(VMax, [MK_0, MK_1, MK_2, MK_3, MK_4, MK_5, MK_6, MK_7, MK_8, MK_9, MK_10], [VPK_0, VPK_1, VPK_2, VPK_3, VPK_4, VPK_5, VPK_6, VPK_7, VPK_8, VPK_9], [VTK_0, VTK_1, VTK_2, VTK_3, VTK_4, VTK_5, VTK_6, VTK_7, VTK_8, VTK_9]):-
	initialMarking(MK_0),
	finalMarking(MK_10),
	stateEquation(VMax, MK_0, MK_1, VPK_0, VTK_0),
	markedGraph(MK_0, VPK_0),
	stateEquation(VMax, MK_1, MK_2, VPK_1, VTK_1),
	markedGraph(MK_1, VPK_1),
	stateEquation(VMax, MK_2, MK_3, VPK_2, VTK_2),
	markedGraph(MK_2, VPK_2),
	stateEquation(VMax, MK_3, MK_4, VPK_3, VTK_3),
	markedGraph(MK_3, VPK_3),
	stateEquation(VMax, MK_4, MK_5, VPK_4, VTK_4),
	markedGraph(MK_4, VPK_4),
	stateEquation(VMax, MK_5, MK_6, VPK_5, VTK_5),
	markedGraph(MK_5, VPK_5),
	stateEquation(VMax, MK_6, MK_7, VPK_6, VTK_6),
	markedGraph(MK_6, VPK_6),
	stateEquation(VMax, MK_7, MK_8, VPK_7, VTK_7),
	markedGraph(MK_7, VPK_7),
	stateEquation(VMax, MK_8, MK_9, VPK_8, VTK_8),
	markedGraph(MK_8, VPK_8),
	stateEquation(VMax, MK_9, MK_10, VPK_9, VTK_9),
	markedGraph(MK_9, VPK_9).

underApproximation(VMax, [MK_0, MK_1, MK_2, MK_3, MK_4, MK_5, MK_6, MK_7, MK_8, MK_9, MK_10], [VPK_0, VPK_1, VPK_2, VPK_3, VPK_4, VPK_5, VPK_6, VPK_7, VPK_8, VPK_9], [VTK_0, VTK_1, VTK_2, VTK_3, VTK_4, VTK_5, VTK_6, VTK_7, VTK_8, VTK_9]):-
	initialMarking(MK_0),
	finalMarking(MK_10),
	stateEquation(VMax, MK_0, MK_1, VPK_0, VTK_0),
	markedGraph(MK_0, VPK_0),
	noSiphon(MK_0, MK_1, VPK_0, VTK_0),
	stateEquation(VMax, MK_1, MK_2, VPK_1, VTK_1),
	markedGraph(MK_1, VPK_1),
	noSiphon(MK_1, MK_2, VPK_1, VTK_1),
	stateEquation(VMax, MK_2, MK_3, VPK_2, VTK_2),
	markedGraph(MK_2, VPK_2),
	noSiphon(MK_2, MK_3, VPK_2, VTK_2),
	stateEquation(VMax, MK_3, MK_4, VPK_3, VTK_3),
	markedGraph(MK_3, VPK_3),
	noSiphon(MK_3, MK_4, VPK_3, VTK_3),
	stateEquation(VMax, MK_4, MK_5, VPK_4, VTK_4),
	markedGraph(MK_4, VPK_4),
	noSiphon(MK_4, MK_5, VPK_4, VTK_4),
	stateEquation(VMax, MK_5, MK_6, VPK_5, VTK_5),
	markedGraph(MK_5, VPK_5),
	noSiphon(MK_5, MK_6, VPK_5, VTK_5),
	stateEquation(VMax, MK_6, MK_7, VPK_6, VTK_6),
	markedGraph(MK_6, VPK_6),
	noSiphon(MK_6, MK_7, VPK_6, VTK_6),
	stateEquation(VMax, MK_7, MK_8, VPK_7, VTK_7),
	markedGraph(MK_7, VPK_7),
	noSiphon(MK_7, MK_8, VPK_7, VTK_7),
	stateEquation(VMax, MK_8, MK_9, VPK_8, VTK_8),
	markedGraph(MK_8, VPK_8),
	noSiphon(MK_8, MK_9, VPK_8, VTK_8),
	stateEquation(VMax, MK_9, MK_10, VPK_9, VTK_9),
	markedGraph(MK_9, VPK_9),
	noSiphon(MK_9, MK_10, VPK_9, VTK_9).

