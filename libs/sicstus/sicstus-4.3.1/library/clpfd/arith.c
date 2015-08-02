/* Copyright(C) 1999, Swedish Institute of Computer Science */

#include "fd.h"
#include "dvars.h"

/*
  '$fd_linear'(+State0, -State, -Actions).
  State = state(CX,Op,RHS,Nground,Handle,Stamp) where CX are all non-ground
  Op in [1,2,3,4] means [#=<,#>=,#=,#\=].
*/
void SPCDECL
prolog_fd_linear(Wam wam,
		 SP_term_ref State0,
		 SP_term_ref State,
		 SP_term_ref Actions)
{
  int flags, ent;
  TAGGED telt;

  (void)State0;                 /* [PM] 3.9b5 avoid -Wunused */

  DerefArg(telt,X(0),2);
  /* The special cases bypass dvar_export(), needed by FDBG. */
  /* In linfast.c, the sequence dvar_init() - dvar_export() - dvar_init() doesn't work with FDBG. */
  /* flags = (fd.debugging ? 0 : GetSmall_int(telt)); */
  flags = GetSmall_int(telt);

  if ((flags & 0x30)==0x30) {
    ent = fd_linear_filter_ubool(wam, State);
  } else if (flags & 0x10) {
    ent = fd_linear_filter_pbool(wam, State);
  } else if (flags & 0x8) {
    ent = fd_linear_filter_fast(wam, State);
  } else {
    ent = fd_linear_filter(wam, State);
  }
  if (ent==1)
    Pfree;
  dvar_export_done(wam, Actions, ent);
}

/* returns TRUE on overflow */
/* ripped off from wamfunc.c */
static SP_BOOL long_mult(SP_WORD x, SP_WORD y, SP_WORD *productp)
{
  SP_UWORD lower, tmp;
  UHALF x0, x1, y0, y1;
  int negated;
  
  if (x>>HALFSHIFT==x>>(HALFSHIFT-1) &&
      y>>HALFSHIFT==y>>(HALFSHIFT-1)) {
    *productp = x*y;
    return FALSE;
  }

  negated = 0;
  if (x<0)
    x = -x, negated ^= 1;
  if (y<0)
    y = -y, negated ^= 1;

  x0 = LOWER_UHALF(x);
  x1 = (UHALF)(x>>HALFSHIFT);
  y0 = LOWER_UHALF(y);
  y1 = (UHALF)(y>>HALFSHIFT);

  lower = 0;
  if (x0 && y0) {
    lower = (SP_UWORD)x0*(SP_UWORD)y0;
  }
  if (x1 && y1) {
    return TRUE;
  } else if (x0 && y1) {
    tmp = (SP_UWORD)x0*(SP_UWORD)y1 + (lower>>HALFSHIFT);
    if (tmp>>HALFSHIFT)
      return TRUE;
    else
      lower = (tmp<<HALFSHIFT) + LOWER_UHALF(lower);
  } else if (x1 && y0) {
    tmp = (SP_UWORD)x1*(SP_UWORD)y0 + (lower>>HALFSHIFT);
    if (tmp>>HALFSHIFT)
      return TRUE;
    else
      lower = (tmp<<HALFSHIFT) + LOWER_UHALF(lower);
  }
  if ((SP_WORD)lower<((SP_WORD)0) && (lower<<1 || !negated))
    return TRUE;
  if (negated)
    lower = -lower;
  *productp = lower;
  return FALSE;
}

/* precond: t1 and t2 are tagged integers */
static TAGGED fd_safe_mul_val(TAGGED t1, TAGGED t2)
{
  SP_integer st = GetSmall0(t1);
  SP_integer su = GetSmall0(t2);
  SP_integer prod;
  
  if (!long_mult(st,su,&prod) && IntIsSmall(prod))
    return MakeSmall0(prod);
  else
    return (((st<0)^(su<0)) ? Inf : Sup);
}

/* operations exported to indexical.c */

TAGGED fd_safe_mul(TAGGED t1, TAGGED t2)
{
  if (TagIsSmall(t1)) {
    if (TagIsSmall(t2))
      t1 = fd_safe_mul_val(t1,t2);
    else if (Teqz(t1))
      return ERRORTAG;	/* 0*inf -> give up */
    else
      t1 = (Tltz(t1)^(t2==Inf) ? Inf : Sup);
  } else if (TagIsSmall(t2)) {
    if (Teqz(t2))
      t1 = ERRORTAG;		/* inf*0 -> give up */
    else
      t1 = (Tltz(t2)^(t1==Inf) ? Inf : Sup);
  } else
    t1 = (t1!=t2 ? Inf : Sup);
  return t1;
}

/* ceiling of quotient, SmallxSmall */
static TAGGED fd_safe_divu_val(TAGGED t1, TAGGED t2)
{
  if (Tnez(t2)) {
    SP_WORD x = GetSmall0(t1);
    SP_WORD y = GetSmall0(t2);
    SP_WORD q = (  x==0 ? 0
		: (x<0)^(y<0) ? x/y 
		: x>0 ? (x+y-1)/y
		: (x+y+1)/y
		);
    t1 = MakeSmall0(q);
  } else if (Tltz(t1))
    t1 = Inf;
  else if (Tgtz(t1))
    t1 = Sup;
  else
    t1 = ERRORTAG;	/* 0/0 -> give up */
  return t1;
}


/* ceiling of quotient */
TAGGED fd_safe_divu(TAGGED t1, TAGGED t2)
{
  if (TagIsSmall(t1)) {
    if (TagIsSmall(t2))
      t1 = fd_safe_divu_val(t1,t2);
    else if (Tnez(t1))	/* +/sup, -/inf -> 1 */
      t1 = (Tltz(t1)^(t2==Inf) ? TaggedZero : TaggedOne);
  } else if (TagIsSmall(t2))
    t1 = (Tltz(t2)^(t1==Inf) ? Inf : Sup);
  else
    t1 = ERRORTAG;		/* inf/inf -> give up */
  return t1;
}

/* floor of quotient, SmallxSmall */
static TAGGED fd_safe_divd_val(TAGGED t1, TAGGED t2)
{
  if (Tnez(t2)) {
    SP_WORD x = GetSmall0(t1);
    SP_WORD y = GetSmall0(t2);
    SP_WORD q = ( x==0 ? 0
	       : (x<0)^(y>0) ? x/y 
	       : x>0 ? (x-y-1)/y
	       : (x-y+1)/y
	       );
	      
    t1 = MakeSmall0(q);
  } else if (Tltz(t1))
    t1 = Inf;
  else if (Tgtz(t1))
    t1 = Sup;
  else
    t1 = ERRORTAG;	/* 0/0 -> give up */
  return t1;
}

