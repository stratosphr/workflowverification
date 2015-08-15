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
	(#\ ((VT_split #> 0) #/\ (VT_pseudo #> 0) #/\ (VT_mdp #> 0) #/\ (VT_valider #> 0) #/\ (VT_consulter #> 0) #/\ (VT_deconnecter #> 0))).

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

overApproximation3(VMax, [MK_0, MK_1, MK_2], [VPK_1, VPK_2], [VTK_1, VTK_2]):-
	initialMarking(MK_0),
	finalMarking(MK_2),
	stateEquation(VMax, MK_0, MK_1, VPK_1, VTK_1),
	markedGraph(MK_0, VPK_1),
	stateEquation(VMax, MK_1, MK_2, VPK_2, VTK_2),
	markedGraph(MK_1, VPK_2),
	labeling([], VTK_1),
	labeling([], VTK_2),
	labeling([], VPK_1),
	labeling([], VPK_2).

underApproximation(VMax, [MK_0, MK_1, MK_2], [VPK_1, VPK_2], [VTK_1, VTK_2]):-
	initialMarking(MK_0),
	finalMarking(MK_2),
	stateEquation(VMax, MK_0, MK_1, VPK_1, VTK_1),
	markedGraph(MK_0, VPK_1),
	noSiphon(MK_0, MK_1, VPK_1, VTK_1),
	stateEquation(VMax, MK_1, MK_2, VPK_2, VTK_2),
	markedGraph(MK_1, VPK_2),
	noSiphon(MK_1, MK_2, VPK_2, VTK_2),
	labeling([], VTK_1),
	labeling([], VTK_2),
	labeling([], VPK_1),
	labeling([], VPK_2).

