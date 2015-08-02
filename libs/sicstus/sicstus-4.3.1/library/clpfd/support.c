/* Copyright(C) 1999, Swedish Institute of Computer Science */

#include "fd.h"
#if __clang__
/* [PM] 4.2.1 Suppress clang warnings. I do not want to modify the code. */
#pragma clang diagnostic ignored "-Warray-bounds"
#endif  /* __clang__ */

/* Returns FD attribute for a dvar or integer.
   Heap consumption bounds:
   existing dvar - 0
   integer - INT_ATTRIBUTE_SIZE
   non-domain variable - FD_ATTRIBUTE_SIZE + ARITYLIMIT + 4
*/
TAGGED fd_check_argument(Wam wam,
			 TAGGED argument,
			 TAGGED min, TAGGED max, TAGGED size)
{
  TAGGED *h=w->global_top, t1, attr;
  int j;

  DerefSwitch(argument,;);
  switch (TagOf(argument)) {
  case REF_TAG:
    if (!VarIsSVA(argument,h) && GVarIsCVA(argument)) {
      if ((t1=get_attributes(argument,fd.fd_module)))
	return t1;
    }
    attr = MakeStructure(h);
    for (j=0; j<FD_ATTRIBUTE_SIZE; j++) {
      t1 = fd_attribute[j];
      if (!TagIsAtomic(t1))
	t1 += TagREF(w->global_top);
      *h++ = t1;
    }
    w->global_top[FD_ATTR_MIN_OFFSET] = min;
    w->global_top[FD_ATTR_MAX_OFFSET] = max;
    w->global_top[FD_ATTR_SIZE_OFFSET] = size;
    w->global_top = h;
    put_attributes(argument,attr,fd.fd_module);
    return attr;
  case CONST_TAG:
    if (TagIsATM(argument))
      return ERRORTAG;
    attr = MakeStructure(h);
    *h++ = fd_attribute[0];
    *h++ = fd_attribute[1];
    *h++ = fd_attribute[2];
    *h++ = attr + WD(5);
    *h++ = TaggedZero;
    /* 5*/*h++ = functor_Dmutable;
    *h++ = attr + WD(8);
    *h++ = TaggedZero;
    /* 8*/*h++ = fd.functor_dom4;
    *h++ = MakeList(w->global_top+13);
    /*10*/*h++ = argument;
    *h++ = argument;
    *h++ = TaggedOne;
    /*13*/*h++ = MakeList(w->global_top+10);
    *h++ = atom_nil;
    w->global_top = h;
    return attr;
  default:
    return ERRORTAG;
  }
}


/* '$fd_arg_attribute'(+Var, +Finitep, -Attr)
*/
void SPCDECL
prolog_fd_arg_attribute(Wam wam,
			SP_term_ref Var,
			SP_integer finitep,
			SP_term_ref Attr)
{
  TAGGED attr, domain;

  attr = fd_check_argument(wam,RefTerm(Var),Inf,Sup,Sup);
  if (attr && finitep) {
    DerefAttribute(domain,attr); /* dom/4 term */
    if (!AreSmall(DomainMin(domain),DomainMax(domain)))
      attr = ERRORTAG;
  }
  if (attr)
    RefTerm(Attr) = attr;
  else
    SP_fail();  
}

/* '$fd_dvar_list'(+List, +Finitep)
*/
void SPCDECL
prolog_fd_dvar_list(Wam wam,
		    SP_term_ref List,
		    SP_integer finitep)
{
  TAGGED domain;

  /* [MC] SPRM 8731 - don't over-estimate memory need */

  DEREF(X(0),RefTerm(List));
  while (TagIsLST(X(0))) {
    DerefCar(X(1),X(0));
    DerefCdr(X(0),X(0));
    if (!TagIsSmall(X(1))) {
      RequireHeap(FD_ATTRIBUTE_SIZE + ARITYLIMIT + 4,2);
      X(1) = fd_check_argument(wam,X(1),Inf,Sup,Sup);
      if (!X(1))
	goto fail;
      if (finitep) {
	DerefAttribute(domain,X(1)); /* dom/4 term */
	if (!AreSmall(DomainMin(domain),DomainMax(domain)))
	  goto fail;
      }
    }
  }
  if (X(0)!=atom_nil)		/* type error? */
    goto fail;
  return;
 fail:
  SP_fail();
}

/* '$fd_coref'(+List)
   Succeeds if List contains F1(X) and F2(X) for some X.
*/
void SPCDECL
prolog_fd_coref(Wam wam, SP_term_ref List)
{
  TAGGED list, var;

  DEREF(list,RefTerm(List));
  while (TagIsLST(list)) {
    DerefCar(var,list);
    DerefCdr(list,list);
    var = CTagToArg(var,1);
    DerefHeapSwitch(var,goto bind;);
    if (var==atom_nil)		/* F(Var), 2nd occurrence */
      return;
    else
      continue;			/* F(Integer) */
  bind:				/* F(Var), 1st occurrence */
    TrailPushCheck(var);
    CTagToPointer(var) = atom_nil;
  }
  SP_fail();			/* No coreference found. */
}

SP_BOOL fd_member(TAGGED x, TAGGED set)
{
  TAGGED range, min, max;

  while (set!=EmptySet) {
    range = CTagToCar(set); 
    set = CTagToCdr(set);
    min = RangeMin(range);
    max = RangeMax(range);
    if (!AreSmall(min,max)) {
      if (min==Inf)
	min = InfAsINT;
      if (max==Sup)
	max = SupAsINT;
    }
    if (Tle(x,max))
      return Tge(x,min);
  }
  return FALSE;
}

/* If x is in the 1st interval, *tail remains unassigned.
 * If x is in the nth interval, *tail = tail beginning with that interval.
 * Otherwise, *tail = first tail that might contain x+1.
 */
SP_BOOL fd_member_else(TAGGED x, TAGGED set, TAGGED *tail)
{
  TAGGED range, min, max;

  while (set!=EmptySet) {
    range = CTagToCar(set); 
    set = CTagToCdr(set);
    min = RangeMin(range);
    max = RangeMax(range);
    if (!AreSmall(min,max)) {
      if (min==Inf)
	min = InfAsINT;
      if (max==Sup)
	max = SupAsINT;
    }
    if (Tle(x,max))
      return Tge(x,min);
    *tail = set;
  }
  return FALSE;
}

