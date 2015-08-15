

(define-fun initialMarking 
((MA_i Int) (MA_o Int) (MA_P2 Int) (MA_P3 Int) (MA_P4 Int) (MA_P5 Int) (MA_P6 Int) (MA_P7 Int))
Bool
(and (= MA_i 1) (= MA_o 0) (= MA_P2 0) (= MA_P3 0) (= MA_P4 0) (= MA_P5 0) (= MA_P6 0) (= MA_P7 0))
)

(define-fun finalMarking 
((MB_i Int) (MB_o Int) (MB_P2 Int) (MB_P3 Int) (MB_P4 Int) (MB_P5 Int) (MB_P6 Int) (MB_P7 Int))
Bool
(and (= MB_o 1) (= MB_i 0) (= MB_P2 0) (= MB_P3 0) (= MB_P4 0) (= MB_P5 0) (= MB_P6 0) (= MB_P7 0))
)

(define-fun stateEquation 
((VMax Int) (MA_i Int) (MA_o Int) (MA_P2 Int) (MA_P3 Int) (MA_P4 Int) (MA_P5 Int) (MA_P6 Int) (MA_P7 Int) (MB_i Int) (MB_o Int) (MB_P2 Int) (MB_P3 Int) (MB_P4 Int) (MB_P5 Int) (MB_P6 Int) (MB_P7 Int) (VP_i Int) (VP_o Int) (VP_P2 Int) (VP_P3 Int) (VP_P4 Int) (VP_P5 Int) (VP_P6 Int) (VP_P7 Int) (VT_consulter Int) (VT_deconnecter Int) (VT_mdp Int) (VT_pseudo Int) (VT_split Int) (VT_valider Int))
Bool
(and (>= MA_i 0) (>= MB_i 0) (and (>= VP_i 0) (<= VP_i VMax)) (>= MA_o 0) (>= MB_o 0) (and (>= VP_o 0) (<= VP_o VMax)) (>= MA_P2 0) (>= MB_P2 0) (and (>= VP_P2 0) (<= VP_P2 VMax)) (>= MA_P3 0) (>= MB_P3 0) (and (>= VP_P3 0) (<= VP_P3 VMax)) (>= MA_P4 0) (>= MB_P4 0) (and (>= VP_P4 0) (<= VP_P4 VMax)) (>= MA_P5 0) (>= MB_P5 0) (and (>= VP_P5 0) (<= VP_P5 VMax)) (>= MA_P6 0) (>= MB_P6 0) (and (>= VP_P6 0) (<= VP_P6 VMax)) (>= MA_P7 0) (>= MB_P7 0) (and (>= VP_P7 0) (<= VP_P7 VMax)) (and (>= VT_consulter 0) (<= VT_consulter VMax)) (and (>= VT_deconnecter 0) (<= VT_deconnecter VMax)) (and (>= VT_mdp 0) (<= VT_mdp VMax)) (and (>= VT_pseudo 0) (<= VT_pseudo VMax)) (and (>= VT_split 0) (<= VT_split VMax)) (and (>= VT_valider 0) (<= VT_valider VMax)) (= (+ MA_i) VP_i) (= (+ MB_i VT_split) VP_i) (= (+ MA_o VT_deconnecter) VP_o) (= (+ MB_o) VP_o) (= (+ MA_P2 VT_split) VP_P2) (= (+ MB_P2 VT_pseudo) VP_P2) (= (+ MA_P3 VT_split) VP_P3) (= (+ MB_P3 VT_mdp) VP_P3) (= (+ MA_P4 VT_pseudo) VP_P4) (= (+ MB_P4 VT_valider) VP_P4) (= (+ MA_P5 VT_mdp) VP_P5) (= (+ MB_P5 VT_valider) VP_P5) (= (+ MA_P6 VT_valider) VP_P6) (= (+ MB_P6 VT_deconnecter) VP_P6) (= (+ MA_P7 VT_consulter VT_valider) VP_P7) (= (+ MB_P7 VT_consulter VT_deconnecter) VP_P7))
)

(define-fun formula 
((VT_consulter Int) (VT_deconnecter Int) (VT_mdp Int) (VT_pseudo Int) (VT_split Int) (VT_valider Int))
Bool
(and (not (and (> VT_split 0) (> VT_pseudo 0) (> VT_mdp 0) (> VT_valider 0) (> VT_consulter 0) (> VT_deconnecter 0))))
)

