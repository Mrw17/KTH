#include <stdio.h>

char* text1 = "This is a string.";	//17
char* text2 = "Yet another thing."; //18
int list1[20];
int list2[20];
int count = 0;

void copycodes(char* str, int* list, int* counter) {	
	/* K�rs fram till zero byte som kommer efter varje str�ng(text1/2) */
	while (*str != '\0') {
		/*
		
		anropning: copycodes(text1, list1, &count);
		
		list (list1 eller list2) kommer peka p� samma som str 
		list1 -> text1
		list2 -> text2
		*/
		*list = *str;
		
		/* 
		�kar med 1, f�r att f� n�sta char 
		char = 1 byte
		kommer peka p� n�sta bokstav
		str -> text1/text2
		*/
		*str++;
		
		/* 
		Tar n�sta plats i listan 
		int = 4 byte	
		int list, ++ = 4 byte
		pekar p� n�sta int i listan
		*/
		*list++;
		
		/*
		R�knar antalet ord 
		*counter++;  pekaren flyttar till n�sta position, men retunerar the gamla  v�rdet. Kommer alltid bli 0 d�   
		++*counter;  �kar v�rdet p� *counter
		*/
		++*counter;
	}
}

/* Anropas f�rst */
void work(){
	copycodes(text1, list1, &count);
	copycodes(text2, list2, &count);
}

/* Anropas efter copycodes*/

void printlist(const int* lst){
  printf("ASCII codes and corresponding characters.\n");
  while(*lst != 0){
    printf("0x%03X '%c' ", *lst, (char)*lst);
    lst++;
  }
  printf("\n");
}

void endian_proof(const char* c){
  printf("\nEndian experiment: 0x%02x,0x%02x,0x%02x,0x%02x\n", 
         (int)*c,(int)*(c+1), (int)*(c+2), (int)*(c+3));
  
}

int main(void){
  work();

  printf("\nlist1: ");
  printlist(list1);
  printf("\nlist2: ");
  printlist(list2);
  printf("\nCount = %d\n", count);

  endian_proof((char*) &count);
}
