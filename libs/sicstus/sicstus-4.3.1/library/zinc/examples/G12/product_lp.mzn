% RUNS ON mzn_mer_lp
% RUNS ON mzn_mer_lp
% RUNS ON mzn_cd_lp
% RUNS ON mzn_fdlp
% RUNS ON mzn_mer_fdlp
% RUNS ON mzn_cd_fdlp
% RUNS ON zinc_mip_hl
% RUNS ON minizinc_fd
%% Product example from the OPL book!
%% product_lp.mzn
%% Ralph Becket June 26 2007
%%
%% This is a version of product.mzn changed to use LP integers rather than
%% LP floats (everything has been scaled up by 10).

% This isn't needed, as "lp" is a built-in annotation in the G12
% implementation (although it's not listed as such in the Zinc spec).
%annotation lp;

%enum Products  = {kluski, capellini, fettucine};
int: NumProducts = 3;
set of int: Products = 1..NumProducts;
int: kluski = 0;
int: capellini = 1;
int: fettucine = 2;

%enum Resources = {flour, eggs};
int: NumResources = 2;
set of int: Resources = 1..NumResources;
int: flour = 0;
int: eggs = 1;

array[Products] of int: demand = [100, 200, 300];
array[Products] of int: insideCost = [60, 80, 30];
array[Products] of int: outsideCost = [80, 90, 40];
array[Products, Resources] of int: consumption =
	[| 5, 2
	 | 4, 4
	 | 3, 6
	 |];

array[Resources] of int: capacity = [200, 400];

array[Products] of var int: inside;
array[Products] of var int: outside;

constraint
	forall (p in Products) (
		inside[p]  >= 0
	/\	outside[p] >= 0
	);

constraint   
	forall(r in Resources) (
		sum (p in Products) (
                    consumption[p, r] * inside[p]
                )
		<=
                capacity[r]
	);

constraint 
	forall(p in Products) (
		inside[p] + outside[p] >= demand[p]
	);

solve minimize
	sum (p in Products) (
		insideCost[p]*inside[p] + outsideCost[p]*outside[p]
	);

output [
	"production planning (LP version of integer model)\n",
	"             \tkluski\t\tfettucine\tcapellini\n",
	"make inside: \t",
	show(inside[1]), "\t\t",
	show(inside[2]), "\t\t",
	show(inside[3]), "\n",
	"make outside: \t",
	show(outside[1]), "\t\t",
	show(outside[2]), "\t\t",
	show(outside[3]), "\n"
];
