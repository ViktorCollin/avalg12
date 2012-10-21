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
#define TIMER_MAX 210000

#ifdef DEBUG
#define TRACE(str) fprintf(stderr,"%s\n", str)
#define TRACE_Z(z) gmp_printf ("%Zd\n", z)
#define TRACE_sZ(s,z) gmp_printf ("%s: %Zd\n", s, z)
#else
#define TRACE(str)
#define TRACE_Z(z)
#define TRACE_sZ(s,z)
#endif

#endif
