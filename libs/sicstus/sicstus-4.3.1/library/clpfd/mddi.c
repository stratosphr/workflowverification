#include "fd.h"
#include "dvars.h"

/* 1 if avoiding to scan dead edges */
#define OPT_EDGESCAN 0


#define DVAR(R,C) (pdata->dvar+nvars*(R)+(C))
#define ATTRIBUTE_LOC(R,C) (pdata->refbase + 2*nvars*(R) + 2*(C))

struct mddi_data {
  void (SPCDECL *destructor)(void*);
  void (SPCDECL *daemon)(Wam,void *,SP_globref,TAGGED); /* (wam,handle,attr_ref,global) */
  SPEnv *spenv;
  SP_globref refbase;
  SP_integer stamp;  /* increases up to backtracking */
  SP_integer first;  /* head of (-1)-terminated list of queued tuples */
  SP_integer *next;  /* [ntuples] next[i]==(-2) iff not in the queue */
  int nrefs;
  int nvars;			/* arg 1 of state */
  int nnodes;			/* arg 2 of state */
  int nedges;			/* |arg 3 of state| */
  int nvarvals;			/* |arg 4 of state| */
  int nlinles;			/* |arg 5 of state| */
  int ntuples;			/* |arg 6 of state| */
  int trail_top;
  int watch;		       /* which entailment flag to watch */
  Dvar dvar;
  unsigned char **trail;	 /* [(nedges+1+nvars+2*nlinles)*ntuples] */
  struct tuple_data *tuples;	/* [ntuples] */
  unsigned char *entailed;	/* [ntuples] */
  struct {
    SP_integer *var;		/* [nnodes+1, unused?] */
    SP_integer *in;		/* [nnodes+1] */
    SP_integer *out;		/* [nnodes+1] */
  } node;
  struct {
    SP_integer *source;		/* [nedges], source node */
    SP_integer *dest;		/* [nedges], destination node */
    SP_integer *varval;		/* [nedges], unique (var,val) combination, increasing */
    SP_integer *in;		/* [nedges] */
    SP_integer *out;		/* [nedges] */
    SP_integer *chunk;		/* [nvars] -- index of first edge for var */
  } edge;
  struct {
    SP_integer *var;		/* [nvarvals] */
    TAGGED *min;		/* [nvarvals], arg 4 of state */
    TAGGED *max;		/* [nvarvals], arg 4 of state */
    SP_integer *edges;		/* [nvarvals] */
    SP_integer *chunk;		/* [nvars] -- index of first varval for var */
  } varval;
  struct {
    SP_integer *lhs;		/* [nlinles*nvars] coefficient[linle,i] */
    SP_integer *lhsc;		/* [nlinles*nvars] coefficient[linle,lhsi[linle,i]], nonzero */
    SP_integer *lhsi;		/* [nlinles*nvars] */
    SP_integer *lhsn;		/* [nlinles] #nonzero coefficients */
    SP_integer *rhs;		/* [nlinles] */
    SP_integer *edge;		/* [nlinles] */
    SP_integer *chunk;		/* [nedges] */
  } linle;
};

struct tuple_data {
  SP_integer *killed_from_above; /* [nedges] */
  SP_integer *killed_from_below; /* [nedges] */
  SP_integer *killed;		 /* [nvarvals] */
  int nkfa, nkfb, nk;
  SP_BOOL active;		/* FALSE means for sure no propagating linle */
  struct {
    SP_integer *watch_in;	/* [nnodes+1], watched edge */
    SP_integer *watch_out;	/* [nnodes+1], watched edge */
  } node;
  struct {
    unsigned char *status;	/* [nedges] */
#if OPT_EDGESCAN
    SP_integer *prev;		/* [nedges+nvars] */
    SP_integer *next;		/* [nedges+nvars] */
#endif
  } edge;
  struct {
    SP_integer *support;	/* [nvarvals], watched edge */
  } varval;
  struct {
    int watch;			/* entailed[watch] is watched */
    unsigned char *entailed;	/* [nlinles] */
    unsigned char *active;	/* [nlinles] */
  } linle;
  struct {
    SP_integer *watch1;		/* [nvars], watched edge */
    SP_integer *watch2;		/* [nvars], watched edge */
  } isthmus;
  struct {
    unsigned char *status;	 /* [nvars] */
    SP_integer watch1;		/* watched var */
    SP_integer watch2;		/* watched var */
  } nonground;
};

#define EDGE_ALIVE 0x3
#define EDGE_KILLED_ABOVE 0x2
#define EDGE_KILLED_BELOW 0x1
#define EDGE_KILLED_VALUE 0x0
#define EDGE_WATCHED_ABOVE 0x4	/* aka. begin, support for node above */
#define EDGE_WATCHED_BELOW 0x8	/* aka. end, support for node below */
#define EDGE_WATCHED_VALUE 0x10	/* aka. val, support for varval */
#define EDGE_WATCHED_ISTHMUS 0x20 /* support for level */

#define TRAIL_DEC(VAR,DECR)			\
  SP_ASSERT((VAR) & EDGE_ALIVE);		\
  (VAR) = (unsigned char)((VAR) - (DECR));	\
  pdata->trail[pdata->trail_top++] = &(VAR);	\

#if OPT_EDGESCAN

#define TRAIL_DEC_EDGE(VAR,DECR,EDGE)		\
  TRAIL_DEC(VAR,DECR)				\
  {						\
    int prev = tdata->edge.prev[EDGE];		\
    int next = tdata->edge.next[EDGE];		\
    tdata->edge.next[prev] = next;		\
    tdata->edge.prev[next] = prev;		\
  }						\

#else

#define TRAIL_DEC_EDGE(VAR,DECR,EDGE)		\
  TRAIL_DEC(VAR,DECR)				\

#endif


static void SPCDECL mddi_destructor(void *pdata_v)
{
  struct mddi_data *pdata = (struct mddi_data *)pdata_v;
  FD_SETUP_SPENV(pdata->spenv);

  SP_free_globrefs(pdata->refbase,pdata->nrefs);
  SP_free(pdata);
}

/* watched edge was killed---do we have a committed isthmus? */
static void maintain_isthmus(Wam wam,
			     struct mddi_data *pdata,
			     struct tuple_data *tdata,
			     SP_integer edge)
{
  SP_integer var = pdata->varval.var[pdata->edge.varval[edge]];
  SP_integer first = pdata->edge.chunk[var];
  SP_integer next = (var+1 < pdata->nvars ? pdata->edge.chunk[var+1] : pdata->nedges);
  SP_integer i;
  (void)wam;

  for (i=first; i<next; i++) {
    if ((tdata->edge.status[i] & (EDGE_ALIVE|EDGE_WATCHED_ISTHMUS)) == EDGE_ALIVE) {
      // tdata->edge.status[edge] -= (unsigned char)EDGE_WATCHED_ISTHMUS;
      tdata->edge.status[edge] = (unsigned char)
	(tdata->edge.status[edge] - EDGE_WATCHED_ISTHMUS);
      tdata->edge.status[i] = (unsigned char)
	(tdata->edge.status[i] + EDGE_WATCHED_ISTHMUS);
      if (tdata->isthmus.watch1[var]==edge)
	tdata->isthmus.watch1[var] = i;
      if (tdata->isthmus.watch2[var]==edge)
	tdata->isthmus.watch2[var] = i;
      return;
    }
  }
  /* no hit - commit remaining edge, if not done already */
  edge = tdata->isthmus.watch1[var] + tdata->isthmus.watch2[var] - edge;
  first = pdata->linle.chunk[edge];
  next = (edge+1 < pdata->nedges ? pdata->linle.chunk[edge+1] : pdata->nlinles);
  for (i=first; i<next; i++) {
    if (tdata->linle.active[i] & EDGE_ALIVE) {
      TRAIL_DEC(tdata->linle.active[i],EDGE_ALIVE);
      tdata->linle.active[i] |= EDGE_WATCHED_ISTHMUS; /* [MC] 4.3.0 */
      tdata->active = TRUE;
    }
  }
}

