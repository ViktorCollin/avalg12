#include <stdio.h>
#include <gmp.h>

#ifdef DEBUG
#define TRACE(str) printf("%s\n", str)
#else
#define TRACE(str)
#endif

void f(mpz_t x, mpz_t number) {
	mpz_pow_ui(x, x, 2);
	mpz_add_ui(x, x, 1);
	mpz_mod(x, x, number);
}

void find_perfect_power(mpz_t base, unsigned long * exp, mpz_t number) {
	// max_exp = log2(number)
	unsigned long max_exp = mpz_sizeinbase(number, 2);

	// Räcker det att kolla alla primtal n?
	for (unsigned long n = 2; n <= max_exp; n++) {
		if (mpz_root(base, number, n)) {
			*exp = n;
			break;
		}
	}
}


// TODO: Flytta ut perfect power och prime koll till tidigare!
void pollardsRoh(mpz_t number, mpz_t d) {
	mpz_init(d);

	if (mpz_even_p(number)) {
		TRACE("Even number");
		mpz_set_ui(d, 2);
	} else if (mpz_probab_prime_p(number, 7)) {
		TRACE("Is prime!");
		mpz_set(d, number);
	} else if (mpz_perfect_power_p(number)) {
		TRACE("Perfect power not implemented!");
		mpz_set(d, number);
	} else {
		mpz_t x, y;
		mpz_init_set_ui(x, 1);
		mpz_init_set_ui(y, 1);
		mpz_set_ui(d, 1);

		while (mpz_cmp_ui(d, 1) == 0) {
			f(x, number);
			f(y, number);
			f(y, number);

			mpz_sub(d, x, y);
			mpz_gcd(d, d, number);
		}

		mpz_clears(x, y, NULL);
	}
}
/*
int main(int argc, char *argv[])
{
	mpz_t a, b;                 

	if (argc<2)
	{					
		printf("Please supply two numbers to add.\n");
		return 1;
	}

	mpz_init_set_str (a, argv[1], 10);

	printf("a: %s\n", mpz_get_str(NULL, 10, a));

	mpz_t base;

	mpz_init(base);
	unsigned long exp = 0;

	find_perfect_power(base, &exp, a);

	printf("PerfectPower: %s ^ %lu\n", mpz_get_str(NULL, 10, base), exp);

	pollardsRoh(a, b);
	printf("Factor: %s\n", mpz_get_str(NULL, 10, b));

	return 0;
}
*/