(define-fun noSiphon 
((MA_i Int) (MA_o Int) (MA_P2 Int) (MA_P3 Int) (MA_P4 Int) (MA_P5 Int) (MA_P6 Int) (MA_P7 Int) (MB_i Int) (MB_o Int) (MB_P2 Int) (MB_P3 Int) (MB_P4 Int) (MB_P5 Int) (MB_P6 Int) (MB_P7 Int) (VP_i Int) (VP_o Int) (VP_P2 Int) (VP_P3 Int) (VP_P4 Int) (VP_P5 Int) (VP_P6 Int) (VP_P7 Int) (VT_consulter Int) (VT_deconnecter Int) (VT_mdp Int) (VT_pseudo Int) (VT_split Int) (VT_valider Int))
Bool
(and (not (exists 
((XI_i Int) (XI_o Int) (XI_P2 Int) (XI_P3 Int) (XI_P4 Int) (XI_P5 Int) (XI_P6 Int) (XI_P7 Int))
(and (and (> (+ XI_i XI_o XI_P2 XI_P3 XI_P4 XI_P5 XI_P6 XI_P7) 0) (and (>= XI_i 0) (<= XI_i 1)) (and (>= XI_o 0) (<= XI_o 1)) (and (>= XI_P2 0) (<= XI_P2 1)) (and (>= XI_P3 0) (<= XI_P3 1)) (and (>= XI_P4 0) (<= XI_P4 1)) (and (>= XI_P5 0) (<= XI_P5 1)) (and (>= XI_P6 0) (<= XI_P6 1)) (and (>= XI_P7 0) (<= XI_P7 1)) (=> (or (> MA_i 0) (> MB_i 0) (= VP_i 0)) (= XI_i 0)) (=> (or (> MA_o 0) (> MB_o 0) (= VP_o 0)) (= XI_o 0)) (=> (or (> MA_P2 0) (> MB_P2 0) (= VP_P2 0)) (= XI_P2 0)) (=> (or (> MA_P3 0) (> MB_P3 0) (= VP_P3 0)) (= XI_P3 0)) (=> (or (> MA_P4 0) (> MB_P4 0) (= VP_P4 0)) (= XI_P4 0)) (=> (or (> MA_P5 0) (> MB_P5 0) (= VP_P5 0)) (= XI_P5 0)) (=> (or (> MA_P6 0) (> MB_P6 0) (= VP_P6 0)) (= XI_P6 0)) (=> (or (> MA_P7 0) (> MB_P7 0) (= VP_P7 0)) (= XI_P7 0)) (=> (>= VT_consulter 0) (and (>= (+ XI_P7) XI_P7))) (=> (>= VT_deconnecter 0) (and (>= (+ XI_P6 XI_P7) XI_o))) (=> (>= VT_mdp 0) (and (>= (+ XI_P3) XI_P5))) (=> (>= VT_pseudo 0) (and (>= (+ XI_P2) XI_P4))) (=> (>= VT_split 0) (and (>= (+ XI_i) XI_P2) (>= (+ XI_i) XI_P3))) (=> (>= VT_valider 0) (and (>= (+ XI_P4 XI_P5) XI_P6) (>= (+ XI_P4 XI_P5) XI_P7)))))
)))
)

(define-fun markedGraph 
((MA_i Int) (MA_o Int) (MA_P2 Int) (MA_P3 Int) (MA_P4 Int) (MA_P5 Int) (MA_P6 Int) (MA_P7 Int) (VP_i Int) (VP_o Int) (VP_P2 Int) (VP_P3 Int) (VP_P4 Int) (VP_P5 Int) (VP_P6 Int) (VP_P7 Int))
Bool
(and (<= (- VP_i MA_i) 1) (<= (- VP_o MA_o) 1) (<= (- VP_P2 MA_P2) 1) (<= (- VP_P3 MA_P3) 1) (<= (- VP_P4 MA_P4) 1) (<= (- VP_P5 MA_P5) 1) (<= (- VP_P6 MA_P6) 1) (<= (- VP_P7 MA_P7) 1))
)