SP_BOOL fd_intersects_else(TAGGED x, TAGGED y, TAGGED set, TAGGED *tail)
{
  TAGGED range, min, max;

  if (!AreSmall(x,y)) {
    if (x==Inf)
      x = InfAsINT;
    if (y==Sup)
      y = SupAsINT;
  }
  while (set!=EmptySet) {
    range = CTagToCar(set); 
    set = CTagToCdr(set);
    min = RangeMin(range);
    max = RangeMax(range);
    if (!AreSmall(min,max)) {
      if (min==Inf)
	min = InfAsINT;
      if (max==Sup)
	max = SupAsINT;
    }
    if (Tle(x,max))
      return Tge(y,min);
    *tail = set;
  }
  return FALSE;
}

SP_BOOL fd_check_overflow(Wam wam, TAGGED goal)
{
  if (!fd.fd_overflow) {
    return TRUE;
  } else if (!fd.overflowing) {
    SP_fail();
    return FALSE;
  } else {
    int res;
    SP_term_ref goalref = SP_new_term_ref();
    SP_term_ref argref = SP_new_term_ref();

    RefTerm(goalref) = goal;
    RefTerm(argref) = MakeSmall(fd.fd_overflow); /* [MC] SPRM 13682 */
    res = SP_query(fd.overflow_action1, goalref, argref);
    if (SP_exception_term(goalref)) {
      SP_ASSERT(res == SP_ERROR);(void)res;
      SP_raise_exception(goalref);
    }
    SP_reset_term_refs(goalref);
    SP_fail();
    return FALSE;
  }
}

/* Build a copy of old on the heap.
   Precondition: old is built entirely on the numstack.
*/
TAGGED fd_globalize(Wam wam,
		    TAGGED old, SP_integer req, int ar)
{
  TAGGED d1, r1, b, e, *h, value;
  TAGGED *valuep = &value;

  d1 = old;
  while (d1!=EmptySet) {
    req += 4;
    d1 = CTagToCdr(d1);
  }
  RequireHeap(req,ar);
  h = w->global_top;
  d1 = old;
  while (d1!=EmptySet) {
    r1 = CTagToCar(d1);
    d1 = CTagToCdr(d1);
    b = RangeMin(r1);
    e = RangeMax(r1);
    *valuep = MakeList(h);
    valuep = h+1;
    h[0] = MakeList(h+2);
    h[2] = b;
    h[3] = e;
    h += 4;
    if (b==Sup)
      fd.fd_overflow = 2;		/* FD integer overflow */
    else if (e==Inf)
      fd.fd_overflow = 1;		/* FD integer underflow */
  }
  *valuep = atom_nil;
  w->global_top = h;
  return value;
}

/* Build a copy of old on the heap.
   Precondition: old has NOT been fd_localized.
*/
TAGGED fd_globalize_unsafe(Wam wam,
			   TAGGED old, SP_integer pad, int ar)
{
  TAGGED d1, r1, b, e, *h, value;
  TAGGED *valuep = &value;

  while (TRUE) {
    SP_integer req = pad;
    d1 = old;
    while (d1!=EmptySet && !OnHeap((TAGGED *)d1)) {
      r1 = CTagToCar(d1);
      d1 = CTagToCdr(d1);
      req += (OnHeap((TAGGED *)r1) ? 2 : 4);
    }
    if (w->stack_start-w->global_top >= CONTPAD+req)
      break;
    old = fd_localize(wam,old);
    call_overflow(w,CONTPAD+req, ar);
  }
  h = w->global_top;
  d1 = old;
  while (d1!=EmptySet && !OnHeap((TAGGED *)d1)) {
    r1 = CTagToCar(d1);
    d1 = CTagToCdr(d1);
    *valuep = MakeList(h);
    valuep = h+1;
    h += 2;
    if (OnHeap((TAGGED *)r1)) {
      h[-2] = r1;
    } else {
      b = RangeMin(r1);
      e = RangeMax(r1);
      h[-2] = MakeList(h);
      h[0] = b;
      h[1] = e;
      h += 2;
      if (b==Sup)
	fd.fd_overflow = 2;		/* FD integer overflow */
      else if (e==Inf)
	fd.fd_overflow = 1;		/* FD integer underflow */
    }
  }
  *valuep = d1;
  w->global_top = h;
  return value;
}



void fd_update_mutable(Wam wam, TAGGED new_value, TAGGED mutable)
{
  TAGGED *h, *arg;
  
#if 0				/* OBSOLETE IDEA */
  TAGGED value = RefMutable(mutable);
  while (TagIsSTR(value) && TagToHeadfunctor(value)==functor_Dmutable) {
    mutable=value; value=RefMutable(value);
  }
#endif							
  arg = TagToArg(mutable,0);
  if (arg[2] < TrailToInt(w->node->trail_top) &&
      arg < w->global_uncond) {
    h = w->trail_top; /* trail mutable if need be */
    *h++ = mutable;
    *h++ = arg[1];
    *h++ = arg[2];
    arg[1] = new_value;	/* must come BEFORE sp_choice_overflow */
    arg[2] = TrailToInt(w->trail_top);
    w->trail_top = h;
    if ((TAGGED *)w->node-CHOICEPAD<w->trail_top)
      sp_choice_overflow(w,CHOICEPAD);
  } else
    arg[1] = new_value;
}