/* floor of quotient */
TAGGED fd_safe_divd(TAGGED t1, TAGGED t2)
{
  if (TagIsSmall(t1)) {
    if (TagIsSmall(t2)) {
      t1 = fd_safe_divd_val(t1,t2);
    } else if (Tnez(t1))	/* -/sup, +/inf -> -1 */
      t1 = (Tltz(t1)^(t2==Sup) ? TaggedZero : TaggedMinusOne);
  } else if (TagIsSmall(t2))
    t1 = (Tltz(t2)^(t1==Inf) ? Inf : Sup);
  else
    t1 = ERRORTAG;		/* inf/inf -> give up */
  return t1;
}

TAGGED fd_safe_plus(TAGGED t1, TAGGED t2)
{
  if (TagIsSmall(t1)) {
    if (TagIsSmall(t2)) {
      TADDCHK(t1,t2);
    } else
      t1 = t2;
  }
  return t1;
}

TAGGED fd_safe_minus(TAGGED t1, TAGGED t2)
{
  if (TagIsSmall(t1)) {
    if (TagIsSmall(t2)) {
      TSUBCHK(t1,t2);
    } else
      t1 = (t2==Inf ? Sup : Inf);
  }
  return t1;
}

static TAGGED fd_safe_negate_val(TAGGED t1)
{
  TAGGED t2 = Tminus(t1);
  return (t2==TaggedZero ? t2 : t2!=t1 ? t2 : Sup);
}

static TAGGED fd_safe_negate(TAGGED t1)
{
  return (TagIsSmall(t1) ? fd_safe_negate_val(t1) : t1==Inf ? Sup : Inf);
}

static TAGGED fd_safe_min(TAGGED t1,TAGGED t2)
{
  return (FDlt(t1,t2) ? t1 : t2);
}

static TAGGED fd_safe_max(TAGGED t1,TAGGED t2)
{
  return (FDgt(t1,t2) ? t1 : t2);
}

static SP_integer fast_modi(SP_integer i1, SP_integer i2)
{
  SP_integer value = i1 % i2;
  if (value != 0 && (value < 0) != (i2 < 0))
    value += i2;
  return value;
}

struct arith_data {
  void (SPCDECL *destructor)(void *);
  SPEnv *spenv;
  SP_globref refbase;
  Dvar dvar;
  int nrefs;
};

#define xmin dvar_min_t(dvx)
#define xmax dvar_max_t(dvx)
#define ymin dvar_min_t(dvy)
#define ymax dvar_max_t(dvy)
#define zmin dvar_min_t(dvz)
#define zmax dvar_max_t(dvz)

static void SPCDECL arith_destructor(void *pdata_v)
{
  struct arith_data *pdata = (struct arith_data*)pdata_v;
  FD_SETUP_SPENV(pdata->spenv)

  SP_free_globrefs(pdata->refbase,pdata->nrefs);
  SP_free(pdata);
}

/*
  '$fd_square'(State0, State, -Actions).
  State = state(X, XMut, Y, YMut)
*/
void SPCDECL
prolog_fd_square(Wam wam,
		 SP_term_ref State,
		 SP_term_ref NewState,
		 SP_term_ref Actions)
{
  Dvar dvx, dvy;
  TAGGED newmin=0, newmax=0;
  TAGGED sqrt_ymin, sqrt_ymax;
  TAGGED handle;
  SP_BOOL committed;
  struct arith_data *pdata;
  int change, ent = -1;			/* disentailed unless otherwise */

  (void)State;                 /* [PM] 3.9b5 avoid -Wunused */
/*    X(0) = RefTerm(State0); */
  dvar_export_start(wam);
  RefTerm(NewState) = fd_static_output_state(wam,&handle,&committed);
  if (!IsVar(handle)) {		/* got [Flag | '$free'(Ptr)] */
    pdata = Pdata(struct arith_data,handle);
  } else {			/* build persistent state */
    pdata = Palloc(struct arith_data, 2*sizeof(struct dvar), handle);
    pdata->destructor = arith_destructor;
    pdata->nrefs = 4;
    FD_STORE_SPENV(pdata->spenv);
    pdata->refbase = SP_alloc_globrefs(4);
    pdata->dvar = (Dvar)(pdata+1);
    fd_get_var_and_attr(X(0),pdata->refbase);
    fd_get_var_and_attr(X(0)+WD(2),pdata->refbase+2);
    dvar_init(pdata->dvar,   pdata->refbase,   pdata->refbase+1);
    dvar_init(pdata->dvar+1, pdata->refbase+2, pdata->refbase+3);
  }

  /* RESUME HERE */
  dvx = pdata->dvar;
  dvy = dvx+1;
  dvar_refresh(dvx);
  dvar_refresh(dvy);

  do {
    change = 0;
    SP_ASSERT(Tgez(ymin));
    /* [PM] 4.3 casting from long 64-bit SP_WORD to 64-bit double will
       discard (ca 10) significant digits. This should be safe as
       argument to sqrt() */
    sqrt_ymin = MakeSmall0(ceil(sqrt((double) GetSmall0(ymin))));
    if (TagIsSmall(ymax)) {
      SP_ASSERT(Tgez(ymax));
      /* [PM] 4.3 See above for why casting to double is safe as argument to sqrt() */
      sqrt_ymax = MakeSmall0(floor(sqrt((double) GetSmall0(ymax))));
    } else {
      sqrt_ymax = ymax;
    }
    if (AreSmall(xmin,xmax) && Tgez(xmin) && SquareIntIsSmall(GetSmall0(xmax))) {
      /* common, fast case, no overflows, inf, sup, x >= 0 */
      change |= dvar_fix_interval_t(dvx, sqrt_ymin, sqrt_ymax);
      if (change<0)
	goto ret;
      newmin = MakeSmall0(GetSmall0(xmin)*GetSmall0(xmin));
      newmax = MakeSmall0(GetSmall0(xmax)*GetSmall0(xmax));
      change |= dvar_fix_interval_t(dvy, newmin, newmax);
      if (change<0)
	goto ret;
    } else {
      /* slow, careful case */
      switch ((!TagIsSmall(xmin) ? 0x0 : Tltz(xmin) ? 0x4 : 0x8)+
	      (!TagIsSmall(xmax) ? 0x0 : Tltz(xmax) ? 0x1 : 0x2)) {
      case 0x0:		/* inf..sup */
      case 0x2:		/* inf..+b */
      case 0x4:		/*  -a..sup */
	newmin = ymin;
	newmax = ymax;
	break;
      case 0x1:		/* inf..-b => b^2..sup */
	newmin = fd_safe_mul_val(xmax,xmax);
	newmax = ymax;
	break;
      case 0x5:		/*  -a..-b => b^2..a^2 */
	newmin = fd_safe_mul_val(xmax,xmax);
	newmax = fd_safe_mul_val(xmin,xmin);
	break;
      case 0x6:		/*  -a..b => 0..max(a^2,b^2) */
	if (Tlt(xmin,Tminus(xmax)))
	  newmax = fd_safe_mul_val(xmin,xmin);
	else
	  newmax = fd_safe_mul_val(xmax,xmax);
	break;
      case 0x8:		/* a..sup => a^2..sup */
	newmin = fd_safe_mul_val(xmin,xmin);
	newmax = ymax;
	break;
      case 0xa:		/*  a..b => a^2..b^2 */
	newmin = fd_safe_mul_val(xmin,xmin);
	newmax = fd_safe_mul_val(xmax,xmax);
	break;
      }
      {
	TAGGED ymin_align = MakeSmall0(GetSmall0(sqrt_ymin)*GetSmall0(sqrt_ymin));
    
	if (FDgt(ymin_align,newmin))
	  newmin = ymin_align;
      }
      change |= dvar_fix_interval_t(dvy, newmin, newmax);
      if (change<0)
	goto fail_or_oflo;
      newmin = fd_safe_negate(sqrt_ymax);
      newmax = fd_safe_negate(sqrt_ymin);
      if (dvar_compare_interval_t(dvx, newmin, newmax)==FDI_DISJOINT) {
	newmin = sqrt_ymin;
	newmax = sqrt_ymax;
      } else if (dvar_compare_interval_t(dvx, sqrt_ymin, sqrt_ymax)!=FDI_DISJOINT) {
	newmax = sqrt_ymax;
      }
      change |= dvar_fix_interval_t(dvx, newmin, newmax);
      if (change<0)
	goto fail_or_oflo;
    }
  } while (change);
  
  ent = !!dvar_is_integer(dvx); /* 4.3 */
  
  dvar_export(dvx);
  dvar_export(dvy);
  goto ret;
  
fail_or_oflo:
  if (newmin==Sup)
    fd.fd_overflow = 2;		/* FD integer overflow */
  else if (newmax==Inf)
    fd.fd_overflow = 1;		/* FD integer underflow */
 ret:
  if (ent==1)
    Pfree;
  dvar_export_done(wam, Actions, ent);
}

