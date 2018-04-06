/*
 print-prime.c
 By David Broman.
 Last modified: 2015-09-15
 This file is in the public domain.
*/


#include <stdio.h>
#include <stdlib.h>

#define COLUMNS 6
/* Räknare for antal kolumner */
int counter = 0;						

int is_prime(int n){
	
	/* Utgår ifrån att alla tal är primtal */
	int prime = 1;						
	
	/* Loopar igenom alla tal från 2 till talet innan n */
	for(int i = 2; i < n; i++) 			
	{
		/* Ger talet rest, =  ej primtal */
		if( n % i == 0)					
			/* Ändrar prime till 0 = "ej primtal" */
			prime = 0;					
	}	
	
	return prime;
}

/* Metod som skriver ut talet n, i en tabell med COLUMNS kolumner */
void print_number(int n){				
	
	//Håller reda på antalet kolumner
	if( counter == COLUMNS)
	{
		printf("\n");
		counter = 1;
	}
	else
	{
		counter++;
	}
	
									/* Skriver ut talet */
	printf("%10d", n);
	
}


void print_primes(int n)
{
  // Should print out all prime numbers less than 'n'
  // with the following formatting. Note that
  // the number of columns is stated in the define
  // COLUMNS

  /*
  Loopar igenom alla tal från 2 till n
  Kontrollerar om det är primtal
  Skriver ut primtalen
  */
  for(int i = 2; i <= n; i++) 
  {
  	  if( is_prime(i) == 1)
  	  {
  	  	print_number(i);
  	  }	  
  }
  
  
  /*
  printf("%10d ", 2);
  printf("%10d ", 3);
  printf("%10d ", 5);
  printf("%10d ", 7);
  printf("%10d ", 11);
  printf("%10d ", 13);
  printf("\n");
  printf("%10d ", 17);
  printf("%10d ", 19);
  */
  printf("\n");
}

// 'argc' contains the number of program arguments, and
// 'argv' is an array of char pointers, where each
// char pointer points to a null-terminated string.
int main(int argc, char *argv[]){
  if(argc == 2)
    print_primes(atoi(argv[1]));
  else
    printf("Please state an interger number.\n");
  return 0;
}

 