void fd_link(Wam wam,
	     TAGGED var,
	     TAGGED key,
	     TAGGED item)
{
  TAGGED mutable, *h, queues, *s=NULL, tail;
  SP_integer mask, count;
  DECL_UPDATE_MUTABLE;

  AttrToSuspM(fd_check_argument(wam,var,Inf,Sup,Sup),mutable);
  queues = RefMutable(mutable);
  h = w->global_top;
  
  if (TagToSTR(queues) < w->global_uncond)/* can't smash */ {
    int i;
	  
    for (s = TagToSTR(queues), i=0; i<8; i++)
      *h++ = *s++;
    queues = MakeStructure(h-8);
    FD_UPDATE_MUTABLE(queues,mutable);
  }
  if (key==fd.functor_dom) {
    mask = IStep(MASK_DOM);
    s = TagToArg(queues,3);
  } else if (key==fd.functor_min) {
    mask = IStep(MASK_MIN);
    s = TagToArg(queues,4);
  } else if (key==fd.functor_max) {
    mask = IStep(MASK_MAX);
    s = TagToArg(queues,5);
  } else if (key==fd.functor_minmax) {
     mask = IStep(MASK_MINMAX);
    s = TagToArg(queues,6);
  } else {
    /* patch item for globals suspending on val */
    if (TagToHeadfunctor(item)!=functor_minus) {
      HeapPush(h,functor_minus);
      HeapPush(h,atom_nil);
      HeapPush(h,item);
      item = MakeStructure(h-3);
    }
    mask = IStep(MASK_VAL);
    s = TagToArg(queues,7);
  }
  /* begin [MC] 4.0.5: skip prefix of entailed propagators */
  tail = *s;
  count = GetSmall(CTagToArg(queues,1)) + 1;
  while (tail!=EmptySet) {
    TAGGED qitem = CTagToCar(tail);
    TAGGED entvar;

    if (mask==IStep(MASK_VAL))
      qitem = CTagToArg(qitem,2);
    entvar = CTagToArg(qitem,4);
    DerefSwitch(entvar,goto nonent;);
    count--;
    tail = CTagToCdr(tail);
  }
 nonent:
  /* end   [MC] 4.0.5 */
  CTagToArg(queues,1) = MakeSmall(count);
  CTagToArg(queues,2) |= mask;
  HeapPush(h,item);
  HeapPush(h,tail);
  *s = MakeList(h-2);
  w->global_top = h;
}


/*** support for queues of indexicals and globals ***/

/* [PM] 4.1.0 break out magic number */
COMPILE_TIME_ASSERT_DECLARATION(CLPFD_MUTABLE_TERM_REF == 5);
#define CLPFD_MUTABLE RefTerm(CLPFD_MUTABLE_TERM_REF) /* xref Emulator/sicstus.c */

void fd_sync(Wam wam)
{
  TAGGED ptr = RefMutable(CLPFD_MUTABLE)
#if SICSTUS_MAJOR_VERSION < 4
    & ((TAGGED)-4) /* clear GC bits */
#endif
    ;
  struct propagator *current = (struct propagator *)TermToPointer(ptr);
  struct propagator *cur = fd.current_propagator;

  while (cur && cur!=current) {
    int i;
    for (i=0; i<FD_NB_QUEUES; i++) {
      cur->queue[i].first = 0;
      cur->queue[i].last = 0;
      cur->queue[i].slack = cur->queue[i].size;
    }
    fd.current_propagator = cur->next;
    cur->next = fd.free_propagators;
#if DBG > 1
    cur->chpt = ChoiceToInt(w->node);
#endif
    fd.free_propagators = cur;
    cur = fd.current_propagator;
  }
  /* [MC 3.11.1] see SPRM 7785.  Shouldn't happen, but does.  Exact cause
     not understood.  Hypothesis: a combination of freeze and
     backtracking causes contents of CLPFD_MUTABLE to be outside the
     fd.current_propagator chain.  Rescue op: pop fd.free_propagators
     stack until found. */

  while (cur!=current) {
    cur = fd.free_propagators;
    fd.free_propagators = cur->next;
    cur->next = fd.current_propagator;
    fd.current_propagator = cur;
  }
}


/* '$fd_begin'
*/
void SPCDECL
prolog_fd_begin(Wam wam)
{
  struct propagator *cur;
  int i;
  DECL_UPDATE_MUTABLE;
  
  if (!fd.batching) {
    fd_sync(wam);
    if (fd.free_propagators) {
      cur = fd.free_propagators;
      fd.free_propagators = cur->next;
    } else {
      cur = (struct propagator *)sp_checkalloc(sizeof(struct propagator), TRUE);
      for (i=0; i<FD_NB_QUEUES; i++) {
	cur->queue[i].first = 0;
	cur->queue[i].last = 0;
	cur->queue[i].size = 4;
	cur->queue[i].slack = 4;
	cur->queue[i].items = (SP_globref)sp_checkalloc(4*sizeof(*cur->queue[i].items), TRUE); /* 4.0.5 [MC] */
	memset((char *)cur->queue[i].items, 0, 4*sizeof(*cur->queue[i].items)); /* 4.0.5 [MC] */
      }
      cur->hint = i;
    }
    cur->next = fd.current_propagator;
    fd.current_propagator = cur;
    FD_UPDATE_MUTABLE(PointerToTerm(cur),CLPFD_MUTABLE);
  }
}


/* '$fd_end'
*/
void fd_end(Wam wam)
{
  struct propagator *cur;
  DECL_UPDATE_MUTABLE;

  if (!fd.batching) {
    fd_sync(wam);
    cur = fd.current_propagator;
    fd.current_propagator = cur->next;
    cur->next = fd.free_propagators;
    fd.free_propagators = cur;
    FD_UPDATE_MUTABLE(PointerToTerm(fd.current_propagator),CLPFD_MUTABLE);
  }
}


void
fd_dealloc(Wam wam)
{
  struct propagator *cur;
  
  RefMutable(CLPFD_MUTABLE) = TaggedZero;
  fd_sync(wam);
  while ((cur=fd.free_propagators)) {
    int i;
    for (i=0; i<FD_NB_QUEUES; i++)
      sp_checkdealloc((TAGGED *)cur->queue[i].items, cur->queue[i].size*sizeof(*cur->queue[i].items), TRUE); /* 4.0.5 [MC] */
    fd.free_propagators = cur->next;
    sp_checkdealloc((TAGGED *)cur, sizeof(struct propagator), TRUE);
  }
}  


/* support function for stack shifting and gc */