/* the given variable of the given tuple is ground */
static void maintain_entailment(Wam wam,
				struct mddi_data *pdata,
				struct tuple_data *tdata,
				SP_integer var)
{
  int tuple = (int)(tdata - pdata->tuples);
  int ntuples = pdata->ntuples;
  int nvars = pdata->nvars;
  int i;
  (void)wam;
  
  if (var >= 0) {
    if (tdata->nonground.status[var] & EDGE_ALIVE) {
      TRAIL_DEC(tdata->nonground.status[var],EDGE_ALIVE);
    }
    if (!(tdata->nonground.status[var] & EDGE_WATCHED_VALUE))
      return;
    for (i=0; i<nvars; i++) {
      if ((tdata->nonground.status[i] & (EDGE_ALIVE|EDGE_WATCHED_VALUE)) == EDGE_ALIVE) {
	tdata->nonground.status[var] = (unsigned char)
	  (tdata->nonground.status[var] - EDGE_WATCHED_VALUE);
	tdata->nonground.status[i] = (unsigned char)
	  (tdata->nonground.status[i] + EDGE_WATCHED_VALUE);
	if (tdata->nonground.watch1==var)
	  tdata->nonground.watch1 = i;
	if (tdata->nonground.watch2==var)
	  tdata->nonground.watch2 = i;
	return;
      }
    }
  }
  if ((tdata->nonground.status[tdata->nonground.watch1] & EDGE_ALIVE) &&
      (tdata->nonground.status[tdata->nonground.watch2] & EDGE_ALIVE))
    return;
  /* here, at most one var is nonground */
  if (pdata->nlinles>0 &&
      (tdata->linle.entailed[tdata->linle.watch] & EDGE_ALIVE) == EDGE_ALIVE)
    return;
  /* here, all linles are (dis)entailed */
  if ((pdata->entailed[tuple] & 0x3) == EDGE_ALIVE) { /* can be called from entail_linle(wam, ) + kill_all_support(wam, ) */
    TRAIL_DEC(pdata->entailed[tuple],EDGE_ALIVE);
    if (pdata->entailed[tuple] & EDGE_WATCHED_VALUE) {
      for (i=0; i<ntuples; i++) {
	if ((pdata->entailed[i] & 0x3) == EDGE_ALIVE) {
	  pdata->entailed[tuple] = (unsigned char)
	    (pdata->entailed[tuple] - EDGE_WATCHED_VALUE);
	  pdata->entailed[i] = (unsigned char)
	    (pdata->entailed[i] + EDGE_WATCHED_VALUE);
	  pdata->watch = i;
	  break;
	}
      }
    }
  }
}

static void entail_linle(Wam wam,
			 struct mddi_data *pdata,
			 struct tuple_data *tdata,
			 SP_integer linle,
			 int ent)
{
  TRAIL_DEC(tdata->linle.entailed[linle],EDGE_ALIVE-ent);
  if (tdata->linle.entailed[linle] & EDGE_WATCHED_VALUE) {
    int j;
    for (j=0; j<pdata->nlinles; j++) {
      if ((tdata->linle.entailed[j] & 0x3) == EDGE_ALIVE) {
	tdata->linle.entailed[linle] = (unsigned char)
	  (tdata->linle.entailed[linle] - EDGE_WATCHED_VALUE);
	tdata->linle.entailed[j] = (unsigned char)
	  (tdata->linle.entailed[j] + EDGE_WATCHED_VALUE);
	tdata->linle.watch = j;
	break;
      }
    }
    if (j==pdata->nlinles) /* it was the last linle to get (dis)entailed */
      maintain_entailment(wam, pdata,tdata,-1);
  }
}

static void disentail_linles(Wam wam,
			     struct mddi_data *pdata,
			     struct tuple_data *tdata,
			     SP_integer edge)
{
  SP_integer i;
  SP_integer first = pdata->linle.chunk[edge];
  SP_integer next = (edge+1 < pdata->nedges ? pdata->linle.chunk[edge+1] : pdata->nlinles);
  for (i=first; i<next; i++) {
    if ((tdata->linle.entailed[i] & 0x3)==EDGE_ALIVE)
      entail_linle(wam, pdata,tdata,i,0);
  }    
}

static void downward_pass(Wam wam,
			  struct mddi_data *pdata,
			  struct tuple_data *tdata)
{
  int nkfa = tdata->nkfa;
  int nk = tdata->nk;
 restart:
  while (nkfa > 0) {	/* search for a new support */
    SP_integer node = tdata->killed_from_above[--nkfa];
    SP_integer edge = pdata->node.in[node];
    SP_integer watch_in = tdata->node.watch_in[node];
    while (edge > -1) {
      if ((tdata->edge.status[edge] & 0x3) == EDGE_ALIVE) {
	/* support found; update watches */
	tdata->edge.status[watch_in] = (unsigned char)
	  (tdata->edge.status[watch_in] & ~EDGE_WATCHED_BELOW);
	tdata->edge.status[edge] |= EDGE_WATCHED_BELOW;
	tdata->node.watch_in[node] = edge;
	goto restart;
      }
      edge = pdata->edge.in[edge];
    }
    /* the node is still dead; kill outgoing edges */
    edge = pdata->node.out[node];
    while (edge > -1) {
      unsigned char status = tdata->edge.status[edge];
      if ((status & 0x3) == EDGE_ALIVE) {
	TRAIL_DEC_EDGE(tdata->edge.status[edge],EDGE_ALIVE - EDGE_KILLED_ABOVE,edge);
	if (status & EDGE_WATCHED_BELOW)
	  tdata->killed_from_above[nkfa++] = pdata->edge.dest[edge];
	if (status & EDGE_WATCHED_VALUE)
	  tdata->killed[nk++] = pdata->edge.varval[edge];
	if (pdata->nlinles>0) {
	  if (status & EDGE_WATCHED_ISTHMUS)
	    maintain_isthmus(wam, pdata,tdata,edge);
	  disentail_linles(wam, pdata,tdata,edge);
	}
      }
      edge = pdata->edge.out[edge];
    }
  }
  tdata->nkfa = 0;
  tdata->nk = nk;
}

static void upward_pass(Wam wam,
			struct mddi_data *pdata,
			struct tuple_data *tdata)
{
  int nkfb = tdata->nkfb;
  int nk = tdata->nk;
 restart:
  while (nkfb > 0) {	/* search for a new support */
    SP_integer node = tdata->killed_from_below[--nkfb];
    SP_integer edge = pdata->node.out[node];
    SP_integer watch_out = tdata->node.watch_out[node];
    while (edge > -1) {
      if ((tdata->edge.status[edge] & 0x3) == EDGE_ALIVE) {
	/* support found; update watches */
	tdata->edge.status[watch_out] = (unsigned char)
	  (tdata->edge.status[watch_out] & ~EDGE_WATCHED_ABOVE);
	tdata->edge.status[edge] |= EDGE_WATCHED_ABOVE;
	tdata->node.watch_out[node] = edge;
	goto restart;
      }
      edge = pdata->edge.out[edge];
    }
    /* the node is still dead; kill ingoing edges */
    edge = pdata->node.in[node];
    while (edge > -1) {
      unsigned char status = tdata->edge.status[edge];
      if ((status & 0x3) == EDGE_ALIVE) {
	TRAIL_DEC_EDGE(tdata->edge.status[edge],EDGE_ALIVE - EDGE_KILLED_BELOW,edge);
	if (status & EDGE_WATCHED_ABOVE)
	  tdata->killed_from_below[nkfb++] = pdata->edge.source[edge];
	if (status & EDGE_WATCHED_VALUE)
	  tdata->killed[nk++] = pdata->edge.varval[edge];
	if (pdata->nlinles>0) {
	  if (status & EDGE_WATCHED_ISTHMUS)
	    maintain_isthmus(wam, pdata,tdata,edge);
	  disentail_linles(wam, pdata,tdata,edge);
	}
      }
      edge = pdata->edge.in[edge];
    }
  }
  tdata->nkfb = 0;
  tdata->nk = nk;
}

static void collect(struct mddi_data *pdata,
		    struct tuple_data *tdata)
{
  int nk0=0, nk=0;

  while (nk0 < tdata->nk) {
    SP_integer varval = tdata->killed[nk0++];
    SP_integer edge = tdata->varval.support[varval];
    SP_integer e = pdata->varval.edges[varval];
    while (e<pdata->nedges && pdata->edge.varval[e]==varval) {
      if ((tdata->edge.status[e] & 0x3) == EDGE_ALIVE) {
	tdata->edge.status[edge] = (unsigned char)
	  (tdata->edge.status[edge] & ~EDGE_WATCHED_VALUE);
	tdata->edge.status[e] |= EDGE_WATCHED_VALUE;
	tdata->varval.support[varval] = edge = e;
	break;
      }
      e++;
    }
    if ((tdata->edge.status[edge] & 0x3) != EDGE_ALIVE) {
      tdata->killed[nk++] = varval;
    }
  }
  tdata->nk = nk;  
}

#if SP_ASSERTIONS
static SP_BOOL mddi_assertion_above(struct mddi_data *pdata,
				    struct tuple_data *tdata)
{
  int i;
  for (i=0; i<pdata->nnodes; i++) { /* exclude bottom node */
    int j = 0;
    SP_integer e = pdata->node.out[i];
    if (e > -1) {
      while (e > -1) {
	if (tdata->edge.status[e] & EDGE_WATCHED_ABOVE) j++;
	e = pdata->edge.out[e];
      }
      if (j!=1)
	return FALSE;
    }
  }
  return TRUE;
}

