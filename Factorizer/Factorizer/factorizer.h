#ifndef FACTORIZER_H
#define FACTORIZER_H

void f(mpz_t x, mpz_t number);
void find_perfect_power(mpz_t base, unsigned long * exp, mpz_t number);
void pollardsRoh(mpz_t number, mpz_t d);
void factorize(list * factors, mpz_t number, int count);

#endif
