/* Copyright (C) 1995, Swedish Institute of Computer Science. */
#define SICSTUS_HIDDEN_API 1    /* sp_syserror */
#ifndef LEGACY_TIMOUT           /* if true, use the 3.8 SIGVTALRM-based code (on UNIX) */
#define LEGACY_TIMOUT 1
#endif /* LEGACY_TIMOUT */

/* ([PM] 4.2.1 this also defines SICSTUS_DBG) */
#include <sicstus/config.h>     /* must be done before anything else (for SP_WIN32) */

#if SP_TIMEOUT_INTERRUPT_HARD   /* [PM] 4.1.3 only for experimenting */
#undef SP_TIMEOUT_INTERRUPT_RESTART
#define SP_TIMEOUT_INTERRUPT_RESTART 0
#endif  /* SP_TIMEOUT_INTERRUPT_HARD */

#ifndef SP_TIMEOUT_INTERRUPT_RESTART
/* [PM] 4.1.3 Do not interrupt I/O when a timeout fires */
#define SP_TIMEOUT_INTERRUPT_RESTART 1
#endif  /* SP_TIMEOUT_INTERRUPT_RESTART */
/********************************************************************************/

#if !SP_WIN32                   /* in sicstus/config.h */

#define timeout_raise_os_error(RC, PRED, CALL) sp_raise_errno_error((RC), (PRED), (CALL))

#if LEGACY_TIMOUT

#include <stdio.h>

#if HAVE_TIME_H
#include <time.h>
#endif

#if HAVE_SYS_TIME_H
#include <sys/time.h>
#endif

#include <signal.h>
#include <errno.h>              /* [PM] 3.8.7 should use HAVE_ERRNO_H */

#include <sys/times.h>          /* times() */
#include <unistd.h>             /* sysconf() */

/* done above: #include <sicstus/config.h> */
#include <sicstus/sicstus.h>

#ifndef SETITIMER_EARLY
/* [PM] 3.9.2 Set to 1 on platforms where setitimer(ITIMER_VIRTUAL) can signal too early.
   This problem exists on IRIX 6.5, Tru64 5.1.
*/
#define SETITIMER_EARLY 0
#endif

#ifdef SETITIMER_EARLY_OVERRIDE
#undef SETITIMER_EARLY
#define SETITIMER_EARLY SETITIMER_EARLY_OVERRIDE
#endif

#ifndef PREVENT_IMMEDIATE_TIMEOUT
/* [PM] 3.10.0b1 Always use a timeout larger than the clock resolution. See to_start_timer_a */
#define PREVENT_IMMEDIATE_TIMEOUT 1
#endif /* PREVENT_IMMEDIATE_TIMEOUT */

#if SICSTUS_DBG
#if !DEBUG_TIMEOUT
#undef DEBUG_TIMEOUT
#define DEBUG_TIMEOUT SICSTUS_DBG
#endif /* !DEBUG_TIMEOUT */
#endif /* SICSTUS_DBG */

#if DEBUG_TIMEOUT

#if !SETITIMER_EARLY            /* [PM] 3.10.0b1 early setitimer is not expected (so we want to report them as errors if they happen anyway) */
#undef SETITIMER_EARLY
#define SETITIMER_EARLY 2       /* [PM] 3.10.0b1 means report early setitimer but otherwise behave like SETITIMER_EARLY==0 */
#endif /* SETITIMER_EARLY */

#endif /* DEBUG_TIMEOUT */

#if DEBUG_TIMEOUT

#if HAVE_GETENV
#define SHOULD_DBG_PRINTF (getenv("SP_TIMEOUT_DEBUG") != NULL)
#else  /* !HAVE_GETENV */
#if DEBUG_TIMEOUT > 1
#define SHOULD_DBG_PRINTF 1
#else  /* DEBUG_TIMEOUT <= 1 */
#define SHOULD_DBG_PRINTF 0
#endif  /* DEBUG_TIMEOUT <= 1  */
#endif  /* !HAVE_GETENV */

#define dbg_fprintf(ARGS) do {if (SHOULD_DBG_PRINTF) {fprintf(stderr, "\n** DBGc: "); fprintf ARGS; fprintf(stderr, "\n"); fflush(stderr);} } while (0)
#define dbg_printf(ARGS) do {if (SHOULD_DBG_PRINTF) {printf("\n** DBGc:"); printf ARGS; fflush(stdout);}} while (0)
#else  /* !DEBUG_TIMEOUT */
#define dbg_fprintf(_ARGS)
#define dbg_printf(_ARGS)
#endif /* !DEBUG_TIMEOUT */


/* time_out/3 primitives. Uses exception and callback mechanisms */

/* Exported functions */

#include "timeout_glue.h"

#define TimerUnitsPerSecond 1000
#define MicroSecondsPerTimerUnit (1000000/(TimerUnitsPerSecond))

#define Timer ITIMER_VIRTUAL /* process virtual time */


static int alarm_clock_on(SP_integer time); /* zero on success, errno-value otherwise */
static int alarm_clock_off(void);  /* zero on success, errno-value otherwise */
static SP_integer get_cpu_now(void);

struct timeout_state {
  SP_atom atom_off;
  int alarm_clock_is_on;
#if DEBUG_TIMEOUT
  struct timeval abs_timeout;
#endif /* DEBUG_TIMEOUT */
  SP_integer expiry_abs;              /* absolute time at which timer is set to expire */
  SP_integer clocks_per_second;       /* [PM] 3.9.2 sysconf(_SC_CLK_TCK) */
#if SETITIMER_EARLY /* [PM] 3.9.2 */
  SP_integer last_setitimer_time;     /* argument to most recent (regular) setitimer() */
  struct tms last_times_tms;    /* times() at time of most recent (regular) setitimer() */
#endif /* SETITIMER_EARLY */
};


/* [PM] 3.9b5 PRM 2939 must be static! Otherwise it will share space
   with other variables of the same name when using --static
   --resources=timeout,<OTHER RESOURCE>

   Happened for --resources=timeout,tcltk
*/
static struct timeout_state local;


void to_init(int when)
{
  (void)when;                  /* [PM] 3.9b5 avoid -Wunused */

  {
    SP_integer cps;
    errno = 0;
    cps = sysconf(_SC_CLK_TCK);
    if (cps <= 0)
      {
        int e = errno;
        SP_ASSERT(0);           /* do not expect failure */
        cps = 100;              /* a common value */
        (void)e;
      }
    local.clocks_per_second = cps;
  }

  dbg_fprintf((stderr, "sysconf(_SC_CLK_TCK) == %" SPRIdINTEGER "", (SP_integer)local.clocks_per_second));

  (void)SP_register_atom(local.atom_off = SP_atom_from_string("off"));

  /* [PM] 4.0 See the SP3 version of this file for why SP_set_reinit_hook is bogus as implemented in SP3 */
#if 0
  (void) SP_set_reinit_hook(to_reinit_hook);
#endif  /* 0 */

  (void) alarm_clock_off();
}

void to_deinit(int when)
{
  (void)when;                  /* [PM] 3.9b5 avoid -Wunused */

  (void) alarm_clock_off();     /* [PM] also ignore error */
  (void)SP_unregister_atom(local.atom_off);
#if 0
  (void) SP_set_reinit_hook(NULL);
#endif  /* 0 */
}

/* [PM] 4.0 for Suite/timeout.pl */
SP_integer SPCDECL to_clocks_per_second(void)
{
  return (SP_integer)local.clocks_per_second;
}

/*------------------------------------------------------------------*/
/* foreign(to_start_timer_a, '$start_timer_a'(+term, +term, -integer)).. */
/*
%% set timer to fire at time min(ContExpires, <<CPU now>>+Limit)
%% Either ContExpires or Limit can be the atom 'off'.

  Return
  0 -- timer started
  1 -- current time is already at or past ContExpires
  <0 -- some other error (will not happen. Raise exception here directly).
*/
SP_integer to_start_timer_a(
                      SP_term_ref tContExpires, /* off | Integer>0 absolute time (ms) */
                      SP_term_ref tLimit /* off | Integer>0 time interval (ms) */
                      )
{
  SP_integer cpu_now = get_cpu_now();
  SP_integer timeout = 0;
  int rc;

#if DEBUG_TIMEOUT
  {
    dbg_fprintf((stderr, "to_start_timer_a("));
    if (SP_is_atom(tContExpires))
      {
        dbg_fprintf((stderr, "ContExpires=off, "));
      }
    else
      {
        SP_integer tmp;
        SP_get_integer(tContExpires, &tmp);
        dbg_fprintf((stderr, "ContExpires=%" SPRIdINTEGER "ms, ", (SP_integer) tmp));
      }

    if (SP_is_atom(tLimit))
      {
        dbg_fprintf((stderr, "Limit=off)"));
      }
    else
      {
        SP_integer tmp;
        SP_get_integer(tLimit, &tmp);
        dbg_fprintf((stderr, "Limit=%" SPRIdINTEGER "ms)", (SP_integer) tmp));
      }
    dbg_fprintf((stderr, " cpu_now = %" SPRIdINTEGER "ms\n", (SP_integer)((cpu_now*1000)/TimerUnitsPerSecond)));
  }
#endif /* DEBUG_TIMEOUT */

  if (SP_is_atom(tLimit)) /* 'off' */
    {
      ;                         /* do nothing */
    }
  else
    {
      SP_integer Limit;
      if (!SP_get_integer(tLimit, &Limit)) goto barf;
      timeout = Limit;
      
      #if PREVENT_IMMEDIATE_TIMEOUT /* [PM] 3.10 always true */
      {
        /*
          %% [PM] 3.9.2 Note that for N less than or equal to the OS timer
          %% resolution the following will sometimes timeout
          %%   time_out(time_out(true,1000,_),N).
          %%
          %% This is because the inner time_out goal will get charged with
          %% either no time at all or 10ms depending on when the OS time
          %% interrupt happens (assuming a timer resolution of 10ms).
          %%
          %% The symptom is an error reported for test1 in Suite/timeout.pl
        */
        /* round towards infinity */
        SP_integer timer_resolution = (TimerUnitsPerSecond + (local.clocks_per_second - 1))/local.clocks_per_second;
        if (timeout <= timer_resolution)
          {
            dbg_printf(("DBGc: Adjusting original timeout %" SPRIdINTEGER "ms to %" SPRIdINTEGER "ms",
                         (SP_integer)timeout, (SP_integer)timer_resolution+1));
            timeout = timer_resolution+1;
          }
      }
      #endif /* PREVENT_IMMEDIATE_TIMEOUT */

    }
  /* timeout is zero or actual limit */
  
  if (SP_is_atom(tContExpires)) /* 'off' */
    {
      ;                         /* do nothing */
    }
  else
    {
      SP_integer ContExpires;
      SP_integer ContLimit;

      if (!SP_get_integer(tContExpires, &ContExpires)) goto barf;
      if (ContExpires <= cpu_now)
        {
          dbg_printf(("DBGc: %" SPRIdINTEGER " has already happened (now==%" SPRIdINTEGER "), returning 1",
                      (SP_integer)ContExpires, (SP_integer)cpu_now));
          return 1;             /* ContExpires has already happened */
        }
      ContLimit = ContExpires-cpu_now;
      
      if (timeout == 0          /* Limit was 'off' */
          || ContLimit < timeout) /* ContExpires expires before cpu_now+Limit */
        {
          timeout = ContLimit;
        }
    }
  
  if (!timeout)                   /* ContExpires and Limit are 'off' */
    {
      if (local.alarm_clock_is_on) /* unnecessary optimization */
        {
          int rc;

          rc = alarm_clock_off();
          if (rc != 0)             /* error */
            {
              /* error */
              timeout_raise_os_error(rc, "$start_timer_a/3", "setitimer");
              return -1;
            }
        }
      return 0;
    }
  /* timeout is how long until timer should trigger */
  local.expiry_abs = cpu_now+timeout; /* This is either ContExpires or (cpu_now+Limit) */
  rc = alarm_clock_on(timeout);
  if (rc != 0) /* error (from setitimer) */
    {
      timeout_raise_os_error(rc, "$start_timer_a/3", "setitimer");
      return -1;
    }
  return 0;                     /* no error */
 barf:

  dbg_fprintf((stderr, "ERROR: in to_start_timer_a\n"));
  return -1;
}