#define ARITH_DISPATCH(xmin,xmax,ymin,ymax)			\
         ((!TagIsSmall(xmin) ? 0x00 :				\
	   Tltz(xmin) ? 0x40 : Teqz(xmin) ? 0x80 : 0xc0)+	\
	  (!TagIsSmall(xmax) ? 0x30 :				\
	   Tltz(xmax) ? 0x00 : Teqz(xmax) ? 0x10 : 0x20)+	\
	  (!TagIsSmall(ymin) ? 0x00 :				\
	   Tltz(ymin) ? 0x04 : Teqz(ymin) ? 0x08 : 0x0c)+	\
	  (!TagIsSmall(ymax) ? 0x03 :				\
	   Tltz(ymax) ? 0x00 : Teqz(ymax) ? 0x01 : 0x02))	\

/*
  '$fd_product'(State0, State, Actions).
  State = state(X, XMut, Y, YMut, Z, ZMut)
*/
void SPCDECL
prolog_fd_product(Wam wam,
		  SP_term_ref State,
		  SP_term_ref NewState,
		  SP_term_ref Actions)
{
  Dvar dvx, dvy, dvz;
  TAGGED newmin, newmax, axmin, axmax, aymin, aymax, azmin, azmax;
  TAGGED handle;
  SP_BOOL committed;
  struct arith_data *pdata;
  int ent = -1;			/* disentailed unless otherwise */
  int iter;

  (void)State;                 /* [PM] 3.9b5 avoid -Wunused */
/*    X(0) = RefTerm(State0); */
  dvar_export_start(wam);
  RefTerm(NewState) = fd_static_output_state(wam,&handle,&committed);
  if (!IsVar(handle)) {		/* got [Flag | '$free'(Ptr)] */
    pdata = Pdata(struct arith_data,handle);
  } else {			/* build persistent state */
    pdata = Palloc(struct arith_data, 3*sizeof(struct dvar), handle);
    pdata->destructor = arith_destructor;
    pdata->nrefs = 6;
    FD_STORE_SPENV(pdata->spenv);
    pdata->refbase = SP_alloc_globrefs(6);
    pdata->dvar = (Dvar)(pdata+1);
    fd_get_var_and_attr(X(0),pdata->refbase);
    fd_get_var_and_attr(X(0)+WD(2),pdata->refbase+2);
    fd_get_var_and_attr(X(0)+WD(4),pdata->refbase+4);
    dvar_init(pdata->dvar  , pdata->refbase,   pdata->refbase+1);
    dvar_init(pdata->dvar+1, pdata->refbase+2, pdata->refbase+3);
    dvar_init(pdata->dvar+2, pdata->refbase+4, pdata->refbase+5);
  }

  /* RESUME HERE */
  dvx = pdata->dvar;
  dvy = dvx+1;
  dvz = dvx+2;
  dvar_refresh(dvx);
  dvar_refresh(dvy);
  dvar_refresh(dvz);
#include "mul-iteration.ic"
  ent = (dvar_is_integer(dvx) &&
 	 dvar_is_integer(dvy) &&
 	 dvar_is_integer(dvz));
  dvar_pruning_done(dvx);
  dvar_pruning_done(dvy);
  dvar_pruning_done(dvz);
  dvar_export(dvx);
  dvar_export(dvy);
  dvar_export(dvz);
/*   ent = (!dvar_is_integer_first(dvx) + */
/* 	 !dvar_is_integer_first(dvy) + */
/* 	 !dvar_is_integer_first(dvz) <= 1); */
  goto ret;
 ret:
  if (ent==1)
    Pfree;
  dvar_export_done(wam, Actions, ent);
}

#if 0

/* Can be linear in the size of some domain.
   Can be wrong, witness SPRM 11282:

   | ?- domain([T,I,Q], -100000,32000000),T#=((I*Q))/100.

   T in-100000..2684354,

   I in-100000..32000000,

   Q in-100000..32000000 ?

*/