void SPCDECL
fd_manager_hook(Wam wam, struct worker *ignore,int msg,TAGGED *ptr)
{
  SP_integer reloc;
  struct propagator *cur;
  TAGGED t, *p;
  int i, j;

  (void)ignore;
  switch (msg) {
  case 1:			/* stack shifter */
    reloc = (SP_integer)ptr;
    fd_sync(wam);
    cur = fd.current_propagator;
    while (cur) {
      for (j=cur->hint; j<FD_NB_QUEUES; j++) {
	int last = cur->queue[j].last;
	for (i=cur->queue[j].first; i!=last; ) {
	  cur->queue[j].items[i].term[0] += reloc; /* 4.0.5 [MC] */
	  i++;
	  if (i==cur->queue[j].size)
	    i = 0;
	}
      }
      cur = cur->next;
    }
    break;
  case 2:			/* gc, mark phase */
    fd_sync(wam);
    cur = fd.current_propagator;
    while (cur) {
      for (j=0; j<FD_NB_QUEUES; j++) {
	int last = cur->queue[j].last;
	for (i=cur->queue[j].first; i!=last; ) {
	  markTerm(cur->queue[j].items[i].term[0]); /* 4.0.5 [MC] */
	  i++;
	  if (i==cur->queue[j].size)
	    i = 0;
	}
      }
      cur = cur->next;
    }
    break;
  case 3:			/* gc, sweep phase */
    cur = fd.current_propagator;
    while (cur) {
      for (j=0; j<FD_NB_QUEUES; j++) {
	int last = cur->queue[j].last;
	for (i=cur->queue[j].first; i!=last; ) {
	  t = cur->queue[j].items[i].term[0]; /* 4.0.5 [MC] */
	  p = TagToPointer(t);
	  if (OffHeaptop(p,ptr))
	    intoRelocationChain(w,p,&cur->queue[j].items[i].term[0]); /* 4.0.5 [MC] */
	  i++;
	  if (i==cur->queue[j].size)
	    i = 0;
	}
      }
      cur = cur->next;
    }
    break;
  case 4:			/* gc, proofreading */
    fd_sync(wam);
    cur = fd.current_propagator;
    while (cur) {
      for (j=0; j<FD_NB_QUEUES; j++) {
	int last = cur->queue[j].last;
	for (i=cur->queue[j].first; i!=last; ) {
	  (*(void(*)(TAGGED *))ptr)(&cur->queue[j].items[i].term[0]); /* 4.0.5 [MC] */
	  i++;
	  if (i==cur->queue[j].size)
	    i = 0;
	}
      }
      cur = cur->next;
    }
    break;
  }
}



struct daemon_frame {
  void (SPCDECL *destructor)(void *);
  void (SPCDECL *daemon)(Wam,void *,SP_globref,TAGGED); /* (wam,handle,attr_ref,global) */
};

/* where=0x0/1 -- prepend/append to indexical queue
         0x2/3 -- prepend/append to 'val' global queue
         0x4/5 -- prepend/append to 'minmax' global queue
         0x6/7 -- prepend/append to 'dom' global queue
         0x8/9 -- prepend/append to daemon queue
         0xa/b -- prepend/append to wake queue
*/
void fd_enqueue_general(Wam wam, TAGGED item, int where)
{
  int ix = where>>1;
  struct propagator *cur = fd.current_propagator;
  struct prop_queue *q = &cur->queue[ix];
  int i, size;
  
  if (ix<FD_QUEUE_DAEMON) {
    TAGGED mutable = CTagToArg(item,3);
    DECL_UPDATE_MUTABLE;
    
    FD_UPDATE_MUTABLE(RefMutable(mutable)|IStep(1),mutable); /* STATUS: enqueued */
  }
  
#if DBG > 1
  if (cur->chpt != ChoiceToInt(w->node)) {	/* [PRM 8883] */
    printf("!!! propagator's chpt = %p, WAM's chpt = %p, item = %p\n", ChoiceFromInt(cur->chpt), w->node, (SP_integer *)item);
  }
#endif
  /* room for item? */
  size = q->size;
  /* begin [MC] 4.0.5 extra word for GC bits in each item */
  if (q->slack==1) { /* grow */
    q->items = (SP_globref)sp_checkrealloc((TAGGED *)q->items,
					   size*sizeof(*q->items),
					   size*sizeof(*q->items)<<1, TRUE);
    memset((char *)(q->items+size), 0, size*sizeof(*q->items));
    if (q->first>q->last) {
      for (i=size-1; i>=q->first; i--)
	q->items[i+size].term[0] = q->items[i].term[0];
      q->first += size;
    }
    q->size += size;
    q->slack += size;
    size += size;
  }
  q->slack--;
  if (where & 0x1) {
    int pos = q->last;
    
    q->items[pos++].term[0] = item;
    if (pos==size)
      pos = 0;
    q->last = pos;
  } else {
    int pos = q->first;
    
    if (pos==0)
      pos = size;
    q->items[--pos].term[0] = item;
    q->first = pos;
  }
  /* end [MC] 4.0.5 */
  if (cur->hint > ix)
    cur->hint = ix;
}

/* enqueue if not already in the queue */
/* where is as above */
void fd_enqueue_global(Wam wam, TAGGED item, int where)
{
  TAGGED t1 = CTagToArg(item,3);
  TAGGED status = RefMutable(t1);
  struct propagator *cur = fd.current_propagator;
  DECL_UPDATE_MUTABLE;
  
  if (!(status & IStep(1))) {	/* STATUS: not enqueued */
    int ix = where>>1;
    if (where & 0x1) {
      fd_enqueue_append(wam,item,ix); /* DOM or MINMAX */
    } else {
      fd_enqueue_prepend(wam,item,ix); /* DOM or MINMAX */
    }
  }
}

/* 0 -- empty queue
   1 -- dequeued indexical
   2 -- dequeued global
   3 -- pending wakeup goal
*/
int fd_dequeue(Wam wam, TAGGED *item)
{
  struct propagator *cur = fd.current_propagator;
  SP_globref attr_ref;
  struct daemon_frame *handle;
  struct prop_queue *dq = &cur->queue[FD_QUEUE_DAEMON];
  int i;

  while (dq->first != dq->last) {
    TAGGED daemon = dq->items[dq->first++].term[0]; /* 4.0.5 [MC] */
    TAGGED statem = CTagToArg(daemon,4);
    if (dq->first==dq->size)
      dq->first = 0;
    DerefSwitch(statem,goto run_daemon;); /* skip if entailed, can happen if non-idempotent */
    continue;
  run_daemon:
    attr_ref = (SP_globref)TermToPointer(CTagToArg(daemon,2));
    handle = (struct daemon_frame *)TermToPointer(CTagToArg(daemon,5));
    (*handle->daemon)(wam, handle, attr_ref, CTagToArg(daemon,1));
  }
  dq->slack = dq->size;
  if (OffHeaptop(w->global_top,w->heap_warn_soft)) {
    SP_ASSERT(w->heap_warn_soft || w->wake_count==0);
    return 3;
  }
  for (i=cur->hint; i<FD_QUEUE_DAEMON; i++) {
    struct prop_queue *q = &cur->queue[i];
    if (q->first != q->last) {
      int pos = q->first;
      
      *item = q->items[pos++].term[0]; /* 4.0.5 [MC] */
      if (pos==q->size)
	pos = 0;
      q->first = pos;
      if (pos==q->last)
	cur->hint = i+1; 
      q->slack++;
      return (i==0 ? 1 : 2);
    }
  }
  cur->hint = i;
  /* [MC] 3.12.9: pending unifications */
  SP_ASSERT(w->heap_warn_soft || w->wake_count==0);
  dq = &cur->queue[FD_QUEUE_WAKE];
  while (dq->first != dq->last) {
    TAGGED pair = dq->items[dq->first++].term[0];
    TAGGED var, value;
    dq->slack++;
    if (dq->first==dq->size)
      dq->first = 0;
    DerefArg(var,pair,1);
    DerefArg(value,pair,2);
    if (IsVar(var)) {
      var = delete_attributes(var,fd.fd_module,EVAL_ARITY); /* GC + deref */
      Wake; BindCVA(var,value);				    /* [MC] SPRM 12274 */
    } else if (var!=value) {
      return -1;
    }
  }
  return 0;
}

