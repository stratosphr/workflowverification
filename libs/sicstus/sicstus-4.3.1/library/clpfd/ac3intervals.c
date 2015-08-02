/*** 

The AC3intervals filtering algorithm for binary relations.

Based on AC3rm, but repacing linked list of supporting tuples by a small array:

Lecoutre, C., Hemery, F., et al (2007). A study of residual supports
in arc consistency. In IJCAI, vol 7, pp 125-130.

Reasoning on intervals as opposed to single values.  X values are
partitioned into intervals.  Ditto for Y values.

For some i in [0..first[0]):
  let j = lit[0].first[i],
      k = lit[0].past[i],
      a = lit[0].a[i],
      b = lit[0].b[i]

Then Y in (support[0].a[m] .. support[0].b[m]), m in [j,k), support X in a..b.

Similarly for Y (index 1 instead).

Optimization 1 (trailing):

  when we found something, update backtrackably (trail) first[*], lit[*].first

Optimization 2 (dicho search)

  the arrays are sorted, so don't use linear search, use dichotomic search

Bad case for trailing: "indomain": value is not used upon trailing.
I.e. exploring all branches of the search.

Bad case for dicho: "step", when trailing would be maximally effective.
I.e. along one branch of the search.
But the cost of dicho is slight.

Conclusion: they are orthogonal, but dicho has better worst-case behavior (indomain).
Also, trailing costs space.

***/

#include "fd.h"
#include "dvars.h"

#define XVAR(T) (pdata->dvar+2*(T)+0)
#define YVAR(T) (pdata->dvar+2*(T)+1)
#define THISVAR(T) (pdata->dvar+2*(T)+this)
#define THATVAR(T) (pdata->dvar+2*(T)+that)

#define DICHO 1

#define EOL 0xfffffffU

struct sched {
  unsigned int next:30;
  unsigned int xy:2;
};

struct ac3intervals_data {
  void (SPCDECL *destructor)(void *);
  void (SPCDECL *daemon)(Wam,void *,SP_globref,TAGGED); /* (wam,handle,attr_ref,global) */
#if MULTI_SP_AWARE
  SPEnv *spenv;
#endif /* MULTI_SP_AWARE */

  SP_globref refbase;		/* static */
  SP_integer stamp;
  int nvartuples;
  int nactive;
  int queue;
  int *active;			/* [nvartuples] */
  int *place;			/* [nvartuples] */
  struct sched *schedule;	/* [nvartuples] */
  /* variable part */
  Dvar dvar;
  SP_integer first[2];
  SP_integer size[2];
  struct {
    TAGGED *a;
    TAGGED *b;
    SP_integer *first;
    SP_integer *past;
  } lit[2];			/* for each a in dom(X/Y) */
  struct {
    TAGGED *a;
    TAGGED *b;
  } support[2];			/* for each reltuple */
  /* space for the above arrays */
};

static void SPCDECL ac3intervals_destructor(void *pdata_v)
{
  struct ac3intervals_data *pdata = (struct ac3intervals_data *)pdata_v;
  FD_SETUP_SPENV(pdata->spenv);

  SP_free_globrefs(pdata->refbase,4*pdata->nvartuples);
  SP_free(pdata);
}

static void SPCDECL 
ac3intervals_daemon(Wam wam, void *vdata, SP_globref attr_ref, TAGGED global)
{
  struct ac3intervals_data *pdata = (struct ac3intervals_data *)vdata;
  TAGGED tstate = RefMutable(CTagToArg(global,1));
  int reltupleno = (int)(attr_ref - pdata->refbase) >> 2;
  SP_integer state_stamp;
  int ar;
  unsigned int xyflag;
  
  pdata->nactive = GetSmall_int(CTagToArg(tstate,7));
  if (pdata->place[reltupleno] >= pdata->nactive)
    return;
  xyflag = ((attr_ref - pdata->refbase) & 0x3) ? 2 : 1;
  ar = Arity(TagToHeadfunctor(tstate));
  state_stamp = GetSmall(CTagToArg(tstate,ar));
  if (pdata->stamp!=state_stamp) { /* non-incremental */
    int f, n;
    for (f = pdata->queue; f != EOL; f = n) {
      n = pdata->schedule[f].next;
      pdata->schedule[f].next = EOL;
      pdata->schedule[f].xy = 0;
    }
    pdata->queue = EOL;
    pdata->stamp = state_stamp;
  }
  if (pdata->queue == EOL) {
    SP_BOOL buried;
    (void)fd_daemon_copy_state(wam, &global,&buried);
    pdata->stamp++;
    fd_enqueue_global(wam, global, 0x7/* DOM, append*/);
  }
  if (!pdata->schedule[reltupleno].xy) {
    pdata->schedule[reltupleno].next = (((unsigned int)pdata->queue) & EOL);
    pdata->queue = reltupleno;
  }
  pdata->schedule[reltupleno].xy = ((pdata->schedule[reltupleno].xy | xyflag) & 3);
}

