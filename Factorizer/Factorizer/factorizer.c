#include <stdio.h>
#ifdef DEBUG
#include "/usr/local/include/gmp.h"
#else
#include <gmp.h>
#endif
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
	TRACE("find perfect power for ");TRACE_Z(number);TRACE_N();
    
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

	while (mpz_cmp_ui(number, 1) != 0 && i < NUMBER_OF_PRIMES) {
		// number = q*d + r
		mpz_tdiv_qr_ui(q, r, number, primes[i]);

		if (mpz_cmp_ui(r, 0) == 0) {
			// Vi har hittat en primtalsfaktor!
            found++;
			mpz_set_ui(div, primes[i]);
			appendToList(div, count, factors);
            TRACE("The prime ");TRACE_Z(div);TRACE(" was found to divide ");TRACE_Z(number);TRACE_N();
			mpz_set(number, q);
		} else {
			// Försök med nästa primtal
			i++;
		}
	}

	mpz_clear(div);
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
		appendToList(number, 1, factors);
	}
		
	mpz_t d;
	mpz_init(d);
				
	while (mpz_cmp_ui(number, 1)) {
		if (mpz_probab_prime_p(number, 10)) {
			//gmp_fprintf(stderr,"Prime factor: %Zd\n", number);
            TRACE_Z(number);TRACE(" was found to be a prime!");TRACE_N();
			appendToList(number, count, factors);

			break;
		} else if (mpz_perfect_power_p(number)) {
            TRACE_Z(number);TRACE(" was found to be a perfect power ");
			//gmp_fprintf(stderr,"Perfect power: %Zd\n", number);
			unsigned long exp = 0;
			find_perfect_power(number, &exp, number);
            TRACE("combined of  ");TRACE_Z(number);TRACE("^");TRACE_U(exp);TRACE_N();
			//gmp_fprintf(stderr,"%Zd exp: %lu, alltså alla faktorer ska räknas exp gånger nu!\n", number, exp);
			count *= exp;

		} else if (trail_division(factors, number, count)) {
			continue;
        } else if (BRENTS && brents(d, number, 10)){
            gmp_fprintf(stderr, " -> %Zd\n", d);
			mpz_div(number, number, d);
			factorize(factors, d, count);
		} else if (!BRENTS && pollardsRoh(d, number, 1)) {
			gmp_fprintf(stderr, " -> %Zd\n", d);
			mpz_div(number, number, d);
			factorize(factors, d, count);
		} else {
			// Funkar detta? :-) Kanske!
			gmp_fprintf(stderr, "Fakoriseringen misslyckades med %Zd\n", number);
			factors->failed = 1;
			break;
		}
	}

    mpz_clear(d);
}

int pollardsRoh(mpz_t d, mpz_t number, unsigned long a) {
	gmp_fprintf(stderr,"Pollards roh: %Zd, a:%lu\n", number, a);
	if (mpz_even_p(number)) {
		mpz_set_ui(d, 2);
		return 1;
        
	} else {
		mpz_t x, y;
		mpz_init_set_ui(x, 1);
		mpz_init_set_ui(y, 1);
		mpz_set_ui(d, 1);

		mpz_t z;
		mpz_init_set_ui(z, 1);

		while (!mpz_cmp_ui(d, 1)) {
			for (int i = 0; i < 100; i++) {
                if (--TIMER < 0){
                    return 0;
                }
				f(x, number, a);
				f(y, number, a);
				f(y, number, a);

				mpz_sub(d, x, y);
				mpz_mul(z, z, d);
				mpz_mod(z, z, number);
			}

			mpz_abs(z, z);
			mpz_gcd(d, z, number);
	
		}
		
		mpz_clear(x);
		mpz_clear(y);

		mpz_clear(z);

		if (mpz_cmp(d, number) == 0) {
			return pollardsRoh(d, number, a + 1);
		}
       
		return 1;
	}
}

int brents(mpz_t d, mpz_t number, unsigned long a){
    // translation table
    // d = g
    // a = c kan vara random mellan 1 och number-3
    // m kan lekas med ju större desto snabbare
    // 
    gmp_fprintf(stderr,"Brents : %Zd, a:%ul\n", number, a);
	if (mpz_even_p(number)) {
		mpz_set_ui(d, 2);
		return 1;
        
	} else {
        
        mpz_t q, x, y, ys,tmp;
        int r = 1, m = 100, k;
        mpz_inits(x,ys,tmp);
        mpz_init_set_ui(q,1);
        mpz_init_set_ui(y,1);
        mpz_set_ui(d,1);
        while(mpz_cmp_ui(d, 1) == 0){
            int i;
            
            mpz_set(x,y);
            for(i=0; i<r;i++){
                f(y,number,a);
            }
            k = 0;
            while(k<r && mpz_cmp_ui(d, 1)==0){
                mpz_set(ys,y);
                for(i=0;i<MIN(m,r-k);i++){
                    f(y,number,a);
                    mpz_sub(tmp,x,y);
                    mpz_abs(tmp,tmp);
                    mpz_mul(q,q,tmp);
                    mpz_mod(q,q,number);
                }
                mpz_gcd(d,q,number);
                k += m;
            }
            r = r << 1;
        }
        if(mpz_cmp(d,number)==0){
            while(1){
                f(ys,number,a);
                mpz_sub(tmp,x,y);
                mpz_abs(tmp,tmp);
                mpz_gcd(d,tmp,number);
                if(mpz_cmp_ui(d, 1) != 0){
                    break;
                }
            }
        }
        mpz_clears(q, x, ys,tmp);
        return 1;
    }
}