#define PROTECTED_LISTS_MUTABLE X(3)
#define PROTECTED_LIST          X(4)
#define PROTECTED_TAIL          X(5)
#define NB_PROTECTED               3

static void gc_suspensions(Wam wam, int index, int total, int ndead)
{
  TAGGED *lists, list, *h, item, entvar, *target;

  RequireHeap(8 + 2*(total-ndead), EVAL_ARITY+NB_PROTECTED);
  h = w->global_top;
  lists = TagToArg(RefMutable(PROTECTED_LISTS_MUTABLE),0);
  if (lists < w->global_uncond) { /* can't smash */
    int i;
    DECL_UPDATE_MUTABLE;
    
    for (i=0; i<8; i++)
      h[i] = lists[i];
    lists = h;
    h += 8;
    FD_UPDATE_MUTABLE(MakeStructure(lists),PROTECTED_LISTS_MUTABLE);
  }
  target = &lists[index];
  list = *target;
  if (total > ndead) {
    while (list!=PROTECTED_TAIL) {
      item = CTagToCar(list);
      entvar = CTagToArg(item,4);
      DerefSwitch(entvar,goto live;);
      list = CTagToCdr(list);
      continue;
    live:
      if (TagToLST(list) < w->global_uncond) {	/* can't smash */
	h[0] = CTagToCar(list);
	h[1] = CTagToCdr(list);
	list = MakeList(h);
	h += 2;
      }
      *target = list;
      target = TagToCdr(list);
      list = *target;
    }
  }
  *target = PROTECTED_TAIL;
  lists[1] -= IStep(ndead);
  if (lists[index]==atom_nil) {
    index = GetSmall(lists[2]) & MASK_VAL;
    if (lists[3]!=EmptySet)
      index |= MASK_DOM;
    if (lists[4]!=EmptySet)
      index |= MASK_MIN;
    if (lists[5]!=EmptySet)
      index |= MASK_MAX;
    if (lists[6]!=EmptySet)
      index |= MASK_MINMAX;
    lists[2] = MakeSmall(index);
  }
  w->global_top = h;
}

#if 0
/* This would require that:
   1. daemons never reuse propagator's dvars
   2. propagators face state changes as side effect of dvar_export()
   3. PROTECTED_* become SP_term_refs
 */
static void fd_enqueue_daemon(Wam wam, TAGGED daemon)
{
  SP_globref attr_ref = (SP_globref)TermToPointer(CTagToArg(daemon,2));
  TAGGED statem = CTagToArg(daemon,4);
  struct daemon_frame *handle = (struct daemon_frame *)TermToPointer(CTagToArg(daemon,5));
  
  DerefSwitch(statem,goto run_daemon;); /* skip if entailed, can happen if non-idempotent */
  return;
 run_daemon:
  (*handle->daemon)(wam, handle, attr_ref, CTagToArg(daemon,1));
}
#endif

static void fd_enqueue_list_gc(Wam wam, int index)
{
  struct propagator *cur = fd.current_propagator;
  int ndead=0, total=0;
  int bin = (index==3 ? 0x3 : 0x2);
  DECL_UPDATE_MUTABLE;

  PROTECTED_LIST = CTagToArg(RefMutable(PROTECTED_LISTS_MUTABLE),index);
  PROTECTED_TAIL = PROTECTED_LIST;
  while (PROTECTED_LIST!=EmptySet) {
    TAGGED item = CTagToCar(PROTECTED_LIST);
    TAGGED functor = TagToHeadfunctor(item);
    TAGGED status = RefMutable(CTagToArg(item,3));

    total++;    
    PROTECTED_LIST = CTagToCdr(PROTECTED_LIST);
    if (status & IStep(16)) {	/* STATUS: entailed */
      ndead++;
      PROTECTED_TAIL = PROTECTED_LIST;
    } else if (functor==functor_daemon5) {
      if ((status & IStep(12)) != IStep(8)) /* STATUS: not current or not idempotent */
	fd_enqueue_daemon(wam, item);
    } else {
      if (!(status & IStep(1))) { /* STATUS: not enqueued */
	if (functor==functor_ix7) {
	  TAGGED entvar = CTagToArg(item,4);
	  DerefSwitch(entvar,goto live;);
	  ndead++;
	  PROTECTED_TAIL = PROTECTED_LIST;
	  continue;
	live:
	  fd_enqueue_append_ix(wam, item);
	} else {
	  fd_enqueue_append(wam, item, bin); /* DOM or MINMAX */
	}
      }
    }
  }
  if (ndead>0)			/* there exist entailed entries */
    gc_suspensions(wam, index, total, ndead);
}

static void fd_enqueue_list(Wam wam, int index)
{
  struct propagator *cur = fd.current_propagator;
  int bin = (index==3 ? 0x3 : 0x2);
  DECL_UPDATE_MUTABLE;

  PROTECTED_LIST = CTagToArg(RefMutable(PROTECTED_LISTS_MUTABLE),index);
  while (PROTECTED_LIST!=EmptySet) {
    TAGGED item = CTagToCar(PROTECTED_LIST);
    TAGGED functor = TagToHeadfunctor(item);
    TAGGED status = RefMutable(CTagToArg(item,3));
    
    PROTECTED_LIST = CTagToCdr(PROTECTED_LIST);
    if (status & IStep(16)) {	/* STATUS: entailed */
    } else if (functor==functor_daemon5) {
      if ((status & IStep(12)) != IStep(8)) /* STATUS: not current or not idempotent */
	fd_enqueue_daemon(wam, item);
    } else {
      if (!(status & IStep(1))) { /* STATUS: not enqueued */
	if (functor==functor_ix7) {
	  TAGGED entvar = CTagToArg(item,4);
	  DerefSwitch(entvar,goto live;);
	  continue;
	live:
	  fd_enqueue_prepend_ix(wam, item);
	} else {
	  fd_enqueue_prepend(wam, item, bin); /* DOM or MINMAX */
	}
      }
    }
  }
}

