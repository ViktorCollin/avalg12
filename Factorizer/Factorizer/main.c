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
#include "setting.h"



struct listElement{
	MP_INT factor;
	struct listE * next;
};

struct list{
	struct listElement * first;
	struct listElement * last;
	int size;
};

int main(int argc, const char * argv[]);
void printFactors(struct list list);
void addToList(MP_INT factor, struct list * list);

void addToList(MP_INT factor, struct list * list){
	struct listElement * element = (struct listElement*) malloc(sizeof(struct listElement));
	element->factor = factor;
	element->next = NULL;
	if(list->size == 0){
		list->first = element;
	}
	list->last = element;
	list->size++;
	
}

void printFactors(struct list list){
	int i;
	for(i=0;i<list.size;i++){
		//printf(<#const char *, ...#>)
	}
}


int main(int argc, const char * argv[]){
    if(argc == 1){
		// standard mode
		
		
		
	}
	if(strcmp(argv[1], "interactive") == 0){
		// interactive mode
		fprintf(stderr, "Interactive mode!\n");
		MP_INT number;
		while(1){
			char str[40];
			fgets(str, sizeof(str), stdin);
			mpz_init_set_str(&number,str,10);
			
		}
		
		
	}
    
    return 0;
}