/* foreign(to_stop_timer_a, '$stop_timer_a'([-term])).
   Return 'off' or the *absolute* time at which the timer was set to expire.
*/
SP_term_ref to_stop_timer_a(void)
{
  SP_term_ref term = SP_new_term_ref();
  
  if (!local.alarm_clock_is_on)
    {
      dbg_printf(("DBGc: to_stop_timer_a was off\n"));

      SP_put_atom(term, local.atom_off);
    }
  else
    {
      int rc;

      rc = alarm_clock_off();
      if (rc != 0) /* error */
        {
          /* It is off if error occurred. Also, the caller will call
             start_timer which will likely raise an error if the problem persists. */
          (void)rc;
          SP_put_atom(term, local.atom_off);
        }
      else
        {
          dbg_printf(("DBGc: to_stop_timer_a was %" SPRIdINTEGER "ms\n", (SP_integer) local.expiry_abs));

          SP_put_integer(term, local.expiry_abs);
        }
    }
  return term;
}

/* foreign(to_timer_now, '$timer_now'([-term])).
   Return the current CPU (user) time
 */

SP_term_ref to_timer_now(void)
{
  SP_term_ref term = SP_new_term_ref();
  SP_integer cpu_now = get_cpu_now();

  dbg_printf(("DBGc: to_timer_now %" SPRIdINTEGER "ms\n", (SP_integer)((cpu_now*1000)/TimerUnitsPerSecond)));

  SP_put_integer(term, cpu_now);
  return term;
}

/*------------------------------------------------------------------*/

static int alarm_event(void *ignore)
{
  SP_term_ref t = SP_new_term_ref();

  (void)ignore;
  SPIO_TRACE_LINE_l("Alarm Event", 0);

  dbg_fprintf((stderr, "alarm_event()\n"));

#ifdef SP_sigaction
  SP_sigaction(SIGVTALRM, SP_SIG_IGN, NULL, SPIO_OPTION_NONE);
#else  /* !SP_sigaction */
  SP_signal(SIGVTALRM,SP_SIG_IGN, NULL);
#endif  /* !SP_sigaction */
  SP_put_string(t, "time_out");
  SP_raise_exception(t);
  return SP_FAILURE;
}

#if DEBUG_TIMEOUT
static struct tms last_times_tms;
#endif /* DEBUG_TIMEOUT */

static void alarm_h(int sig, void *user_data)
{
  (void)sig;                    /* [PM] 3.9b5 avoid -Wunused */
  (void)user_data;

#if DEBUG_TIMEOUT
  {
    struct timeval tv;
    (void) gettimeofday(&tv, NULL);
    dbg_fprintf((stderr, "gettimeofday alarm_h now-abs= %" SPRIdINTEGER "us", (SP_integer)(((tv.tv_sec-local.abs_timeout.tv_sec)*1000*1000)+(tv.tv_usec-local.abs_timeout.tv_usec))));
  }
#endif /* DEBUG_TIMEOUT */

#if DEBUG_TIMEOUT > 1
  {
    struct tms times_tms;
    
    times(&times_tms);

    dbg_fprintf((stderr,
            "DBGc: alarm_h\n"
            "DBGc: times() returned:\n"
            "DBGc:    timer_now: %" SPRIdINTEGER "ms user time (%" SPRIdINTEGER ") (%" SPRIdINTEGER "ms since last)\n"
            /* "DBGc:    %" SPRIdINTEGER "ms system time (%" SPRIdINTEGER ")\n" */
            "DBGc:    %" SPRIdINTEGER " clocks per second\n",
          
            (SP_integer)((times_tms.tms_utime*1000)/local.clocks_per_second), (SP_integer)times_tms.tms_utime,
            (SP_integer)(((times_tms.tms_utime-last_times_tms.tms_utime)*1000)/local.clocks_per_second),
            /* (SP_integer)((times_tms.tms_stime*1000)/local.clocks_per_second), (SP_integer)times_tms.tms_stime, */
                 (SP_integer)local.clocks_per_second));

  }

#endif /* DEBUG_TIMEOUT */

  {
#if SETITIMER_EARLY

    struct tms times_tms;
    SP_integer diff;

    times(&times_tms);
    diff = times_tms.tms_utime - local.last_times_tms.tms_utime;
    if ((diff*TimerUnitsPerSecond)/local.clocks_per_second < local.last_setitimer_time) /* early expiry, re-start the timer (FIXME: overflow?) */
      {
        {

          SP_integer time;
          struct itimerval timer;
        
        
          time = local.last_setitimer_time-((diff*TimerUnitsPerSecond)/local.clocks_per_second);

#if PREVENT_IMMEDIATE_TIMEOUT   /* [PM] 3.10 always on */
          { /* [PM] 3.10.0b1 See to_start_timer_a */
            SP_integer timer_resolution = (TimerUnitsPerSecond + (local.clocks_per_second - 1))/local.clocks_per_second;
            if (time <= timer_resolution)
              {
                dbg_printf(("DBGc: alarm_h: Adjusting original timeout %" SPRIdINTEGER "ms to %" SPRIdINTEGER "ms",
                             (SP_integer)time, (SP_integer)timer_resolution+1));
                time = timer_resolution+1;
              }
          }
#endif /* PREVENT_IMMEDIATE_TIMEOUT */

#if (DEBUG_TIMEOUT>1 || (SICSTUS_DBG && SETITIMER_EARLY>1))
          fprintf(stderr, "DBGc: WARNING: diff=%" SPRIdINTEGER "<%" SPRIdINTEGER ", local.last_setitimer_time %" SPRIdINTEGER "ms, times=%" SPRIdINTEGER "HZ, last_times = %" SPRIdINTEGER "HZ, HZ=%" SPRIdINTEGER ", new_setitimer(%" SPRIdINTEGER "ms)\n",
                  (SP_integer)diff,
                  (SP_integer)((local.last_setitimer_time*local.clocks_per_second)/TimerUnitsPerSecond),
                  (SP_integer)local.last_setitimer_time, (SP_integer)times_tms.tms_utime,
                  (SP_integer)local.last_times_tms.tms_utime, (SP_integer)local.clocks_per_second,
                  (SP_integer) time
                  ); fflush(stderr);
#endif /* (DEBUG_TIMEOUT>1 || (SICSTUS_DBG && SETITIMER_EARLY>1)) */

#if (DEBUG_TIMEOUT && SETITIMER_EARLY>1)
          fprintf(stderr, "ERROR: setitimer early (set SETITIMER_EARLY in configure.in for this platform)\n");fflush(stderr);
          goto normal_exit;     /* [PM] Do not adjust for the early signal, let the Suite detect any problems. */
#endif /* (DEBUG_TIMEOUT && SETITIMER_EARLY>1) */

          timer.it_value.tv_sec = time/TimerUnitsPerSecond;
          timer.it_value.tv_usec = (suseconds_t)((time%TimerUnitsPerSecond)*MicroSecondsPerTimerUnit);
          timerclear(&timer.it_interval);
          /* [PM] Should *not* set local.last_times_tms, local.last_setitimer_time. */
          if (setitimer(Timer, &timer, NULL) != 0) /* we do not expect this to fail since it already succeeded once */
            {
#if DEBUG_TIMEOUT
              fprintf(stderr, "ERROR: setitimer() failed, errno=%d\n", (int)errno);fflush(stderr);
#endif /* DEBUG_TIMEOUT */

              /* [PM] Not ideal but should never happen */
              local.alarm_clock_is_on = 0;
              SP_event(alarm_event, NULL);
            }
        }
      }
    else                        /* correct case, local.last_setitimer_time or more elapsed */
#endif /* SETITIMER_EARLY */
      {
#if (DEBUG_TIMEOUT && SETITIMER_EARLY>1)
        normal_exit:
#endif /* (DEBUG_TIMEOUT && SETITIMER_EARLY>1) */

        local.alarm_clock_is_on = 0;
        SP_event(alarm_event, NULL);
      }
  }
}


/*------------------------------------------------------------------*/

#ifndef timerclear
#define	timerclear(tvp)		(tvp)->tv_sec = (tvp)->tv_usec = 0
#endif