static void relink_item(Wam wam, TAGGED pair)
{
  TAGGED vars, item, var, *h;
  
  RequireHeap1(13, pair, EVAL_ARITY+NB_PROTECTED);
  vars = CTagToArg(pair,1);
  item = CTagToArg(pair,2);
  /* unwrap any iff(Ix,B,Key,A) item */
  if (TagToHeadfunctor(item)==functor_iff4) {
    TAGGED bvar, key;

    bvar = CTagToArg(item,2);
    key = CTagToArg(item,3);
    DerefSwitch(bvar,{
	bvar = get_attributes(bvar,fd.fd_module);
	DerefAttribute(bvar,bvar);
	bvar = CTagToArg(bvar,2);
      });
    if (bvar!=key)
      return;
    DerefArg(item,item,1);
  }
  do {
    DerefCar(var,vars);
    DerefCdr(vars,vars);
  } while (!IsVar(var));
  h = w->global_top;
  HeapPush(h,functor_minus);
  HeapPush(h,vars);
  HeapPush(h,item);
  w->global_top = h;
  fd_link(wam, var, fd.functor_val, MakeStructure(h-3));
}

static void fd_enqueue_val(Wam wam, int index)
{
  struct propagator *cur = fd.current_propagator;
  int iff_count=0;
  DECL_UPDATE_MUTABLE;

  PROTECTED_LIST = CTagToArg(RefMutable(PROTECTED_LISTS_MUTABLE),index);
  PROTECTED_TAIL = PROTECTED_LIST;
  while (PROTECTED_LIST!=EmptySet) {
    TAGGED pair = CTagToCar(PROTECTED_LIST);
    TAGGED vars = CTagToArg(pair,1);
    TAGGED item = CTagToArg(pair,2); 
    TAGGED functor = TagToHeadfunctor(item);
    TAGGED entvar = CTagToArg(item,4);
    TAGGED var = atom_nil;

    PROTECTED_LIST = CTagToCdr(PROTECTED_LIST);
    DerefSwitch(entvar,goto live;);
    continue;
  live:
    while (vars!=EmptySet && !IsVar(var)) {
      DerefCar(var,vars);
      DerefCdr(vars,vars);
    }
    if (IsVar(var)) {
      iff_count += (functor==functor_iff4);
      relink_item(wam, pair);
    } else if (functor==functor_daemon5) {
      TAGGED status = RefMutable(CTagToArg(item,3));
      
      if ((status & IStep(12)) != IStep(8)) /* STATUS: not current or not idempotent */
	fd_enqueue_daemon(wam, item);
    } else if (functor==functor_ix7) {
      fd_enqueue_prepend_ix(wam, item);
    } else if (functor==functor_iff4) {
      TAGGED bvar = CTagToArg(item,2);
      TAGGED key = CTagToArg(item,3);
      
      iff_count++;
      DerefSwitch(bvar,{
	bvar = get_attributes(bvar,fd.fd_module);
	DerefAttribute(bvar,bvar);
	bvar = CTagToArg(bvar,2);
      });
      if (bvar==key) {
	DerefArg(item,item,1);
	fd_enqueue_prepend_ix(wam, item);
      }
    } else {
      TAGGED status = RefMutable(CTagToArg(item,3));
      
      if (!(status & IStep(1))) { /* STATUS: not enqueued */
	fd_enqueue_prepend(wam, item, 0x1);
      }
    }
    if (iff_count==0)
      PROTECTED_TAIL = PROTECTED_LIST;
  }
  /* now disable all iff(Ix,B,Key,A) items we encountered */
  while (PROTECTED_TAIL!=EmptySet) {
    TAGGED pair = CTagToCar(PROTECTED_TAIL);
    TAGGED item = CTagToArg(pair,2); 
    TAGGED entvar;

    DerefArg(entvar,item,4);
    PROTECTED_TAIL = CTagToCdr(PROTECTED_TAIL);
    if (IsVar(entvar) && TagToHeadfunctor(item)==functor_iff4) {	/* iff(Ix,B,Key,A) */
      TAGGED key = CTagToArg(item,3);

      BindHVA(entvar,key);
    }
  }
}

/* assuming bits > 0 */
/* X(1), X(2) must survive over this */
void fd_enqueue_all(Wam wam, int bits, TAGGED lists_loc)
{
  PROTECTED_LISTS_MUTABLE = lists_loc;
  if (bits & MASK_SINGLETON) {
    /* 3.9: enqueue all constraints when ground,
       (a) to maximize entailment detection,
       (b) to handle co-references */
    bits |= GetSmall_int(CTagToArg(RefMutable(PROTECTED_LISTS_MUTABLE),2)); /* bitmask of susp. lists */
    /* we are about to prepend---prepend slowest first! */
    if (bits & MASK_DOM)
      fd_enqueue_list(wam, 3);
    if (bits & MASK_MIN)
      fd_enqueue_list(wam, 4);
    if (bits & MASK_MAX)
      fd_enqueue_list(wam, 5);
    if (bits & MASK_MINMAX)
      fd_enqueue_list(wam, 6);
    if (bits & MASK_VAL)
      fd_enqueue_val(wam, 7);
  } else {
    if (bits & MASK_MINMAX)
      fd_enqueue_list_gc(wam, 6);
    if (bits & MASK_MAX)
      fd_enqueue_list_gc(wam, 5);
    if (bits & MASK_MIN)
      fd_enqueue_list_gc(wam, 4);
    if (bits & MASK_DOM)
      fd_enqueue_list_gc(wam, 3);
  }
}

/* implies $fd_begin */
void SPCDECL
prolog_fd_global_enqueue(Wam wam, SP_term_ref TermR)
{
  TAGGED term=RefTerm(TermR);
  TAGGED mutable;
  DECL_UPDATE_MUTABLE;

  SP_MANGLE(prolog_fd_begin)(wam);
  DerefNonvar(term);
  mutable = CTagToArg(term,3);
  FD_UPDATE_MUTABLE(RefMutable(mutable)|IStep(9),mutable); /* STATUS: enqueued, current */
  /*** done in Prolog
  fd_sync(wam);
  fd_enqueue(w, term, 0x3);
  ***/
}