#if DICHO

/* dichotomic search for first mid | FDle(key,elements[mid]) */
static INLINE SP_integer dicho_le(TAGGED key, TAGGED *elements, SP_integer nelements)
{
  SP_integer inf = 0;
  while (inf<nelements) {
    SP_integer mid = (inf+nelements)>>1;
    if (FDgt(key,elements[mid]))
      inf = mid+1;
    else
      nelements = mid;
  }
  return inf;
}

/* dichotomic search for first mid | FDlt(key,elements[mid]) */
static INLINE SP_integer dicho_lt(TAGGED key, TAGGED *elements, SP_integer nelements)
{
  SP_integer inf = 0;
  while (inf<nelements) {
    SP_integer mid = (inf+nelements)>>1;
    if (FDge(key,elements[mid]))
      inf = mid+1;
    else
      nelements = mid;
  }
  return inf;
}

#endif

/* if THIS interval l has no support in dom(THAT), then add it to cons */
static void seeksupport(Wam wam,
			struct ac3intervals_data *pdata,
			SP_integer l,
			FDCONS *cons,
			int this,
			int current)
{
  int that = (this^1);
  SP_integer first = pdata->lit[this].first[l];
  SP_integer  past = pdata->lit[this].past[l];
  DVITER it;
  TAGGED da, db;
  
  dviter_init(&it, THATVAR(current));
  dviter_next_interval_t(&it, &da, &db);
#if DICHO
  first += dicho_le(da, pdata->support[this].b+first, past-first);
#endif
  while (first<past) {
    if (FDlt(db, pdata->support[this].a[first])) {
      if (dviter_empty(&it))
	break;
      dviter_next_interval_t(&it, &da, &db);
    } else if (FDgt(da, pdata->support[this].b[first])) {
      first++;
    } else {
      return;
    }
  }
  fdcons_add_interval(wam,cons,pdata->lit[this].a[l], pdata->lit[this].b[l]);
}

/* 1 - entailment, 2 - fail */
static unsigned int filter(Wam wam,
			   struct ac3intervals_data *pdata,
			   int this,
			   int current)
{
  DVITER it;
  FDCONS cons;
  SP_integer l, first, past;
  TAGGED da, db;
  int rc;
  int that = (this^1);
  
  /* first, skip THAT support arrays that precede THAT */

  fdcons_init(&cons);

  l = pdata->first[that];
  da = dvar_min_t(THATVAR(current));
  db = dvar_max_t(THATVAR(current));
#if DICHO
  l += dicho_le(da, pdata->lit[that].b+l, pdata->size[that]-l);
#else
  while (FDgt(da,pdata->lit[that].b[l])) l++;
#endif
  if (FDlt(da,pdata->lit[that].a[l]) ||
      FDgt(db,pdata->lit[that].b[l]))
    goto hit;

  /* THAT is contained in a single support interval. */

  first = pdata->lit[that].first[l];
  past = pdata->lit[that].past[l];
  while (first<past) {
    TAGGED a = pdata->support[that].a[first];
    TAGGED b = pdata->support[that].b[first++];
    fdcons_add_interval(wam,&cons,a,b);
  }
  rc = dvar_fix_set(THISVAR(current), fdcons_set(&cons));
  return (rc >= 0 ? 1 : 2);

  /* THAT is not contained in a single support interval */

 hit:

  /** For every THIS interval that intersects THISVAR(current): seek its support **/

  l = pdata->first[this];
  past = pdata->size[this];
  dviter_init(&it, THISVAR(current));
  dviter_next_interval_t(&it, &da, &db);
#if DICHO
  l += dicho_le(da, pdata->lit[this].b+l, past-l);
#endif
  while (l<past) {
    if (FDlt(db,pdata->lit[this].a[l])) {
      if (dviter_empty(&it))
	break;
      dviter_next_interval_t(&it, &da, &db);
    } else if (FDgt(da,pdata->lit[this].b[l])) {
      l++;
    } else {
      seeksupport(wam, pdata, l, &cons, this, current);
      l++;
    }
  }
  rc = dvar_prune_set(THISVAR(current), fdcons_set(&cons));
  return (rc >= 0 ? 0 : 2);
}

