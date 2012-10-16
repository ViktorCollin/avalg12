#ifndef FACTORIZER_H
#define FACTORIZER_H

#ifdef DEBUG
#define TRACE(str) printf("%s\n", str)
#define TRACE_Z(z) gmp_printf ("%Zd\n", z)
#else
#define TRACE(str)
#define TRACE_Z(z)
#endif

void f(mpz_t x, mpz_t number);
void find_perfect_power(mpz_t base, unsigned long * exp, mpz_t number);
void pollardsRoh(mpz_t number, mpz_t d);

#endif
