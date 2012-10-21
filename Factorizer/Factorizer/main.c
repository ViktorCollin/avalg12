//
//  main.c
//  Factorizer
//
//  Created by Viktor Collin on 10/15/12.
//  Copyright (c) 2012 Viktor Collin & Anton Lindström. All rights reserved.
//

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#ifdef DEBUG
#include "/usr/local/include/gmp.h"
#else
#include <gmp.h>
#endif
#include "list.h"
#include "factorizer.h"
#include "settings.h"
#include "main.h"

void printFactors(list * factors){
	if(factors->failed){
		printf("fail\n");
	}else{
		node * currElem = factors->first;
		
		while(currElem != NULL){
			for (int i = 0; i < currElem->count; i++)
				gmp_printf("%Zd\n", currElem->factor);
			currElem = currElem->next;
		}
	}
	printf("\n");
	fflush(stdout);
}

int main(int argc, const char * argv[]){
    if(argc == 1){
		// standard mode
		mpz_t numbers[NUMBERS];
		list * calculatedFactors[NUMBERS];
		int i;

		for (i = 0; i < NUMBERS; i++){
			mpz_init(numbers[i]);
			mpz_inp_str(numbers[i], stdin, 10);
		}

		for (i = 0; i < NUMBERS; i++) {
	
			/*
			if (i > 60) {
				printf("fail\n\n");
				continue;
			}
			*/
	
			reset_timer();
			list* factors = createList();
			factorize(factors, numbers[i],1);
			
			calculatedFactors[i] = factors;
			printFactors(calculatedFactors[i]);
		}
	/*	
		for (i = 0; i < NUMBERS; i++){
			printFactors(calculatedFactors[i]);
		}
		
		*/
		return 0;
		
	} else if(strcmp(argv[1], "interactive") == 0){
		// interactive mode
		fprintf(stderr, "Interactive mode!\n");
		
		mpz_t number;
		mpz_init(number);
		list * factors = NULL;

		while (1) {
			mpz_inp_str(number, stdin, 10);
			reset_timer();
			factors = createList();
			factorize(factors, number, 1);

			TRACE("PRINTING FACTORS:\n");
			printFactors(factors);
		}

		return 0;
	}
    
    return 1;
}
