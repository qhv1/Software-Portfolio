//qhv1
//Quinn Vierick

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "mymalloc.h"


// USE THIS GODDAMN MACRO OKAY
#define PTR_ADD_BYTES(ptr, byte_offs) ((void*)(((char*)(ptr)) + (byte_offs)))

// Don't change or remove these constants.
#define MINIMUM_ALLOCATION  16
#define SIZE_MULTIPLE       8

unsigned int round_up_size(unsigned int data_size) {
	if(data_size == 0)
		return 0;
	else if(data_size < MINIMUM_ALLOCATION)
		return MINIMUM_ALLOCATION;
	else
		return (data_size + (SIZE_MULTIPLE - 1)) & ~(SIZE_MULTIPLE - 1);
}
typedef struct Node
{	
	unsigned int sizeOf;
	int allocated; //1 for allocated, 0 for free
	struct Node* next;
	struct Node* previous;
}Node;

Node* header = NULL;
Node* tail = NULL;

Node* createNode(int size)
{
	Node* newNode = sbrk(sizeof(Node) + size);
	newNode->allocated = 1;
	newNode->sizeOf = size;
	return newNode;
}
Node* findNodeToReuse(unsigned int size)
{
	Node* finderNode = header;
	while(finderNode != NULL)
	{
		if(finderNode->sizeOf >= size && finderNode->allocated == 0)
		{
			return finderNode;
		}
		finderNode = finderNode->next;
	}
	return NULL;
}
Node* split(Node* nodeToBeSplit, unsigned int size)
{
	unsigned int nodeAndDataSize = nodeToBeSplit->sizeOf + sizeof(Node);
	unsigned int originalDataSize = nodeToBeSplit->sizeOf;
	unsigned int difference = nodeAndDataSize - size;
	if(difference <= sizeof(Node))
	{
		nodeToBeSplit->allocated = 1;
		return nodeToBeSplit;
	}
	else
	{
		nodeToBeSplit->allocated = 1;
		nodeToBeSplit->sizeOf = size;
		nodeAndDataSize = nodeToBeSplit->sizeOf + sizeof(Node);
		Node* newNode = PTR_ADD_BYTES(nodeToBeSplit, nodeAndDataSize);
		newNode->sizeOf = originalDataSize - nodeToBeSplit->sizeOf - sizeof(Node);
		Node* newNodeNext = nodeToBeSplit->next;
		nodeToBeSplit->next = newNode;
		newNode->next = newNodeNext;
		newNode->previous = nodeToBeSplit;
		newNode->allocated = 0;
		return nodeToBeSplit;
	}
	return NULL;
}
int checkCoalesce(Node* theNode)
{
	if(theNode == NULL)
	{
		return 0;
	}
	if(theNode->allocated == 1)
	{
		return 0;
	}
	Node* theNextNode = theNode->next;
	if(theNextNode == NULL)
	{
		return 0;
	}
	if(theNextNode->allocated == 1)
	{
		return 0;
	}
	return 1;
}
Node* coalesce(Node* theNode)
{
	Node* theNextNode = theNode->next;
	theNode->sizeOf = (theNode->sizeOf + theNextNode->sizeOf) + sizeof(Node);

	if(theNextNode->next == NULL)
	{
		tail = theNode;
		return tail;
	}
	theNextNode = theNextNode->next;
	theNode->next = theNextNode;
	theNextNode->previous = theNode;
	return theNode;
}

Node* listAppend(Node* theNode)
{
	if(header == tail)
	{
		tail = theNode;
		header->next = tail;
		tail->previous = header;
		return tail;
	}
	Node* oldTail = tail;
	tail = theNode;
	oldTail->next = tail;
	tail->previous = oldTail;
	return tail;
}

Node* listPrepend(Node* theNode)
{
	if(header == tail)
	{
		header = theNode;
		header->next = tail;
		tail->previous = header;
		return header;
	}
	Node* oldHeader = header;
	header = theNode;
	oldHeader->previous = header;
	header->next = oldHeader;
	return header;
}

void setFree(Node* theNode)
{
	theNode->allocated = 0;
}
int hasNext(Node* theNode)
{
	if(theNode->next != NULL)
	{
		return 1;
	}
	return 0;
}

void* my_malloc(unsigned int size) {
	if(size == 0)
		return NULL;

	size = round_up_size(size);
	Node* newNode;
	if(header == NULL && tail == NULL)
	{
		newNode = createNode(size);
		header = newNode;
		tail = newNode;
		return PTR_ADD_BYTES(newNode, sizeof(Node));
	}
	newNode = findNodeToReuse(size);
	if(newNode != NULL)
	{
		split(newNode, size);
		return PTR_ADD_BYTES(newNode, sizeof(Node));
	}
	else
	{
		newNode = createNode(size);
		listAppend(newNode);
	}
	return PTR_ADD_BYTES(newNode, (sizeof(Node)));
}

void my_free(void* ptr) {
	if(ptr == NULL)
		return;
	Node* freeNode = PTR_ADD_BYTES(ptr, -(sizeof(Node)));
	setFree(freeNode);
	// if previous is free, freeNode = coalesce(freeNode->previous)
	// if next is free, freeNode = coalesce(freeNode)
	if(checkCoalesce(freeNode) == 1)
	{
		freeNode = coalesce(freeNode);
	}
	if(checkCoalesce(freeNode->previous) == 1)
	{
		freeNode = coalesce(freeNode->previous);
	}
	
	if(header == tail)
	{
		sbrk(-(freeNode->sizeOf + sizeof(Node)));
		header = NULL;
		tail = NULL;
		return;
	}
	if(freeNode == tail)
	{
		if(tail->previous != NULL)
		{
			tail = tail->previous;
			if(tail == header)
			{
				tail->previous = NULL;
				header->next = NULL;
			}
		}
		sbrk(-(freeNode->sizeOf + sizeof(Node)));
		Node* thePreviousNode = freeNode->previous;
		thePreviousNode->next = NULL;
		freeNode = NULL;

		return;
	}
	return;
	}
   