/* [PM] 3.8.7 return 0 on success, non-zero errno-value on error. */
/* time is in milliseconds, i.e., TimerUnitsPerSecond==1000, (relative from <<now>>) */
static int alarm_clock_on(SP_integer time)
{
  struct itimerval timer;
  int rc;

#if DEBUG_TIMEOUT && 0
  {
    struct timeval tv;
    (void) gettimeofday(&tv, NULL);
    tv.tv_sec += time/TimerUnitsPerSecond;
    tv.tv_usec += (time%TimerUnitsPerSecond)*MicroSecondsPerTimerUnit;
    local.abs_timeout = tv;
    dbg_printf(("alarm_clock_on %" SPRIdINTEGER "ms, abs {%" SPRIdINTEGER "s,%" SPRIdINTEGER "us}", (SP_integer) time, (SP_integer) tv.tv_sec, (SP_integer) tv.tv_usec));
  }
#endif /* DEBUG_TIMEOUT */

#ifndef TIMEOUT_INTEGRAL_CLK_TCK
#define TIMEOUT_INTEGRAL_CLK_TCK 1
#endif

  #if TIMEOUT_INTEGRAL_CLK_TCK  /* [PM] 3.10.0b1 */
  {
    /* [PM] 3.10.0.b1 Round up to nearest multiple of CLK_TCK
       (clocks_per_second). This is to work around setitimer
       implementations that do not do this for us. (Tru64 5.1, more?)
    */
    SP_uinteger t = (SP_uinteger)time;
    SP_uinteger ticks;
    SP_uinteger x, r;
    SP_uinteger CT = (SP_uinteger)local.clocks_per_second;
    SP_uinteger TU = (SP_uinteger)TimerUnitsPerSecond;
    SP_uinteger US = (SP_uinteger)1000000; /* a million milliseconds per second */
    SP_uinteger y, w;
    SP_uinteger sec, usec;

    /* time (a.k.a. t) rounded up to an even number of CLK_TCK units.
       TU short for TimerUnitsPerSecond (1000)
       CT short for CLK_TCK (local.clocks_per_second, typically 100 or 60)
       t is in TU units, ticks is in CT units
       Want the number of ticks that correspond to t TU units, rounded up. Must avoid overflow.


       t/TU = ticks/CT
       ticks = (t*CT)/TU
       rewrite t as x*TU+r (r in [0,TU-1]) then
       ticks = ((x*TU + r)*CT)/TU = x + (r*CT)/TU
       round up
       ticks <= x*CT + (r*CT + TU-1)/TU

       Since r*CT < TU*CT and it will not overflow, with TU=1000
       and CT is not over 100 and is unlikely to go over a million in
       future OSes.

       x*CT will not overflow if CT<=TU which is presently the case
       and seems unlikely to change in the future.

    */
    x = t/TimerUnitsPerSecond;
    r = t%TimerUnitsPerSecond;
    ticks = x*CT + (r*CT + TU-1)/TU;
    
    /*
      Now convert the number of clock ticks to seconds and microseconds, rounding up, without overflow.
      
      ticks/CT = u/US
      u = (ticks*US)/CT
      rewrite ticks = y*CT+w
      u = (y*CT+w)*US/CT = y*CT*US/CT + w*US/CT = y*US + w*US/CT
      round up
      u = y*US + (w*US+(CT-1))/CT
      since seconds is US-units divided by US we have
      sec = y*US/US = y
      usec = (w*US+(CT-1))/CT // this may be more than one seconds worth of milliseconds if CT>US which it is not in practice.

     */
    y = ticks/CT;
    w = ticks%CT;
    sec = y;
    usec = (w*US+(CT-1))/CT;
    timer.it_value.tv_sec = sec;
    timer.it_value.tv_usec = (suseconds_t)usec;

    dbg_printf(("alarm_clock_on(%" SPRIdINTEGER "ms), rounded up to multiple (%" SPRIdINTEGER ") of CLK_TCK (%" SPRIdINTEGER ") {%" SPRIdINTEGER "s,%" SPRIdINTEGER "us}",
                 (SP_integer)time, (SP_integer)ticks, (SP_integer)local.clocks_per_second,
                 (SP_integer)sec, (SP_integer)usec));
    #if DEBUG_TIMEOUT
    if ((sec*TimerUnitsPerSecond)+(usec/MicroSecondsPerTimerUnit) < time)
      {
        fprintf(stderr, "\nERROR: ((sec*TimerUnitsPerSecond)+(usec/MicroSecondsPerTimerUnit) < time)\n"
                        "         ((%" SPRIdINTEGER "*%" SPRIdINTEGER ""              ")+(" "%" SPRIdINTEGER "/%" SPRIdINTEGER ""                 ") < %" SPRIdINTEGER ")\n",
                (SP_integer)sec, (SP_integer)TimerUnitsPerSecond, (SP_integer)usec, (SP_integer)MicroSecondsPerTimerUnit, (SP_integer)time);
        fflush(stderr);
        abort();
      }
    #endif                      /* DEBUG_TIMEOUT */
  }
  #else                         /* !TIMEOUT_INTEGRAL_CLK_TCK */
  {
    timer.it_value.tv_sec = time/TimerUnitsPerSecond;
    timer.it_value.tv_usec = (time%TimerUnitsPerSecond)*MicroSecondsPerTimerUnit;
  }
  #endif                        /* !TIMEOUT_INTEGRAL_CLK_TCK */
  
  timerclear(&timer.it_interval);
#ifdef SP_sigaction
  {
    spio_t_error_code code = SPIO_E_INTERNAL_ERROR;
    spio_t_bits sigaction_options = 0;
#if SP_TIMEOUT_INTERRUPT_RESTART
    SPIO_SET_MASK(sigaction_options, SP_SIGACTION_OPTION_RESTART);
#else  /* !SP_TIMEOUT_INTERRUPT_RESTART */
    SPIO_SET_MASK(sigaction_options, SP_SIGACTION_OPTION_HARD_INTERRUPT);
#endif  /* !SP_TIMEOUT_INTERRUPT_RESTART */
    code = SP_sigaction(SIGVTALRM, alarm_h, NULL, sigaction_options);
    if (SPIO_FAILED(code))      /* unexpected */
      {
        return 1;
      }
  }
#else  /* !SP_sigaction */
  {
#if SICSTUS_VERSION >= 40103
#error "[PM] 4.1.3 expected SP_sigaction to be available"
#endif  /* SICSTUS_VERSION >= 4.1.3 */
    SP_signal(SIGVTALRM, alarm_h, NULL);
  }
#endif  /* !SP_sigaction */

#if SETITIMER_EARLY
  {
    struct tms times_tms;

    times(&times_tms);
    local.last_times_tms = times_tms;
    local.last_setitimer_time = time;
  }
#endif /* SETITIMER_EARLY */


  /* [PM] 3.8.7 setitimer can fail.
     SunOS 5.7 man setitimer:
        All flags to setitimer() other than  ITIMER_REAL  behave  as
        documented  only with "bound" threads. Their ability to mask
        the signal works only with bound threads.  If  the  call  is
        made  using  one  of these flags from an unbound thread, the
        system call returns -1 and sets errno to EACCES.

     In particular, if Java is initialized then (presumably because
     this creates threads) setitimer will fail.
  */
  if (setitimer(Timer, &timer, NULL) != 0)
    {
      rc = errno;

#if DEBUG_TIMEOUT
      fprintf(stderr, "\n*** ERROR: alarm_clock_on: setitimer() error %d%s\n",
              rc,
              (( rc == EACCES) ? " (EACCES)" : "")
              );fflush(stderr);
#endif /* DEBUG_TIMEOUT */

#ifdef SP_sigaction
      SP_sigaction(SIGVTALRM,SP_SIG_IGN, NULL, SPIO_OPTION_NONE);
#else  /* !SP_sigaction */
      SP_signal(SIGVTALRM,SP_SIG_IGN, NULL);
#endif  /* !SP_sigaction */

      SP_ASSERT(rc != 0);
      return rc;
    }

#if DEBUG_TIMEOUT
  {
    dbg_printf(("\nDBGc: alarm_clock_on(%" SPRIdINTEGER "ms)\n", (SP_integer) time));
    {
      struct tms times_tms;
      struct itimerval timer1;

      setitimer(Timer, &timer, &timer1);

      times(&times_tms);
      last_times_tms = times_tms;

      dbg_fprintf((stderr,
                   "DBGc: times() returned:\n"
                   "DBGc:    %" SPRIdINTEGER "ms user time (%" SPRIdINTEGER ")\n"
                   /* "DBGc:    %" SPRIdINTEGER "ms system time (%" SPRIdINTEGER ")\n" */
                   /* "DBGc:    %" SPRIdINTEGER " clocks per second\n", */
                   ,
          
                   (SP_integer)((times_tms.tms_utime*TimerUnitsPerSecond)/local.clocks_per_second), (SP_integer)times_tms.tms_utime
                   /* (SP_integer)((times_tms.tms_stime*TimerUnitsPerSecond)/local.clocks_per_second), (SP_integer)times_tms.tms_stime, */
                   /* (SP_integer)local.clocks_per_second */
                   ));

      dbg_printf(("DBGc: setitimer returned %" SPRIdINTEGER "s %" SPRIdINTEGER "us should be %" SPRIdINTEGER "s %" SPRIdINTEGER "us\n",
                   (SP_integer)timer1.it_value.tv_sec, (SP_integer)timer1.it_value.tv_usec,
                   (SP_integer)timer.it_value.tv_sec, (SP_integer)timer.it_value.tv_usec
                   ));

    }
  }
#endif /* DEBUG_TIMEOUT */

  local.alarm_clock_is_on = 1;
  return 0;
}

