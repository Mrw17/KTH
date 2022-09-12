#include <stdio.h>
#include <stdlib.h>
#include <inttypes.h>
#define COLUMNS 6
/* Räknare for antal kolumner */
int counter = 0;	


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

/*
Metod som skriver ut primtal enligt 
algoritmen Eratosthenes såll(Sieve of Eratoshenes)
*/
void print_sieves(int n){
	//int arrPrime[n];
	
	//uint8_t arrPrime[n];
	char arrPrime[n+1];
	//arrPrime[0] = 20;
	
	printf("Storleken utav ett falt: %d\n", sizeof(arrPrime[0]));
	printf("Storleken utav arrayen: %d\n", sizeof(arrPrime));
   	

   /* 
   	Fyller arrayen med n - 1 tal
   	Ex: arrPrime[25] = 25 osv
   */
   for(int i = 2; i <= n; i++)
   	   arrPrime[i] = 1;
 
   /*
   */
   for(int i = 2; (i*i <= n); i++)
   {
   	   if(arrPrime[i] > 0)
   	   {
   	   	   for(int j = i*i; j <= n; j +=i)
   	   	   {
   	   	   	   arrPrime[j] = 0;
   	   	   	}
   	    }
   }
   	   
   
   /* Går igenom arrayen och skriver ut primtalen */
   for(int i = 2; i <= n; i++)
   	   if(arrPrime[i] > 0)
   	   	   print_number(i);
}



int main(int argc, char *argv[]){
  if(argc == 2)
  {
    print_sieves(atoi(argv[1]));
    printf("\n");
  }
  else
    printf("Please state an interger number.\n");
  return 0;
}