/*
  '$fd_ac3intervals'(+State0, +State, -Actions).
  State = state(VarTuples,NVarTuples,Size[0],Size[1],Reltuples,Inverse,NActive,Handle,Stamp)
  Reltuples = list of t(A,B,A,B)
  Inverse = list of t(A,B,A,B)
*/
void SPCDECL
prolog_fd_ac3intervals(Wam wam,
		       SP_term_ref State0,
		       SP_term_ref State,
		       SP_term_ref Actions)
{
  int ent = -1;			/* initially disentailed */
  TAGGED handle;
  struct ac3intervals_data *pdata;
  SP_BOOL committed;
  char *ptr;

  (void)State0;                 /* [PM] 3.9b5 avoid -Wunused */
/*    X(0) = RefTerm(State0); */
  dvar_export_start(wam);
  RefTerm(State) = fd_static_output_state(wam,&handle,&committed);
  if (!IsVar(handle)) {		/* got [Flag | '$free'(Ptr)] */
    pdata = Pdata(struct ac3intervals_data,handle);
  } else {			/* build persistent state */
    TAGGED reltuples[2], cur, vartuples;
    SP_integer size[2], sizet[2], extra_size;
    int i, j, k, nvartuples;

    DerefArg(cur,X(0),2);
    nvartuples = GetSmall_int(cur);
    DerefArg(cur,X(0),3);
    size[0] = GetSmall(cur);
    DerefArg(cur,X(0),4);
    size[1] = GetSmall(cur);
    DerefArg(reltuples[0], X(0), 5);	/* get Reltuples */
    DerefArg(reltuples[1], X(0), 6);	/* get Inverse */
    CTagToArg(X(0),5) = atom_nil; /* free for GC */
    CTagToArg(X(0),6) = atom_nil; /* free for GC */
    sizet[0] = fd_list_length(reltuples[0]);
    sizet[1] = fd_list_length(reltuples[1]);
    extra_size = (2*sizet[0] + 2*sizet[1] + 4*size[0] + 4*size[1])*sizeof(TAGGED) +
      2*nvartuples*sizeof(struct dvar) +
      3*nvartuples*sizeof(int);
    pdata = Palloc(struct ac3intervals_data, extra_size, handle);
    ptr = (char *)(pdata+1);
    pdata->dvar = (Dvar)ptr;
    ptr += 2*nvartuples*sizeof(struct dvar);
    pdata->lit[0].a = (TAGGED *)ptr;
    ptr += sizeof(TAGGED)*size[0];
    pdata->lit[0].b = (TAGGED *)ptr;
    ptr += sizeof(TAGGED)*size[0];
    pdata->lit[0].first = (SP_integer *)ptr;
    ptr += sizeof(SP_integer)*size[0];
    pdata->lit[0].past = (SP_integer *)ptr;
    ptr += sizeof(SP_integer *)*size[0];
    pdata->lit[1].a = (TAGGED *)ptr;
    ptr += sizeof(TAGGED)*size[1];
    pdata->lit[1].b = (TAGGED *)ptr;
    ptr += sizeof(TAGGED)*size[1];
    pdata->lit[1].first = (SP_integer *)ptr;
    ptr += sizeof(SP_integer)*size[1];
    pdata->lit[1].past = (SP_integer *)ptr;
    ptr += sizeof(SP_integer *)*size[1];
    pdata->support[0].a = (TAGGED *)ptr;
    ptr += sizeof(TAGGED)*sizet[0];
    pdata->support[0].b = (TAGGED *)ptr;
    ptr += sizeof(TAGGED)*sizet[0];
    pdata->support[1].a = (TAGGED *)ptr;
    ptr += sizeof(TAGGED)*sizet[1];
    pdata->support[1].b = (TAGGED *)ptr;
    ptr += sizeof(TAGGED)*sizet[1];
    pdata->active = (int *)ptr;
    ptr += sizeof(int)*nvartuples;
    pdata->place = (int *)ptr;
    ptr += sizeof(int)*nvartuples;
    pdata->schedule = (struct sched *)ptr;
    ptr += sizeof(struct sched)*nvartuples;
    SP_ASSERT(ptr==(char *)(pdata+1)+extra_size);
    pdata->destructor = ac3intervals_destructor;
    pdata->daemon = ac3intervals_daemon;
    FD_STORE_SPENV(pdata->spenv);
    pdata->first[0] = 0;
    pdata->size[0] = size[0];
    pdata->first[1] = 0;
    pdata->size[1] = size[1];
    pdata->stamp = 0;
    pdata->nvartuples = nvartuples;
    pdata->refbase = SP_alloc_globrefs(4*nvartuples);
    DerefArg(vartuples,X(0),1);		/* get var tuples */
    for (i=0; i<nvartuples; i++) {
      TAGGED xvar, yvar;
      DerefCar(cur,vartuples);
      DerefCdr(vartuples,vartuples);
      DerefCar(xvar,cur);
      DerefCdr(cur,cur);
      DerefCar(yvar,cur);
      fd_get_var_and_attr(xvar,pdata->refbase + 4*i);
      fd_get_var_and_attr(yvar,pdata->refbase + 4*i + 2);
    }
    for (k=0; k<2; k++) {
      for (i=0, j=0, cur=0; TagIsLST(reltuples[k]); ) {
	TAGGED car, a, b, c, d;

	DerefCar(car,reltuples[k]); /* car = (A..B)-(C..D) */
	DerefCdr(reltuples[k],reltuples[k]);
	DerefArg(b,car,1);
	DerefArg(d,car,2);
	DerefArg(a,b,1);
	DerefArg(b,b,2);
	DerefArg(c,d,1);
	DerefArg(d,d,2);
	if (a!=cur) {
	  cur = a;
	  pdata->lit[k].a[i] = a;
	  pdata->lit[k].b[i] = b;
	  pdata->lit[k].first[i++] = j;
	}
	pdata->support[k].a[j] = c;
	pdata->support[k].b[j++] = d;
      }
      for (i=0; i<size[k]-1; i++) {
	pdata->lit[k].past[i] = pdata->lit[k].first[i+1];
      }
      pdata->lit[k].past[size[k]-1] = sizet[k];
    }
    pdata->queue = EOL;
    for (i=0; i<nvartuples; i++) {
      SP_globref refoffset = pdata->refbase + 4*i;
      dvar_init(XVAR(i), refoffset,   refoffset+1);
      dvar_init(YVAR(i), refoffset+2, refoffset+3);
      dvar_attach_daemon(wam, XVAR(i), pdata, X(1), fd.functor_dom);
      dvar_attach_daemon(wam, YVAR(i), pdata, X(1), fd.functor_dom);
      pdata->schedule[i].next = (((unsigned int)pdata->queue) & EOL);
      pdata->schedule[i].xy = 3;
      pdata->active[i] = i;
      pdata->place[i] = i;
      pdata->queue = i;
    }
    pdata->nactive = i;
  }

  /* RESUME HERE */
  while (pdata->queue != EOL) {
    unsigned int rc=0, xy;
    int current = pdata->queue;
    int next;

    dvar_refresh(XVAR(current));
    dvar_refresh(YVAR(current));
    xy = pdata->schedule[current].xy;
    if (xy & 1)
      rc |= filter(wam,pdata,1,current);
    if (xy & 2)
      rc |= filter(wam,pdata,0,current);
    if (rc & 2) {
      goto fail;
    } else if (rc) {
      int oldplace = pdata->place[current];
      int newplace = --pdata->nactive;
      int other = pdata->active[newplace];
      pdata->active[oldplace] = other;
      pdata->active[newplace] = current;
      pdata->place[other] = oldplace;
      pdata->place[current] = newplace;      
    }
    dvar_pruning_done(XVAR(current));
    dvar_pruning_done(YVAR(current));
    dvar_export(XVAR(current));
    dvar_export(YVAR(current));
    next = pdata->schedule[current].next;
    pdata->schedule[current].next = EOL;
    pdata->schedule[current].xy = 0;
    pdata->queue = next;
  }

  CTagToArg(X(0),7) = MakeSmall(pdata->nactive);
  ent = (pdata->nactive == 0);
  if (ent==1)
    Pfree;
 fail:
  dvar_export_done(wam,Actions, ent);
}

