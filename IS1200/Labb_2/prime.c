/*
 prime.c
 By David Broman.
 Last modified: 2015-09-15
 This file is in the public domain.
*/


#include <stdio.h>

/*
	Retunerar 1 för primtal
	Retunerar 0 för icke primtal
*/
int is_prime(int n){
	int prime = 1;						/* Utgår ifrån att alla tal är primtal */
	
	for(int i = 2; i < n; i++) 			/* Loopar igenom alla tal från 2 till talet innan n */
	{
		if( n % i == 0)					/* Ger talet rest, =  ej primtal */
		{	prime = 0;					/* Ändrar prime till 0 = "ej primtal" */
		}
	}	
	
	return prime;
}

int main(void){
  printf("%d\n", is_prime(11));  // 11 is a prime.      Should print 1.
  printf("%d\n", is_prime(383)); // 383 is a prime.     Should print 1.
  printf("%d\n", is_prime(987)); // 987 is not a prime. Should print 0.
}