static SP_BOOL mddi_assertion_below(struct mddi_data *pdata,
				    struct tuple_data *tdata)
{
  int i;
  for (i=1; i<=pdata->nnodes; i++) { /* exclude top node */
    int j = 0;
    SP_integer e = pdata->node.in[i];
    if (e > -1) {
      while (e > -1) {
	if (tdata->edge.status[e] & EDGE_WATCHED_BELOW) j++;
	e = pdata->edge.in[e];
      }
      if (j!=1)
	return FALSE;
    }
  }
  return TRUE;
}

static SP_BOOL mddi_assertion_varval(struct mddi_data *pdata,
				     struct tuple_data *tdata)
{
  int i;
  for (i=0; i<pdata->nvarvals; i++) {
    int j = 0;
    SP_integer e = tdata->varval.support[i];
    while (e<pdata->nedges && pdata->edge.varval[e]==i) {
      if (tdata->edge.status[e] & EDGE_WATCHED_VALUE) j++;
      e++;
    }
    if (j!=1)
      return FALSE;
  }
  return TRUE;
}
#endif /* SP_ASSERTIONS */

static void kill_edge(Wam wam,
		      struct mddi_data *pdata,
		      struct tuple_data *tdata,
		      SP_integer edge,
		      SP_BOOL from_linle)
{
  unsigned char status = tdata->edge.status[edge];
  TRAIL_DEC_EDGE(tdata->edge.status[edge],EDGE_ALIVE - EDGE_KILLED_VALUE,edge);
  if (status & EDGE_WATCHED_ABOVE)
    tdata->killed_from_below[tdata->nkfb++] = pdata->edge.source[edge];
  if (status & EDGE_WATCHED_BELOW)
    tdata->killed_from_above[tdata->nkfa++] = pdata->edge.dest[edge];
  if (pdata->nlinles>0) {
    if (from_linle) {
      if (status & EDGE_WATCHED_VALUE)
	tdata->killed[tdata->nk++] = pdata->edge.varval[edge];
    }
    if (status & EDGE_WATCHED_ISTHMUS)
      maintain_isthmus(wam, pdata,tdata,edge);
    disentail_linles(wam, pdata,tdata,edge);
  }
}

#if OPT_EDGESCAN

static void kill_all_support_integer(Wam wam,
				     struct mddi_data *pdata,
				     struct tuple_data *tdata,
				     SP_integer var,
				     TAGGED value)
{
  int root = pdata->nedges + var;
  SP_integer e = tdata->edge.next[root];
  int vvfalse = -1, vvtrue = -1;

  while (e!=root) {
    SP_integer varval = pdata->edge.varval[e];
    if (varval==vvtrue) {
    } else if (varval==vvfalse) {
      kill_edge(wam, pdata,tdata,e,FALSE);
    } else if ((Tlt(pdata->varval.max[varval],value) && IsSmall(pdata->varval.max[varval])) ||
	       (Tgt(pdata->varval.min[varval],value) && IsSmall(pdata->varval.min[varval]))) {
      vvfalse = varval;
      kill_edge(wam, pdata,tdata,e,FALSE);
    } else {
      vvtrue = varval;
    }
    e = tdata->edge.next[e];
  }
}

static void kill_all_support_interval(Wam wam,
				      struct mddi_data *pdata,
				      struct tuple_data *tdata,
				      SP_integer var,
				      TAGGED min,
				      TAGGED max)
{
  int root = pdata->nedges + var;
  SP_integer e = tdata->edge.next[root];
  int vvfalse = -1, vvtrue = -1;

  while (e!=root) {
    SP_integer varval = pdata->edge.varval[e];
    if (varval==vvtrue) {
    } else if (varval==vvfalse) {
      kill_edge(wam, pdata,tdata,e,FALSE);
    } else if (FDlt(pdata->varval.max[varval],min) ||
	       FDgt(pdata->varval.min[varval],max)) {
      vvfalse = varval;
      kill_edge(wam, pdata,tdata,e,FALSE);
    } else {
      vvtrue = varval;
    }
    e = tdata->edge.next[e];
  }
}

static void kill_all_support_general(Wam wam,
				     struct mddi_data *pdata,
				     struct tuple_data *tdata,
				     SP_integer var,
				     Dvar dv)
{
  int root = pdata->nedges + var;
  SP_integer e = tdata->edge.next[root];
  int vvfalse = -1, vvtrue = -1;

  while (e!=root) {
    SP_integer varval = pdata->edge.varval[e];
    if (varval==vvtrue) {
    } else if (varval==vvfalse) {
      kill_edge(wam, pdata,tdata,e,FALSE);
    } else if (dvar_compare_interval_t(dv, pdata->varval.min[varval], pdata->varval.max[varval])==FDI_DISJOINT) {
      vvfalse = varval;
      kill_edge(wam, pdata,tdata,e,FALSE);
    } else {
      vvtrue = varval;
    }
    e = tdata->edge.next[e];
  }
}

#else

static void kill_all_support_integer(Wam wam,
				     struct mddi_data *pdata,
				     struct tuple_data *tdata,
				     SP_integer var,
				     TAGGED value)
{
  SP_integer e = pdata->edge.chunk[var];
  SP_integer last = (var+1 < pdata->nvars ? pdata->edge.chunk[var+1] : pdata->nedges);
  SP_integer vvfalse = -1, vvtrue = -1;

  for (; e<last; e++) {
    if ((tdata->edge.status[e] & 0x3) == EDGE_ALIVE) { /* killed due to external pruning */
      SP_integer varval = pdata->edge.varval[e];
      if (varval==vvtrue) {
      } else if (varval==vvfalse) {
	kill_edge(wam, pdata,tdata,e,FALSE);
      } else if (Tgt(pdata->varval.min[varval],value) && IsSmall(pdata->varval.min[varval])) {
	break;			/* condition now monotone */
      } else if (Tlt(pdata->varval.max[varval],value) && IsSmall(pdata->varval.max[varval])) {
	vvfalse = varval;
	kill_edge(wam, pdata,tdata,e,FALSE);
      } else {
	vvtrue = varval;
      }
    }
  }
  for (; e<last; e++) {
    if ((tdata->edge.status[e] & 0x3) == EDGE_ALIVE) {
      kill_edge(wam, pdata,tdata,e,FALSE);
    }
  }
}

static void kill_all_support_interval(Wam wam,
				      struct mddi_data *pdata,
				      struct tuple_data *tdata,
				      SP_integer var,
				      TAGGED min,
				      TAGGED max)
{
  SP_integer e = pdata->edge.chunk[var];
  SP_integer last = (var+1 < pdata->nvars ? pdata->edge.chunk[var+1] : pdata->nedges);
  SP_integer vvfalse = -1, vvtrue = -1;

  for (; e<last; e++) {
    if ((tdata->edge.status[e] & 0x3) == EDGE_ALIVE) { /* killed due to external pruning */
      SP_integer varval = pdata->edge.varval[e];
      if (varval==vvtrue) {
      } else if (varval==vvfalse) {
	kill_edge(wam, pdata,tdata,e,FALSE);
      } else if (FDgt(pdata->varval.min[varval],max)) {
	break;			/* condition now monotone */
      } else if (FDlt(pdata->varval.max[varval],min)) {
	vvfalse = varval;
	kill_edge(wam, pdata,tdata,e,FALSE);
      } else {
	vvtrue = varval;
      }
    }
  }
  for (; e<last; e++) {
    if ((tdata->edge.status[e] & 0x3) == EDGE_ALIVE) {
      kill_edge(wam, pdata,tdata,e,FALSE);
    }
  }
}

static void kill_all_support_general(Wam wam,
				     struct mddi_data *pdata,
				     struct tuple_data *tdata,
				     SP_integer var,
				     Dvar dv) 
{
  SP_integer e = pdata->edge.chunk[var];
  SP_integer last = (var+1 < pdata->nvars ? pdata->edge.chunk[var+1] : pdata->nedges);
  SP_integer vvfalse = -1, vvtrue = -1;
  TAGGED min = dv->min;
  TAGGED max = dv->max;
  TAGGED set = dvar_set(dv);

  for (; e<last; e++) {
    if ((tdata->edge.status[e] & 0x3) == EDGE_ALIVE) { /* killed due to external pruning */
      SP_integer varval = pdata->edge.varval[e];
      TAGGED vmin = pdata->varval.min[varval];
      TAGGED vmax = pdata->varval.max[varval];
      if (varval==vvtrue) {
      } else if (varval==vvfalse) {
	kill_edge(wam, pdata,tdata,e,FALSE);
      } else if (FDgt(vmin,max)) {
	break;			/* remaining edges must all be dead */
      } else if (FDlt(vmax,min)) {
	vvfalse = varval;
	kill_edge(wam, pdata,tdata,e,FALSE);
      } else if (!fd_intersects_else(vmin,vmax,set,&set)) {
	if (set==EmptySet)
	  break;			/* remaining edges must all be dead */
	min = RangeMin(CTagToCar(set)); /* fd_min(set) */
	vvfalse = varval;
	kill_edge(wam, pdata,tdata,e,FALSE);
      } else {
	min = RangeMin(CTagToCar(set)); /* fd_min(set) */
	vvtrue = varval;
      }
    }
  }
  for (; e<last; e++) {
    if ((tdata->edge.status[e] & 0x3) == EDGE_ALIVE) {
      kill_edge(wam, pdata,tdata,e,FALSE);
    }
  }
}