/* implies $fd_begin */
void SPCDECL
prolog_fd_enqueue_all(Wam wam, SP_term_ref ListsM)
{
  TAGGED t1;
  int bits;

/*    X(0) = RefTerm(ListsM); */
  (void)ListsM;
  SP_MANGLE(prolog_fd_begin)(wam);
  DerefNonvar(X(0));
  t1 = RefMutable(X(0));	/* get suspension lists */
  bits = GetSmall_int(CTagToArg(t1,2)); /* get filter */
  if (bits > 0) {
    fd_sync(wam);
    fd_enqueue_all(wam,bits+MASK_SINGLETON,X(0));
  }
}

/* Support for managing incumbents:
   '$fd_update_incumbent'(+Ref, +Value, +Vertex).
	store a new incumbent vertex and value
   '$fd_incumbent_bound'(+Ref, -Value).
	retrieve the current incumbent value.
*/

/* extract pointer from db_reference */
static struct instance *get_dbref(TAGGED ref)
{
  DerefNonvar(ref);
  return (struct instance *)TermToPointer(CTagToArg(ref,1));
}


void SPCDECL
prolog_fd_update_incumbent(Wam wam,
				  SP_term_ref RefR,
				  SP_term_ref ValueR,
				  SP_term_ref VertexR)
{
  struct instance *ins = get_dbref(RefTerm(RefR));
  int i;
  TAGGED vertex;
  /* 3.9 assert scheme 
     => value is at offset no_cells-4,
        vertex is at offset no_cells-7, -9, ...
     3.10 assert scheme 
     => value is at offset 1,
        vertex is at offset 3, 5, ...
  */

#if SICSTUS_MAJOR_VERSION < 4
  int no_cells = ((ins->objsize-sizeof(struct instance))>>LogSizeOfWord) + 1;
  DEREF(ins->code[no_cells-4],RefTerm(ValueR));
  DEREF(vertex,RefTerm(VertexR));
  for (i=no_cells-7; TagIsLST(vertex); i-=2) {
    DerefCar(ins->code[i],vertex);
    DerefCdr(vertex,vertex);
  }
#else
  DEREF(ins->code[1],RefTerm(ValueR));
  DEREF(vertex,RefTerm(VertexR));
  for (i=3; TagIsLST(vertex); i+=2) {
    DerefCar(ins->code[i],vertex);
    DerefCdr(vertex,vertex);
  }
#endif
}


void SPCDECL
prolog_fd_incumbent_bound(Wam wam,
				 SP_term_ref RefR,
				 SP_term_ref ValueR)
{
  struct instance *ins = get_dbref(RefTerm(RefR));
  /* 3.9 assert scheme 
     => value is at offset no_cells-4,
        vertex is at offset no_cells-7, -9, ...
     3.10 assert scheme 
     => value is at offset 1,
        vertex is at offset 3, 5, ...
  */
#if SICSTUS_MAJOR_VERSION < 4
  int no_cells = ((ins->objsize-sizeof(struct instance))>>LogSizeOfWord) + 1;
  RefTerm(ValueR) = ins->code[no_cells-4];
#else
  RefTerm(ValueR) = ins->code[1];
#endif
}


/* Support for Palloc/Pfree. */

void *fd_perm_alloc(Wam wam,
		    size_t nbytes,
		    TAGGED handle) /* HVA to bind */
{
  void *ptr;
  TAGGED tptr;
  TAGGED *h;
  TAGGED inner, outer;

  ptr = fd_malloc(wam, nbytes);
  tptr = PointerToTerm(ptr);
  RequireHeap1(4,handle,EVAL_ARITY);
  h = w->global_top;
  inner = MakeStructure(h);
  *h++ = functor_Dfree;
  *h++ = tptr;
  outer = MakeList(h);
  Load0HVA(h);
  *h++ = inner;
  w->global_top = h;
  TrailPushCheck(outer);
  BindHVA(handle,outer);
  return ptr;
}


void *fd_perm_data(TAGGED handle) /* [Flag | '$free'(Ptr)] */
{
  handle = CTagToCdr(handle);
  handle = CTagToArg(handle,1);
  return TermToPointer(handle);
}


/* A mutable item on the trail is redundant if:
   1. the mutable is in H+, or
   2. there is a previous item in TR+, or
   3. the new and old values are the same.
   */
#define RedundantMutableItem(Mutp,Item) \
((Mutp) >= w->global_uncond || (Item)[2] >= TrailToInt(w->node->trail_top) || (Mutp)[1]==(Item)[1])

#define PrevMutItem(Item) \
(w->trail_start + GetSmall((Item)[2]))


void fd_perm_free(Wam wam)
{
  struct node *nd;
  void (SPCDECL *destructor)(void *);
  ANYPOINTER frame;
  TAGGED handle;		/* [Flag | '$free'(Ptr)] */
  TAGGED flag;
  SP_BOOL committed;
  
  fd_static_output_state(wam,&handle,&committed);
  flag = CTagToCar(handle);
  if (committed) {
    BindHVA(flag,TaggedZero);	/* disable cleanup */
    frame = TermToPointer(CTagToArg(CTagToCdr(handle),1));
    destructor = *(void (SPCDECL **)(void*))frame;
    (*destructor)(frame);
    if (w->trail_top[-1]==handle)
      w->trail_top--;
  } else {
    BindHVA(flag,TaggedOne);	/* enable cleanup */
    for (nd = w->node;
	 (TAGGED *)nd<w->choice_start && !ChoiceptTestCleanup(nd);
	 nd = ChoiceptPrevious(nd))
      ChoiceptMarkCleanup(nd);
  }
}

/* check absence of [Flag | '$free'(Ptr)] with unbound Flag,
   which would be a memory leak,
   typically after the end of a query */
void SPCDECL
prolog_fd_assert_no_leak(Wam wam)
{
#if DBG
  {
  TAGGED *tr;
  for (tr=w->trail_top; tr>w->trail_start; ) {
    TAGGED item = *--tr;
    if (TagIsSmall(item)) {
      tr -= 2;
    } else if (TagIsLST(item)) {
      TAGGED car, cdr;
      DerefCar(car,item);
      DerefCdr(cdr,item);
      SP_ASSERT(!IsVar(car) || TagToHeadfunctor(cdr)!=functor_Dfree);
    }
  }
  }
#endif
  (void)wam;
}

