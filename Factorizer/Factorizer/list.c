//
//  list.c
//  Factorizer
//
//  Created by Viktor Collin on 10/16/12.
//  Copyright (c) 2012 Viktor Collin & Anton Lindström. All rights reserved.
//

#include <stdlib.h>
#include <stdio.h>
#ifdef DEBUG
#include "/usr/local/include/gmp.h"
#else
#include <gmp.h>
#endif
#include "list.h"
#include "settings.h"

list * createList(void){
	list * newList = (list *) malloc(sizeof(list));
	newList->first = NULL;
	newList->last = NULL;
	newList->size = 0;
	newList->failed = 0;
	return newList;
}

int appendToList(mpz_t factor, int count, list * list){
	node * element = (node*) malloc(sizeof(node));
	mpz_init_set(element->factor, factor);
	element->count = count;
	element->next = NULL;

	if (list->first == NULL) {
		list->first = element;
		list->last  = element;
	} else {
		list->last->next = element;
		list->last = element;
	}

	list->size++;
    
    return 1;
}

int appendListToList(list * smallList, list * resultList){
    int prevSize = resultList->size;
	if (resultList->first == NULL) {
		resultList = smallList;
	} else {
		resultList->last->next = smallList->first;
		resultList->size += smallList->size;
		resultList->last = smallList->last;
	}
    return (resultList->size - prevSize);
}

int clearList(list* listContainingElement){
    int i;
    int count = 0;
    for(i=0;i<listContainingElement->size;i++){
        count += removeElementFromList(listContainingElement, 0);
    }
    return count;
}

int removeElementFromList(list* list, int index){
    if(index<list->size){
        int i;
        node* currNode = list->first;
        if(index == 0){
            list->first = currNode->next;
            list->size--;
            free(currNode);
            return 1;
        }else{
            node* prevNode = list->first;
            currNode = currNode->next;
            for(i=1;i<index;i++){
                prevNode = currNode;
                currNode = currNode->next;
            }
            prevNode->next = currNode->next;
            list->size--;
            free(currNode);
            return 1;
        }
        if(index==(list->size-1)){
            list->last = NULL;
        }
    }
    TRACE("You have tried to remove a element at a index outside of list");
    return 0;
}