/*** special case: element/3 (over multiple tuples) ***/

struct ac3element_data {
  void (SPCDECL *destructor)(void *);
  void (SPCDECL *daemon)(Wam,void *,SP_globref,TAGGED); /* (wam,handle,attr_ref,global) */
#if MULTI_SP_AWARE
  SPEnv *spenv;
#endif /* MULTI_SP_AWARE */

  SP_globref refbase;		/* static */
  SP_integer stamp;
  int nvartuples;
  int nactive;			
  int queue;			
  int *active;			/* [nvartuples] */
  int *place;			/* [nvartuples] */
  struct sched *schedule;	/* [nvartuples] */
  /* variable part */
  Dvar dvar;
  int xsize;
  int ysize;
  TAGGED *y;			/* [xsize] */
  struct {
    TAGGED *a;
    TAGGED *b;
    SP_integer *first;
    SP_integer *past;
  } lit;			/* [ysize] */
  struct {
    TAGGED *a;
    TAGGED *b;
  } support;			/* [ysize] */
  /* space for the above arrays */
};

/* 1 - entailment, 2 - fail */
static unsigned int filterx(Wam wam,
			    struct ac3element_data *pdata,
			    int current)
{
  DVITER it;
  FDCONS cons;
  SP_integer l, first, past;
  TAGGED da, db;
  int rc;
  
  fdcons_init(&cons);

  da = dvar_min_t(YVAR(current));
  db = dvar_max_t(YVAR(current));
#if DICHO
  l = dicho_le(da, pdata->lit.b, pdata->ysize);
#else
  l = 0;
  while (Tgt(da,pdata->lit.b[l])) l++;
#endif
  if (Tlt(da,pdata->lit.a[l]) ||
      Tgt(db,pdata->lit.b[l]))
    goto hit;

  /* Y is contained in a single support interval. */

  first = pdata->lit.first[l];
  past = pdata->lit.past[l];
  while (first<past) {
    TAGGED a = pdata->support.a[first];
    TAGGED b = pdata->support.b[first++];
    fdcons_add_interval(wam,&cons,a,b);
  }
  rc = dvar_fix_set(XVAR(current), fdcons_set(&cons));
  return (rc >= 0 ? 1 : 2);

  /* Y is not contained in a single support interval */

 hit:

  /** For every X value: seek its support **/

  past = pdata->xsize;
  dviter_init(&it, XVAR(current));
  while (!dviter_empty(&it)) {
    da = dviter_next_value_t(&it);
    if (!dvar_contains_value_t(YVAR(current), pdata->y[GetSmall_int(da)-1]))
      fdcons_add_interval(wam,&cons,da,da);
  }
  rc = dvar_prune_set(XVAR(current), fdcons_set(&cons));
  return (rc >= 0 ? 0 : 2);
}