/* 
   [PM] 3.8.7 return non-zero errno-value on error (from setitimer())

*/
static int alarm_clock_off(void)
{
  struct itimerval timer1, timer2;
  SP_integer time;
  int rc;
  
#ifdef SP_sigaction
  SP_sigaction(SIGVTALRM,SP_SIG_IGN, NULL, SPIO_OPTION_NONE);
#else  /* !SP_sigaction */
  SP_signal(SIGVTALRM,SP_SIG_IGN, NULL);
#endif  /* !SP_sigaction */

  timerclear(&timer2.it_value);
  timerclear(&timer2.it_interval);
  if (setitimer(Timer,&timer2,&timer1) != 0) /* [PM] 3.8.7 */
    {
      rc = errno;
#if DEBUG_TIMEOUT
      fprintf(stderr, "\n*** ERROR: alarm_clock_off: setitimer() error %d%s\n",
              rc,
              (( rc == EACCES) ? " (EACCES)" : "")
              );fflush(stderr);
#endif /* DEBUG_TIMEOUT */

      /* SIGVTALRM already ignored */
      local.alarm_clock_is_on = 0;

      SP_ASSERT(rc != 0);
      return rc;
    }

  time = timer1.it_value.tv_usec/MicroSecondsPerTimerUnit + timer1.it_value.tv_sec*TimerUnitsPerSecond;
  (void)time;
#if DEBUG_TIMEOUT
  {
    dbg_fprintf((stderr, "\nDBGc: alarm_clock_off()\n"));

    dbg_fprintf((stderr, "setitimer returned %" SPRIdINTEGER "s %" SPRIdINTEGER "us (%" SPRIdINTEGER "ms)\n",
            (SP_integer)timer1.it_value.tv_sec, (SP_integer)timer1.it_value.tv_usec,
                 (SP_integer)time));

    {
      struct tms times_tms;
    
      times(&times_tms);

      dbg_fprintf((stderr,
              "DBGc: times() returned:\n"
              "DBGc:    %" SPRIdINTEGER "ms user time (%" SPRIdINTEGER ") (%" SPRIdINTEGER "ms since last)\n"

              /* "DBGc:    %" SPRIdINTEGER "ms system time (%" SPRIdINTEGER ")\n" */
              "DBGc:    %" SPRIdINTEGER " clocks per second\n",
          
              (SP_integer)((times_tms.tms_utime*1000)/local.clocks_per_second), (SP_integer)times_tms.tms_utime,
              (SP_integer)(((times_tms.tms_utime-last_times_tms.tms_utime)*1000)/local.clocks_per_second),
              /* (SP_integer)((times_tms.tms_stime*1000)/local.clocks_per_second), (SP_integer)times_tms.tms_stime, */
                   (SP_integer)local.clocks_per_second));

    }
  }
#endif /* DEBUG_TIMEOUT */

  local.alarm_clock_is_on = 0;
  return 0;                     /* success */
}

/* [PM] Current time in TimerUnits (ms) */
static SP_integer get_cpu_now(void)
{
  struct tms times_tms;
    
  times(&times_tms);

  return ((times_tms.tms_utime*TimerUnitsPerSecond)/local.clocks_per_second);
}

#endif /* LEGACY_TIMOUT */
#endif /* !SP_WIN32 */

/********************************************************************************/

#if SP_WIN32               /* [PM] 3.9b4 */
/* [PM] 4.0 FIXME: we should perhaps replace much of this with the "Timer Queues" that appeared in Win2k */

#define timeout_raise_os_error(RC, PRED, CALL) sp_raise_win32_error((RC), (PRED), (CALL))

/* done above: #include <sicstus/config.h> */

#include <sicstus/sicstus.h>
#include <windows.h>
#include <tchar.h>
#if HAVE_PROCESS_H
#include <process.h>            /* _beginthreadex et al. */
#endif  /* HAVE_PROCESS_H */
#include <stdlib.h>             /* errno, _doserrno */
#include <stdio.h>
#include <limits.h>             /* LONG_MAX */

#if WINCE
#include <Kfuncs.h>             /* GetCurrentThread() */
#include <Winbase.h>            /* CeGetThreadQuantum() */

/* [PM] 3.11.2 returns MAXDWORD on error. Mask instead of proper error
 handling for simplicity.  Default thread quantum in CE .NET 4.2 is
 100ms */
#define WIN32_MINIMUM_TIMEOUT_ms (CeGetThreadQuantum(GetCurrentThread()) & 0xff)+1

#endif /* WINCE */

#include "timeout_glue.h"

/*
  Idea:
  CreateThread()
  WaitForMultipleObjects with timeout to implement interruptible sleep
  Possibly GetTickCount, perhaps GetSystemTimeAdjustment to adjust actual elapsed time
 */


#if 0 /* [PM] 3.12.3 Ignore GetTickCount issues, we use it only as unlikely fallback. */
#error "Handle wraparound in GetTickCount() (or do proper Process User time time out)/PM"
#endif

#ifndef RAISE_TIMER_THREAD_PRIORITY

/* [PM] 3.10 Raise priority of timer thread to ensure it interrupts
   the main thread when it wakes up. This takes care of test2 error
   (outer timeout taking too much time). It is strange that it is
   necessary to raise the priority as the OS-supplied thread priority
   boost ought to be sufficient */
#define RAISE_TIMER_THREAD_PRIORITY 1
#endif /* RAISE_TIMER_THREAD_PRIORITY */

#ifndef PREVENT_IMMEDIATE_TIMEOUT
/* [PM] 3.10.0b1 Always use a timeout larger than the clock resolution. See to_start_timer_a */
#define PREVENT_IMMEDIATE_TIMEOUT 1
#endif /* PREVENT_IMMEDIATE_TIMEOUT */
#ifndef WIN32_MINIMUM_TIMEOUT_ms
#if 1
#define WIN32_MINIMUM_TIMEOUT_ms (local.timer_resolution_ms + 1)
#else  /* SP3 */
/* [PM] 3.10 Strangely enough 15 to 16ms was experimentally determined
   as the minimum time between calls to GetThreadTimes.  Not the
   expected 10ms (on NT). FIXME: This was on my home machine, cross check at office machine */
/* [PM] 3.10 FIXME: is there a way to determine this value ([PM] 4.0 GetSystemTimeAdjustment) */
#define WIN32_MINIMUM_TIMEOUT_ms (16+1) /* used with PREVENT_IMMEDIATE_TIMEOUT */
#endif  /* SP3 */
#endif

#if SICSTUS_DBG
#if !DEBUG_TIMEOUT
#undef DEBUG_TIMEOUT
#define DEBUG_TIMEOUT SICSTUS_DBG
#endif /* !DEBUG_TIMEOUT */
#endif /* SICSTUS_DBG */

#if DEBUG_TIMEOUT
#if HAVE_GETENV
#define SHOULD_DBG_PRINTF (getenv("SP_TIMEOUT_DEBUG") != NULL)
#else  /* !HAVE_GETENV */
#if DEBUG_TIMEOUT > 1
#define SHOULD_DBG_PRINTF 1
#else  /* DEBUG_TIMEOUT <= 1 */
#define SHOULD_DBG_PRINTF 0
#endif  /* DEBUG_TIMEOUT <= 1  */
#endif  /* !HAVE_GETENV */
#endif  /* DEBUG_TIMEOUT */

#if DEBUG_TIMEOUT
#define dbg_fprintf(ARGS) do {if (SHOULD_DBG_PRINTF) {fprintf(stderr, "%s:%d DBGc: ", __FILE__, (int)__LINE__); fprintf ARGS; fprintf(stderr, "\n"); fflush(stderr);} } while(0)
#define dbg_printf(ARGS) do {if (SHOULD_DBG_PRINTF) {sp_ttyprintf("%s:%d DBGc: ", __FILE__, (int)__LINE__); sp_ttyprintf ARGS; SP_fflush(SP_stderr);} } while (0)
#else  /* !DEBUG_TIMEOUT */
#define dbg_fprintf(_ARGS)
#define dbg_printf(_ARGS)
#endif /* !DEBUG_TIMEOUT */


#if DEBUG_TIMEOUT
#if SP_WIN32

#if HAVE_DEBUGBREAK
#define DEBUG_BREAK() DebugBreak()
#elif defined(_MSC_VER) && defined(_M_IX86)
#define DEBUG_BREAK() do {_asm { int 3h };} while(0)
#else
#error "Need a DEBUG_BREAK for this platform"
#endif /* defined(_MSC_VER) */

#endif /* SP_WIN32 */

#ifndef DEBUG_BREAK
/* Fallback */
#define DEBUG_BREAK() debug_break()
/* put breakpoint in this function */
static void debug_break(void)
{
  ;
}
#endif /* DEBUG_BREAK */

#else  /* !DEBUG_TIMEOUT */
#define DEBUG_BREAK() do{}while(0)
#endif /* !DEBUG_TIMEOUT */

#if DEBUG_TIMEOUT
#define DEBUG_BREAK_MSG(FPRINTFARGS) \
  do{ \
    fprintf(stderr, "\n%s:%d ERROR: (debug break) ", __FILE__, (int)__LINE__); fprintf FPRINTFARGS; fprintf(stderr, "\n"); \
    fflush(stderr); \
    DEBUG_BREAK(); \
  }while(0)
#else  /* !DEBUG_TIMEOUT */
#define DEBUG_BREAK_MSG(FPRINTFARGS) do{}while(0)
#endif /* !DEBUG_TIMEOUT */


#ifndef WORKAROUND_WaitForMultipleObjects_truncates_timeout_BUG
/* [PM] 3.9.2 WaitForMultipleObjects will sometimes return with
   WAIT_TIMEOUT before the timeout interval has expired. Tests
   indicate that the true time out happens at now+trunc(timeout) where
   trunc() rounds down to the nearest 10ms. This was determined on
   Windows XP (NT 5.1), other NT versions may be different Windows
   95/98/ME even more so.
 */
#define WORKAROUND_WaitForMultipleObjects_truncates_timeout_BUG 1
#endif /* WORKAROUND_WaitForMultipleObjects_truncates_timeout_BUG */

#ifndef WIN32_TIMER_RESOLUTION_ms
#if 1
#define WIN32_TIMER_RESOLUTION_ms local.timer_resolution_ms
#else  /* SP3 */
/* [PM] NT value. Presumably this ought to be 55ms on Windows 95/98/ME
   (but unknown if the WaitForMultipleObjects-truncates-timeout bug
   appears there). */
#define WIN32_TIMER_RESOLUTION_ms 10 /* ms between ticks */
#endif  /* SP3 */
#endif  /* WIN32_TIMER_RESOLUTION_ms */

#ifndef WIN32_PROCESS_TIME_MIN_SLEEP
/* The minimimum walltime to sleep while waiting for some amount of thread time to pass */
#define WIN32_PROCESS_TIME_MIN_SLEEP 10 /* heuristic */
#endif /* WIN32_PROCESS_TIME_MIN_SLEEP */

static int alarm_clock_on (SP_integer time); /* zero on success, non-zero GetLastError()-value on failure */
static int alarm_clock_off (int *was_off); /* note, different from non-Win32 version */
static DWORD reSynchTimerThread(void); /* zero on success, non-zero GetLastError()-value on failure */
static void DisplayErrorText(DWORD dwLastError, int c_error, const char *fun_name);

struct {

  SP_atom atom_off;
  int alarm_clock_is_on;

  /* The timer thread signals this event to tell the main thread that it
     is willing to accept a new what_to_do message */
  HANDLE wantNewToDo_Event;
  int have_wantNewToDo_Event;

  /* The main thread signals this event to tell the timer thread that a
     new what_to_do message is available */
  HANDLE newToDoAvailable_Event;
  int have_newToDoAvailable_Event;

#if WINCE
  HANDLE timerThread;
#else  /* !WINCE */
  SP_uinteger timerThread;
#endif  /* !WINCE */
  int have_timerThread;

