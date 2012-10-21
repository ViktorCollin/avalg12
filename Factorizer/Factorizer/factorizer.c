#include <stdio.h>
#include <gmp.h>
#include "list.h"
#include "factorizer.h"
#include "settings.h"
#include "primes.h"

int TIMER;

void reset_timer() {
	TIMER = TIMER_MAX;
}

void f(mpz_t x, mpz_t number, unsigned long a) {
	mpz_mul(x, x, x);
	mpz_add_ui(x, x, a);
	mpz_mod(x, x, number);
}

void find_perfect_power(mpz_t base, unsigned long * exp, mpz_t number) {
	//gmp_fprintf(stderr,"find perfect power for %Zd", number);
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

unsigned int trail_division(list * factors, mpz_t number, int count) {
	mpz_t q, r, div;
	mpz_init(q);
	mpz_init(r);
	mpz_init(div);

	unsigned int found = 0;	

	int i = 0;
	while (mpz_cmp_ui(number, 1) != 0) {
		mpz_tdiv_qr_ui(q, r, number, primes[i]);

		if (mpz_cmp_ui(r, 0) == 0) {
			mpz_set(number, q);
			found++;

			mpz_set_ui(div, primes[i]);
			for (int j = 0; j < count; j++)
				appendToList(div, factors);
			gmp_fprintf(stderr, "trail factor: %Zd\n", div);
		} else if (i < 999) {
			i++;
		} else {
			break;
		}
	}

	mpz_clear(q);
	mpz_clear(r);

	return found;
}

void factorize(list * factors, mpz_t number, int count) {
	if (--TIMER < 0) {
		factors->failed = 1;
		return;
	}

	fprintf(stderr, "TIMER: %d\n", TIMER);

	if (factors->failed) {
		TRACE("list is marked as failed.");
		return;
	}

	//gmp_fprintf(stderr,"factorize(%Zd, %d)\n", number, count);
	if (mpz_cmp_ui(number, 1) == 0) {
		appendToList(number, factors);
	}
		
	mpz_t d;
	mpz_init(d);
				
	while (mpz_cmp_ui(number, 1)) {
		if (mpz_probab_prime_p(number, 10)) {
			for (int i = 0; i < count; i++) {
				//gmp_fprintf(stderr,"Prime factor: %Zd\n", number);
				appendToList(number, factors);
			}

			break;
		} else if (mpz_perfect_power_p(number)) {
			//gmp_fprintf(stderr,"Perfect power: %Zd\n", number);
			unsigned long exp = 0;
			find_perfect_power(number, &exp, number);
			//gmp_fprintf(stderr,"%Zd exp: %lu, alltså alla faktorer ska räknas exp gånger nu!\n", number, exp);
			count *= exp;

		} else if (trail_division(factors, number, count)) {
			continue;
		} else if (pollardsRoh(d, number, 1)) {
			gmp_fprintf(stderr, " -> %Zd\n", d);
			mpz_div(number, number, d);
			factorize(factors, d, count);
		} else {
			// Funkar detta? :-) Kanske!
			gmp_fprintf(stderr, "Pollards Roh misslyckades med %Zd\n", number);
			factors->failed = 1;
			break;
		}
	}

    mpz_clear(d);
}

int pollardsRoh(mpz_t d, mpz_t number, unsigned long a) {
	if (a > 3)
		return 0;


	gmp_fprintf(stderr,"Pollards roh: %Zd\n", number);
	if (mpz_even_p(number)) {
		mpz_set_ui(d, 2);
		return 1;
	} else {
		mpz_t x, y;
		mpz_init_set_ui(x, 1);
		mpz_init_set_ui(y, 1);
		mpz_set_ui(d, 1);

		while (!mpz_cmp_ui(d, 1)) {
			f(x, number, a);
			f(y, number, a);
			f(y, number, a);

			mpz_sub(d, x, y);
			mpz_abs(d, d);
			mpz_gcd(d, d, number);
	
			if (--TIMER < 0)
				return 0;
		}
		
		mpz_clear(x);
		mpz_clear(y);

		if (mpz_cmp(d, number) == 0) {
			return pollardsRoh(d, number, a + 1);
		}
       
		return 1;
	}
}