(define-fun overApproximation1 
((VMax Int) (MA_i Int) (MA_o Int) (MA_P2 Int) (MA_P3 Int) (MA_P4 Int) (MA_P5 Int) (MA_P6 Int) (MA_P7 Int) (MB_i Int) (MB_o Int) (MB_P2 Int) (MB_P3 Int) (MB_P4 Int) (MB_P5 Int) (MB_P6 Int) (MB_P7 Int) (VP_i Int) (VP_o Int) (VP_P2 Int) (VP_P3 Int) (VP_P4 Int) (VP_P5 Int) (VP_P6 Int) (VP_P7 Int) (VT_consulter Int) (VT_deconnecter Int) (VT_mdp Int) (VT_pseudo Int) (VT_split Int) (VT_valider Int))
Bool
(and (initialMarking MA_i MA_o MA_P2 MA_P3 MA_P4 MA_P5 MA_P6 MA_P7) (finalMarking MB_i MB_o MB_P2 MB_P3 MB_P4 MB_P5 MB_P6 MB_P7) (stateEquation VMax MA_i MA_o MA_P2 MA_P3 MA_P4 MA_P5 MA_P6 MA_P7 MB_i MB_o MB_P2 MB_P3 MB_P4 MB_P5 MB_P6 MB_P7 VP_i VP_o VP_P2 VP_P3 VP_P4 VP_P5 VP_P6 VP_P7 VT_consulter VT_deconnecter VT_mdp VT_pseudo VT_split VT_valider) (formula VT_consulter VT_deconnecter VT_mdp VT_pseudo VT_split VT_valider))
)

(define-fun overApproximation2 
((VMax Int) (MA_i Int) (MA_o Int) (MA_P2 Int) (MA_P3 Int) (MA_P4 Int) (MA_P5 Int) (MA_P6 Int) (MA_P7 Int) (MB_i Int) (MB_o Int) (MB_P2 Int) (MB_P3 Int) (MB_P4 Int) (MB_P5 Int) (MB_P6 Int) (MB_P7 Int) (VP_i Int) (VP_o Int) (VP_P2 Int) (VP_P3 Int) (VP_P4 Int) (VP_P5 Int) (VP_P6 Int) (VP_P7 Int) (VT_consulter Int) (VT_deconnecter Int) (VT_mdp Int) (VT_pseudo Int) (VT_split Int) (VT_valider Int))
Bool
(and (initialMarking MA_i MA_o MA_P2 MA_P3 MA_P4 MA_P5 MA_P6 MA_P7) (finalMarking MB_i MB_o MB_P2 MB_P3 MB_P4 MB_P5 MB_P6 MB_P7) (stateEquation VMax MA_i MA_o MA_P2 MA_P3 MA_P4 MA_P5 MA_P6 MA_P7 MB_i MB_o MB_P2 MB_P3 MB_P4 MB_P5 MB_P6 MB_P7 VP_i VP_o VP_P2 VP_P3 VP_P4 VP_P5 VP_P6 VP_P7 VT_consulter VT_deconnecter VT_mdp VT_pseudo VT_split VT_valider) (formula VT_consulter VT_deconnecter VT_mdp VT_pseudo VT_split VT_valider) (noSiphon MA_i MA_o MA_P2 MA_P3 MA_P4 MA_P5 MA_P6 MA_P7 MB_i MB_o MB_P2 MB_P3 MB_P4 MB_P5 MB_P6 MB_P7 VP_i VP_o VP_P2 VP_P3 VP_P4 VP_P5 VP_P6 VP_P7 VT_consulter VT_deconnecter VT_mdp VT_pseudo VT_split VT_valider))
)

