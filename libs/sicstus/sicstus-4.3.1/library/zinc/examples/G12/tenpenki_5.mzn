% RUNS ON mzn_fd
% RUNS ON mzn_mer_fd
% RUNS ON mzn_mer_lazyfd
% RUNS ON mzn_mer_lp
% RUNS ON mzn_cd_fd
% RUNS ON zinc_fdic_mznlib
% RUNS ON zinc_mip_hl
% RUNS ON minizinc_fd
include "tenpenki.mzn.model";

nrows = 15;
ncols = 15;

constraint row_constraint( 1, [4, 4]);
constraint row_constraint( 2, [1, 3, 1]);
constraint row_constraint( 3, [2, 3, 2]);
constraint row_constraint( 4, [11]);
constraint row_constraint( 5, [12]);
constraint row_constraint( 6, [2, 3, 3]);
constraint row_constraint( 7, [11]);
constraint row_constraint( 8, [3, 3, 1]);
constraint row_constraint( 9, [1, 1, 1, 1]);
constraint row_constraint(10, [1, 1, 1, 1]);
constraint row_constraint(11, [1, 1, 1, 1]);
constraint row_constraint(12, [3]);
constraint row_constraint(13, [2]);
constraint row_constraint(14, [2]);
constraint row_constraint(15, [2]);

constraint col_constraint( 1, []);
constraint col_constraint( 2, []);
constraint col_constraint( 3, [4]);
constraint col_constraint( 4, [7]);
constraint col_constraint( 5, [1, 2, 2]);
constraint col_constraint( 6, [1, 2, 5]);
constraint col_constraint( 7, [8]);
constraint col_constraint( 8, [6]);
constraint col_constraint( 9, [7]);
constraint col_constraint(10, [1, 2, 5]);
constraint col_constraint(11, [1, 2, 2]);
constraint col_constraint(12, [12]);
constraint col_constraint(13, [5, 4]);
constraint col_constraint(14, [11]);
constraint col_constraint(15, []);