#endif

static void kill_all_support(Wam wam,
			     struct mddi_data *pdata,
			     struct tuple_data *tdata,
			     SP_integer var) 
{
  int tuple = (int)(tdata-pdata->tuples);
  int nvars = pdata->nvars;
  Dvar dv = DVAR(tuple,var);

  if (dvar_is_integer(dv)) {	/* ground case */
    kill_all_support_integer(wam, pdata,tdata,var,dv->min);
    maintain_entailment(wam, pdata,tdata,var);
  } else if (dvar_is_interval(dv)) { /* interval case */
    kill_all_support_interval(wam, pdata,tdata,var,dv->min,dv->max);
  } else {			/* general case */
    kill_all_support_general(wam, pdata,tdata,var,dv);
  }
}


/* Propagation of a linear le constraint
   Fixpoints are dealt with in the main loop
   TRUE for success, FALSE for failure
*/
static SP_BOOL linle_propagate(struct mddi_data *pdata,
			       struct tuple_data *tdata,
			       SP_integer linle)
{
  int tuple = (int)(tdata-pdata->tuples);
  int nvars = pdata->nvars;
  SP_integer lbase = pdata->nvars * linle;
  SP_integer ix, ixn = pdata->linle.lhsn[linle];
  SP_integer bigf = pdata->linle.rhs[linle]; /* rhs - \sum \min a_i x_i */

  for (ix=0; ix<ixn; ix++) {
    SP_integer i = pdata->linle.lhsi[lbase+ix];
    SP_integer c = pdata->linle.lhsc[lbase+ix];
    Dvar dv = DVAR(tuple,i);
    if (c > 0) {
      bigf -= c*dvar_min_l(dv);
    } else {
      bigf -= c*dvar_max_l(dv);
    }
  }
  if (bigf<0)
    return FALSE;
  for (ix=0; ix<ixn; ix++) {
    SP_integer i = pdata->linle.lhsi[lbase+ix];
    SP_integer c = pdata->linle.lhsc[lbase+ix];
    Dvar dv = DVAR(tuple,i);
    SP_integer min, max;
    if (c > 0) {
      min = c*dvar_min_l(dv);
      max = c*dvar_max_l(dv);
      if (max-min > bigf) {
	SP_integer ub = bigf/c + dvar_min_l(dv);
	if (dvar_fix_max_l(dv,ub)<0)
	  return FALSE;
      }
    } else {
      min = c*dvar_max_l(dv);
      max = c*dvar_min_l(dv);
      if (max-min > bigf) {
	SP_integer lb = -(bigf/(-c)) + dvar_max_l(dv);
	if (dvar_fix_min_l(dv,lb)<0)
	  return FALSE;
      }
    }
  }
  return TRUE;
}

/* (dis)entailment test of a linear le constraint
   Note: under the assumption that the edge is committed
   -1 for disentailed
    1 for entailed
    2 for can prune
    0 for neither
*/
static int linle_entailment(struct mddi_data *pdata,
			    struct tuple_data *tdata,
			    SP_integer linle)
{
  int tuple = (int)(tdata-pdata->tuples);
  int nvars = pdata->nvars;
  SP_integer edge = pdata->linle.edge[linle];
  SP_integer edgevar =
    (edge == -1 ? -1 : /* no edge - don't narrow */
     !(tdata->linle.active[linle] & EDGE_ALIVE) ? -1 : /* isthmus - don't narrow */
     pdata->varval.var[pdata->edge.varval[edge]]);
  SP_integer lbase = pdata->nvars * linle;
  SP_integer min = - pdata->linle.rhs[linle];
  SP_integer max = min;
  SP_integer maxinterval = 0;
  SP_integer ix, ixn = pdata->linle.lhsn[linle];

  for (ix=0; ix<ixn; ix++) {
    SP_integer i = pdata->linle.lhsi[lbase+ix];
    SP_integer c = pdata->linle.lhsc[lbase+ix];
    Dvar dv = DVAR(tuple,i);
    SP_integer cmin, cmax;
    if (i==edgevar) {		/* simulate domain in current edge */
      TAGGED tedgemin = pdata->varval.min[pdata->edge.varval[edge]];
      TAGGED tedgemax = pdata->varval.max[pdata->edge.varval[edge]];
      SP_integer edgemin = (tedgemin!=Inf && Tge(tedgemin,dvar_min_t(dv)) ?
			    GetSmall0(tedgemin) : dvar_min_l(dv));
      SP_integer edgemax = (tedgemax!=Sup && Tle(tedgemax,dvar_max_t(dv)) ?
			    GetSmall0(tedgemax) : dvar_max_l(dv));
      if (c > 0) {
	cmin = c*edgemin;
	cmax = c*edgemax;
      } else {
	cmin = c*edgemax;
	cmax = c*edgemin;
      }
    } else {
      if (c > 0) {
	cmin = c*dvar_min_l(dv);
	cmax = c*dvar_max_l(dv);
      } else {
	cmin = c*dvar_max_l(dv);
	cmax = c*dvar_min_l(dv);
      }
    }
    min += cmin;
    max += cmax;
    maxinterval = cmax-cmin > maxinterval ? cmax-cmin : maxinterval;
  }
  return (max <= 0 ? 1 : min > 0 ? -1 : maxinterval > -min ? 2 : 0);
}

static void check_all_linles(Wam wam,
			     struct mddi_data *pdata,
			     struct tuple_data *tdata,
			     int filter)
{
  int i;
  for (i=0; i<pdata->nlinles; i++) {
    SP_integer edge = pdata->linle.edge[i];
    if ((tdata->linle.entailed[i] & 0x3)==EDGE_ALIVE &&
	(filter<0 || pdata->linle.lhs[pdata->nvars * i + filter]!=0)) {
      SP_ASSERT((edge == -1 || (tdata->edge.status[edge] & 0x3)==EDGE_ALIVE));
      switch (linle_entailment(pdata,tdata,i)) {
      case -1:			/* disentailed */
	if (edge >= 0) {
	  kill_edge(wam, pdata,tdata,edge,TRUE);
	} else {
	  tdata->linle.active[i] |= EDGE_WATCHED_ISTHMUS; /* will fail in propagator */
	  tdata->active = TRUE;
	}
	break;
      case 1:		/* entailed */
	entail_linle(wam, pdata,tdata,i,1);
	break;
      case 2:
	if (!(tdata->linle.active[i] & EDGE_ALIVE)) {
	  tdata->linle.active[i] |= EDGE_WATCHED_ISTHMUS;
	  tdata->active = TRUE;
	}
      }
    }
  }
}

