#ifndef FACTORIZER_H
#define FACTORIZER_H

void f(mpz_t x, mpz_t number, unsigned long a);
void find_perfect_power(mpz_t base, unsigned long * exp, mpz_t number);
int pollardsRoh(mpz_t number, mpz_t d, unsigned long a);
int brents(mpz_t number, mpz_t d, unsigned long a);
void factorize(list * factors, mpz_t number, int count);
void reset_timer();


#endif
