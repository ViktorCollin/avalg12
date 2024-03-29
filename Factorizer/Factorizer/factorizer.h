#ifndef FACTORIZER_H
#define FACTORIZER_H

void f(mpz_t x, mpz_t number, unsigned long a);
void find_perfect_power(mpz_t base, unsigned long * exp, mpz_t number);
int pollardsRoh(mpz_t number, mpz_t d, unsigned long a);
int brents(mpz_t number, mpz_t d, unsigned long a, mpz_t y);
int fermat(mpz_t d, mpz_t number);
void factorize(list * factors, mpz_t number, int count, mpz_t y);
void reset_timer();


#endif