static TAGGED fast_div(TAGGED t1, TAGGED t2)
{
  return MakeSmall0(GetSmall0(t1)/GetSmall0(t2));
}

static TAGGED fast_mod(TAGGED t1, TAGGED t2)
{
  SP_integer dividend = GetSmall0(t1);
  SP_integer divisor  = GetSmall0(t2);
  SP_integer value = dividend % divisor;
  if (value != 0 && (value < 0) != (divisor < 0))
    value += divisor;
  return MakeSmall0(value);
}

static TAGGED fast_rem(TAGGED t1, TAGGED t2)
{
  return MakeSmall0(GetSmall0(t1)%GetSmall0(t2));
}

static SP_BOOL
is_bound_consistent(Wam wam,
		    Dvar dvx,
		    int i,  /* 0=x, 1=y, 2=z */
		    int op, /* 0=div, 1=mod, 2=rem */
		    TAGGED val)
{
  TAGGED x = i==0 ? val : dvar_min_t(dvx+0);
  TAGGED y = i==1 ? val : dvar_min_t(dvx+1);
  TAGGED z = i==2 ? val : dvar_min_t(dvx+2);
  TAGGED ax, ay, az, newmin=0, newmax=0, support=0;
  int key, ground=0;

  if (i==1 && y==TaggedZero)
    return TRUE;

  key = ((i==0 || dvar_is_integer(dvx+0))<<0)+
        ((i==1 || dvar_is_integer(dvx+1))<<1)+
        ((i==2 || dvar_is_integer(dvx+2))<<2);
  
  switch (key) {
  case 3:
    i = 2;
    support = dvar_set(dvx+2);
    break;
  case 5:
    i = 1;
    support = dvar_set(dvx+1);
    break;
  case 6:
    i = 0;
    support = dvar_set(dvx+0);
    break;
  case 7:
    ground = 1;
    i = 2;
    break;
  default:
    return TRUE;
  }
  switch (3*op+i) {
   case 0:
    ay = (Tgez(y) ? y : fd_safe_negate_val(y));
    az = (Tgez(z) ? z : fd_safe_negate_val(z));
    /* AUTOMATICALLY GENERATED CASES FOR div SHAVING, CHECKING DVAR dvx */
    if (Tlt(y,TaggedZero)) {
      if (Tlt(z,TaggedZero)) {
        newmin = fd_safe_mul(wam, ay,az);
        newmax = fd_safe_minus(fd_safe_plus(fd_safe_mul(wam, ay,az),ay),TaggedOne);
      } else if (Teq(z,TaggedZero)) {
        newmax = fd_safe_minus(ay,TaggedOne);
        newmin = fd_safe_negate(newmax);
      } else {
        newmin = fd_safe_minus(TaggedOne,fd_safe_plus(fd_safe_mul(wam, ay,az),ay));
        newmax = fd_safe_negate(fd_safe_mul(wam, ay,az));
      }
    } else if (Tlt(z,TaggedZero)) {
      newmin = fd_safe_minus(TaggedOne,fd_safe_plus(fd_safe_mul(wam, ay,az),ay));
      newmax = fd_safe_negate(fd_safe_mul(wam, ay,az));
    } else if (Teq(z,TaggedZero)) {
      newmax = fd_safe_minus(ay,TaggedOne);
      newmin = fd_safe_negate(newmax);
    } else {
      newmin = fd_safe_mul(wam, ay,az);
      newmax = fd_safe_minus(fd_safe_plus(fd_safe_mul(wam, ay,az),ay),TaggedOne);
    }
    break;

   case 1:
    ax = (Tgez(x) ? x : fd_safe_negate_val(x));
    az = (Tgez(z) ? z : fd_safe_negate_val(z));
    /* AUTOMATICALLY GENERATED CASES FOR div SHAVING, CHECKING DVAR dvy */
    if (Tlt(x,TaggedZero)) {
      if (Tlt(z,TaggedZero)) {
        newmin = fd_safe_divu(fd_safe_plus(ax,TaggedOne),fd_safe_plus(az,TaggedOne));
        newmax = fd_safe_divd(ax,az);
      } else if (Tgt(z,TaggedZero)) {
        newmin = fd_safe_negate(fd_safe_divd(ax,az));
        newmax = fd_safe_negate(fd_safe_divu(fd_safe_plus(ax,TaggedOne),fd_safe_plus(az,TaggedOne)));
      } else {
        newmin = fd_safe_negate(ax);
        newmax = ax;
        support = fd_subtract_interval(wam, support,newmin,newmax);
        if (support==EmptySet)
          goto ret;
        return TRUE;
      }
    } else if (Teq(x,TaggedZero)) {
      if (Tne(z,TaggedZero)) {
        goto ret;
      } else {
        return TRUE;
      }
    } else if (Tlt(z,TaggedZero)) {
      newmin = fd_safe_negate(fd_safe_divd(ax,az));
      newmax = fd_safe_negate(fd_safe_divu(fd_safe_plus(ax,TaggedOne),fd_safe_plus(az,TaggedOne)));
    } else {
      if (Tgt(z,TaggedZero)) {
        newmin = fd_safe_divu(fd_safe_plus(ax,TaggedOne),fd_safe_plus(az,TaggedOne));
        newmax = fd_safe_divd(ax,az);
      } else {
        newmin = fd_safe_negate(ax);
        newmax = ax;
        support = fd_subtract_interval(wam, support,newmin,newmax);
        if (support==EmptySet)
          goto ret;
        return TRUE;
      }
    }
    break;

   case 2:
    /* AUTOMATICALLY GENERATED CASES FOR div SHAVING, CHECKING DVAR dvz */
    newmin = newmax = fast_div(x,y);
    break;

   case 3:
    ay = (Tgez(y) ? y : fd_safe_negate_val(y));
    az = (Tgez(z) ? z : fd_safe_negate_val(z));
    /* AUTOMATICALLY GENERATED CASES FOR mod SHAVING, CHECKING DVAR dvx */
    if (Tlt(y,TaggedZero)) {
      if (Tlt(z,TaggedZero)) {
        newmin = TaggedZero;
        newmax = newmin;
        support = fd_subtract_interval(wam, support,newmin,newmax);
        if (support==EmptySet)
          goto ret;
        return TRUE;
      } else if (Teq(z,TaggedZero)) {
        return TRUE;
      } else {
        goto ret;
      }
    } else if (Tlt(z,TaggedZero)) {
      goto ret;
    } else if (Teq(z,TaggedZero)) {
      return TRUE;
    } else {
      newmin = TaggedZero;
      newmax = newmin;
      support = fd_subtract_interval(wam, support,newmin,newmax);
      if (support==EmptySet)
        goto ret;
      return TRUE;
    }
    break;

   case 4:
    ax = (Tgez(x) ? x : fd_safe_negate_val(x));
    az = (Tgez(z) ? z : fd_safe_negate_val(z));
    /* AUTOMATICALLY GENERATED CASES FOR mod SHAVING, CHECKING DVAR dvy */
    if (Tlt(x,TaggedZero)) {
      if (Tlt(z,TaggedZero)) {
        if (Teq(ax,az)) {
          newmin = fd_safe_negate(ax);
          newmax = fd_safe_negate(fd_safe_plus(fd_safe_minus(ax,az),TaggedOne));
          support = fd_subtract_interval(wam, support,newmin,newmax);
          if (support==EmptySet)
            goto ret;
          newmin = Inf;
          newmax = fd_safe_negate(fd_safe_plus(az,TaggedOne));
        } else {
          newmin = fd_safe_minus(az,ax);
          newmax = fd_safe_negate(fd_safe_plus(az,TaggedOne));
        }
      } else if (Tgt(z,TaggedZero)) {
        newmin = fd_safe_plus(az,TaggedOne);
        newmax = fd_safe_plus(ax,az);
      } else {
        newmin = fd_safe_negate(ax);
        newmax = ax;
      }
    } else if (Teq(x,TaggedZero)) {
      if (Tne(z,TaggedZero)) {
        goto ret;
      } else {
        return TRUE;
      }
    } else if (Tlt(z,TaggedZero)) {
      newmin = fd_safe_negate(fd_safe_plus(ax,az));
      newmax = fd_safe_negate(fd_safe_plus(az,TaggedOne));
    } else {
      if (Tgt(z,TaggedZero)) {
        if (Teq(ax,az)) {
          newmin = fd_safe_plus(fd_safe_minus(ax,az),TaggedOne);
          newmax = ax;
          support = fd_subtract_interval(wam, support,newmin,newmax);
          if (support==EmptySet)
            goto ret;
          newmin = fd_safe_plus(az,TaggedOne);
          newmax = Sup;
        } else {
          newmin = fd_safe_plus(az,TaggedOne);
          newmax = fd_safe_minus(ax,az);
        }
      } else {
        newmin = fd_safe_negate(ax);
        newmax = ax;
      }
    }
    break;

   case 5:
    /* AUTOMATICALLY GENERATED CASES FOR mod SHAVING, CHECKING DVAR dvz */
    newmin = newmax = fast_mod(x,y);
    break;

   case 6:
    ay = (Tgez(y) ? y : fd_safe_negate_val(y));
    az = (Tgez(z) ? z : fd_safe_negate_val(z));
    /* AUTOMATICALLY GENERATED CASES FOR rem SHAVING, CHECKING DVAR dvx */
    if (Tlt(z,TaggedZero)) {
      newmin = fd_safe_minus(TaggedOne,fd_safe_plus(ay,az));
      newmax = fd_safe_negate(fd_safe_plus(az,TaggedOne));
      support = fd_subtract_interval(wam, support,newmin,newmax);
      if (support==EmptySet)
        goto ret;
      newmin = Inf;
      newmax = fd_safe_negate(az);
    } else if (Teq(z,TaggedZero)) {
      newmin = TaggedOne;
      newmax = fd_safe_minus(ay,TaggedOne);
      support = fd_subtract_interval(wam, support,newmin,newmax);
      if (support==EmptySet)
        goto ret;
      newmin = fd_safe_minus(TaggedOne,ay);
      newmax = TaggedMinusOne;
      support = fd_subtract_interval(wam, support,newmin,newmax);
      if (support==EmptySet)
        goto ret;
      return TRUE;
    } else {
      newmin = fd_safe_plus(az,TaggedOne);
      newmax = fd_safe_minus(fd_safe_plus(ay,az),TaggedOne);
      support = fd_subtract_interval(wam, support,newmin,newmax);
      if (support==EmptySet)
        goto ret;
      newmin = az;
      newmax = Sup;
    }
    break;

   case 7:
    ax = (Tgez(x) ? x : fd_safe_negate_val(x));
    az = (Tgez(z) ? z : fd_safe_negate_val(z));
    /* AUTOMATICALLY GENERATED CASES FOR rem SHAVING, CHECKING DVAR dvy */
    if (Tlt(x,TaggedZero)) {
      if (Tlt(z,TaggedZero)) {
        newmin = fd_safe_negate(az);
        newmax = az;
        support = fd_subtract_interval(wam, support,newmin,newmax);
        if (support==EmptySet)
          goto ret;
        if (Teq(ax,az)) {
          return TRUE;
        } else {
          newmax = fd_safe_minus(ax,az);
          newmin = fd_safe_negate(newmax);
        }
      } else if (Tgt(z,TaggedZero)) {
        goto ret;
      } else {
        newmin = fd_safe_negate(ax);
        newmax = ax;
      }
    } else if (Teq(x,TaggedZero)) {
      if (Tne(z,TaggedZero)) {
        goto ret;
      } else {
        return TRUE;
      }
    } else if (Tlt(z,TaggedZero)) {
      goto ret;
    } else {
      if (Tgt(z,TaggedZero)) {
        newmin = fd_safe_negate(az);
        newmax = az;
        support = fd_subtract_interval(wam, support,newmin,newmax);
        if (support==EmptySet)
          goto ret;
        if (Teq(ax,az)) {
          return TRUE;
        } else {
          newmax = fd_safe_minus(ax,az);
          newmin = fd_safe_negate(newmax);
        }
      } else {
        newmin = fd_safe_negate(ax);
        newmax = ax;
      }
    }
    break;

   case 8:
    /* AUTOMATICALLY GENERATED CASES FOR rem SHAVING, CHECKING DVAR dvz */
    newmin = newmax = fast_rem(x,y);
    break;
  }
  if (ground) {
    if (newmin==z)
      return TRUE;
  } else {
    if (FDle(newmin,newmax) && support!=EmptySet && fd_compare_interval(support,newmin,newmax)!=FDI_DISJOINT)
      return TRUE;
  }
 ret:
  return FALSE;
}

