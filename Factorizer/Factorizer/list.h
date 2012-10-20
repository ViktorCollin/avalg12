//
//  list.h
//  Factorizer
//
//  Created by Viktor Collin on 10/16/12.
//  Copyright (c) 2012 Viktor Collin & Anton Lindström. All rights reserved.
//

#ifndef Factorizer_list_h
#define Factorizer_list_h

typedef struct node {
	mpz_t factor;
	struct node * next;
} node;

typedef struct list {
	node * first;
	node * last;
	int size;
} list;


list * createList(void);
int appendToList(mpz_t factor, list * list);
int appendListToList(list * smallList, list * resultList);
int clearList(list * listContainingElement);
int removeElementFromList(list* list, int index);

#endif
