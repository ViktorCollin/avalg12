#include <stdio.h>
#include "/usr/local/include/gmp.h"
#include "list.h"
#include "factorizer.h"
#include "settings.h"

void f(mpz_t x, mpz_t number) {
	mpz_pow_ui(x, x, 2);
	mpz_add_ui(x, x, 1);
	mpz_mod(x, x, number);
}

void find_perfect_power(mpz_t base, unsigned long * exp, mpz_t number) {
	gmp_printf("find perfect power for %Zd", number);
	// max_exp = log2(number)
	unsigned long max_exp = mpz_sizeinbase(number, 2);

	mpz_t temp;
	mpz_init(temp);

	// Räcker det att kolla alla primtal n?
	for (unsigned long n = 2; n <= max_exp; n++) {
		if (mpz_root(temp, number, n)) {
			*exp = n;
			break;
		}
	}

	mpz_set(base, temp);
}


void factorize(list * factors, mpz_t number, int count) {
	gmp_printf("factorize(%Zd, %d)\n", number, count);
	mpz_t d;
	mpz_init(d);

	while (mpz_cmp_ui(number, 1) != 0) {
		if (mpz_probab_prime_p(number, 10)) {
			for (int i = 0; i < count; i++) {
				gmp_printf("Prime factor: %Zd\n", number);
				appendToList(&number, factors);
			}

			break;
		} else if (mpz_perfect_power_p(number)) {
			gmp_printf("Perfect power: %Zd\n", number);
			unsigned long exp = 0;
			find_perfect_power(number, &exp, number);
			gmp_printf("%Zd exp: %lu, alltså alla faktorer ska räknas exp gånger nu!\n", number, exp);
			count++; // TODO: Detta är fel :)
		} else {
			pollardsRoh(d, number);
			gmp_printf(" ->  %Zd | %Zd\n", number, d);
			mpz_div(number, number, d);
			factorize(factors, d, count);
		}
	}
}


// TODO: Flytta ut perfect power och prime koll till tidigare!
void pollardsRoh(mpz_t d, mpz_t number) {
	gmp_printf("Pollards roh: %Zd\n", number);
	if (mpz_even_p(number)) {
		mpz_set_ui(d, 2);
	} else {
		mpz_t x, y;
		mpz_init_set_ui(x, 1);
		mpz_init_set_ui(y, 1);
		mpz_set_ui(d, 1);

		while (!(mpz_cmp_ui(d, 1) != 0 && mpz_cmp(d, number) != 0)) {
			f(x, number);
			f(y, number);
			f(y, number);

			mpz_sub(d, x, y);
			mpz_gcd(d, d, number);
		}

		mpz_clears(x, y, NULL);
	}
}