static SP_BOOL
shave_bounds(Wam wam, Dvar dvx, int op)
{
  int i;
  for (i=0; i<3; i++) {
    {
      TAGGED t = dvar_min_t(dvx+i);
      while (TagIsSmall(t) && !is_bound_consistent(wam, dvx,i,op,t)) { 
	if (dvar_prune_value_t(dvx+i,t)<0)
	  return FALSE;
	t = dvar_min_t(dvx+i);
      }
    }
    if (!dvar_is_integer(dvx+i)) {
      TAGGED t = dvar_max_t(dvx+i);
      while (TagIsSmall(t) && !is_bound_consistent(wam, dvx,i,op,t)) { 
	if (dvar_prune_value_t(dvx+i,t)<0)
	  return FALSE;
	t = dvar_max_t(dvx+i);
      }
    }
  }
  return TRUE;
}
#endif

static SP_BOOL
plan_divmodrem(Wam wam, struct arith_data *pdata) 
{
  Dvar dvx = pdata->dvar;
  int i;
  
  for (i=0; i<3; i++)
    dvar_refresh(dvx+i);

  if (dvar_prune_value_l(dvx+1,0)<0)
    return FALSE;
  return TRUE;
}

static int
dvar_fix_min_safe(Wam wam, Dvar dv, SP_integer lb)
{
  if (IntIsSmall(lb))
    return dvar_fix_min_l(dv,lb);
  else {
    fd.fd_overflow = 2;
    return -1;
  }
}