static int
mddi_propagate(Wam wam,
	       struct mddi_data *pdata,
	       SP_BOOL norefresh)
{
  int ent = -1;
  int tuple, i, j;
  int nvars = pdata->nvars;
  int nnodes = pdata->nnodes;
  SP_BOOL idempotent = (!(RefMutable(CTagToArg(X(1),3))&IStep(4))); /* STATUS: idempotent */
  
  tuple = (int)pdata->first;
  while (tuple != -1) {
    SP_integer edge;
    struct tuple_data *tdata = pdata->tuples + tuple;

    downward_pass(wam, pdata,tdata);
    edge = tdata->node.watch_in[nnodes];
    if ((tdata->edge.status[edge] & 0x3) != EDGE_ALIVE)
      return ent;
    upward_pass(wam, pdata,tdata);
    collect(pdata,tdata); /* tdata->killed contains a list of varvals */
    SP_ASSERT(tdata->nk <= pdata->nvarvals);
    SP_ASSERT(mddi_assertion_above(pdata,tdata));
    SP_ASSERT(mddi_assertion_below(pdata,tdata));
    SP_ASSERT(mddi_assertion_varval(pdata,tdata));
    /* sort and form sets for batch pruning? */
    if (tdata->nk + tdata->active > 0) {
      if (!norefresh) {
	for (i=0; i<nvars; i++) {	/* i: var index */
	  Dvar dv = DVAR(tuple,i);
	  dvar_refresh(dv);
	}
      }
      for (j=0; j<tdata->nk; j++) {
	SP_integer varval = tdata->killed[j];
	SP_integer var = pdata->varval.var[varval];
	Dvar dv = DVAR(tuple,var);
	TAGGED min = pdata->varval.min[varval];
	TAGGED max = pdata->varval.max[varval];
	if (dvar_prune_interval_t(dv,min,max)< 0) /* can happen if there are corefs? */
	  return ent;
	if (dvar_is_integer(dv) && idempotent && (pdata->entailed[tuple] & EDGE_ALIVE))
	  maintain_entailment(wam, pdata,tdata,var);
      }
      tdata->nk = 0;
      if (tdata->active) {
	for (i=0; i<pdata->nlinles; i++) {
	  if ((tdata->linle.entailed[i] & 0x3)==EDGE_ALIVE &&
	      (tdata->linle.active[i] & (EDGE_ALIVE|EDGE_WATCHED_ISTHMUS))==EDGE_WATCHED_ISTHMUS) {
	    if (!linle_propagate(pdata,tdata,i))
	      return ent;
	    tdata->linle.active[i] = (unsigned char)
	      (tdata->linle.active[i] - EDGE_WATCHED_ISTHMUS);
	  }
	}
      }
      tdata->active = FALSE;
      for (i=0; i < nvars; i++) {
	Dvar dv = DVAR(tuple,i);
	dvar_pruning_done( dv);
      }
      for (i=0; i < nvars; i++) {
	Dvar dv = DVAR(tuple,i);
	dvar_export(dv);
      }
    }
    pdata->first = pdata->next[tuple];
    pdata->next[tuple] = -2;
    tuple = (int)pdata->first;
  }
  CTagToArg(X(0),2) = MakeSmall(pdata->trail_top);
  ent = ((pdata->entailed[pdata->watch] & 0x3) != EDGE_ALIVE);
#if DBG && 0
  /* gives false alarm on fdbasic's SMT tests,
     where outgoing edges can have overlapping intervals */
  if (!ent && idempotent && pdata->nlinles==0) { /* check that we don't miss entailment */
    int all_entailed=1;
    for (tuple=0; tuple<pdata->ntuples; tuple++) {
      if ((pdata->entailed[tuple] & 0x3) == EDGE_ALIVE) {
	int nint=0;
	all_entailed = 0;
	for (i=0; i<nvars; i++) {	/* i: var index */
	  Dvar dv = DVAR(tuple,i);
	  dvar_refresh(dv);
	  if (dvar_is_integer(dv))
	    nint++;
	}
	SP_ASSERT(nvars-nint > 1);
      }
    }
    SP_ASSERT(!all_entailed);
  }
#endif
  return ent;
}

static void SPCDECL 
mddi_daemon(Wam wam,
	    void *vdata,
	    SP_globref attr_ref,
	    TAGGED global)
{
  struct mddi_data *pdata = (struct mddi_data *)vdata;
  int nvars = pdata->nvars;
  TAGGED tstate;
  int ar, tuple, state_stamp;

  tstate = RefMutable(CTagToArg(global,1));
  ar = Arity(TagToHeadfunctor(tstate));
  state_stamp = GetSmall_int(CTagToArg(tstate,ar));
  if (pdata->stamp!=state_stamp) { /* non-incremental */
    int trail_top = GetSmall_int(CTagToArg(tstate,2));
    int f = pdata->trail_top;
    int n;
    
#if OPT_EDGESCAN
    int nedges = pdata->nedges;
    while (f>trail_top) {
      unsigned char *ptr = pdata->trail[--f];
      *ptr |= EDGE_ALIVE;
      if (ptr < pdata->entailed) {
	int n = ptr - pdata->tuples->edge.status;
	int tuple = n/nedges;
	SP_integer edge = n%nedges;
	struct tuple_data *tdata = pdata->tuples + tuple;
	SP_integer root = pdata->nedges + pdata->varval.var[pdata->edge.varval[edge]];
	SP_integer next = tdata->edge.next[root];
	tdata->edge.next[root] = edge;
	tdata->edge.prev[edge] = root;
	tdata->edge.next[edge] = next;
	tdata->edge.prev[next] = edge;
      }
    }
#else
    while (f>trail_top) {
      *(pdata->trail[--f]) |= EDGE_ALIVE;
    }
#endif
    pdata->trail_top = f;
    for (f = (int)pdata->first; f != -1; f = n) {
      struct tuple_data *tdata = pdata->tuples + f;
      tdata->nkfa = 0;
      tdata->nkfb = 0;
      tdata->nk = 0;
      n = (int)pdata->next[f];
      pdata->next[f] = -2;
    }
    pdata->first = -1;
    pdata->stamp = state_stamp;
  }
  tuple =  (int)((attr_ref - pdata->refbase)/(nvars<<1));
  if ((pdata->entailed[tuple] & 0x3) == EDGE_ALIVE) { /* if tuple not entailed */
    struct tuple_data *tdata = pdata->tuples + tuple;
    int col = (int)((attr_ref - pdata->refbase)%(nvars<<1))>>1;
    if (tdata->nonground.status[col] & EDGE_ALIVE) { /* could have been processed already */
      Dvar dv = DVAR(tuple,col);
      SP_BOOL buried;
    
      tstate = fd_daemon_copy_state(wam, &global,&buried);
      pdata->stamp++;				       /* corefs cause bugs */
      dvar_refresh(dv);
      kill_all_support(wam, pdata,tdata,col);
      if ((pdata->entailed[tuple] & EDGE_ALIVE) && pdata->nlinles>0) {
	int i;
	for (i=0; i<pdata->nvars; i++) {
	  Dvar dv = DVAR(tuple,i);
	  dvar_refresh(dv);
	}
	check_all_linles(wam, pdata,tdata,col);
      }
      CTagToArg(tstate,2) = MakeSmall(pdata->trail_top);
      if (pdata->next[tuple] == -2 &&
	  tdata->nkfa + tdata->nkfb + tdata->nk + tdata->active > 0) {
	pdata->next[tuple] = pdata->first;
	pdata->first = tuple;
	fd_enqueue_global(wam, global, 0x7);
      } else if ((pdata->entailed[pdata->watch] & 0x3) != EDGE_ALIVE) {
	fd_enqueue_global(wam, global, 0x7);
      }
    }
  }
}