(define-fun overApproximation3 
((VMax Int) (MK_0_i Int) (MK_0_o Int) (MK_0_P2 Int) (MK_0_P3 Int) (MK_0_P4 Int) (MK_0_P5 Int) (MK_0_P6 Int) (MK_0_P7 Int) (MK_1_i Int) (MK_1_o Int) (MK_1_P2 Int) (MK_1_P3 Int) (MK_1_P4 Int) (MK_1_P5 Int) (MK_1_P6 Int) (MK_1_P7 Int) (VPK_1_i Int) (VPK_1_o Int) (VPK_1_P2 Int) (VPK_1_P3 Int) (VPK_1_P4 Int) (VPK_1_P5 Int) (VPK_1_P6 Int) (VPK_1_P7 Int) (VTK_1_consulter Int) (VTK_1_deconnecter Int) (VTK_1_mdp Int) (VTK_1_pseudo Int) (VTK_1_split Int) (VTK_1_valider Int) (MK_2_i Int) (MK_2_o Int) (MK_2_P2 Int) (MK_2_P3 Int) (MK_2_P4 Int) (MK_2_P5 Int) (MK_2_P6 Int) (MK_2_P7 Int) (VPK_2_i Int) (VPK_2_o Int) (VPK_2_P2 Int) (VPK_2_P3 Int) (VPK_2_P4 Int) (VPK_2_P5 Int) (VPK_2_P6 Int) (VPK_2_P7 Int) (VTK_2_consulter Int) (VTK_2_deconnecter Int) (VTK_2_mdp Int) (VTK_2_pseudo Int) (VTK_2_split Int) (VTK_2_valider Int))
Bool
(and (initialMarking MK_0_i MK_0_o MK_0_P2 MK_0_P3 MK_0_P4 MK_0_P5 MK_0_P6 MK_0_P7) (finalMarking MK_2_i MK_2_o MK_2_P2 MK_2_P3 MK_2_P4 MK_2_P5 MK_2_P6 MK_2_P7) (stateEquation VMax MK_0_i MK_0_o MK_0_P2 MK_0_P3 MK_0_P4 MK_0_P5 MK_0_P6 MK_0_P7 MK_1_i MK_1_o MK_1_P2 MK_1_P3 MK_1_P4 MK_1_P5 MK_1_P6 MK_1_P7 VPK_1_i VPK_1_o VPK_1_P2 VPK_1_P3 VPK_1_P4 VPK_1_P5 VPK_1_P6 VPK_1_P7 VTK_1_consulter VTK_1_deconnecter VTK_1_mdp VTK_1_pseudo VTK_1_split VTK_1_valider) (markedGraph MK_0_i MK_0_o MK_0_P2 MK_0_P3 MK_0_P4 MK_0_P5 MK_0_P6 MK_0_P7 VPK_1_i VPK_1_o VPK_1_P2 VPK_1_P3 VPK_1_P4 VPK_1_P5 VPK_1_P6 VPK_1_P7) (stateEquation VMax MK_1_i MK_1_o MK_1_P2 MK_1_P3 MK_1_P4 MK_1_P5 MK_1_P6 MK_1_P7 MK_2_i MK_2_o MK_2_P2 MK_2_P3 MK_2_P4 MK_2_P5 MK_2_P6 MK_2_P7 VPK_2_i VPK_2_o VPK_2_P2 VPK_2_P3 VPK_2_P4 VPK_2_P5 VPK_2_P6 VPK_2_P7 VTK_2_consulter VTK_2_deconnecter VTK_2_mdp VTK_2_pseudo VTK_2_split VTK_2_valider) (markedGraph MK_1_i MK_1_o MK_1_P2 MK_1_P3 MK_1_P4 MK_1_P5 MK_1_P6 MK_1_P7 VPK_2_i VPK_2_o VPK_2_P2 VPK_2_P3 VPK_2_P4 VPK_2_P5 VPK_2_P6 VPK_2_P7))
)