static int
dvar_fix_max_safe(Wam wam, Dvar dv, SP_integer lb)
{
  if (IntIsSmall(lb))
    return dvar_fix_max_l(dv,lb);
  else {
    fd.fd_overflow = 1;
    return -1;
  }
}

static SP_BOOL
mod_integer(Wam wam, struct arith_data *pdata) 
{
  Dvar dvx = pdata->dvar;
  SP_integer c = dvar_min_l(dvx+1);
  int notinf = TagIsSmall(dvar_min_t(dvx+0));
  int notsup = TagIsSmall(dvar_max_t(dvx+0));
  int rc = 0;

  if (c==0)
    rc = FALSE;
  else if (c>0) {
    SP_integer xi = dvar_max_l(dvx+0) - dvar_min_l(dvx+0) + 1;
    SP_integer xminc = fast_modi(dvar_min_l(dvx+0),c);
    SP_integer xmaxc = fast_modi(dvar_max_l(dvx+0),c);

    if (!notinf || !notsup || xi>=c)
      rc |= dvar_fix_interval_l(dvx+2,0,(c-1));
    else if (xminc <= xmaxc)
      rc |= dvar_fix_interval_l(dvx+2,(xminc),(xmaxc));
    else {
      rc |= dvar_prune_interval_l(dvx+2,(xmaxc+1),(xminc-1));
      rc |= dvar_fix_interval_l(dvx+2,0,(c-1));
    }

    if (notinf) {
      SP_integer q = (dvar_min_l(dvx+0)-xminc)/c;
      if (xminc<dvar_min_l(dvx+2))
	rc |= dvar_fix_min_safe(wam, dvx+0,(c*q+dvar_min_l(dvx+2)));
      else if (xminc>dvar_max_l(dvx+2))
	rc |= dvar_fix_min_safe(wam, dvx+0,(c*(q+1)+dvar_min_l(dvx+2)));
    }
    if (notsup) {
      SP_integer q = (dvar_max_l(dvx+0)-xmaxc)/c;
      if (xmaxc>dvar_max_l(dvx+2))
	rc |= dvar_fix_max_safe(wam, dvx+0,(c*q+dvar_max_l(dvx+2)));
      else if (xmaxc<dvar_min_l(dvx+2))
	rc |= dvar_fix_max_safe(wam, dvx+0,(c*(q-1)+dvar_max_l(dvx+2)));
    }
  } else {
    SP_integer xi = dvar_max_l(dvx+0) - dvar_min_l(dvx+0) + 1;
    SP_integer xminc = fast_modi(dvar_min_l(dvx+0),c);
    SP_integer xmaxc = fast_modi(dvar_max_l(dvx+0),c);

    if (!notinf || !notsup || xi>=-c)
      rc |= dvar_fix_interval_l(dvx+2,(c+1),0);
    else if (xminc <= xmaxc)
      rc |= dvar_fix_interval_l(dvx+2,(xminc),(xmaxc));
    else {
      rc |= dvar_prune_interval_l(dvx+2,(xmaxc+1),(xminc-1));
      rc |= dvar_fix_interval_l(dvx+2,(c+1),0);
    }

    if (notinf) {
      SP_integer q = (dvar_min_l(dvx+0)-xminc)/c;
      if (xminc<dvar_min_l(dvx+2))
	rc |= dvar_fix_min_safe(wam, dvx+0,(c*q+dvar_min_l(dvx+2)));
      else if (xminc>dvar_max_l(dvx+2))
	rc |= dvar_fix_min_safe(wam, dvx+0,(c*(q-1)+dvar_min_l(dvx+2)));
    }
    if (notsup) {
      SP_integer q = (dvar_max_l(dvx+0)-xmaxc)/c;
      if (xmaxc>dvar_max_l(dvx+2))
	rc |= dvar_fix_max_safe(wam, dvx+0,(c*q+dvar_max_l(dvx+2)));
      else if (xmaxc<dvar_min_l(dvx+2))
	rc |= dvar_fix_max_safe(wam, dvx+0,(c*(q+1)+dvar_max_l(dvx+2)));
    }
  }
  return rc>=0;
}