  DWORD timeToSleep;              /* must be valid when what_to_do==WHAT_TO_DO_SLEEP */
  SP_uinteger lastTime;	          /* GetTickCount() when we timerThread sleep started */

  #define WHAT_TO_DO_SLEEP 1      /* sleep timeToSleep ms before doing SP_event */
  #define WHAT_TO_DO_EXIT 2       /* exit from timerThread */
  #define WHAT_TO_DO_RESTART 3    /* Abort sleep, Wait for next what_to_do */
  int what_to_do;                 /* must be valid before event is signalled. */


  SP_integer expiry_abs;                /* absolute time at which timer is set to expire */
  int usertime_method;            /* 0 -> first time transition to 2 or 3 depending on getenv("SP_WIN32_TIMEOUT_WALLTIME")
                                     3 -> Use GetThreadTimes (if this fails, transitions to 2)
                                     2 -> Use GetTickCount()
                                  */
  HANDLE mainThread;

  DWORD timer_resolution_ms;     /* time between ticks in ms */
  DWORD timer_resolution_us;     /* time between ticks in us */
} local;

#if WINCE
static void debug_msg(wchar_t const *fmt, ...)
{
  va_list ap;
  TCHAR msg[2000];
  va_start(ap, fmt);
  vswprintf(msg, fmt, ap);
  va_end(ap);
  MessageBoxW(NULL, msg, L"SICStus dbg", MB_OK);
}
static void error_msg(wchar_t const *fmt, ...)
{
  va_list ap;
  wchar_t msg[2000];
  va_start(ap, fmt);
  vswprintf(msg, fmt, ap);
  va_end(ap);
  MessageBoxW(NULL, msg, L"SICStus ERROR", MB_OK);
}
#endif /* WINCE */

/* When this returns the timer thread has died or been forcibly killed */
static void wait_for_timer_thread_death(void)
{
  DWORD rc;
  DWORD msToSleep = 60*1000;    /* Wait 1 minute, then kill it forcibly */

  /* Consider if this should be MsgWaitForMultipleObjects */
  rc = WaitForSingleObject((HANDLE)local.timerThread, msToSleep);
  
  if (rc == WAIT_OBJECT_0)      /* timer thread exited */
    {
      return;
    }

  /* Something bad happened. */
#if DEBUG_TIMEOUT
  /* not dbg_fprintf */
  fprintf(stderr, "\n*** ERROR: Got error %" SPRIuINTEGER" (%s) waiting for timer thread to die\n", (SP_uinteger)rc, (rc == WAIT_TIMEOUT ? "TIMEOUT" : (rc == WAIT_FAILED ? "FAILED" : "UNKNOWN")));fflush(stderr);
#endif/* DEBUG_TIMEOUT */
  
  if (rc == WAIT_TIMEOUT)
    {
    }
  else if (rc == WAIT_FAILED)
    {
#if DEBUG_TIMEOUT
      DisplayErrorText(GetLastError(), 0, "wait_for_timer_thread_death");
#endif/* DEBUG_TIMEOUT */
    }
#if DEBUG_TIMEOUT
  /* not dbg_fprintf */
  fprintf(stderr, "\n*** ERROR: Will force timer thread to quit\n");fflush(stderr);
#endif /* DEBUG_TIMEOUT */
  SP_ASSERT(0);
  if (!TerminateThread((HANDLE)local.timerThread, 1))
    {
#if DEBUG_TIMEOUT
      DisplayErrorText(GetLastError(), 0, "wait_for_timer_thread_death");
#endif/* DEBUG_TIMEOUT */
    }
}


static void to_cleanup(void)
{
  if (local.have_timerThread)
    {
      if (reSynchTimerThread() != 0)
        {
#if DEBUG_TIMEOUT
          DisplayErrorText(0, 0, "to_cleanup");
#endif /* DEBUG_TIMEOUT */
        }
      else
        {
          local.what_to_do = WHAT_TO_DO_EXIT;
          if (!SetEvent(local.newToDoAvailable_Event))
            {
              #if DEBUG_TIMEOUT
              DisplayErrorText(GetLastError(), 0, "to_cleanup");
              #endif/* DEBUG_TIMEOUT */
            }
        }
      /* we need to wait until the timer thread exits. If return from
         to_cleanup before the timer thread is done
         unload_foreign_resource will be performed, unloading the code that
         the timer thread is running. [PM] had a Heisenbug caused by
         returning too soon from to_cleanup. */

      wait_for_timer_thread_death();
      /* invariant: The timer thread is dead here */
      
      if (!CloseHandle((HANDLE)local.timerThread))
        {
          #if DEBUG_TIMEOUT
          DisplayErrorText(GetLastError(), 0, "to_cleanup");
          #endif
        }

      local.have_timerThread = 0;
    }
  
  if (local.have_newToDoAvailable_Event)
    {
      local.have_newToDoAvailable_Event = 0;
      if (!CloseHandle(local.newToDoAvailable_Event))
        {
          #if DEBUG_TIMEOUT
          DisplayErrorText(GetLastError(), 0, "to_cleanup");
          #endif
        }
    }

  if (local.have_wantNewToDo_Event)
    {
      local.have_wantNewToDo_Event = 0;
      if (!CloseHandle(local.wantNewToDo_Event))
        {
          #if DEBUG_TIMEOUT
          DisplayErrorText(GetLastError(), 0, "to_cleanup");
          #endif
        }
    }
  local.alarm_clock_is_on = 0;
}

static void to_reinit(void)
{
  (void) alarm_clock_off(NULL);
  (void) SP_register_atom(local.atom_off = SP_atom_from_string("off"));
}

/* timer resolution in micro seconds */
static DWORD timer_resolution_us(void)
{
  DWORD clockInterval;        /* 100 ns units */
  DWORD adjustment;
  BOOL  adjustmentDisabled;

  if (GetSystemTimeAdjustment(&adjustment,
                              &clockInterval,
                              &adjustmentDisabled) == 0)
    {
      DWORD gle = GetLastError();
      SP_ASSERT(0);         /* should not fail */
      (void)gle;
      clockInterval = 156250; /* 15.625 ms (as observed) */
    }
  (void)adjustment;
  (void)adjustmentDisabled;

  return (clockInterval+ (10-1))/10; /* round up to even us */
}

void SPCDECL to_init(int when)
{
  local.alarm_clock_is_on = 0;
  local.have_wantNewToDo_Event = 0;
  local.have_newToDoAvailable_Event = 0;
  local.have_timerThread = 0;
  /* local.expiry_abs = 0; no need to init */
  local.usertime_method = 0;
  local.timer_resolution_us = timer_resolution_us();
  local.timer_resolution_ms = local.timer_resolution_us/1000;

  /* On Win32:
     The thread id is not the handle, instead a real handle to the
     thread can be obtained by duplicating the GetCurrentThread()
     pseudo-handle.

     On WinCE:
     DuplicateHandle does not exist (until CE .NET)
     Fortunately, the thread id is also the handle of the thread.
  */
  #if WINCE
  local.mainThread = (HANDLE)GetCurrentThreadId();
  #else/* !WINCE */
  {
    if (!DuplicateHandle(GetCurrentProcess(), GetCurrentThread(), /* source process and handle */
                         GetCurrentProcess(), &local.mainThread , /* target process and (ptr to) handle */
                         0,
                         FALSE,
                         DUPLICATE_SAME_ACCESS))
      {
        DEBUG_BREAK_MSG((stderr, "DuplicateHandle failed (err==%d)", (int)GetLastError()));

        local.mainThread = INVALID_HANDLE_VALUE;
      }
  }
  #endif  /* !WINCE */
  to_reinit();
}

void SPCDECL to_deinit(int when)
{
  (void) alarm_clock_off(NULL);     /* ignore error and was_off */
  to_cleanup();
  (void) SP_unregister_atom(local.atom_off);
}

#include "win32gettimes.ic"

#if DEBUG_TIMEOUT

#if WINCE
/* not available on CE */
/* Lets see if the lack of precision triggers the problem described in current_time() */
static VOID GetSystemTimeAsFileTime(
  LPFILETIME lpSystemTimeAsFileTime  // file time
)
{
  SYSTEMTIME st;
  GetSystemTime( &st );
  SystemTimeToFileTime( &st, lpSystemTimeAsFileTime );
}
#endif /* WINCE */

/* Current UTC time in 100nsec units */
static ULONGLONG current_time(void)
{
  FILETIME ft;
  ULARGE_INTEGER tmp;

  /* 
     Note: Using GetSystemTimeAsFileTime instead of GetSystemTime is
     critical, GetSystemTime has too low resolution, causing process
     creation time (as reported by GetProcessTimes) to sometimes lie
     in the future. 
  */
  GetSystemTimeAsFileTime(&ft);
  return SP_FILETIME_TO_ULONGLONG(ft);
}
#endif /* DEBUG_TIMEOUT */

/* in milliseconds */
static SP_integer win32_usertime(void)
{
  if (local.usertime_method == 0)     /* first call. Decide on method to use. */
    {
      char *val = NULL;
      #if HAVE_GETENV
      val = getenv("SP_WIN32_TIMEOUT_WALLTIME");
      #endif  /* HAVE_GETENV */

      if (val && val[0] != '\0') /* non-empty value */
        {
          #if DEBUG_TIMEOUT
          /* not dbg_fprintf */
          fprintf(stderr, "DBG: win32_usertime() will use old GetTickCount()-based method (SP_WIN32_TIMEOUT_WALLTIME set)\n");fflush(stderr);
          #endif/* DEBUG_TIMEOUT */

          local.usertime_method = 2;  /* force use of the old method */
        }
      else
        {
          local.usertime_method = 3;  /* try GetThreadTimes method */
          dbg_fprintf((stderr, "win32_usertime will use GetThreadTimes()\n"));
        }
    }

  if (0)
    {
    retry:
      #if DEBUG_TIMEOUT
      /* not dbg_fprintf */
      fprintf(stderr, "win32_usertime() falls back to old clock()-based method\n");fflush(stderr);
      #endif/* DEBUG_TIMEOUT */

      /* FALLTHROUGH */
      ;
    }
  if (local.usertime_method == 2)
    {
      /* [PM] 4.2.3 FIXME: We should use GetTickCount64() but it is new since Vista */
      return GetTickCount();
    }

  /* elif local.usertime_method == 3 */
  {
    ULONGLONG kernelTime;
    ULONGLONG userTime;
    ULONGLONG threadStartTime;

    if (!sp_GetTime1(local.mainThread, &kernelTime, &userTime, &threadStartTime))
      {
        #if DEBUG_TIMEOUT
        /* not dbg_fprintf */
        fprintf(stderr, "ERROR: could not use GetThreadTimes()\n");fflush(stderr);
        #endif/* DEBUG_TIMEOUT */
        /* GetThreadTimes does not exist. Fallback to old method */
        local.usertime_method = 2; /* do not try GetThreadTimes once it failed once */
        SP_ASSERT(0);
        goto retry;
      }
#if DEBUG_TIMEOUT
    {
      if ((kernelTime/(10*1000)) > LONG_MAX) DEBUG_BREAK_MSG((stderr, "kernelTime overflow"));
      if ((userTime/(10*1000)) > LONG_MAX) DEBUG_BREAK_MSG((stderr, "userTime overflow"));
      if (((current_time() - threadStartTime)/(10*1000)) > LONG_MAX) DEBUG_BREAK_MSG((stderr, "wallTime overflow"));
    }
#endif/* DEBUG_TIMEOUT */

    /* Have ULONGLONG values */
    {
      SP_integer cpu_now = (SP_integer)(userTime/(10*1000)); /* 100ns units to milliseconds */
      
      return cpu_now;
    }
  }
}