/* Y interval l has support in dom(XVAR(current))? */
static SP_BOOL seeksupporty(struct ac3element_data *pdata,
			    SP_integer l,
			    int current)
{
  SP_integer first = pdata->lit.first[l];
  SP_integer  past = pdata->lit.past[l];
  DVITER it;
  TAGGED da, db;
  
  dviter_init(&it, XVAR(current));
  dviter_next_interval_t(&it, &da, &db);
#if DICHO
  first += dicho_le(da, pdata->support.b+first, past-first);
#endif
  while (first<past) {
    if (Tlt(db, pdata->support.a[first])) {
      if (dviter_empty(&it))
	break;
      dviter_next_interval_t(&it, &da, &db);
    } else if (Tgt(da, pdata->support.b[first])) {
      first++;
    } else {
      return TRUE;
    }
  }
  return FALSE;
}

/* 1 - entailment, 2 - fail */
static unsigned int filtery(Wam wam,
			    struct ac3element_data *pdata,
			    int current)
{
  SP_integer l;
  TAGGED tmin, tmax;
  int rc;
  
  if (dvar_is_integer(XVAR(current))) {
    rc = dvar_fix_value_t(YVAR(current), pdata->y[dvar_min_l(XVAR(current))-1]);
    return (rc >= 0 ? 1 : 2);
  }

  /** For every Y interval that intersects YVAR(current): seek its support **/

  tmin = Sup;
  tmax = Inf;
#if DICHO
  l = dicho_le(dvar_min_t(YVAR(current)), pdata->lit.b, pdata->ysize);
#else
  l = 0;
#endif
  while (tmin==Sup && l<pdata->ysize) {
    if (dvar_compare_interval_t(YVAR(current), pdata->lit.a[l], pdata->lit.b[l])!=FDI_DISJOINT &&
	seeksupporty(pdata, l, current))
      tmin = pdata->lit.a[l];
    l++;
  }
  if (tmin==Sup)
    return 2;
#if DICHO
  l = dicho_lt(dvar_max_t(YVAR(current)), pdata->lit.a, pdata->ysize)-1;
#else
  l = pdata->ysize-1;
#endif
  while (tmax==Inf && l>=0) {
    if (dvar_compare_interval_t(YVAR(current), pdata->lit.a[l], pdata->lit.b[l])!=FDI_DISJOINT &&
	(tmin==pdata->lit.a[l] || seeksupporty(pdata, l, current)))
      tmax = pdata->lit.b[l];
    --l;
  }
  rc = dvar_fix_interval_t(YVAR(current), tmin, tmax);
  return (rc >= 0 ? 0 : 2);
}


