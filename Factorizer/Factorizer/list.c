//
//  list.c
//  Factorizer
//
//  Created by Viktor Collin on 10/16/12.
//  Copyright (c) 2012 Viktor Collin & Anton Lindström. All rights reserved.
//

#include <stdlib.h>
#include <stdio.h>
#include <gmp.h>
#include "list.h"
#include "settings.h"

list * createList(void){
	list * newList = (list *)(malloc(sizeof(list)));
	newList->first = NULL;
	newList->last = NULL;
	newList->size = 0;
	TRACE("created list");
	return newList;
}

void appendToList(mpz_t * factor, list * list){
	node * element = (node*) malloc(sizeof(node));
	element->factor = factor;
	element->next = NULL;

	if (list->first == NULL) {
		list->first = element;
	} else {
		list->last->next = element;
	}

	list->last = element;
	list->size++;
}

void appendListToList(list * smallList, list * resultList){
	if (resultList->first == NULL) {
		resultList = smallList;
	} else {
		resultList->last->next = smallList->first;
		resultList->size += smallList->size;
		resultList->last = smallList->last;
	}
}
