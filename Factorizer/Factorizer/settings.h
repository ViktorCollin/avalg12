//
//  setting.h
//  Factorizer
//
//  Created by Viktor Collin on 10/15/12.
//  Copyright (c) 2012 Viktor Collin & Anton Lindström. All rights reserved.
//

#ifndef Factorizer_setting_h
#define Factorizer_setting_h

#define NUMBERS 100
#define MAXNUMBEROFDIGITS 40
#define TIMER_MAX 360000
#define BRENTS 1

#define MIN(a,b) (a>b?b:a)
#define ABS(a) (a<0?-a:a)

#ifdef DEBUG
#define TRACE(str) fprintf(stderr,"%s", str)
#define TRACE_Z(z) gmp_fprintf (stderr,"%Zd", z)
#define TRACE_U(u) gmp_fprintf (stderr,"%u", u)
#define TRACE_N() fprintf(stderr,"\n")
#else
#define TRACE(str)
#define TRACE_Z(z)
#define TRACE_U(ul)
#define TRACE_N()

#endif

#endif
