#include <stdio.h>
#include <stdlib.h>
#include <inttypes.h>
#define COLUMNS 6
/* R�knare for antal kolumner */
int counter = 0;	

/* Metod som skriver ut talet n, i en tabell med COLUMNS kolumner */
void print_number(int n){				
	
	//H�ller reda p� antalet kolumner
	if( counter == COLUMNS)
	{
		printf("\n");
		counter = 1;
	}
	else
		counter++;
	
	/* Skriver ut talet */
	printf("%10d", n);
	
}

/*
Metod som skriver ut primtal enligt 
algoritmen Eratosthenes s�ll(Sieve of Eratoshenes)
*/
void print_sieves(int n){
      
	//uint8_t arrPrime[n];
    int* arrPrime = malloc((n+1) * sizeof(int));

	//printf("Storleken utav ett falt: %d\n", sizeof(arrPrime[0]));
	//printf("Storleken utav arrayen: %d\n", sizeof(arrPrime));
   
	   /* 
   	Fyller arrayen med n - 1 tal
   	Ex: arrPrime[25] = 25 osv
   */
   for(int i = 2; i <= n; i++)
   	   arrPrime[i] = 1;
 
   /*
   G�r igenom och markerar icke primtal med 0,
   enligt Sieve of Eratosthenes
   */
   for(int i = 2; (i*i <= n); i++)
   {
   	   if(arrPrime[i] > 0)
   	   {
   	   	   for(int j = i*i; j <= n; j +=i)
   	   	   	   arrPrime[j] = 0;
   	    }
   }
   
   /* G�r igenom arrayen och skriver ut primtalen */
   for(int i = 2; i <= n; i++)
   {
   	   if(arrPrime[i] > 0)
   	   	   print_number(i);   
   }
   
   //Frig�r minne	   
   free(arrPrime);	   
}

/* ******Suprise***** 
Tar fram alla primtal fram till talet n
R�knar ut medelv�rdet
*/
double mean_sieves(int n)
{
	char* arrPrime = malloc((n+1) * sizeof(char));
	double sum = 0;
	int numOfPrime = 0;
	
	/* 
	Fyller arrayen med n - 1 tal
   	Ex: arrPrime[25] = 25 osv
   */
   for(int i = 2; i <= n; i++)
   	   arrPrime[i] = 1;
    
	/*
   G�r igenom och markerar icke primtal med 0,
   enligt Sieve of Eratosthenes
   */
   for(int i = 2; (i*i <= n); i++)
   {
   	   if(arrPrime[i] > 0)
   	   {
   	   	   for(int j = i*i; j <= n; j +=i)
   	   	   	   arrPrime[j] = 0;
   	    }
   }
	
	/*
	H�mtar alla primtal
	Spar v�rdet av primtalet i sum
	�kar numOfPrime med 1, f�r varje primtal som hittas
	*/
	for(int i = 2; i <= n; i++)
	{
		if(arrPrime[i] > 0)
		{	
			sum = sum + i;
			numOfPrime++;
		}
	}
		
	//Tar fram medelv�rde
	//kollar f�r div med 0	
	if(numOfPrime > 0)
		sum = sum / numOfPrime;
	
	

	//"Sl�pper minnet"
	free(arrPrime);
	return sum;
}

int main(int argc, char *argv[]){
  if(argc == 2)
  {
    print_sieves(atoi(argv[1]));
    printf("\n");
    printf("mean value: %.2f\n", mean_sieves(atoi(argv[1])));
  }
    else
    printf("Please state an interger number.\n");
  return 0;
}