static struct mddi_data *
mddi_alloc_state(Wam wam,
		 int nvars,int nnodes,int nedges,
		 int nvarvals,int nlinles,int ntuples,TAGGED handle)
{
  char *ptr;
  int i;
  struct mddi_data *pdata;
  int ntuples1 = (ntuples >= 1 ? ntuples : 1);
  SP_integer total_size = ntuples*nvars*sizeof(struct dvar) + /* dvar */
    ntuples*sizeof(SP_integer) + /* next */
    ntuples*nedges*sizeof(SP_integer) + /* killed_from_above */
    ntuples*nedges*sizeof(SP_integer) + /* killed_from_below */
    ntuples*nvarvals*sizeof(SP_integer) + /* killed */
    ntuples*(nedges+1+nvars+2*nlinles)*sizeof(unsigned char *) + /* trail */
    ntuples*sizeof(struct tuple_data) + /* tuples */
    3*(nnodes+1)*sizeof(SP_integer) +  /* node */
    5*nedges*sizeof(SP_integer) +  /* edge */
    nvars*sizeof(SP_integer) +  /* edge */
    4*nvarvals*sizeof(SP_integer) +  /* varval */
    nvars*sizeof(SP_integer) +  /* varval */
    nlinles*(3*nvars+3)*sizeof(SP_integer) + /* linle */
    nedges*sizeof(SP_integer) + /* linle */
    2*ntuples*(nnodes+1)*sizeof(SP_integer) + /* node */
    2*ntuples*nvars*sizeof(SP_integer) + /* isthmus */
    ntuples*nvars +			    /* nonground */
    ntuples*nedges +			    /* edge */
    ntuples1 +				    /* entailed, at least one */
    ntuples*nvarvals*sizeof(SP_integer) + /* varval */
    2*ntuples*nlinles;		    /* linle */
#if OPT_EDGESCAN
  total_size += 2*ntuples*(nedges+nvars)*sizeof(SP_integer); /* edge.prev, edge.next */
#endif
  pdata = Palloc(struct mddi_data, total_size, handle);
  pdata->destructor = mddi_destructor;
  pdata->daemon = mddi_daemon;
  FD_STORE_SPENV(pdata->spenv);
  pdata->stamp = 0;
  pdata->nrefs = 2*ntuples*nvars;
  pdata->refbase = SP_alloc_globrefs(pdata->nrefs);
  pdata->nvars = nvars;
  pdata->nnodes = nnodes;
  pdata->nedges = nedges;
  pdata->nvarvals = nvarvals;
  pdata->nlinles = nlinles;
  pdata->ntuples = ntuples;
  ptr = (char *)(pdata+1);
  pdata->dvar = (Dvar)ptr; ptr += ntuples*nvars*sizeof(struct dvar);
  pdata->next = (SP_integer *)ptr; ptr += ntuples*sizeof(SP_integer);
  pdata->trail = (unsigned char **)ptr; ptr += (nedges+1+nvars+2*nlinles)*ntuples*sizeof(unsigned char *);
  pdata->tuples = (struct tuple_data *)ptr; ptr += ntuples*sizeof(struct tuple_data);
  pdata->node.var = (SP_integer *)ptr; ptr += (nnodes+1)*sizeof(SP_integer);
  pdata->node.in  = (SP_integer *)ptr; ptr += (nnodes+1)*sizeof(SP_integer);
  pdata->node.out = (SP_integer *)ptr; ptr += (nnodes+1)*sizeof(SP_integer);
  pdata->edge.source = (SP_integer *)ptr; ptr += nedges*sizeof(SP_integer);
  pdata->edge.dest = (SP_integer *)ptr; ptr += nedges*sizeof(SP_integer);
  pdata->edge.varval = (SP_integer *)ptr; ptr += nedges*sizeof(SP_integer);
  pdata->edge.in = (SP_integer *)ptr; ptr += nedges*sizeof(SP_integer);
  pdata->edge.out = (SP_integer *)ptr; ptr += nedges*sizeof(SP_integer);
  pdata->edge.chunk = (SP_integer *)ptr; ptr += nvars*sizeof(SP_integer);
  pdata->varval.var = (SP_integer *)ptr; ptr += nvarvals*sizeof(SP_integer);
  pdata->varval.min = (TAGGED *)ptr; ptr += nvarvals*sizeof(TAGGED);
  pdata->varval.max = (TAGGED *)ptr; ptr += nvarvals*sizeof(TAGGED);
  pdata->varval.edges = (SP_integer *)ptr; ptr += nvarvals*sizeof(SP_integer);
  pdata->varval.chunk = (SP_integer *)ptr; ptr += nvars*sizeof(SP_integer);
  pdata->linle.lhs = (SP_integer *)ptr; ptr += nlinles*nvars*sizeof(SP_integer);
  pdata->linle.lhsc = (SP_integer *)ptr; ptr += nlinles*nvars*sizeof(SP_integer);
  pdata->linle.lhsi = (SP_integer *)ptr; ptr += nlinles*nvars*sizeof(SP_integer);
  pdata->linle.lhsn = (SP_integer *)ptr; ptr += nlinles*sizeof(SP_integer);
  pdata->linle.rhs = (SP_integer *)ptr; ptr += nlinles*sizeof(SP_integer);
  pdata->linle.edge = (SP_integer *)ptr; ptr += nlinles*sizeof(SP_integer);
  pdata->linle.chunk = (SP_integer *)ptr; ptr += nedges*sizeof(SP_integer);
  for (i=0; i<ntuples; i++) {
    struct tuple_data *tdata = pdata->tuples + i;
    tdata->node.watch_in = (SP_integer *)ptr; ptr += (nnodes+1)*sizeof(SP_integer);
    tdata->node.watch_out = (SP_integer *)ptr; ptr += (nnodes+1)*sizeof(SP_integer);
    tdata->isthmus.watch1 = (SP_integer *)ptr; ptr += (nvars)*sizeof(SP_integer);
    tdata->isthmus.watch2 = (SP_integer *)ptr; ptr += (nvars)*sizeof(SP_integer);
    tdata->varval.support = (SP_integer *)ptr; ptr += nvarvals*sizeof(SP_integer);
    tdata->killed_from_above = (SP_integer *)ptr; ptr += nedges*sizeof(SP_integer);
    tdata->killed_from_below = (SP_integer *)ptr; ptr += nedges*sizeof(SP_integer);
    tdata->killed = (SP_integer *)ptr; ptr += nvarvals*sizeof(SP_integer);
#if OPT_EDGESCAN
    tdata->edge.prev = (SP_integer *)ptr; ptr += (nedges+nvars)*sizeof(SP_integer);
    tdata->edge.next = (SP_integer *)ptr; ptr += (nedges+nvars)*sizeof(SP_integer);
#endif
  }
  for (i=0; i<ntuples; i++) {
    struct tuple_data *tdata = pdata->tuples + i;
    tdata->edge.status = (unsigned char *)ptr; ptr += nedges;
  }
  pdata->entailed = (unsigned char *)ptr; ptr += ntuples1; /* marks end of edge.status */
  for (i=0; i<ntuples; i++) {
    struct tuple_data *tdata = pdata->tuples + i;
    tdata->nonground.status = (unsigned char *)ptr; ptr += nvars;
    tdata->linle.entailed = (unsigned char *)ptr; ptr += nlinles;
    tdata->linle.active = (unsigned char *)ptr; ptr += nlinles;
  }
  SP_ASSERT(ptr == (char *)(pdata+1)+total_size);
  for (i=0; i<ntuples; i++) {	/* factor out */
    struct tuple_data *tdata = pdata->tuples + i;
    tdata->nkfa = 0;
    tdata->nkfb = 0;
    tdata->nk = 0;
    tdata->active = TRUE;
    pdata->next[i] = i-1;
    pdata->entailed[i] = EDGE_ALIVE;
  }
  pdata->first = ntuples-1;
  pdata->watch = 0;
  pdata->entailed[0] |= EDGE_WATCHED_VALUE; /* [MC] has at least one element */
  return pdata;
}

static void
mddi_init_state(Wam wam, struct mddi_data *pdata)
{
  int nvars = pdata->nvars;
  int nnodes = pdata->nnodes;
  int nedges = pdata->nedges;
  int nvarvals = pdata->nvarvals;
  int nlinles = pdata->nlinles;
  int ntuples = pdata->ntuples;
  int i, j;
				/* build linked lists */
  for (i=nvarvals-1; i>=0; --i) {
    pdata->varval.chunk[pdata->varval.var[i]] = i;
  }
  for (i=nedges-1; i>=0; --i) {
    SP_integer node = pdata->edge.dest[i];
    SP_integer next = pdata->node.in[node];
    SP_integer varval = pdata->edge.varval[i];
    pdata->edge.in[i] = next;
    pdata->node.in[node] = i;
    node = pdata->edge.source[i];
    next = pdata->node.out[node];
    pdata->edge.out[i] = next;
    pdata->node.out[node] = i;
    pdata->varval.edges[varval] = i;
    pdata->edge.chunk[pdata->varval.var[varval]] = i;
    pdata->linle.chunk[i] = nlinles;
  }
  for (i=nlinles-1; i>=0; --i) {
    SP_integer edge = pdata->linle.edge[i];
    if (edge >= 0)
      for (j=0; j<=edge; j++)
	pdata->linle.chunk[j] = i;
  }
  for (i=0; i<ntuples; i++) {
    struct tuple_data *tdata = pdata->tuples + i;
#if OPT_EDGESCAN
    for (j=0; j<nvars; j++) {
      SP_integer first = pdata->edge.chunk[j];
      SP_integer last  = (j+1 < nvars ? pdata->edge.chunk[j+1] : nedges);
      SP_integer k;
      for (k=first; k<last; k++) {
	tdata->edge.prev[k] = k-1;
	tdata->edge.next[k] = k+1;
      }
      tdata->edge.prev[first] = nedges+j;
      tdata->edge.next[nedges+j] = first;
      tdata->edge.next[last-1] = nedges+j;
      tdata->edge.prev[nedges+j] = last-1;
    }
#endif
				/* tuple is entailed when < 2 nonground */
    for (j=0; j<nvars; j++) {
      tdata->nonground.status[j] = EDGE_ALIVE;
    }
    tdata->nonground.status[0] |= EDGE_WATCHED_VALUE;
    tdata->nonground.watch1 = 0;
    tdata->nonground.watch2 = 0;
    if (nvars>1) {
      tdata->nonground.status[1] |= EDGE_WATCHED_VALUE;
      tdata->nonground.watch2 = 1;
    }
    for (j=0; j<nedges; j++) {
      tdata->edge.status[j] = EDGE_ALIVE;
    }
    for (j=0; j<nlinles; j++) {
      SP_integer edge = pdata->linle.edge[j];
      tdata->linle.entailed[j] = EDGE_ALIVE;
      tdata->linle.active[j] = (edge == -1 ? EDGE_WATCHED_ISTHMUS : EDGE_ALIVE);
      if (edge >= 0) {
	SP_integer var = pdata->varval.var[pdata->edge.varval[edge]];
	edge = pdata->edge.chunk[var];
	tdata->edge.status[edge] |= EDGE_WATCHED_ISTHMUS;
	tdata->isthmus.watch1[var] = edge;
	tdata->isthmus.watch2[var] = edge;
	if (edge+1<nedges && pdata->varval.var[pdata->edge.varval[edge+1]]==var) {
	  tdata->edge.status[edge+1] |= EDGE_WATCHED_ISTHMUS;
	  tdata->isthmus.watch2[var] = edge+1;
	} else {		/* committed edge */
	  tdata->linle.active[j] = EDGE_WATCHED_ISTHMUS;
	}
      }
    }
    if (nlinles>0) {
      tdata->linle.entailed[0] |= EDGE_WATCHED_VALUE;
      tdata->linle.watch = 0;
    }
    for (j=0; j<(nnodes+1); j++) {
      SP_integer edge = pdata->node.in[j];
      if (edge > -1) {	/* not for top node */
	tdata->node.watch_in[j] = edge;
	tdata->edge.status[edge] |= EDGE_WATCHED_BELOW;
      }
      edge = pdata->node.out[j];
      if (edge > -1) {	/* not for bot node */
	tdata->node.watch_out[j] = edge;
	tdata->edge.status[edge] |= EDGE_WATCHED_ABOVE;
      }
    }
    for (j=0; j<nvarvals; j++) {
      SP_integer edge = pdata->varval.edges[j];
      tdata->varval.support[j] = edge;
      tdata->edge.status[edge] |= EDGE_WATCHED_VALUE;
    }
    for (j=0; j<nvars; j++) {
      SP_globref ref = ATTRIBUTE_LOC(i,j);
      Dvar dv = DVAR(i,j);
	
      dvar_init(dv, ref, ref+1);
      dvar_attach_daemon(wam, dv, pdata, X(1), fd.functor_dom);
    }
  }
  CTagToArg(X(0),1) = atom_nil; /* [MC] 3.12: free for GC */
  pdata->trail_top = 0;
}