static SP_integer myGetTickCount(void)
{
  return win32_usertime();
}

#if DEBUG_TIMEOUT
static void
DisplayErrorText(
                 DWORD dwLastError,
                 int c_error,
                 const char *fun_name
    )
{
  HMODULE hModule = NULL; // default to system source
  LPTSTR MessageBuffer;
  DWORD dwBufferLength;

  DWORD dwFormatFlags = 
    ( FORMAT_MESSAGE_IGNORE_INSERTS | FORMAT_MESSAGE_FROM_SYSTEM );

  if (!dwLastError)
    {
      /* not dbg_fprintf */
      fprintf(stderr, "*** ERROR: (errno==%d) in %s\n", c_error, (fun_name ? fun_name : "???"));
      fflush(stderr);
      return;
    }

  dwBufferLength = FormatMessage(
                                 (dwFormatFlags | FORMAT_MESSAGE_ALLOCATE_BUFFER),
                                  hModule, // module to get message from (NULL == system)
                                  dwLastError,
                                  MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // default language
                                  (LPTSTR) &MessageBuffer, /* [PM] 3.11.2 cast needed with FORMAT_MESSAGE_ALLOCATE_BUFFER */
                                  0,
                                  NULL
                                  );

#ifndef _UNICODE
  /* not dbg_fprintf */
  fprintf(stderr, "*** ERROR: in %s, GetLastError()==%" SPRIdINTEGER "%s%s\n",
          (fun_name ? fun_name : "???"),
          (SP_integer)dwLastError,
          (dwBufferLength ? ": " : ""), 
          (dwBufferLength ? MessageBuffer : ""));
#else  /* _UNICODE */
  fprintf(stderr, "*** ERROR: in %s, GetLastError()==%" SPRIdINTEGER "%s%S\n",
          (fun_name ? fun_name : "???"),
          (SP_integer) dwLastError,
          (dwBufferLength ? ": " : ""), 
          (dwBufferLength ? MessageBuffer : _T("")));
#endif /* _UNICODE */

  fflush(stderr);
      
  if (dwBufferLength)
    {
      LocalFree(MessageBuffer);
    }
}
#endif /* DEBUG_TIMEOUT */

static int __cdecl timerSPEventFunc (void* data)
{
  SP_term_ref t = SP_new_term_ref();

  SP_put_string(t, "time_out");
  SP_raise_exception(t);

  dbg_fprintf((stderr, "timerSPEventFunc does raise(time_out)\n"));
  return SP_FAILURE;
}

#if WINCE
/* [PM] 3.11.2 CreateThread callback */
static DWORD WINAPI timerThreadFunc (void* data)
#else  /* !WINCE */
/* [PM] 3.11.2 _beginthreadex callback */
static unsigned int __stdcall timerThreadFunc (void* data)
#endif  /* !WINCE */
{
  unsigned int exit_code = (unsigned) EXIT_SUCCESS; /* currently not used */
  DWORD rc;
  int noLastError = 0;          /* true if GetLastError() is irrelevant */
  DWORD msToSleep;
  int keep_events = 0;          /* true if the events are already set (used at timeout since, in that case, WaitForMultipleObjects does not clear wantNewToDo_Event */
  SP_integer now, now2;
  DWORD msToSleepExtra;
  DWORD msToSleepMin;



  (void)now;
  (void)now2;

 wait_for_something_to_do:

  #if WINCE
  // debug_msg(L"timerThreadFunc wait_for_something_to_do");
  #error "WINCE FIXME"
  #endif/* WINCE */

  msToSleep = INFINITE;
  msToSleepExtra = 0;
  /* never sleep less than this (heuristic). */
  msToSleepMin = WIN32_PROCESS_TIME_MIN_SLEEP;

 wait_for_timeout_or_something_new_to_do:

  if (keep_events)              /* last wait did timeout */
    {
      dbg_fprintf((stderr, "timerThreadFunc keeps wantNewToDo_Event\n"));
    }
  else
    {
      dbg_fprintf((stderr, "timerThreadFunc sets wantNewToDo_Event\n"));

      if (!SetEvent(local.wantNewToDo_Event)) /* tell main thread that we are about to wait for new what_to_do */
        {
#if WINCE
#error "WINCE FIXME"
          error_msg(L"timerThreadFunc SetEvent(wantNewToDo_Event) failed (GLE 0x%" SPRIxINTEGER ")", (SP_uinteger)GetLastError());
#endif/* WINCE */

          goto barf;
        }
    }

  /* [PM] 3.9.2 Now the main thread will (is allowed to) set newToDoAvailable_Event */

  now = myGetTickCount();

  dbg_fprintf((stderr, "timerThreadFunc Enter WaitForMultipleObjects(newToDoAvailable_Event, %dms+%dms%s (expire at %" SPRIdINTEGER "ms))..", (int)msToSleep, (int)msToSleepExtra, (msToSleep == INFINITE ? " (INFINITE)" : ""), (SP_integer) (msToSleep+now)));

  rc = WaitForSingleObject(local.newToDoAvailable_Event, (msToSleep == INFINITE ? msToSleep : msToSleep+msToSleepExtra));

  now2 = myGetTickCount();

  dbg_fprintf((stderr, "..Exit WaitForMultipleObjects(%dms+%dms%s)==%d.. at %" SPRIdINTEGER "ms\n", (int)msToSleep,(int)msToSleepExtra,(msToSleep==INFINITE ? " (INFINITE)" : ""), (int)rc,(SP_integer)now2));

  if (rc == WAIT_TIMEOUT && (now+((SP_integer)msToSleep) > now2))
    {
      {                         /* extra indent to match SP3 formatting (eases diff) */
        /* We expect WaitForMultipleObjects (which sleeps in walltime)
           to return before the corresponding thread/process time has
           elapsed. We can ignore the bug that WaitForMultipleObjects
           may return wall-time-too-soon since we will just re-start the
           wait anyway. */

        dbg_fprintf((stderr, "\nDBG: WaitForMultipleObjects(%dms walltime expire at %" SPRIdINTEGER "ms thread time) expired at %" SPRIdINTEGER "ms thread time.\n", (int)msToSleep,(SP_integer)(now+msToSleep), (SP_integer)now2));

        /* [PM] 3.10 FIXME: To protect against busy wait caused by low
           OS timer resolution we should consider incrementing
           msToSleepMin with WIN32_PROCESS_TIME_MIN_SLEEP each time we
           get here and no walltime has elapsed. For this we need a
           way to measure elapsed wall-time.
        */

        msToSleep = (DWORD) ((now+msToSleep) - now2);
        msToSleep = max(msToSleep, msToSleepMin);

        dbg_fprintf((stderr, "Restarting with msToSleep in timerThreadFunc ==%" SPRIdINTEGER "ms\n", (SP_integer) msToSleep));

#if WORKAROUND_WaitForMultipleObjects_truncates_timeout_BUG /* [PM] 3.9.2 */
        {
          {                         /* see duplicate code below */
            DWORD const timer_resolution = WIN32_TIMER_RESOLUTION_ms;
            msToSleepExtra = (timer_resolution-1);
            msToSleepExtra = (((msToSleep+msToSleepExtra) / timer_resolution)*timer_resolution) - msToSleep;
          }

#if DEBUG_TIMEOUT
          if (msToSleepExtra != 0)
            { dbg_fprintf((stderr, ".. fudged msToSleep in timerThreadFunc ==%" SPRIdINTEGER "ms\n", (SP_integer) (msToSleep+msToSleepExtra))); }
#endif/* DEBUG_TIMEOUT */
        }
#endif/* WORKAROUND_WaitForMultipleObjects_truncates_timeout_BUG */

        goto wait_for_timeout_or_something_new_to_do;
      }
    }


  if (rc == WAIT_OBJECT_0)      /* event signalled, if we were sleep the sleep is now ignored */
    {
      dbg_fprintf((stderr, "..what_to_do==%d (%s)\n", (int)local.what_to_do,
              (local.what_to_do==1 ? "SLEEP" :
               (local.what_to_do==2 ? "EXIT" :
                (local.what_to_do==3 ? "RESTART" :
                 "UNKNOWN")))
              ));
      dbg_fprintf((stderr, "\nDBG: timerThreadFunc cleared newToDoAvailable_Event\n"));


      noLastError = 1;
      if (local.what_to_do == WHAT_TO_DO_EXIT)
        {
          #if WINCE
          // debug_msg(L"timerThreadFunc what_to_do==WHAT_TO_DO_EXIT");
          #error "WINCE FIXME"
          #endif/* WINCE */

          goto do_exit;
        }
      else if (local.what_to_do == WHAT_TO_DO_SLEEP)
        {
          msToSleep = local.timeToSleep;

          dbg_fprintf((stderr, "..msToSleep in timerThreadFunc ==%" SPRIdINTEGER "ms\n", (SP_integer) msToSleep));

          #if WORKAROUND_WaitForMultipleObjects_truncates_timeout_BUG /* [PM] 3.9.2 */
          {
            DWORD const timer_resolution = WIN32_TIMER_RESOLUTION_ms;
            msToSleepExtra = (timer_resolution-1);
          }
          #if DEBUG_TIMEOUT
          if (msToSleepExtra != 0)
            { dbg_fprintf((stderr, ".. fudged msToSleep in timerThreadFunc ==%" SPRIdINTEGER "ms\n", (SP_integer) (msToSleep+msToSleepExtra))); }
          #endif/* DEBUG_TIMEOUT */

          #endif /* WORKAROUND_WaitForMultipleObjects_truncates_timeout_BUG */

          /* We re-use the event so that the wait can be aborted */
          goto wait_for_timeout_or_something_new_to_do;
        }
      else if (local.what_to_do == WHAT_TO_DO_RESTART)
        {
          goto wait_for_something_to_do; /* sleep was cancelled */
        }
      else                  /* unknown what_to_do */
        {
          #if WINCE
          error_msg(L"timerThreadFunc what_to_do==0x%" SPRIxINTEGER"", (SP_uinteger)local.what_to_do);
          #error "WINCE FIXME"
          #endif/* WINCE */

          goto barf;
        }
    }
  else if (rc == WAIT_TIMEOUT) /* ordinary sleep for local.timeToSleep ms */
    {
      dbg_fprintf((stderr, "..WAIT_TIMEOUT (newToDoAvailable_Event unchanged)\n"));

      local.alarm_clock_is_on = 0;
      if (!SP_event(timerSPEventFunc, NULL))
        {
          goto barf;
        }
      goto wait_for_something_to_do;
    }
  else if (rc == WAIT_FAILED)
    {
      goto barf;
    }
  else                          /* unexpected (WAIT_ABANDON?) */
    {
      noLastError = 1;
      goto barf;
    }
  /* NOT REACHED */

 barf:
  exit_code = (unsigned) EXIT_FAILURE;
#if DEBUG_TIMEOUT
  DisplayErrorText(( noLastError ? 0 : GetLastError()), 0, "timerThreadFunc");
#endif /* DEBUG_TIMEOUT */

 do_exit:

  /* have_timerThread is owned by the main thread. It will be cleared
     when the thread handle indicates that the thread is truly
     dead. */

  return exit_code;
}