static SP_BOOL
rem_integer(Wam wam, struct arith_data *pdata) 
{
  Dvar dvx = pdata->dvar;
  SP_integer c = dvar_min_l(dvx+1);
  SP_integer uc = (c > 0 ? c : -c);
  int notinf = TagIsSmall(dvar_min_t(dvx+0));
  int notsup = TagIsSmall(dvar_max_t(dvx+0));
  int rc = 0;

  if (c==0) {
    rc = -1;
  } else {
    SP_integer xi = dvar_max_l(dvx+0) - dvar_min_l(dvx+0) + 1;
    SP_integer xminc = dvar_min_l(dvx+0) % uc;
    SP_integer xmaxc = dvar_max_l(dvx+0) % uc;
  
    /* prune Z */
    if ((!notinf || dvar_min_l(dvx+0)<=0) && (!notsup || dvar_max_l(dvx+0)>=0)) {
      SP_integer lb = ((notinf && dvar_min_l(dvx+0) > -uc) ? dvar_min_l(dvx+0) : -uc+1);
      SP_integer ub = ((notsup && dvar_max_l(dvx+0) <  uc) ? dvar_max_l(dvx+0) :  uc-1);
      rc |= dvar_fix_interval_l(dvx+2,(lb),(ub));
    } else if (!notsup || dvar_max_l(dvx+0)>=0) {
      if (!notsup || xi>=uc)
	rc |= dvar_fix_interval_l(dvx+2,0,(uc-1));
      else if (xminc <= xmaxc)
	rc |= dvar_fix_interval_l(dvx+2,(xminc),(xmaxc));
      else {
	rc |= dvar_prune_interval_l(dvx+2,(xmaxc+1),(xminc-1));
	rc |= dvar_fix_interval_l(dvx+2,0,(uc-1));
      }
    } else {
      if (!notinf || xi>=uc)
	rc |= dvar_fix_interval_l(dvx+2,(-uc+1),0);
      else if (xminc <= xmaxc)
	rc |= dvar_fix_interval_l(dvx+2,(xminc),(xmaxc));
      else {
	rc |= dvar_prune_interval_l(dvx+2,(xmaxc+1),(xminc-1));
	rc |= dvar_fix_interval_l(dvx+2,(-uc+1),0);
      }
    }

    /* prune X */
    if (notinf) {
      SP_integer xmini = dvar_min_l(dvx+0);
      SP_integer zmini = dvar_min_l(dvx+2);
      SP_integer zmaxi = dvar_max_l(dvx+2);
      if (xminc<zmini)
	rc |= dvar_fix_min_safe(wam, dvx+0,(xmini+zmini-xminc));
      else if (xminc>zmaxi)
	rc |= dvar_fix_min_safe(wam, dvx+0,(xmini+zmini-xminc+uc));
    }
    if (notsup) {
      SP_integer xmaxi = dvar_max_l(dvx+0);
      SP_integer zmini = dvar_min_l(dvx+2);
      SP_integer zmaxi = dvar_max_l(dvx+2);
      if (xmaxc>zmaxi)
	rc |= dvar_fix_max_safe(wam, dvx+0,(xmaxi+zmaxi-xmaxc));
      else if (xmaxc<zmini)
	rc |= dvar_fix_max_safe(wam, dvx+0,(xmaxi+zmaxi-xmaxc-uc));
    }
  }
  return rc>=0;
}


/*
  '$fd_quotient'(State0, State, Actions). -- truncated quotient
  State = state(X-XMut, Y-YMut, Z-ZMut, B-Bmut)
  Z #= X/Y AND (Y=0 implies B=0)
*/
void SPCDECL
prolog_fd_quotient(Wam wam,
		   SP_term_ref State,
		   SP_term_ref NewState,
		   SP_term_ref Actions)
{
  Dvar dvx, dvy, dvz;
  TAGGED newmin, newmax, axmin, axmax, aymin, aymax, azmin, azmax;
  int i, iter;
  TAGGED handle, t1;
  SP_BOOL committed;
  struct arith_data *pdata;
  int ent = -1;			/* disentailed unless otherwise */

  (void)State;                 /* [PM] 3.9b5 avoid -Wunused */
/*    X(0) = RefTerm(State0); */
  dvar_export_start(wam);
  RefTerm(NewState) = fd_static_output_state(wam,&handle,&committed);
  if (!IsVar(handle)) {		/* got [Flag | '$free'(Ptr)] */
    pdata = Pdata(struct arith_data,handle);
  } else {			/* build persistent state */
    pdata = Palloc(struct arith_data, 4*sizeof(struct dvar), handle);
    pdata->destructor = arith_destructor;
    pdata->nrefs = 6;
    FD_STORE_SPENV(pdata->spenv);
    pdata->refbase = SP_alloc_globrefs(6);
    pdata->dvar = (Dvar)(pdata+1);
    for (i=0; i<3; i++) {
      DerefArg(t1,X(0),i+1);
      fd_get_var_and_attr(t1,pdata->refbase+2*i);
      dvar_init(pdata->dvar+i, pdata->refbase+2*i,   pdata->refbase+2*i+1);
    }
  }

  /* RESUME HERE */
  dvx = pdata->dvar;
  dvy = dvx+1;
  dvz = dvx+2;
  if (!plan_divmodrem(wam, pdata))
    goto ret;
#include "quo-iteration.ic"
#if 0
  /* shaving */
  if (!shave_bounds(wam, dvx,0))
    goto ret;
#endif
  if (dvar_is_integer(dvx) && dvar_is_integer(dvy) && dvar_is_integer(dvz)) {
    ent = (dvar_min_l(dvx)/dvar_min_l(dvy))==dvar_min_l(dvz) ? 1 : -1;
  } else {
    ent = 0;
  }
  for (i=0; i<3; i++)
    dvar_pruning_done( dvx+i);
  for (i=0; i<3; i++)
    dvar_export(dvx+i);
 ret:
  if (ent==1)
    Pfree;
  dvar_export_done(wam, Actions, ent);
}

/*
  '$fd_divide'(State0, State, Actions). -- floored quotient
  State = state(X-XMut, Y-YMut, Z-ZMut, B-Bmut)
  Z #= X/Y AND (Y=0 implies B=0)
*/
void SPCDECL
prolog_fd_divide(Wam wam,
		 SP_term_ref State,
		 SP_term_ref NewState,
		 SP_term_ref Actions)
{
  Dvar dvx, dvy, dvz;
  TAGGED newmin, newmax, axmin, axmax, aymin, aymax, azmin, azmax;
  int i, iter;
  TAGGED handle, t1;
  SP_BOOL committed;
  struct arith_data *pdata;
  int ent = -1;			/* disentailed unless otherwise */

  (void)State;                 /* [PM] 3.9b5 avoid -Wunused */
/*    X(0) = RefTerm(State0); */
  dvar_export_start(wam);
  RefTerm(NewState) = fd_static_output_state(wam,&handle,&committed);
  if (!IsVar(handle)) {		/* got [Flag | '$free'(Ptr)] */
    pdata = Pdata(struct arith_data,handle);
  } else {			/* build persistent state */
    pdata = Palloc(struct arith_data, 4*sizeof(struct dvar), handle);
    pdata->destructor = arith_destructor;
    pdata->nrefs = 6;
    FD_STORE_SPENV(pdata->spenv);
    pdata->refbase = SP_alloc_globrefs(6);
    pdata->dvar = (Dvar)(pdata+1);
    for (i=0; i<3; i++) {
      DerefArg(t1,X(0),i+1);
      fd_get_var_and_attr(t1,pdata->refbase+2*i);
      dvar_init(pdata->dvar+i, pdata->refbase+2*i,   pdata->refbase+2*i+1);
    }
  }

  /* RESUME HERE */
  dvx = pdata->dvar;
  dvy = dvx+1;
  dvz = dvx+2;
  if (!plan_divmodrem(wam, pdata))
    goto ret;
#include "div-iteration.ic"
#if 0
  /* shaving */
  if (!shave_bounds(wam, dvx,0))
    goto ret;
#endif
  if (dvar_is_integer(dvx) && dvar_is_integer(dvy) && dvar_is_integer(dvz)) {
    SP_WORD x = dvar_min_l(dvx);
    SP_WORD y = dvar_min_l(dvy);
    SP_WORD z = dvar_min_l(dvz);
    SP_WORD q = ( x==0 ? 0
	       : (x<0)^(y>0) ? x/y 
	       : x>0 ? (x-y-1)/y
	       : (x-y+1)/y
	       );
    
    ent = (q==z ? 1 : -1);
  } else {
    ent = 0;
  }
  for (i=0; i<3; i++)
    dvar_pruning_done( dvx+i);
  for (i=0; i<3; i++)
    dvar_export(dvx+i);
 ret:
  if (ent==1)
    Pfree;
  dvar_export_done(wam, Actions, ent);
}