/*
state(f(NVars,Nodes,Edges,VarVals,Linles,Tuples), 0 *trail_top* , _Handle,0)
*/
void SPCDECL
prolog_fd_mddi(Wam wam,
	       SP_term_ref State0,
	       SP_term_ref State,
	       SP_term_ref Actions)
{
  struct mddi_data *pdata;
  int i, j, tuple, nvars, nnodes, nedges, nlinles, ntuples, nvarvals;
  int ent = -1;
  TAGGED state, handle;
  SP_BOOL committed, posted;
  
/*    X(0) = RefTerm(State0); */
  (void)State0;
  dvar_export_start(wam);
  RefTerm(State) = fd_static_output_state(wam,&handle,&committed);

  if (!IsVar(handle)) {		/* got [Flag | '$free'(Ptr)] */
    posted = FALSE;
    pdata = Pdata(struct mddi_data,handle);
    nvars = pdata->nvars;
    nnodes = pdata->nnodes;
    nedges = pdata->nedges;
    nvarvals = pdata->nvarvals;
    nlinles = pdata->nlinles;
    ntuples = pdata->ntuples;
  } else {			/* build persistent state */
    TAGGED item, tmp, nodes, edges, varvals, tuples, linles, tvars;
    
    posted = TRUE;
    DerefArg(state,X(0),1);
    DerefArg(tmp,state,1); 
    nvars = GetSmall_int(tmp);
    DerefArg(tmp,state,2);
    nnodes = fd_list_length(tmp);
    DerefArg(tmp,state,3);
    nedges = fd_list_length(tmp);
    DerefArg(tmp,state,4);
    nvarvals = fd_list_length(tmp);
    DerefArg(tmp,state,5);
    nlinles = fd_list_length(tmp);
    DerefArg(tmp,state,6);
    ntuples = fd_list_length(tmp);
    pdata = mddi_alloc_state(wam, nvars,nnodes,nedges,nvarvals,nlinles,ntuples,handle);
    DerefArg(state,X(0),1);	/* refresh */
    DerefArg(nodes,state,2);
    DerefArg(edges,state,3);
    DerefArg(varvals,state,4);
    DerefArg(linles,state,5);
    DerefArg(tuples,state,6);
    
    for (i=0; i<nlinles; i++) {
      int k = nvars*i;
      int ix = 0;
      DerefCar(tvars,linles);
      DerefCdr(linles,linles);
      DerefArg(item,tvars,3);
      pdata->linle.rhs[i] = GetSmall(item);
      DerefArg(item,tvars,1);
      pdata->linle.edge[i] = GetSmall(item);
      DerefArg(tvars,tvars,2);
      for (j=0; j<nvars; j++) {
	SP_integer c;
	DerefCar(item,tvars);
	DerefCdr(tvars,tvars);
	c = GetSmall(item);
	pdata->linle.lhs[k+j] = c;
	if (c) {
	  pdata->linle.lhsc[k+ix] = c;
	  pdata->linle.lhsi[k+ix++] = j;
	}
      }
      pdata->linle.lhsn[i] = ix;
    }

    for (i=0; i<ntuples; i++) {
      DerefCar(tvars,tuples);
      DerefCdr(tuples,tuples);
      for (j=0; j<nvars; j++) {
	SP_globref ref = ATTRIBUTE_LOC(i,j);

	DerefCar(item,tvars);
	DerefCdr(tvars,tvars);
	fd_get_var_and_attr(item,ref);
      }
    }    
    for (i=0; i<nnodes; i++) {
      DerefCar(item,nodes);
      DerefCdr(nodes,nodes);
      pdata->node.var[i] = GetSmall(item);
      pdata->node.in[i] = -1;
      pdata->node.out[i] = -1;
    }
    pdata->node.in[nnodes] = -1;
    pdata->node.out[nnodes] = -1;
    
    for (i=0; i<nedges; i++) {
      DerefCar(item,edges);
      DerefCdr(edges,edges);
      DerefArg(tmp,item,1);	/* source */
      pdata->edge.source[i] = GetSmall(tmp);
      DerefArg(tmp,item,2);	/* dest */
      pdata->edge.dest[i] = GetSmall(tmp);
      DerefArg(tmp,item,3);	/* varval */
      pdata->edge.varval[i] = GetSmall(tmp);
      pdata->edge.in[i] = -1;
      pdata->edge.out[i] = -1;
    }
    
    for (i=0; i<nvarvals; i++) {
      DerefCar(item,varvals);
      DerefCdr(varvals,varvals);
      DerefArg(tmp,item,1);	/* var */
      pdata->varval.var[i] = GetSmall(tmp);
      DerefArg(tmp,item,2);	/* min */
      pdata->varval.min[i] = tmp;
      DerefArg(tmp,item,3);	/* max */
      pdata->varval.max[i] = tmp;
      pdata->varval.edges[i] = -1;
    }
    mddi_init_state(wam, pdata);	/* can GC */
  }
  
  /* RESUME */
  if (posted) {
    for (tuple=0; tuple<ntuples; tuple++) {
      struct tuple_data *tdata = pdata->tuples + tuple;
      for (i=0; i<nvars && (pdata->entailed[tuple] & EDGE_ALIVE); i++) {
	Dvar dv = DVAR(tuple,i);
	
	dvar_refresh(dv);
	kill_all_support(wam, pdata,tdata,i);
      }
      if (nvars<2)
	maintain_entailment(wam, pdata,tdata,-1);
      if (pdata->entailed[tuple] & EDGE_ALIVE)
	check_all_linles(wam, pdata,tdata,-1);
				/* clean up orphants */
      for (j=1; j<nnodes; j++) {
	if (pdata->node.in[j] == -1) {
	  tdata->node.watch_in[j] = -1;
	  tdata->killed_from_above[tdata->nkfa++] = j;
	}
	if (pdata->node.out[j] == -1) {
	  tdata->node.watch_out[j] = -1;
	  tdata->killed_from_below[tdata->nkfb++] = j;
	}
      }
    }
  }
  ent = mddi_propagate(wam, pdata, FALSE);
  if (ent==1)
    Pfree;
  dvar_export_done(wam, Actions, ent);
}

#undef DVAR
#define DVAR(I) (pdata->dvar+(I))