/* Wait for the timer thread to be ready to accept a new message (or
   die). This should be a very short wait.
   [PM] 4.0 zero on success, non-zero GetLastError() on failure
*/
static DWORD waitForTimerThread(int reset)
{
  HANDLE events[2];
  DWORD dwError;                /* GetLastError() value */
  int rc;
  int c_error = 0;              /* errno */

  dbg_fprintf((stderr, "waitForTimerThread(wantNewToDo_Event, %s)\n", (reset ? "RESET" : "!RESET")));

  events[0] = local.wantNewToDo_Event;
  events[1] = (HANDLE)local.timerThread; /* also notice if the thread dies */
  /* Consider if this should be MsgWaitForMultipleObjects */
  rc = WaitForMultipleObjects(2, events, FALSE /* wait for either to be signalled */, INFINITE);

#if DEBUG_TIMEOUT
    if (rc == WAIT_OBJECT_0)
      {
        dbg_fprintf((stderr, "waitForTimerThread(%s) WaitForMultipleObjects has cleared wantNewToDo_Event\n", (reset ? "RESET" : "!RESET")));
      }
#endif /* DEBUG_TIMEOUT */

  if (rc != WAIT_OBJECT_0)    /* WAIT_OBJECT_0 if wantNewToDo_Event was signalled */
    {
      if (rc == WAIT_OBJECT_0+1) /* timerThread exited. We're toast */
        {
          dwError = ERROR_INVALID_THREAD_ID; /* hmm */
          goto barf;
        }
      else if (rc == WAIT_FAILED)
        {
          dwError = GetLastError();
          goto barf;
        }
      else                        /* unexpected. */
        {
          SP_ASSERT(0);
          dwError = ERROR_INVALID_THREAD_ID; /* whatever */
          goto barf;
        }
    }
  /* wantNewToDo_Event has been reset by WaitForMultipleObjects, unset it if needed */
  if (!reset)
    {
      dbg_fprintf((stderr, "waitForTimerThread(%s) setting wantNewToDo_Event\n", (reset ? "RESET" : "!RESET")));

      if (!SetEvent(local.wantNewToDo_Event))
        {
          dwError = GetLastError();
          goto barf;
        }
    }
  else
    {
      ;
        dbg_fprintf((stderr, "waitForTimerThread(%s) NOT setting wantNewToDo_Event\n", (reset ? "RESET" : "!RESET")));
    }
  return 0;                     /* success */

 barf:
#if DEBUG_TIMEOUT
  DisplayErrorText(dwError, 0, "alarm_clock_on");
#endif /* DEBUG_TIMEOUT */
  SP_ASSERT(dwError != 0);
  return dwError;
}

static DWORD reSynchTimerThread(void)
{
  DWORD dwError;          /* GetLastError() */

  dwError = waitForTimerThread(TRUE);
  if (dwError != 0)
    {
      goto barf;
    }
  /* timer thread is either waiting for a new command or it is processing a time out */
  local.what_to_do = WHAT_TO_DO_RESTART;

  dbg_fprintf((stderr, "reSynchTimerThread setting newToDoAvailable_Event with what_to_do==WHAT_TO_DO_RESTART)\n"));

  if (!SetEvent(local.newToDoAvailable_Event))
    {
      dwError = GetLastError();
      goto barf;
    }
  dwError = waitForTimerThread(FALSE); /* leave the Event set */
  if (dwError != 0)
    {
      goto barf;
    }
  /* timer thread has received the WHAT_TO_DO_RESTART and is now
     waiting without time-limit on the next to-do command. */

  return 0;                     /* success */

 barf:

#if DEBUG_TIMEOUT
  DisplayErrorText(dwError, 0, "reSynchTimerThread");
#endif /* DEBUG_TIMEOUT */
  SP_ASSERT(dwError != 0);
  return dwError;
}

/* [PM] 3.8.7 return 0 on success, non-zero GetLastError()-value on error. */
static int alarm_clock_on(
     SP_integer time_ /* time is in milliseconds */
     )
{
  DWORD dwError;                /* GetLastError() */
  DWORD time = (DWORD)time_;
  dbg_fprintf((stderr, "alarm_clock_on(%" SPRIdINTEGER "ms)\n", (SP_integer) time_));
  SP_ASSERT(time == time_);

  if (!local.have_wantNewToDo_Event)
    {
      HANDLE event;
      event = CreateEvent(NULL,     /* no security */
                          FALSE,    /* auto-reset */
                          FALSE,    /* initially unsignalled */
                          NULL);    /* no name */
      if (event == NULL)
        {
          dwError = GetLastError();
          goto barf;
        }
      local.wantNewToDo_Event = event;
      local.have_wantNewToDo_Event = 1;
    }

  if (!local.have_newToDoAvailable_Event)
    {
      HANDLE event;
      event = CreateEvent(NULL,     /* no security */
                          FALSE,    /* auto-reset */
                          FALSE,    /* initially unsignalled */
                          NULL);    /* no name */
      if (event == NULL)
        {
          dwError = GetLastError();
          goto barf;
        }
      local.newToDoAvailable_Event = event;
      local.have_newToDoAvailable_Event = 1;
    }

  if (!local.have_timerThread)
    {
#if !WINCE
      SP_uinteger thread;
#else  /* WINCE */
      HANDLE thread;
#endif  /* WINCE */

      unsigned threadID;        /* ignored */

      /* timerThreadFunc will start immediately but will hang at the
         unsignalled newToDoAvailable_Event */
      /* We cannot use _beginthread since we want to wait on the thread handle */
#if !WINCE
#if SICSTUS_TODO
#error "We should perhaps use CreateThread directly, instead (xref the WINCE code below)?"
#endif  /* SICSTUS_TODO */

      thread = _beginthreadex(NULL, /* no security */
                              0, /* default stack size */
                              timerThreadFunc,
                              NULL, /* arg ptr */
                              0, /* init-flag, run directly */
                              &threadID
                              );

      if (thread == 0)
        {
          dwError = _doserrno;
          goto barf;
        }
#else  /* WINCE */
      thread = CreateThread(NULL, /* no security */
                              0, /* default stack size */
                              timerThreadFunc,
                              NULL, /* arg ptr */
                              0, /* init-flag, run directly */
                              &threadID
                              );

      if (thread == 0)
        {
          dwError = GetLastError();
#if WINCE
#error "WINCE FIXME"
          error_msg(L"CreateThread (timerThreadFunc) failed (0x%" SPRIxINTEGER ")", (SP_uinteger)dwError);
#endif/* WINCE */
          goto barf;
        }
#endif /* WINCE */
      #if RAISE_TIMER_THREAD_PRIORITY
      {
        dbg_fprintf((stderr, "raising timer thread priority to THREAD_PRIORITY_ABOVE_NORMAL\n"));
        if (!SetThreadPriority((HANDLE)thread, THREAD_PRIORITY_ABOVE_NORMAL))
          {
            ;
            dbg_fprintf((stderr, "ERROR: SetThreadPriority(), GLE==0x%" SPRIxINTEGER "\n", (SP_uinteger) GetLastError()));
          }
      }
      #endif/* RAISE_TIMER_THREAD_PRIORITY */
      local.timerThread = thread;
      local.have_timerThread = 1;
    }

  /* Here we have timerThreadFunc running in a separate thread,
     waiting on newToDoAvailable_Event (the timerThread already or
     soon having signalled wantNewToDo_Event) */

  dwError = waitForTimerThread(TRUE);
  if (dwError != 0)
    {
      goto barf;
    }

  dbg_fprintf((stderr, "alarm_clock_on(%" SPRIdINTEGER "ms) about to signal timeToSleep\n", (SP_integer) time));

  /* get here if wantNewToDo_Event was signalled, indicating that
     the timer thread is now waiting on newToDoAvailable_Event */
  local.what_to_do = WHAT_TO_DO_SLEEP;
  local.timeToSleep = time;
  #if WIN32_FUDGE_FACTOR          /* [PM] 3.9.2 experiment */
  {
    local.timeToSleep = max(time, 10);

    #if DEBUG_TIMEOUT
    if (time != local.timeToSleep)
      { dbg_fprintf((stderr, "alarm_clock_on fudged timeToSleep ==%" SPRIdINTEGER "ms\n", (SP_integer) local.timeToSleep)); }
    #endif/* DEBUG_TIMEOUT */
  }
  #endif /* WIN32_FUDGE_FACTOR */

  local.lastTime = myGetTickCount();
  local.alarm_clock_is_on = 1;

  dbg_fprintf((stderr, "alarm_clock_on sets newToDoAvailable_Event\n"));
  
  if (!SetEvent(local.newToDoAvailable_Event)) /* tell timerThreadFunc that what_to_do is set-up */
    {
      dwError = GetLastError();
      goto barf;
    }
  
  return 0;
  
 barf:

  SP_ASSERT(dwError != 0);
#if DEBUG_TIMEOUT
  DisplayErrorText(dwError, 0, "alarm_clock_on");
#endif /* DEBUG_TIMEOUT */

  return dwError;
}