/*
  '$fd_modulo'(State0, State, Actions). -- floored remainder
  State = state(X-XMut, Y-YMut, Z-ZMut, B-Bmut)
  Z #= X mod Y AND (Y=0 implies B=0)
*/
void SPCDECL
prolog_fd_modulo(Wam wam,
		 SP_term_ref State,
		 SP_term_ref NewState,
		 SP_term_ref Actions)
{
  Dvar dvx, dvy, dvz;
  TAGGED newmin, newmax, axmin, axmax, aymin, aymax, azmin, azmax;
  int i, iter;
  TAGGED handle, t1;
  SP_BOOL committed;
  struct arith_data *pdata;
  int ent = -1;			/* disentailed unless otherwise */

  (void)State;                 /* [PM] 3.9b5 avoid -Wunused */
/*    X(0) = RefTerm(State0); */
  dvar_export_start(wam);
  RefTerm(NewState) = fd_static_output_state(wam,&handle,&committed);
  if (!IsVar(handle)) {		/* got [Flag | '$free'(Ptr)] */
    pdata = Pdata(struct arith_data,handle);
  } else {			/* build persistent state */
    pdata = Palloc(struct arith_data, 4*sizeof(struct dvar), handle);
    pdata->destructor = arith_destructor;
    pdata->nrefs = 6;
    FD_STORE_SPENV(pdata->spenv);
    pdata->refbase = SP_alloc_globrefs(6);
    pdata->dvar = (Dvar)(pdata+1);
    for (i=0; i<3; i++) {
      DerefArg(t1,X(0),i+1);
      fd_get_var_and_attr(t1,pdata->refbase+2*i);
      dvar_init(pdata->dvar+i, pdata->refbase+2*i,   pdata->refbase+2*i+1);
    }
  }

  /* RESUME HERE */
  dvx = pdata->dvar;
  dvy = dvx+1;
  dvz = dvx+2;
  if (!plan_divmodrem(wam, pdata))
    goto ret;
  if (dvar_is_integer(dvy)) {
    if (!mod_integer(wam, pdata))
      goto ret;
  } else {    
#include "mod-iteration.ic"
  }
#if 0
  /* shaving */
  if (!shave_bounds(wam, dvx,1))
    goto ret;
#endif
  if (dvar_is_integer(dvx) && dvar_is_integer(dvy) && dvar_is_integer(dvz)) {
    ent = fast_modi(dvar_min_l(dvx),dvar_min_l(dvy))==dvar_min_l(dvz) ? 1 : -1;
  } else {
    ent = 0;
  }
  for (i=0; i<3; i++)
    dvar_pruning_done( dvx+i);
  for (i=0; i<3; i++)
    dvar_export(dvx+i);
 ret:
  if (ent==1)
    Pfree;
  dvar_export_done(wam, Actions, ent);
}

/*
  '$fd_remainder'(State0, State, Actions). -- truncated remainder
  State = state(X-XMut, Y-YMut, Z-ZMut, B-Bmut)
  Z #= X rem Y AND (Y=0 implies B=0)
*/
void SPCDECL
prolog_fd_remainder(Wam wam,
		    SP_term_ref State,
		    SP_term_ref NewState,
		    SP_term_ref Actions)
{
  Dvar dvx, dvy, dvz;
  TAGGED newmin, newmax, axmin, axmax, aymin, aymax, azmin, azmax;
  int i, iter;
  TAGGED handle, t1;
  SP_BOOL committed;
  struct arith_data *pdata;
  int ent = -1;			/* disentailed unless otherwise */

  (void)State;                 /* [PM] 3.9b5 avoid -Wunused */
/*    X(0) = RefTerm(State0); */
  dvar_export_start(wam);
  RefTerm(NewState) = fd_static_output_state(wam,&handle,&committed);
  if (!IsVar(handle)) {		/* got [Flag | '$free'(Ptr)] */
    pdata = Pdata(struct arith_data,handle);
  } else {			/* build persistent state */
    pdata = Palloc(struct arith_data, 4*sizeof(struct dvar), handle);
    pdata->destructor = arith_destructor;
    pdata->nrefs = 6;
    FD_STORE_SPENV(pdata->spenv);
    pdata->refbase = SP_alloc_globrefs(6);
    pdata->dvar = (Dvar)(pdata+1);
    for (i=0; i<3; i++) {
      DerefArg(t1,X(0),i+1);
      fd_get_var_and_attr(t1,pdata->refbase+2*i);
      dvar_init(pdata->dvar+i, pdata->refbase+2*i,   pdata->refbase+2*i+1);
    }
  }

  /* RESUME HERE */
  dvx = pdata->dvar;
  dvy = dvx+1;
  dvz = dvx+2;
  if (!plan_divmodrem(wam, pdata))
    goto ret;
  if (dvar_is_integer(dvy)) {
    if (!rem_integer(wam, pdata))
      goto ret;
  } else {    
#include "rem-iteration.ic"
  }
#if 0
  /* shaving */
  if (!shave_bounds(wam, dvx,2))
    goto ret;
#endif
  if (dvar_is_integer(dvx) && dvar_is_integer(dvy) && dvar_is_integer(dvz)) {
    ent = (dvar_min_l(dvx)%dvar_min_l(dvy))==dvar_min_l(dvz) ? 1 : -1;
  } else {
    ent = 0;
  }
  for (i=0; i<3; i++)
    dvar_pruning_done( dvx+i);
  for (i=0; i<3; i++)
    dvar_export(dvx+i);
 ret:
  if (ent==1)
    Pfree;
  dvar_export_done(wam, Actions, ent);
}