/*
  '$fd_dc_linear'(+State0, -State, -Actions).
  State = state(CX,N,RHS,N,Handle,Stamp) where CX are all non-ground
*/
void SPCDECL
prolog_fd_dc_linear(Wam wam,
		    SP_term_ref State0,
		    SP_term_ref State,
		    SP_term_ref Actions)
{
  TAGGED handle;
  SP_BOOL committed, posted;
  SP_integer state_stamp;
  struct mddi_data *pdata = NULL;
  int i, j, nvars, ent = -1;

/*    X(0) = RefTerm(State0); */
  (void)State0;
  dvar_export_start(wam);
  RefTerm(State) = fd_unify_output_state(wam, &handle,&state_stamp,&committed);

  if (!IsVar(handle)) {		/* got [Flag | '$free'(Ptr)] */
    posted = FALSE;
    ent = 0;
    pdata = Pdata(struct mddi_data,handle);
  } else {			      /* build persistent state */
    TAGGED tvec, telt, *supported;
    int nnodes=0, nedges=0, nvarvals=0;
    SP_integer *lb, *ub, *offset, *coeff, rhs, acmin, acmax;
    Dvar dvars;
    SP_globref refbase;
    char *x = NULL;
    int *node_map = NULL;

    posted = TRUE;
    DerefArg(telt,X(0),2);	      /* get N */
    nvars = GetSmall_int(telt); 
    DerefArg(telt,X(0),3);	      /* get RHS */
    rhs = GetSmall(telt);
    if (nvars==0) {
      ent = (rhs==0 ? 1 : -1);
      goto ret;
    }
    lb = Malloc(nvars+1,SP_integer);
    ub = Malloc(nvars+1,SP_integer);
    offset = Malloc(nvars+1,SP_integer);
    coeff = Malloc(nvars,SP_integer);
    dvars = Malloc(nvars,struct dvar);
    supported = Malloc(nvars,TAGGED);
    refbase = SP_alloc_globrefs(nvars<<1);

    DerefArg(tvec,X(0),1);	      /* get CX */
    for (i=0; i<nvars; i++) {
      TAGGED t1;
      DerefCar(telt,tvec);
      DerefCdr(tvec,tvec);
      DerefArg(t1,telt,1);
      coeff[i] = GetSmall(t1);
      fd_get_var_and_attr(telt+WD(1),refbase+2*i);
      dvar_init(dvars+i, refbase+2*i, refbase+2*i+1);
    }

    acmin = acmax = 0;
    lb[0] = ub[0] = 0;
    for (i=0; i<nvars-1; i++) {
      if (coeff[i]>0) {
	acmin += coeff[i]*dvar_min_l(dvars+i);
	acmax += coeff[i]*dvar_max_l(dvars+i);
      } else {
	acmin += coeff[i]*dvar_max_l(dvars+i);
	acmax += coeff[i]*dvar_min_l(dvars+i);
      }
      lb[i+1] = acmin;
      ub[i+1] = acmax;
    }

    acmin = acmax = rhs;
    lb[nvars] = ub[nvars] = rhs;
    for (i=nvars-1; i>0; i--) {
      if (coeff[i]<0) {
	acmin -= coeff[i]*dvar_min_l(dvars+i);
	acmax -= coeff[i]*dvar_max_l(dvars+i);
      } else {
	acmin -= coeff[i]*dvar_max_l(dvars+i);
	acmax -= coeff[i]*dvar_min_l(dvars+i);
      }
      lb[i] = acmin > lb[i] ? acmin : lb[i];
      ub[i] = acmax < ub[i] ? acmax : ub[i];
      if (lb[i]>ub[i])
	goto fail;
    }
				/* compute offset[i] = offset into array x for row i */
    offset[0] = 0;
    for (i=0; i<nvars; i++) {
      offset[i+1] = offset[i] + ub[i] - lb[i] + 1;
    }
    x = Malloc(offset[nvars]+1,char);
    node_map = Malloc(offset[nvars]+1,int);
    memset(x,0,offset[nvars]+1);
				/* build reachability graph */
    x[0] = 1;
    for (i=0; i<nvars; i++) {
      SP_integer off0 = offset[i];
      SP_integer off1 = offset[i+1];
      FDITER it;
      fditer_init(&it, dvar_set(dvars+i));
      while (!fditer_empty(&it)) {
	TAGGED y = fditer_next(&it);
	SP_integer cy = coeff[i]*GetSmall(y);
	SP_integer sofar = lb[i]+cy > lb[i+1] ? lb[i]+cy : lb[i+1];
	for (j=(int)(off0 + sofar - (lb[i]+cy));
	     j<off1 && sofar <= ub[i+1];
	     j++, sofar++) {
	  if (x[j])
	    x[off1 + sofar - lb[i+1]] = 1;
	}
      }
    }
    if (!x[offset[nvars]])
      goto fail;
    ent = 0;
    x[offset[nvars]] = 3;
    for (i=nvars-1; i>=0; i--) {
      SP_integer off0 = offset[i];
      SP_integer off1 = offset[i+1];
      SP_integer off2 = i+2 > nvars ? off1+1 : offset[i+2];
      FDITER it;
      FDCONS cons;
      fdcons_init(&cons);
      fditer_init(&it, dvar_set(dvars+i));
      while (!fditer_empty(&it)) {
	TAGGED y = fditer_next(&it);
	SP_integer cy = coeff[i]*GetSmall(y);
	SP_integer sofar = lb[i+1]-cy > lb[i] ? lb[i+1]-cy : lb[i];
	int support=0;
	for (j=(int)(off1 + sofar - (lb[i+1]-cy));
	     j<off2 && sofar <= ub[i];
	     j++, sofar++) {
	  if (x[j]==3 && x[off0 + sofar - lb[i]]) {
	    x[off0 + sofar - lb[i]] |= 2;
	    nedges++;
	    support=1;
	  }
	}
	if (support) {
	  fdcons_add(wam, &cons,y);
	  nvarvals++;
	}
      }
				/* now all supported values of the var are in cons */
      supported[i] = fdcons_set(&cons);
    }

    for (i=0; i<offset[nvars]; i++) /* DON'T count sink node */
      nnodes += (x[i]==3);
				/* we have enough now to build the mddi frame */
				/* N.B. handle is inly valid up to GC */
    pdata = mddi_alloc_state(wam, nvars,nnodes,nedges,nvarvals,0,1,handle);
    
    DerefArg(tvec,X(0),1);	      /* get CX */
    for (j=0; j<nvars; j++) {
      SP_globref ref = ATTRIBUTE_LOC(0,j);

      DerefCar(telt,tvec);
      DerefCdr(tvec,tvec);
      fd_get_var_and_attr(telt+WD(1),ref);
      dvar_init(DVAR(j), ref, ref+1);
    }
    /* store node data */
    acmin = 0;
    for (i=0; i<nvars; i++) {
      for (j=(int)offset[i]; j<offset[i+1]; j++) {
	if (x[j]==3) {
	  pdata->node.var[acmin] = i;
	  pdata->node.in[acmin] = -1;
	  pdata->node.out[acmin] = -1;
	  node_map[j] = (int)acmin++;
	}
      }
    }
    pdata->node.in[nnodes] = -1;
    pdata->node.out[nnodes] = -1;
    node_map[offset[nvars]] = (int)acmin;
    SP_ASSERT(acmin==nnodes);
				/* store varval data */
    acmin = 0;
    for (i=0; i<nvars; i++) {
      FDITER it;
      fditer_init(&it, supported[i]);
      while (!fditer_empty(&it)) {
	TAGGED y = fditer_next(&it);
	pdata->varval.var[acmin] = i;
	pdata->varval.min[acmin] = y;
	pdata->varval.max[acmin] = y;
	pdata->varval.edges[acmin++] = -1;
      }
    }
    SP_ASSERT(acmin==nvarvals);
				/* store edge data */
    acmin = 0;
    acmax = 0;
    for (i=0; i<nvars; i++) {
      SP_integer off0 = offset[i];
      SP_integer off1 = offset[i+1];
      FDITER it;
      fditer_init(&it, supported[i]);
      while (!fditer_empty(&it)) {
	TAGGED y = fditer_next(&it);
	SP_integer cy = coeff[i]*GetSmall(y);
	SP_integer sofar = lb[i]+cy > lb[i+1] ? lb[i]+cy : lb[i+1];
	for (j=(int)(off0 + sofar - (lb[i]+cy));
	     j<off1 && sofar <= ub[i+1];
	     j++, sofar++) {
	  if (x[j]==3 && x[off1 + sofar - lb[i+1]]==3) {
	    pdata->edge.source[acmin] = node_map[j];
	    pdata->edge.dest[acmin] = node_map[off1 + sofar - lb[i+1]];
	    pdata->edge.varval[acmin] = acmax;
	    pdata->edge.in[acmin] = -1;
	    pdata->edge.out[acmin++] = -1;
	  }	    
	}
	acmax++;
      }
    }
    SP_ASSERT(acmin==nedges);
    SP_ASSERT(acmax==nvarvals);
    mddi_init_state(wam, pdata);	/* can GC */
    for (i=0; i<nvars; i++) {
      dvar_refresh(DVAR(i));
      dvar_fix_set(DVAR(i), supported[i]); /* no GC while pruning! */
      if (dvar_is_integer(DVAR(i)) && (pdata->entailed[0] & EDGE_ALIVE))
	maintain_entailment(wam, pdata,pdata->tuples,i);
    }
  fail:
    Free(lb);
    Free(ub);
    Free(offset);
    Free(coeff);
    Free(dvars);
    Free(supported);
    if (x)
      Free(x);
    if (node_map)
      Free(node_map);
    SP_free_globrefs(refbase,nvars<<1);
    if (ent<0)
      goto ret;
  }
  
  /* RESUME */
  ent = mddi_propagate(wam, pdata, posted);
  if (ent==1)
    Pfree;
 ret:
  dvar_export_done(wam, Actions, ent);
}