/*
  '$fd_ac3element'(+State0, +State, -Actions).
  State = state(VarTuples,NVarTuples,XSize,YSize,Ys,Y2X,NActive,Handle,Stamp)
*/
void SPCDECL
prolog_fd_ac3element(Wam wam,
		       SP_term_ref State0,
		       SP_term_ref State,
		       SP_term_ref Actions)
{
  int ent = -1;			/* initially disentailed */
  TAGGED handle;
  struct ac3element_data *pdata;
  SP_BOOL committed;
  char *ptr;

  (void)State0;                 /* [PM] 3.9b5 avoid -Wunused */
/*    X(0) = RefTerm(State0); */
  dvar_export_start(wam);
  RefTerm(State) = fd_static_output_state(wam,&handle,&committed);
  if (!IsVar(handle)) {		/* got [Flag | '$free'(Ptr)] */
    pdata = Pdata(struct ac3element_data,handle);
  } else {			/* build persistent state */
    TAGGED reltuples[2], cur, vartuples;
    SP_integer size[2], sizet[2], extra_size;
    int i, j, nvartuples;

    DerefArg(cur,X(0),2);
    nvartuples = GetSmall_int(cur);
    DerefArg(cur,X(0),3);
    size[0] = GetSmall(cur);
    DerefArg(cur,X(0),4);
    size[1] = GetSmall(cur);
    DerefArg(reltuples[0], X(0), 5);	/* get Ys */
    DerefArg(reltuples[1], X(0), 6);	/* get Inverse */
    CTagToArg(X(0),5) = atom_nil; /* free for GC */
    CTagToArg(X(0),6) = atom_nil; /* free for GC */
    sizet[0] = fd_list_length(reltuples[0]);
    sizet[1] = fd_list_length(reltuples[1]);
    extra_size = (2*sizet[1] + size[0] + 4*size[1])*sizeof(TAGGED) +
      2*nvartuples*sizeof(struct dvar) +
      3*nvartuples*sizeof(int);
    pdata = Palloc(struct ac3element_data, extra_size, handle);
    ptr = (char *)(pdata+1);
    pdata->dvar = (Dvar)ptr;
    ptr += 2*nvartuples*sizeof(struct dvar);
    pdata->y = (TAGGED *)ptr;
    ptr += sizeof(TAGGED)*size[0];
    pdata->lit.a = (TAGGED *)ptr;
    ptr += sizeof(TAGGED)*size[1];
    pdata->lit.b = (TAGGED *)ptr;
    ptr += sizeof(TAGGED)*size[1];
    pdata->lit.first = (SP_integer *)ptr;
    ptr += sizeof(SP_integer)*size[1];
    pdata->lit.past = (SP_integer *)ptr;
    ptr += sizeof(SP_integer *)*size[1];
    pdata->support.a = (TAGGED *)ptr;
    ptr += sizeof(TAGGED)*sizet[1];
    pdata->support.b = (TAGGED *)ptr;
    ptr += sizeof(TAGGED)*sizet[1];
    pdata->active = (int *)ptr;
    ptr += sizeof(int)*nvartuples;
    pdata->place = (int *)ptr;
    ptr += sizeof(int)*nvartuples;
    pdata->schedule = (struct sched *)ptr;
    ptr += sizeof(struct sched)*nvartuples;
    SP_ASSERT(ptr==(char *)(pdata+1)+extra_size);
    pdata->destructor = ac3intervals_destructor;
    pdata->daemon = ac3intervals_daemon;
    FD_STORE_SPENV(pdata->spenv);
    pdata->xsize = (int)size[0];
    pdata->ysize = (int)size[1];
    pdata->stamp = 0;
    pdata->nvartuples = nvartuples;
    pdata->refbase = SP_alloc_globrefs(4*nvartuples);
    DerefArg(vartuples,X(0),1);		/* get var tuples */
    for (i=0; i<nvartuples; i++) {
      TAGGED xvar, yvar;
      DerefCar(cur,vartuples);
      DerefCdr(vartuples,vartuples);
      DerefCar(xvar,cur);
      DerefCdr(cur,cur);
      DerefCar(yvar,cur);
      fd_get_var_and_attr(xvar,pdata->refbase + 4*i);
      fd_get_var_and_attr(yvar,pdata->refbase + 4*i + 2);
    }
    
    for (i=0; TagIsLST(reltuples[0]); ) {
      TAGGED a;
      DerefCar(a,reltuples[0]);
      DerefCdr(reltuples[0],reltuples[0]);
      pdata->y[i++] = a;
    }

    for (i=0, j=0, cur=0; TagIsLST(reltuples[1]); ) {
      TAGGED car, a, b, c, d;
      DerefCar(car,reltuples[1]); /* car = (A..B)-(C..D) */
      DerefCdr(reltuples[1],reltuples[1]);
      DerefArg(b,car,1);
      DerefArg(d,car,2);
      DerefArg(a,b,1);
      DerefArg(b,b,2);
      DerefArg(c,d,1);
      DerefArg(d,d,2);
      if (a!=cur) {
	cur = a;
	pdata->lit.a[i] = a;
	pdata->lit.b[i] = b;
	pdata->lit.first[i++] = j;
      }
      pdata->support.a[j] = c;
      pdata->support.b[j++] = d;
    }
    for (i=0; i<size[1]-1; i++) {
      pdata->lit.past[i] = pdata->lit.first[i+1];
    }
    pdata->lit.past[size[1]-1] = sizet[1];

    pdata->queue = EOL;
    for (i=0; i<nvartuples; i++) {
      SP_globref refoffset = pdata->refbase + 4*i;
      dvar_init(XVAR(i), refoffset,   refoffset+1);
      dvar_init(YVAR(i), refoffset+2, refoffset+3);
      dvar_attach_daemon(wam, XVAR(i), pdata, X(1), fd.functor_dom);
      dvar_attach_daemon(wam, YVAR(i), pdata, X(1), fd.functor_dom);
      pdata->schedule[i].next = (((unsigned int)pdata->queue) & EOL);
      pdata->schedule[i].xy = 3;
      pdata->active[i] = i;
      pdata->place[i] = i;
      pdata->queue = i;
    }
    pdata->nactive = i;
  }

  /* RESUME */

  while (pdata->queue != EOL) {
    unsigned int rc=0, xy;
    int current = pdata->queue;
    int next;

    dvar_refresh(XVAR(current));
    dvar_refresh(YVAR(current));
    xy = pdata->schedule[current].xy;
    if (TRUE) /*(xy & 1) remember, if Y was pruned, bounds may not have support */
      rc |= filtery(wam,pdata,current);
    if (xy & 2)
      rc |= filterx(wam,pdata,current);
    if (rc & 2) {
      goto fail;
    } else if (rc) {
      int oldplace = pdata->place[current];
      int newplace = --pdata->nactive;
      int other = pdata->active[newplace];
      pdata->active[oldplace] = other;
      pdata->active[newplace] = current;
      pdata->place[other] = oldplace;
      pdata->place[current] = newplace;      
    }
    dvar_pruning_done(XVAR(current));
    dvar_pruning_done(YVAR(current));
    dvar_export(XVAR(current));
    dvar_export(YVAR(current));
    next = pdata->schedule[current].next;
    pdata->schedule[current].next = EOL;
    pdata->schedule[current].xy = 0;
    pdata->queue = next;
  }

  CTagToArg(X(0),7) = MakeSmall(pdata->nactive);
  ent = (pdata->nactive == 0);
  if (ent==1)
    Pfree;
 fail:
  dvar_export_done(wam,Actions, ent);
}
