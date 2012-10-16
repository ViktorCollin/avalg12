//
//  list.h
//  Factorizer
//
//  Created by Viktor Collin on 10/16/12.
//  Copyright (c) 2012 Viktor Collin & Anton Lindström. All rights reserved.
//

#ifndef Factorizer_list_h
#define Factorizer_list_h

#include "/usr/local/include/gmp.h"

typedef struct node{
	MP_INT factor;
	struct node * next;
}node;

typedef struct list{
	node * first;
	node * last;
	int size;
}list;

list* createList(void);
void appendToList(MP_INT factor, list * list);
void appendListToList(list * smallList, list * resultList);

#endif