(define-fun underApproximation 
((VMax Int) (MK_0_i Int) (MK_0_o Int) (MK_0_P2 Int) (MK_0_P3 Int) (MK_0_P4 Int) (MK_0_P5 Int) (MK_0_P6 Int) (MK_0_P7 Int) (MK_1_i Int) (MK_1_o Int) (MK_1_P2 Int) (MK_1_P3 Int) (MK_1_P4 Int) (MK_1_P5 Int) (MK_1_P6 Int) (MK_1_P7 Int) (VPK_1_i Int) (VPK_1_o Int) (VPK_1_P2 Int) (VPK_1_P3 Int) (VPK_1_P4 Int) (VPK_1_P5 Int) (VPK_1_P6 Int) (VPK_1_P7 Int) (VTK_1_consulter Int) (VTK_1_deconnecter Int) (VTK_1_mdp Int) (VTK_1_pseudo Int) (VTK_1_split Int) (VTK_1_valider Int) (MK_2_i Int) (MK_2_o Int) (MK_2_P2 Int) (MK_2_P3 Int) (MK_2_P4 Int) (MK_2_P5 Int) (MK_2_P6 Int) (MK_2_P7 Int) (VPK_2_i Int) (VPK_2_o Int) (VPK_2_P2 Int) (VPK_2_P3 Int) (VPK_2_P4 Int) (VPK_2_P5 Int) (VPK_2_P6 Int) (VPK_2_P7 Int) (VTK_2_consulter Int) (VTK_2_deconnecter Int) (VTK_2_mdp Int) (VTK_2_pseudo Int) (VTK_2_split Int) (VTK_2_valider Int))
Bool
(and (initialMarking MK_0_i MK_0_o MK_0_P2 MK_0_P3 MK_0_P4 MK_0_P5 MK_0_P6 MK_0_P7) (finalMarking MK_2_i MK_2_o MK_2_P2 MK_2_P3 MK_2_P4 MK_2_P5 MK_2_P6 MK_2_P7) (stateEquation VMax MK_0_i MK_0_o MK_0_P2 MK_0_P3 MK_0_P4 MK_0_P5 MK_0_P6 MK_0_P7 MK_1_i MK_1_o MK_1_P2 MK_1_P3 MK_1_P4 MK_1_P5 MK_1_P6 MK_1_P7 VPK_1_i VPK_1_o VPK_1_P2 VPK_1_P3 VPK_1_P4 VPK_1_P5 VPK_1_P6 VPK_1_P7 VTK_1_consulter VTK_1_deconnecter VTK_1_mdp VTK_1_pseudo VTK_1_split VTK_1_valider) (markedGraph MK_0_i MK_0_o MK_0_P2 MK_0_P3 MK_0_P4 MK_0_P5 MK_0_P6 MK_0_P7 VPK_1_i VPK_1_o VPK_1_P2 VPK_1_P3 VPK_1_P4 VPK_1_P5 VPK_1_P6 VPK_1_P7) (noSiphon MK_0_i MK_0_o MK_0_P2 MK_0_P3 MK_0_P4 MK_0_P5 MK_0_P6 MK_0_P7 MK_1_i MK_1_o MK_1_P2 MK_1_P3 MK_1_P4 MK_1_P5 MK_1_P6 MK_1_P7 VPK_1_i VPK_1_o VPK_1_P2 VPK_1_P3 VPK_1_P4 VPK_1_P5 VPK_1_P6 VPK_1_P7 VTK_1_consulter VTK_1_deconnecter VTK_1_mdp VTK_1_pseudo VTK_1_split VTK_1_valider) (stateEquation VMax MK_1_i MK_1_o MK_1_P2 MK_1_P3 MK_1_P4 MK_1_P5 MK_1_P6 MK_1_P7 MK_2_i MK_2_o MK_2_P2 MK_2_P3 MK_2_P4 MK_2_P5 MK_2_P6 MK_2_P7 VPK_2_i VPK_2_o VPK_2_P2 VPK_2_P3 VPK_2_P4 VPK_2_P5 VPK_2_P6 VPK_2_P7 VTK_2_consulter VTK_2_deconnecter VTK_2_mdp VTK_2_pseudo VTK_2_split VTK_2_valider) (markedGraph MK_1_i MK_1_o MK_1_P2 MK_1_P3 MK_1_P4 MK_1_P5 MK_1_P6 MK_1_P7 VPK_2_i VPK_2_o VPK_2_P2 VPK_2_P3 VPK_2_P4 VPK_2_P5 VPK_2_P6 VPK_2_P7) (noSiphon MK_1_i MK_1_o MK_1_P2 MK_1_P3 MK_1_P4 MK_1_P5 MK_1_P6 MK_1_P7 MK_2_i MK_2_o MK_2_P2 MK_2_P3 MK_2_P4 MK_2_P5 MK_2_P6 MK_2_P7 VPK_2_i VPK_2_o VPK_2_P2 VPK_2_P3 VPK_2_P4 VPK_2_P5 VPK_2_P6 VPK_2_P7 VTK_2_consulter VTK_2_deconnecter VTK_2_mdp VTK_2_pseudo VTK_2_split VTK_2_valider))
)

