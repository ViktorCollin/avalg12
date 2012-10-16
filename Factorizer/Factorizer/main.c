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
#include "/usr/local/include/gmp.h"
#include "settings.h"



typedef struct node{
	MP_INT factor;
	struct node * next;
}node;

typedef struct list{
	node * first;
	node * last;
	int size;
}list;

int main(int argc, const char * argv[]);
void printFactors(list * factors);
void appendToList(MP_INT factor, list * list);
void appendListToList(list * smallList, list * resultList);
list* factorize(MP_INT);

list* createList(void){
	list * newList = (list *)(malloc(sizeof(list)));
	newList->first = NULL;
	newList->last = NULL;
	newList->size = 0;
	if(DEBUGLEVEL > 1){
		fprintf(stderr, "created list\n");
	}
	return newList;
}

void appendToList(MP_INT factor,  list * list){
	node * element = (node*) malloc(sizeof(node));
	element->factor = factor;
	element->next = NULL;
	if(list->first == NULL){
		list->first = element;
	}else{
		list->last->next = element;
	}
	list->last = element;
	list->size++;
	if(DEBUGLEVEL > 1){
		char str[MAXNUMBEROFDIGITS];
		mpz_get_str(str,10,&factor);
		fprintf(stderr, "Added %s to list\n",str);
	}
	
}
void appendListToList(list * smallList, list * resultList){
	if(resultList->first == NULL){
		resultList = smallList;
	}else{
		resultList->last->next = smallList->first;
		resultList->size += smallList->size;
		resultList->last = smallList->last;
	}
	
}

list* factorize(MP_INT number){
	list * factors = createList();
	appendToList(number, factors);
	if(DEBUGLEVEL > 1){
		char str[MAXNUMBEROFDIGITS];
		mpz_get_str(str,10,&number);
		fprintf(stderr, "Factorized %s\n",str);
	}
	return factors;
	
}

void printFactors(list * factors){
	if(factors->first == NULL){
		printf("fail\n");
	}else{
		node * currElem = factors->first;
		while(currElem != NULL){
			mpz_out_str(stdout,10,&(currElem->factor));
			printf("\n");
			currElem = currElem->next;
		}
	}
	printf("\n");
	fflush(stdout);
	
}


int main(int argc, const char * argv[]){
    if(argc == 1){
		// standard mode
		MP_INT numbers[NUMBERS];
		list* calculatedFactors[NUMBERS];
		int i;
		for(i=0;i<NUMBERS;i++){
			mpz_inp_str(&numbers[i],stdin,10);
		}
		for(i=0;i<NUMBERS;i++){
			calculatedFactors[i] = factorize(numbers[i]);
		}
		
		for(i=0;i<NUMBERS;i++){
			printFactors(calculatedFactors[i]);
		}
		
		return 0;
		
		
	} else if(strcmp(argv[1], "interactive") == 0){
		// interactive mode
		fprintf(stderr, "Interactive mode!\n");
		MP_INT number;
		mpz_inits(&number);
		list* factors;
		while(1){
			mpz_inp_str(&number,stdin,10);
			factors = factorize(number);
			printFactors(factors);
			
			
			
		}
		return 0;
		
	}
    
    return 1;
}