/* [PM] 4.0 return zero on success, non-zero GetLastError()-value on error */
static int alarm_clock_off(int *was_off)
{
  DWORD dwError;                /* GetLastError() */
  int dummy;

  if (was_off==NULL) was_off = &dummy;

  if (!local.have_timerThread)
    {
      *was_off = 1;
      return 0;                 /* not inited, not an error! */
    }

  if ( (dwError = reSynchTimerThread()) != 0)
    {
      goto barf;
    }
  /* Now the timer thread is definitely stopped and waiting for the next what_to_do */

  /* alarm_clock_is_on may be cleared by timer thread up to the return
     from reSynchTimerThread(). This can happen if the timer thread
     was processing a time out. */
  if (!local.alarm_clock_is_on)
    {
      *was_off = 1;
      return 0;
    }
  else
    {
      *was_off = 0;
    }
  {
    SP_integer currentTime = myGetTickCount();
    DWORD remainingTime;
    DWORD elapsedTime;
    
    elapsedTime = (DWORD) (currentTime-local.lastTime);
    if (local.timeToSleep > elapsedTime)
      {
        remainingTime = local.timeToSleep - elapsedTime;
      }
    else
      {
        remainingTime = 0;
      }
    
    local.alarm_clock_is_on = 0;
    return 0;                   /* success */
  }

 barf:
#if DEBUG_TIMEOUT
  DisplayErrorText(dwError, 0, "alarm_clock_off");
#endif /* DEBUG_TIMEOUT */

  local.alarm_clock_is_on = 0;
  return dwError;
}

/* in milliseconds */
static SP_integer get_cpu_now(void)
{
#if 0
  {
#if (SEE_WHAT_THE_INCREMENT_APPEARS_TO_BE && SICSTUS_DBG) && 1
#if !SICSTUS_DBG
#error "Remove the SEE_WHAT_THE_INCREMENT_APPEARS_TO_BE code/PM"
#endif

    {
#if TIME_BEGIN_PERIOD_EXPERIMENT
      MMRESULT tbp_rc = timeBeginPeriod(1); /* turns out this does not affect GetThreadTimes precision */
#endif

      SP_integer start_time = myGetTickCount();
      SP_integer last_time = 0;
      SP_integer tmp;
#if TIME_BEGIN_PERIOD_EXPERIMENT
      dbg_fprintf((stderr, "timeBeginPeriod(1)==%" SPRIdINTEGER " %s\n", (SP_integer)tbp_rc, (tbp_rc==TIMERR_NOERROR ? "TIMERR_NOERROR" : "ERROR")));
#endif

      do {
        while ((tmp = myGetTickCount()) == last_time)
          {
            ;                     /* empty */
          }
        dbg_fprintf((stderr, "%" SPRIdINTEGER "ms, diff==%" SPRIdINTEGER "ms\n", (SP_integer) tmp, (SP_integer) (tmp-last_time)));
        last_time = tmp;
      } while (tmp - start_time < 4000);

#if TIME_BEGIN_PERIOD_EXPERIMENT
      if (tbp_rc == TIMERR_NOERROR) timeEndPeriod(1);
#endif

    }
#else
#endif
  }
#endif  /* 0 */

  {
    SP_integer cpu_now = (SP_integer)myGetTickCount();
    dbg_fprintf((stderr, "cpu_now==%" SPRIdINTEGER "ms\n", (SP_integer)cpu_now));
    return cpu_now ;
  }
}


/*
 * Foreign functions 
 */
/* These should be shared with the UNIX version */

/* [PM] 4.0 for Suite/timeout.pl */
SP_integer SPCDECL to_clocks_per_second(void)
{
  return (SP_integer)((1000000 + (local.timer_resolution_us - 1))/ local.timer_resolution_us);
}

/*------------------------------------------------------------------*/
/* foreign(to_start_timer_a, '$start_timer_a'(+term, +term, -integer)).. */
/*
%% set timer to fire at time min(ContExpires, <<CPU now>>+Limit)
%% Either ContExpires or Limit can be the atom 'off'.

  Return
  0 -- timer started
  1 -- current time is already at or past ContExpires
  <0 -- some other error (will not happen except for bad params. Raise exception here directly).
*/
SP_integer SPCDECL to_start_timer_a(
                      SP_term_ref tContExpires, /* off | Integer>0 absolute time (ms) */
                      SP_term_ref tLimit /* off | Integer>0 time interval (ms) */
                      )
{
  SP_integer cpu_now = get_cpu_now();
  SP_integer timeout = 0;

#if DEBUG_TIMEOUT
  {
    dbg_fprintf((stderr, "to_start_timer_a("));
    if (SP_is_atom(tContExpires))
      {
        dbg_fprintf((stderr, "ContExpires=off, "));
      }
    else
      {
        SP_integer tmp;
        SP_get_integer(tContExpires, &tmp);
        dbg_fprintf((stderr, "ContExpires=%" SPRIdINTEGER "ms, ", (SP_integer) tmp));
      }

    if (SP_is_atom(tLimit))
      {
        dbg_fprintf((stderr, "Limit=off)"));
      }
    else
      {
        SP_integer tmp;
        SP_get_integer(tLimit, &tmp);
        dbg_fprintf((stderr, "Limit=%" SPRIdINTEGER "ms)", (SP_integer) tmp));
      }
    dbg_fprintf((stderr, " cpu_now = %" SPRIdINTEGER "ms\n", (SP_integer) cpu_now));
  }
#endif /* DEBUG_TIMEOUT */

  if (SP_is_atom(tLimit)) /* 'off' */
    {
      ;                         /* do nothing */
    }
  else
    {
      SP_integer Limit;
      if (!SP_get_integer(tLimit, &Limit)) goto barf;
      timeout = Limit;

      #if PREVENT_IMMEDIATE_TIMEOUT /* [PM] 3.10 always true */
      {
        SP_integer minimum_timeout = WIN32_MINIMUM_TIMEOUT_ms;
        if (timeout < minimum_timeout)
          {
            dbg_fprintf((stderr, "Adjusting original timeout %" SPRIdINTEGER "ms to %" SPRIdINTEGER "ms\n",
                         (SP_integer)timeout, (SP_integer)minimum_timeout));
            timeout = minimum_timeout;
          }
      }
      #endif /* PREVENT_IMMEDIATE_TIMEOUT */

    }
  /* timeout is zero or actual limit */
  
  if (SP_is_atom(tContExpires)) /* 'off' */
    {
      ;                         /* do nothing */
    }
  else
    {
      SP_integer ContExpires;
      SP_integer ContLimit;

      if (!SP_get_integer(tContExpires, &ContExpires)) goto barf;
      if (ContExpires <= cpu_now)
        {
          dbg_fprintf((stderr, "ContExpires == %" SPRIdINTEGER "ms has already happened (cpu_now == %" SPRIdINTEGER "\n)",
                      (SP_integer)ContExpires, (SP_integer)cpu_now));
          return 1;             /* ContExpires has already happened */
        }
      ContLimit = ContExpires-cpu_now;
      
      if (timeout == 0          /* Limit was 'off' */
          || ContLimit < timeout) /* ContExpires expires before cpu_now+Limit */
        {
          timeout = ContLimit;
        }
    }
  
  if (timeout == 0)                   /* ContExpires and Limit are 'off' */
    {
      if (local.alarm_clock_is_on) /* unnecessary optimization */
        {
          int rc;

          rc = alarm_clock_off(NULL);
          if (rc != 0)             /* error */
            {
              /* error */
              timeout_raise_os_error(rc, "$start_timer_a/3", "setitimer");
              return -1;
            }
        }
      dbg_fprintf((stderr, "ContExpires and Limit off\n"));

      return 0;
    }
  /* timeout is how long until timer should trigger */
  local.expiry_abs = cpu_now+timeout; /* This is either ContExpires or (cpu_now+Limit) */
  
  {
    int rc;

    rc = alarm_clock_on(timeout);
    if (rc != 0)
      {
        timeout_raise_os_error(rc, "$start_timer_a/3", "alarm_clock_on");
        return -1;
      }
  }

  return 0;                     /* no error */
 barf:
  dbg_fprintf((stderr, "ERROR: in to_start_timer_a\n"));
  return -1;
}

/* foreign(to_stop_timer_a, '$stop_timer_a'([-term])).
   Return 'off' or the *absolute* time at which the timer was set to expire.
*/
SP_term_ref SPCDECL to_stop_timer_a(void)
{
  SP_term_ref term = SP_new_term_ref();
  
  if (!local.alarm_clock_is_on)
    {
      dbg_fprintf((stderr, "to_stop_timer_a was off\n"));

      SP_put_atom(term, local.atom_off);
    }
  else
    {
      int was_off;
      int rc;
      
      rc = alarm_clock_off(&was_off);

      if (rc != 0) /* error */
        {
          /* It is off if error occurred. Also, the caller will call
             start_timer which will likely raise an error if the problem persists. */
          (void)rc;
          SP_put_atom(term, local.atom_off);
        }
      else if (was_off)
        {
          SP_put_atom(term, local.atom_off);
        }
      else
        {
          dbg_fprintf((stderr, "to_stop_timer_a was %" SPRIdINTEGER "ms\n", (SP_integer) local.expiry_abs));

          SP_put_integer(term, local.expiry_abs);
        }
    }
  return term;
}

/* foreign(to_timer_now, '$timer_now'([-term])).
   Return the current CPU (user) time
 */

SP_term_ref SPCDECL to_timer_now(void)
{
  SP_term_ref term = SP_new_term_ref();
  SP_integer cpu_now = get_cpu_now();

  dbg_fprintf((stderr, "to_timer_now %" SPRIdINTEGER "ms\n", (SP_integer)cpu_now));

  SP_put_integer(term, cpu_now);
  return term;
}

#endif /* SP_WIN32 */

/********************************************************************************/
/* [PM] 3.9b4 (Beginning of) A port of the Windows thread-based code to UNIX. */
#if !LEGACY_TIMOUT
#if !SP_WIN32
#error "this code does not work/PM"
#endif /* !SP_WIN32 */
#endif /* !LEGACY_TIMOUT */
/********************************************************************************/
