//qhv1
//Quinn Vierick

#include <stdio.h>
#include <unistd.h>

#include "mymalloc.h"

int main() {
	// You can use sbrk(0) to get the current position of the break.
	// This is nice for testing cause you can see if the heap is the same size
	// before and after your tests, like here.
	void* heap_at_start = sbrk(0);
	unsigned int currentSize = (unsigned int)sbrk(0) - (unsigned int)heap_at_start;
	printf("Size of heap: %u\n", currentSize);

	void* a = my_malloc(100);

	currentSize = (unsigned int)sbrk(0) - (unsigned int)heap_at_start;
	printf("Size of heap: %u\n", currentSize);

	void* b = my_malloc(100);

	currentSize = (unsigned int)sbrk(0) - (unsigned int)heap_at_start;
	printf("Size of heap: %u\n", currentSize);

	void* c = my_malloc(100);

	currentSize = (unsigned int)sbrk(0) - (unsigned int)heap_at_start;
	printf("Size of heap: %u\n", currentSize);

	void* d = my_malloc(100);

	currentSize = (unsigned int)sbrk(0) - (unsigned int)heap_at_start;
	printf("Size of heap: %u\n", currentSize);

	void* e = my_malloc(100);

	printf("b free\n");
	my_free(b);

	currentSize = (unsigned int)sbrk(0) - (unsigned int)heap_at_start;
	printf("Size of heap: %u\n", currentSize);

	printf("a free\n");
	my_free(a);

	currentSize = (unsigned int)sbrk(0) - (unsigned int)heap_at_start;
	printf("Size of heap: %u\n", currentSize);

	printf("d free\n");
	my_free(d);

	currentSize = (unsigned int)sbrk(0) - (unsigned int)heap_at_start;
	printf("Size of heap: %u\n", currentSize);

	printf("e free\n");
	my_free(e);

	currentSize = (unsigned int)sbrk(0) - (unsigned int)heap_at_start;
	printf("Size of heap: %u\n", currentSize);

	printf("c free\n");
	my_free(c);

	currentSize = (unsigned int)sbrk(0) - (unsigned int)heap_at_start;
	printf("Size of heap: %u\n", currentSize);

	void* heap_at_end = sbrk(0);
	unsigned int heap_size_diff = (unsigned int)(heap_at_end - heap_at_start);

	if(heap_size_diff)
		printf("Hmm, the heap got bigger by %u (0x%X) bytes...\n", heap_size_diff, heap_size_diff);

	// ADD MORE TESTS HERE.

	return 0;
}