TAGGED
fd_daemon_copy_state(Wam wam, TAGGED *global, SP_BOOL *buried)
{
  TAGGED *s, *h, tstate;
  int ar, i;
  
  tstate = RefMutable(CTagToArg(*global,1));
  ar = Arity(TagToHeadfunctor(tstate));
  s = TagToArg(tstate,0);
  if (s >= w->global_uncond) {
    *buried = FALSE;
    s[ar] += IStep(1);		/* increment stamp */
  } else {
    DECL_UPDATE_MUTABLE;
    *buried = TRUE;
    RequireHeap2(ar+1,*global,tstate,EVAL_ARITY);
    s = TagToArg(tstate,0);
    h = w->global_top;
    for (i=ar; i>=0; i--)
      h[i] = s[i];
    w->global_top = h+ar+1;
    h[ar] += IStep(1);		/* increment stamp */
    tstate = MakeStructure(h);
    FD_UPDATE_MUTABLE(tstate,CTagToArg(*global,1));
  }
  return tstate;
}

/* Most propagators have arguments (+State0, -State), ... where
   State0 = F(......,Handle,Stamp), is left dereferenced,
   State  = copy of State0 with Stamp incremented.
   Also, check if this execution step can be backtracked over or not.
*/
TAGGED fd_unify_output_state(Wam wam,
			     TAGGED *phandle,
			     SP_integer *pstamp,
			     SP_BOOL *pcommitted)
{
  TAGGED handle, t1, *s, *h;
  int ar, i;
  
  DerefNonvar(X(0));
  ar = Arity(TagToHeadfunctor(X(0)));
  DerefArg(handle,X(0),ar-1);
  DerefArg(t1,X(0),ar);
  *pstamp = GetSmall(t1);
  *pcommitted = (IsVar(handle) ? TRUE : TagToLST(handle) >= w->global_uncond);
  s = TagToArg(X(0),0);
  if (s >= w->global_uncond) {
    s[ar] += IStep(1);		/* increment stamp */
  } else {
    RequireHeap1(ar+1,handle,EVAL_ARITY);
    s = TagToArg(X(0),0);
    h = w->global_top;
    for (i=0; i<ar+1; i++)
      h[i] = s[i];
    w->global_top = h+ar+1;
    h[ar] += IStep(1);		/* increment stamp */
    X(0) = MakeStructure(h);
  }
  *phandle = handle;
  return X(0);
}

/* Some propagators have arguments (+State0, -State), ... where
   State0 = F(......,Handle), is left dereferenced,
   State  = State0,
   Also, check if this execution step can be backtracked over or not.
*/
TAGGED fd_static_output_state(Wam wam,
			      TAGGED *phandle,
			      SP_BOOL *pcommitted)
{
  TAGGED handle;
  int ar;
  
  DerefNonvar(X(0));
  ar = Arity(TagToHeadfunctor(X(0)));
  DerefArg(handle,X(0),ar-1);
  *phandle = handle;
  *pcommitted = (IsVar(handle) ? TRUE : TagToLST(handle) >= w->global_uncond);
  return X(0);
}

/* length(+List,-Length) */
int 
fd_list_length(TAGGED tvec)
{
  int nvars=0;
  
  while (TagIsLST(tvec)) {
    DerefCdr(tvec,tvec);
    nvars++;
  }
  return nvars;
}

/* store var & attr in global term refs */
void
fd_get_var_and_attr(TAGGED term, SP_globref ref)
{
  TAGGED t1;
  
  DerefArg(t1,term,1);	/* get domain var */
  RefGlob(ref+1) = t1;
  DerefArg(t1,term,2);	/* get attribute */
  RefGlob(ref) = t1;
}

/* access singleton value also in the context of extra attributes */
/* term is known to be dvar or integer */
TAGGED
fd_deref(Wam wam, TAGGED term)
{
  TAGGED attr, domain;
  DerefSwitch(term, {
      attr = get_attributes(term,fd.fd_module);
      DerefAttribute(domain,attr);
      if (DomainMin(domain)==DomainMax(domain))
	term = DomainMin(domain);
  });
  return term;
}


/* for qsorting by ascending SP_integer */
static int cmp_asc_long(Wam wam, SP_integer *l1, SP_integer *l2)
{
  SP_integer val1 = *l1;
  SP_integer val2 = *l2;

  (void)wam;
  return CMP(val1,val2);
}

#define QType SP_integer
#define QCmp  cmp_asc_long
#define QSort fd_qsort_asc_long1
#include "qsort.ic"

void
fd_qsort_asc_long(Wam wam,
		      SP_integer *l1, int n)
{
  fd_qsort_asc_long1(wam, l1,n);
}

void *
fd_malloc(Wam wam,
                 size_t size)
{
  void *p;
#if FD_MALLOC_PAD
  size += (FD_MALLOC_PAD);
#endif  /* FD_MALLOC_PAD */
  /* [PM] 4.0 FIXME: update all callers to check the return value from Malloc/SP_malloc, then remove the lazy-check */
  LAZY_NULL_CHECK(p = SP_malloc(size));
  return p;
}

void *
fd_realloc(Wam wam,
		  void *p,
		  size_t size)
{
  void *tmp;
  LAZY_NULL_CHECK(tmp = SP_realloc(p, size));
  return tmp;
}

/* Confine local memory management to the numstack. */
void *
fd_temp_alloc(Wam wam, size_t nchars)
{
  void *p;
  int nlongs = (int)(((nchars-1)>>LogSizeOfWord)+1);
  
  NumstackAlloc(nlongs,p);
  return p;
}

/* Confine local memory management to a reused block. */
void *
fd_perm_alloc2(Wam wam,
		 size_t nchars,
		 struct size_mem *size_mem)
{
  if (size_mem->size==0) {
    size_mem->mem = SP_malloc(nchars);
  } else if (size_mem->size < nchars) {
    size_mem->size = nchars;
    size_mem->mem  = SP_realloc(size_mem->mem,nchars);
  }
  return size_mem->mem;
}

void
fd_perm_free2(Wam wam,
		void *ptr,
		struct size_mem *size_mem)
{
  if (size_mem->size==0)
    SP_free(ptr);
}

struct fd_state *
fd_state(Wam wam)
{
  return &fd;
}
